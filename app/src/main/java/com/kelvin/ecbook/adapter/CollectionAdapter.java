package com.kelvin.ecbook.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kelvin.ecbook.R;
import com.kelvin.ecbook.activity.BookDetailActivity;
import com.kelvin.ecbook.config.CollectionType;
import com.kelvin.ecbook.config.MyConstant;
import com.kelvin.ecbook.fragment.CollectionFragment;
import com.kelvin.ecbook.model.Book;
import com.kelvin.ecbook.model.Collection;
import com.kelvin.ecbook.utils.ImageLoadOptions;
import com.kelvin.ecbook.view.toast.ToastView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.GetListener;

/**
 * Created by Kelvin on 2016/5/3.
 */
public class CollectionAdapter extends BaseAdapter {

    private Context mContext;
    private List<Collection> mData;
    private Book[] books;
    private int type;

    public CollectionAdapter(Context context,List<Collection> mData,int type){

        this.mContext = context;
        this.mData = mData;
        this.books = new Book[mData.size()];
        this.type = type;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Collection getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final View view = LayoutInflater.from(mContext).inflate(R.layout.collection_cell, null);

        RelativeLayout bottom_view = (RelativeLayout) view.findViewById(R.id.bottom_view);
        if (position == mData.size()-1){
            bottom_view.setVisibility(View.VISIBLE);
        }
        else {
            bottom_view.setVisibility(View.GONE);
        }

        BmobQuery<Book> query = new BmobQuery<>();
        query.getObject(mContext, mData.get(position).getBook(), new GetListener<Book>() {
            @Override
            public void onSuccess(Book model) {
                books[position] = model;

                TextView cost = (TextView)view.findViewById(R.id.shop_car_item_total);
                cost.setText("EC币：" + books[position].getCost());

                if (!TextUtils.isEmpty(books[position].getPhoto01())) {
                    ImageView cover = (ImageView) view.findViewById(R.id.shop_car_item_image);
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
                    imageLoader.displayImage(books[position].getPhoto01(), cover,
                            ImageLoadOptions.getOptions());
                }

                TextView title = (TextView)view.findViewById(R.id.shop_car_item_text);
                title.setText(books[position].getTitle());

                TextView desc = (TextView)view.findViewById(R.id.shop_car_item_property);
                desc.setText("类别：" + books[position].getCategory() + "   下载量：" + books[position].getPayTime());

                Button remove = (Button)view.findViewById(R.id.collection_remove);
                if (type == CollectionType.COLLECTION) {     //收藏夹
                    remove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            new AlertDialog.Builder(mContext).setTitle("提示")       //设置标题
                                    .setMessage("你确定要删除《" + books[position].getTitle() + "》这个收藏吗？")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            mData.get(position).delete(mContext, mData.get(position).getObjectId(),
                                                    new DeleteListener() {
                                                        @Override
                                                        public void onSuccess() {
                                                            ToastView toast = new ToastView(mContext, "删除成功");
                                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                                            toast.show();

                                                            CollectionFragment.collections.remove(position);
                                                            CollectionFragment.adapter.notifyDataSetChanged();

                                                            if (CollectionFragment.collections.size() == 0) {
                                                                CollectionFragment.top_view.setVisibility(View.GONE);
                                                                CollectionFragment.no_collections.setVisibility(View.VISIBLE);
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(int i, String s) {
                                                            ToastView toast = new ToastView(mContext, "删除失败：" + s);
                                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                                            toast.show();
                                                        }
                                                    });
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).show();
                        }
                    });
                }
                else if (type == CollectionType.MYUPLOAD){     //我上传的
                    remove.setText("撤回");
                    remove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new AlertDialog.Builder(mContext).setTitle("提示")       //设置标题
                                    .setMessage("你确定要撤回《" + books[position].getTitle() + "》的发布吗，此行为将会扣除3个EC币？")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            Book book = new Book();
                                            book.delete(mContext, mData.get(position).getBook(), new DeleteListener() {
                                                @Override
                                                public void onSuccess() {
                                                    ToastView toast = new ToastView(mContext, "撤回成功");
                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                    toast.show();
                                                }

                                                @Override
                                                public void onFailure(int i, String s) {
                                                    ToastView toast = new ToastView(mContext, "撤回失败：" + s);
                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                    toast.show();
                                                }
                                            });
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).show();
                        }
                    });
                }
                else if (type == CollectionType.MYDOWNLOAD){
                    //remove.setVisibility(View.GONE);
                    remove.setText("打开");
                    remove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String path =  MyConstant.BookDir + books[position].getTitle() + ".pdf";
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                            Uri uri = Uri.fromFile(new File(path));
                            intent.setDataAndType(uri, "application/pdf");
                            mContext.startActivity(intent);
                        }
                    });
                }


                LinearLayout book_content = (LinearLayout) view.findViewById(R.id.book_content);
                book_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(mContext, BookDetailActivity.class);
                        intent.putExtra("bookId",books[position].getObjectId());
                        mContext.startActivity(intent);
                    }
                });

            }

            @Override
            public void onFailure(int i, String s) {

            }
        });

        return view;
    }
}
