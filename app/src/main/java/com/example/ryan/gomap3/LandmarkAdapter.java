package com.example.ryan.gomap3;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.example.ryan.db.Landmark;
import com.example.ryan.db.Landmarkdata;

import java.util.List;

/**
 * @author devonwong
 */
public class LandmarkAdapter extends RecyclerView.Adapter<LandmarkAdapter.ViewHolder> {
    private Context mContext;
    private final List<Landmarkdata> mLandmarkList;
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
    @NonNull
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
                intent.putExtra(Landmark_DetailActivity.LANDMARK_COUNTRY_ID,landmark.getCountyId());
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
        DrawableCrossFadeFactory drawableCrossFadeFactory = new DrawableCrossFadeFactory.Builder(500).setCrossFadeEnabled(true).build();
        Glide.with(mContext).load("http://uitlearn.top/images/p"+landmark.getImageId()+".jpg").apply(new RequestOptions().placeholder(R.drawable.loading).fitCenter()).transition(DrawableTransitionOptions.with(drawableCrossFadeFactory)).into(holder.landmarkimage);
    }
    @Override
    public int getItemCount(){
        return mLandmarkList.size();
    }


}
