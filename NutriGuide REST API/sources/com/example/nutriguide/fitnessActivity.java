package com.example.nutriguide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class fitnessActivity extends AppCompatActivity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_fitness);
        ((TextView) findViewById(R.id.farmer)).setText(Farmer.farmerName);
    }

    public void pfi(View view) {
        startActivity(new Intent(this, pfiActivity.class));
        overridePendingTransition(17432576, 17432577);
    }

    public void vo2(View view) {
        startActivity(new Intent(this, vo2Activity.class));
        overridePendingTransition(17432576, 17432577);
    }
}
