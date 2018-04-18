package com.thebeginner.volumiovibes.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.thebeginner.volumiovibes.R;
import com.thebeginner.volumiovibes.socketio.Manager;

import org.json.JSONException;
import org.json.JSONObject;

public class AddYoutubeSongFragment extends Fragment
        implements View.OnClickListener{
    private EditText yt_url;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_youtube, container, false);
        yt_url = (EditText) view.findViewById(R.id.editText_yt_url);
        view.findViewById(R.id.button_yt_song_add).setOnClickListener(this);

        if(getArguments().getString("YTLINK") !=  null) {
            yt_url.setText(getArguments().getString("YTLINK"));
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.button_yt_song_add:
                if(isValidUrl(yt_url.getText())){
                    String ip = getArguments().getString("DEVICE_IP");
                    Manager manager = new Manager(getActivity(), getArguments().getString("DEVICE_IP"));
                    manager.connectToDevice();
                    JSONObject json=new JSONObject();
                    try {
                        json.put("endpoint", "music_service/youtube");
                        json.put("method", "add");
                        json.put("data",yt_url.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    manager.attemptSend("callMethod", json);
                }
                break;
        }
    }

    private boolean isValidUrl(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.WEB_URL.matcher(target).matches());
    }

}
