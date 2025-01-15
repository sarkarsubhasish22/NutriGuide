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

public class bmiActivity extends AppCompatActivity {
    Float bmi;
    TextView bmi_category;
    EditText bmi_height;
    TextView bmi_value;
    EditText bmi_weight;
    Float height_;
    Float weight_;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_bmi);
        ((TextView) findViewById(R.id.farmer)).setText(Farmer.farmerName);
        this.bmi_weight = (EditText) findViewById(R.id.bmi_weight);
        this.bmi_height = (EditText) findViewById(R.id.bmi_height);
        this.bmi_value = (TextView) findViewById(R.id.bmi_value);
        this.bmi_category = (TextView) findViewById(R.id.bmi_category);
    }

    public void bmiResult(View view) {
        if (this.bmi_weight.getText().toString().isEmpty() || this.bmi_height.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill all the field...", 0).show();
            return;
        }
        this.weight_ = Float.valueOf(Float.parseFloat(this.bmi_weight.getText().toString()));
        this.height_ = Float.valueOf(Float.parseFloat(this.bmi_height.getText().toString()));
        Float valueOf = Float.valueOf(this.weight_.floatValue() / (this.height_.floatValue() * this.height_.floatValue()));
        this.bmi = valueOf;
        this.bmi = Float.valueOf(((float) Math.round(valueOf.floatValue() * 100.0f)) / 100.0f);
        Farmer.farmerWeight = (double) this.weight_.floatValue();
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject object = new JSONObject();
        try {
            object.put("farmerId", Farmer.farmerId);
            object.put("farmerHeight", this.height_);
            object.put("farmerWeight", this.weight_);
            object.put("bmi", this.bmi);
            System.out.println(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(object);
        requestQueue.add(new JsonObjectRequest(1, "http://67.202.30.148:8080/NutriGuide/user/farmer/BMI", object, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                TextView textView = bmiActivity.this.bmi_value;
                textView.setText("" + bmiActivity.this.bmi);
                if (((double) bmiActivity.this.bmi.floatValue()) <= 16.0d) {
                    bmiActivity.this.bmi_category.setText("CED Grade III(Severe)");
                } else if (((double) bmiActivity.this.bmi.floatValue()) > 16.0d && ((double) bmiActivity.this.bmi.floatValue()) <= 17.0d) {
                    bmiActivity.this.bmi_category.setText("CED Grade II(Moderate)");
                } else if (((double) bmiActivity.this.bmi.floatValue()) > 17.0d && ((double) bmiActivity.this.bmi.floatValue()) <= 18.5d) {
                    bmiActivity.this.bmi_category.setText("CED Grade I(Mild)");
                } else if (((double) bmiActivity.this.bmi.floatValue()) > 18.5d && ((double) bmiActivity.this.bmi.floatValue()) <= 20.0d) {
                    bmiActivity.this.bmi_category.setText("Low Weight Normal");
                } else if (((double) bmiActivity.this.bmi.floatValue()) > 20.0d && ((double) bmiActivity.this.bmi.floatValue()) <= 25.0d) {
                    bmiActivity.this.bmi_category.setText("Normal");
                } else if (((double) bmiActivity.this.bmi.floatValue()) > 25.0d && ((double) bmiActivity.this.bmi.floatValue()) <= 30.0d) {
                    bmiActivity.this.bmi_category.setText("Obese Grade I");
                } else if (((double) bmiActivity.this.bmi.floatValue()) > 30.0d) {
                    bmiActivity.this.bmi_category.setText("Obese Grade II");
                } else {
                    bmiActivity.this.bmi_category.setText("Error");
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(bmiActivity.this.getApplicationContext(), "Network Error...", 0).show();
            }
        }));
    }

    public void sft(View view) {
        startActivity(new Intent(this, sftActivity.class));
        overridePendingTransition(17432576, 17432577);
    }
}
