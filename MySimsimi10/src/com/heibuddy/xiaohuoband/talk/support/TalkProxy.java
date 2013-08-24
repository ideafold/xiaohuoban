package com.heibuddy.xiaohuoband.talk.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.heibuddy.xiaohuoban.util.NetworkHelper;
import com.heibuddy.xiaohuoband.XiaohuobandSettings;
import com.heibuddy.xiaohuoband.talk.BaseListItemEntity;
import com.heibuddy.xiaohuoband.talk.BaseSendEntity;

public class TalkProxy {
    private static final String TAG = "NetUtil";
    private static final boolean DEBUG = XiaohuobandSettings.DEBUG;
    
    private static final String TALK_SERVER_URL = "http://42.121.52.246/xiaohuoban/"; 
    
	public synchronized static BaseListItemEntity getAnswer(BaseSendEntity msg, Context context) throws IOException
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
		if (DEBUG) Log.d(TAG, "sendXml:" + msg);
		
		if (NetworkHelper.isConnected(context) == false){
			if (DEBUG) Log.d(TAG, "Network unavailable!");
			throw new IOException("Network unavailable");
        }
		
		//TODO
//		sendXml = getExampleXML("post.xml",context);
		
		String res = NetworkHelper.sendRequestFromHttpClient(TalkProxy.TALK_SERVER_URL, sendXml);
		if (res == null)
		{
			Log.e(TAG, "response of sendRequestFromHttpClient is null!");
			return null;
		}
		if (DEBUG) Log.d(TAG, "Recv content is " + res);
		
		return parseXML(res);
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
	
	public static BaseListItemEntity parseXML(String xmlString){
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
			listItemEntity = msgRecvContentHandler.getData();
		}
		catch(Exception e){
			Log.e(TAG, e.toString());
		}
		
		return listItemEntity;
	}
}
