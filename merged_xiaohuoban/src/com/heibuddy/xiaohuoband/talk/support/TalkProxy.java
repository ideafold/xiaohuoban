package com.heibuddy.xiaohuoband.talk.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.util.Log;

import com.heibuddy.xiaohuoban.util.DesHelper;
import com.heibuddy.xiaohuoban.util.NetworkHelper;
import com.heibuddy.xiaohuoband.XiaohuobandSettings;
import com.heibuddy.xiaohuoband.talk.BaseListItemEntity;
import com.heibuddy.xiaohuoband.talk.BaseSendEntity;

public class TalkProxy {
    private static final String TAG = "NetUtil";
    private static final boolean DEBUG = XiaohuobandSettings.DEBUG;
    private static final String TALK_SERVER_URL = "http://www.360island.com:8080/xiaohuoban/"; 
    private static final String SECRET = "GZ9Gn2U5nhpea8hw";
    private static final String IV = "70301000";
    private static DesHelper.DESCrypto aesWrapper = new DesHelper.DESCrypto(SECRET, IV);
    
	public synchronized static BaseListItemEntity sendMessage(BaseSendEntity msg, Context context) throws IOException
	{
		if (msg == null)
		{
			Log.e(TAG, "Input BaseSendEntity is null!");
			return null;
		}
		
		String sendXml = msg.parceToXML();
		if (sendXml == null)
		{
			Log.e(TAG, "sendXml is null!");
			return null;
		}
		if (DEBUG) Log.d(TAG, "sendXml:" + sendXml);
		
		if (NetworkHelper.isConnected(context) == false){
			if (DEBUG) Log.d(TAG, "Network unavailable!");
			throw new IOException("Network unavailable");
        }
		
		/*
		String encryptedText = aesWrapper.encrypt(sendXml);
		if (DEBUG){
			Log.d(TAG, encryptedText);
			Log.d(TAG, "length: " + encryptedText.length());
			String decryptedText = aesWrapper.decrypt(encryptedText);
			if (!decryptedText.equals(sendXml)){
				Log.e(TAG, "decryptedText is not equal to input!!");
			}
		}
		String res = NetworkHelper.sendRequestFromHttpClient(TalkProxy.TALK_SERVER_URL, encryptedText);
		*/
		String res = NetworkHelper.sendRequestFromHttpClient(TalkProxy.TALK_SERVER_URL, sendXml);
		if (res == null)
		{
			Log.e(TAG, "response of sendRequestFromHttpClient is null!");
			return null;
		}

		/*
		String decryptedText = aesWrapper.decrypt(res);
		if (decryptedText == null){
			Log.e(TAG, "aesWrapper.decrypt return is null!");
			return null;
		}
		decryptedText.replaceAll("\\.*UserName><!", "<xml><ToUserName><!");
		
		Log.d(TAG, "Recv content is " + decryptedText);
		*/
		
		//return parseXML(decryptedText);
		return parseXML(res, context);
	}
	
	public static String getExampleXML(String fileName, Context context){
		String result = "";
		try {
			InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName)); 
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line = "";
			while ((line = bufReader.readLine()) != null)
			{
				result += line;
			}
		} catch (Exception e) { 
			e.printStackTrace(); 
		}
		
		return result;
	}
	
	public static BaseListItemEntity parseXML(String xmlString, Context context){
		BaseListItemEntity listItemEntity = null;
		try{
			//create a SAXParserFactory
			SAXParserFactory factory = SAXParserFactory.newInstance();
			XMLReader reader = factory.newSAXParser().getXMLReader();
			
			//set handler for XMLReader
			MsgRecvContentHandler msgRecvContentHandler = new MsgRecvContentHandler();
			reader.setContentHandler(msgRecvContentHandler);
			
			//do parse
			reader.parse(new InputSource(new StringReader(xmlString)));
			listItemEntity = msgRecvContentHandler.getData(context);
		}
		catch(Exception e){
			Log.e(TAG, e.toString());
		}
		
		return listItemEntity;
	}
}
