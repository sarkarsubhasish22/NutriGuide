package com.example.nutriguide;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class existingFarmerActivity extends AppCompatActivity {
    /* access modifiers changed from: private */
    private JSONArray allFarmers;
    private String biceps;
    private String bmi;
    private String bodyDensity;
    private String cc;
    private String da;
    private String farmerHeight;
    private String farmerWeight;
    private String fatFreeMass;
    private String fatMass;
    private File filepath = new File(Environment.getExternalStorageDirectory() + "/Download/Farmers Details.xls");
    private String hr_1;
    private String hr_2;
    private String hr_3;
    List<Integer> list = new ArrayList();
    private String mdd;
    private String muac;
    private String percentBodyFat;
    private String pfi;
    /* access modifiers changed from: private */
    public JSONArray response0;
    /* access modifiers changed from: private */
    public JSONArray response1;
    /* access modifiers changed from: private */
    public JSONArray response2;
    /* access modifiers changed from: private */
    public JSONArray response3;
    /* access modifiers changed from: private */
    public JSONArray response4;
    /* access modifiers changed from: private */
    public JSONArray response5;
    /* access modifiers changed from: private */
    public JSONArray response6;
    private String subscapular;
    private String suprailiac;
    private String triceps;
    private String vo2;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_existing_farmer);
        final ListView farmersList = (ListView) findViewById(R.id.farmersList);
        final ArrayList<String> tubeLines = new ArrayList<>();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.listitem, R.id.single_farmer, tubeLines);
        farmersList.setAdapter(arrayAdapter);
        final long userId = getIntent().getLongExtra("userId", 0);
        System.out.println(userId);
        ((FloatingActionButton) findViewById(R.id.RegisterFarmerButton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent1 = new Intent(existingFarmerActivity.this, newFarmerActivity.class);
                intent1.putExtra("userId", userId);
                existingFarmerActivity.this.startActivity(intent1);
                existingFarmerActivity.this.overridePendingTransition(17432576, 17432577);
            }
        });
        Volley.newRequestQueue(getApplicationContext()).add(new JsonArrayRequest(0, "http://67.202.30.148:8080/NutriGuide/user/farmers/" + userId, (JSONArray) null, new Response.Listener<JSONArray>() {
            public void onResponse(final JSONArray response) {
                JSONArray unused = existingFarmerActivity.this.allFarmers = response;
                for (int j = 0; j < existingFarmerActivity.this.allFarmers.length(); j++) {
                    int id = existingFarmerActivity.this.allFarmers.optJSONObject(j).optInt("farmerId");
                    System.out.println(id);
                    existingFarmerActivity.this.list.add(Integer.valueOf(id));
                }
                existingFarmerActivity.this.allresponse(existingFarmerActivity.this.list.toString().replace("[", "").replace("]", ""));
                for (int i = 0; i < response.length(); i++) {
                    String line = response.optJSONObject(i).optString("farmerName");
                    if (line != null) {
                        tubeLines.add(line);
                    }
                }
                arrayAdapter.notifyDataSetChanged();
                farmersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        try {
                            System.out.println(response.getJSONObject(position));
                            existingFarmerActivity.this.fitness(view, response.getJSONObject(position));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
            }
        }));
    }

    public void fitness(View view, JSONObject farmerInfo) {
        try {
            Farmer.farmerId = farmerInfo.getLong("farmerId");
            Farmer.userId = farmerInfo.getLong("userId");
            Farmer.farmerName = farmerInfo.getString("farmerName");
            Farmer.farmerAge = farmerInfo.getInt("farmerAge");
            Farmer.farmerGender = farmerInfo.getString("farmerGender");
            Farmer.farmerEducation = farmerInfo.getString("farmerEducation");
            Farmer.farmerState = farmerInfo.getString("farmerState");
            Farmer.farmerDistrict = farmerInfo.getString("farmerDistrict");
            Farmer.farmerVillage = farmerInfo.getString("farmerVillage");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startActivity(new Intent(this, samActivity.class));
        overridePendingTransition(17432576, 17432577);
    }

    public void buttonCreateExcel(View view) {
        HSSFCell cell7;
        HSSFCell cell8;
        HSSFCell cell3;
        Toast.makeText(this, "Downloading Farmers Details.xls...", 0).show();
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet hssfSheet = hssfWorkbook.createSheet("custom sheet");
        HSSFCellStyle hssfCellStyle = hssfWorkbook.createCellStyle();
        hssfCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
        hssfCellStyle.setFillPattern(1);
        HSSFRow hssfRow = hssfSheet.createRow(1);
        HSSFCell cell1 = hssfRow.createCell(1);
        cell1.setCellValue("Name");
        cell1.setCellStyle(hssfCellStyle);
        HSSFCell cell2 = hssfRow.createCell(2);
        cell2.setCellValue("Age");
        cell2.setCellStyle(hssfCellStyle);
        HSSFCell cell32 = hssfRow.createCell(3);
        cell32.setCellValue("Gender");
        cell32.setCellStyle(hssfCellStyle);
        HSSFCell cell4 = hssfRow.createCell(4);
        cell4.setCellValue("Education");
        cell4.setCellStyle(hssfCellStyle);
        HSSFCell cell5 = hssfRow.createCell(5);
        cell5.setCellValue("Village");
        cell5.setCellStyle(hssfCellStyle);
        HSSFCell cell6 = hssfRow.createCell(6);
        cell6.setCellValue("District");
        cell6.setCellStyle(hssfCellStyle);
        HSSFCell cell72 = hssfRow.createCell(7);
        cell72.setCellValue("State");
        cell72.setCellStyle(hssfCellStyle);
        HSSFCell cell82 = hssfRow.createCell(8);
        cell82.setCellValue("Duration of Activity");
        cell82.setCellStyle(hssfCellStyle);
        HSSFCell cell9 = hssfRow.createCell(9);
        cell9.setCellValue("Heart Rate at 1st minute");
        cell9.setCellStyle(hssfCellStyle);
        HSSFCell hSSFCell = cell9;
        HSSFCell cell10 = hssfRow.createCell(10);
        cell10.setCellValue("Heart Rate at 2nd minute");
        cell10.setCellStyle(hssfCellStyle);
        HSSFCell hSSFCell2 = cell10;
        HSSFCell cell11 = hssfRow.createCell(11);
        cell11.setCellValue("Heart Rate at 3rd minute");
        cell11.setCellStyle(hssfCellStyle);
        HSSFCell hSSFCell3 = cell11;
        HSSFCell cell112 = hssfRow.createCell(12);
        cell112.setCellValue("PFI");
        cell112.setCellStyle(hssfCellStyle);
        HSSFCell hSSFCell4 = cell112;
        HSSFCell cell13 = hssfRow.createCell(13);
        cell13.setCellValue("Weight");
        cell13.setCellStyle(hssfCellStyle);
        HSSFCell hSSFCell5 = cell13;
        HSSFCell cell14 = hssfRow.createCell(14);
        cell14.setCellValue("vo2");
        cell14.setCellStyle(hssfCellStyle);
        HSSFCell hSSFCell6 = cell14;
        HSSFCell cell15 = hssfRow.createCell(15);
        cell15.setCellValue("Height");
        cell15.setCellStyle(hssfCellStyle);
        HSSFCell hSSFCell7 = cell15;
        HSSFCell cell152 = hssfRow.createCell(16);
        cell152.setCellValue("BMI");
        cell152.setCellStyle(hssfCellStyle);
        HSSFCell hSSFCell8 = cell152;
        HSSFCell cell17 = hssfRow.createCell(17);
        cell17.setCellValue("MUAC");
        cell17.setCellStyle(hssfCellStyle);
        HSSFCell hSSFCell9 = cell17;
        HSSFCell cell18 = hssfRow.createCell(18);
        cell18.setCellValue("CC");
        cell18.setCellStyle(hssfCellStyle);
        HSSFCell hSSFCell10 = cell18;
        HSSFCell cell19 = hssfRow.createCell(19);
        cell19.setCellValue("Biceps");
        cell19.setCellStyle(hssfCellStyle);
        HSSFCell hSSFCell11 = cell19;
        HSSFCell cell192 = hssfRow.createCell(20);
        cell192.setCellValue("Triceps");
        cell192.setCellStyle(hssfCellStyle);
        HSSFCell hSSFCell12 = cell192;
        HSSFCell cell21 = hssfRow.createCell(21);
        cell21.setCellValue("Subscapular");
        cell21.setCellStyle(hssfCellStyle);
        HSSFCell hSSFCell13 = cell21;
        HSSFCell cell22 = hssfRow.createCell(22);
        cell22.setCellValue("Suprailliac");
        cell22.setCellStyle(hssfCellStyle);
        HSSFCell hSSFCell14 = cell22;
        HSSFCell cell222 = hssfRow.createCell(23);
        cell222.setCellValue("Body Density");
        cell222.setCellStyle(hssfCellStyle);
        HSSFCell hSSFCell15 = cell222;
        HSSFCell cell24 = hssfRow.createCell(24);
        cell24.setCellValue("Percent Body Fat");
        cell24.setCellStyle(hssfCellStyle);
        HSSFCell hSSFCell16 = cell24;
        HSSFCell cell25 = hssfRow.createCell(25);
        cell25.setCellValue("Fat Mass");
        cell25.setCellStyle(hssfCellStyle);
        HSSFCell hSSFCell17 = cell25;
        HSSFCell cell26 = hssfRow.createCell(26);
        cell26.setCellValue("Fat Free Mass");
        cell26.setCellStyle(hssfCellStyle);
        HSSFCell hSSFCell18 = cell26;
        HSSFCell cell27 = hssfRow.createCell(27);
        cell27.setCellValue("Dietary Diversity");
        cell27.setCellStyle(hssfCellStyle);
        int k = 0;
        while (true) {
            HSSFCellStyle hssfCellStyle2 = hssfCellStyle;
            if (k >= this.allFarmers.length()) {
                break;
            }
            HSSFRow row = hssfSheet.createRow(k + 2);
            HSSFSheet hssfSheet2 = hssfSheet;
            JSONObject obj = this.allFarmers.optJSONObject(k);
            HSSFCell cell272 = cell27;
            HSSFRow hssfRow2 = hssfRow;
            String farmerid = obj.optString("farmerId");
            HSSFCell cell12 = cell1;
            HSSFCell cell23 = cell2;
            row.createCell(1).setCellValue(obj.optString("farmerName"));
            row.createCell(2).setCellValue(obj.optString("farmerAge"));
            row.createCell(3).setCellValue(obj.optString("farmerGender"));
            row.createCell(4).setCellValue(obj.optString("farmerEducation"));
            row.createCell(5).setCellValue(obj.optString("farmerVillage"));
            row.createCell(6).setCellValue(obj.optString("farmerDistrict"));
            row.createCell(7).setCellValue(obj.optString("farmerState"));
            if (this.response0 != null) {
                JSONObject response0i = null;
                int l = 0;
                while (true) {
                    JSONObject obj2 = obj;
                    if (l >= this.response0.length()) {
                        break;
                    }
                    JSONObject response0l = this.response0.optJSONObject(l);
                    HSSFCell cell83 = cell82;
                    if (response0l.optString("farmerId").equals(farmerid)) {
                        response0i = response0l;
                    }
                    l++;
                    obj = obj2;
                    cell82 = cell83;
                }
                cell8 = cell82;
                if (response0i != null) {
                    row.createCell(8).setCellValue(response0i.optString("da"));
                    row.createCell(9).setCellValue(response0i.optString("hr_1"));
                    row.createCell(10).setCellValue(response0i.optString("hr_2"));
                    row.createCell(11).setCellValue(response0i.optString("hr_3"));
                    cell3 = cell32;
                    cell7 = cell72;
                    row.createCell(12).setCellValue(response0i.optDouble("pfi"));
                } else {
                    cell3 = cell32;
                    cell7 = cell72;
                }
            } else {
                cell8 = cell82;
                cell3 = cell32;
                cell7 = cell72;
            }
            if (this.response1 != null) {
                JSONObject response1i = null;
                for (int l2 = 0; l2 < this.response1.length(); l2++) {
                    JSONObject response0l2 = this.response1.optJSONObject(l2);
                    if (response0l2.optString("farmerId").equals(farmerid)) {
                        response1i = response0l2;
                    }
                }
                if (response1i != null) {
                    row.createCell(13).setCellValue(response1i.optString("farmerWeight"));
                    row.createCell(14).setCellValue(response1i.optString("vo2"));
                }
            }
            if (this.response2 != null) {
                JSONObject response2i = null;
                for (int l3 = 0; l3 < this.response2.length(); l3++) {
                    JSONObject response0l3 = this.response2.optJSONObject(l3);
                    if (response0l3.optString("farmerId").equals(farmerid)) {
                        response2i = response0l3;
                    }
                }
                if (response2i != null) {
                    row.createCell(15).setCellValue(response2i.optString("farmerHeight"));
                    row.createCell(16).setCellValue(response2i.optString("bmi"));
                }
            }
            if (this.response3 != null) {
                JSONObject response3i = null;
                for (int l4 = 0; l4 < this.response3.length(); l4++) {
                    JSONObject response0l4 = this.response3.optJSONObject(l4);
                    if (response0l4.optString("farmerId").equals(farmerid)) {
                        response3i = response0l4;
                    }
                }
                if (response3i != null) {
                    row.createCell(17).setCellValue(response3i.optString("muac"));
                }
            }
            if (this.response4 != null) {
                JSONObject response4i = null;
                for (int l5 = 0; l5 < this.response4.length(); l5++) {
                    JSONObject response0l5 = this.response4.optJSONObject(l5);
                    if (response0l5.optString("farmerId").equals(farmerid)) {
                        response4i = response0l5;
                    }
                }
                if (response4i != null) {
                    row.createCell(18).setCellValue(response4i.optString("cc"));
                }
            }
            if (this.response5 != null) {
                JSONObject response5i = null;
                for (int l6 = 0; l6 < this.response5.length(); l6++) {
                    JSONObject response0l6 = this.response5.optJSONObject(l6);
                    if (response0l6.optString("farmerId").equals(farmerid)) {
                        response5i = response0l6;
                    }
                }
                if (response5i != null) {
                    row.createCell(19).setCellValue(response5i.optString("biceps"));
                    row.createCell(20).setCellValue(response5i.optString("triceps"));
                    row.createCell(21).setCellValue(response5i.optString("subscapular"));
                    row.createCell(22).setCellValue(response5i.optString("suprailiac"));
                    row.createCell(23).setCellValue(response5i.optString("bodyDensity"));
                    row.createCell(24).setCellValue(response5i.optString("percentBodyFat"));
                    row.createCell(25).setCellValue(response5i.optString("fatMass"));
                    row.createCell(26).setCellValue(response5i.optString("fatFreeMass"));
                }
            }
            if (this.response6 != null) {
                JSONObject response6i = null;
                for (int l7 = 0; l7 < this.response6.length(); l7++) {
                    JSONObject response0l7 = this.response6.optJSONObject(l7);
                    if (response0l7.optString("farmerId").equals(farmerid)) {
                        response6i = response0l7;
                    }
                }
                if (response6i != null) {
                    row.createCell(27).setCellValue(response6i.optString("mdd"));
                }
            }
            k++;
            cell32 = cell3;
            hssfCellStyle = hssfCellStyle2;
            hssfSheet = hssfSheet2;
            cell27 = cell272;
            hssfRow = hssfRow2;
            cell1 = cell12;
            cell2 = cell23;
            cell82 = cell8;
            cell72 = cell7;
        }
        HSSFCell hSSFCell19 = cell27;
        HSSFRow hSSFRow = hssfRow;
        HSSFCell hSSFCell20 = cell1;
        HSSFCell hSSFCell21 = cell2;
        HSSFCell hSSFCell22 = cell82;
        HSSFCell hSSFCell23 = cell32;
        HSSFCell hSSFCell24 = cell72;
        try {
            this.filepath.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(this.filepath);
            hssfWorkbook.write(fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "Downloaded", 1).show();
    }

    public void allresponse(String text) {
        String str = text;
        RequestQueue requestQueue0 = Volley.newRequestQueue(getApplicationContext());
        requestQueue0.add(new JsonArrayRequest(0, "http://67.202.30.148:8080/NutriGuide/user/farmer/PFI/" + str, (JSONArray) null, new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                JSONArray unused = existingFarmerActivity.this.response0 = response;
                System.out.println(response);
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
            }
        }));
        RequestQueue requestQueue1 = Volley.newRequestQueue(getApplicationContext());
        requestQueue1.add(new JsonArrayRequest(0, "http://67.202.30.148:8080/NutriGuide/user/farmer/VO2/" + str, (JSONArray) null, new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                JSONArray unused = existingFarmerActivity.this.response1 = response;
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
            }
        }));
        RequestQueue requestQueue2 = Volley.newRequestQueue(getApplicationContext());
        requestQueue2.add(new JsonArrayRequest(0, "http://67.202.30.148:8080/NutriGuide/user/farmer/BMI/" + str, (JSONArray) null, new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                JSONArray unused = existingFarmerActivity.this.response2 = response;
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
            }
        }));
        RequestQueue requestQueue3 = Volley.newRequestQueue(getApplicationContext());
        requestQueue3.add(new JsonArrayRequest(0, "http://67.202.30.148:8080/NutriGuide/user/farmer/MUAC/" + str, (JSONArray) null, new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                JSONArray unused = existingFarmerActivity.this.response3 = response;
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
            }
        }));
        RequestQueue requestQueue4 = Volley.newRequestQueue(getApplicationContext());
        requestQueue4.add(new JsonArrayRequest(0, "http://67.202.30.148:8080/NutriGuide/user/farmer/CC/" + str, (JSONArray) null, new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                JSONArray unused = existingFarmerActivity.this.response4 = response;
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
            }
        }));
        RequestQueue requestQueue5 = Volley.newRequestQueue(getApplicationContext());
        requestQueue5.add(new JsonArrayRequest(0, "http://67.202.30.148:8080/NutriGuide/user/farmer/SFT/" + str, (JSONArray) null, new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                JSONArray unused = existingFarmerActivity.this.response5 = response;
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
            }
        }));
        RequestQueue requestQueue6 = Volley.newRequestQueue(getApplicationContext());
        StringBuilder sb = new StringBuilder();
        RequestQueue requestQueue = requestQueue0;
        sb.append("http://67.202.30.148:8080/NutriGuide/user/farmer/MDD/");
        sb.append(str);
        requestQueue6.add(new JsonArrayRequest(0, sb.toString(), (JSONArray) null, new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                JSONArray unused = existingFarmerActivity.this.response6 = response;
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
            }
        }));
    }
}
