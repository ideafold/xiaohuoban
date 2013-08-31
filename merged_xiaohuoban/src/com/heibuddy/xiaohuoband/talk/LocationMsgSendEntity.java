package com.heibuddy.xiaohuoband.talk;

import java.io.StringWriter;
import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import android.util.Xml;

import com.heibuddy.xiaohuoband.XiaohuobandSettings;

public class LocationMsgSendEntity extends BaseSendEntity
{
    public static final String TAG = "LocationMsgSendEntity";
    public static final boolean DEBUG = XiaohuobandSettings.DEBUG;
	
    private String mContent;
	private double mLocationX;	//Latitude
	private double mLocationY;	//Longitude
	private double mScale;
	private String mLabel;
	
    public LocationMsgSendEntity(){
    }
    
    public LocationMsgSendEntity(final String ct, double locX, double locY, double scale, final String label)
    {
    	this.mContent = ct;
    	this.mLocationX = locX;
    	this.mLocationY = locY;
    	this.mScale = scale;
    	this.mLabel = label;
    }
    
    public LocationMsgSendEntity(final String toUserName, final String fromUserName,
			 					  final String createTime, final String userId,
			 					  final String ct, double locX, double locY, double scale, final String label)
    {
    	this(ct, locX, locY, scale, label);
		super.setToUserName(toUserName);
		super.setFromUserName(fromUserName);
		super.setCreateTime(createTime);
		super.setMsgType("pubLocation");
		super.setUserId(userId);
    }

	public String getContent() {
		return mContent;
	}

	public void setContent(String mContent) {
		this.mContent = mContent;
	}

	public double getLocationX() {
		return mLocationX;
	}

	public void setLocationX(double mLocationX) {
		this.mLocationX = mLocationX;
	}

	public double getLocationY() {
		return mLocationY;
	}

	public void setLocationY(double mLocationY) {
		this.mLocationY = mLocationY;
	}

	public double getScale() {
		return mScale;
	}

	public void setScale(double mScale) {
		this.mScale = mScale;
	}

	public String getLabel() {
		return mLabel;
	}

	public void setLabel(String mLabel) {
		this.mLabel = mLabel;
	}
	
	@Override
	public String toString() {
		return "MsgSendEntity [toUserName=" + getToUserName() + ", fromUserName="
				+ getFromUserName() + ", createTime=" + getCreateTime() + ", msgType="
				+ getMsgType() + ", content=" + getContent() + ", UserId=" + getUserId()
				+ ", LocX: " + getLocationX() + ", LocY: " + getLocationY() + ", scale: " 
				+ getScale() + ", label: " + getLabel() + "]";
	}

	@Override
	public String parceToXML() {
		String text = null;
		
        try {  
            XmlSerializer serializer = Xml.newSerializer();  
            StringWriter writer = new StringWriter();  
            
            serializer.setOutput(writer);  
            serializer.startDocument("UTF-8",true);  
                  
            serializer.startTag("", "xml");  
                
            serializer.startTag("", "ToUserName");  
            serializer.cdsect(getToUserName()); 
            serializer.endTag("", "ToUserName");  
                
            serializer.startTag("", "FromUserName");  
            serializer.cdsect(getFromUserName()); 
            serializer.endTag("", "FromUserName");  
                
            serializer.startTag("", "CreateTime");  
            serializer.cdsect(getCreateTime()); 
            serializer.endTag("", "CreateTime");  
                
            serializer.startTag("", "MsgType"); 
            serializer.cdsect("pubLocation");
            serializer.endTag("", "MsgType");  
            
            serializer.startTag("", "Location_X");  
            serializer.text(String.valueOf(mLocationX)); 
            serializer.endTag("", "Location_X");  
            
            serializer.startTag("", "Location_Y");  
            serializer.text(String.valueOf(mLocationY)); 
            serializer.endTag("", "Location_Y");  
            
            serializer.startTag("", "Scale");  
            serializer.text(String.valueOf(mScale));  
            serializer.endTag("", "Scale"); 
            
            serializer.startTag("", "Label");  
            serializer.cdsect(mLabel); 
            serializer.endTag("", "Label"); 
            
            serializer.startTag("", "Content");  
            serializer.cdsect(getContent()); 
            serializer.endTag("", "Content");  
                
            serializer.startTag("", "UserId");  
            serializer.text(getUserId()); 
            serializer.endTag("", "UserId");  
                
            serializer.endTag("", "xml");  
  
            serializer.endDocument();  
            text = writer.toString();  
        } catch (Exception e) {  
            Log.e(TAG, e.toString()); 
        }  		
		
		return text;
	}
	
}