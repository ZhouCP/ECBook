package com.kelvin.ecbook.activity;

import android.app.Activity;
import android.os.Bundle;

import com.kelvin.ecbook.config.Config;

import cn.bmob.v3.Bmob;

/**
 * Created by Kelvin on 2016/4/22.
 */
public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bmob.initialize(this, Config.ApplicationId);
    }
}
