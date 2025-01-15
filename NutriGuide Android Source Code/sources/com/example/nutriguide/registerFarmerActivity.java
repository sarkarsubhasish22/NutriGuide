package com.example.nutriguide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class registerFarmerActivity extends AppCompatActivity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_register_farmer);
    }

    public void newFarmer(View view) {
        long userId = getIntent().getLongExtra("userId", 0);
        System.out.println(userId);
        Intent intent = new Intent(this, newFarmerActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        overridePendingTransition(17432576, 17432577);
    }

    public void existingFarmer(View view) {
        long userId = getIntent().getLongExtra("userId", 0);
        System.out.println(userId);
        Intent intent = new Intent(this, existingFarmerActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        overridePendingTransition(17432576, 17432577);
    }
}
