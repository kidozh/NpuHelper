package com.kidozh.npuhelper.accountAuth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kidozh.npuhelper.R;

import org.ksoap2.serialization.SoapObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class personalInfoDisplayActivity extends AppCompatActivity {
    private String TAG = personalInfoDisplayActivity.class.getSimpleName();

    @BindView(R.id.personal_center_toolbar)
    Toolbar toolbar;
    @BindView(R.id.personal_center_fab)
    FloatingActionButton fab;
    @BindView(R.id.personal_center_info_recyclerview)
    RecyclerView mRecylerView;
    @BindView(R.id.personal_center_user_name)
    TextView mUserNameTextView;
    @BindView(R.id.personal_center_exit_btn)
    Button mLogoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info_display);
        ButterKnife.bind(this);
        toolbar.setTitle(getString(R.string.personal_center_text));
        setSupportActionBar(toolbar);
        configureStatusBar();

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Dev......", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        accountInfoBean accountInfo = loginUtils.getTokenInfoToLocal(this);
        // set adapter
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        // COMPLETED (41) Set the layoutManager on mRecyclerView
        mRecylerView.setLayoutManager(layoutManager);
        mRecylerView.setHasFixedSize(true);
        if(accountInfo == null){
            // just go to new page
            Intent intent = new Intent(this,LoginUniversityActivity.class);
            startActivity(intent);
        }

        mUserNameTextView.setText(accountInfo.name);

        setActionBar();

        personalInfoAdapter adapter = getPersonalInfoAdapter(this,accountInfo);
        mRecylerView.setAdapter(adapter);
        configureLoggoutBtn(this);

        new retrievePersonalInfoIdFromSchoolTask(this,adapter).execute();
    }

    private void configureStatusBar(){
        getWindow().setStatusBarColor(getColor(R.color.colorTurquoise));
        //getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
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



    private void configureLoggoutBtn(Context context){
        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.logout_account_notice)
                        .setPositiveButton(R.string.logout_account_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                loginUtils.clearAccountInfo(personalInfoDisplayActivity.this);
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.logout_account_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                // Show the dialog
                builder.show();



            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.logout_account_notice)
                        .setPositiveButton(R.string.logout_account_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                loginUtils.clearAccountInfo(personalInfoDisplayActivity.this);
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.logout_account_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                // Show the dialog
                builder.show();



            }
        });
    }

    private void setActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    public personalInfoAdapter getPersonalInfoAdapter(Context context, accountInfoBean accountInfo){
        personalInfoAdapter adapter = new personalInfoAdapter(context);
        List<personalInfo> personalInfoList = new ArrayList<>();
        if(accountInfo.role.equals("XS")){
            personalInfoList.add(new personalInfo(
                    R.drawable.vector_drawable_user,
                    context.getString(R.string.id_role),
                    context.getString(R.string.student)
            ));
        }
        if(accountInfo.accountNumber!=null){
            personalInfoList.add(new personalInfo(
                    R.drawable.vector_drawable_matric_number,
                    context.getString(R.string.account_label),
                    accountInfo.accountNumber
            ));
        }
        if(accountInfo.college!=null){
            personalInfoList.add(new personalInfo(
                    R.drawable.vector_drawable_college,
                    context.getString(R.string.college),
                    accountInfo.college
            ));
        }
        if(accountInfo.majorNumber!= null){
            personalInfoList.add(new personalInfo(
                    R.drawable.vector_drawable_major_code,
                    context.getString(R.string.major_code),
                    accountInfo.majorNumber
            ));
        }
        if(accountInfo.majorName!= null){
            personalInfoList.add(new personalInfo(
                    R.drawable.vector_drawable_major_name,
                    context.getString(R.string.major_name),
                    accountInfo.majorName
            ));
        }
        if(accountInfo.classNumber!= null){
            personalInfoList.add(new personalInfo(
                    R.drawable.vector_drawable_class,
                    context.getString(R.string.class_name),
                    accountInfo.classNumber
            ));
        }
        if(accountInfo.grade!= null){
            personalInfoList.add(new personalInfo(
                    R.drawable.vector_drawable_grade,
                    context.getString(R.string.grade),
                    accountInfo.classNumber
            ));
        }
        if(accountInfo.appToken!=null){
            personalInfoList.add(new personalInfo(
                    R.drawable.vector_drawable_app_token,
                    context.getString(R.string.app_token),
                    accountInfo.appToken
            ));
        }
        if(accountInfo.TGTticket!=null){
            personalInfoList.add(new personalInfo(
                    R.drawable.vector_drawable_ticket,
                    context.getString(R.string.TGT_ticket),
                    accountInfo.TGTticket
            ));
        }
        if(accountInfo.STticket!=null){
            personalInfoList.add(new personalInfo(
                    R.drawable.vector_drawable_ticket,
                    context.getString(R.string.ST_ticket),
                    accountInfo.STticket
            ));
        }
        adapter.mPersonalInfoList = personalInfoList;
        return adapter;


    }

    @SuppressLint("StaticFieldLeak")
    class retrievePersonalInfoIdFromSchoolTask extends AsyncTask<Void,Void,SoapObject>{
        Context mContext;
        personalInfoAdapter mAdapter;
        String username,apptoken;
        retrievePersonalInfoIdFromSchoolTask(Context context){
            this.mContext = context;
        }

        retrievePersonalInfoIdFromSchoolTask(Context context,personalInfoAdapter adapter){
            this.mContext = context;
            this.mAdapter = adapter;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            accountInfoBean accountInfo = userInfoUtils.getTokenInfoFromLocal(mContext);
            username = accountInfo.accountNumber;
            apptoken = accountInfo.appToken;

        }

        @Override
        protected SoapObject doInBackground(Void... voids) {
            SoapObject result = userInfoUtils.getDocumentIdResult(username,apptoken);
            return result;
        }

        @Override
        protected void onPostExecute(SoapObject soapObject) {
            super.onPostExecute(soapObject);
            if(soapObject!=null){
                String documentID = userInfoUtils.parseDocumentId(soapObject);
                if (documentID!=null && documentID.length()==0){
                    // clear the information
                    accountInfoBean accountInfo = new accountInfoBean();
                    loginUtils.saveTokenInfoToLocal(personalInfoDisplayActivity.this,accountInfo);
                    Toasty.error(mContext,mContext.getString(R.string.Error_in_getting_documentID), Toast.LENGTH_LONG).show();
                    finish();
                }
                else if(documentID == null){
                    showAppInvalidDialog();
                }
                else{
                    // save to SharedPreference
                    userInfoUtils.saveDocumentIDToLocal(mContext,documentID);
                    mAdapter.mPersonalInfoList.add(new personalInfo(
                            R.drawable.vector_drawable_finger_print,
                            mContext.getString(R.string.document_ID_name),
                            documentID
                    ));
                    mRecylerView.setAdapter(mAdapter);
                    new retrieveDetailedInfoTask(mContext,mAdapter,username,apptoken,documentID).execute();
                }



            }
            else{
                Toasty.error(mContext,mContext.getString(R.string.Error_in_getting_documentID), Toast.LENGTH_LONG).show();
            }



        }
    }

    private void showAppInvalidDialog(){
        loginUtils.clearAccountInfo(personalInfoDisplayActivity.this);

        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(this);
        normalDialog.setTitle(R.string.app_token_error_title);
        normalDialog.setMessage(R.string.app_token_demonstration);
        normalDialog.setPositiveButton(getString(R.string.i_get_it),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        finish();
                    }
                });

        normalDialog.show();
    }

    @SuppressLint("StaticFieldLeak")
    class retrieveDetailedInfoTask extends AsyncTask<Void,Void,SoapObject>{
        Context mContext;
        personalInfoAdapter mAdapter;
        String username,apptoken,documentId;
        retrieveDetailedInfoTask(Context context,personalInfoAdapter adapter, String username, String apptoken, String documentID){
            this.mContext = context;
            this.mAdapter = adapter;
            this.username = username;
            this.apptoken = apptoken;
            this.documentId = documentID;
        }



        @Override
        protected SoapObject doInBackground(Void... voids) {
            SoapObject result = userInfoUtils.getDocumentDetailResult(username,apptoken,documentId);
            return result;
        }

        @Override
        protected void onPostExecute(SoapObject soapObject) {
            super.onPostExecute(soapObject);
            int insertIdx = 3;
            Map<String, String> map = new HashMap<String, String>();
            map = userInfoUtils.parseDocumentDetail(soapObject);
            if(map != null){
                List<personalInfo> personalInfos = mAdapter.mPersonalInfoList;
                // Looking for college
                String college_key = "所属部门";
                String college_value = map.get(college_key);
                for(int i = 0; i<personalInfos.size();i++){
                    if(personalInfos.get(i).demonstrateText.equals( mContext.getString(R.string.college))){
                        personalInfos.get(i).value = college_value;
                    }
                }
                // them handle with ID..
                String ID_type = map.get("证件类型");
                String ID_value = map.get("证件号码");
                personalInfos.add(insertIdx,new personalInfo(
                        R.drawable.vector_drawable_key,
                        ID_type,
                        ID_value
                ));
                // interact with born date
                String birthday_value = map.get("出生日期");
                String pattern = "yyyy年MM月dd日";
                DateFormat format = new SimpleDateFormat(pattern, Locale.CHINA);
                try{
                    Date date = format.parse(birthday_value);
                    SimpleDateFormat customFormat =new SimpleDateFormat(mContext.getString(R.string.format_date_yy_mm_dd));
                    birthday_value = customFormat.format(date);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                personalInfos.add(insertIdx,new personalInfo(
                        R.drawable.vector_drawable_birthday,
                        mContext.getString(R.string.date_of_birth_text),
                        birthday_value
                ));

                personalInfos.add(insertIdx+1,new personalInfo(
                        R.drawable.vector_drawable_mobile,
                        mContext.getString(R.string.mobile_phone_text),
                        map.get("移动电话")
                ));
                mAdapter.mPersonalInfoList = personalInfos;
                mRecylerView.setAdapter(mAdapter);

                for (String key: map.keySet()) {

                    Log.i(TAG,"key "+key+" val "+map.get(key));
                }
            }
            else {
                Log.i(TAG,"Information MAP is null!");
            }

        }
    }
}
