package com.kelvin.ecbook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kelvin.ecbook.R;
import com.kelvin.ecbook.model.BookCategory;

import java.util.List;

/**
 * Created by Kelvin on 2016/5/3.
 */
public class ListCategoryAdpater extends BaseAdapter {

    private Context context;
    private List<BookCategory> mData;

    public ListCategoryAdpater(Context context,List<BookCategory> mData){

        this.context = context;
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public BookCategory getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final View view = LayoutInflater.from(context).inflate(R.layout.list_category_cell, null);

        TextView category_name = (TextView) view.findViewById(R.id.category_name);
        category_name.setText(mData.get(position).getTitle());

        return view;
    }
}
