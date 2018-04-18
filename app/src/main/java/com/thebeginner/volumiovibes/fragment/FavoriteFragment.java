package com.thebeginner.volumiovibes.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.thebeginner.volumiovibes.R;
import com.thebeginner.volumiovibes.adapter.MusicAdapter;
import com.thebeginner.volumiovibes.items.MusicItem;
import com.thebeginner.volumiovibes.utils.Utils;

import java.util.ArrayList;
import java.util.List;


public class FavoriteFragment extends Fragment implements MusicAdapter.ListItemClickListener {

    private FirebaseUser user;
    private MusicAdapter mAdapter;
    private RecyclerView mMusicList;
    private List<MusicItem> music_list;
    private DatabaseReference mDatabase;
    private Context mContext;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        mContext = view.getContext();

        /* Get current User */
        user = FirebaseAuth.getInstance().getCurrentUser();

        /* Database Reference */
        mDatabase = Utils.getmDatabase().getReference();

        music_list = new ArrayList<>();

        /* Get Adapter */
        mAdapter = new MusicAdapter(music_list, this);

        mMusicList = (RecyclerView) view.findViewById(R.id.rv);


        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                switch (dataSnapshot.getKey()) {
                    case "favorite":
                        // Get events using Uid
                        populateRecyclerView(dataSnapshot.child(user.getUid()));
                        break;
                }
                Log.d("SnapshotAdd", dataSnapshot.toString());
                if(s != null)
                    Log.d("StringAdd", s);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("SnapshotChanged", dataSnapshot.toString());
                if(s != null)
                    Log.d("StringChanged", s);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("SnapshotRemoved", dataSnapshot.toString());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d("SnapshotMoved", dataSnapshot.toString());
                if(s != null)
                    Log.d("StringMoved", s);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.addChildEventListener(childEventListener);

        /* Layout Manager */
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        mMusicList.setLayoutManager(layoutManager);

        /* Rv Adapter */
        mMusicList.setHasFixedSize(false);
        mMusicList.setAdapter(mAdapter);

        return view;
    }

    private void populateRecyclerView(DataSnapshot dataSnapshot) {
        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
            MusicItem item = snapshot.getValue(MusicItem.class);
            mAdapter.addItem(item);
        }
    }

    @Override
    public void onListItemClickListener(int clickedItemIndex, View view) {

    }
}
