package com.kelvin.ecbook.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kelvin.ecbook.R;
import com.kelvin.ecbook.adapter.BookListAdapter;
import com.kelvin.ecbook.model.Book;
import com.kelvin.ecbook.model.BookCellModel;
import com.kelvin.ecbook.view.xlistview.XListView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Kelvin on 2016/5/2.
 */
public class ProductListActivity extends BaseActivity implements View.OnClickListener,XListView.IXListViewListener,TextWatcher {

    private ImageView back;
    private EditText search_input;
    private XListView xlistView;
    private BookListAdapter adapter;
    private List<BookCellModel> mData;

    private String category = "";
    private int page = 0;

    private FrameLayout null_pager;

    private RelativeLayout filter_popularity,filter_expensive,filter_cheap;
    private int filter = 0;
    private ImageView tab_one,tab_two,tab_three;
    private TextView title_one,title_two,title_three;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_productlist);

        Intent intent = this.getIntent();
        category = intent.getStringExtra("category");

        back = (ImageView) findViewById(R.id.top_view_back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);

        search_input = (EditText) findViewById(R.id.search_input);
        search_input.addTextChangedListener(this);

        filter_popularity = (RelativeLayout) findViewById(R.id.filter_popularity);
        filter_expensive = (RelativeLayout) findViewById(R.id.filter_expensive);
        filter_cheap = (RelativeLayout) findViewById(R.id.filter_cheap);
        filter_popularity.setOnClickListener(this);
        filter_expensive.setOnClickListener(this);
        filter_cheap.setOnClickListener(this);
        tab_one = (ImageView) findViewById(R.id.filter_triangle_tabone);
        tab_two = (ImageView) findViewById(R.id.filter_triangle_tabtwo);
        tab_three = (ImageView) findViewById(R.id.filter_triangle_tabthree);
        title_one = (TextView) findViewById(R.id.filter_title_tabone);
        title_two = (TextView) findViewById(R.id.filter_title_tabtwo);
        title_three = (TextView) findViewById(R.id.filter_title_tabthree);

        mData = new ArrayList<>();
        adapter = new BookListAdapter(this,mData);
        getBooksData();

        xlistView = (XListView) findViewById(R.id.goods_listview);
        xlistView.setPullLoadEnable(true);
        xlistView.setRefreshTime();
        xlistView.setXListViewListener(this, 1);
        xlistView.setAdapter(adapter);

        null_pager = (FrameLayout) findViewById(R.id.null_pager);
    }

    /**
     * 获得电子书数据
     */
    private void getBooksData(){

        BmobQuery<Book> query = new BmobQuery<>();
        query.addWhereEqualTo("category", category);
        if (filter == 0){
            query.order("-payTime");
        }
        else if (filter == 1){
            query.order("-cost");
        }
        else{
            query.order("cost");
        }
        query.setSkip(20 * page);
        query.setLimit(20);
        query.findObjects(this, new FindListener<Book>() {
            @Override
            public void onSuccess(List<Book> list) {

                if (list.size() < 20){
                    xlistView.setPullLoadEnable(false);
                }
                else{
                    xlistView.setPullLoadEnable(true);
                }

                if (page == 0 && list.size() == 0){
                    null_pager.setVisibility(View.VISIBLE);
                }else{
                    null_pager.setVisibility(View.GONE);
                    page++;
                }

                if (!TextUtils.isEmpty(search_input.getText().toString())) {

                    List<Book> newData = new ArrayList<Book>();

                    for (int i=0; i<list.size(); i++){

                        if (list.get(i).getTitle().contains(search_input.getText().toString())){
                            newData.add(list.get(i));
                        }
                    }

                    list = newData;
                }

                for (int i=0; i<list.size(); i+=2){

                    BookCellModel model = new BookCellModel();
                    model.setBook_one(list.get(i));
                    if (i + 1 < list.size()) {
                        model.setBook_two(list.get(i + 1));
                    }
                    mData.add(model);

                }

                adapter = new BookListAdapter(ProductListActivity.this,mData);
                xlistView.setAdapter(adapter);
            }

            @Override
            public void onError(int i, String s) {

            }
        });


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.top_view_back:
                finish();
                break;
            case R.id.filter_popularity:    //选择人气排行
                filter = 0;
                page = 0;
                mData = new ArrayList<>();
                tab_one.setVisibility(View.VISIBLE);
                tab_two.setVisibility(View.INVISIBLE);
                tab_three.setVisibility(View.INVISIBLE);
                title_one.setTextColor(Color.parseColor("#ffffff"));
                title_two.setTextColor(Color.parseColor("#666666"));
                title_three.setTextColor(Color.parseColor("#666666"));
                getBooksData();
                break;
            case R.id.filter_expensive:     //选择最贵
                filter = 1;
                page = 0;
                mData = new ArrayList<>();
                tab_one.setVisibility(View.INVISIBLE);
                tab_two.setVisibility(View.VISIBLE);
                tab_three.setVisibility(View.INVISIBLE);
                title_one.setTextColor(Color.parseColor("#666666"));
                title_two.setTextColor(Color.parseColor("#ffffff"));
                title_three.setTextColor(Color.parseColor("#666666"));
                getBooksData();
                break;
            case R.id.filter_cheap:       //选择最便宜
                filter = 2;
                page = 0;
                mData = new ArrayList<>();
                tab_one.setVisibility(View.INVISIBLE);
                tab_two.setVisibility(View.INVISIBLE);
                tab_three.setVisibility(View.VISIBLE);
                title_one.setTextColor(Color.parseColor("#666666"));
                title_two.setTextColor(Color.parseColor("#666666"));
                title_three.setTextColor(Color.parseColor("#ffffff"));
                getBooksData();
                break;
        }
    }


    @Override
    public void onRefresh(int id) {

        page = 0;
        mData = new ArrayList<>();
        getBooksData();
    }

    @Override
    public void onLoadMore(int id) {
        getBooksData();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        page = 0;
        mData = new ArrayList<>();
        getBooksData();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
