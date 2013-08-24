package com.heibuddy.xiaohuoband.talk;

import java.io.StringWriter;

import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import android.util.Xml;

import com.heibuddy.xiaohuoband.XiaohuobandSettings;

public class NewsMsgSendEntity extends BaseSendEntity
{
    public static final String TAG = "NewsMsgSendEntity";
    public static final boolean DEBUG = XiaohuobandSettings.DEBUG;
    
    public NewsMsgSendEntity(){
    }
    
	public NewsMsgSendEntity(final String toUserName, final String fromUserName,
			 				final String createTime, final String userId) {
		super.setToUserName(toUserName);
		super.setFromUserName(fromUserName);
		super.setCreateTime(createTime);
		super.setMsgType("pubNews");
		super.setUserId(userId);
	}
    
	@Override
	public String toString() {
		return "MsgSendEntity [toUserName=" + getToUserName() + ", fromUserName="
				+ getFromUserName() + ", createTime=" + getCreateTime() + ", msgType="
				+ getMsgType() + "]";
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
            serializer.cdsect(getMsgType());
            serializer.endTag("", "MsgType");
                
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