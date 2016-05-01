package com.kelvin.ecbook.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelvin.ecbook.R;

/**
 * Created by Kelvin on 2016/5/1.
 */
public class WebViewActivity extends BaseActivity {

    private WebView webview;
    private String url;
    private View progress_bar;
    private ImageView back;
    private TextView title;

    private int screen_width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_webview);

        Intent intent = this.getIntent();
        url = intent.getStringExtra("url");

        title = (TextView)findViewById(R.id.top_view_text);
        title.setText(intent.getStringExtra("title"));

        back = (ImageView)findViewById(R.id.top_view_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //获得屏幕宽度
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        screen_width = wm.getDefaultDisplay().getWidth();

        progress_bar = findViewById(R.id.progress_bar);
        webview = (WebView)findViewById(R.id.webview);
        webview.loadUrl(url);

        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });

        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                if (newProgress == 100) {
                    // 网页加载完成
                    progress_bar.setVisibility(View.GONE);
                } else {
                    // 加载中
                    progress_bar.setVisibility(View.VISIBLE);
                    ViewGroup.LayoutParams layoutParams = progress_bar.getLayoutParams();
                    layoutParams.width = (screen_width * newProgress)/100;
                    progress_bar.setLayoutParams(layoutParams);
                }

            }
        });
    }
}
