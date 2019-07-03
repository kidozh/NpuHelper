package com.kidozh.npuhelper.aboutApp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kidozh.npuhelper.R;
import com.kidozh.npuhelper.bbsService.bbsUtils;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class aboutAppActivity extends AppCompatActivity {
    private String TAG = aboutAppActivity.class.getSimpleName();

    @BindView(R.id.about_app_toolbar)
    Toolbar toolbar;
    @BindView(R.id.about_app_info_recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.about_app_progressBar)
    ProgressBar mAboutProgressBar;
    String latestVersion,latestVersionDescription,latestVersionURL;
    private OkHttpClient client = new OkHttpClient();
    aboutAppInfoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        ButterKnife.bind(this);
        configureToolbar();
        configureRecyclerview();

        new versionInfoUpdateTask().execute();


    }

    private void configureToolbar(){

        setSupportActionBar(toolbar);
        getWindow().setStatusBarColor(getColor(R.color.colorPrimary));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.about_app));
    }

    private void configureRecyclerview(){
        mRecyclerview.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerview.setLayoutManager(linearLayoutManager);
        adapter = new aboutAppInfoAdapter(this);
        adapter.mList = aboutAppUtils.getAppInfoList(this);
        mRecyclerview.setAdapter(adapter);
    }

    private class versionInfoUpdateTask extends AsyncTask<Void,Void,String>{
        Request request;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            request = new Request.Builder()
                    .url(aboutAppUtils.getAppVersionUrlString())
                    .build();
            mAboutProgressBar.setVisibility(View.VISIBLE);
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
            Log.d(TAG,"Get url "+s);
            if(s == null){
                Toasty.error(getApplicationContext(),getString(R.string.failed_to_connected_to_location_api), Toast.LENGTH_LONG).show();
                mAboutProgressBar.setVisibility(View.GONE);
                return;
            }
            try{
                JSONObject jsonObject = new JSONObject(s);
                latestVersion = jsonObject.getString("latest_version");
                latestVersionDescription = jsonObject.getString("latest_version_description_zh_CN");
                latestVersionURL = jsonObject.getString("latest_version_download_url");
                adapter.mList.get(1).infoValue = latestVersion;
                adapter.updateInfo = latestVersionDescription;
                adapter.updateUrl = latestVersionURL;
                adapter.notifyDataSetChanged();
                mAboutProgressBar.setVisibility(View.GONE);
            }
            catch (Exception e){
                e.printStackTrace();
                mAboutProgressBar.setVisibility(View.GONE);

            }
        }
    }

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
