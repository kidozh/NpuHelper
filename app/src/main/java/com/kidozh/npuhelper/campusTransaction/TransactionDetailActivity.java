package com.kidozh.npuhelper.campusTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.kidozh.npuhelper.R;
import com.kidozh.npuhelper.accountAuth.expensesRecordEntity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransactionDetailActivity extends AppCompatActivity {
    @BindView(R.id.transaction_detail_status)
    TextView mTransactionDetailStatus;
    @BindView(R.id.transaction_detail_precise_location)
    TextView mPreciseLocation;
    @BindView(R.id.transaction_detail_number)
    TextView mTransactionDetailNumber;
    @BindView(R.id.transaction_detail_type_imageview)
    ImageView mTransactionRecordImageView;
    @BindView(R.id.transaction_detail_title_textview)
    TextView mTransactionDetailTitle;
    @BindView(R.id.transaction_detail_type)
    TextView mSecondaryLocation;
    @BindView(R.id.transaction_detail_place)
    TextView mGeneralLocation;
    @BindView(R.id.transaction_detail_balance)
    TextView mTransactionDetailBalance;
    @BindView(R.id.transaction_detail_time)
    TextView mTransactionDetailTime;
    @BindView(R.id.transaction_detail_toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        //Parcelable 反序列化
        expensesRecordEntity entity = extras.getParcelable("expensesRecord");
        if(entity == null){
            finish();
            return;
        }

        String location = entity.location;
        String[] locationDetail = location.split("\\\\");
        if(location.equals("null")){
            mPreciseLocation.setText(R.string.unknown_deduction);
            mSecondaryLocation.setText(R.string.unknown);
            mGeneralLocation.setText(R.string.unknown);
        }
        else if(locationDetail.length == 3){
            mPreciseLocation.setText(locationDetail[2]);
            mSecondaryLocation.setText(locationDetail[1]);
            mGeneralLocation.setText(locationDetail[0]);

        }
        else if(locationDetail.length == 2){
            mPreciseLocation.setText(locationDetail[1]);
            mSecondaryLocation.setText(locationDetail[0]);
            mGeneralLocation.setVisibility(View.GONE);

        }
        else {
            mPreciseLocation.setText(location);
            mSecondaryLocation.setVisibility(View.GONE);
            mGeneralLocation.setVisibility(View.GONE);
        }

        if(location.contains("洗浴")){
            mTransactionRecordImageView.setImageDrawable(getDrawable(R.drawable.vector_drawable_bath));
        }
        else if(location.contains("吧台")){
            mTransactionRecordImageView.setImageDrawable(getDrawable(R.drawable.vector_drawable_flute_glass));
        }
        else if(location.contains("小炒")){
            mTransactionRecordImageView.setImageDrawable(getDrawable(R.drawable.vector_drawable_cutlery_small));
        }
        else if(location.contains("食堂")){
            mTransactionRecordImageView.setImageDrawable(getDrawable(R.drawable.vector_drawable_table));
        }
        else if(location.contains("小厨")){
            mTransactionRecordImageView.setImageDrawable(getDrawable(R.drawable.vector_drawable_kitchen));
        }
        else if(location.contains("快餐")){
            mTransactionRecordImageView.setImageDrawable(getDrawable(R.drawable.vector_drawable_plates));
        }
        else if(location.contains("餐厅")){
            mTransactionRecordImageView.setImageDrawable(getDrawable(R.drawable.vector_drawable_dish));
        }
        else if(location.contains("商店")){
            mTransactionRecordImageView.setImageDrawable(getDrawable(R.drawable.vector_drawable_store));
        }
        else if(location.contains("超市")){
            mTransactionRecordImageView.setImageDrawable(getDrawable(R.drawable.vector_drawable_supermarket));
        }
        else if(location.contains("体育")){
            mTransactionRecordImageView.setImageDrawable(getDrawable(R.drawable.vector_drawable_sports_run));
        }
        else if(location.contains("校车")){
            mTransactionRecordImageView.setImageDrawable(getDrawable(R.drawable.vector_drawable_campus_shuttle));
        }
        else {
            mTransactionRecordImageView.setImageDrawable(getDrawable(R.drawable.vector_drawable_pay));
        }


        mTransactionDetailBalance.setText(String.valueOf(entity.balance));
        DateFormat dateFormat = DateFormat.getDateTimeInstance(SimpleDateFormat.MEDIUM,SimpleDateFormat.MEDIUM, Locale.getDefault());
        mTransactionDetailTime.setText(dateFormat.format(entity.payTime));
        mTransactionDetailStatus.setTextColor(getColor(R.color.colorTurquoise));
        if(entity.amount > 0 ){
            mTransactionDetailNumber.setText(String.format("+%s",entity.amount));
            mTransactionDetailNumber.setTextColor(getColor(R.color.colorAlizarin));
            mTransactionDetailStatus.setText(R.string.recharge_successful);
            if(location.equals("null")||location == null){
                mPreciseLocation.setText(R.string.online_recharge_text);
                mTransactionDetailBalance.setText(String.valueOf(entity.balance+entity.amount));
            }
            mTransactionRecordImageView.setImageDrawable(getDrawable(R.drawable.vector_drawable_recharge));
            mSecondaryLocation.setText(R.string.simple_recharge_text);
            mGeneralLocation.setText(R.string.NPU_name);
            mSecondaryLocation.setVisibility(View.VISIBLE);
            mGeneralLocation.setVisibility(View.VISIBLE);
        }
        else {
            mTransactionDetailNumber.setText(String.valueOf(entity.amount));
            mTransactionDetailNumber.setTextColor(getColor(R.color.colorPureBlack));
            mTransactionDetailStatus.setText(R.string.payment_successful);
        }






        configureStatusBar();
        configureToolbar();
    }

    private void configureStatusBar(){
        getWindow().setStatusBarColor(getColor(R.color.colorBelizahole));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    void configureToolbar(){

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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

}
