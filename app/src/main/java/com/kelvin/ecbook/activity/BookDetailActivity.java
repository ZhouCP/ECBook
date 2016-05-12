package com.kelvin.ecbook.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.kelvin.ecbook.config.StaticData;
import com.kelvin.ecbook.model.Book;
import com.kelvin.ecbook.model.Collection;
import com.kelvin.ecbook.model.Download;
import com.kelvin.ecbook.utils.ImageLoadOptions;
import com.kelvin.ecbook.view.toast.ToastView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

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

    private TextView download,collect;

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
        collect = (TextView) findViewById(R.id.add_to_collection);
        download.setOnClickListener(this);
        collect.setOnClickListener(this);

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
                setBookScanAddOne();
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
     * 设置电子书浏览次数+1
     */
    private void setBookScanAddOne(){

        book.setScanTime(book.getScanTime()+1);
        book.update(this, book.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    /**
     * 设置电子书下载次数+1
     */
    private void setBookDownloadAddOne(){

        book.setPayTime(book.getPayTime() + 1);
        book.update(this, book.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i, String s) {

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
                    SharedPreferences sh = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
                    String userid = sh.getString("objectid","");

                    if (userid.equals("")){
                        Intent intent = new Intent(this, SigninActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.push_buttom_in,R.anim.push_buttom_out);
                    }
                    else {

                        BmobQuery<Download> downloadBmobQuery = new BmobQuery<>();
                        downloadBmobQuery.addWhereEqualTo("downloader",userid);
                        downloadBmobQuery.addWhereEqualTo("book", bookId);
                        downloadBmobQuery.findObjects(BookDetailActivity.this, new FindListener<Download>() {
                            @Override
                            public void onSuccess(List<Download> list) {

                                if (list.size() == 0) {
                                    String tip = "你确定要下载《" + book.getTitle() + "》吗，此行为将扣除" + book.getCost() + "个EC币";
                                    new AlertDialog.Builder(BookDetailActivity.this).setTitle("提示")       //设置标题
                                            .setMessage(tip)        //设置显示的内容
                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    download_tip.setText("下载中");
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
                                } else {
                                    String tip = "你已经下载过《" + book.getTitle() + "》了，是否重新下载";
                                    new AlertDialog.Builder(BookDetailActivity.this).setTitle("提示")       //设置标题
                                            .setMessage(tip)        //设置显示的内容
                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    download_tip.setText("下载中");
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
                                }
                            }

                            @Override
                            public void onError(int i, String s) {

                            }
                        });

                    }
                    break;
                case R.id.add_to_collection:
                    addToCollections();
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
                    addDownloadRecord();
                    setBookDownloadAddOne();
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                //toast("下载失败："+code+","+msg);
            }
        });
    }


    /**
     * 添加下载记录
     */
    private void addDownloadRecord(){

        SharedPreferences sh = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        final String userid = sh.getString("objectid", "");

        Download download = new Download();
        download.setDownloader(userid);
        download.setBook(bookId);

        download.save(this, new SaveListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    /**
     * 添加进收藏
     */
    private void addToCollections(){

        SharedPreferences sh = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        final String userid = sh.getString("objectid","");

        if (userid.equals("")){
            Intent intent = new Intent(this, SigninActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.push_buttom_in, R.anim.push_buttom_out);
        }
        else{

            BmobQuery<Collection> query = new BmobQuery<>();
            query.addWhereEqualTo("book",bookId);
            query.setLimit(1);
            query.findObjects(this, new FindListener<Collection>() {
                @Override
                public void onSuccess(List<Collection> list) {

                    if (list.size() == 0) {
                        Collection collection = new Collection();
                        collection.setCollector(userid);
                        collection.setBook(bookId);
                        collection.save(BookDetailActivity.this, new SaveListener() {
                            @Override
                            public void onSuccess() {
                                ToastView toast = new ToastView(BookDetailActivity.this, "加入收藏成功");
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();

                                StaticData.NUM_COLLECTIONS++;
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                ToastView toast = new ToastView(BookDetailActivity.this, "加入收藏失败："+s);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        });
                    }
                    else{
                        ToastView toast = new ToastView(BookDetailActivity.this, "你已经收藏该书了哦");
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }

                @Override
                public void onError(int i, String s) {

                }
            });


        }
    }
}
