package com.kidozh.npuhelper.scoreQuery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kidozh.npuhelper.R;
import com.kidozh.npuhelper.accountAuth.accountInfoBean;
import com.kidozh.npuhelper.accountAuth.userInfoUtils;

import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.util.List;
import java.util.Locale;

public class queryScoreMainActivity extends AppCompatActivity {
    static final String TAG = queryScoreMainActivity.class.getSimpleName();

    @BindView(R.id.query_score_recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.weight_average_score)
    TextView mWeightAvgScore;
    @BindView(R.id.gpa_value)
    TextView mGPAValue;
    @BindView(R.id.score_info_constraint_layout)
    ConstraintLayout mScoreInfoLayout;
    @BindView(R.id.query_score_progressbar)
    ProgressBar mProgressbar;
    @BindView(R.id.query_score_toolbar)
    Toolbar toolbar;
    @BindView(R.id.npu_gpa_value)
    TextView NPUGPAValTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_score_main);
        ButterKnife.bind(this);
        configureToolbar();
        configureStatusBar();
        Log.d(TAG,"Triggle score info");
        new getScoreInfoTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        // new getScoreInfoTask(this).execute();
    }

    private void configureToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void configureStatusBar(){
        getWindow().setStatusBarColor(getColor(R.color.colorTurquoise));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    private class getScoreInfoTask extends AsyncTask<Void,Void,SoapObject>{
        String apptoken,username;
        Context mContext;

        getScoreInfoTask(Context context){
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mRecyclerView.setVisibility(View.INVISIBLE);
            mScoreInfoLayout.setVisibility(View.INVISIBLE);
            mProgressbar.setVisibility(View.VISIBLE);
            Log.d(TAG,"FETCHING ACCOUNT...");
            accountInfoBean accountInfo = userInfoUtils.getTokenInfoFromLocal(mContext);
            apptoken = accountInfo.appToken;
            username = accountInfo.accountNumber;
        }

        @Override
        protected SoapObject doInBackground(Void... voids) {
            Log.d(TAG,"FETCHING SCORE...");
            SoapObject result = queryScoreUtils.getScoreInfoResult(username,apptoken);
            return result;
        }

        @Override
        protected void onPostExecute(SoapObject s) {
            super.onPreExecute();
            mRecyclerView.setVisibility(View.VISIBLE);
            mScoreInfoLayout.setVisibility(View.VISIBLE);
            mProgressbar.setVisibility(View.INVISIBLE);
            List<queryScoreUtils.scoreBeam> scoreBeamList = queryScoreUtils.parseCardInfoRes(s);
            scoreAdapter adapter = new scoreAdapter();
            adapter.scoreBeams = scoreBeamList;
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mRecyclerView.setAdapter(adapter);
            float weightAvgScore = queryScoreUtils.getWeightsAveragePoints(scoreBeamList);
            if(weightAvgScore !=0){
                mWeightAvgScore.setText(String.format(Locale.getDefault(),"%.2f",weightAvgScore));
            }
            else {
                mWeightAvgScore.setText(R.string.unknown);
            }
            float USGPA = queryScoreUtils.getUSGPAPoints(scoreBeamList);
            if(USGPA !=0){
                mGPAValue.setText(String.format(Locale.getDefault(),"%.1f",USGPA));
            }
            else {
                mGPAValue.setText(R.string.unknown);
            }

            float NPUGPA = queryScoreUtils.getNPUGPAPoints(scoreBeamList);
            if(NPUGPA !=0){
                NPUGPAValTextView.setText(String.format(Locale.getDefault(),"%.1f",NPUGPA));
            }
            else {
                NPUGPAValTextView.setText(R.string.unknown);
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


}
