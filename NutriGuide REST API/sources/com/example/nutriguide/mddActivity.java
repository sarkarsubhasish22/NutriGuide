package com.example.nutriguide;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
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

public class mddActivity extends AppCompatActivity {
    CheckBox checkBox1;
    CheckBox checkBox10;
    CheckBox checkBox2;
    CheckBox checkBox3;
    CheckBox checkBox4;
    CheckBox checkBox5;
    CheckBox checkBox6;
    CheckBox checkBox7;
    CheckBox checkBox8;
    CheckBox checkBox9;
    int foodGroups;
    TextView mdd_category;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_mdd);
        ((TextView) findViewById(R.id.farmer)).setText(Farmer.farmerName);
        this.checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
        this.checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
        this.checkBox3 = (CheckBox) findViewById(R.id.checkBox3);
        this.checkBox4 = (CheckBox) findViewById(R.id.checkBox4);
        this.checkBox5 = (CheckBox) findViewById(R.id.checkBox5);
        this.checkBox6 = (CheckBox) findViewById(R.id.checkBox6);
        this.checkBox7 = (CheckBox) findViewById(R.id.checkBox7);
        this.checkBox8 = (CheckBox) findViewById(R.id.checkBox8);
        this.checkBox9 = (CheckBox) findViewById(R.id.checkBox9);
        this.checkBox10 = (CheckBox) findViewById(R.id.checkBox10);
        this.mdd_category = (TextView) findViewById(R.id.mdd_category);
    }

    public void mdd(View view) {
        this.foodGroups = 0;
        if (this.checkBox1.isChecked()) {
            this.foodGroups++;
        }
        if (this.checkBox2.isChecked()) {
            this.foodGroups++;
        }
        if (this.checkBox3.isChecked()) {
            this.foodGroups++;
        }
        if (this.checkBox4.isChecked()) {
            this.foodGroups++;
        }
        if (this.checkBox5.isChecked()) {
            this.foodGroups++;
        }
        if (this.checkBox6.isChecked()) {
            this.foodGroups++;
        }
        if (this.checkBox7.isChecked()) {
            this.foodGroups++;
        }
        if (this.checkBox8.isChecked()) {
            this.foodGroups++;
        }
        if (this.checkBox9.isChecked()) {
            this.foodGroups++;
        }
        if (this.checkBox10.isChecked()) {
            this.foodGroups++;
        }
        System.out.println(this.foodGroups);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject object = new JSONObject();
        try {
            object.put("farmerId", Farmer.farmerId);
            object.put("mdd", this.foodGroups);
            System.out.println(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(object);
        requestQueue.add(new JsonObjectRequest(1, "http://67.202.30.148:8080/NutriGuide/user/farmer/MDD", object, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                if (mddActivity.this.foodGroups >= 5) {
                    mddActivity.this.mdd_category.setText("High Dietary Diversity..");
                } else {
                    mddActivity.this.mdd_category.setText("Low Dietary Diversity...");
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mddActivity.this.getApplicationContext(), "Network Error...", 0).show();
            }
        }));
    }
}
