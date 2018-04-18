package com.thebeginner.volumiovibes.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.inputmethod.InputMethodManager;

import com.google.firebase.database.FirebaseDatabase;
import com.thebeginner.volumiovibes.R;

public class Utils {
    private Context context;
    private static FirebaseDatabase mDatabase;

    public Utils(Context context) {
        this.context = context;
    }

    public static FirebaseDatabase getmDatabase() {
        if(mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            /* Enable Firebase disk persistence*/
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }

    public void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.dialog_title_no_internet));
        builder.setMessage(context.getResources().getString(R.string.dialog_message_no_internet));
        // Set positive button text and handle onclick
        String positiveText = context.getResources().getString(android.R.string.ok);
        builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // Show dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void hideKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(
                    ((Activity)context).getCurrentFocus().getWindowToken(), 0);
        }
    }
}
