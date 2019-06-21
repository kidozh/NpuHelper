package com.kidozh.npuhelper.schoolCalendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.kidozh.npuhelper.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class schoolCalendarMainActivity extends AppCompatActivity implements
        CalendarView.OnCalendarSelectListener,
        CalendarView.OnYearChangeListener,
        View.OnClickListener{
    private final static String TAG = schoolCalendarMainActivity.class.getSimpleName();

    @BindView(R.id.school_calendar_calendarView)
    CalendarView mCalendarView;
    @BindView(R.id.tv_month_day)
    TextView mTextMonthDay;
    @BindView(R.id.tv_year)
    TextView mTextYear;
    @BindView(R.id.tv_lunar)
    TextView mTextLunar;
    @BindView(R.id.rl_tool)
    RelativeLayout mRelativeTool;
    @BindView(R.id.tv_current_day)
    TextView mTextCurrentDay;
    @BindView(R.id.calendarLayout)
    CalendarLayout mCalendarLayout;
    private int mYear;
    @BindView(R.id.recyclerView)
    GroupRecyclerView mRecyclerView;
    @BindView(R.id.fl_current)
    ConstraintLayout backToCurTextView;
    @BindView(R.id.tv_semester)
    TextView mSemesterTextView;
    @BindView(R.id.tv_week_number)
    TextView mWeekNumberTextView;
    Boolean isCalendarFinished = false;
    JSONObject calendarJSONObj;
    Map<String, Calendar> calendarHolidayMap = new HashMap<>();
    ArticleAdapter articleAdapter;
    private int lastMonth;
    @BindView(R.id.calendar_card_lunar_tv)
    TextView mCalendarCardLunarTextView;
    @BindView(R.id.calendar_card_semester_name_tv)
    TextView mCalendarCardSemesterNameTextView;
    @BindView(R.id.calendar_card_week_number_tv)
    TextView mCalendarCardWeekNumberTextView;
    @BindView(R.id.calendar_card_scheme)
    TextView mCalendarCardScheme;
    @BindView(R.id.tv_calendar_event_list)
    ImageView mCalendarEventImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_calendar_main);
        ButterKnife.bind(this);
        Log.d(TAG,"Create Butter knife");

        initView();
        Log.d(TAG,"Init view successfully");
        initData();
        Log.d(TAG,"Init data successfully");
        new getSchoolCalendarTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);;

    }

    @SuppressLint("SetTextI18n")
    protected void initView() {
        getWindow().setStatusBarColor(getColor(R.color.colorPureWhite));
        getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this) ;
        String api_string = prefs.getString(getString(R.string.pref_key_calendar_start_day),"mon");
        if(api_string.equals("mon")){
            mCalendarView.setWeekStarWithMon();
        }
        else if(api_string.equals("sat")){
            mCalendarView.setWeekStarWithSat();
        }
        else if(api_string.equals("sun")){
            mCalendarView.setWeekStarWithSun();
        }


        mTextMonthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCalendarLayout.isExpand()) {
                    mCalendarView.showYearSelectLayout(mYear);
                    return;
                }
                if(mCalendarView.isYearSelectLayoutVisible()){
                    mCalendarView.closeYearSelectLayout();
                    mCalendarView.scrollToCurrent();
                    mTextLunar.setVisibility(View.VISIBLE);
                    mTextYear.setVisibility(View.VISIBLE);
                }
                else {
                    mCalendarView.showYearSelectLayout(mYear);
                    mTextLunar.setVisibility(View.GONE);
                    mTextYear.setVisibility(View.GONE);
                    mTextMonthDay.setText(String.valueOf(mYear));
                    mWeekNumberTextView.setVisibility(View.GONE);
                    mSemesterTextView.setVisibility(View.GONE);

                }


            }
        });
        mTextCurrentDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCalendarView.isYearSelectLayoutVisible()){
                    mCalendarView.closeYearSelectLayout();

                    mCalendarView.scrollToCurrent();
                }
                else {
                    mCalendarView.scrollToCurrent();
                }

            }
        });
        mCalendarView.setOnCalendarSelectListener(this);
        mCalendarView.setOnYearChangeListener(this);
        mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
        mYear = mCalendarView.getCurYear();
        lastMonth = mCalendarView.getCurMonth();
