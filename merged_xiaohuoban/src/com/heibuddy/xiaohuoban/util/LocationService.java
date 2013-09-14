package com.heibuddy.xiaohuoban.util;

import com.baidu.location.BDLocation;
import com.heibuddy.xiaohuoband.XiaohuobandSettings;

import android.content.Context;
import android.util.Log;
import android.content.SharedPreferences;
import android.location.Location;

public class LocationService
{
    public static final String TAG = "LocationService";
    public static final boolean DEBUG = XiaohuobandSettings.DEBUG;
    
	public enum  HuobanLocationType {RESIDENT, LAST_VALID} 
	 
	public static final String SETTING_INFOS = "setting_infos";
	public static final String RESIDENT_LOCATION_LON = "residentAddrLon";
	public static final String RESIDENT_LOCATION_LAT = "residentAddrLat";	
	
	public static final String LAST_VALID_LOCAITON_LON = "lastValidAddrLon";
	public static final String LAST_VALID_LOCAITON_LAT = "lastValidAddrLat";	

	public static boolean isGoingFarAway(Context context, BDLocation location, float minDistanceToHome, float minDistanceToLastLocation)
	{
		float distance = 0.00f;
		distance = getDistanceToResidentLocation(context, location);
		if (distance < minDistanceToHome)
		{
			if (DEBUG)
        	{
				Log.i("LocationService", String.valueOf(distance) + "is less than minDistanceToHome: "
						+ String.valueOf(minDistanceToHome));
        	}
			return false;
		}
		
		distance =  getDistanceToLastValidLocation(context, location);
		if (distance < minDistanceToLastLocation)
		{
			if (DEBUG)
        	{
				Log.i("LocationService", String.valueOf(distance) + "is less than minDistanceToLastLocation: " 
							+ String.valueOf(minDistanceToLastLocation));
        	}
			return false;
		}
		
		if (DEBUG)
		{
			Log.i(TAG, "It does go far away!");
		}
		
		return true;
	}
	
	public static float getDistanceToLastValidLocation(Context context, BDLocation currentLocation){
		//retrieval the last valid location from SharedPreferences db
		BDLocation lastValidAddr = getLocationFromPreferencesDB(context, HuobanLocationType.LAST_VALID);
		return distanceTo(currentLocation, lastValidAddr);
	}

	public static float getDistanceToResidentLocation(Context context, BDLocation currentLocation){
		//retrieval the resident location from SharedPreferences db
		BDLocation resident = getLocationFromPreferencesDB(context, HuobanLocationType.RESIDENT);
		return distanceTo(currentLocation, resident);
	}	
	
	private static float distanceTo(BDLocation from, BDLocation to){
		if(from != null || to != null){
			float[] results=new float[1]; 
			Location.distanceBetween(from.getLatitude(), from.getLongitude(), to.getLatitude(), to.getLongitude(), results);
			return results[0];
		}
		return -1;
	}
	
	public static boolean updateLastValidLocationToPreferencesDB(Context context, BDLocation location)
	{
		return storeLocationToPreferencesDB(context, HuobanLocationType.LAST_VALID, location);
	}
	
	public static boolean storeLocationToPreferencesDB(Context context, HuobanLocationType type, BDLocation location)
	{
		SharedPreferences settings = context.getSharedPreferences(SETTING_INFOS, 0);
		SharedPreferences.Editor editor = settings.edit();
		if (type == HuobanLocationType.RESIDENT)
		{
			editor.putFloat(RESIDENT_LOCATION_LON, (float)location.getLongitude());
			editor.putFloat(RESIDENT_LOCATION_LAT, (float)location.getLatitude());
			editor.commit();
		}
		else if (type == HuobanLocationType.LAST_VALID)
		{
			editor.putFloat(LAST_VALID_LOCAITON_LON, (float)location.getLongitude());
			editor.putFloat(LAST_VALID_LOCAITON_LAT, (float)location.getLatitude());
			editor.commit();
		}
		else
		{
			//unknow type
			return false;
		}
		
		return true;
	}	
	
	public static BDLocation getLocationFromPreferencesDB(Context context, HuobanLocationType type)
	{
		SharedPreferences settings = context.getSharedPreferences(SETTING_INFOS, 0);
		float lon = 0.0f;
		float lat = 0.0f;
		if (type == HuobanLocationType.RESIDENT)
		{
			lon = settings.getFloat(RESIDENT_LOCATION_LON, 0.0f);
			lat = settings.getFloat(RESIDENT_LOCATION_LAT, 0.0f);
		}
		else if (type == HuobanLocationType.LAST_VALID)
		{
			lon = settings.getFloat(LAST_VALID_LOCAITON_LON, 0.0f);
			lat = settings.getFloat(LAST_VALID_LOCAITON_LAT, 0.0f);
		}
		BDLocation location = new BDLocation();
		
        if (lon < 0.01f && lat < 0.01f)
        {
        	if (DEBUG)
        	{
        		Log.i("LocationService", "old locaiton had lon < 0.01f && lat < 0.01f, using current user network location");
        	}
        	return location;
        }
        
		location.setLongitude(lon);
		location.setLatitude(lat);
		
		if (DEBUG) Log.i("LocationService getLocationFromPreferencesDB return: ", location.toString());
		return location;
	}
}