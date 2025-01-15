package com.example.nutriguide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class samActivity extends AppCompatActivity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_sam);
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

    public void bmi(View view) {
        startActivity(new Intent(this, bmiActivity.class));
        overridePendingTransition(17432576, 17432577);
    }

    public void muac(View view) {
        startActivity(new Intent(this, muacActivity.class));
        overridePendingTransition(17432576, 17432577);
    }

    public void cc(View view) {
        startActivity(new Intent(this, ccActivity.class));
        overridePendingTransition(17432576, 17432577);
    }

    public void sft(View view) {
        startActivity(new Intent(this, sftActivity.class));
        overridePendingTransition(17432576, 17432577);
    }

    public void goto_mdd(View view) {
        startActivity(new Intent(this, mddActivity.class));
        overridePendingTransition(17432576, 17432577);
    }
}
