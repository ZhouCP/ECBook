package com.kelvin.ecbook.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.kelvin.ecbook.R;
import com.kelvin.ecbook.activity.BookDetailActivity;
import com.kelvin.ecbook.activity.ProductListActivity;
import com.kelvin.ecbook.adapter.ListBookAdapter;
import com.kelvin.ecbook.adapter.ListCategoryAdpater;
import com.kelvin.ecbook.model.Book;
import com.kelvin.ecbook.model.BookCategory;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by kelvin on 16/4/19.
 */
public class CategoryFragment extends Fragment implements OnItemClickListener,TextWatcher,AbsListView.OnScrollListener{

    private Context mContext;

    private ImageView back;
    private EditText search_input;

    private ListView list_category;
    private ListCategoryAdpater adpater;
    private ListBookAdapter bookAdapter;
    private List<BookCategory> categories;
    private List<Book> books;

    private int firstVisibleItem;
    private int bookPage = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mainView = inflater.inflate(R.layout.fragment_category,null);

        mContext = getActivity();

        back = (ImageView) mainView.findViewById(R.id.top_view_back);
        back.setVisibility(View.GONE);

        search_input = (EditText) mainView.findViewById(R.id.search_input);
        search_input.addTextChangedListener(this);

        list_category = (ListView) mainView.findViewById(R.id.list_category);

        categories = new ArrayList<>();
        adpater = new ListCategoryAdpater(mContext,categories);

        books = new ArrayList<>();
        bookAdapter = new ListBookAdapter(mContext,books);

        getCategoriesBySum();
        list_category.setAdapter(adpater);
        list_category.setOnItemClickListener(this);

        return mainView;
    }

    /**
     * 按受总数获得分类数据
     */
    private void getCategoriesBySum(){

        BmobQuery<BookCategory> query = new BmobQuery<>();
        query.order("-sum");
        query.setLimit(10);
        query.findObjects(mContext, new FindListener<BookCategory>() {
            @Override
            public void onSuccess(List<BookCategory> list) {
                categories = list;
                adpater = new ListCategoryAdpater(mContext, categories);
                list_category.setAdapter(adpater);
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    /**
     * 按照搜索词列出电子书
     */
    private void getEcBookBySearch(final String filter){

        BmobQuery<Book> query = new BmobQuery<>();
        query.order("-scanTime");
        query.setSkip(bookPage * 20);
        query.setLimit(20);
        query.findObjects(mContext, new FindListener<Book>() {
            @Override
            public void onSuccess(List<Book> list) {

                bookPage++;

                if (!TextUtils.isEmpty(filter)){

                    for (int i=0; i<list.size(); i++){

                        if (list.get(i).getTitle().contains(filter)){
                            books.add(list.get(i));
                        }
                    }
                }
                else{
                    books = list;
                }

                bookAdapter = new ListBookAdapter(mContext, books);
                list_category.setAdapter(bookAdapter);

            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (TextUtils.isEmpty(search_input.getText().toString())) {     //分类选择
            Intent intent = new Intent(mContext, ProductListActivity.class);
            intent.putExtra("category", categories.get(position).getTitle());
            mContext.startActivity(intent);
        }
        else{      //单项
            Intent intent = new Intent(mContext, BookDetailActivity.class);
            intent.putExtra("bookId", books.get(position).getObjectId());
            mContext.startActivity(intent);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (!TextUtils.isEmpty(search_input.getText().toString())) {
            bookPage = 0;
            books = new ArrayList<>();
            getEcBookBySearch(search_input.getText().toString());
        }
        else{
            getCategoriesBySum();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //当滑动到底部时
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                && firstVisibleItem != 0) {

            getEcBookBySearch(search_input.getText().toString());
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem = firstVisibleItem;
    }
}
