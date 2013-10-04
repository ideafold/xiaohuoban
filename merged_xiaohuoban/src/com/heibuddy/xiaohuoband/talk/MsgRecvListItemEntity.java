package com.heibuddy.xiaohuoband.talk;

import com.heibuddy.xiaohuoban.util.NetworkHelper;
import com.heibuddy.xiaohuoband.XiaohuobandSettings;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class MsgRecvListItemEntity {
    public static final String TAG = "MsgRecvListItemEntity";
    public static final boolean DEBUG = XiaohuobandSettings.DEBUG;
    
	private String title;
	private String description;
	private String picUrl = null;
	private String url;
	private Bitmap bitmap = null;
	
	private boolean retrieving = false;
	
	public MsgRecvListItemEntity() {
	}
	
	public MsgRecvListItemEntity(String title, String description,
			String picUrl, String url) {
		this.title = title;
		this.description = description;
		this.picUrl = picUrl;
		this.url = url;
		//this.setPicUrl(this.picUrl);
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getPicUrl() {
		return picUrl;
	}
	
	public void setPicUrl(final String picUrl) {
		this.picUrl = picUrl;
		/*
		if (picUrl != null && (!picUrl.equals(""))){
			if (!retrieving)
			{
				retrieving = true;
				Thread retrievalThread = new Thread(new Runnable(){
					public void run(){
						try {
							byte[] imgData = NetworkHelper.getImage(picUrl);
							bitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
						} catch (Exception e) {
							bitmap = null;
							Log.e(TAG, e.toString());
						}
					}
				});
				retrievalThread.start();
			}
		}
		*/
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
}
