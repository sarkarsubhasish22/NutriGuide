package com.example.nutriguide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class sftActivity extends AppCompatActivity {
    int age;
    EditText biceps;
    Float biceps_;
    double density;
    Float density_;
    double fatFreeMass;
    Float fatFreeMass_;
    double fatMass;
    Float fatMass_;
    String gender;
    double l;
    double percentBodyFat;
    Float percentBodyFat_;
    TextView sft_body_density;
    TextView sft_fatFreeMass;
    TextView sft_fatMass;
    TextView sft_percentage_body_fat;
    EditText sft_weight;
    EditText subscapular;
    Float subscapular_;
    EditText suprailliac;
    Float suprailliac_;
    EditText triceps;
    Float triceps_;
    double weight;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_sft);
        ((TextView) findViewById(R.id.farmer)).setText(Farmer.farmerName);
        this.sft_weight = (EditText) findViewById(R.id.sft_weight);
        this.biceps = (EditText) findViewById(R.id.biceps);
        this.triceps = (EditText) findViewById(R.id.triceps);
        this.subscapular = (EditText) findViewById(R.id.subscapular);
        this.suprailliac = (EditText) findViewById(R.id.suprailliac);
        this.sft_body_density = (TextView) findViewById(R.id.sft_body_density);
        this.sft_percentage_body_fat = (TextView) findViewById(R.id.sft_percentage_body_fat);
        this.sft_fatMass = (TextView) findViewById(R.id.sft_fatMass);
        this.sft_fatFreeMass = (TextView) findViewById(R.id.sft_fatFreeMass);
        if (Farmer.farmerWeight != 0.0d) {
            EditText editText = this.sft_weight;
            editText.setText(Farmer.farmerWeight + "");
        }
    }

    public void sftResult(View view) {
        if (this.sft_weight.getText().toString().isEmpty() || this.biceps.getText().toString().isEmpty() || this.triceps.getText().toString().isEmpty() || this.subscapular.getText().toString().isEmpty() || this.suprailliac.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill all the field...", 0).show();
            return;
        }
        this.age = Farmer.farmerAge;
        this.weight = (double) Float.parseFloat(this.sft_weight.getText().toString());
        this.gender = Farmer.farmerGender;
        this.biceps_ = Float.valueOf(Float.parseFloat(this.biceps.getText().toString()));
        this.triceps_ = Float.valueOf(Float.parseFloat(this.triceps.getText().toString()));
        this.subscapular_ = Float.valueOf(Float.parseFloat(this.subscapular.getText().toString()));
        this.suprailliac_ = Float.valueOf(Float.parseFloat(this.suprailliac.getText().toString()));
        this.l = Math.log10((double) (this.biceps_.floatValue() + this.triceps_.floatValue() + this.subscapular_.floatValue() + this.suprailliac_.floatValue()));
        if (this.gender.equals("Male")) {
            System.out.println(this.gender);
            int i = this.age;
            if (i < 17) {
                this.density = 1.1533d - (this.l * 0.0643d);
            } else if (i > 17 && i <= 19) {
                this.density = 1.162d - (this.l * 0.063d);
            } else if (i > 19 && i <= 29) {
                this.density = 1.1631d - (this.l * 0.0632d);
            } else if (i > 29 && i <= 39) {
                this.density = 1.1422d - (this.l * 0.0544d);
            } else if (i > 39 && i <= 49) {
                this.density = 1.162d - (this.l * 0.07d);
            } else if (i > 49) {
                this.density = 1.1715d - (this.l * 0.0779d);
            }
        } else if (this.gender.equals("Female")) {
            int i2 = this.age;
            if (i2 < 17) {
                this.density = 1.1369d - (this.l * 0.0598d);
            } else if (i2 > 17 && i2 <= 19) {
                this.density = 1.1549d - (this.l * 0.0678d);
            } else if (i2 > 19 && i2 <= 29) {
                this.density = 1.1599d - (this.l * 0.0717d);
            } else if (i2 > 29 && i2 <= 39) {
                this.density = 1.1423d - (this.l * 0.0632d);
            } else if (i2 > 39 && i2 <= 49) {
                this.density = 1.1333d - (this.l * 0.0612d);
            } else if (i2 > 49) {
                this.density = 1.1339d - (this.l * 0.0645d);
            }
        }
        this.density_ = Float.valueOf(((float) Math.round(this.density * 100.0d)) / 100.0f);
        double d = (495.0d / this.density) - 450.0d;
        this.percentBodyFat = d;
        this.percentBodyFat_ = Float.valueOf(((float) Math.round(d * 100.0d)) / 100.0f);
        double d2 = this.weight * ((4.95d / this.density) - 4.5d);
        this.fatMass = d2;
        this.fatMass_ = Float.valueOf(((float) Math.round(d2 * 100.0d)) / 100.0f);
        double d3 = this.weight - this.fatMass;
        this.fatFreeMass = d3;
        this.fatFreeMass_ = Float.valueOf(((float) Math.round(d3 * 100.0d)) / 100.0f);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject object = new JSONObject();
        try {
            object.put("farmerId", Farmer.farmerId);
            object.put("farmerAge", Farmer.farmerAge);
            object.put("biceps", this.biceps_);
            object.put("triceps", this.triceps_);
            object.put("subscapular", this.subscapular_);
            object.put("suprailiac", this.suprailliac_);
            object.put("bodyDensity", this.density_);
            object.put("percentBodyFat", this.percentBodyFat_);
            object.put("fatMass", this.fatMass_);
            object.put("fatFreeMass", this.fatFreeMass_);
            System.out.println(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(object);
        requestQueue.add(new JsonObjectRequest(1, "http://67.202.30.148:8080/NutriGuide/user/farmer/SFT", object, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                TextView textView = sftActivity.this.sft_body_density;
                textView.setText("" + sftActivity.this.density_);
                TextView textView2 = sftActivity.this.sft_percentage_body_fat;
                textView2.setText("" + sftActivity.this.percentBodyFat_);
                TextView textView3 = sftActivity.this.sft_fatMass;
                textView3.setText("" + sftActivity.this.fatMass_ + "kg");
                TextView textView4 = sftActivity.this.sft_fatFreeMass;
                textView4.setText("" + sftActivity.this.fatFreeMass_ + "kg");
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(sftActivity.this.getApplicationContext(), "Network Error...", 0).show();
            }
        }));
    }

    public void goto_mdd(View view) {
        startActivity(new Intent(this, mddActivity.class));
        overridePendingTransition(17432576, 17432577);
    }
}
