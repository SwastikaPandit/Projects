package com.example.civiladvocacy;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class About extends AppCompatActivity {

    TextView apiTextView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        apiTextView = findViewById(R.id.aboutApiTextLink);
        apiTextView.setPaintFlags(apiTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        apiTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://developers.google.com/civic-information/"));
                startActivity(intent);
            }
        });
    }
}