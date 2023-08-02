package com.example.newsaggregator;

import android.content.Intent;
import android.net.Uri;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    MainActivity mainActivity;
    List<Map<String, String>> list;

    public RecyclerViewAdapter(MainActivity mainActivity, List<Map<String, String>> list) {
        this.mainActivity = mainActivity;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_article, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        try {
            Date d = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")).parse(list.get(position).get("publishedAt"));
            String s = (new SimpleDateFormat("MMM dd, yyyy hh:mm")).format(d);
            holder.articleDate.setText(s);
        }catch (Exception e){
            e.printStackTrace();
        }

        holder.articleImageView.setOnClickListener(v -> this.mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(list.get(position).get("url")))));
        holder.articleHeadline.setOnClickListener(v -> this.mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(list.get(position).get("url")))));
        holder.articleDescription.setOnClickListener(v -> this.mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(list.get(position).get("url")))));

        holder.articleHeadline.setText(list.get(position).get("title"));
        holder.articleCount.setText((position + 1) + " of " + list.size());

        if(list.get(position).get("author").equals("null")){
            holder.articleAuthor.setText("");
        }else{
            holder.articleAuthor.setText(list.get(position).get("author"));
        }

        if(!list.get(position).get("urlToImage").equals("null")){
            holder.articleImageView.setImageResource(R.drawable.loading);
            new ArticleService(holder.articleImageView, mainActivity).execute(list.get(position).get("urlToImage"));
        }

        if(list.get(position).get("description").equals("null")){
            holder.articleDescription.setText("");
        }else{
            holder.articleDescription.setText(list.get(position).get("description"));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView articleHeadline , articleDate, articleAuthor, articleDescription, articleCount;
        ImageView articleImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            articleHeadline = itemView.findViewById(R.id.articleTitle);
            articleDate = itemView.findViewById(R.id.articleDate);
            articleAuthor = itemView.findViewById(R.id.articleAuthor);
            articleImageView = itemView.findViewById(R.id.articleImage);
            articleDescription = itemView.findViewById(R.id.articleText);
            articleDescription.setMovementMethod(new ScrollingMovementMethod());
            articleCount = itemView.findViewById(R.id.PageCount);
        }
    }
}
