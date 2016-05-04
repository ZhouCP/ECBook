package com.kelvin.ecbook.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kelvin.ecbook.R;
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
    private TextView upload_num;
    private FrameLayout credit;
    private TextView credit_num;
    private FrameLayout download;
    private TextView download_num;

    private LinearLayout detail;
    private LinearLayout manage;
    private LinearLayout help;

    private LinearLayout memberLevelLayout;
    private TextView     memberLevelName;
    private ImageView    memberLevelIcon;

    private SharedPreferences shared;
    private SharedPreferences.Editor editor;

    private ImageView setting;
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

        setting = (ImageView) view.findViewById(R.id.profile_setting);
        setting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /**
                 * 设置
                 *
                 * Intent intent = new Intent(getActivity(), G0_SettingActivity.class);
                 * startActivity(intent);
                 * getActivity().overridePendingTransition(R.anim.push_right_in,
                 * R.anim.push_right_out);
                 *
                 */

            }
        });

        xlistView = (XListView) view.findViewById(R.id.profile_list);
        xlistView.addHeaderView(headView);

        xlistView.setPullLoadEnable(false);
        xlistView.setRefreshTime();
        xlistView.setXListViewListener(this,1);
        xlistView.setAdapter(null);

        memberLevelLayout = (LinearLayout)headView.findViewById(R.id.member_level_layout);
        memberLevelName   = (TextView)headView.findViewById(R.id.member_level);
        memberLevelIcon   = (ImageView)headView.findViewById(R.id.member_level_icon);

        setting  = (ImageView) headView.findViewById(R.id.profile_head_setting);
        photo = (WebImageView) headView.findViewById(R.id.profile_head_photo);
        camera = (ImageView) headView.findViewById(R.id.profile_head_camera);
        name = (TextView) headView.findViewById(R.id.profile_head_name);

        upload = (FrameLayout) headView.findViewById(R.id.profile_upload_history);
        upload_num = (TextView) headView.findViewById(R.id.profile_upload_history_num);

        credit = (FrameLayout) headView.findViewById(R.id.profile_credit);
        credit_num = (TextView) headView.findViewById(R.id.profile_credit_num);

        download = (FrameLayout) headView.findViewById(R.id.profile_download_history);
        download_num = (TextView) headView.findViewById(R.id.profile_download_history_num);


        detail = (LinearLayout) headView.findViewById(R.id.profile_detail);
        manage = (LinearLayout) headView.findViewById(R.id.profile_download_manage);
        help = (LinearLayout) headView.findViewById(R.id.profile_help);

        setting.setOnClickListener(this);
        camera.setOnClickListener(this);
        name.setOnClickListener(this);
        upload.setOnClickListener(this);
        credit.setOnClickListener(this);
        download.setOnClickListener(this);
        detail.setOnClickListener(this);
        manage.setOnClickListener(this);
        help.setOnClickListener(this);

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

        if (uid != ""){
            BmobQuery<EcBookUser> query = new BmobQuery<EcBookUser>();
            query.addWhereEqualTo("objectId", uid);
            query.setLimit(1);
            query.findObjects(getActivity(), new FindListener<EcBookUser>() {
                @Override
                public void onSuccess(List<EcBookUser> list) {

                    if (list.size() != 0) {

                        userModel.setUser(list.get(0));

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
    }


    @Override
    public void onClick(View v) {

        Intent intent;
        switch(v.getId()) {
            case R.id.profile_head_setting:

                /**
                uid = shared.getString("uid", "");
                if (uid.equals("")) {
                    isRefresh = true;
                    intent = new Intent(getActivity(), A0_SigninActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.push_buttom_in,R.anim.push_buttom_out);
                } else {
                    intent = new Intent(getActivity(), G0_SettingActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.push_right_in,
                            R.anim.push_right_out);
                }**/
                break;
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
                Toast.makeText(getActivity(),"我上传的",Toast.LENGTH_SHORT).show();

                /**
                if (uid.equals("")) {
                    isRefresh = true;
                    intent = new Intent(getActivity(), A0_SigninActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.push_buttom_in,R.anim.push_buttom_out);
                } else {
                    intent = new Intent(getActivity(), E4_HistoryActivity.class);
                    intent.putExtra("flag", "await_pay");
                    startActivityForResult(intent, 2);
                    getActivity().overridePendingTransition(R.anim.push_right_in,
                            R.anim.push_right_out);
                }**/
                break;
            case R.id.profile_credit:

                //我的积分
                Toast.makeText(getActivity(),"我的积分",Toast.LENGTH_SHORT).show();
                /**
                if (uid.equals("")) {
                    isRefresh = true;
                    intent = new Intent(getActivity(), A0_SigninActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.push_buttom_in,R.anim.push_buttom_out);
                } else {
                    intent = new Intent(getActivity(), E4_HistoryActivity.class);
                    intent.putExtra("flag", "await_ship");
                    startActivityForResult(intent, 2);
                    getActivity().overridePendingTransition(R.anim.push_right_in,
                            R.anim.push_right_out);
                }**/
                break;
            case R.id.profile_download_history:

                //下载历史
                Toast.makeText(getActivity(),"下载历史",Toast.LENGTH_SHORT).show();
                /**
                if (uid.equals("")) {
                    isRefresh = true;
                    intent = new Intent(getActivity(), A0_SigninActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.push_buttom_in,R.anim.push_buttom_out);
                } else {
                    intent = new Intent(getActivity(), E4_HistoryActivity.class);
                    intent.putExtra("flag", "shipped");
                    startActivityForResult(intent, 2);
                    getActivity().overridePendingTransition(R.anim.push_right_in,
                            R.anim.push_right_out);
                }**/
                break;
            case R.id.profile_detail:

                //个人信息
                Toast.makeText(getActivity(),"个人信息",Toast.LENGTH_SHORT).show();
                /**
                if (uid.equals("")) {
                    isRefresh = true;
                    intent = new Intent(getActivity(), A0_SigninActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.push_buttom_in,R.anim.push_buttom_out);
                } else {
                    intent = new Intent(getActivity(), E4_HistoryActivity.class);
                    intent.putExtra("flag", "finished");
                    startActivityForResult(intent, 2);
                    getActivity().overridePendingTransition(R.anim.push_right_in,
                            R.anim.push_right_out);
                }**/
                break;
            case R.id.profile_download_manage:

                //下载管理
                Toast.makeText(getActivity(),"下载管理",Toast.LENGTH_SHORT).show();
                /**
                if (uid.equals("")) {
                    isRefresh = true;
                    intent = new Intent(getActivity(), A0_SigninActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.push_buttom_in,R.anim.push_buttom_out);
                } else {
                    intent = new Intent(getActivity(), E5_CollectionActivity.class);
                    startActivityForResult(intent, 2);
                    getActivity().overridePendingTransition(R.anim.push_right_in,
                            R.anim.push_right_out);
                }**/
                break;
            case R.id.profile_help:

                //帮助
                Toast.makeText(getActivity(),"帮助",Toast.LENGTH_SHORT).show();
                /**
                 intent = new Intent(getActivity(), G2_InfoActivity.class);
                 startActivity(intent);
                 getActivity().overridePendingTransition(R.anim.push_right_in,
                 R.anim.push_right_out);
                 **/
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

        if (!uid.equals("")) {
            getUserInfo();
        }

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
            default:
                break;
        }
    }

}
