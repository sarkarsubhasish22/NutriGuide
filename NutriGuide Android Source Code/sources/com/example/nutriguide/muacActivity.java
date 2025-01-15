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

public class muacActivity extends AppCompatActivity {
    EditText muac;
    TextView muac_category;
    float muac_value;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_muac);
        ((TextView) findViewById(R.id.farmer)).setText(Farmer.farmerName);
        this.muac = (EditText) findViewById(R.id.muac);
        this.muac_category = (TextView) findViewById(R.id.muac_category);
    }

    public void get_muac(View view) {
        if (this.muac.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill all the field...", 0).show();
            return;
        }
        float parseFloat = Float.parseFloat(this.muac.getText().toString());
        this.muac_value = parseFloat;
        this.muac_value = ((float) Math.round(parseFloat * 100.0f)) / 100.0f;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject object = new JSONObject();
        try {
            object.put("farmerId", Farmer.farmerId);
            object.put("muac", (double) this.muac_value);
            System.out.println(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(object);
        requestQueue.add(new JsonObjectRequest(1, "http://67.202.30.148:8080/NutriGuide/user/farmer/MUAC", object, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                if (Farmer.farmerGender.equals("Male")) {
                    if (((double) muacActivity.this.muac_value) < 22.9d) {
                        muacActivity.this.muac_category.setText("Under Nutrition");
                    } else if (((double) muacActivity.this.muac_value) >= 22.9d && ((double) muacActivity.this.muac_value) < 25.7d) {
                        muacActivity.this.muac_category.setText("Normal");
                    } else if (((double) muacActivity.this.muac_value) >= 25.7d) {
                        muacActivity.this.muac_category.setText("Obese");
                    }
                } else if (!Farmer.farmerGender.equals("Female")) {
                } else {
                    if (((double) muacActivity.this.muac_value) < 22.8d) {
                        muacActivity.this.muac_category.setText("Under Nutrition");
                    } else if (((double) muacActivity.this.muac_value) >= 22.8d && ((double) muacActivity.this.muac_value) < 25.5d) {
                        muacActivity.this.muac_category.setText("Normal");
                    } else if (((double) muacActivity.this.muac_value) >= 25.5d) {
                        muacActivity.this.muac_category.setText("Obese");
                    }
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(muacActivity.this.getApplicationContext(), "Network Error...", 0).show();
            }
        }));
    }

    public void sft_from_muac(View view) {
        startActivity(new Intent(this, sftActivity.class));
        overridePendingTransition(17432576, 17432577);
    }
}
