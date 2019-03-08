package com.kidozh.npuhelper.campusTransaction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;


import com.google.android.material.chip.Chip;
import com.kidozh.npuhelper.R;
import com.kidozh.npuhelper.accountAuth.accountInfoBean;
import com.kidozh.npuhelper.accountAuth.expensesRecordEntity;
import com.kidozh.npuhelper.accountAuth.loginUtils;
import com.kidozh.npuhelper.accountAuth.personalInfoDisplayActivity;
import com.kidozh.npuhelper.accountAuth.schoolCardUtils;
import com.kidozh.npuhelper.accountAuth.userInfoUtils;
import com.kidozh.npuhelper.campusTransaction.NormalDecoration;

import org.ksoap2.serialization.SoapObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.kidozh.npuhelper.accountAuth.schoolCardUtils.getBalanceDetailResult;
import static com.kidozh.npuhelper.accountAuth.schoolCardUtils.getCardInfoResult;
import static com.kidozh.npuhelper.accountAuth.schoolCardUtils.getChargeDetailResult;
import static com.kidozh.npuhelper.accountAuth.schoolCardUtils.getOCIDFromLocal;
import static com.kidozh.npuhelper.accountAuth.schoolCardUtils.parseCardInfoRes;
import static com.kidozh.npuhelper.accountAuth.schoolCardUtils.saveOCIDToLocal;

public class TransactionHistoryActivity extends AppCompatActivity {
    static final String TAG = TransactionHistoryActivity.class.getSimpleName();

    @BindView(R.id.campus_card_balance_number)
    TextView mCampusCardBalanceNumberTextView;
    @BindView(R.id.ocid_account_progressbar)
    ProgressBar mOCIDProgressBar;
    @BindView(R.id.transaction_record_recyclerview)
    RecyclerView mTransactionRecordRecylerview;
    @BindView(R.id.transaction_history_toolbar)
    Toolbar mTransactionToolbar;
    @BindView(R.id.campus_card_number)
    TextView mCampusCardNumberTV;
    @BindView(R.id.more_transaction_history_progressBar)
    ProgressBar mMoreTransactionProgressbar;

