package com.thebeginner.volumiovibes.socketio;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.thebeginner.volumiovibes.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class Manager {

    private Activity mActivity;
    private String mIp;
    private Socket mSocket;

    private boolean REQUEST_QUEUE;

    public Manager(Activity activity, String ip) {
        this.mActivity = activity;
        this.mIp = ip;
        initDevice();
    }

    private void initDevice() {
        try {
            mSocket = IO.socket("http://" + mIp);
        } catch (URISyntaxException e) {
            Log.e("URISyntaxException", e.getMessage());
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
    public Socket connectToDevice() {
        mSocket.on("pushState", testListener);
        mSocket.on("pushMethod", addMusicYt);
        mSocket.on("pushQueue", showQueue);
        mSocket.connect();
        return mSocket;
    }

    public void attemptSend(String event_name, Object... args) {
        mSocket.emit(event_name, args);
    }


    private Emitter.Listener testListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    Log.d("JSONData", data.toString());
                    /*String username;
                    String message;
                    try {
                        username = data.getString("username");
                        message = data.getString("message");
                    } catch (JSONException e) {
                        Log.d("JSONException - Manager", e.getMessage());
                        return;
                    }*/

                    // add the message to view
                    //addMessage(username, message);
                }
            });
        }
    };

    private Emitter.Listener addMusicYt = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    Log.d("JSONDataNew", data.toString());
                }
            });
        }
    };

    private Emitter.Listener showQueue = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONArray array = (JSONArray) args[0];
                    if(REQUEST_QUEUE) {
                        RecyclerView rv = mActivity.findViewById(R.id.rv);;
                        for (int i = 0; i < array.length(); i++) {
                            try {
                                Log.d("Array" + i, array.getJSONObject(i).toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            });
        }
    };

}
