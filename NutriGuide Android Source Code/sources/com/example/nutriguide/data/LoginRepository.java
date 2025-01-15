package com.example.nutriguide.data;

import com.example.nutriguide.data.Result;
import com.example.nutriguide.data.model.LoggedInUser;

public class LoginRepository {
    private static volatile LoginRepository instance;
    private LoginDataSource dataSource;
    private LoggedInUser user = null;

    private LoginRepository(LoginDataSource dataSource2) {
        this.dataSource = dataSource2;
    }

    public static LoginRepository getInstance(LoginDataSource dataSource2) {
        if (instance == null) {
            instance = new LoginRepository(dataSource2);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return this.user != null;
    }

    public void logout() {
        this.user = null;
        this.dataSource.logout();
    }

    private void setLoggedInUser(LoggedInUser user2) {
        this.user = user2;
    }

    public Result<LoggedInUser> login(String username, String password) {
        Result<LoggedInUser> result = this.dataSource.login(username, password);
        if (result instanceof Result.Success) {
            setLoggedInUser((LoggedInUser) ((Result.Success) result).getData());
        }
        return result;
    }
}
