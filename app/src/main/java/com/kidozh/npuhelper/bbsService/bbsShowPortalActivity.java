package com.kidozh.npuhelper.bbsService;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.kidozh.npuhelper.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.kidozh.npuhelper.utilities.okHttpUtils.getUnsafeOkHttpClient;

public class bbsShowPortalActivity extends AppCompatActivity {
    private static String TAG = bbsShowPortalActivity.class.getSimpleName();
    @BindView(R.id.bbs_portal_appbar)
    AppBarLayout appBarLayout;
    @BindView(R.id.bbs_portal_toolbar)
    Toolbar toolbar;
    @BindView(R.id.bbs_portal_recyclerview)
    RecyclerView portalRecyclerView;
    @BindView(R.id.bbs_portal_cardview)
    CardView mErrorCardview;
    @BindView(R.id.bbs_portal_error_text)
    TextView mBBSPortalErrorText;
    private OkHttpClient client = new OkHttpClient();
    bbsShowPortalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bbs_show_portal);
        ButterKnife.bind(this);
        configureClient();
        configurePortalRecyclerview();
        configureToolbar();
        new getPortalInfoTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }



    private void configureClient(){
        client = getUnsafeOkHttpClient();
    }

    private void configureToolbar(){
        toolbar.setTitle(getString(R.string.npu_bbs));
        setSupportActionBar(toolbar);
        getWindow().setStatusBarColor(getColor(R.color.colorCloud));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void configurePortalRecyclerview(){
        portalRecyclerView.setHasFixedSize(true);
        portalRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new bbsShowPortalAdapter(this,null);
        portalRecyclerView.setAdapter(adapter);
    }

    private class getPortalInfoTask extends AsyncTask<Void,Void,String>{
        Context mContext;
        Request request;

        getPortalInfoTask(Context context){
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            request = new Request.Builder()
                    .url(bbsUtils.getBBSForumInfoApi())
                    .build();
            Log.d(TAG,"API ->"+bbsUtils.getBBSForumInfoApi());


        }

        @Override
        protected String doInBackground(Void... voids) {
            try{
                Response resp = client.newCall(request).execute();
                if(resp.isSuccessful() && resp.body()!=null){
                    return resp.body().string();
                }
                else {
                    return null;
                }
            }
            catch (Exception e){
                e.printStackTrace();
                //Log.e(TAG,e.printStackTrace());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG,"Info:"+s);
            if(s == null){
                // Make notifications
                Toasty.error(mContext,getString(R.string.bbs_browse_failed), Toast.LENGTH_LONG).show();
                mErrorCardview.setVisibility(View.VISIBLE);
            }
            else {

                List<bbsUtils.categorySectionFid> categorySectionFidList = bbsUtils.parseCategoryFids(s);
                if(categorySectionFidList!=null){
                    adapter.jsonString = s;
                    adapter.setmCateList(categorySectionFidList);
                    Log.d(TAG,"CATE:"+categorySectionFidList.size());
                }
                else {
                    mErrorCardview.setVisibility(View.VISIBLE);
                    String error = bbsUtils.parseErrorInfo(s);
                    Log.d(TAG,"error Info"+error);
                    if(error !=null){
                        Spanned sp = Html.fromHtml(error);
                        SpannableString spannableString = new SpannableString(sp);
                        mBBSPortalErrorText.setText(spannableString, TextView.BufferType.SPANNABLE);

                    }

                    Toasty.error(mContext,getString(R.string.bbs_browse_failed), Toast.LENGTH_LONG).show();
                }

            }


        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d(TAG,"You press "+id);
        if (id == R.id.bbs_toolbar_personal_center_item) {
            Intent intent = new Intent(this, bbsPersonalInfoActivity.class);
            startActivity(intent);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bbs_toolbar_menu, menu);
        return true;
    }

}
