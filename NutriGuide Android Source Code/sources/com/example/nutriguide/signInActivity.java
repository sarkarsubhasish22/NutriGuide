package com.example.nutriguide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class signInActivity extends AppCompatActivity {
    String checkEmail;
    String checkPassword;
    EditText enterEmail;
    EditText enterPassword;
    TextView logInConfirmation;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_sign_in);
        this.enterEmail = (EditText) findViewById(R.id.enterEmail);
        this.enterPassword = (EditText) findViewById(R.id.enterPassword);
        this.logInConfirmation = (TextView) findViewById(R.id.logInConfirmation);
    }

    public void userLogIn(final View view) {
        this.logInConfirmation.setText("");
        if (this.enterEmail.getText().toString().isEmpty() || this.enterPassword.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill all the field...", 0).show();
            return;
        }
        this.checkEmail = this.enterEmail.getText().toString();
        this.checkPassword = this.enterPassword.getText().toString();
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject object = new JSONObject();
        try {
            object.put(NotificationCompat.CATEGORY_EMAIL, this.checkEmail);
            object.put("password", this.checkPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(object);
        requestQueue.add(new JsonObjectRequest(1, "http://67.202.30.148:8080/NutriGuide/users/login", object, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("userId").equals("0")) {
                        Toast.makeText(signInActivity.this.getApplicationContext(), "Incorrect Email or Password...", 0).show();
                    } else {
                        signInActivity.this.registerFarmer(view, response);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(signInActivity.this.getApplicationContext(), "Error getting response...", 0).show();
                System.out.println(error);
            }
        }));
    }

    public void registerFarmer(View view, JSONObject response) {
        Intent intent = new Intent(this, existingFarmerActivity.class);
        try {
            intent.putExtra("userId", response.getLong("userId"));
            startActivity(intent);
            overridePendingTransition(17432576, 17432577);
            Toast.makeText(getApplicationContext(), "Successfully logged in", 1).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void goto_forgot_password(View view) {
        startActivity(new Intent(this, forgotPasswordActivity.class));
    }
}
