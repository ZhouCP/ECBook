package com.kelvin.ecbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelvin.ecbook.R;
import com.kelvin.ecbook.config.MyConstant;
import com.kelvin.ecbook.engine.LoginEngine;
import com.kelvin.ecbook.model.EcBookUserModel;
import com.kelvin.ecbook.view.toast.ToastView;

/**
 * Created by kelvin on 16/4/19.
 */
public class SigninActivity extends BaseActivity implements View.OnClickListener{

    private TextView register;
    private ImageView reback;

    private Button signin;
    private EditText email,password;

    private LoginEngine engine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signin);

        init();
    }

    private void init(){

        engine = new LoginEngine(this);

        register = (TextView)findViewById(R.id.login_register);
        reback = (ImageView)findViewById(R.id.login_back);
        signin = (Button)findViewById(R.id.login_login);
        email = (EditText)findViewById(R.id.login_name);
        password = (EditText)findViewById(R.id.login_password);

        register.setOnClickListener(this);
        reback.setOnClickListener(this);
        signin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.login_register: {
                //跳转注册
                Intent intent = new Intent(this, SignupActivity.class);
                startActivityForResult(intent, MyConstant.REQUESTCODE_SIGN_UP);
                overridePendingTransition(R.anim.push_right_in,
                        R.anim.push_right_out);
                break;
            }
            case R.id.login_back:{
                finish();
                break;
            }
            case R.id.login_login:{

                if (email.getText().toString().equals("") || password.getText().toString().equals("")){
                    ToastView toast = new ToastView(this, "登录信息不能为空");
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                engine.login(email.getText().toString(),password.getText().toString());
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MyConstant.REQUESTCODE_SIGN_UP){
            if (resultCode == MyConstant.RESULTSTCODE_SIGN_UP_SUCCESS){
                //注册成功返回
                EcBookUserModel model = new EcBookUserModel(this);
                email.setText(model.getMarkEmail());
                password.setText(model.getMarkPwd());
                engine.login(model.getMarkEmail(),model.getMarkPwd());
            }
        }
    }
}
