package com.kelvin.ecbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelvin.ecbook.R;

/**
 * Created by Kelvin on 2016/5/14.
 */
public class MyCreditActivity extends BaseActivity {

    private ImageView back;
    private TextView title,tcredit;
    private int credit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mycredit);

        Intent intent = this.getIntent();
        credit = intent.getIntExtra("credit",0);

        back = (ImageView) findViewById(R.id.top_view_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        title = (TextView) findViewById(R.id.top_view_text);
        title.setText("我的积分");

        tcredit = (TextView) findViewById(R.id.credit);
        tcredit.setText(credit+"");
    }
}
