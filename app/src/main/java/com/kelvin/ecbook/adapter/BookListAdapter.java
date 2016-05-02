package com.kelvin.ecbook.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kelvin.ecbook.R;
import com.kelvin.ecbook.model.BookCellModel;
import com.kelvin.ecbook.utils.ImageLoadOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

/**
 * Created by Kelvin on 2016/5/2.
 */
public class BookListAdapter extends BaseAdapter {

    private Context mContext;
    private List<BookCellModel> mData;

    public BookListAdapter(Context mContext,List<BookCellModel> mData){

        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public BookCellModel getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final View view = LayoutInflater.from(mContext).inflate(R.layout.product_list_cell, null);


        ImageView photo_one = (ImageView) view.findViewById(R.id.gooditem_photo_one);
        String photo01 = mData.get(position).getBook_one().getPhoto01();
        if (!TextUtils.isEmpty(photo01)) {
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
            imageLoader.displayImage(photo01, photo_one,
                    ImageLoadOptions.getOptions());
        }

        TextView price_one = (TextView) view.findViewById(R.id.shop_price_one);
        price_one.setText("EC币：" + mData.get(position).getBook_one().getCost());

        TextView desc_one = (TextView) view.findViewById(R.id.good_desc_one);
        desc_one.setText(mData.get(position).getBook_one().getTitle());

        if (mData.get(position).getBook_two() != null) {

            ImageView photo_two = (ImageView) view.findViewById(R.id.gooditem_photo_two);
            String photo02 = mData.get(position).getBook_two().getPhoto01();
            if (!TextUtils.isEmpty(photo02)) {
                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
                imageLoader.displayImage(photo02, photo_two,
                        ImageLoadOptions.getOptions());
            }

            TextView price_two = (TextView) view.findViewById(R.id.shop_price_two);
            price_two.setText("EC币：" + mData.get(position).getBook_two().getCost());

            TextView desc_two = (TextView) view.findViewById(R.id.good_desc_two);
            desc_two.setText(mData.get(position).getBook_two().getTitle());
        }
        else{
            LinearLayout cell_two = (LinearLayout) view.findViewById(R.id.good_item_two);
            cell_two.setVisibility(View.INVISIBLE);
        }

        return view;
    }
}
