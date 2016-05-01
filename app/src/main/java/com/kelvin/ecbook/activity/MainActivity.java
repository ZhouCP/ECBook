package com.kelvin.ecbook.activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.widget.Toast;

import com.kelvin.ecbook.R;
import com.kelvin.ecbook.config.Config;

import cn.bmob.v3.Bmob;

/**
 * Created by kelvin on 16/4/19.
 */
public class MainActivity extends FragmentActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bmob.initialize(this, Config.ApplicationId);

        setContentView(R.layout.activity_main);
    }

    private boolean isExit = false;
    //退出操作
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(isExit==false){
                isExit=true;
                Resources resource = (Resources) getBaseContext().getResources();
                String exi=resource.getString(R.string.exit);
                Toast.makeText(getApplicationContext(), exi, Toast.LENGTH_SHORT).show();
                handler.sendEmptyMessageDelayed(0, 3000);

                return true;
            } else {
                finish();

                return false;
            }
        }
        return true;
    }


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit=false;
        }
    };
}
