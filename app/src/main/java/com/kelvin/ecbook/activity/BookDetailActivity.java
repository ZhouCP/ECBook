package com.kelvin.ecbook.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kelvin.ecbook.R;
import com.kelvin.ecbook.config.MyConstant;
import com.kelvin.ecbook.model.Book;
import com.kelvin.ecbook.utils.ImageLoadOptions;
import com.kelvin.ecbook.view.toast.ToastView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.GetListener;

/**
 * Created by Kelvin on 2016/5/3.
 */
public class BookDetailActivity extends BaseActivity implements OnClickListener{

    private String bookId;
    private Book book;

    private ImageView back;
    private TextView title;

    private ImageView photo_one,photo_two;

    private TextView show_title,show_category,show_price;

    private TextView download;

    private LinearLayout download_layout;
    private TextView progress_text,download_tip;
    private View progress_bar;

    private boolean isDownloading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_book_detail);

        Intent intent = this.getIntent();
        bookId = intent.getStringExtra("bookId");

        back = (ImageView) findViewById(R.id.top_view_back);
        back.setOnClickListener(this);

        title = (TextView) findViewById(R.id.top_view_text);
        title.setText("电子书详情");

        getBookData();

        download = (TextView) findViewById(R.id.download_now);
        download.setOnClickListener(this);

        download_layout = (LinearLayout) findViewById(R.id.download_layout);
        progress_text = (TextView) findViewById(R.id.progress_text);
        download_tip = (TextView) findViewById(R.id.download_tip);
        progress_bar = findViewById(R.id.progress_bar);
    }

    /**
     * 获得电子书详细信息
     */
    private void getBookData(){

        BmobQuery<Book> query = new BmobQuery<>();
        query.getObject(this, bookId, new GetListener<Book>() {
            @Override
            public void onSuccess(Book model) {
                book = model;

                setBookDetail();
            }

            @Override
            public void onFailure(int i, String s) {
                ToastView toast = new ToastView(BookDetailActivity.this, "获取不到电子书信息：" + s);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }


    /**
     * 设置电子书信息
     */
    private void setBookDetail(){

        photo_one = (ImageView) findViewById(R.id.good_cell_photo_one);
        photo_two = (ImageView) findViewById(R.id.good_cell_photo_two);

        if (!TextUtils.isEmpty(book.getPhoto01())) {
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
            imageLoader.displayImage(book.getPhoto01(), photo_one,
                    ImageLoadOptions.getOptions());
        }

        if (!TextUtils.isEmpty(book.getPhoto02())) {
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
            imageLoader.displayImage(book.getPhoto02(), photo_two,
                    ImageLoadOptions.getOptions());
        }


        show_title = (TextView) findViewById(R.id.show_title);
        show_category = (TextView) findViewById(R.id.show_category);
        show_price = (TextView) findViewById(R.id.show_price);

        show_title.setText(book.getTitle());
        show_category.setText(book.getCategory());
        show_price.setText(book.getCost() + "");

    }

    @Override
    public void onClick(View v) {

        if (isDownloading == false) {

            switch (v.getId()) {
                case R.id.top_view_back:
                    finish();
                    break;
                case R.id.download_now:
                    String tip = "你确定要下载《" + book.getTitle() + "》吗";
                    download_tip.setText("下载中");
                    new AlertDialog.Builder(BookDetailActivity.this).setTitle("提示")       //设置标题
                            .setMessage(tip)        //设置显示的内容
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    isDownloading = true;
                                    download_layout.setVisibility(View.VISIBLE);
                                    dowloadBook();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                    break;
            }
        }
    }


    /**
     * 下载电子书
     */
    private void dowloadBook(){

        BmobFile bookfile =new BmobFile(book.getTitle()+".pdf","",book.getBookUrl());
        File saveFile = new File(MyConstant.BookDir + book.getTitle() + ".pdf");

        bookfile.download(this, saveFile, new DownloadFileListener() {
            @Override
            public void onStart() {
                //toast("开始下载...");
            }

            @Override
            public void onSuccess(String savePath) {
                //toast("下载成功,保存路径:"+savePath);
            }

            @Override
            public void onProgress(Integer value, long newworkSpeed) {
                //Log.i("bmob", "下载进度：" + value + "," + newworkSpeed);

                ViewGroup.LayoutParams layoutParams = progress_bar.getLayoutParams();
                layoutParams.width = value * 5;
                progress_bar.setLayoutParams(layoutParams);
                progress_text.setText(value + "%");

                if (value == 100){
                    download_tip.setText("下载完成(" + MyConstant.BookDir + ")");
                    isDownloading = false;
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                //toast("下载失败："+code+","+msg);
            }
        });
    }
}
