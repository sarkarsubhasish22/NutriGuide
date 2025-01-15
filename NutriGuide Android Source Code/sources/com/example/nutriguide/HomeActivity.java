package com.example.nutriguide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class HomeActivity extends AppCompatActivity {
    int backpress;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_home);
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"}, 0);
    }

    public void evaluateNutritionalStatus(View view) {
        startActivity(new Intent(this, userProfileActivity.class));

    }

    public void nutriInformation(View view) {
        startActivity(new Intent(this, nutriInformationActivity.class));

    }

    public void onBackPressed() {
        this.backpress++;
        Toast.makeText(getApplicationContext(), " Press Back again to Exit ", Toast.LENGTH_SHORT).show();
        if (this.backpress > 1) {
            finish();
        }
    }
}
