package com.example.civiladvocacy;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class OfficialViewAdapter extends RecyclerView.Adapter<OfficialViewAdapter.ViewHolder> implements View.OnClickListener {
    MainActivity mainActivity;
    List<Object> officalsList;

    public OfficialViewAdapter(MainActivity mainActivity, List<Object> officalsList){
        this.mainActivity = mainActivity;
        this.officalsList = officalsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mainActivity).inflate(R.layout.activity_official_view,parent,false);
        itemView.setOnClickListener(this);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OfficialViewAdapter.ViewHolder holder, int position) {
        Map<String,Object> data = (Map<String, Object>) officalsList.get(position);
        holder.officialViewPost.setText((CharSequence) data.get("postName"));
        holder.officialViewName.setText(data.get("name")+" ("+data.get("party")+")");
        String image = (String) data.get("photoUrl");
        if(!image.isEmpty()){
            String imageUrl = image.replace("http:","https:");
            Picasso.get().load(imageUrl).error(R.drawable.brokenimage).into(holder.officialViewPhoto);
        }else{
            holder.officialViewPhoto.setImageResource(R.drawable.missing);
        }
    }

    @Override
    public int getItemCount() {
        return officalsList.size();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(mainActivity,IndividualOfficial.class);
        intent.putExtra("Details", (Serializable) officalsList.get(mainActivity.recyclerView.getChildAdapterPosition(v)));
        mainActivity.startActivity(intent);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView officialViewPhoto;
        TextView officialViewPost, officialViewName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            officialViewPhoto = itemView.findViewById(R.id.officialViewPhoto);
            officialViewPost = itemView.findViewById(R.id.officialViewPost);
            officialViewName = itemView.findViewById(R.id.officialViewName);
        }
    }
}
