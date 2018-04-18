package com.thebeginner.volumiovibes.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.thebeginner.volumiovibes.R;
import com.thebeginner.volumiovibes.RegistrationTextWatcher;
import com.thebeginner.volumiovibes.object.User;
import com.thebeginner.volumiovibes.utils.Utils;

public class RegistrationActivity extends Activity
        implements View.OnClickListener {

    private boolean isValid;
    private Utils utils;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private RelativeLayout progressBarLayout;
    private EditText editText_name, editText_surname, editText_email, editText_pass, editText_conf_pass;
    private TextInputLayout textInput_name, textInput_surname, textInput_email, textInput_pass, textInput_conf_pass;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        /* Get instance of FirebaseAuth */
        mAuth = FirebaseAuth.getInstance();

        /* Use app language for sending email with correct language */
        mAuth.useAppLanguage();

        /* Get instance of FirebaseDatabse */
        mDatabase = Utils.getmDatabase().getReference();

        /* Utils */
        utils = new Utils(this);

        /* ProgressBarLayout */
        progressBarLayout = (RelativeLayout) findViewById(R.id.progressbar_layout);


        /* LinearLayout registration */
        LinearLayout linearLayoutRegistration = (LinearLayout) findViewById(R.id.registration_content);

        /* LinearLayout registration click listener */
        linearLayoutRegistration.setOnClickListener(this);

        /* TextInputLayout */
        textInput_name = (TextInputLayout)findViewById(R.id.textInput_name);
        textInput_surname = (TextInputLayout)findViewById(R.id.textInput_surname);
        textInput_email = (TextInputLayout)findViewById(R.id.textInput_email);
        textInput_pass = (TextInputLayout)findViewById(R.id.textInput_password);
        textInput_conf_pass = (TextInputLayout)findViewById(R.id.textInput_password_conf);

        /* TextInputLayout error */
        textInput_name.setError(null);
        textInput_surname.setError(null);
        textInput_email.setError(null);
        textInput_pass.setError(null);
        textInput_conf_pass.setError(null);

        /* EditText */
        editText_name = (EditText)findViewById(R.id.editText_name);
        editText_surname = (EditText)findViewById(R.id.editText_surname);
        editText_email = (EditText)findViewById(R.id.editText_email);
        editText_pass = (EditText)findViewById(R.id.editText_password);
        editText_conf_pass = (EditText)findViewById(R.id.editText_password_confirm);

        /* EditText ChangeTextListener */
        editText_name.addTextChangedListener(new RegistrationTextWatcher(textInput_name));
        editText_surname.addTextChangedListener(new RegistrationTextWatcher(textInput_surname));
        editText_email.addTextChangedListener(new RegistrationTextWatcher(textInput_email));
        editText_pass.addTextChangedListener(new RegistrationTextWatcher(textInput_pass));
        editText_conf_pass.addTextChangedListener(new RegistrationTextWatcher(textInput_conf_pass));

        /* Button */
        Button buttonSignIn = (Button) findViewById(R.id.button_sign_in);

        /* Button click listener */
        buttonSignIn.setOnClickListener(this);

        /* TextView */
        TextView textHaveAccount = (TextView) findViewById(R.id.text_already_have_account);

        /* TextView click listener */
        textHaveAccount.setOnClickListener(this);

        /* isValid */
        isValid = false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.registration_content:
                utils.hideKeyboard();
                break;
            case R.id.button_sign_in:
                // Hide keyboard
                utils.hideKeyboard();

                if(!isValidNameAndSurname(editText_name.getText())) {
                    textInput_name.setError("Errore. Può contenere solo lettere!");
                    isValid = false;
                } else isValid = true;
                if(!isValidNameAndSurname(editText_surname.getText())) {
                    isValid = false;
                    textInput_surname.setError("Errore. Può contenere solo lettere!");
                }
                if(!isValidEmail(editText_email.getText())) {
                    isValid = false;
                    textInput_email.setError("Email non valida!");
                }
                if(!isValidPassword(editText_pass.getText())) {
                    isValid = false;
                    textInput_pass.setError("Password non corretta! Deve essere composta da almeno 8 caratteri.");
                }
                if(!isPasswordEq(editText_pass.getText(), editText_conf_pass.getText())) {
                    isValid = false;
                    textInput_conf_pass.setError("La password inserita non coincide con quella precedente!");
                }
                if(isValid) {
                    createUser();
                }
                break;
            case R.id.text_already_have_account:
                // Close RegistrationActivity
                this.finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        // Close RegistrationActivity
        this.finish();
    }

    /* VALIDATION METHODS */

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private boolean isValidNameAndSurname(CharSequence target) {
        return (!TextUtils.isEmpty(target) && !target.toString().matches(".*[\\d&._-]+.*"));
    }

    private boolean isValidPassword(CharSequence target) {
        return (!TextUtils.isEmpty(target) && target.length() > 7);
    }

    private boolean isPasswordEq(CharSequence target, CharSequence target2) {
        return (target.toString().compareTo(target2.toString()) == 0);
    }
    /* ****************** */

    private void createUser() {
        final String email = editText_email.getText().toString(),
                pass = editText_pass.getText().toString();

        // Show ProgressBar
        progressBarLayout.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Hide progressBar
                        progressBarLayout.setVisibility(View.GONE);
                        if(task.isSuccessful()) {
                            // Sign in success
                            Log.d("success", "createUserAWithEmail:true");

                            // Get current user (not-null)
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Update user profile
                            String name = editText_name.getText().toString(),
                                    surname = editText_surname.getText().toString();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name + " " + surname)
                                    .build();
                            updateCurrentUser(user, profileUpdates);
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBarLayout.setVisibility(View.GONE);
                        e.getMessage();
                        if(e instanceof FirebaseNetworkException) {
                            textInput_email.setError(null);
                            textInput_pass.setError(null);
                            showRegistrationFailedDialog();
                        } else if(e instanceof FirebaseAuthUserCollisionException) {
                            textInput_email.setError(null);
                            textInput_pass.setError(null);
                            showUserAlreadyExistDialog();
                        }
                    }
                });
    }

    private void updateCurrentUser(final FirebaseUser user, UserProfileChangeRequest profileUpdates) {
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            // Add user to database
                            writeNewUser(user.getUid(), user.getDisplayName(), user.getEmail(), "");

                            Log.d("success", "User profile updated");
                        }
                    }
                });
    }

    private void writeNewUser(String userId, String displayedName, String email, String imgUrl) {
        User user = new User(displayedName, email, imgUrl);
        // Add current user to node 'users'
        mDatabase.child("users").child(userId).setValue(user);
    }

    public void showRegistrationFailedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_title_registration_failed));
        builder.setMessage(getString(R.string.dialog_message_registration_failed));
        // Set positive button text and handle click
        String positiveText = getString(R.string.dialog_button_retry);
        builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createUser();
            }
        });
        // Set negative button text and handle click
        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // Show dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showUserAlreadyExistDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_title_registration_failed));
        builder.setMessage(getString(R.string.dialog_message_user_already_exist));
        // Set positive button text and handle click
        String positiveText = getString(R.string.button_login);
        builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RegistrationActivity.this.finish();
            }
        });
        // Set negative button text and handle click
        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // Show dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
