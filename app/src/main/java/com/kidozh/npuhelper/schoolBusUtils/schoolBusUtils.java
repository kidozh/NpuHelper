package com.kidozh.npuhelper.schoolBusUtils;



import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public final class schoolBusUtils {

    private static final String TAG = schoolBusUtils.class.getSimpleName();

    private static final int[] changan2youyiWeekdayTime = {0, 640, 900, 1030, 1100, 1230, 1300, 1400, 1500, 1600, 1710, 1800, 1900, 2100, 2230};
    private static final int[] changan2youyiWeekendTime = {0, 900, 1100, 1230, 1700, 1800, 2100};
    private static final int[] youyi2changanWeekdayTime = {0, 700, 800, 900, 1000, 1100, 1200, 1230, 1400, 1430, 1600, 1700, 1730, 1830, 2100};
    private static final int[] youyi2changanWeekendTime = {0, 800, 900, 1230, 1400, 1800, 2000};

    private static boolean isWeekday(){
        Calendar calendar = Calendar.getInstance();
        int dayOfWeekIndex = calendar.get(Calendar.DAY_OF_WEEK);
        if(dayOfWeekIndex == calendar.SATURDAY || dayOfWeekIndex == calendar.SUNDAY){
            return false;
        }
        else {
            return true;
        }

    }

    public static int getNearestTime(int[] timeSchedule){
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

}
