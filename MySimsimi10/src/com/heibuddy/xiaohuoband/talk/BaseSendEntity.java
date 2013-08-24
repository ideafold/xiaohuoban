package com.heibuddy.xiaohuoband.talk;

public abstract class BaseSendEntity
{
	private String mToUserName;
	private String mFromUserName;
	private String mCreateTime;
	private String mMsgType;
	private String mUserId;
	
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
	
	public String getCreateTime() {
		return this.mCreateTime;
	}
	
	public void setCreateTime(String createTime) {
		this.mCreateTime = createTime;
	}
	
	public String getMsgType() {
		return this.mMsgType;
	}
	
	public void setMsgType(String msgType) {
		this.mMsgType = msgType;
	}
	
	public String getUserId() {
		return this.mUserId;
	}
	
	public void setUserId(String userId) {
		this.mUserId = userId;
	}
	
	public abstract String parceToXML();
}