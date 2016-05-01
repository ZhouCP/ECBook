package com.kelvin.ecbook.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelvin.ecbook.R;

/**
 * Created by kelvin on 16/4/19.
 */
public class ShoppingCartFragment extends Fragment {

    private TextView title;
    private ImageView back;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mainView = inflater.inflate(R.layout.fragment_shoppingcart,null);

        title = (TextView) mainView.findViewById(R.id.top_view_text);
        title.setText("我的收藏");

        back = (ImageView) mainView.findViewById(R.id.top_view_back);
        back.setVisibility(View.GONE);

        return mainView;
    }
}
