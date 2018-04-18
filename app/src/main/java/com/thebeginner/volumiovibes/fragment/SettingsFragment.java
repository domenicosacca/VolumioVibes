package com.thebeginner.volumiovibes.fragment;


import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.thebeginner.volumiovibes.R;
import com.thebeginner.volumiovibes.activity.LoginActivity;

public class SettingsFragment extends Fragment
        implements View.OnClickListener{

    private Button button_logout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_settings, container, false);
        button_logout = view.findViewById(R.id.button_logout);
        button_logout.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.button_logout:
                FirebaseAuth.getInstance().signOut();
                Intent login_activity = new Intent(v.getContext(), LoginActivity.class);
                startActivity(login_activity);
                ((Activity) v.getContext()).finish();
                break;
        }
    }
}
