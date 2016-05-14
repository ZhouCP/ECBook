package com.kelvin.ecbook.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelvin.ecbook.R;
import com.kelvin.ecbook.adapter.CollectionAdapter;
import com.kelvin.ecbook.config.CollectionType;
import com.kelvin.ecbook.model.Collection;
import com.kelvin.ecbook.model.Download;
import com.kelvin.ecbook.view.xlistview.XListViewCart;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Kelvin on 2016/5/12.
 */
public class MyDownloadActivity extends BaseActivity implements XListViewCart.IXListViewListenerCart {

    private ImageView back;
    private TextView title;

    private XListViewCart xListView;
    public CollectionAdapter adapter;
    public List<Collection> collections;

    private FrameLayout top_view,no_collections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mydownload);

        back = (ImageView) findViewById(R.id.top_view_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        title = (TextView) findViewById(R.id.top_view_text);
        title.setText("我下载的");

        top_view = (FrameLayout) findViewById(R.id.shop_car_isnot);
        no_collections = (FrameLayout) this.findViewById(R.id.collections_null);

        collections = new ArrayList<>();
        adapter = new CollectionAdapter(this,collections, CollectionType.MYDOWNLOAD);
        getMyDownloads();

        xListView = (XListViewCart) findViewById(R.id.mydownload_list);
        xListView.setPullLoadEnable(false);
        xListView.setRefreshTime();
        xListView.setXListViewListener(this, 1);
        xListView.setAdapter(adapter);
    }

    private void getMyDownloads(){

        SharedPreferences sh = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        final String userid = sh.getString("objectid", "");

        BmobQuery<Download> query = new BmobQuery<>();
        query.addWhereEqualTo("downloader", userid);
        query.order("-createdAt");
        query.findObjects(this, new FindListener<Download>() {
            @Override
            public void onSuccess(List<Download> list) {

                collections = new ArrayList<>();

                for (int i = 0; i < list.size(); i++) {
                    Collection collection = new Collection();
                    collection.setCollector(userid);
                    collection.setBook(list.get(i).getBook());
                    collections.add(collection);
                }

                //Log.e("collections",list.size()+"");
                if (collections.size() > 0) {
                    top_view.setVisibility(View.VISIBLE);
                    no_collections.setVisibility(View.GONE);
                } else {
                    top_view.setVisibility(View.GONE);
                    no_collections.setVisibility(View.VISIBLE);
                }
                adapter = new CollectionAdapter(MyDownloadActivity.this, collections,CollectionType.MYDOWNLOAD);
                xListView.setAdapter(adapter);
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    @Override
    public void onRefresh(int id) {
        getMyDownloads();
    }

    @Override
    public void onLoadMore(int id) {

    }
}
