package com.kelvin.ecbook.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.kelvin.ecbook.R;
import com.kelvin.ecbook.engine.RegistEngine;
import com.kelvin.ecbook.view.toast.ToastView;

/**
 * Created by Kelvin on 2016/4/21.
 */
public class SignupActivity extends BaseActivity implements View.OnClickListener {

    private ImageView reback;

    private EditText username;
    private EditText email;
    private EditText password;
    private EditText confirm;

    private Button signup;

    private RegistEngine engine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);

        init();
    }

    private void init(){

        engine = new RegistEngine(this);

        reback = (ImageView)findViewById(R.id.register_back);
        reback.setOnClickListener(this);

        username = (EditText)findViewById(R.id.register_name);
        email = (EditText)findViewById(R.id.register_email);
        password = (EditText)findViewById(R.id.register_password1);
        confirm = (EditText)findViewById(R.id.register_password2);

        signup = (Button)findViewById(R.id.register_register);
        signup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.register_back: {
                finish();
                break;
            }
            case R.id.register_register:{

                if (username.getText().toString().equals("")){
                    ToastView toast = new ToastView(this, "用户名不能为空");
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                if (email.getText().toString().equals("")){
                    ToastView toast = new ToastView(this, "邮箱不能为空");
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                if (password.getText().toString().equals("")){
                    ToastView toast = new ToastView(this, "密码不能为空");
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                if (confirm.getText().toString().equals("")){
                    ToastView toast = new ToastView(this, "请确认你的密码");
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }

                if (!password.getText().toString().equals(confirm.getText().toString())) {
                    ToastView toast = new ToastView(this, "前后密码不一致");
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }

                //注册
                engine.signUp(username.getText().toString(),email.getText().toString(),
                        password.getText().toString());
            }
        }
    }
}
