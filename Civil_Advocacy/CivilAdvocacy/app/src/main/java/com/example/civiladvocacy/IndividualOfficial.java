package com.example.civiladvocacy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class IndividualOfficial extends AppCompatActivity {

    ImageView facebookImageView,youtubeImageView,twitterImageView,partyLogoImageView,officialImageView;
    TextView officialNameTextView,officialOfficeTextView,officialPartyTextView,officialAddressValue,officialPhoneValue,officialEmailValue,officialWebsiteValue,location;
    TextView officialAddressTextView,officialPhoneTextView,officialEmailTextView,officialWebsiteTextView;
    String facbookID,youtubeID,twitterID, partyID, addressID, phoneID, emailID, websiteID;
    ConstraintLayout layout;
    Map<String,Object> details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual);
        setID();
        details = (Map<String, Object>) getIntent().getSerializableExtra("Details");
        parseJson(details);
    }

    private void parseJson(Map<String, Object> details) {
        location.setText((CharSequence) details.get("location"));
        officialNameTextView.setText((CharSequence) details.get("name"));
        officialOfficeTextView.setText((CharSequence) details.get("postName"));

        String party = (String) details.get("party");
        if(!party.equals("")) {
            officialPartyTextView.setText("(" + party + ")");
            if(party.contains("Republican")){
                partyID = "https://www.gop.com/";
                partyLogoImageView.setImageResource(R.drawable.rep_logo);
                layout.setBackgroundColor(ContextCompat.getColor(this,R.color.Republican));
            }else if(party.contains("Democratic")){
                partyID = "https://democrats.org/";
                partyLogoImageView.setImageResource(R.drawable.dem_logo);
                layout.setBackgroundColor(ContextCompat.getColor(this,R.color.Democratic));
            }else{
                partyLogoImageView.setVisibility(ImageView.GONE);
                layout.setBackgroundColor(ContextCompat.getColor(this,R.color.black));
            }
        }

        String address = details.get("address").toString();
        if(!address.equals("")){
            addressID=address;
            SpannableString spannableString = new SpannableString(address);
            spannableString.setSpan(new UnderlineSpan(), 0,  spannableString.length(), 0);
            officialAddressValue.setText(spannableString);
        }else{
            officialAddressTextView.setVisibility(TextView.GONE);
            officialAddressValue.setVisibility(TextView.GONE);
        }

        String phone = details.get("phone").toString();
        if(!phone.equals("")){
            phoneID=phone;
            officialPhoneValue.setText(phone);
        }else{
            officialPhoneTextView.setVisibility(TextView.GONE);
            officialPhoneValue.setVisibility(TextView.GONE);
        }

        String email = details.get("email").toString();
        if(!email.equals("")){
            emailID = email;
            officialEmailValue.setText(email);
        }else{
            officialEmailTextView.setVisibility(TextView.GONE);
            officialEmailValue.setVisibility(TextView.GONE);
        }

        String url = details.get("url").toString();
        if(!url.equals("")){
            websiteID = url;
            officialWebsiteValue.setText(url);
        }else{
            officialWebsiteTextView.setVisibility(TextView.GONE);
            officialWebsiteValue.setVisibility(TextView.GONE);
        }

        String photo = details.get("photoUrl").toString();
        if(!photo.equals("")) {
            String Photourl = photo.replace("http:", "https:");
            Picasso.get().load(Photourl).error(R.drawable.brokenimage).into(officialImageView);
        }else{
            officialImageView.setImageResource(R.drawable.missing);
        }
        if(!isNetworkConnected()){
            officialImageView.setImageResource(R.drawable.brokenimage);
        }

        List<String[]> list = (List<String[]>) details.get("channels");
        for(String[] str : list){
            if(str[0].equals("Facebook")){
                facbookID = str[1];
                facebookImageView.setImageResource(R.drawable.facebook);
            }else if(str[0].equals("Twitter")){
                twitterID = str[1];
                twitterImageView.setImageResource(R.drawable.twitter);
            }else if(str[0].equals("YouTube")){
                youtubeID = str[1];
                youtubeImageView.setImageResource(R.drawable.youtube);
            }
        }
        if(facbookID == null){
            facebookImageView.setVisibility(View.GONE);
        }
        if(twitterID == null){
            twitterImageView.setVisibility(View.GONE);
        }
        if(youtubeID == null){
            youtubeImageView.setVisibility(View.GONE);
        }
    }

    /***********************************************
     Setting IDs
    ***********************************************/
    private void setID() {
        facebookImageView = findViewById(R.id.officialFBLink);
        youtubeImageView = findViewById(R.id.officialYoutubeLink);
        twitterImageView = findViewById(R.id.officialTwitterLink);
        partyLogoImageView = findViewById(R.id.officialPartyLogo);
        officialImageView = findViewById(R.id.officialPhotoTextView);
        officialNameTextView = findViewById(R.id.officialNameTextView);
        officialOfficeTextView = findViewById(R.id.officialOfficeTextView);
        officialPartyTextView = findViewById(R.id.officialPartyTextView);
        officialAddressValue = findViewById(R.id.officialAddressValue);
        officialPhoneValue = findViewById(R.id.officialPhoneValue);
        officialEmailValue = findViewById(R.id.officialEmailValue);
        officialWebsiteValue = findViewById(R.id.officialWebsiteValue);
        officialAddressTextView = findViewById(R.id.officialAddressTextView);
        officialPhoneTextView = findViewById(R.id.officialPhoneTextView);
        officialEmailTextView = findViewById(R.id.officialEmailTextView);
        officialWebsiteTextView = findViewById(R.id.officialWebsiteTextView);
        location = findViewById(R.id.locationTextView3);
        layout = findViewById(R.id.partyBackgroundLayout);
    }

    /***********************************************
                Social Media Links
    ***********************************************/
    public void onClickFacebook(View view) {
        String name = facbookID;
        Intent intent = null;
        Uri uri ;
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.facebook.katana", 0);
            if (info.versionCode>=3002850) {
                uri = Uri.parse("fb://facewebmodal/f?href=https://www.facebook.com/" + name);
            }else{
                uri = Uri.parse("fb://page/" + name);
            }
        } catch (PackageManager.NameNotFoundException e) {
            uri = Uri.parse("https://www.facebook.com/"+name);
        }
        intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        startActivity(intent);
    }

    public void onClickYoutube(View view) {
        String name = youtubeID;
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.youtube.com/" + name)));
        }
    }

    public void onClickTwitter(View view) {
        String name = twitterID;
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.twitter.android");
            intent.setData(Uri.parse("twitter://user?screen_name=" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.twitter.com/" + name)));
        }
    }

    /***********************************************
                    Party Link
    ***********************************************/
    public void onClickPartyLogo(View view) {
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(partyID));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(partyID)));
        }
    }

    public void onClickImage(View view){
        if(!details.get("photoUrl").equals("")) {
            Intent intent = new Intent(this, OfficialPhoto.class);
            intent.putExtra("details", (Serializable) details);
            startActivity(intent);
        }
    }

    /***********************************************
                    Contact Links
    ***********************************************/

    public void onClickAddress(View view) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q="+ addressID);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    public void onClickPhone(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+ phoneID));
        startActivity(intent);
    }

    public void onClickEmail(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { emailID });
        intent.putExtra(Intent.EXTRA_SUBJECT, "subject");
        intent.putExtra(Intent.EXTRA_TEXT, "mail body");
        startActivity(Intent.createChooser(intent, ""));
    }

    public void onClickWebsite(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(websiteID));
        startActivity(intent);
    }
    /***********************************************
                    Extra's
    ***********************************************/
    public boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        return (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting());
    }

}