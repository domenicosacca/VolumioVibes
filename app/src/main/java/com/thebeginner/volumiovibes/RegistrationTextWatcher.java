package com.thebeginner.volumiovibes;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

/**
 * Created by domen on 27/02/2018.
 */

public class RegistrationTextWatcher implements TextWatcher {
    private View view;

    public RegistrationTextWatcher(View view) {
        this.view = view;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        ((TextInputLayout) view).setError(null);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }
}
