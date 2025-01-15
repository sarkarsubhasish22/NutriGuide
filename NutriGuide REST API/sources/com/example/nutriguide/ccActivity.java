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

public class ccActivity extends AppCompatActivity {
    EditText cc;
    TextView cc_category;
    float cc_value;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_cc);
        ((TextView) findViewById(R.id.farmer)).setText(Farmer.farmerName);
        this.cc = (EditText) findViewById(R.id.cc);
        this.cc_category = (TextView) findViewById(R.id.cc_category);
    }

    public void get_cc(View view) {
        if (this.cc.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill all the field...", 0).show();
            return;
        }
        float parseFloat = Float.parseFloat(this.cc.getText().toString());
        this.cc_value = parseFloat;
        this.cc_value = ((float) Math.round(parseFloat * 100.0f)) / 100.0f;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject object = new JSONObject();
        try {
            object.put("farmerId", Farmer.farmerId);
            object.put("muac", (double) this.cc_value);
            System.out.println(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(object);
        requestQueue.add(new JsonObjectRequest(1, "http://67.202.30.148:8080/NutriGuide/user/farmer/CC", object, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                if (Farmer.farmerGender.equals("Male")) {
                    if (((double) ccActivity.this.cc_value) < 22.9d) {
                        ccActivity.this.cc_category.setText("Under Nutrition");
                    } else if (((double) ccActivity.this.cc_value) >= 22.9d && ((double) ccActivity.this.cc_value) < 25.7d) {
                        ccActivity.this.cc_category.setText("Normal");
                    } else if (((double) ccActivity.this.cc_value) >= 25.7d) {
                        ccActivity.this.cc_category.setText("Obese");
                    }
                } else if (!Farmer.farmerGender.equals("Female")) {
                } else {
                    if (((double) ccActivity.this.cc_value) < 22.8d) {
                        ccActivity.this.cc_category.setText("Under Nutrition");
                    } else if (((double) ccActivity.this.cc_value) >= 22.8d && ((double) ccActivity.this.cc_value) < 25.5d) {
                        ccActivity.this.cc_category.setText("Normal");
                    } else if (((double) ccActivity.this.cc_value) >= 25.5d) {
                        ccActivity.this.cc_category.setText("Obese");
                    }
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ccActivity.this.getApplicationContext(), "Network Error...", 0).show();
            }
        }));
    }

    public void sft_from_cc(View view) {
        startActivity(new Intent(this, sftActivity.class));
        overridePendingTransition(17432576, 17432577);
    }
}
