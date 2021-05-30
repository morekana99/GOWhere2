package com.example.ryan.adapter;

import android.content.Context;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.example.ryan.entity.LandmarkList;
import com.example.ryan.gomap3.R;

import java.util.ArrayList;
import java.util.List;

public final class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ItemViewHolder> {
    private static final int VIEW_TYPE_HEADER_VIEW      = 100;
    private static final int VIEW_TYPE_CONTENT_VIEW     = 200;
    private static final int VIEW_TYPE_FOOTER_VIEW      = 300;

    private Context mContext;
    private List<View> mHeaderViewList = new ArrayList<>();
    private List<View> mFooterViewList = new ArrayList<>();
    private final List<LandmarkList> mLandmarkList;
    private IOnItemClickListener mItemClickListener;

    public RecyclerViewAdapter(Context context, List<LandmarkList> mLandmarkList) {
        this.mContext = context;
        this.mLandmarkList = mLandmarkList;
    }


    @Override
    public int getItemViewType(int position) {
        if (position < getHeaderViewSize()) {
            return VIEW_TYPE_HEADER_VIEW + position;
        } else if (position >= getHeaderViewSize() + mLandmarkList.size()) {
            return VIEW_TYPE_FOOTER_VIEW + position - getHeaderViewSize() - mLandmarkList.size();
        } else {
            return VIEW_TYPE_CONTENT_VIEW;
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == VIEW_TYPE_CONTENT_VIEW) {
            return new ContentViewHolder(LayoutInflater.from(mContext).inflate(R.layout.landmark_item, viewGroup,false));
        } else if (viewType < VIEW_TYPE_CONTENT_VIEW) {
            return new HeaderFooterView(mHeaderViewList.get(viewType - VIEW_TYPE_HEADER_VIEW));
        } else {
            return new HeaderFooterView(mFooterViewList.get(viewType - VIEW_TYPE_FOOTER_VIEW));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder viewHolder, int position) {
        if (position >= getHeaderViewSize()) {
            viewHolder.bindViewHolder(position - getHeaderViewSize());
        }
    }

    @Override
    public int getItemCount() {
        return getHeaderViewSize() + mLandmarkList.size() + getFooterViewSize();
    }

    public void addHeaderView(View headerView) {
        this.mHeaderViewList.add(headerView);
    }

    public void addFooterView(View footerView) {
        this.mFooterViewList.add(footerView);
    }

    private int getHeaderViewSize() {
        return mHeaderViewList.size();
    }

    private int getFooterViewSize() {
        return mFooterViewList.size();
    }


    public void setItemClickListener(IOnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        protected void bindViewHolder(int position) {
        }

    }

    private class ContentViewHolder extends ItemViewHolder {
        CardView cardView;
        ImageView landmarkimage;
        TextView landmarkname;

        public ContentViewHolder(@NonNull View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.card_view_landmarks);
            landmarkimage = (ImageView) view.findViewById(R.id.landmark_image);
            landmarkname = (TextView) view.findViewById(R.id.landmark_name);
        }

        @Override
        protected void bindViewHolder(final int position) {
            LandmarkList landmark = mLandmarkList.get(position);
            landmarkname.setText(landmark.getLandmarkName());
            DrawableCrossFadeFactory drawableCrossFadeFactory = new DrawableCrossFadeFactory.Builder(500).setCrossFadeEnabled(true).build();
            Glide.with(mContext).load("http://uitlearn.top/images/p"+landmark.getImageId()+".jpg")
                    .apply(new RequestOptions()
                    .placeholder(R.drawable.loading)
                     .fitCenter())
                    .transition(DrawableTransitionOptions
                    .with(drawableCrossFadeFactory))
                    .into(landmarkimage);
            cardView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v){
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(position);
                    }
                }
            });
        }

    }

    private class HeaderFooterView extends ItemViewHolder {

        public HeaderFooterView(@NonNull View itemView) {
            super(itemView);
        }

    }

    public interface IOnItemClickListener {
        void onItemClick(int position);
    }


}
