package com.kelvin.ecbook.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kelvin.ecbook.R;
import com.kelvin.ecbook.activity.MyCreditActivity;
import com.kelvin.ecbook.activity.MyDownloadActivity;
import com.kelvin.ecbook.activity.MyInfoActivity;
import com.kelvin.ecbook.activity.MyUploadActivity;
import com.kelvin.ecbook.activity.SigninActivity;
import com.kelvin.ecbook.model.EcBookUser;
import com.kelvin.ecbook.model.EcBookUserModel;
import com.kelvin.ecbook.utils.ImageLoadOptions;
import com.kelvin.ecbook.utils.ImageUtils;
import com.kelvin.ecbook.view.toast.ToastView;
import com.kelvin.ecbook.view.webimage.WebImageView;
import com.kelvin.ecbook.view.xlistview.XListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by kelvin on 16/4/19.
 */
public class ProfileFragment extends Fragment implements XListView.IXListViewListener, View.OnClickListener {

    private View view;
    private View headView;
    private XListView xlistView;

    public static WebImageView photo;
    private ImageView camera;

    private TextView name;

    private FrameLayout upload;
    private FrameLayout credit;
    private FrameLayout download;

    private LinearLayout detail;
    private LinearLayout about;
    private LinearLayout exit;

    private SharedPreferences shared;
    private SharedPreferences.Editor editor;

    private String uid;
    private EcBookUserModel userModel;
    public static boolean isRefresh = false;

