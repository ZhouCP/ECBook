package com.kelvin.ecbook.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kelvin.ecbook.R;
import com.kelvin.ecbook.model.EcBookUser;
import com.kelvin.ecbook.view.dialog.MyProgressDialog;
import com.kelvin.ecbook.view.toast.ToastView;

import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Kelvin on 2016/5/14.
 */
public class UpdateInfoActivity extends BaseActivity implements OnClickListener{

    private TextView title;
    private ImageView back;
    private LinearLayout right;
    private TextView right_text;

    private String type;

    private LinearLayout username,old_pwd,new_pwd,new_pwd02;

    private EditText input_username,input_old_pwd,input_new_pwd,input_new_pwd02;

    private SharedPreferences shared;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_updateinfo);

        Intent intent = this.getIntent();
        type = intent.getStringExtra("type");

        shared = getSharedPreferences("userInfo", 0);

        title = (TextView) findViewById(R.id.top_view_text);

        back = (ImageView) findViewById(R.id.top_view_back);
        back.setOnClickListener(this);

        right_text = (TextView) findViewById(R.id.top_right_text);
        right_text.setText("修改");

        right = (LinearLayout) findViewById(R.id.top_right_button);
        right.setVisibility(View.VISIBLE);
        right.setOnClickListener(this);

        username = (LinearLayout) findViewById(R.id.profile_username);
        old_pwd = (LinearLayout) findViewById(R.id.profile_old_pwd);
        new_pwd = (LinearLayout) findViewById(R.id.profile_new_pwd);
        new_pwd02 = (LinearLayout) findViewById(R.id.profile_new_pwd02);

        input_username = (EditText) findViewById(R.id.input_username);
        input_old_pwd = (EditText) findViewById(R.id.input_old_pwd);
        input_new_pwd = (EditText) findViewById(R.id.input_new_pwd);
        input_new_pwd02 = (EditText) findViewById(R.id.input_new_pwd02);

        if (type.equals("username")){
            title.setText("修改用户名");
        }
        else if (type.equals("password")){
            title.setText("修改密码");
            username.setVisibility(View.GONE);
            old_pwd.setVisibility(View.VISIBLE);
            new_pwd.setVisibility(View.VISIBLE);
            new_pwd02.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.top_view_back:
                finish();
                break;
            case R.id.top_right_button:
                if (type.equals("username")){
                    updateUsername();
                }
                else if (type.equals("password")) {
                    updatePassword();
                }
                break;
        }
    }


    /**
     * 更新用户名
     */
    private void updateUsername(){

        if (!TextUtils.isEmpty(input_username.getText().toString())){

            new AlertDialog.Builder(UpdateInfoActivity.this).setTitle("提示")       //设置标题
                    .setMessage("您是否确定要修改您的用户名吗")        //设置显示的内容
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            final MyProgressDialog mydialog = new MyProgressDialog(UpdateInfoActivity.this,"努力修改中...");
                            mydialog.show();

                            String objectId = shared.getString("objectid", "");
                            int credit = shared.getInt("credit",0);
                            EcBookUser user = new EcBookUser();
                            user.setUsername(input_username.getText().toString());
                            user.setCredit(credit);
                            user.update(UpdateInfoActivity.this, objectId, new UpdateListener() {

                                @Override
                                public void onSuccess() {

                                    editor = shared.edit();
                                    editor.putString("username",input_username.getText().toString());
                                    editor.commit();

                                    if (mydialog.isShowing()) mydialog.dismiss();
                                    ToastView toast = new ToastView(UpdateInfoActivity.this, "用户名修改成功" );
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    finish();
                                }

                                @Override
                                public void onFailure(int i, String s) {

                                    if (mydialog.isShowing()) mydialog.dismiss();
                                    ToastView toast = new ToastView(UpdateInfoActivity.this, "用户名修改失败：" + s);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                            });

                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();

        }
        else{
            ToastView toast = new ToastView(this, "用户名不能为空" );
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }


    /**
     * 更新密码
     */
    private void updatePassword(){

        if (!TextUtils.isEmpty(input_old_pwd.getText().toString()) &&
                !TextUtils.isEmpty(input_new_pwd.getText().toString()) &&
                !TextUtils.isEmpty(input_new_pwd02.getText().toString())) {

            String password = shared.getString("password", "");
            if (!password.equals(input_old_pwd.getText().toString())){
                ToastView toast = new ToastView(this, "原密码输入错误" );
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            if (!input_new_pwd.getText().toString().equals(input_new_pwd02.getText().toString())){
                ToastView toast = new ToastView(this, "新密码前后输入不一致" );
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            new AlertDialog.Builder(UpdateInfoActivity.this).setTitle("提示")       //设置标题
                    .setMessage("您是否确定要修改您的登录密码吗")        //设置显示的内容
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            final MyProgressDialog mydialog = new MyProgressDialog(UpdateInfoActivity.this,"努力修改中...");
                            mydialog.show();

                            String objectId = shared.getString("objectid", "");
                            EcBookUser user = new EcBookUser();
                            user.setPassword(input_new_pwd.getText().toString());
                            user.update(UpdateInfoActivity.this, objectId, new UpdateListener() {

                                @Override
                                public void onSuccess() {

                                    editor = shared.edit();
                                    editor.putString("password",input_new_pwd.getText().toString());
                                    editor.commit();

                                    if (mydialog.isShowing()) mydialog.dismiss();
                                    ToastView toast = new ToastView(UpdateInfoActivity.this, "密码修改成功" );
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    finish();
                                }

                                @Override
                                public void onFailure(int i, String s) {

                                    if (mydialog.isShowing()) mydialog.dismiss();
                                    ToastView toast = new ToastView(UpdateInfoActivity.this, "密码修改失败：" + s);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                            });

                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
        }
        else{
            ToastView toast = new ToastView(this, "填写信息不能为空" );
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }
}
