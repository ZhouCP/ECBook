package com.kelvin.ecbook.engine;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.Gravity;

import com.kelvin.ecbook.config.MyConstant;
import com.kelvin.ecbook.model.EcBookUser;
import com.kelvin.ecbook.model.EcBookUserModel;
import com.kelvin.ecbook.view.dialog.MyProgressDialog;
import com.kelvin.ecbook.view.toast.ToastView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Kelvin on 2016/4/23.
 */
public class LoginEngine {

    private Activity activity;
    private EcBookUserModel model;
    private MyProgressDialog dialog;

    private SharedPreferences shared;
    private SharedPreferences.Editor editor;

    public LoginEngine(Activity activity){

        this.activity = activity;
        this.model = new EcBookUserModel(activity);
    }

    public void login(String email, final String password){

        dialog = new MyProgressDialog(activity,"登录中");
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        BmobQuery<EcBookUser> query = new BmobQuery<EcBookUser>();
        query.addWhereEqualTo("email", email);
        query.setLimit(1);
        query.findObjects(activity, new FindListener<EcBookUser>() {
            @Override
            public void onSuccess(List<EcBookUser> list) {

                if (list.size() == 0) {       //邮箱没被注册
                    if (dialog.isShowing()) dialog.dismiss();
                    ToastView toast = new ToastView(activity, "该邮箱还没注册");
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    EcBookUser user = list.get(0);
                    if (user.getPassword().equals(password)){
                        //保存登录信息
                        /*
                        model.setUsername(user.getUsername());
                        model.setEmail(user.getEmail());
                        model.setPassword(user.getPassword());*/
                        model.setUser(user);
                        model.saveUserInfoToLocal();

                        if (dialog.isShowing()) dialog.dismiss();
                        ToastView toast = new ToastView(activity, "登录成功");
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        activity.setResult(MyConstant.RESULTSTCODE_SIGN_IN_SUCCESS);
                        activity.finish();
                    }
                    else{
                        if (dialog.isShowing()) dialog.dismiss();
                        ToastView toast = new ToastView(activity, "密码错误");
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                if (dialog.isShowing()) dialog.dismiss();
                ToastView toast = new ToastView(activity, "登录失败：" + s);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }
}
