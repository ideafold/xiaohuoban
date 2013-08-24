package com.heibuddy.xiaohuoban.util;

import com.heibuddy.xiaohuoband.XiaohuobandSettings;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.content.SharedPreferences;

public class LocationService
{
	public enum  HuobanLocationType {RESIDENT, LAST_VALID} 
	 
	public static final String SETTING_INFOS = "setting_infos";
	public static final String RESIDENT_LOCATION_LON = "residentAddrLon";
	public static final String RESIDENT_LOCATION_LAT = "residentAddrLat";	
	
	public static final String LAST_VALID_LOCAITON_LON = "lastValidAddrLon";
	public static final String LAST_VALID_LOCAITON_LAT = "lastValidAddrLat";	

	public static boolean isGoingFarAway(Context context, Location location, float minDistanceToHome, float minDistanceToLastLocation)
	{
		float distance = 0.00f;
		distance = getDistanceToResidentLocation(context, location);
		if (distance < minDistanceToHome)
		{
			if (XiaohuobandSettings.DEBUG)
        	{
				Log.i("LocationService", String.valueOf(distance) + "is less than minDistanceToHome: "
						+ String.valueOf(minDistanceToHome));
        	}
			return false;
		}
		
		distance =  getDistanceToLastValidLocation(context, location);
		if (distance < minDistanceToLastLocation)
		{
			if (XiaohuobandSettings.DEBUG)
        	{
				Log.i("LocationService", String.valueOf(distance) + "is less than minDistanceToLastLocation: " 
							+ String.valueOf(minDistanceToLastLocation));
        	}
			return false;
		}
		
		if (XiaohuobandSettings.DEBUG)
    	{
			Log.i("LocationService", "It does go far away!");
    	}
		
		return true;
	}
	
	public static float getDistanceToLastValidLocation(Context context, Location currentLocation){
		//retrieval the last valid location from SharedPreferences db
		Location lastValidAddr = getLocationFromPreferencesDB(context, HuobanLocationType.LAST_VALID);
		return currentLocation.distanceTo(lastValidAddr);
	}

	public static float getDistanceToResidentLocation(Context context, Location currentLocation){
		//retrieval the resident location from SharedPreferences db
		Location resident = getLocationFromPreferencesDB(context, HuobanLocationType.RESIDENT);
		return currentLocation.distanceTo(resident);
	}	
	
	public static boolean updateLastValidLocationToPreferencesDB(Context context, Location location)
	{
		return storeLocationToPreferencesDB(context, HuobanLocationType.LAST_VALID, location);
	}
	
	public static boolean storeLocationToPreferencesDB(Context context, HuobanLocationType type, Location location)
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
	
	public static Location getLocationFromPreferencesDB(Context context, HuobanLocationType type)
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
		
		LocationManager mgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location location = mgr.getLastKnownLocation("network");
        if (lon < 0.01f && lat < 0.01f)
        {
        	if (XiaohuobandSettings.DEBUG)
        	{
        		Log.i("LocationService", "old locaiton had lon < 0.01f && lat < 0.01f, using current user network location");
        	}
        	return location;
        }
        
		location.setLongitude(lon);
		location.setLatitude(lat);
		
		Log.i("LocationService getLocationFromPreferencesDB return: ", location.toString());
		return location;
	}
}