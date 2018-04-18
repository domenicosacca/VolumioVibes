package com.thebeginner.volumiovibes.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.thebeginner.volumiovibes.R;
import com.thebeginner.volumiovibes.RegistrationTextWatcher;
import com.thebeginner.volumiovibes.object.User;
import com.thebeginner.volumiovibes.utils.Utils;

public class LoginActivity extends Activity
        implements View.OnClickListener{

    private static final int RC_SIGN_IN_EMAIL = 64803;

    private Utils utils;
    private boolean isValid;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private RelativeLayout progressBarLayout;
    private EditText editText_email, editText_pass;
    private TextInputLayout textInput_email, textInput_pass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /* Initialize FirebaseAuth */
        mAuth = FirebaseAuth.getInstance();

        /* Get instance of FirebaseDatabse */
        mDatabase = Utils.getmDatabase().getReference();

        /* Utils */
        utils = new Utils(this);

        /* ProgressBarLayout */
        progressBarLayout = (RelativeLayout) findViewById(R.id.progressbar_layout);

        /* LinearLayout login */
        LinearLayout linearLayoutLogin = (LinearLayout) findViewById(R.id.login_content);

        /* LinearLayout login click listener */
        linearLayoutLogin.setOnClickListener(this);

        /* TextInputLayout */
        textInput_email = (TextInputLayout) findViewById(R.id.textInput_email);
        textInput_pass = (TextInputLayout) findViewById(R.id.textInput_password);

        /* TextInputLayout error */
        textInput_email.setError(null);
        textInput_pass.setError(null);

        /* EditText */
        editText_email = (EditText) findViewById(R.id.editText_email);
        editText_pass = (EditText) findViewById(R.id.editText_password);

        /* EditText ChangeTextListener */
        editText_email.addTextChangedListener(new RegistrationTextWatcher(textInput_email));
        editText_pass.addTextChangedListener(new RegistrationTextWatcher(textInput_pass));

        /* Buttons */
        Button buttonLogin = (Button) findViewById(R.id.button_login);
        Button buttonSignIn = (Button) findViewById(R.id.button_sign_in);

        /* Buttons click listener */
        buttonLogin.setOnClickListener(this);
        buttonSignIn.setOnClickListener(this);

        /* isValid */
        isValid = false;

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.login_content:
                utils.hideKeyboard();
                break;
            case R.id.button_login:
                // Hide keyboard
                utils.hideKeyboard();

                if(!isValidEmail(editText_email.getText())) {
                    isValid = false;
                    textInput_email.setError("Errore. Email inserita non valida!");
                } else isValid = true;
                if(!isValidPassword(editText_pass.getText())) {
                    isValid = false;
                    textInput_pass.setError("Errore. Password inserita non valida!");
                }
                // If EditText are valid, sign in user
                if(isValid) signInUser();
                break;
            case R.id.button_sign_in:
                Intent activity_registration = new Intent(this, RegistrationActivity.class);
                startActivityForResult(activity_registration, RC_SIGN_IN_EMAIL);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // Result returned from Google SignIn intent
        switch (requestCode) {
            case RC_SIGN_IN_EMAIL:
                if(resultCode == Activity.RESULT_OK) {
                    editText_email.setText(data.getStringExtra("usr_email"));
                    Snackbar.make(findViewById(R.id.coordinator_layout), "E' stata inviata una mail di verifica all'indirizzo da te fornito.", Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }


    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private boolean isValidPassword(CharSequence target) {
        return (!TextUtils.isEmpty(target));
    }


    private void signInUser() {
        String email = editText_email.getText().toString(),
                pass = editText_pass.getText().toString();

        // Show ProgressBar
        progressBarLayout.setVisibility(View.VISIBLE);

        // Sign in user
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBarLayout.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Sign in success
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            writeNewUser(user.getUid(), user.getDisplayName(), user.getEmail(), "");
                            signInSuccess();
                        } else {
                            textInput_email.setError("Errore. Email inserita non valida!");
                            textInput_pass.setError("Errore. Password inserita non valida!");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBarLayout.setVisibility(View.GONE);
                        if(e instanceof FirebaseNetworkException) {
                            textInput_email.setError(null);
                            textInput_pass.setError(null);
                            utils.showNoInternetDialog();
                        }
                    }
                });

    }

    private void signInSuccess() {
        Intent activity_main = new Intent(LoginActivity.this, MainActivity.class);
        Intent i = getIntent();
        activity_main.putExtra("DEVICE_IP", i.getStringExtra("DEVICE_IP"));
        startActivity(activity_main);
        // Close LoginActivity
        this.finish();
    }

    private void writeNewUser(String userId, String displayedName, String email, String imgUrl) {
        User user = new User(displayedName, email, imgUrl);
        // Add current user to node 'users'
        mDatabase.child("users").child(userId).setValue(user);
    }
}
