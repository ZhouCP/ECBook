package com.kelvin.ecbook.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.kelvin.ecbook.R;
import com.kelvin.ecbook.config.MyConstant;
import com.kelvin.ecbook.model.Book;
import com.kelvin.ecbook.model.BookCategory;
import com.kelvin.ecbook.utils.FileUtils;
import com.kelvin.ecbook.utils.ImageUtils;
import com.kelvin.ecbook.utils.UriUtils;
import com.kelvin.ecbook.view.dialog.MyProgressDialog;
import com.kelvin.ecbook.view.toast.ToastView;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by Kelvin on 2016/5/1.
 */
public class DeclareBookActivity extends BaseActivity implements OnClickListener,OnItemSelectedListener{

    private TextView title;
    private ImageView back;
    private LinearLayout right;
    private TextView right_text;

    private ImageView photo01,photo02;
    private int which = 1;
    private Uri uri01 = null,uri02 = null;

    private Spinner category;
    private String[] categories = {"IT类","设计类","教材类","其他"};

    private FrameLayout other_category;
    private EditText book_title,book_other,book_price;
    private String book_category = "IT类";

    private String photoUrl01,photoUrl02;

    private MyProgressDialog dialog;

    private LinearLayout upload_book;
    private RelativeLayout upload_progress;
    private View progress_bar;
    private TextView progress_text;
    private String pdfPath = "";
    private String pdfUrl = "";

    private boolean isUpload = false;     //标记是否处于上传状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_declarebook);

        title = (TextView) findViewById(R.id.top_view_text);
        title.setText("赚EC币");

        back = (ImageView) findViewById(R.id.top_view_back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);

        right_text = (TextView) findViewById(R.id.top_right_text);
        right_text.setText("发布");

        right = (LinearLayout) findViewById(R.id.top_right_button);
        right.setVisibility(View.VISIBLE);
        right.setOnClickListener(this);

        photo01 = (ImageView) findViewById(R.id.good_cell_photo_one);
        photo02 = (ImageView) findViewById(R.id.good_cell_photo_two);
        photo01.setOnClickListener(this);
        photo02.setOnClickListener(this);

        category = (Spinner) findViewById(R.id.spinner_category);
        category.setOnItemSelectedListener(this);

        other_category = (FrameLayout)findViewById(R.id.other_category);

        book_title = (EditText)findViewById(R.id.input_title);
        book_other = (EditText)findViewById(R.id.input_category);
        book_price = (EditText)findViewById(R.id.input_price);

        upload_book = (LinearLayout) findViewById(R.id.upload_book);
        upload_book.setOnClickListener(this);

        upload_progress = (RelativeLayout) findViewById(R.id.upload_progress);
        progress_bar = findViewById(R.id.progress_bar);
        progress_text = (TextView) findViewById(R.id.progress_text);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.top_view_back:
                if (isUpload == false) {
                    finish();
                }
                break;
            case R.id.top_right_button:      //发布资源

