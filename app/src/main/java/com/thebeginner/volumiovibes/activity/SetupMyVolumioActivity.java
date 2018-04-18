package com.thebeginner.volumiovibes.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.thebeginner.volumiovibes.R;
import com.thebeginner.volumiovibes.helper.MDNSScanner;
import com.thebeginner.volumiovibes.helper.NsdHelper;

public class SetupMyVolumioActivity extends Activity
        implements View.OnClickListener{

    private static final String DEVICE_NAME = "volumio-3";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_setup);

        Button button_setup_connect = (Button) findViewById(R.id.button_setup_connect);
        button_setup_connect.setOnClickListener(this);

        /*NsdHelper helper = new NsdHelper(this);
        helper.initializeNsd();
        helper.discoverServices();*/
        /*final MDNSScanner scanner = new MDNSScanner(this);
        scanner.setDeviceHandler(new MDNSScanner.DeviceHandler() {
            @Override
            public void handle(MDNSScanner.Device device) {
                if(device.name.compareTo(DEVICE_NAME) == 0){
                    Intent appLinkIntent = getIntent();
                    String value1 = appLinkIntent.getStringExtra(Intent.EXTRA_TEXT);
                    Intent main_activity = new Intent(SetupMyVolumioActivity.this, MainActivity.class);
                    if(value1 != null) {
                        main_activity.putExtra("YTLINK", value1);
                    }
                    main_activity.putExtra("DEVICE_IP", device.host);
                    startActivity(main_activity);
                    scanner.stop();
                    SetupMyVolumioActivity.this.finish();
                }
                //Log.d("test", "Found device " + device.name + " at " + device.host);
            }
        });
        scanner.start();*/
    }

    @Override
    public void onClick(View v) {
        int resId = v.getId();

        switch (resId) {
            case R.id.button_setup_connect:
                EditText editText_ip = (EditText) findViewById(R.id.editText_ip);
                if (isIpValid(editText_ip.getText())) {
                        Intent appLinkIntent = getIntent();
                        String value1 = appLinkIntent.getStringExtra(Intent.EXTRA_TEXT);
                        Intent main_activity = new Intent(SetupMyVolumioActivity.this, MainActivity.class);
                        if(value1 != null) {
                            main_activity.putExtra("YTLINK", value1);
                        }
                        main_activity.putExtra("DEVICE_IP", editText_ip.getText().toString());
                        startActivity(main_activity);
                        SetupMyVolumioActivity.this.finish();
                } else {
                    TextInputLayout textInput_ip = (TextInputLayout) findViewById(R.id.textInput_ip);
                    textInput_ip.setError(getString(R.string.wrong_ip));
                }
                break;
        }
    }

    private boolean isIpValid(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.IP_ADDRESS.matcher(target).matches());
    }
}
