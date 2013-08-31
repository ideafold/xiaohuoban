package com.heibuddy.xiaohuoband.talk;

import java.io.StringWriter;
import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import android.util.Xml;

import com.heibuddy.xiaohuoband.XiaohuobandSettings;

public class TextMsgSendEntity extends BaseSendEntity
{
    public static final String TAG = "TextMsgSendEntity";
    public static final boolean DEBUG = XiaohuobandSettings.DEBUG;
	
    private String mContent;
    private boolean mIsPrivate;
    
    public TextMsgSendEntity(){
    }
    
    public TextMsgSendEntity(final String ct, boolean isPrivate){
    	this.mContent = ct;
    	this.mIsPrivate = isPrivate;
    }
    
	public TextMsgSendEntity(final String toUserName, final String fromUserName,
							 final String createTime, final String userId, 
							 final String ct, boolean isPrivate) {
		super();
		super.setToUserName(toUserName);
		super.setFromUserName(fromUserName);
		super.setCreateTime(createTime);
		if (!mIsPrivate)
		{
			super.setMsgType("pubTexts");
		}
		else
		{
			super.setMsgType("privateText");
		}
		super.setUserId(userId);
		this.mContent = ct;
		this.mIsPrivate = isPrivate;
	}
	
	public String getContent(){
		return this.mContent;
	}
	
	public void setContent(final String ct){
		this.mContent = ct;
	}
	
	public boolean isPrivate(){
		return this.mIsPrivate;
	}
	
	public void setIsPrivate(boolean isPrivate){
		this.mIsPrivate = isPrivate;
	}
	
	@Override
	public String toString() {
		return "MsgSendEntity [toUserName=" + getToUserName() + ", fromUserName="
				+ getFromUserName() + ", createTime=" + getCreateTime() + ", msgType="
				+ getMsgType() + ", content=" + getContent() + ", UserId=" + getUserId()
				+ ", isPrivate: " + isPrivate() + "]";
	}
	
	public String parceToXML(){
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
            if (!mIsPrivate)
            {
            	serializer.cdsect("pubText");
            }
            else
            {
                serializer.cdsect("privateText");
            }
            serializer.endTag("", "MsgType");  
                
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