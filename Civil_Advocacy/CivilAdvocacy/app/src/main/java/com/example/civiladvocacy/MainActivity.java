package com.example.civiladvocacy;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Object> officialList;
    FusedLocationProviderClient fusedLocationProviderClient;
    TextView Location, NodataTextView;
    private static final int PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!isNetworkConnected()){
            setContentView(R.layout.activity_no_internet);
            setTitle("Know Your Government");
        }else{
            setContentView(R.layout.activity_main);
            setID();
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            getLocationDetails();
        }
    }

    public void setID(){
        Location = findViewById(R.id.locationTextView);
        NodataTextView = findViewById(R.id.NodataTextView);
        recyclerView = findViewById(R.id.officialRecyclerView);
    }

    private void setAdapter() {
        System.out.println("SetAdapter");
        recyclerView.setAdapter(new OfficialViewAdapter(this,officialList));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("location", location);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        location = savedInstanceState.getString("location");
        fetchData(location);
    }

    /***********************************************
     Task bar Menu function
     ***********************************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Intent aboutIntent = new Intent(this,About.class);
                startActivity(aboutIntent);
                return true;
            case R.id.search:
                EditText locationView = new EditText(this);
                new AlertDialog.Builder(this)
                        .setTitle("Enter a Address")
                        .setView(locationView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @SuppressLint("ResourceAsColor")
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                locationView.setTextColor(R.color.white);
                                if(!isNetworkConnected()){
                                    setContentView(R.layout.activity_no_internet);
                                    return;
                                }else{
                                    setContentView(R.layout.activity_main);
                                    setID();
                                }
                                location = locationView.getText().toString();
                                fetchData(location);
                            }
                        })
                        .setNegativeButton("CANCEL",null)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /***********************************************
     Location function
     ***********************************************/
    String location = null;
    public double wayLatitude = 0.0, wayLongitude = 0.0;

    public void getLocationDetails(){
        final String[] locations = {"Chicago,IL"};
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);

        } else {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    wayLatitude=addresses.get(0).getLatitude();
                    wayLongitude=addresses.get(0).getLongitude();
                    locations[0] =  addresses.get(0).getAddressLine(0);
                    fetchData(addresses.get(0).getAddressLine(0));
                }});
        }
    }

    /***********************************************
                API function
     ***********************************************/
    private RequestQueue queue;
    private List<Object> addressList = new ArrayList<>();

    public void fetchData(String address) {
        queue = Volley.newRequestQueue(this);
        Uri.Builder builder = Uri.parse("https://www.googleapis.com/civicinfo/v2/representatives").buildUpon();
        builder.appendQueryParameter("key", "AIzaSyCu-7miMgIUmNz4whrS260KY-0RdA_Zge4");
        builder.appendQueryParameter("address",address);

        String url = builder.build().toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ArrayList<Map<String,Object>> list = new ArrayList<>();
                    JSONObject normalizedInput = response.getJSONObject("normalizedInput");
                    JSONArray offices = response.getJSONArray("offices");
                    JSONArray official = response.getJSONArray("officials");

                    String currentLocation = normalizedInput.getString("line1") + " " + normalizedInput.getString("city") + " " + normalizedInput.getString("state") + " " + normalizedInput.getString("zip");
                    System.out.println(currentLocation.toString());
                    /*********************************************** Parsing the officials ***********************************************/
                    for (int i = 0; i < official.length(); i++) {
                        Map<String, Object> map = new HashMap<>();
                        String address = "";

                        JSONObject officialObj = (JSONObject) official.get(i);

                        if(officialObj.has("address")){
                            JSONObject o = (JSONObject) officialObj.getJSONArray("address").get(0);
                            address = o.getString("line1")+" "+o.getString("city")+" "+o.getString("state")+" "+o.getString("zip");
                        }

                        map.put("address",  address);
                        map.put("name",     officialObj.getString("name"));
                        map.put("location", currentLocation);
                        map.put("party",    officialObj.getString("party"));
                        map.put("photoUrl", officialObj.has("photoUrl") ? officialObj.getString("photoUrl") : "");
                        map.put("email",    officialObj.has("emails")   ? officialObj.getJSONArray("emails").get(0).toString() : "");
                        map.put("url",      officialObj.has("urls")     ? officialObj.getJSONArray("urls").get(0).toString() : "");
                        map.put("phone",    officialObj.has("phones")   ? officialObj.getJSONArray("phones").get(0).toString() : "");

                        List<String[]> list1 = new ArrayList<>();
                        if(officialObj.has("channels")){
                            JSONArray array = officialObj.getJSONArray("channels");
                            for (int j = 0; j < array.length(); j++) {
                                JSONObject object = (JSONObject) array.get(j);
                                String[] channel = {object.getString("type"), object.getString("id")};
                                list1.add(channel);
                            }
                        }
                        map.put("channels",list1);
                        list.add(map);
                    }
                    /******/

                    /*********************************************** Parsing the officies ***********************************************/
                    addressList = new ArrayList<>();
                    for (int i = 0; i < offices.length(); i++) {
                        JSONObject object = (JSONObject) offices.get(i);
                        JSONArray array = object.getJSONArray("officialIndices");
                        for (int j = 0; j < array.length(); j++) {
                            Map<String, Object> ob = (Map<String, Object>) list.get(Integer.parseInt(array.get(j).toString()));
                            ob.put("postName",object.getString("name"));
                            addressList.add(ob);
                        }
                    }
                    /******/
                    System.out.println(addressList.toString());
                    if(addressList == null) {
                        Toast.makeText(getApplicationContext(), "Please Enter Appropriate City Name", Toast.LENGTH_SHORT).show();
                    }else {
                        setTitle("Civil Advocacy");
                        officialList = addressList;

                        if(NodataTextView != null){
                            NodataTextView.setText(currentLocation);
                        }
                        if(Location != null){
                            Location.setText(currentLocation);
                        }
                    }
                    setAdapter();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("VolleyError");
                Log.d("onResponse: ",error.toString());
            }
        });
        queue.add(jsonObjectRequest);
    }

    /***********************************************
                Network functions
     ***********************************************/

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationDetails();
            }
            else {
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }
    }
}