package com.kidozh.npuhelper.accountAuth;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.kidozh.npuhelper.R;

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

    class loginAsyncTask extends AsyncTask<Void,Void,String>{

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
        }

        @Override
        protected String doInBackground(Void... voids) {
            String res = loginUtils.loginToGetToken(username,password);
            return res;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
            mLoginBtn.setEnabled(true);
            mLoginBtn.setText(R.string.sign_in_text);
        }
    }
}
