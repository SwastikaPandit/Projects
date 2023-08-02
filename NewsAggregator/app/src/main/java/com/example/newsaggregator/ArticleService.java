package com.example.newsaggregator;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;

public class ArticleService extends AsyncTask<String, Void, Bitmap> {
    Map<String, Integer> color;
    ImageView imageView;
    MainActivity mainActivity;

    public ArticleService() {
    }

    public ArticleService(ImageView imageView, MainActivity mainActivity) {
        this.imageView = imageView;
        this.mainActivity = mainActivity;
    }

    public int getColorList(String data) {
        return color.get(data);
    }

    public void setColorList(MainActivity mainActivity) {
        color = new HashMap<>();
        Resources r = mainActivity.getResources();
        color.put("general", r.getColor(R.color.general));
        color.put("technology", r.getColor(R.color.technology));
        color.put("science", r.getColor(R.color.science));
        color.put("sports", r.getColor(R.color.sports));
        color.put("business", r.getColor(R.color.business));
        color.put("entertainment", r.getColor(R.color.entertainment));
        color.put("health", r.getColor(R.color.health));
    }


    @Override
    protected Bitmap doInBackground(String... strings) {
        Bitmap Icon = null;
        try {
            Icon = BitmapFactory.decodeStream(new java.net.URL(strings[0]).openStream());
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return Icon;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap == null) {
            imageView.setImageResource(mainActivity.getResources().getIdentifier("brokenimage", "drawable", mainActivity.getPackageName()));
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }


}
