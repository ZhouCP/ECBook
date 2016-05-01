package com.kelvin.ecbook.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.kelvin.ecbook.R;

/**
 * Created by kelvin on 16/4/19.
 */
public class SplashActivity extends BaseActivity{

    private Context context;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View startView = View.inflate(this, R.layout.activity_splash, null);
        setContentView(startView);
        context = this;
        //渐变
        AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
        aa.setDuration(2000);
        startView.setAnimation(aa);
        aa.setAnimationListener(new Animation.AnimationListener() {

                                    @Override
                                    public void onAnimationStart(Animation animation) {


                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {


                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {

                                        redirectto();
                                    }
                                }
        );
    }

    private void redirectto() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
