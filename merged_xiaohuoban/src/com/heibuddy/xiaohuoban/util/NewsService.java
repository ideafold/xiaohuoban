package com.heibuddy.xiaohuoban.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

import com.heibuddy.xiaohuoband.XiaohuobandSettings;

public class NewsService
{
    public static final String TAG = "NewsService";
    public static final boolean DEBUG = XiaohuobandSettings.DEBUG;
    
	public static final String SETTING_INFOS = "setting_infos";
	public static final String LAST_DISPLAY_NEWS_TIME = "last_display_news_time";
	public static final String TODAY_DISPLAY_NEWS_TIMES = "today_display_news_times";
	
	//timeGapToDisplay unit is millisecond
	public static boolean isNeedingToPullNews(Context context, long minTimeGapToDisplay, int maxCountEveryDay)
	{
		//get current  time, in millisecond
		 long now = new Date().getTime();
		 long lastDisplayTime = getLastDisplayTimeFromPreferencesDB(context);
		 if (now - lastDisplayTime < minTimeGapToDisplay)
		 {
			 if (DEBUG) Log.d(TAG, "now - lastDisplayTime < minTimeGapToDisplay");
			 return false;
		 }

		 //check whether now it is a new day, if yes, set the displayedCountToday to zero
		 if (!isSameDay(now, lastDisplayTime))
		 {
			 if (!updateTodayNewsDisplayTimesToPreferencesDB(context, 0))
			 {
				 if (DEBUG) Log.i(TAG, "updateTodayNewsDisplayTimesToPreferencesDB failed!");
				 return false;
			 }
		 }
		 
		 int displayedCountToday = getTodayNewsDisplayTimesFromPreferencesDB(context);
		 if (displayedCountToday > maxCountEveryDay)
		 {
			 if (DEBUG) Log.d(TAG, "displayedCountToday > maxCountEveryDay");
			 return false;
		 }
		 
		 if (DEBUG) Log.d(TAG, "need to pull news");
		 return true;
	}
	
	public static boolean isSameDay(long now, long lastDisplayTime)
	{
		 Date nowDate = new Date(now);
		 Date lastDisplayDate = new Date(lastDisplayTime);
		 Calendar nowCal = Calendar.getInstance();
		 Calendar lastDisplayCal = Calendar.getInstance();
		 nowCal.setTime(nowDate);
		 lastDisplayCal.setTime(lastDisplayDate);
		 return nowCal.get(Calendar.YEAR) == lastDisplayCal.get(Calendar.YEAR) &&
				 		 	nowCal.get(Calendar.DAY_OF_YEAR) == nowCal.get(Calendar.DAY_OF_YEAR);		
	}
	
	public static boolean updateLastDisplayTimeToPreferencesDB(Context context, long time)
	{
		SharedPreferences settings = context.getSharedPreferences(SETTING_INFOS, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong(LAST_DISPLAY_NEWS_TIME, time);
		return editor.commit();
	}	
	
	public static long getLastDisplayTimeFromPreferencesDB(Context context)
	{
		SharedPreferences settings = context.getSharedPreferences(SETTING_INFOS, 0);
		return settings.getLong(LAST_DISPLAY_NEWS_TIME, 0);
	}
	
	public static boolean updateTodayNewsDisplayTimesToPreferencesDB(Context context, int count)
	{
		SharedPreferences settings = context.getSharedPreferences(SETTING_INFOS, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(TODAY_DISPLAY_NEWS_TIMES, count);
		return editor.commit();
	}
	
	public static int getTodayNewsDisplayTimesFromPreferencesDB(Context context)
	{
		SharedPreferences settings = context.getSharedPreferences(SETTING_INFOS, 0);
		return settings.getInt(TODAY_DISPLAY_NEWS_TIMES, 0);
	}	
}