    Date lastQueryTimeStart;
    Boolean isLastQueryBack = true,isLastQuerySuccess = true;
    int lastIndex = 1;
    transactionHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);
        ButterKnife.bind(this);
        configureStatusBar();
        configureToolbar();
        adapter = new transactionHistoryAdapter(TransactionHistoryActivity.this);
        mTransactionRecordRecylerview.setAdapter(adapter);

        new getCardInfoTask(this,true).execute();
        configureRecyclerViewListener();
    }

    private void configureStatusBar(){
        getWindow().setStatusBarColor(getColor(R.color.colorBelizahole));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    void configureToolbar(){
        setSupportActionBar(mTransactionToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle(R.string.transaction_label);

    }

    void configureRecyclerViewListener(){
        mTransactionRecordRecylerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(isScrollAtEnd() && isLastQueryBack && isLastQuerySuccess){
                    Date endTime = lastQueryTimeStart;
                    // look back one month ago
                    Date startTime = new Date(endTime.getTime() - (long) 30 * 24 * 60 * 60 * 1000);
                    if(adapter.queriedEndTimeSet.contains(endTime)){
                        return;
                    }
                    else {

                        new getTransactionHistoryTask(getApplicationContext(),String.valueOf(lastIndex+1),"30",startTime,endTime).execute();
                    }

                }
            }

            public boolean isScrollAtEnd(){

                if (mTransactionRecordRecylerview.computeVerticalScrollExtent() + mTransactionRecordRecylerview.computeVerticalScrollOffset()
                        >= mTransactionRecordRecylerview.computeVerticalScrollRange()){
                    return true;
                }
                else {
                    return false;
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class getCardInfoTask extends AsyncTask<Void,Void,SoapObject> {
        @SuppressLint("StaticFieldLeak")
        private Context mContext;
        String apptoken,username;
        boolean continueToGetBalance = false;


        getCardInfoTask(Context context){
            this.mContext = context;
        }

        getCardInfoTask(Context context, Boolean continueToGetBalance){
            this.continueToGetBalance = continueToGetBalance;
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            accountInfoBean accountInfo = userInfoUtils.getTokenInfoFromLocal(mContext);
            apptoken = accountInfo.appToken;
            username = accountInfo.accountNumber;
            mOCIDProgressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected SoapObject doInBackground(Void... voids) {
            return getCardInfoResult(username,apptoken);
        }

        @Override
        protected void onPostExecute(SoapObject soapObject) {
            super.onPostExecute(soapObject);
            //Log.d(TAG,"Get card number info : "+soapObject.toString());
            schoolCardUtils.schoolCardInfoBean schoolCardInfo = null;
            try{
                schoolCardInfo = parseCardInfoRes(soapObject);
            }
            catch (Exception e){
                e.printStackTrace();
                Toasty.error(TransactionHistoryActivity.this,mContext.getString(R.string.connection_error_notice),Toast.LENGTH_LONG).show();

            }


            if (schoolCardInfo != null){
                saveOCIDToLocal(mContext,schoolCardInfo.cardNumber);

                if(continueToGetBalance){
                    // continue to get balance info
                    mOCIDProgressBar.setVisibility(View.GONE);
                    mCampusCardBalanceNumberTextView.setText(schoolCardInfo.balance);
                    mCampusCardNumberTV.setText(String.format(getString(R.string.card_number_format),schoolCardInfo.cardNumber));
                    new getTransactionHistoryTask(mContext).execute();

                }
            }
            else{
                if(transactionUtils.isAppTokenError(soapObject)){
                    showAppInvalidDialog();
                }

                mOCIDProgressBar.setVisibility(View.GONE);
                mCampusCardBalanceNumberTextView.setText(R.string.unknown);
                mCampusCardNumberTV.setText(R.string.app_token);
            }

        }
    }
    private void showAppInvalidDialog(){
        loginUtils.clearAccountInfo(TransactionHistoryActivity.this);
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(TransactionHistoryActivity.this);
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

    public class getTransactionHistoryTask extends AsyncTask<Void,Void,SoapObject> {
        Context mContext;
        String apptoken,username,ocidNumber;
        String index = "1",pageSize = "30";
        Date startTime,endTime;



        getTransactionHistoryTask(Context context){
            this.mContext = context;
            Date now = new Date();
            endTime = now;
            // 30 days before...
            startTime = new Date(endTime.getTime() - (long) 30 * 24 * 60 * 60 * 1000);

        }

        getTransactionHistoryTask(Context context, String index,String pageSize,Date startTime, Date endTime){
            this.mContext = context;
            this.index = index;
            this.pageSize = pageSize;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            accountInfoBean accountInfo = userInfoUtils.getTokenInfoFromLocal(mContext);
            apptoken = accountInfo.appToken;
            username = accountInfo.accountNumber;
            ocidNumber = getOCIDFromLocal(mContext);
            adapter.queriedEndTimeSet.add(endTime);
            isLastQueryBack = false;
            lastQueryTimeStart = this.startTime;
            lastIndex = Integer.parseInt(this.index);
            mMoreTransactionProgressbar.setVisibility(View.VISIBLE);

        }

        @Override
        protected SoapObject doInBackground(Void... voids) {


            return getBalanceDetailResult(apptoken,ocidNumber,index,pageSize,startTime,endTime);
        }

        @Override
        protected void onPostExecute(SoapObject soapObject) {
            super.onPostExecute(soapObject);
            Log.d(TAG,soapObject.toString());
            isLastQueryBack = true;
            mMoreTransactionProgressbar.setVisibility(View.INVISIBLE);
            // handle that
            List<expensesRecordEntity> expensesRecordEntityList = transactionUtils.parseExpensesInfo(soapObject);
            Log.d(TAG,"Get expenses record length:"+expensesRecordEntityList);
            if(expensesRecordEntityList != null && expensesRecordEntityList.size() !=0 ){
                //LinearLayoutManager layoutManager = new LinearLayoutManager(TransactionHistoryActivity.this, RecyclerView.VERTICAL, false);
                LinearLayoutManager layoutManager = new LinearLayoutManager(TransactionHistoryActivity.this);
                // COMPLETED (41) Set the layoutManager on mRecyclerView
                //mRecyclerView.addItemDecoration(decoration);

                //transactionHistoryAdapter adapter = new transactionHistoryAdapter(TransactionHistoryActivity.this);
                adapter.addExpensesRecordEntityList(expensesRecordEntityList);
                //adapter.expensesRecordEntityList = expensesRecordEntityList;
                //mTransactionRecordRecylerview.setHasFixedSize(true);
                NormalDecoration decoration = getRecyclerViewDecoration(mTransactionRecordRecylerview,adapter);
                if(mTransactionRecordRecylerview.getLayoutManager()== null){
                    mTransactionRecordRecylerview.setLayoutManager(layoutManager);
                }
                for (int i=0;i<mTransactionRecordRecylerview.getItemDecorationCount();i++){
                    mTransactionRecordRecylerview.removeItemDecorationAt(i);
                }

                mTransactionRecordRecylerview.addItemDecoration(decoration);


                // scroll if it's not the first time
                if (lastIndex != 1){
                    mTransactionRecordRecylerview.scrollToPosition(adapter.getItemCount()-expensesRecordEntityList.size());
                }

                new getChargeHistoryTask(mContext).execute();
            }
            else {
                Toasty.warning(TransactionHistoryActivity.this,getString(R.string.fail_to_get_balance_info_text), Toast.LENGTH_LONG).show();
                isLastQuerySuccess = false;
            }


        }
    }

    public class getChargeHistoryTask extends AsyncTask<Void,Void,SoapObject> {
        Context mContext;
        String apptoken,username,ocidNumber;
        String index = "1",pageSize = "5";
        Date startTime,endTime;



        getChargeHistoryTask(Context context){
            this.mContext = context;
            Date now = new Date();
            endTime = now;
            // 30 days before...
            startTime = new Date(endTime.getTime() - (long) 30 * 24 * 60 * 60 * 1000);
            this.index = String.valueOf(lastIndex);

        }

        getChargeHistoryTask(Context context, String index,String pageSize,Date startTime, Date endTime){
            this.mContext = context;
            this.index = index;
            this.pageSize = pageSize;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            accountInfoBean accountInfo = userInfoUtils.getTokenInfoFromLocal(mContext);
            apptoken = accountInfo.appToken;
            username = accountInfo.accountNumber;
            ocidNumber = getOCIDFromLocal(mContext);
            adapter.queriedEndTimeSet.add(endTime);
            isLastQueryBack = false;
            lastQueryTimeStart = this.startTime;
            lastIndex = Integer.parseInt(this.index);
            mMoreTransactionProgressbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected SoapObject doInBackground(Void... voids) {


            return getChargeDetailResult(apptoken,ocidNumber,index,pageSize,startTime,endTime);
        }

        @Override
        protected void onPostExecute(SoapObject soapObject) {
            super.onPostExecute(soapObject);
            Log.d(TAG,soapObject.toString());
            isLastQueryBack = true;
            mMoreTransactionProgressbar.setVisibility(View.INVISIBLE);
            // handle that
            List<expensesRecordEntity> expensesRecordEntityList = transactionUtils.parseExpensesInfo(soapObject);
            Log.d(TAG,"Get expenses record length:"+expensesRecordEntityList);
            if(expensesRecordEntityList != null && expensesRecordEntityList.size() !=0 ){
                //LinearLayoutManager layoutManager = new LinearLayoutManager(TransactionHistoryActivity.this, RecyclerView.VERTICAL, false);
                LinearLayoutManager layoutManager = new LinearLayoutManager(TransactionHistoryActivity.this);
                // COMPLETED (41) Set the layoutManager on mRecyclerView
                //mRecyclerView.addItemDecoration(decoration);

                //transactionHistoryAdapter adapter = new transactionHistoryAdapter(TransactionHistoryActivity.this);
                adapter.addExpensesRecordEntityList(expensesRecordEntityList);
                //adapter.expensesRecordEntityList = expensesRecordEntityList;
                //mTransactionRecordRecylerview.setHasFixedSize(true);
                NormalDecoration decoration = getRecyclerViewDecoration(mTransactionRecordRecylerview,adapter);
                if(mTransactionRecordRecylerview.getLayoutManager()== null){
                    mTransactionRecordRecylerview.setLayoutManager(layoutManager);
                }
                for (int i=0;i<mTransactionRecordRecylerview.getItemDecorationCount();i++){
                    mTransactionRecordRecylerview.removeItemDecorationAt(i);
                }


                mTransactionRecordRecylerview.addItemDecoration(decoration);


                // scroll if it's not the first time
                if (lastIndex != 1){
                    mTransactionRecordRecylerview.scrollToPosition(adapter.getItemCount()-expensesRecordEntityList.size());
                }
            }
            else {
                Toasty.warning(TransactionHistoryActivity.this,getString(R.string.fail_to_get_balance_info_text), Toast.LENGTH_LONG).show();
                isLastQuerySuccess = false;
            }


        }
    }

    public NormalDecoration getRecyclerViewDecoration(RecyclerView recyclerView,transactionHistoryAdapter adapter){

        final NormalDecoration decoration = new NormalDecoration(){

            @Override
            public String getHeaderName(int i) {
                return adapter.expensesRecordEntityList.get(i).getPayTimeMonth();
            }
        };
        decoration.setOnDecorationHeadDraw(new NormalDecoration.OnDecorationHeadDraw() {
            @Override
            public View getHeaderView(final int pos) {
                final View headView = LayoutInflater.from(TransactionHistoryActivity.this).inflate(R.layout.decoration_transaction_head_view, null);
                Chip chip = (Chip) headView.findViewById(R.id.select_time_chip);
                CardView cardView = (CardView) headView.findViewById(R.id.expenditure_cardview);
                int width = recyclerView.getRight() - recyclerView.getPaddingRight();
                cardView.setMinimumWidth(width);
                DateFormat df = DateFormat.getDateInstance(SimpleDateFormat.MEDIUM,Locale.getDefault());
                String curTimeString = df.format(adapter.expensesRecordEntityList.get(pos).payTime);
                chip.setText(curTimeString);
                TextView expenditureText = (TextView) headView.findViewById(R.id.expenditure_number);
                TextView incomeText = (TextView) headView.findViewById(R.id.income_number);

                //expenditureText.setText("UNK");
                // Looking for data
                float costMoney = 0;
                float chargeMoney = 0;

                for(int i = 0;i < adapter.expensesRecordEntityList.size();i++){
                    String timeString = df.format(adapter.expensesRecordEntityList.get(i).payTime);
                    if(timeString.equals(curTimeString)){
                        float number = adapter.expensesRecordEntityList.get(i).amount;
                        if(number < 0){
                            costMoney += number;
                        }
                        else {
                            chargeMoney+= number;
                        }

                    }

                }
                costMoney = Math.abs(costMoney);
                expenditureText.setText(String.format(Locale.getDefault(),"¥ %.2f",costMoney));
                incomeText.setText(String.format(Locale.getDefault(),"¥ %.2f",chargeMoney));

                return headView;
            }
        });
        return decoration;

    }
}
