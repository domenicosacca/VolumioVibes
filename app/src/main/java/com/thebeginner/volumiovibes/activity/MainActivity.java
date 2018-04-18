package com.thebeginner.volumiovibes.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.thebeginner.volumiovibes.R;
import com.thebeginner.volumiovibes.fragment.AddYoutubeSongFragment;
import com.thebeginner.volumiovibes.fragment.FavoriteFragment;
import com.thebeginner.volumiovibes.fragment.HomeFragment;
import com.thebeginner.volumiovibes.fragment.SettingsFragment;
import com.thebeginner.volumiovibes.utils.Utils;

public class MainActivity extends AppCompatActivity {

    private String ip;
    private FirebaseAuth mAuth;
    private FragmentManager manager;
    private Bundle standard_bundle;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_play:
                    AddYoutubeSongFragment fragment_yt = new AddYoutubeSongFragment();
                    fragment_yt.setArguments(standard_bundle);
                    manager.beginTransaction().replace(R.id.frame_content, fragment_yt).commit();
                    return true;
                case R.id.navigation_playlist:
                    HomeFragment fragment_home = new HomeFragment();
                    fragment_home.setArguments(standard_bundle);
                    manager.beginTransaction().replace(R.id.frame_content, fragment_home).commit();
                    return true;
                case R.id.navigation_favorite:
                    FavoriteFragment fragment_favorite = new FavoriteFragment();
                    manager.beginTransaction().replace(R.id.frame_content, fragment_favorite).commit();
                    return true;
                case R.id.navigation_settings:
                    SettingsFragment fragment_settings = new SettingsFragment();
                    manager.beginTransaction().replace(R.id.frame_content, fragment_settings).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent curr_intent = getIntent();
        ip = curr_intent.getStringExtra("DEVICE_IP");

        /* Add ip in bundle */
        standard_bundle = new Bundle();
        standard_bundle.putString("DEVICE_IP", ip);

        /* Initialize FirebaseAuth */
        mAuth = FirebaseAuth.getInstance();

        /* Keep users location synced */
        if (mAuth.getCurrentUser() != null) {
            DatabaseReference usersRef = Utils.getmDatabase().getReference("users").child(mAuth.getCurrentUser().getUid());
            usersRef.keepSynced(true);
        }

        Bundle b = new Bundle();
        Intent appLinkIntent = getIntent();
        String value1 = appLinkIntent.getStringExtra("YTLINK");
        if (value1 != null){
            b.putString("YTLINK", value1);
            b.putString("DEVICE_IP", appLinkIntent.getStringExtra("DEVICE_IP"));
        }
        AddYoutubeSongFragment f = new AddYoutubeSongFragment();
        f.setArguments(b);
        /* Fragment Manager */
        manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.frame_content, f).commit();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is signed in with Email
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null) {  // User is not logged
            // Open Login Activity
            Intent activityLogin = new Intent(this, LoginActivity.class);
            Intent i = getIntent();
            activityLogin.putExtra("DEVICE_IP",i.getStringExtra("DEVICE_IP"));
            startActivity(activityLogin);
            this.finish();
        }
    }


}
