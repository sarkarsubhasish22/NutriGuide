package com.example.nutriguide.ui.login;

class LoginFormState {
    private boolean isDataValid;
    private Integer passwordError;
    private Integer usernameError;

    LoginFormState(Integer usernameError2, Integer passwordError2) {
        this.usernameError = usernameError2;
        this.passwordError = passwordError2;
        this.isDataValid = false;
    }

    LoginFormState(boolean isDataValid2) {
        this.usernameError = null;
        this.passwordError = null;
        this.isDataValid = isDataValid2;
    }

    /* access modifiers changed from: package-private */
    public Integer getUsernameError() {
        return this.usernameError;
    }

    /* access modifiers changed from: package-private */
    public Integer getPasswordError() {
        return this.passwordError;
    }

    /* access modifiers changed from: package-private */
    public boolean isDataValid() {
        return this.isDataValid;
    }
}
