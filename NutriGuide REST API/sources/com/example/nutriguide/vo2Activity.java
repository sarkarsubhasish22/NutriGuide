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

public class vo2Activity extends AppCompatActivity {
    int age;
    EditText bodyWeight_vo2;
    float relativeVO2;
    Double vo2;
    TextView vo2_category;
    TextView vo2_value;
    Float weight;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_vo2);
        ((TextView) findViewById(R.id.farmer)).setText(Farmer.farmerName);
        this.bodyWeight_vo2 = (EditText) findViewById(R.id.bodyWeight_vo2);
        this.vo2_value = (TextView) findViewById(R.id.vo2_value);
        this.vo2_category = (TextView) findViewById(R.id.vo2_category);
    }

    public void vo2Result(View view) {
        if (this.bodyWeight_vo2.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill all the field...", 0).show();
            return;
        }
        Float valueOf = Float.valueOf(Float.parseFloat(this.bodyWeight_vo2.getText().toString()));
        this.weight = valueOf;
        Farmer.farmerWeight = (double) valueOf.floatValue();
        this.age = Farmer.farmerAge;
        double floatValue = (double) this.weight.floatValue();
        Double.isNaN(floatValue);
        double d = (double) this.age;
        Double.isNaN(d);
        Double valueOf2 = Double.valueOf(((floatValue * 0.023d) - (d * 0.034d)) + 1.65d);
        this.vo2 = valueOf2;
        double doubleValue = valueOf2.doubleValue();
        double floatValue2 = (double) this.weight.floatValue();
        Double.isNaN(floatValue2);
        this.relativeVO2 = ((float) Math.round(100.0d * ((doubleValue / floatValue2) * 1000.0d))) / 100.0f;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject object = new JSONObject();
        try {
            object.put("farmerId", Farmer.farmerId);
            object.put("farmerAge", this.age);
            object.put("farmerWeight", this.weight);
            object.put("vo2", (double) this.relativeVO2);
            System.out.println(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(object);
        requestQueue.add(new JsonObjectRequest(1, "http://67.202.30.148:8080/NutriGuide/user/farmer/VO2", object, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                TextView textView = vo2Activity.this.vo2_value;
                textView.setText("" + vo2Activity.this.relativeVO2);
                if (((double) vo2Activity.this.relativeVO2) <= 15.0d) {
                    vo2Activity.this.vo2_category.setText("Poor");
                } else if (((double) vo2Activity.this.relativeVO2) > 15.0d && ((double) vo2Activity.this.relativeVO2) <= 25.0d) {
                    vo2Activity.this.vo2_category.setText("Low Average");
                } else if (((double) vo2Activity.this.relativeVO2) > 25.0d && ((double) vo2Activity.this.relativeVO2) <= 30.0d) {
                    vo2Activity.this.vo2_category.setText("High Average");
                } else if (((double) vo2Activity.this.relativeVO2) > 30.0d && ((double) vo2Activity.this.relativeVO2) <= 40.0d) {
                    vo2Activity.this.vo2_category.setText("Good");
                } else if (((double) vo2Activity.this.relativeVO2) > 40.0d && ((double) vo2Activity.this.relativeVO2) <= 45.0d) {
                    vo2Activity.this.vo2_category.setText("Very Good");
                } else if (((double) vo2Activity.this.relativeVO2) > 45.0d) {
                    vo2Activity.this.vo2_category.setText("Excellent");
                } else {
                    vo2Activity.this.vo2_category.setText("Error");
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(vo2Activity.this.getApplicationContext(), "Network Error...", 0).show();
            }
        }));
    }

    public void sam(View view) {
        startActivity(new Intent(this, samActivity.class));
        overridePendingTransition(17432576, 17432577);
    }
}