//        java.util.Calendar cal = java.util.Calendar.getInstance();
//        cal.set(mCalendarView.getCurYear(),mCalendarView.getCurMonth(),mCalendarView.getCurDay());
//        String formatTimeString = DateFormat.getDateInstance().format(cal.getTime());
//        mTextMonthDay.setText(formatTimeString);
        mTextMonthDay.setText(mCalendarView.getCurMonth() + "月" + mCalendarView.getCurDay() + "日");
        mTextLunar.setText(R.string.today);
        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));
        //mCalendarView.scrollToCurrent();
    }

    private void addHolidayInfoToCalendar(Calendar calendarInfo){
        calendarHolidayMap.put(calendarInfo.toString(), calendarInfo);
    }

    protected void initData() {
        List<Calendar> schemes = new ArrayList<>();
        int year = mCalendarView.getCurYear();
        int month = mCalendarView.getCurMonth();
        articleAdapter = new ArticleAdapter(this);
        //mRecyclerView = (GroupRecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new GroupItemDecoration<String, Article>());
        mRecyclerView.setAdapter(articleAdapter);
        mRecyclerView.notifyDataSetChanged();
        mCalendarView.scrollToCurrent();

        new getHotNewsTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);;
        new getNewsFromPortalTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);;

    }


    @Override
    public void onClick(View v) {

    }

    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        calendar.addScheme(new Calendar.Scheme());
        calendar.addScheme(0xFF008800, "假");
        calendar.addScheme(0xFF008800, "节");
        return calendar;
    }


    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);
        if(calendar.isCurrentDay()){
            mTextCurrentDay.setVisibility(View.GONE);
        }
        else {
            mTextCurrentDay.setVisibility(View.VISIBLE);
        }

        if(lastMonth != calendar.getMonth()){
            lastMonth = calendar.getMonth();
            new getNewsFromPortalTask(this,calendar.getYear(),calendar.getMonth()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);;
        }
        java.util.Calendar calendarObj = java.util.Calendar.getInstance();
        calendarObj.set(calendar.getYear(),calendar.getMonth()-1,calendar.getDay());
        SimpleDateFormat formatter = new SimpleDateFormat("MMM",Locale.getDefault());
        mTextMonthDay.setText(formatter.format(calendarObj.getTime()));
        //mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
        mTextYear.setText(String.valueOf(calendar.getYear()));
        mTextLunar.setText(calendar.getLunar());
        mYear = calendar.getYear();
        showSemesterInfo(calendar.getYear(),calendar.getMonth(),calendar.getDay());
        mCalendarCardLunarTextView.setText(calendar.getLunar());
        // judge whether today is off
        String curScheme = calendar.getScheme();
        Log.d(TAG,"current scheme "+curScheme);
        if(curScheme == null || curScheme.length() == 0){
            if(calendar.isCurrentDay()){
                mCalendarCardScheme.setText(getString(R.string.today));
                mCalendarCardScheme.setTextColor(getColor(R.color.cardview_dark_background));
            }
            else if(calendar.isWeekend()){

                mCalendarCardScheme.setText(getString(R.string.rest));
                mCalendarCardScheme.setTextColor(getColor(R.color.cardview_dark_background));
            }
            else {
                Log.d(TAG,"workday activated");
                mCalendarCardScheme.setText(getString(R.string.work));
                mCalendarCardScheme.setTextColor(getColor(R.color.cardview_dark_background));
            }


        }
        else{
            if(curScheme.equals(getString(R.string.start_label))){
                mCalendarCardScheme.setText(getString(R.string.semester_start_tag));
                mCalendarCardScheme.setTextColor(calendar.getSchemeColor());
            }
            else if(curScheme.equals(getString(R.string.end_label))) {
                mCalendarCardScheme.setText(getString(R.string.semester_end_tag));
                mCalendarCardScheme.setTextColor(calendar.getSchemeColor());
            }
            else if(curScheme.equals(getString(R.string.rest))){
                mCalendarCardScheme.setText(calendar.getScheme());
                mCalendarCardScheme.setTextColor(calendar.getSchemeColor());
            }
            else if(curScheme.equals(getString(R.string.work))){
                mCalendarCardScheme.setText(calendar.getScheme());
                mCalendarCardScheme.setTextColor(calendar.getSchemeColor());
            }
            else {
                mCalendarCardScheme.setText(calendar.getScheme());
                mCalendarCardScheme.setTextColor(calendar.getSchemeColor());
            }
        }



    }

    @Override
    public void onYearChange(int year) {
        mTextMonthDay.setText(String.valueOf(year));
    }

    private boolean isCurDateInSemester(JSONObject jsonObject,int year,int month,int day){
        try {
            String startDateString = jsonObject.getString("start");
            String endDateString = jsonObject.getString("end");
            int startDateInt = Integer.parseInt(startDateString);
            int endDateInt = Integer.parseInt(endDateString);
            int curDateInt = year * 100*100 + month * 100 + day;
            if(curDateInt >= startDateInt && endDateInt >= curDateInt){
                return true;
            }
            else {
                return false;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

    }

    @SuppressLint("StaticFieldLeak")
    public class getSchoolCalendarTask extends AsyncTask<Void,Void,String>{

        Context mContext;
        URL api_url;
        private Request request;
        private final OkHttpClient client = new OkHttpClient();

        getSchoolCalendarTask(Context context){
            this.mContext = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            api_url = schoolCalendarUtils.build_school_calendar_url(mContext);
            request = new Request.Builder()
                    .url(api_url)
                    .build();
            isCalendarFinished = false;
            mSemesterTextView.setVisibility(View.INVISIBLE);
            mWeekNumberTextView.setVisibility(View.INVISIBLE);


        }

        @Override
        protected String doInBackground(Void... voids) {
            String jsonResponse = "";
            try {
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    jsonResponse = response.body().string();
                } else {
                    throw new IOException("Unexpected code " + response);
                }
            } catch (IOException e) {
                e.printStackTrace();
                jsonResponse = "";
            } catch (NullPointerException e){
                jsonResponse = "";
            }

            return jsonResponse;
        }



        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mSemesterTextView.setVisibility(View.VISIBLE);
            mWeekNumberTextView.setVisibility(View.VISIBLE);
            Log.d(TAG,"Finished load "+s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                calendarJSONObj = jsonObject;
                isCalendarFinished = true;
                labelOnSpecialDay(jsonObject);
                mCalendarView.setSchemeDate(calendarHolidayMap);
                showSemesterInfo(mCalendarView.getCurYear(),mCalendarView.getCurMonth(),mCalendarView.getCurDay());

            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
    }

    private void labelOnSpecialDay(JSONObject jsonObject){
        try {
            JSONObject holidayObj = jsonObject.getJSONObject("holiday");
            Iterator holidayIt = holidayObj.keys();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            while(holidayIt.hasNext()){
                String year = (String) holidayIt.next();
                JSONObject yearHoliday = holidayObj.getJSONObject(year);
                // workday
                if(yearHoliday.has("workday")){
                    JSONObject workdayObj = yearHoliday.getJSONObject("workday");
                    for(Iterator workdayIt = workdayObj.keys();workdayIt.hasNext();){
                        String holidayName = (String) workdayIt.next();
                        JSONArray workdayArr = workdayObj.getJSONArray(holidayName);
                        for(int i = 0; i<workdayArr.length();i++){
                            String workdayStr = (String) workdayArr.get(i);
                            Date workdayDateObj = formatter.parse(workdayStr);
                            java.util.Calendar calendarObj = java.util.Calendar.getInstance();
                            calendarObj.setTime(workdayDateObj);
                            int mYear = calendarObj.get(java.util.Calendar.YEAR);
                            int mMonth = calendarObj.get(java.util.Calendar.MONTH) + 1;
                            int mDay = calendarObj.get(java.util.Calendar.DAY_OF_MONTH);
                            // add to calendar
                            addHolidayInfoToCalendar(getSchemeCalendar(mYear,mMonth,mDay,getColor(R.color.colorCalendarWorkDayLabel),getString(R.string.work)));
                        }
                    }
                }
                if(yearHoliday.has("festival")){
                    JSONObject festivalObj = yearHoliday.getJSONObject("festival");
                    for(Iterator workdayIt = festivalObj.keys();workdayIt.hasNext();){
                        String holidayName = (String) workdayIt.next();
                        JSONArray workdayArr = festivalObj.getJSONArray(holidayName);
                        for(int i = 0; i<workdayArr.length();i++){
                            String workdayStr = (String) workdayArr.get(i);
                            Date workdayDateObj = formatter.parse(workdayStr);
                            java.util.Calendar calendarObj = java.util.Calendar.getInstance();
                            calendarObj.setTime(workdayDateObj);
                            int mYear = calendarObj.get(java.util.Calendar.YEAR);
                            int mMonth = calendarObj.get(java.util.Calendar.MONTH) + 1;
                            int mDay = calendarObj.get(java.util.Calendar.DAY_OF_MONTH);
                            // add to calendar
                            addHolidayInfoToCalendar(getSchemeCalendar(mYear,mMonth,mDay,getColor(R.color.colorCalendarHolidayDayLabel),getString(R.string.rest)));
                        }
                    }
                }

            }
            // then semester day
            JSONObject semesterObj = jsonObject.getJSONObject("calendar");
            for(Iterator semesterIt = semesterObj.keys();semesterIt.hasNext();){
                String semseterName = (String) semesterIt.next();
                JSONObject semesterYearObj = semesterObj.getJSONObject(semseterName);
                Log.d(TAG,"Selected Semester Year "+semseterName);
                if(semesterYearObj.has("spring")){
                    String DateStr = semesterYearObj.getJSONObject("spring").getString("start");
                    // label it
                    Date startDateObj = formatter.parse(DateStr);
                    java.util.Calendar calendarObj = java.util.Calendar.getInstance();
                    calendarObj.setTime(startDateObj);
                    int mYear = calendarObj.get(java.util.Calendar.YEAR);
                    int mMonth = calendarObj.get(java.util.Calendar.MONTH) + 1;
                    int mDay = calendarObj.get(java.util.Calendar.DAY_OF_MONTH);
                    // add to calendar
                    addHolidayInfoToCalendar(getSchemeCalendar(mYear,mMonth,mDay,getColor(R.color.colorCalendarStartSemesterLabel),getString(R.string.start_label)));
                    DateStr = semesterYearObj.getJSONObject("spring").getString("end");
                    // label it
                    startDateObj = formatter.parse(DateStr);
                    calendarObj = java.util.Calendar.getInstance();
                    calendarObj.setTime(startDateObj);
                    mYear = calendarObj.get(java.util.Calendar.YEAR);
                    mMonth = calendarObj.get(java.util.Calendar.MONTH) + 1;
                    mDay = calendarObj.get(java.util.Calendar.DAY_OF_MONTH);
                    // add to calendar
                    addHolidayInfoToCalendar(getSchemeCalendar(mYear,mMonth,mDay,getColor(R.color.colorCalendarEndSemesterLabel),getString(R.string.end_label)));
                }
                if(semesterYearObj.has("fall")){
                    String DateStr = semesterYearObj.getJSONObject("fall").getString("start");
                    // label it
                    Date startDateObj = formatter.parse(DateStr);
                    java.util.Calendar calendarObj = java.util.Calendar.getInstance();
                    calendarObj.setTime(startDateObj);
                    int mYear = calendarObj.get(java.util.Calendar.YEAR);
                    int mMonth = calendarObj.get(java.util.Calendar.MONTH) + 1;
                    int mDay = calendarObj.get(java.util.Calendar.DAY_OF_MONTH);
                    // add to calendar
                    addHolidayInfoToCalendar(getSchemeCalendar(mYear,mMonth,mDay,getColor(R.color.colorCalendarStartSemesterLabel),getString(R.string.start_label)));
                    DateStr = semesterYearObj.getJSONObject("fall").getString("end");
                    // label it
                    startDateObj = formatter.parse(DateStr);
                    calendarObj = java.util.Calendar.getInstance();
                    calendarObj.setTime(startDateObj);
                    mYear = calendarObj.get(java.util.Calendar.YEAR);
                    mMonth = calendarObj.get(java.util.Calendar.MONTH) + 1;
                    mDay = calendarObj.get(java.util.Calendar.DAY_OF_MONTH);
                    // add to calendar
                    addHolidayInfoToCalendar(getSchemeCalendar(mYear,mMonth,mDay,getColor(R.color.colorCalendarEndSemesterLabel),getString(R.string.end_label)));
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch (ParseException e){
            e.printStackTrace();
        }

    }



    @SuppressLint("StringFormatMatches")
    private void showSemesterInfo(int year, int month, int day){
        semesterInfo curSemester = getCurSemester(year,month,day);
        if(!isCalendarFinished){
            return ;
        }
        if(curSemester != null){
            String seasonName = "";
            if(curSemester.season.equals("spring")){
                seasonName = getString(R.string.spring_semester);
            }
            else if(curSemester.season.equals("fall")){
                seasonName = getString(R.string.fall_semester);
            }
            else {
                seasonName = curSemester.season;
            }
            mSemesterTextView.setText(String.format("%s %s",curSemester.semesterName,seasonName));
            mCalendarCardSemesterNameTextView.setText(String.format("%s %s",curSemester.semesterName,seasonName));
            mSemesterTextView.setVisibility(View.VISIBLE);

            java.util.Calendar selectedCalendar = java.util.Calendar.getInstance();
            selectedCalendar.set(year,month-1,day);
            //Log.d(TAG,"Current Selected Date "+selectedCalendar.getTime().toString()+" CurSemester start date "+curSemester.startDate.toString());
            //Log.d(TAG,"Current Selected Date "+selectedCalendar.getTime().getTime()+" CurSemester start date "+curSemester.startDate.getTime());
            long diffMillsSeconds = selectedCalendar.getTime().getTime() - curSemester.startDate.getTime();

            long week = diffMillsSeconds /(1000 * 60 * 60 * 24 * 7);
            //Log.d(TAG,String.format("Diff %s week %s",diffMillsSeconds,week));
            String week_number_template = getString(R.string.week_number_template);
            mWeekNumberTextView.setText(String.format(week_number_template,week+1));
            mCalendarCardWeekNumberTextView.setText(String.format(week_number_template,week+1));
            mWeekNumberTextView.setVisibility(View.VISIBLE);
        }
        else {
            mWeekNumberTextView.setText("");
            mCalendarCardWeekNumberTextView.setText("");
            mSemesterTextView.setText(R.string.vacation);
            mCalendarCardSemesterNameTextView.setText(R.string.vacation);
        }

    }

    private semesterInfo getCurSemester(int year,int month,int day){
        if(calendarJSONObj == null){
            return null;
        }
        JSONObject jsonObject = calendarJSONObj;
        JSONObject calendarObj = null;
        try {
            calendarObj = jsonObject.getJSONObject("calendar");
            // handle calendar info
            Iterator calendarIt = calendarObj.keys();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd",Locale.getDefault());
            Date startDate,endDate;
            String curSemester,curSeason = "";
            while (calendarIt.hasNext()) {
                String semester = (String) calendarIt.next();
                JSONObject semesterObj = calendarObj.getJSONObject(semester);
                // spring or fall

                if (semesterObj.has("spring") && isCurDateInSemester(semesterObj.getJSONObject("spring"),year,month,day)) {
                    JSONObject seasonObj = semesterObj.getJSONObject("spring");
                    String startDateString = seasonObj.getString("start");
                    String endDateString = seasonObj.getString("end");

                    startDate = formatter.parse(startDateString);
                    endDate = formatter.parse(endDateString);
                    curSeason = "spring";
                    return new semesterInfo(semester,curSeason,startDate,endDate);

                }

                if (semesterObj.has("fall") && isCurDateInSemester(semesterObj.getJSONObject("fall"),year,month,day)) {
                    JSONObject seasonObj = semesterObj.getJSONObject("fall");
                    String startDateString = seasonObj.getString("start");
                    String endDateString = seasonObj.getString("end");
                    startDate = formatter.parse(startDateString);
                    endDate = formatter.parse(endDateString);
                    Log.d(TAG,"start date "+startDateString+" "+startDate.toString()+" end date "+endDate.toString());
                    curSeason = "fall";
                    return new semesterInfo(semester,curSeason,startDate,endDate);

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();

        }
        return null;
    }

    private class semesterInfo{
        public String semesterName;
        public String season;
        public Date startDate;
        public Date endDate;

        semesterInfo(String semesterName,String season,Date startDate,Date endDate){
            this.semesterName = semesterName;
            this.season = season;
            this.startDate = startDate;
            this.endDate = endDate;
        }

    }

    @SuppressLint("StaticFieldLeak")
    public class getNewsFromPortalTask extends AsyncTask<Void,Void,String>{
        Context mContext;
        URL api_url;
        private Request request;
        private OkHttpClient client;
        int year = -1 , month=-1;
        RequestBody requestBody;
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36";


        getNewsFromPortalTask(Context context){
            this.mContext = context;
            OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
            mBuilder.sslSocketFactory(TrustAllCerts.createSSLSocketFactory());
            mBuilder.hostnameVerifier(new TrustAllHostnameVerifier());
            client = mBuilder.build();

        }

        getNewsFromPortalTask(Context context,int year,int month){
            this.mContext = context;
            this.year = year;
            this.month = month;
            OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
            mBuilder.sslSocketFactory(TrustAllCerts.createSSLSocketFactory());
            mBuilder.hostnameVerifier(new TrustAllHostnameVerifier());
            client = mBuilder.build();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            api_url = schoolCalendarUtils.build_school_news_url(mContext);

            // year: 2018
            // month: 9
            // owner: 867808034
            // site_url: http://www.lijin.gov.cn/
            String mYear,mMonth;
            if(year == -1 || month == -1){
                mYear = String.format("%s",mCalendarView.getCurYear());
                mMonth = String.format("%s",mCalendarView.getCurMonth());
            }
            else {
                mYear = String.format("%s",year);
                mMonth = String.format("%s",month);
            }

            Log.d(TAG,"Build Year "+mYear+" Month "+mMonth);
            requestBody = new FormBody.Builder()
                    .add("site_url","http://www.lijin.gov.cn/")
                    .add("owner","867808034")
                    .add("year",mYear)
                    .add("month",mMonth)
                    .build();
            request = new Request.Builder()
                    .url(api_url)
                    .post(requestBody)
                    .addHeader("User-Agent", userAgent)
                    .build();
        }


        @Override
        protected String doInBackground(Void... voids) {
            String jsonResponse;
            try {
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    jsonResponse = response.body().string();
                    Log.d(TAG,"JSON TEXT FROM SCHOOL NEWS : "+jsonResponse);
                    return jsonResponse;

                } else {
                    throw new IOException("Unexpected code " + response);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG,"Recv News "+s);
            List<Article> articleList = new ArrayList<>();
            try{
                JSONArray respArr = new JSONArray(s);
                JSONArray newsArr = respArr.getJSONArray(0);
                for (int i=0; i<newsArr.length(); i++){
                    Article article = new Article();
                    JSONObject newsObj = newsArr.getJSONObject(i);
                    // JSONObject newsObj = newsArr.getJSONObject(i);
                    article.setTitle(newsObj.getString("title"));
                    article.setDate(newsObj.getString("wbdate"));
                    article.setSourceURL(newsObj.getString("url"));
                    article.setImgUrl("");
                    articleList.add(article);
                }
                Log.d(TAG,"Create news "+newsArr.length());
                articleAdapter.addArticles(mContext.getString(R.string.school_news_title),articleList);
                articleAdapter.resetAdapterGroups();
                mRecyclerView.setAdapter(articleAdapter);
                mRecyclerView.notifyDataSetChanged();



            }
            catch (Exception e){
                e.printStackTrace();
                Log.i(TAG,"Error found"+e.toString());
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class getHotNewsTask extends AsyncTask<Void,Void,String>{
        Context mContext;
        URL api_url;
        private Request request;
        private OkHttpClient client;
        RequestBody requestBody;
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36";


        getHotNewsTask(Context context){
            this.mContext = context;
            OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
            mBuilder.sslSocketFactory(TrustAllCerts.createSSLSocketFactory());
            mBuilder.hostnameVerifier(new TrustAllHostnameVerifier());
            client = mBuilder.build();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            api_url = schoolCalendarUtils.build_hot_news_url(mContext);

            // year: 2018
            // month: 9
            // owner: 867808034
            // site_url: http://www.lijin.gov.cn/

            requestBody = new FormBody.Builder()
                    .add("treeid","1001")
                    .add("owner","867808034")
                    .add("mode","10")
                    .add("pageUrl","/index.htm")
                    .add("uniqueId", "u24")
                    .add("actionmethod","getnewslist")
                    .add("viewid","199044")
                    .add("locale","zh_CN")
                    .build();
            request = new Request.Builder()
                    .url(api_url)
                    .post(requestBody)
                    .addHeader("User-Agent", userAgent)
                    .build();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String jsonResponse;
            try {
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    jsonResponse = response.body().string();
                    Log.d(TAG,"STRING: "+jsonResponse);
                    return jsonResponse;

                } else {
                    throw new IOException("Unexpected code " + response);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG,"Recv HOT NEWS "+s);
            final String HOT_NEWS_PREFIX = "https://news.nwpu.edu.cn/";
            final String PIC_NEWS_PREFIX = "https://news.nwpu.edu.cn";
            List<Article> articleList = new ArrayList<>();
            // Base 64 decoder
            try{
                JSONArray newsArr = new JSONArray(s);
                for (int i=0; i<newsArr.length(); i++){
                    Article article = new Article();
                    JSONObject newsObj = newsArr.getJSONObject(i);
                    // JSONObject newsObj = newsArr.getJSONObject(i);
                    article.setTitle(newsObj.getString("showtitle"));
                    String base64TimeStr = newsObj.getString("timestr");
                    byte[] timeStr = Base64.decode(base64TimeStr, Base64.DEFAULT);
                    String dateStr = new String(timeStr);
                    article.setDate(dateStr);
                    article.setSourceURL(HOT_NEWS_PREFIX+newsObj.getString("linkurl"));
                    String newsPicUrl = newsObj.optString("wbpicurl","");
                    if(newsPicUrl.equals("")){
                        article.setImgUrl("");
                    }
                    else {
                        article.setImgUrl(HOT_NEWS_PREFIX+newsPicUrl);
                    }


                    articleList.add(article);
                }
                Log.d(TAG,"Create hot news "+newsArr.length());
                articleAdapter.addArticles(mContext.getString(R.string.hot_news),articleList);
                articleAdapter.resetAdapterGroups();
                mRecyclerView.setAdapter(articleAdapter);
                mRecyclerView.notifyDataSetChanged();



            }
            catch (Exception e){
                e.printStackTrace();
                Log.i(TAG,"Error found"+e.toString());
            }
        }
    }


}
