package com.example.nutriguide.ui.login;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.nutriguide.data.LoginDataSource;
import com.example.nutriguide.data.LoginRepository;

public class LoginViewModelFactory implements ViewModelProvider.Factory {
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return new LoginViewModel(LoginRepository.getInstance(new LoginDataSource()));
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
