package com.example.nutriguide.ui.login;

import android.util.Patterns;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.nutriguide.R;
import com.example.nutriguide.data.LoginRepository;
import com.example.nutriguide.data.Result;
import com.example.nutriguide.data.model.LoggedInUser;

public class LoginViewModel extends ViewModel {
    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private LoginRepository loginRepository;
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();

    LoginViewModel(LoginRepository loginRepository2) {
        this.loginRepository = loginRepository2;
    }

    /* access modifiers changed from: package-private */
    public LiveData<LoginFormState> getLoginFormState() {
        return this.loginFormState;
    }

    /* access modifiers changed from: package-private */
    public LiveData<LoginResult> getLoginResult() {
        return this.loginResult;
    }

    public void login(String username, String password) {
        Result<LoggedInUser> result = this.loginRepository.login(username, password);
        if (result instanceof Result.Success) {
            this.loginResult.setValue(new LoginResult(new LoggedInUserView(((LoggedInUser) ((Result.Success) result).getData()).getDisplayName())));
        } else {
            this.loginResult.setValue(new LoginResult(Integer.valueOf(R.string.login_failed)));
        }
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            this.loginFormState.setValue(new LoginFormState(Integer.valueOf(R.string.invalid_username), (Integer) null));
        } else if (!isPasswordValid(password)) {
            this.loginFormState.setValue(new LoginFormState((Integer) null, Integer.valueOf(R.string.invalid_password)));
        } else {
            this.loginFormState.setValue(new LoginFormState(true));
        }
    }

    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        }
        return !username.trim().isEmpty();
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}
