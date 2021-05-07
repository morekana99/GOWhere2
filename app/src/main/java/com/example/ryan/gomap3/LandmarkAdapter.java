package com.example.ryan.gomap3;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ryan.db.Landmark;
import com.example.ryan.db.Landmarkdata;

import java.util.List;

public class LandmarkAdapter extends RecyclerView.Adapter<LandmarkAdapter.ViewHolder> {
    private Context mContext;
    private List<Landmarkdata> mLandmarkList;
    static  class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView landmarkimage;
        TextView landmarkname;
        public ViewHolder(View view){
            super(view);
            cardView = (CardView) view;
            landmarkimage = (ImageView) view.findViewById(R.id.landmark_image);
            landmarkname = (TextView) view.findViewById(R.id.landmark_name);

        }

    }
    public LandmarkAdapter(List<Landmarkdata> landmarkList){
        mLandmarkList=landmarkList;

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.landmark_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                int position;
                position = holder.getAdapterPosition();
                Landmarkdata landmark = mLandmarkList.get(position);
                Intent intent = new Intent(mContext,Landmark_DetailActivity.class);
                intent.putExtra(Landmark_DetailActivity.LANDMARK_NAME,landmark.getLandmarkName());
                intent.putExtra(Landmark_DetailActivity.LANDMARK_IMAGE_ID,landmark.getImageId());
                intent.putExtra(Landmark_DetailActivity.LANDMARK_CITY_NAME,landmark.getCityName());
                mContext.startActivity(intent);
            }
        });
        return  holder;

    }
    @Override
    public void onBindViewHolder(ViewHolder holder,int position) {
        Landmarkdata landmark = mLandmarkList.get(position);
        holder.landmarkname.setText(landmark.getLandmarkName());
        Glide.with(mContext).load(landmark.getImageId()).into(holder.landmarkimage);
    }
    @Override
    public int getItemCount(){
        return mLandmarkList.size();
    }


}
