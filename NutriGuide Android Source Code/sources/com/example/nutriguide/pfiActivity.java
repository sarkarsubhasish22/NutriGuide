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

public class pfiActivity extends AppCompatActivity {
    int dur;
    EditText hr1;
    EditText hr2;
    EditText hr3;
    Float hr_1;
    Float hr_2;
    Float hr_3;
    Float pfi_cal;
    TextView pfi_category;
    TextView pfi_value;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_pfi);
        ((TextView) findViewById(R.id.farmer)).setText(Farmer.farmerName);
        this.hr1 = (EditText) findViewById(R.id.hr1);
        this.hr2 = (EditText) findViewById(R.id.hr2);
        this.hr3 = (EditText) findViewById(R.id.hr3);
        this.pfi_value = (TextView) findViewById(R.id.pfi_value);
        this.pfi_category = (TextView) findViewById(R.id.pfi_category);
    }

    public void pfiResult(View view) {
        if (this.hr3.getText().toString().isEmpty() || this.hr2.getText().toString().isEmpty() || this.hr1.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill all the field...", 0).show();
            return;
        }
        this.dur = 300;
        this.hr_1 = Float.valueOf(Float.parseFloat(this.hr1.getText().toString()));
        this.hr_2 = Float.valueOf(Float.parseFloat(this.hr2.getText().toString()));
        this.hr_3 = Float.valueOf(Float.parseFloat(this.hr3.getText().toString()));
        Float valueOf = Float.valueOf((((float) this.dur) / ((this.hr_1.floatValue() + this.hr_2.floatValue()) + this.hr_3.floatValue())) * 100.0f);
        this.pfi_cal = valueOf;
        this.pfi_cal = Float.valueOf(((float) Math.round(valueOf.floatValue() * 100.0f)) / 100.0f);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject object = new JSONObject();
        try {
            object.put("farmerId", Farmer.farmerId);
            object.put("hr_1", this.hr_1);
            object.put("hr_2", this.hr_2);
            object.put("hr_3", this.hr_3);
            object.put("da", this.dur);
            object.put("pfi", this.pfi_cal);
            System.out.println(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(object);
        requestQueue.add(new JsonObjectRequest(1, "http://67.202.30.148:8080/NutriGuide/user/farmer/PFI", object, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                TextView textView = pfiActivity.this.pfi_value;
                textView.setText("" + pfiActivity.this.pfi_cal);
                if (((double) pfiActivity.this.pfi_cal.floatValue()) <= 80.0d) {
                    pfiActivity.this.pfi_category.setText("Poor");
                } else if (((double) pfiActivity.this.pfi_cal.floatValue()) > 80.0d && ((double) pfiActivity.this.pfi_cal.floatValue()) <= 100.0d) {
                    pfiActivity.this.pfi_category.setText("Low Average");
                } else if (((double) pfiActivity.this.pfi_cal.floatValue()) > 100.0d && ((double) pfiActivity.this.pfi_cal.floatValue()) <= 115.0d) {
                    pfiActivity.this.pfi_category.setText("High Average");
                } else if (((double) pfiActivity.this.pfi_cal.floatValue()) > 115.0d && ((double) pfiActivity.this.pfi_cal.floatValue()) <= 135.0d) {
                    pfiActivity.this.pfi_category.setText("Good");
                } else if (((double) pfiActivity.this.pfi_cal.floatValue()) > 135.0d && ((double) pfiActivity.this.pfi_cal.floatValue()) <= 150.0d) {
                    pfiActivity.this.pfi_category.setText("Very Good");
                } else if (((double) pfiActivity.this.pfi_cal.floatValue()) > 150.0d) {
                    pfiActivity.this.pfi_category.setText("Excellent");
                } else {
                    pfiActivity.this.pfi_category.setText("Error");
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(pfiActivity.this.getApplicationContext(), "Network Error...", 0).show();
            }
        }));
    }

    public void sam(View view) {
        startActivity(new Intent(this, samActivity.class));
        overridePendingTransition(17432576, 17432577);
    }
}
