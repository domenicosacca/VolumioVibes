package com.thebeginner.volumiovibes.fragment;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thebeginner.volumiovibes.R;
import com.thebeginner.volumiovibes.adapter.MusicAdapter;
import com.thebeginner.volumiovibes.items.MusicItem;
import com.thebeginner.volumiovibes.socketio.Manager;
import com.thebeginner.volumiovibes.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment
        implements MusicAdapter.ListItemClickListener{

    private FirebaseUser user;
    private MusicAdapter mAdapter;
    private RecyclerView mMusicList;
    private List<MusicItem> music_list;
    private DatabaseReference mDatabase;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        mContext = view.getContext();

        Manager manager = new Manager(getActivity(), getArguments().getString("DEVICE_IP"));
        Socket socket = manager.getSocket();
        socket.on("pushQueue", showQueue);
        socket.connect();

        /* Get current User */
        user = FirebaseAuth.getInstance().getCurrentUser();

        /* Database Reference */
        mDatabase = Utils.getmDatabase().getReference();

        music_list = new ArrayList<>();

        /* Get Adapter */
        mAdapter = new MusicAdapter(music_list, this);

        mMusicList = (RecyclerView) view.findViewById(R.id.rv);
        /* Layout Manager */
        LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        mMusicList.setLayoutManager(layoutManager);

        /* Rv Adapter */
        mMusicList.setHasFixedSize(false);
        mMusicList.setAdapter(mAdapter);

        /* Popolo Rv */
        fillRv(socket);

        return view;
    }

    @Override
    public void onListItemClickListener(int clickedItemIndex, View view) {
        ImageButton favorite_button = (ImageButton) view.findViewById(R.id.button_favourite);
        favorite_button.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
        addFavoriteToUser(mAdapter.getItem(clickedItemIndex));
    }


    private void populateRecyclerView(DataSnapshot dataSnapshot) {
        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
            MusicItem item = snapshot.getValue(MusicItem.class);
            mAdapter.addItem(item);
        }
    }

    private void fillRv(Socket socket) {
        Socket socketq = null;
        try {
            socketq = IO.socket("http://" + "10.17.2.199");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        socketq.on("pushQueue", showQueue);
        socketq.connect();
        socketq.emit("getQueue", new JSONObject());
        socketq.emit("getQueue", new JSONObject());
    }

    private Emitter.Listener showQueue = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ((Activity)mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONArray array = (JSONArray) args[0];
                    if(array != null) {
                        loadList(array);
                        mAdapter = new MusicAdapter(music_list, HomeFragment.this);
                        mMusicList.setAdapter(mAdapter);

                        for (int i = 0; i < array.length(); i++) {
                            try {
                                Log.d("Array" + i, array.getJSONObject(i).toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Log.d("ArrayNull", "ARRAY NULL");
                    }
                }
            });
        }
    };

    private void loadList(JSONArray jsonArray) {
        mAdapter.clearList();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                String track_name = jsonArray.getJSONObject(i).getString("name");
                String track_artist, track_image, track_source, track_uri;
                try {
                    track_artist = jsonArray.getJSONObject(i).getString("artist");
                } catch (JSONException e){
                    track_artist = "";
                }
                try {
                    track_source = jsonArray.getJSONObject(i).getString("trackType");
                } catch (JSONException e) {
                    track_source = "";
                }
                try {
                    track_image = jsonArray.getJSONObject(i).getString("albumart");
                } catch (JSONException e) {
                    track_image = "";
                }
                try {
                    track_uri = jsonArray.getJSONObject(i).getString("uri");
                } catch (JSONException e) {
                    track_uri = "";
                }
                MusicItem item = new MusicItem(String.valueOf(i), track_name,track_artist, track_source, track_image, track_uri);
                mAdapter.addItem(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void addFavoriteToUser(MusicItem item) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("favorite").child(user.getUid()).child("m" + item.get_id() + Calendar.getInstance().getTime());
            mDatabase.setValue(item);
        }
    }

}
