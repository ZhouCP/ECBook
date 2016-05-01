package com.kelvin.ecbook.engine;

import android.app.Activity;
import android.view.Gravity;

import com.kelvin.ecbook.model.EcBookUser;
import com.kelvin.ecbook.model.EcBookUserModel;
import com.kelvin.ecbook.view.dialog.MyProgressDialog;
import com.kelvin.ecbook.view.toast.ToastView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Kelvin on 2016/4/22.
 */
public class RegistEngine {

    private Activity activity;
    private EcBookUserModel model;
    private MyProgressDialog dialog;

    public RegistEngine(Activity activity){
        this.activity = activity;
        this.model = new EcBookUserModel(activity);
    }


    public void signUp(final String username, final String email, final String password){

        dialog = new MyProgressDialog(activity,"注册中");
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        BmobQuery<EcBookUser> query = new BmobQuery<EcBookUser>();
        query.addWhereEqualTo("email", email);
        query.setLimit(1);
        query.findObjects(activity, new FindListener<EcBookUser>() {
            @Override
            public void onSuccess(List<EcBookUser> list) {

                if (list.size() == 0) {       //邮箱没被注册

                    model.setUsername(username);
                    model.setEmail(email);
                    model.setPassword(password);
                    model.saveUserInfoToServer(dialog);

                } else {
                    if (dialog.isShowing()) dialog.dismiss();
                    ToastView toast = new ToastView(activity, "邮箱已被注册");
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }

            @Override
            public void onError(int i, String s) {
                if (dialog.isShowing()) dialog.dismiss();
                ToastView toast = new ToastView(activity, "注册失败：" + s);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });


    }
}
