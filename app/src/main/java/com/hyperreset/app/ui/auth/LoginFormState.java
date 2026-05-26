package com.hyperreset.app.ui.auth;

import androidx.annotation.Nullable;

/**
 * Data class representing the validation state of the login form.
 */
public class LoginFormState {

    @Nullable
    private final Integer emailError;

    @Nullable
    private final Integer passwordError;

    private final boolean isDataValid;

    public LoginFormState(@Nullable Integer emailError, @Nullable Integer passwordError) {
        this.emailError = emailError;
        this.passwordError = passwordError;
        this.isDataValid = emailError == null && passwordError == null;
    }

    public LoginFormState(boolean isDataValid) {
        this.emailError = null;
        this.passwordError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getEmailError() {
        return emailError;
    }

    @Nullable
    public Integer getPasswordError() {
        return passwordError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}
