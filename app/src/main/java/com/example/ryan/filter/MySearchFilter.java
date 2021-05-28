package com.example.ryan.filter;

import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

public class MySearchFilter extends Filter {
    private final List<String> mTextList ;
    private final ArrayAdapter<String> mAdapter;

    public MySearchFilter(List<String> mTextList, ArrayAdapter<String> mAdapter) {
        this.mTextList = mTextList;
        this.mAdapter = mAdapter;

    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        // FilterResults用来保存过滤结果，包含两个属性values和count
        FilterResults results = new FilterResults();

        List<String> resultList = new ArrayList<>();
        for (String text : mTextList) {
            if (constraint != null && constraint.length() > 0 && text.contains(constraint)) {
                resultList.add(text);
            }
        }

        results.values = resultList;
        results.count = resultList.size();

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        mAdapter.clear();
        if (results.count > 0) {
            mAdapter.addAll((List<String>)results.values);
        }
    }

}
