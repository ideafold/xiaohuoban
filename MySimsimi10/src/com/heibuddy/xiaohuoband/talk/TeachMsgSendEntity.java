package com.heibuddy.xiaohuoband.talk;

import java.io.StringWriter;
import org.xmlpull.v1.XmlSerializer;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.Xml;

import com.heibuddy.xiaohuoband.XiaohuobandSettings;

public class TeachMsgSendEntity extends BaseSendEntity
{
    public static final String TAG = "TeachMsgSendEntity";
    public static final boolean DEBUG = XiaohuobandSettings.DEBUG;
    
    private String mQuestion;
    private String mAnswer;
    private Bitmap mImage;
    private boolean mIsPrivate;
    
    public TeachMsgSendEntity(){
    }
    
    public TeachMsgSendEntity(final String quesiton, final String answer, 
    						  final Bitmap image, boolean isprivate){
    	this.mQuestion = quesiton;
    	this.mAnswer = answer;
    	this.mImage = image;
    	this.mIsPrivate = isprivate; 
    }
    
	public TeachMsgSendEntity(final String toUserName, final String fromUserName,
			 				 final String createTime, final String userId, 
			 				 final String quesiton, final String answer, 
			 				 final Bitmap image, boolean isPrivate) 
	{
		this(quesiton, answer, image, isPrivate);
		super.setToUserName(toUserName);
		super.setFromUserName(fromUserName);
		super.setCreateTime(createTime);
		if (!mIsPrivate)
		{
			super.setMsgType("pubTeach");
		}
		else
		{
			super.setMsgType("privateTeach");
		}
		super.setUserId(userId);
	}

	public String getQuestion() {
		return mQuestion;
	}

	public void setQuestion(String mQuestion) {
		this.mQuestion = mQuestion;
	}

	public String getAnswer() {
		return mAnswer;
	}

	public void setAnswer(String mAnswer) {
		this.mAnswer = mAnswer;
	}

	public Bitmap getImage() {
		return mImage;
	}

	public void setImage(Bitmap mImage) {
		this.mImage = mImage;
	}

	public boolean isPrivate() {
		return mIsPrivate;
	}

	public void setIsPrivate(boolean mIsPrivate) {
		this.mIsPrivate = mIsPrivate;
	}
    
	@Override
	public String toString() {
		return "MsgSendEntity [toUserName=" + getToUserName() + ", fromUserName="
				+ getFromUserName() + ", createTime=" + getCreateTime() + ", msgType="
				+ getMsgType() + ", question=" + getQuestion() + ", answer=" + getAnswer()
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
            	serializer.cdsect("pubTeach");
            }
            else
            {
                serializer.cdsect("privateTeach");
            }
            serializer.endTag("", "MsgType");  
                
            serializer.startTag("", "Question");  
            serializer.cdsect(getQuestion()); 
            serializer.endTag("", "Question");  
                
            serializer.startTag("", "Answer");  
            serializer.cdsect(getAnswer()); 
            serializer.endTag("", "Answer");  
            
            serializer.startTag("", "Image");  
            serializer.cdsect(""); 
            serializer.endTag("", "Image");  
            
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