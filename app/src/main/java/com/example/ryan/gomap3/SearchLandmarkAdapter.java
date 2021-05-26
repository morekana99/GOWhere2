package com.example.ryan.gomap3;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;

public class SearchLandmarkAdapter extends RecyclerView.Adapter<SearchLandmarkAdapter.SearchAdapterViewHolder> {
    private List<String> mDataList;
    private Context mContext;
    public SearchLandmarkAdapter(List<String> dataList) {
        this.mDataList = dataList;
    }

    @NonNull
    @Override
    public SearchAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(mContext == null){
            mContext = viewGroup.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search,viewGroup,false);
        final SearchLandmarkAdapter.SearchAdapterViewHolder holder = new SearchLandmarkAdapter.SearchAdapterViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapterViewHolder searchAdapterViewHolder, int position) {
        String history = mDataList.get(position);
        searchAdapterViewHolder.tv.setText(history);

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    static class SearchAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView tv;

        public SearchAdapterViewHolder(View view) {
            super(view);
            tv = view.findViewById(R.id.history_data);
        }
    }
}
