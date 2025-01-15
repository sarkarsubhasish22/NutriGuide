package com.example.nutriguide;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.github.barteksc.pdfviewer.PDFView;

public class nutriInformationActivity extends AppCompatActivity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_nutri_information);
        ((PDFView) findViewById(R.id.pdfview1)).fromAsset("nutritional information.pdf").load();
    }
}
