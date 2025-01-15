package com.example.nutriguide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class userProfileActivity extends AppCompatActivity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_user_profile);
    }

    public void signUp(View view) {
        startActivity(new Intent(this, signUpActivity.class));
        overridePendingTransition(17432576, 17432577);
    }

    public void signIn(View view) {
        startActivity(new Intent(this, signInActivity.class));
        overridePendingTransition(17432576, 17432577);
    }

    public void info(View view) {
        startActivity(new Intent(this, infoActivity.class));
    }
}
