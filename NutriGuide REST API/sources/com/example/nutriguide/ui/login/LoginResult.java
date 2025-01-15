package com.example.nutriguide.ui.login;

class LoginResult {
    private Integer error;
    private LoggedInUserView success;

    LoginResult(Integer error2) {
        this.error = error2;
    }

    LoginResult(LoggedInUserView success2) {
        this.success = success2;
    }

    /* access modifiers changed from: package-private */
    public LoggedInUserView getSuccess() {
        return this.success;
    }

    /* access modifiers changed from: package-private */
    public Integer getError() {
        return this.error;
    }
}
