package com.kidozh.npuhelper.bbsService;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestOptions;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.kidozh.npuhelper.R;
import com.kidozh.npuhelper.accountAuth.loginUtils;
import com.kidozh.npuhelper.accountAuth.personalInfoDisplayActivity;
import com.kidozh.npuhelper.utilities.okHttpUtils;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.kidozh.npuhelper.utilities.okHttpUtils.getUnsafeOkHttpClient;

public class bbsPersonalInfoActivity extends AppCompatActivity {
    private final static String TAG = bbsPersonalInfoActivity.class.getSimpleName();

    @BindView(R.id.bbs_personal_info_toolbar)
    Toolbar toolbar;
    @BindView(R.id.bbs_personal_info_toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.bbs_personal_info_username)
    TextView mUsername;
    @BindView(R.id.bbs_personal_info_avatar)
    ImageView mAvatarImageview;
    @BindView(R.id.bbs_personal_center_read_perm)
    TextView mUserReadPerm;
    @BindView(R.id.bbs_personal_center_thread_recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.bbs_personal_info_thread_recyclerview)
    RecyclerView mPersonalThreadRecyclerview;

    private bbsPersonalNoticeAdapter personalNoticeAdapter;
    private bbsForumThreadAdapter bbsForumThreadAdapter;
    private OkHttpClient client;
    private int page=1;
    private boolean isOver = false;
    private boolean isFirstActivity = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bbs_personal_info);
        ButterKnife.bind(this);
        configureUnsafeJSONClient();
        configureToolbar();
        configureRecyclerview();
        new getBBSPersonalInfoTask(this).execute();
    }

    private void configureUnsafeJSONClient(){
        client = okHttpUtils.getUnsafeOkHttpClientWithCookieJar(this);
    }

    private void configureToolbar(){
        setSupportActionBar(toolbar);
//        getWindow().setStatusBarColor(getColor(R.color.colorPrimary));
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    private void configureRecyclerview(){
        mRecyclerview.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this,4);
        mRecyclerview.setLayoutManager(layoutManager);
        personalNoticeAdapter = new bbsPersonalNoticeAdapter(this);
        mRecyclerview.setAdapter(personalNoticeAdapter);
        mPersonalThreadRecyclerview.setHasFixedSize(true);
        mPersonalThreadRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        bbsForumThreadAdapter = new bbsForumThreadAdapter(this,"","4");
        mPersonalThreadRecyclerview.setAdapter(bbsForumThreadAdapter);

    }

    public class getBBSPersonalInfoTask extends AsyncTask<Void,Void,String>{
        Request request;
        Context mContext;

        getBBSPersonalInfoTask(Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            request = new Request.Builder()
                    .url(bbsUtils.getUserThreadUrl(page))
                    .build();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try{
                Response response = client.newCall(request).execute();
                if(response.isSuccessful()&& response.body()!=null){
                    return response.body().string();
                }
                else {
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

            // parse personInfo
            bbsUtils.bbsPersonInfo bbsPersonInfo = bbsUtils.parsePersonInfo(s);
            if(bbsPersonInfo!=null){
                Log.d(TAG,"get auth info"+bbsPersonInfo.auth+" Len "+bbsPersonInfo.auth.length());
            }

            if(bbsPersonInfo == null || bbsPersonInfo.auth == null || bbsPersonInfo.auth.equals("null") || bbsPersonInfo.auth.length() == 0){
                // no auth need to relogin
                // need to refresh...

                mUsername.setText(R.string.unknown);
                mUserReadPerm.setText(R.string.unknown);
                mAvatarImageview.setImageDrawable(getDrawable(R.drawable.avatar_5));
                if(isFirstActivity){
                    Toasty.info(mContext,mContext.getString(R.string.bbs_require_login), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(mContext, bbsLoginActivity.class);
                    isFirstActivity = false;
                    startActivity(intent);
                }
                else {
                    finish();
                }

            }
            else {
                toolbarTitle.setText(bbsPersonInfo.username);
                mUsername.setText(bbsPersonInfo.username);
                mUserReadPerm.setText(bbsPersonInfo.readPerm);
                OkHttpUrlLoader.Factory factory = new OkHttpUrlLoader.Factory(client);
                Glide.get(mContext).getRegistry().replace(GlideUrl.class, InputStream.class,factory);
                Glide.with(mContext)
                        .load(bbsUtils.getSmallAvatarUrlByUid(bbsPersonInfo.uid))
                        .apply(RequestOptions.placeholderOf(R.drawable.avatar_1).error(R.drawable.avatar_1))
                        .into(mAvatarImageview);
                // handle thread
                bbsForumThreadAdapter.setThreadInfoList(bbsUtils.parsePersonalThreadListInfo(s),null);

            }
            // parse notification
            List<bbsUtils.bbsNotification> bbsNotificationList = bbsUtils.parseNotificationInfo(s);
            if(s!=null){
                Log.d(TAG,"return info" + s);
                page += 1;
                personalNoticeAdapter.mList = bbsNotificationList;
                personalNoticeAdapter.notifyDataSetChanged();

            }
            else {
                // require to login
                Toasty.info(mContext,mContext.getString(R.string.bbs_require_login), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(mContext, bbsLoginActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bbs_personal_center_toolbar_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        configureUnsafeJSONClient();
        configureToolbar();
        configureRecyclerview();
        new getBBSPersonalInfoTask(this).execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.bbs_toolbar_personal_center_logout_item) {
            // logout ...
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.logout_account_notice)
                    .setPositiveButton(R.string.logout_account_yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // clear cookie first..
                            new SharedPrefsCookiePersistor(getApplicationContext()).clear();
                            Intent intent = new Intent(getApplicationContext(), bbsLoginActivity.class);
                            startActivity(intent);
                            //finish();
                        }
                    })
                    .setNegativeButton(R.string.logout_account_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Show the dialog
            builder.show();

            return true;
        }
        else if(id == android.R.id.home){
            this.finish();
            return false;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }
}
