package com.kelvin.ecbook.adapter;


import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kelvin.ecbook.R;
import com.kelvin.ecbook.activity.ProductListActivity;
import com.kelvin.ecbook.model.Book;
import com.kelvin.ecbook.model.BookCategory;
import com.kelvin.ecbook.utils.ImageLoadOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Kelvin on 2016/5/1.
 */
public class BookCategoryAdapter extends BaseAdapter{

    private Context context;
    private List<BookCategory> mData;

    public BookCategoryAdapter(Context context,List<BookCategory> mData){
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        final View view = LayoutInflater.from(context).inflate(R.layout.index_category_cell, null);

        TextView title = (TextView)view.findViewById(R.id.good_cell_name_one);
        title.setText(mData.get(position).getTitle());

        BmobQuery<Book> query = new BmobQuery<>();
        query.addWhereEqualTo("category", mData.get(position).getTitle());
        query.order("-scanTime");
        query.setLimit(1);
        query.findObjects(context, new FindListener<Book>() {
            @Override
            public void onSuccess(List<Book> list) {

                ImageView photo_one = (ImageView) view.findViewById(R.id.good_cell_photo_one);
                if (!TextUtils.isEmpty(list.get(0).getPhoto01())) {
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.init(ImageLoaderConfiguration.createDefault(context));
                    imageLoader.displayImage(list.get(0).getPhoto01(), photo_one,
                            ImageLoadOptions.getOptions());
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });

        BmobQuery<Book> query02 = new BmobQuery<>();
        query02.addWhereEqualTo("category", mData.get(position).getTitle());
        query02.order("-payTime");
        query02.setLimit(2);
        query02.findObjects(context, new FindListener<Book>() {
            @Override
            public void onSuccess(List<Book> list) {

                TextView title_two = (TextView) view.findViewById(R.id.good_cell_name_two);
                TextView price_two = (TextView) view.findViewById(R.id.good_cell_price_two);
                ImageView photo_two = (ImageView) view.findViewById(R.id.good_cell_photo_two);
                TextView title_three = (TextView) view.findViewById(R.id.good_cell_name_three);
                TextView price_three = (TextView) view.findViewById(R.id.good_cell_price_three);
                ImageView photo_three = (ImageView) view.findViewById(R.id.good_cell_photo_three);

                title_two.setText(list.get(0).getTitle());
                price_two.setText("EC币：" + list.get(0).getCost() + "个");
                if (!TextUtils.isEmpty(list.get(0).getPhoto01())) {
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.init(ImageLoaderConfiguration.createDefault(context));
                    imageLoader.displayImage(list.get(0).getPhoto01(), photo_two,
                            ImageLoadOptions.getOptions());
                }
                title_three.setText(list.get(1).getTitle());
                price_three.setText("EC币：" + list.get(1).getCost() + "个");
                if (!TextUtils.isEmpty(list.get(1).getPhoto01())) {
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.init(ImageLoaderConfiguration.createDefault(context));
                    imageLoader.displayImage(list.get(1).getPhoto01(), photo_three,
                            ImageLoadOptions.getOptions());
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });


        LinearLayout cell_one = (LinearLayout) view.findViewById(R.id.good_cell_one);
        cell_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductListActivity.class);
                intent.putExtra("category",mData.get(position).getTitle());
                context.startActivity(intent);
            }
        });

        return view;
    }

}
