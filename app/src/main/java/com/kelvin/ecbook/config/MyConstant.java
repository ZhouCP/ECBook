package com.kelvin.ecbook.config;

import android.os.Environment;

/**
 * Created by kelvin on 16/4/20.
 */
public class MyConstant {

    public static String AvatarDir = Environment.getExternalStorageDirectory() + "/ECBook/image/";
    public static String BookDir = Environment.getExternalStorageDirectory() + "/ECBook/book/";

    public static int REQUESTCODE_SIGN_UP = 0x001;
    public static int RESULTSTCODE_SIGN_UP_SUCCESS = 0x002;
    public static int RESULTSTCODE_SIGN_IN_SUCCESS = 0x003;

    /**
     * 打开文件返回
     */
    public static final int FILE_SELECT_CODE = 0x000004;
}
