package com.kelvin.ecbook.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kelvin.ecbook.R;

/**
 * Created by Kelvin on 2016/5/14.
 */
public class MyInfoActivity extends BaseActivity implements OnClickListener{

    private ImageView back;
    private TextView title;

    private SharedPreferences shared;

    private TextView username,email,credit;

    private LinearLayout edit_username,edit_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_myinfo);

        back = (ImageView) findViewById(R.id.top_view_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        title = (TextView) findViewById(R.id.top_view_text);
        title.setText("个人信息");

        username = (TextView) findViewById(R.id.username);

        email = (TextView) findViewById(R.id.email);

        credit = (TextView) findViewById(R.id.credit);

        edit_username = (LinearLayout) findViewById(R.id.profile_username);
        edit_password = (LinearLayout) findViewById(R.id.profile_password);

        edit_username.setOnClickListener(this);
        edit_password.setOnClickListener(this);

        setUserDetail();

    }

    private void setUserDetail(){

        shared = getSharedPreferences("userInfo", 0);
        username.setText(shared.getString("username",""));
        email.setText(shared.getString("email",""));
        credit.setText(shared.getInt("credit",0)+"");
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.profile_username: {
                Intent intent = new Intent(MyInfoActivity.this, UpdateInfoActivity.class);
                intent.putExtra("type","username");
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                break;
            }
            case R.id.profile_password:{
                Intent intent = new Intent(MyInfoActivity.this, UpdateInfoActivity.class);
                intent.putExtra("type","password");
                startActivityForResult(intent,0);
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        setUserDetail();
    }
}