    protected Context mContext;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_profile, null);

        mContext = getActivity();

        shared = getActivity().getSharedPreferences("userInfo", 0);
        editor = shared.edit();

        userModel = new EcBookUserModel(getActivity());

        headView = LayoutInflater.from(getActivity()).inflate(R.layout.profile_head, null);

        xlistView = (XListView) view.findViewById(R.id.profile_list);
        xlistView.addHeaderView(headView);

        xlistView.setPullLoadEnable(false);
        xlistView.setRefreshTime();
        xlistView.setXListViewListener(this, 1);
        xlistView.setAdapter(null);

        photo = (WebImageView) headView.findViewById(R.id.profile_head_photo);
        camera = (ImageView) headView.findViewById(R.id.profile_head_camera);
        name = (TextView) headView.findViewById(R.id.profile_head_name);

        upload = (FrameLayout) headView.findViewById(R.id.profile_upload_history);

        credit = (FrameLayout) headView.findViewById(R.id.profile_credit);

        download = (FrameLayout) headView.findViewById(R.id.profile_download_history);


        detail = (LinearLayout) headView.findViewById(R.id.profile_detail);
        about = (LinearLayout) headView.findViewById(R.id.profile_about);
        exit = (LinearLayout) headView.findViewById(R.id.profile_exit);

        camera.setOnClickListener(this);
        name.setOnClickListener(this);
        upload.setOnClickListener(this);
        credit.setOnClickListener(this);
        download.setOnClickListener(this);
        detail.setOnClickListener(this);
        about.setOnClickListener(this);
        exit.setOnClickListener(this);

        uid = shared.getString("objectid", "");
        photo.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {

        super.onResume();

        setUserInfo();
        uid = shared.getString("objectid", "");
        if (!uid.equals("")) {
            getUserInfo();
            camera.setVisibility(View.VISIBLE);
        } else {
            camera.setVisibility(View.GONE);
        }
    }

    // set User 信息
    public void setUserInfo() {

        if (!TextUtils.isEmpty(userModel.getCurrentUser().getUsername())) {
            name.setText(userModel.getCurrentUser().getUsername());
        }

        String avatar = userModel.getCurrentUser().getAvatar();
        if (avatar != null && !avatar.equals("")) {

            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
            imageLoader.displayImage(avatar, photo,
                    ImageLoadOptions.getOptions());
        } else {
            photo.setImageResource(R.drawable.profile_no_avatar_icon);
        }


    }


    // get User 信息
    public void getUserInfo(){

        BmobQuery<EcBookUser> query = new BmobQuery<EcBookUser>();
        query.addWhereEqualTo("objectId", uid);
        query.setLimit(1);
        query.findObjects(getActivity(), new FindListener<EcBookUser>() {
            @Override
            public void onSuccess(List<EcBookUser> list) {

                if (list.size() != 0) {

                    userModel.setUser(list.get(0));
                    userModel.saveUserInfoToLocal();

                    setUserInfo();
                }
            }

            @Override
            public void onError(int i, String s) {
                ToastView toast = new ToastView(getActivity(), "获取用户信息失败：" + s);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });

    }


    @Override
    public void onClick(View v) {

        Intent intent;
        switch(v.getId()) {
            case R.id.profile_head_camera:

                if (uid.equals("")) {
                    isRefresh = true;
                    intent = new Intent(getActivity(), SigninActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.push_buttom_in,R.anim.push_buttom_out);
                } else {
                    ImageUtils.showImagePickDialog(this);
                }
                break;
            case R.id.profile_upload_history:

                //我上传的
                //Toast.makeText(getActivity(),"我上传的",Toast.LENGTH_SHORT).show();

                if (uid.equals("")) {
                    isRefresh = true;
                    intent = new Intent(getActivity(), SigninActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.push_buttom_in,R.anim.push_buttom_out);
                } else {
                    intent = new Intent(getActivity(), MyUploadActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                }
                break;
            case R.id.profile_credit:

                //我的积分
                //Toast.makeText(getActivity(),"我的积分",Toast.LENGTH_SHORT).show();

                if (uid.equals("")) {
                    isRefresh = true;
                    intent = new Intent(getActivity(), SigninActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.push_buttom_in,R.anim.push_buttom_out);
                } else {
                    intent = new Intent(getActivity(), MyCreditActivity.class);
                    intent.putExtra("credit", userModel.getCurrentUser().getCredit());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.push_right_in,
                            R.anim.push_right_out);
                }
                break;
            case R.id.profile_download_history:

                //下载历史
                //Toast.makeText(getActivity(),"下载历史",Toast.LENGTH_SHORT).show();
                if (uid.equals("")) {
                    isRefresh = true;
                    intent = new Intent(getActivity(), SigninActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.push_buttom_in,R.anim.push_buttom_out);
                } else {
                    intent = new Intent(getActivity(), MyDownloadActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                }
                break;
            case R.id.profile_detail:

                //个人信息
                //Toast.makeText(getActivity(),"个人信息",Toast.LENGTH_SHORT).show();

                if (uid.equals("")) {
                    isRefresh = true;
                    intent = new Intent(getActivity(), SigninActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.push_buttom_in,R.anim.push_buttom_out);
                } else {
                    intent = new Intent(getActivity(), MyInfoActivity.class);
                    startActivityForResult(intent, 0);
                    getActivity().overridePendingTransition(R.anim.push_right_in,
                            R.anim.push_right_out);
                }
                break;
            case R.id.profile_about:

                //关于
                //Toast.makeText(getActivity(),"关于",Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(getActivity()).setTitle("关于")       //设置标题
                        .setMessage("ECBook 1.0版本\nKelvin版权所有")        //设置显示的内容
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
                break;
            case R.id.profile_exit:

                //退出登录
                //Toast.makeText(getActivity(),"退出登录",Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(getActivity()).setTitle("提示")       //设置标题
                        .setMessage("您确定要退出登录吗")        //设置显示的内容
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                shared = mContext.getSharedPreferences("userInfo", 0);
                                editor = shared.edit();
                                editor.putString("objectid","");
                                editor.putString("username","");
                                editor.putString("email","");
                                editor.putString("password", "");
                                editor.putInt("credit", 0);
                                editor.commit();        //提交更新

                                uid = shared.getString("objectid", "");
                                camera.setVisibility(View.GONE);
                                photo.setImageResource(R.drawable.profile_no_avatar_icon);
                                name.setText("点击此处登录");
                            }
                        }).show();
                break;
            case R.id.profile_head_name:

                if (uid.equals("")) {
                    isRefresh = true;
                    intent = new Intent(getActivity(), SigninActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.push_buttom_in,R.anim.push_buttom_out);
                }
                break;
            case R.id.profile_head_photo:

                if (uid.equals("")) {
                    isRefresh = true;
                    intent = new Intent(getActivity(), SigninActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.push_buttom_in,R.anim.push_buttom_out);
                }else{
                    ImageUtils.showImagePickDialog(this);
                }
                break;

        }
    }


    @Override
    public void onRefresh(int id) {

        getUserInfo();

    }

    @Override
    public void onLoadMore(int id) {


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case ImageUtils.REQUEST_CODE_FROM_ALBUM: {

                if (resultCode == getActivity().RESULT_CANCELED) {   //取消操作
                    return;
                }

                Uri imageUri = data.getData();
                ImageUtils.copyImageUri(getActivity(),imageUri);
                ImageUtils.cropImageUri(this, ImageUtils.getCurrentUri(), 200, 200);
                break;
            }
            case ImageUtils.REQUEST_CODE_FROM_CAMERA: {

                if (resultCode == getActivity().RESULT_CANCELED) {     //取消操作
                    ImageUtils.deleteImageUri(getActivity(), ImageUtils.getCurrentUri());   //删除Uri
                }

                ImageUtils.cropImageUri(this, ImageUtils.getCurrentUri(), 200, 200);
                break;
            }
            case ImageUtils.REQUEST_CODE_CROP: {

                if (resultCode == getActivity().RESULT_CANCELED) {     //取消操作
                    return;
                }

                Uri imageUri = ImageUtils.getCurrentUri();
                if (imageUri != null) {
                    photo.setImageURI(imageUri);
                    userModel.uploadAvatar(imageUri);     //保存头像
                }
                break;
            }
            case 0: {
                getUserInfo();
                break;
            }
            default:
                break;
        }
    }

}