                if (isUpload == false) {
                    if (TextUtils.isEmpty(book_title.getText().toString()) ||
                            TextUtils.isEmpty(book_price.getText().toString())) {
                        ToastView toast = new ToastView(DeclareBookActivity.this, "请完善基本信息");
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        uploadPhoto();
                    }
                }
                break;
            case R.id.good_cell_photo_one:     //选择第一张图片
                which = 1;
                ImageUtils.pickImageFromAlbum(this);
                break;
            case R.id.good_cell_photo_two:     //选择第二张图片
                which = 2;
                ImageUtils.pickImageFromAlbum(this);
                break;
            case R.id.upload_book:     //选择上传电子书
                if (isUpload == false) {
                    showFileChooser();
                }
                break;
        }
    }

    /**
     * 上传照片
     */
    private void uploadPhoto(){

        isUpload = true;

        dialog = new MyProgressDialog(this,"发布中");
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        final String[] photoPaths = new String[2];
        if (uri01 != null) {
            photoPaths[0] = UriUtils.getRealFilePath(this, uri01);
            final BmobFile file01 = new BmobFile(new File(photoPaths[0]));
            file01.uploadblock(this, new UploadFileListener() {
                @Override
                public void onSuccess() {

                    photoUrl01 = file01.getFileUrl(DeclareBookActivity.this);
                    if (uri02 != null) {
                        photoPaths[1] = UriUtils.getRealFilePath(DeclareBookActivity.this, uri02);

                        final BmobFile file02 = new BmobFile(new File(photoPaths[1]));
                        file02.uploadblock(DeclareBookActivity.this, new UploadFileListener() {
                            @Override
                            public void onSuccess() {

                                photoUrl02 = file02.getFileUrl(DeclareBookActivity.this);
                                declareBook();
                            }

                            @Override
                            public void onFailure(int i, String s) {

                                isUpload = false;
                                if (dialog.isShowing()) dialog.dismiss();
                                ToastView toast = new ToastView(DeclareBookActivity.this, "图片上传失败：" + s);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        });
                    }
                    else{
                        declareBook();
                    }
                }

                @Override
                public void onFailure(int i, String s) {
                    isUpload = false;
                    if (dialog.isShowing()) dialog.dismiss();
                    ToastView toast = new ToastView(DeclareBookActivity.this, "图片上传失败：" + s);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            });

        }
        else{
            declareBook();
        }

    }


    /**
     * 上传电子书pdf
     */
    private void uploadPdfFile(){

        isUpload = true;

        final BmobFile pdfFile = new BmobFile(new File(pdfPath));
        pdfFile.uploadblock(this, new UploadFileListener() {
            @Override
            public void onSuccess() {

                Log.e("url", pdfFile.getFileUrl(DeclareBookActivity.this));
                pdfUrl = pdfFile.getFileUrl(DeclareBookActivity.this);

                isUpload = false;
            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
                ViewGroup.LayoutParams layoutParams = progress_bar.getLayoutParams();
                layoutParams.width = value * 5;
                progress_bar.setLayoutParams(layoutParams);
                progress_text.setText(value+"%");
            }

            @Override
            public void onFailure(int i, String s) {
                isUpload = false;
                ToastView toast = new ToastView(DeclareBookActivity.this, "电子书上传失败：" + s);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

    /**
     * 发布电子书资源
     */
    private void declareBook(){

        if (book_category == "其他") {
            book_category = book_other.getText().toString();
        }

        SharedPreferences sh = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);

        Book book = new Book();
        book.setDelcarerId(sh.getString("objectid",""));
        book.setTitle(book_title.getText().toString());
        book.setCategory(book_category);
        book.setCost(Integer.parseInt(book_price.getText().toString()));
        book.setPhoto01(photoUrl01);
        book.setPhoto02(photoUrl02);
        book.setBookUrl(pdfUrl);
        book.setPayTime(0);
        book.setScanTime(0);

        book.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                isUpload = false;
                AddCategorySum();
                if (dialog.isShowing()) dialog.dismiss();
                ToastView toast = new ToastView(DeclareBookActivity.this, "电子书发布成功");
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                isUpload = false;
                if (dialog.isShowing()) dialog.dismiss();
                ToastView toast = new ToastView(DeclareBookActivity.this, "电子书发布失败：" + s);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

    /**
     * 指定电子书类别数量加一
     */
    private void AddCategorySum(){

        BmobQuery<BookCategory> query = new BmobQuery<>();
        query.addWhereEqualTo("title", book_category);
        query.setLimit(1);
        query.findObjects(this, new FindListener<BookCategory>() {
            @Override
            public void onSuccess(List<BookCategory> list) {

                if (list.size() != 0) {

                    BookCategory category = list.get(0);
                    category.setSum(category.getSum() + 1);
                    category.update(DeclareBookActivity.this, category.getObjectId(), new UpdateListener() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });
                } else {
                    BookCategory category = new BookCategory();
                    category.setTitle(book_category);
                    category.setSum(1);
                    category.save(DeclareBookActivity.this, new SaveListener() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    /**
     * 打开文件管理器
     */
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult( Intent.createChooser(intent, "选择PDF文件"), MyConstant.FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            ToastView toast = new ToastView(DeclareBookActivity.this, "请先安装一个文件管理器");
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case ImageUtils.REQUEST_CODE_FROM_ALBUM: {

                if (resultCode == RESULT_CANCELED) {   //取消操作
                    return;
                }

                Uri imageUri = data.getData();
                if (which == 1){
                    uri01 = imageUri;
                    photo01.setImageURI(imageUri);
                }
                else{
                    uri02 = imageUri;
                    photo02.setImageURI(imageUri);
                }
                break;
            }
            case MyConstant.FILE_SELECT_CODE:

                if (resultCode == RESULT_OK) {

                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    pdfPath = UriUtils.getRealFilePath(this, uri);

                    String tip = "你确定要上传《"+ FileUtils.getFileName(pdfPath) + "》吗";

                    new AlertDialog.Builder(DeclareBookActivity.this).setTitle("提示")       //设置标题
                            .setMessage(tip)        //设置显示的内容
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    ViewGroup.LayoutParams layoutParams = upload_book.getLayoutParams();
                                    layoutParams.height = layoutParams.height * 2;
                                    upload_book.setLayoutParams(layoutParams);
                                    upload_progress.setVisibility(View.VISIBLE);

                                    uploadPdfFile();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();


                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        //Toast.makeText(this,categories[position]+"",Toast.LENGTH_SHORT).show();
        book_category = categories[position];
        if (position == 3){
            other_category.setVisibility(View.VISIBLE);
        }
        else{
            other_category.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
