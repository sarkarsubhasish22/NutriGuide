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

public class signUpActivity extends AppCompatActivity {
    String conPassword;
    EditText confirmPassword;
    long contact;
    EditText contactNumber;
    EditText createPassword;
    String email;
    String fullName;
    EditText newEmail;
    EditText newFullName;

    /* renamed from: org  reason: collision with root package name */
    String f0org;
    EditText organization;
    String password;
    TextView signUpConfirmation;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_sign_up);
        this.newFullName = (EditText) findViewById(R.id.newFullName);
        this.newEmail = (EditText) findViewById(R.id.newEmail);
        this.createPassword = (EditText) findViewById(R.id.createPassword);
        this.confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        this.organization = (EditText) findViewById(R.id.organization);
        this.contactNumber = (EditText) findViewById(R.id.contactNumber);
        this.signUpConfirmation = (TextView) findViewById(R.id.signUpConfirmation);
    }

    public void newSignUp(final View view) {
        if (this.newFullName.getText().toString().isEmpty() || this.newEmail.getText().toString().isEmpty() || this.createPassword.getText().toString().isEmpty() || this.confirmPassword.getText().toString().isEmpty() || this.contactNumber.getText().toString().isEmpty() || this.organization.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill all the field...", 0).show();
        } else if (!this.newEmail.getText().toString().contains("@")) {
            Toast.makeText(getApplicationContext(), "Please provide a valid email...", 0).show();
        } else if (this.contactNumber.getText().toString().length() == 10) {
            this.fullName = this.newFullName.getText().toString();
            this.email = this.newEmail.getText().toString();
            this.password = this.createPassword.getText().toString();
            this.conPassword = this.confirmPassword.getText().toString();
            this.contact = Long.parseLong(this.contactNumber.getText().toString());
            this.f0org = this.organization.getText().toString();
            if (this.password.equals(this.conPassword)) {
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                JSONObject object = new JSONObject();
                try {
                    object.put("userName", this.fullName);
                    object.put(NotificationCompat.CATEGORY_EMAIL, this.email);
                    object.put("password", this.password);
                    object.put("contactNo", this.contact);
                    object.put("organization", this.f0org);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println(object);
                requestQueue.add(new JsonObjectRequest(1, "http://67.202.30.148:8080/NutriGuide/users/register", object, new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        try {
                            if (response.getString("userId").equals("0")) {
                                signUpActivity.this.signin_from_signup(view, "User Already Exists!");
                                return;
                            }
                            signUpActivity.this.signin_from_signup(view, response.getString("userName") + " successfully registered");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(signUpActivity.this.getApplicationContext(), "Error getting response...", 0).show();
                    }
                }));
                return;
            }
            Toast.makeText(getApplicationContext(), "Passwords does not match...", 0).show();
        } else {
            Toast.makeText(getApplicationContext(), "Please provide a valid Contact Number...", 0).show();
        }
    }

    /* access modifiers changed from: private */
    public void signin_from_signup(View view, String message) {
        startActivity(new Intent(this, signInActivity.class));
        Toast.makeText(getApplicationContext(), message, 1).show();
    }
}
