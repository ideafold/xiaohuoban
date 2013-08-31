package com.heibuddy.xiaohuoband.talk;

import java.util.Date;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

public abstract class BaseListItemEntity
{
	private String mToUserName;
	private String mFromUserName;
	private Date mCreateTime;
	private String mMsgType;
	
	public String getToUserName() {
		return this.mToUserName;
	}
	
	public void setToUserName(String toUserName) {
		this.mToUserName = toUserName;
	}
	
	public String getFromUserName() {
		return this.mFromUserName;
	}
	
	public void setFromUserName(String fromUserName) {
		this.mFromUserName = fromUserName;
	}
	
	public Date getCreateTime() {
		return this.mCreateTime;
	}
	
	public void setCreateTime(Date createTime) {
		this.mCreateTime = createTime;
	}
	
	public String getMsgType() {
		return this.mMsgType;
	}
	
	public void setMsgType(String msgType) {
		this.mMsgType = msgType;
	}
	
	public abstract ListItemEntityType getItemType();
	
    public abstract int getItemLayout();
	
    public abstract View getView(Context context, View convertView);
    
    public abstract View getView(Context context, OnClickListener onClickListener, View convertView);
    
    public abstract int getViewType();
    
}