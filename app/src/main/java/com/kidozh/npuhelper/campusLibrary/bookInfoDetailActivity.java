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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kidozh.npuhelper.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class bookInfoDetailActivity extends AppCompatActivity implements bookDetailShowOptionFragment.onSettingsAppliedListener {
    final static String TAG = bookInfoDetailActivity.class.getSimpleName();
    @BindView(R.id.book_detail_title)
    TextView mBookDetailTitle;

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
    @BindView(R.id.book_detail_filter_floatingActionButton)
    FloatingActionButton mFilterBtn;
    @BindView(R.id.book_borrow_search_item)
    TextView mBookBorrowSearchText;
    @BindView(R.id.book_borrow_search_filter_size)
    TextView mBookBorrowSearchFilterSize;
    @BindView(R.id.book_borrow_search_total_book)
    TextView mBookBorrowSearchTotalSize;

    private final OkHttpClient client = new OkHttpClient();
    bookDetailItemInfoAdapter adapter = new bookDetailItemInfoAdapter();
    bookInfoUtils.bookBeam bookBeam;
    List<bookInfoUtils.bookBorrowStatus> bookBorrowStatusList;

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
        configureFloatButton();
        Log.d(TAG,"GET TOTAL BOOK : "+bookBeam.totalNumber);
        if(bookBeam.totalNumber>=1){
            getBorrowStatusTask task = new getBorrowStatusTask(this,bookBeam.marcRecNumber);
            task.execute();
            bookBorrowStatusList = task.bookBorrowStatusList;
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

    private void configureFloatButton(){
        mFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new bookDetailShowOptionFragment().show(getSupportFragmentManager(), "dialog");
            }
        });
    }

    private String getCampusCode(){
        Context context = this;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context) ;
        String selectLibraryCampus = prefs.getString(context.getString(R.string.pref_key_book_select_campus),"follow");
        if(selectLibraryCampus.equals("follow")){
            return prefs.getString(getString(R.string.pref_key_location_selection),"y");
        }
        else if(selectLibraryCampus.equals("all")) {
            return "";
        }
        else {
            return selectLibraryCampus;
        }
    }

    private Boolean getBookAccessible(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this) ;
        return prefs.getBoolean(getString(R.string.pref_key_show_accessible_book),true);
    }

    protected List<bookInfoUtils.bookBorrowStatus> filterBookInfoBySettings(List<bookInfoUtils.bookBorrowStatus> mList){
        Context context = this;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context) ;
        String selectLibraryCampus = prefs.getString(context.getString(R.string.pref_key_book_select_campus),"follow");
        String campus = "";
        if(selectLibraryCampus.equals("follow")){
            campus = prefs.getString(getString(R.string.pref_key_location_selection),"y");
        }
        else if(selectLibraryCampus.equals("all")) {
            campus = "";
        }
        else {
            campus = selectLibraryCampus;
        }
        Boolean onlyShowAccessBook = prefs.getBoolean(context.getString(R.string.pref_key_show_accessible_book),true);
        List<bookInfoUtils.bookBorrowStatus> filtered = new ArrayList<>();
        for(int i=0; i<mList.size();i++){
            // check
            bookInfoUtils.bookBorrowStatus bookBorrowStatus = mList.get(i);
            Log.d(TAG,"Raw campus "+campus+" location "+bookBorrowStatus.location);
            if(onlyShowAccessBook){
                // check the accessibility
                if(!bookBorrowStatus.isAccessible){
                    // throw data
                    continue;
                }
            }
            // check campus
            if(campus.equals("y")){
                if(!bookBorrowStatus.location.contains("友谊校区")){
                    continue;
                }
            }
            if(campus.equals("c")){
                if(!bookBorrowStatus.location.contains("长安校区")){
                    continue;
                }
            }
            Log.d(TAG,"Add campus "+campus+" location "+bookBorrowStatus.location);
            filtered.add(bookBorrowStatus);

        }

        return filtered;
    }

    public class getBorrowStatusTask extends AsyncTask<Void,Void,String>{
        Context mContext;
        Request request;
        String marcNumber;
        List<bookInfoUtils.bookBorrowStatus> bookBorrowStatusList;
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
            String selectedCampusCode = getCampusCode();
            Boolean onlyListAccessibleBook = getBookAccessible();
            if(selectedCampusCode.equals("")){
                if(!onlyListAccessibleBook) mBookBorrowSearchText.setText(R.string.not_limit_accessibility);
                else mBookBorrowSearchText.setText(R.string.only_accessible_book_is_listed);
            }
            else if(selectedCampusCode.equals("y")){
                if(!onlyListAccessibleBook){
                    mBookBorrowSearchText.setText(
                            String.format(getString(R.string.book_search_text_format),
                                    getString(R.string.youyi_campus_name_full),
                                    getString((R.string.not_limit_accessibility))
                            )
                    );
                }
                else {
                    mBookBorrowSearchText.setText(
                            String.format(getString(R.string.book_search_text_format),
                                    getString(R.string.youyi_campus_name_full),
                                    getString((R.string.only_accessible_book_is_listed))
                            )
                    );
                }

            }

            else if(selectedCampusCode.equals("c")){
                if(!onlyListAccessibleBook){
                    mBookBorrowSearchText.setText(
                            String.format(getString(R.string.book_search_text_format),
                                    getString(R.string.changan_campus_name_full),
                                    getString((R.string.not_limit_accessibility))
                            )
                    );
                }
                else {
                    mBookBorrowSearchText.setText(
                            String.format(getString(R.string.book_search_text_format),
                                    getString(R.string.changan_campus_name_full),
                                    getString((R.string.only_accessible_book_is_listed))
                            )
                    );
                }

            }
            else {
                mBookBorrowSearchText.setText(R.string.unknown);
            }
            mBookBorrowSearchFilterSize.setText(R.string.unknown);
            mBookBorrowSearchTotalSize.setText(R.string.unknown);
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
                bookBorrowStatusList = bookInfoUtils.parseBookBorrowInfo(s);
                bookBorrowStatusAdapter adapter = new bookBorrowStatusAdapter(mContext);
                // filter by setting
                List<bookInfoUtils.bookBorrowStatus> filtered = filterBookInfoBySettings(bookBorrowStatusList);
                Log.d(TAG,"Filtered Size "+filtered.size());
                adapter.bookBorrowStatusList = filtered;
                mRecyclerView.setAdapter(adapter);
                mBookBorrowSearchFilterSize.setText(String.format(Locale.getDefault(),"%d",filtered.size()));
                mBookBorrowSearchTotalSize.setText(String.format(Locale.getDefault(),"%d",bookBorrowStatusList.size()));
            }

        }
    }

    public class getBookDetailInfoTask extends AsyncTask<Void,Void,String>{
        Context mContext;
        Request request;
        String marcNumber;
        List<bookInfoUtils.bookInfoItem> bookInfoItemList;
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
                bookInfoItemList = bookInfoUtils.parseBookDetailInfo(s);

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

    @Override
    public void onSettingApplied() {
        new getBorrowStatusTask(this,bookBeam.marcRecNumber).execute();
    }
}
