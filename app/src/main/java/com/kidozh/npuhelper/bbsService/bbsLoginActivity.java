package com.kidozh.npuhelper.bbsService;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.kidozh.npuhelper.R;
import com.kidozh.npuhelper.utilities.okHttpUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class bbsLoginActivity extends AppCompatActivity implements loginForumFragment.OnFragmentInteractionListener {
    private static String TAG = bbsLoginActivity.class.getSimpleName();
    @BindView(R.id.bbs_personal_center_toolbar_title)
    TextView mBBSPersonalCenterToolbarTitle;
    @BindView(R.id.bbs_personal_center_toolbar)
    Toolbar toolbar;
    @BindView(R.id.bbs_login_fragment_username_editText)
    EditText mUsernameEditText;
    @BindView(R.id.bbs_login_fragment_password_edittext)
    EditText mPasswordEditText;
    @BindView(R.id.bbs_login_fragment_login_btn)
    Button mBBSLoginByPwdBtn;
    @BindView(R.id.bbs_login_fragment_login_by_official_btn)
    Button mBBSLoginByOfficialBtn;
    private OkHttpClient client;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bbs_personal_center);
        ButterKnife.bind(this);

        configureToolbar();
        configureLoginByPwdBtn();
        context = getApplicationContext();


        if(false){
            // directly personal center
        }
        else {
            //login

            //fragmentTransaction.commit();
        }
    }

    private void configureToolbar(){
        setSupportActionBar(toolbar);
        getWindow().setStatusBarColor(getColor(R.color.colorPrimary));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mBBSPersonalCenterToolbarTitle.setText(R.string.bbs_personal_center);
    }

    public void configureLoginByPwdBtn(){
        mBBSLoginByPwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputUsername = mUsernameEditText.getText().toString();
                String inputPassword = mPasswordEditText.getText().toString();
                Log.d(TAG,"You press"+inputUsername+" USR "+mPasswordEditText.getText().toString());
                if(inputPassword.length() == 0 || inputUsername.length() == 0){
                    Toasty.warning(getApplicationContext(),getString(R.string.account_or_password_required), Toast.LENGTH_SHORT).show();

                }
                else {
                    new loginByUserPwdTask(inputUsername,inputPassword).execute();
                }

            }
        });
    }


    public class loginByUserPwdTask extends AsyncTask<Void,Void,Boolean> {

        Request request;
        String username,password;

        loginByUserPwdTask(String username,String password){
            this.username = username;
            this.password= password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            client = okHttpUtils.getUnsafeOkHttpClientWithCookieJar(getApplicationContext());
            // construct request
            String url = bbsUtils.getLoginUrl();
            Log.d(TAG,"Browse Login Url ->"+url);

            FormBody formBody = new FormBody.Builder()
                    .add("fastloginfield", "username")
                    .add("cookietime", "2592000")
                    .add("username", username)
                    .add("password", password)
                    .add("quickforward", "yes")
                    .add("handlekey", "1s")
                    .build();
            request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try{
                Response response = client.newCall(request).execute();
                if(response.isSuccessful()){
                    // store cookie
                    if(response.body() !=null){
                        String content = response.body().string();
                        Log.d(TAG,"get response body "+content);

                        if(!content.contains("username")){
                            return false;
                        }
                        else {
                            // successful grab information
                            Headers headers=response.headers();
                            List<String> cookies=headers.values("Set-Cookie");
                            if(cookies.size()>0){
                                for(int i=0;i<cookies.size();i++){
                                    String session = cookies.get(i);
                                    Log.d(TAG,"Cookie results:"+session+" Size: "+cookies.size());
                                }
                            }


                            return true;
                        }
                    }


                    return true;
                }
                else {
                    return false;
                }
            }
            catch (Exception e){
                e.printStackTrace();
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean){
                Toasty.success(getApplicationContext(),getString(R.string.login_successful_text), Toast.LENGTH_LONG).show();
                finish();
                finish();
            }
            else {
                Toasty.error(getApplicationContext(),getString(R.string.wrong_acc_or_pwd), Toast.LENGTH_LONG).show();
                // clear the cookie
                new SharedPrefsCookiePersistor(context).clear();
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.bbs_toolbar_personal_center_item) {
            Intent intent = new Intent(this, bbsLoginActivity.class);
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


    // login forum fragment
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
