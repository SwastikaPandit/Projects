package com.example.newsaggregator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    private Menu menu;
    private String[] sourcesList;
    private static List<Map<String, String>> dataList;
    private Map<Integer,String> newsList = new HashMap<>();
    private static List<Map<String, String>> articleDataList;

    private RecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private ArticleService service;
    private ListView drawerListView;

    private DrawerLayout drawerLayout;
    private ArrayAdapter<String> arrayAdapter;
    private ConstraintLayout constraintLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private static RequestQueue queueSource;
    private static RequestQueue queueArticle;

    private static String articleUri = "https://newsapi.org/v2/top-headlines";
    private static String sourceUri = "https://newsapi.org/v2/top-headlines/sources";
    private static String key = "99c1afc3f8d4483f98e7036b311394a7";

    private static List<Map<String, String>> List = new ArrayList<>();
    private static List<Map<String, String>> ArticleList = new ArrayList<>();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        drawerLayout = findViewById(R.id.drawerLayout);
        drawerListView = findViewById(R.id.drawer);
        constraintLayout = findViewById(R.id.constraintLayout);

        newsList.put(1,"all");
        fetchSourceData();
        service = new ArticleService();
        service.setColorList(this);

        drawerListView.setOnItemClickListener(
                (parent, view, position, id) -> {
                    setTitle(articleDataList.get(position).get("name"));
                    fetchSourceArticleData(articleDataList.get(position).get("id"));
                    findViewById(R.id.constraintLayout).setBackgroundColor(Color.parseColor("#ffffff"));
                    drawerLayout.closeDrawer(drawerListView);
                }
        );

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menus) {
        this.menu = menus;
        this.menu.add(1, 0, 0, "All");
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        updateList(item.getTitle().toString(),item.getGroupId());
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void setAdapter(List<Map<String, String>> list) {
        adapter = new RecyclerViewAdapter(this,list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void fetchSourceArticleData(String id) {
        queueArticle = Volley.newRequestQueue(this);
        Uri.Builder builder = Uri.parse(articleUri).buildUpon();
        builder.appendQueryParameter("sources", id);
        builder.appendQueryParameter("apiKey", key);
        String url = builder.build().toString();
        Response.Listener<JSONObject> jsonObjectListener = response -> parseArticleJSON(response.toString());
        Response.ErrorListener errorListener = error -> {
            setAdapter(null);
        };
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,jsonObjectListener,errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", "News-App");
                return headers;
            }
        };
        queueArticle.add(jsonObjectRequest);
    }

    private void parseJSON(String toString) {
        try {
            JSONArray jSources = new JSONObject(toString).getJSONArray("sources");
            for(int i = 0; i < jSources.length(); i++) {
                Map<String, String> map = new HashMap<>();
                JSONObject sourceNews = (JSONObject) jSources.get(i);
                map.put("id", sourceNews.getString("id"));
                map.put("name", sourceNews.getString("name"));
                map.put("category", sourceNews.getString("category"));
                List.add(map);
            }
            updateDetails(List);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseArticleJSON(String toString) {
        ArticleList.clear();
        try {
            JSONArray newsSources = new JSONObject(toString).getJSONArray("articles");
            for(int i = 0; i < newsSources.length(); i++) {
                Map<String, String> map = new HashMap<>();
                JSONObject article = (JSONObject) newsSources.get(i);
                map.put("title", article.getString("title"));
                map.put("author", article.getString("author"));
                map.put("description", article.getString("description"));
                map.put("publishedAt", article.getString("publishedAt"));
                map.put("urlToImage", article.getString("urlToImage"));
                map.put("url", article.getString("url"));
                ArticleList.add(map);
            }
            setAdapter(ArticleList);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void fetchSourceData() {
        queueSource = Volley.newRequestQueue(this);
        Uri.Builder builder = Uri.parse(sourceUri).buildUpon();
        builder.appendQueryParameter("apiKey", key);
        String url = builder.build().toString();
        Response.Listener<JSONObject> jsonObjectListener = response -> parseJSON(response.toString());
        Response.ErrorListener errorListener = error -> {
            updateDetails(null);
        };
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,jsonObjectListener,errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", "News-App");
                return headers;
            }
        };
        queueSource.add(jsonObjectRequest);
    }

    public void updateDetails(List<Map<String, String>> list) {
        dataList = new ArrayList<>(list);
        articleDataList = new ArrayList<>(list);
        sourcesList = new String[dataList.size()];
        this.setTitle("News Gateway " + "(" +sourcesList.length+")");
        for(int i = 0; i < sourcesList.length; i++) {
            sourcesList[i] = list.get(i).get("name");
        }
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_drawer_item, sourcesList){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView)v.findViewById(android.R.id.text1))
                        .setTextColor(service.getColorList(list.get(position).get("category")));
                return v;
            }
        };
        drawerListView.setAdapter(arrayAdapter);
        updateMenuList(list);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateList(String s, int groupId) {
        if(groupId == 0)
            return;
        newsList.put(groupId,s);
        List<Map<String, String>> filtered = dataList.stream()
                .filter(source -> (source.get("category").toString().equals(newsList.get(1)) || newsList.get(1).equals("All"))).collect(Collectors.toList());

        String[] newsSources = new String[filtered.size()];
        articleDataList = new ArrayList<>(filtered);
        for(int i = 0; i < filtered.size(); i++){
            newsSources[i] = filtered.get(i).get("name");
        }
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_drawer_item, newsSources)  {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView)v.findViewById(android.R.id.text1))
                        .setTextColor(service.getColorList(filtered.get(position).get("category")));
                return v;
            }
        };
        drawerListView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        setTitle("News Gateway " + "(" +newsSources.length+")");
        recyclerView.setAdapter(null);
        constraintLayout.setBackgroundResource(R.drawable.background);
    }

    private void updateMenuList(List<Map<String, String>> list){
        Set<String> set = new HashSet<>();
        for(Map<String, String> map : list){
            String s = map.get("category");
            if(set.add(s)) {
                SpannableString spannableString = new SpannableString(s);
                spannableString.setSpan(new ForegroundColorSpan(service.getColorList(s)), 0, spannableString.length(), 0);
                menu.add(1, 0, 0, spannableString);
            }
        }
    }

}