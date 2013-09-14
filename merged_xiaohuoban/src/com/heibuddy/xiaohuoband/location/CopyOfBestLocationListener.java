package com.heibuddy.xiaohuoband.location;

import java.util.Observable;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.heibuddy.xiaohuoban.util.LocationService;

public class CopyOfBestLocationListener extends Observable implements BDLocationListener {

	BDLocation mLastLocation;
	Context mContext;
	
	public CopyOfBestLocationListener(Context c){
		mContext = c;
	}
	
	@Override
	public void onReceiveLocation(BDLocation location) {
		if(location == null){
			return;
		}
		if(mLastLocation == null){
			onLocationChanged(location);
		}else if(LocationService.getDistanceToLastValidLocation(mContext, location) > 0.1){
			onLocationChanged(location);
		}
		mLastLocation = location;
	}

	@Override
	public void onReceivePoi(BDLocation location) {
		
	}
	
	public void onLocationChanged(BDLocation location){
		setChanged();
		notifyObservers(location);
	}
}
