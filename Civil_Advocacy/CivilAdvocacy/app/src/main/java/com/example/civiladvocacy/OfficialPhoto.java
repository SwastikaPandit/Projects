package com.example.civiladvocacy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Map;

public class OfficialPhoto extends AppCompatActivity {

    Map<String, Object> details;
    TextView locationTextView, individualNameTextView, individualOfficeTextView;
    ImageView individualPhotoTextView, individualPartyLogoTextView;
    ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official_photo);
        getID();
        details = (Map<String, Object>) getIntent().getSerializableExtra("details");
        setID(details);

    }

    /***********************************************
     Getter and Setter
     ***********************************************/

    private void getID() {
        locationTextView = findViewById(R.id.locationTextView2);
        individualNameTextView = findViewById(R.id.individualNameTextView);
        individualOfficeTextView = findViewById(R.id.individualOfficeTextView);
        individualPhotoTextView = findViewById(R.id.individualPhotoTextView);
        individualPartyLogoTextView = findViewById(R.id.individualPartyLogoTextView);
        layout = findViewById(R.id.individualOfficialLayout);
    }

    private void setID(Map<String, Object> details) {
        locationTextView.setText((CharSequence) details.get("location"));

        individualNameTextView.setText((CharSequence) details.get("name"));
        individualOfficeTextView.setText((CharSequence) details.get("postName"));
        String Photo = details.get("photoUrl").toString();
        String str = Photo.replace("http:","https:");
        Picasso.get().load(str).error(R.drawable.brokenimage).into(individualPhotoTextView);
        if(!isNetworkConnected()){
            individualPhotoTextView.setImageResource(R.drawable.brokenimage);
        }
        String Party = details.get("party").toString();
        if(Party.contains("Republican")){
            individualPartyLogoTextView.setImageResource(R.drawable.rep_logo);
            layout.setBackgroundColor(ContextCompat.getColor(this,R.color.Republican));
        }else if(Party.contains("Democratic")){
            individualPartyLogoTextView.setImageResource(R.drawable.dem_logo);
            layout.setBackgroundColor(ContextCompat.getColor(this,R.color.Democratic));
        }else {
            individualPartyLogoTextView.setVisibility(ImageView.GONE);
            layout.setBackgroundColor(ContextCompat.getColor(this,R.color.black));
        }
    }

    /***********************************************
     Extra's
     ***********************************************/
    public boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        return (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting());
    }
}