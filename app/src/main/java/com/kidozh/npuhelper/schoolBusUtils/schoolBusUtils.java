package com.kidozh.npuhelper.schoolBusUtils;



import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public final class schoolBusUtils {

    private static final String TAG = schoolBusUtils.class.getSimpleName();

    private static final int[] changan2youyiWeekdayTime = {0, 640, 900, 1030, 1100, 1230, 1300, 1400, 1500, 1600, 1710, 1800, 1900, 2100, 2230};
    private static final int[] changan2youyiWeekendTime = {0, 900, 1100, 1230, 1700, 1800, 2100};
    private static final int[] youyi2changanWeekdayTime = {0, 700, 800, 900, 1000, 1100, 1200, 1230, 1400, 1430, 1600, 1700, 1730, 1830, 2100};
    private static final int[] youyi2changanWeekendTime = {0, 800, 900, 1230, 1400, 1800, 2000};
    private static final String[] festival_list = {"dragon_boat_festival","mid_fall_festival","national_day"};

    public static String speacialDayName = "";
    public static Boolean isFestivalHoliday = false;
    public static Boolean isFestivalWorkDay = false;

    public static boolean isWeekday(){
        Calendar calendar = Calendar.getInstance();
        int dayOfWeekIndex = calendar.get(Calendar.DAY_OF_WEEK);
        if(isFestivalHoliday){
            return false;
        }

        if(isFestivalWorkDay){
            return true;
        }

        if(dayOfWeekIndex == Calendar.SATURDAY || dayOfWeekIndex == Calendar.SUNDAY){
            return false;
        }
        else {
            return true;
        }

    }

    public static int[] getChangan2YouyiBusList(){
        if(isWeekday()){
            return changan2youyiWeekdayTime;
        }
        else {
            return changan2youyiWeekendTime;
        }
    }

    public static int[] getYouyi2ChanganBusList(){
        if(isWeekday()){
            return youyi2changanWeekdayTime;
        }
        else {
            return youyi2changanWeekendTime;
        }
    }

    public static int getBusLeftMinutes(int timeLabel){
        Calendar calendar = Calendar.getInstance();
        int minute = calendar.get(Calendar.MINUTE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int formatTime = hour *100 + minute;
        int busDepatureHour = timeLabel / 100;
        int busDepatureMinute = timeLabel % 100;

        // Log.d(TAG,String.format("%s %s %s %s",nextBusDepatureHour,nextBusDepatureMinute,hour,minute));
        return (busDepatureHour - hour) * 60 + (busDepatureMinute - minute);
    }


    private static int getNearestTime(int[] timeSchedule){
        Calendar calendar = Calendar.getInstance();
        int minute = calendar.get(Calendar.MINUTE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int formatTime = hour *100 + minute;
        int nextBus = timeSchedule.length;

        for(int i=1;i<timeSchedule.length;i++){
            if(timeSchedule[i] >=  formatTime){
                // there is a school shuttle
                nextBus = i;
                break;
            }
        }

        if(nextBus == timeSchedule.length){
            return -1;
        }
        else return timeSchedule[nextBus];
    }

    public static int getNearestIndex(int[] timeSchedule){
        Calendar calendar = Calendar.getInstance();
        int minute = calendar.get(Calendar.MINUTE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int formatTime = hour *100 + minute;
        int nextBus = timeSchedule.length;

        for(int i=1;i<timeSchedule.length;i++){
            if(timeSchedule[i] >=  formatTime){
                // there is a school shuttle
                nextBus = i;
                break;
            }
        }

        if(nextBus == timeSchedule.length){
            return -1;
        }
        else return nextBus;
    }

    private static int getMinutesLeft(int[] timeSchedule){
        Calendar calendar = Calendar.getInstance();
        int minute = calendar.get(Calendar.MINUTE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int formatTime = hour *100 + minute;
        int nextBus = timeSchedule.length;

        for(int i=1;i<timeSchedule.length;i++){
            if(timeSchedule[i] >=  formatTime){
                // there is a school shuttle
                nextBus = i;
                break;
            }
        }



        if(nextBus == timeSchedule.length){
            return -1;
        }
        else {
            int nextBusDepatureHour = timeSchedule[nextBus] / 100;
            int nextBusDepatureMinute = timeSchedule[nextBus] % 100;

            // Log.d(TAG,String.format("%s %s %s %s",nextBusDepatureHour,nextBusDepatureMinute,hour,minute));
            return (nextBusDepatureHour - hour) * 60 + (nextBusDepatureMinute - minute);
        }


    }

    public static int getBusLeftMinutesToChangan(){

        if(isWeekday()){
            return getMinutesLeft(youyi2changanWeekdayTime);
        }
        else {
            return getMinutesLeft(youyi2changanWeekendTime);
        }


    }

    public static int getBusLeftMinutesToYouyi(){

        if(isWeekday()){
            return getMinutesLeft(changan2youyiWeekdayTime);
        }
        else {
            return getMinutesLeft(changan2youyiWeekendTime);
        }
    }

    public static int getNearestBusTimeToChangan(){
        int nextBusScheduleTime;
        if(isWeekday()){
            return getNearestTime(youyi2changanWeekdayTime);
        }
        else {
            return getNearestTime(youyi2changanWeekendTime);
        }


    }

    public static int getNearestBusTimeToYouyi(){
        int nextBusScheduleTime;
        if(isWeekday()){
            return getNearestTime(changan2youyiWeekdayTime);
        }
        else {
            return getNearestTime(changan2youyiWeekendTime);
        }


    }

    private static boolean isDayMatch(String s){
        Log.d(TAG,"Passed string : "+s);
        Calendar calendar = Calendar.getInstance();
        int curYear = calendar.get(Calendar.YEAR);
        int curMonth = calendar.get(Calendar.MONTH)+1;
        int curDay = calendar.get(Calendar.DAY_OF_MONTH);
        int timeInt = Integer.parseInt(s);
        if(timeInt / 10000 == curYear && (timeInt %10000) /100 == curMonth && timeInt % 100 == curDay){
            return true;
        }
        else {
            return false;
        }
    }

    public static String handleApiJson(JSONObject jsonObject){
        try {
            JSONObject holidayObj = jsonObject.getJSONObject("holiday");
            // get by year
            Calendar calendar = Calendar.getInstance();
            int curYear = calendar.get(Calendar.YEAR);

            JSONObject curYearHolidayObj = holidayObj.getJSONObject(String.format("%s",curYear));
            JSONObject curFestivalObj = curYearHolidayObj.getJSONObject("festival");
            JSONObject curWorkdayObj = curYearHolidayObj.getJSONObject("workday");
            // traverse it
            for(int i=0;i<festival_list.length;i++){
                String festival = festival_list[i];
                if(curFestivalObj.has(festival)){
                    JSONArray festivalArr = curFestivalObj.getJSONArray(festival);
                    for(int j=0 ; j<festivalArr.length(); j++){
                        String festivalDay = (String) festivalArr.get(j);
                        if(isDayMatch(festivalDay)){
                            isFestivalHoliday = true;
                            isFestivalWorkDay = false;
                            speacialDayName = festival;
                            return festival;
                        }

                    }
                }
            }
            // for workday
            // traverse it
            for(int i=0;i<festival_list.length;i++){
                String festival = festival_list[i];
                if(curWorkdayObj.has(festival)){
                    JSONArray festivalArr = curWorkdayObj.getJSONArray(festival);
                    for(int j=0 ; j<festivalArr.length();j++){
                        String festivalDay = (String) festivalArr.get(j);
                        if(isDayMatch(festivalDay)){
                            isFestivalHoliday = false;
                            isFestivalWorkDay = true;
                            speacialDayName = festival;
                            return festival;
                        }

                    }
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";

    }
}
