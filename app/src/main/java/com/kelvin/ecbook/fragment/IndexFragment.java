package com.kelvin.ecbook.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kelvin.ecbook.R;
import com.kelvin.ecbook.activity.DeclareBookActivity;
import com.kelvin.ecbook.activity.WebViewActivity;
import com.kelvin.ecbook.adapter.BookCategoryAdapter;
import com.kelvin.ecbook.adapter.ViewPagerAdapter;
import com.kelvin.ecbook.model.Banner;
import com.kelvin.ecbook.model.BookCategory;
import com.kelvin.ecbook.utils.ImageLoadOptions;
import com.kelvin.ecbook.view.xlistview.XListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by kelvin on 16/4/19.
 */
public class IndexFragment extends Fragment implements ViewPager.OnPageChangeListener,OnClickListener,
        XListView.IXListViewListener {

    private Context mContext;

    private ImageView back;
    private LinearLayout right;
    private TextView title;

    private ViewPager banner_viewpager;
    private List<Banner> banners;
    private List<View> views;
    private ViewPagerAdapter viewPagerAdapter;
    private View[] points;
    private LinearLayout layout_indicator;
    private int currentPoint;       //当前页下标

    private XListView index_list;
    private View indicatorView;

    private List<BookCategory> categories;
    private BookCategoryAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mainView = inflater.inflate(R.layout.fragment_index,null);

        mContext = getActivity();
        banners = new ArrayList<>();

        back = (ImageView) mainView.findViewById(R.id.top_view_back);
        back.setVisibility(View.GONE);

        title = (TextView) mainView.findViewById(R.id.top_view_text);
        title.setText("EC电子书商城");

        right = (LinearLayout) mainView.findViewById(R.id.top_right_button);
        right.setVisibility(View.VISIBLE);
        right.setOnClickListener(this);


        indicatorView = LayoutInflater.from(getActivity()).inflate(R.layout.index_banner, null);
        banner_viewpager = (ViewPager) indicatorView.findViewById(R.id.banner_viewpager);
        layout_indicator = (LinearLayout) indicatorView.findViewById(R.id.layout_indicator);
        views = new ArrayList<View>();
        viewPagerAdapter = new ViewPagerAdapter(views);
        banner_viewpager.setOnClickListener(this);
        getBannersData();

        categories = new ArrayList<>();
        adapter = new BookCategoryAdapter(mContext,categories);
        getBookCategories();

        index_list = (XListView)mainView.findViewById(R.id.index_list);
        index_list.addHeaderView(indicatorView);
        index_list.setPullLoadEnable(false);
        index_list.setRefreshTime();
        index_list.setXListViewListener(this, 1);
        index_list.setAdapter(adapter);


        return mainView;
    }


    /**
     * 获得Banner内容
     */
    private void getBannersData(){

        BmobQuery<Banner> query = new BmobQuery<>();
        query.setLimit(3);
        query.findObjects(mContext, new FindListener<Banner>() {
            @Override
            public void onSuccess(List<Banner> list) {

                banners = list;
                addViewsByBanners();
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    /**
     * 根据Banners添加View
     */
    int i;
    private void addViewsByBanners(){
        ViewGroup.LayoutParams layoutParams = banner_viewpager.getLayoutParams();
        for (i=0; i<banners.size(); i++){
            ImageView imageView=new ImageView(getActivity());

            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
            imageLoader.displayImage(banners.get(i).getPicture(), imageView,
                    ImageLoadOptions.getOptions());

            imageView.setLayoutParams(layoutParams);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, WebViewActivity.class);
                    intent.putExtra("title",banners.get(currentPoint).getTitle());
                    intent.putExtra("url",banners.get(currentPoint).getUrl());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                }
            });
            views.add(imageView);
        }
        points = new View[banners.size()];
        setCurrentIndicator(0);
        banner_viewpager.setAdapter(viewPagerAdapter);
        banner_viewpager.setOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setCurrentIndicator(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void setCurrentIndicator(int position) {
        for (int i=0;i<layout_indicator.getChildCount();i++){
            points[i] = layout_indicator.getChildAt(i);
            points[i].setBackgroundResource(R.drawable.indicator_unselected);
        }
        currentPoint = position;
        points[currentPoint].setBackgroundResource(R.drawable.indicator_selected);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.top_right_button: {

                Intent intent = new Intent(mContext, DeclareBookActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                break;
            }
        }
    }

    @Override
    public void onRefresh(int id) {
        getBannersData();
        getBookCategories();
    }

    @Override
    public void onLoadMore(int id) {

    }


    /**
     * 获取书籍类别信息
     */
    public void getBookCategories(){

        BmobQuery<BookCategory> query = new BmobQuery<>();
        query.order("-sum");
        query.setLimit(3);
        query.findObjects(mContext, new FindListener<BookCategory>() {
            @Override
            public void onSuccess(List<BookCategory> list) {
                categories = list;
                //Log.e("categories", categories.size() + "");
                //adapter.notifyDataSetChanged();
                adapter = new BookCategoryAdapter(mContext,categories);
                index_list.setAdapter(adapter);
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }
}
