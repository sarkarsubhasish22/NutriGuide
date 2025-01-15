package com.example.nutriguide;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class forgotPasswordActivity extends AppCompatActivity {
    String email;
    EditText forgotEmail;
    TextView yourPassword;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_forgot_password);
        this.forgotEmail = (EditText) findViewById(R.id.forgotEmail);
        this.yourPassword = (TextView) findViewById(R.id.yourPassword);
    }

    public void get_password(View view) {
        if (this.forgotEmail.getText().toString().isEmpty()) {
            this.yourPassword.setText("Please Enter Email...");
            return;
        }
        this.email = this.forgotEmail.getText().toString();
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject object = new JSONObject();
        try {
            object.put(NotificationCompat.CATEGORY_EMAIL, this.email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(object);
        requestQueue.add(new JsonObjectRequest(1, "http://67.202.30.148:8080/NutriGuide/users/forgot_password", object, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                try {
                    TextView textView = forgotPasswordActivity.this.yourPassword;
                    textView.setText("Your Password is: " + response.getString("password"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                forgotPasswordActivity.this.yourPassword.setText("Password Not Fournd!");
                System.out.println(error);
            }
        }));
    }
}
