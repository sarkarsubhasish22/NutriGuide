package com.example.nutriguide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import java.io.PrintStream;
import org.json.JSONException;
import org.json.JSONObject;

public class newFarmerActivity extends AppCompatActivity {
    Spinner education_spinner;
    EditText farmerAge;
    EditText farmerDistrict;
    EditText farmerEducation;
    EditText farmerFullName;
    TextView farmerRegisterConfirmation;
    EditText farmerState;
    EditText farmerVillage;
    int farmer_age;
    String farmer_district;
    String farmer_edu;
    String farmer_gender;
    String farmer_name;
    String farmer_state;
    String farmer_village;
    RadioGroup radioGroup;
    RadioButton selectedRB;
    Spinner state_spinner;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_new_farmer);
        long userId = getIntent().getLongExtra("userId", 0);
        PrintStream printStream = System.out;
        printStream.println("userId is " + userId);
        this.farmerFullName = (EditText) findViewById(R.id.farmerFullName);
        this.farmerAge = (EditText) findViewById(R.id.farmerAge);
        this.farmerVillage = (EditText) findViewById(R.id.farmerVillage);
        this.farmerDistrict = (EditText) findViewById(R.id.farmerDistrict);
        this.radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        this.education_spinner = (Spinner) findViewById(R.id.education_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.education_array, 17367048);
        adapter.setDropDownViewResource(17367049);
        this.education_spinner.setAdapter(adapter);
        this.education_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newFarmerActivity.this.farmer_edu = (String) parent.getItemAtPosition(position);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        this.state_spinner = (Spinner) findViewById(R.id.state_spinner);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.india_states, 17367048);
        adapter1.setDropDownViewResource(17367049);
        this.state_spinner.setAdapter(adapter1);
        this.state_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newFarmerActivity.this.farmer_state = (String) parent.getItemAtPosition(position);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void registerFarmer(final View view) {
        System.out.println(this.farmer_edu);
        if (this.farmerFullName.getText().toString().isEmpty() || this.farmerAge.getText().toString().isEmpty() || this.farmer_state.equals("State") || this.farmerDistrict.getText().toString().isEmpty() || this.farmerVillage.getText().toString().isEmpty() || this.farmer_edu.equals("Educational Qualification")) {
            Toast.makeText(getApplicationContext(), "Please fill all the field...", 0).show();
            return;
        }
        final long userId = getIntent().getLongExtra("userId", 0);
        this.farmer_name = this.farmerFullName.getText().toString();
        this.farmer_age = Integer.parseInt(this.farmerAge.getText().toString());
        this.farmer_village = this.farmerVillage.getText().toString();
        this.farmer_district = this.farmerDistrict.getText().toString();
        RadioButton radioButton = (RadioButton) findViewById(this.radioGroup.getCheckedRadioButtonId());
        this.selectedRB = radioButton;
        this.farmer_gender = radioButton.getText().toString();
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject object = new JSONObject();
        try {
            object.put("userId", userId);
            object.put("farmerName", this.farmer_name);
            object.put("farmerAge", this.farmer_age);
            object.put("farmerGender", this.farmer_gender);
            object.put("farmerEducation", this.farmer_edu);
            object.put("farmerVillage", this.farmer_village);
            object.put("farmerDistrict", this.farmer_district);
            object.put("farmerState", this.farmer_state);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(object);
        requestQueue.add(new JsonObjectRequest(1, "http://67.202.30.148:8080/NutriGuide/user/farmer", object, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                System.out.println(response);
                newFarmerActivity.this.goto_registerFarmerActivity(view, userId);
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
            }
        }));
    }

    /* access modifiers changed from: private */
    public void goto_registerFarmerActivity(View view, long userId) {
        Toast.makeText(getApplicationContext(), "Farmer Registered Successfully...", 1).show();
        Intent intent = new Intent(this, existingFarmerActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        overridePendingTransition(17432576, 17432577);
    }
}
