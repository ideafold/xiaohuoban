package com.heibuddy.utils;

import com.heibuddy.ui.activities.BrowserMainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ProxyChangeReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) 
    {
    	Log.d("ProxyChangeReceiver", "Proxy change receiver called: " + intent.toString());
    	
    	if (BrowserMainActivity.INSTANCE != null)
    	{
    		Log.d("ProxyChangeReceiver", "Refresh system preferences");
    		BrowserMainActivity.INSTANCE.applyPreferences();
    	}
    }
}
