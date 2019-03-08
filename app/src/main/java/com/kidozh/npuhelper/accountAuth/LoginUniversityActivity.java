package com.kidozh.npuhelper.accountAuth;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kidozh.npuhelper.R;

import org.ksoap2.serialization.SoapObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class LoginUniversityActivity extends AppCompatActivity {
    @BindView(R.id.login_btn)
    Button mLoginBtn;
    @BindView(R.id.login_toolbar)
    Toolbar toolbar;
    @BindView(R.id.account_edit_text)
    EditText mAccountEditText;
    @BindView(R.id.password_edit_text)
    EditText mPasswordEditText;
    @BindView(R.id.login_toolbar_textview)
    TextView mLoginToolbarTextview;
    @BindView(R.id.login_progressbar)
    ProgressBar mLoginProgressbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_university);
        ButterKnife.bind(this);
        configureActionBar();


        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = mAccountEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();
                if (account.length()!=0 && password.length()!=0){
                    new loginAsyncTask(account,password).execute();
                }
                else {
                    Toasty.info(LoginUniversityActivity.this,getString(R.string.account_or_password_required),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == android.R.id.home){
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void configureActionBar(){
        toolbar.setTitle(R.string.sign_in_text);
        mLoginToolbarTextview.setText(R.string.sign_in_text);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    class loginAsyncTask extends AsyncTask<Void,Void,SoapObject>{

        String username,password;
        loginAsyncTask(String username,String password){
            this.username = username;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoginBtn.setEnabled(false);
            mLoginBtn.setText(R.string.sign_in_now_text);
            mLoginProgressbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected SoapObject doInBackground(Void... voids) {
            SoapObject res = loginUtils.loginToGetToken(username,password);
            return res;
        }

        @Override
        protected void onPostExecute(SoapObject s) {

            super.onPostExecute(s);
            if(loginUtils.isUserSignedSuccessfully(s)){
                // pass
                accountInfoBean accountInfo = loginUtils.parseSuccessfulXMLText(s);
                if (accountInfo!= null){
                    loginUtils.saveTokenInfoToLocal(LoginUniversityActivity.this,accountInfo);
                    loginUtils.savePasswordInfoToLocal(LoginUniversityActivity.this,this.password);
                    Toasty.success(LoginUniversityActivity.this,getString(R.string.login_successful_text),Toast.LENGTH_LONG).show();
                    finish();
                }else {
                    Toasty.info(LoginUniversityActivity.this,getString(R.string.wrong_acc_or_pwd),Toast.LENGTH_LONG).show();
                }


            }
            else {
                Toasty.warning(LoginUniversityActivity.this,getString(R.string.wrong_acc_or_pwd),Toast.LENGTH_LONG).show();
            }
            mLoginBtn.setEnabled(true);
            mLoginBtn.setText(R.string.sign_in_text);
            mLoginProgressbar.setVisibility(View.INVISIBLE);
        }
    }
}
