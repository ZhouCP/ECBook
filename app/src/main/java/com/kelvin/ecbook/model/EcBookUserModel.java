package com.kelvin.ecbook.model;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.Gravity;

import com.kelvin.ecbook.config.MyConstant;
import com.kelvin.ecbook.fragment.ProfileFragment;
import com.kelvin.ecbook.utils.ImageLoadOptions;
import com.kelvin.ecbook.utils.UriUtils;
import com.kelvin.ecbook.view.dialog.MyProgressDialog;
import com.kelvin.ecbook.view.toast.ToastView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by Kelvin on 2016/4/30.
 */
public class EcBookUserModel {

    private Activity activity;
    private EcBookUser user;

    private SharedPreferences shared;
    private SharedPreferences.Editor editor;

    public EcBookUserModel(Activity activity){
        this.activity = activity;
        this.user = new EcBookUser();
    }

    public void setUser(EcBookUser user){
        this.user = user;
    }

    public void setEmail(String email){
        user.setEmail(email);
    }

    public void setUsername(String username){
        user.setUsername(username);
    }

    public void setPassword(String password){
        user.setPassword(password);
    }

    public EcBookUser getCurrentUser(){
        return user;
    }

    public void getUserInfo(){

        if (user.getEmail() != ""){
            BmobQuery<EcBookUser> query = new BmobQuery<EcBookUser>();
            query.addWhereEqualTo("email", user.getEmail());
            query.setLimit(1);
            query.findObjects(activity, new FindListener<EcBookUser>() {
                @Override
                public void onSuccess(List<EcBookUser> list) {

                    if (list.size() != 0) {

                        user = list.get(0);
                    }
                }

                @Override
                public void onError(int i, String s) {
                    ToastView toast = new ToastView(activity, "获取用户信息失败：" + s);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            });
        }
    }

    public void saveUserInfoToServer(final MyProgressDialog dialog){

        user.save(activity, new SaveListener() {
            @Override
            public void onSuccess() {

                saveUserInfoToLocal();

                if (dialog != null) dialog.dismiss();
                ToastView toast = new ToastView(activity, "注册成功");
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();


                activity.setResult(MyConstant.RESULTSTCODE_SIGN_UP_SUCCESS);
                activity.finish();
            }

            @Override
            public void onFailure(int i, String s) {
                if (dialog != null) dialog.dismiss();
                ToastView toast = new ToastView(activity, "注册失败：" + s);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

    /**
     * 保存用户信息到本地
     */
    public void saveUserInfoToLocal(){
        shared = this.activity.getSharedPreferences("userInfo", 0);
        editor = shared.edit();
        editor.putString("email",user.getEmail());
        editor.putString("password",user.getPassword());
        editor.commit();        //提交更新
    }

    /**
     * 更新用户信息
     */
    public void updateUserInfoToServer(){

        user.update(activity, user.getObjectId(),new UpdateListener() {
            @Override
            public void onSuccess() {

                ToastView toast = new ToastView(activity, "头像更新成功");
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.init(ImageLoaderConfiguration.createDefault(activity));
                imageLoader.displayImage(user.getAvatar(), ProfileFragment.photo,
                        ImageLoadOptions.getOptions());
            }

            @Override
            public void onFailure(int i, String s) {
                ToastView toast = new ToastView(activity, "头像更新失败：" + s);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

    /**
     * 上传头像
     */
    public void uploadAvatar(Uri imageUri){

        String picPath = UriUtils.getRealFilePath(activity, imageUri);
        final BmobFile bmobFile = new BmobFile(new File(picPath));

        bmobFile.uploadblock(activity, new UploadFileListener() {
            @Override
            public void onSuccess() {

                String avatar = bmobFile.getFileUrl(activity);
                user.setAvatar(avatar);
                updateUserInfoToServer();
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    /**
     * 返回登录过的用户邮箱
     * @Title: getMarkEmail
     * @return String
     */
    public String getMarkEmail(){

        SharedPreferences sh = activity.getSharedPreferences("userInfo",Activity.MODE_PRIVATE);
        return sh.getString("email","");
    }


    /**
     * 返回登录过的密码
     * @Title: getMarkPwd
     * @return String
     */
    public String getMarkPwd(){

        SharedPreferences sh = activity.getSharedPreferences("userInfo",Activity.MODE_PRIVATE);
        return sh.getString("password","");
    }
}
