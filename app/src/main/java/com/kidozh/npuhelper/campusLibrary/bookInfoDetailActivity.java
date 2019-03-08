package com.kidozh.npuhelper.campusLibrary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.kidozh.npuhelper.R;

import java.util.List;

public class bookInfoDetailActivity extends AppCompatActivity {
    final static String TAG = bookInfoDetailActivity.class.getSimpleName();
    @BindView(R.id.book_detail_title)
    TextView mBookDetailTitle;
    @BindView(R.id.book_detail_imageView)
    ImageView mBookDetailImage;
    @BindView(R.id.book_borrow_status_recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.book_information_recyclerview)
    RecyclerView mBookInfoRecyclerView;
    @BindView(R.id.book_detail_toolbar)
    Toolbar toolbar;
    @BindView(R.id.book_detail_toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.book_borrow_progressBar)
    ProgressBar mBorrowProgressbar;
    private final OkHttpClient client = new OkHttpClient();
    bookDetailItemInfoAdapter adapter = new bookDetailItemInfoAdapter();
    bookInfoUtils.bookBeam bookBeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info_detail);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        bookBeam = intent.getParcelableExtra("bookInfo");
        configureToolbar(bookBeam);
        configureStatusBar();
        configureRecyclerView();
        Log.d(TAG,"GET TOTAL BOOK : "+bookBeam.totalNumber);
        if(bookBeam.totalNumber>=1){
            new getBorrowStatusTask(this,bookBeam.marcRecNumber).execute();
        }
        setmBookDetail(bookBeam);
        new getBookDetailInfoTask(this,bookBeam.marcRecNumber).execute();


    }

    private void configureToolbar(bookInfoUtils.bookBeam bookBeam){
        toolbar.setTitle(bookBeam.title);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(bookBeam.title);

    }

    private void setmBookDetail(bookInfoUtils.bookBeam book){
        if(book.imgUrl == null || book.imgUrl.length() == 0){
            mBookDetailImage.setImageDrawable(getDrawable(R.drawable.vector_drawable_book));
        }
        else {
            Glide.with(this)
                    .load(book.imgUrl)
                    .transition(new DrawableTransitionOptions().crossFade())
                    .into(mBookDetailImage);
        }
        mBookDetailTitle.setText(book.title);

    }

    private void configureStatusBar(){
        //getWindow().setStatusBarColor(getColor(R.color.colorPeterRiver));
        toolbarLayout.setStatusBarScrimColor(getColor(R.color.colorPeterRiver));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

    }

    private void configureRecyclerView(){
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBookInfoRecyclerView.setHasFixedSize(true);
        mBookInfoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public class getBorrowStatusTask extends AsyncTask<Void,Void,String>{
        Context mContext;
        Request request;
        String marcNumber;
        getBorrowStatusTask(Context context, String marcNumber){
            this.mContext = context;
            this.marcNumber = marcNumber;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String api_url = bookInfoUtils.buildBookDetailStatusApi(marcNumber);
            request = new Request.Builder()
                    .url(api_url)
                    .build();
            mBorrowProgressbar.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(Void... voids) {
            try{
                Response response=client.newCall(request).execute();
                if(response.isSuccessful()){
                    return response.body().string();
                }
                else {
                    Log.d(TAG,"Get Wrong Resp "+response.code());
                    return null;
                }
            }
            catch (Exception e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
            mBorrowProgressbar.setVisibility(View.INVISIBLE);
            Log.d(TAG,"Recv book status "+s);
            if(s!= null){
                List<bookInfoUtils.bookBorrowStatus> bookBorrowStatusList = bookInfoUtils.parseBookBorrowInfo(s);
                bookBorrowStatusAdapter adapter = new bookBorrowStatusAdapter(mContext);
                adapter.bookBorrowStatusList = bookBorrowStatusList;
                mRecyclerView.setAdapter(adapter);
            }

        }
    }

    public class getBookDetailInfoTask extends AsyncTask<Void,Void,String>{
        Context mContext;
        Request request;
        String marcNumber;
        getBookDetailInfoTask(Context context, String marcNumber){
            this.mContext = context;
            this.marcNumber = marcNumber;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String api_url = bookInfoUtils.buildBookDetailInfoApi(marcNumber);
            request = new Request.Builder()
                    .url(api_url)
                    .build();

            //mBorrowProgressbar.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(Void... voids) {
            try{
                Response response=client.newCall(request).execute();
                if(response.isSuccessful()){
                    return response.body().string();
                }
                else {
                    Log.d(TAG,"Get Wrong Resp "+response.code());
                    return null;
                }
            }
            catch (Exception e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
            //mBorrowProgressbar.setVisibility(View.INVISIBLE);
            //Log.d(TAG,"Recv book status "+s);
            if(s!= null){
                List<bookInfoUtils.bookInfoItem> bookInfoItemList = bookInfoUtils.parseBookDetailInfo(s);

                adapter.bookInfoItemList = bookInfoItemList;
                mBookInfoRecyclerView.setAdapter(adapter);
                new getDoubanCommentTask(mContext,bookBeam.isbnNumber).execute();

            }

        }
    }

    public class getDoubanCommentTask extends AsyncTask<Void,Void,String>{
        Context mContext;
        Request request;
        String isbn;
        getDoubanCommentTask(Context context, String isbn){
            this.mContext = context;
            this.isbn = isbn;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String api_url = bookInfoUtils.buildBookCommentApi(isbn);
            request = new Request.Builder()
                    .url(api_url)
                    .build();

            //mBorrowProgressbar.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(Void... voids) {
            try{
                Response response=client.newCall(request).execute();
                if(response.isSuccessful()){
                    return response.body().string();
                }
                else {
                    Log.d(TAG,"Get Wrong Resp "+response.code());
                    return null;
                }
            }
            catch (Exception e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
            //mBorrowProgressbar.setVisibility(View.INVISIBLE);
            Log.d(TAG,"Recv comment status "+s);
            if(s!= null){
                List<bookInfoUtils.bookInfoItem> bookInfoItemList = bookInfoUtils.parseBookCommentInfo(mContext,s);
                //bookDetailItemInfoAdapter adapter = new bookDetailItemInfoAdapter();
                adapter.bookInfoItemList.remove(adapter.bookInfoItemList.size()-1);
                adapter.bookInfoItemList.addAll(bookInfoItemList);
                adapter.notifyDataSetChanged();

            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:   //返回键的id
                this.finish();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
