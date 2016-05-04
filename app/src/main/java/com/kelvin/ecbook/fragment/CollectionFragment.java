package com.kelvin.ecbook.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelvin.ecbook.R;
import com.kelvin.ecbook.adapter.CollectionAdapter;
import com.kelvin.ecbook.config.StaticData;
import com.kelvin.ecbook.model.Collection;
import com.kelvin.ecbook.view.xlistview.XListViewCart;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by kelvin on 16/4/19.
 */
public class CollectionFragment extends Fragment implements XListViewCart.IXListViewListenerCart{

    private Context mContext;

    private TextView title;
    private ImageView back;

    private XListViewCart xListView;
    public static CollectionAdapter adapter;
    public static List<Collection> collections;

    public static FrameLayout top_view,no_collections;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mainView = inflater.inflate(R.layout.fragment_collection,null);

        mContext = getActivity();

        title = (TextView) mainView.findViewById(R.id.top_view_text);
        title.setText("我的收藏");

        back = (ImageView) mainView.findViewById(R.id.top_view_back);
        back.setVisibility(View.GONE);

        top_view = (FrameLayout) mainView.findViewById(R.id.shop_car_isnot);
        no_collections = (FrameLayout) mainView.findViewById(R.id.collections_null);

        collections = new ArrayList<>();
        adapter = new CollectionAdapter(mContext,collections);
        getMyCollections();

        xListView = (XListViewCart) mainView.findViewById(R.id.collection_list);
        xListView.setPullLoadEnable(false);
        xListView.setRefreshTime();
        xListView.setXListViewListener(this, 1);
        xListView.setAdapter(adapter);

        return mainView;
    }

    /**
     * 获得我的收藏数据
     */
    private void getMyCollections(){

        SharedPreferences sh = mContext.getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        final String userid = sh.getString("objectid", "");

        BmobQuery<Collection> query = new BmobQuery<>();
        query.addWhereEqualTo("collector",userid);
        query.order("-createdAt");
        query.findObjects(mContext, new FindListener<Collection>() {
            @Override
            public void onSuccess(List<Collection> list) {

                StaticData.NUM_COLLECTIONS = list.size();   //记录收藏数目

                //Log.e("collections",list.size()+"");
                if (list.size() > 0){
                    top_view.setVisibility(View.VISIBLE);
                    no_collections.setVisibility(View.GONE);
                }
                else{
                    top_view.setVisibility(View.GONE);
                    no_collections.setVisibility(View.VISIBLE);
                }
                collections = list;
                adapter = new CollectionAdapter(mContext,collections);
                xListView.setAdapter(adapter);
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    @Override
    public void onRefresh(int id) {
        getMyCollections();
    }

    @Override
    public void onLoadMore(int id) {

    }
}
