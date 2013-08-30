package com.heibuddy.xiaohuoband.talk.support;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.heibuddy.xiaohuoband.XiaohuobandSettings;
import com.heibuddy.xiaohuoband.talk.ArticleListItemEntity;
import com.heibuddy.xiaohuoband.talk.BaseListItemEntity;
import com.heibuddy.xiaohuoband.talk.ListItemEntityFactory;
import com.heibuddy.xiaohuoband.talk.MsgRecvListItemEntity;
import com.heibuddy.xiaohuoband.talk.SimpleAnswerItemEntity;

public class MsgRecvContentHandler extends DefaultHandler {
    private static final String TAG = "MsgRecvContentHandler";
    private static final boolean DEBUG = XiaohuobandSettings.DEBUG;
    
	private String mToUserName;
	private String mFromUserName;
	private Date mCreateTime;
	private String mMsgType;
	
	private String mContent;
	private String mUrl;
	private int mArticleCount;
	private List<MsgRecvListItemEntity> mArticles;
	private MsgRecvListItemEntity mItemEntity;
	
	private String mTagName;
	private boolean mInItem;

	private StringBuffer mBuffer = new StringBuffer();
	
	@Override
	public void startDocument() throws SAXException {
		if (DEBUG) Log.d(TAG, "````````begin````````");
		mArticles = new ArrayList<MsgRecvListItemEntity>();
		mInItem = false;
	}

	@Override
	public void endDocument() throws SAXException {
		if (DEBUG) Log.d(TAG, "````````end````````");
		if (mArticleCount != mArticles.size())
		{
			Log.e(TAG, "Receive a uncomplete article message!");
		}
	}

	@Override
	public void startElement(String namespaceURI, String localName,
							 String qName, Attributes attr) throws SAXException {
		if (DEBUG) Log.d(TAG, "````````startElement````````" + localName);
		mTagName = localName;
		if (mTagName.equals("item")) {
			mInItem = true;
			mItemEntity = new MsgRecvListItemEntity();
		}
	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName)
							throws SAXException {
		if (DEBUG) Log.d(TAG, "````````endElement````````" + localName);
		
		if (mTagName.equals("ToUserName"))
		{
			this.mToUserName = mBuffer.toString().trim();
		}
		else if (mTagName.equals("FromUserName"))
		{
			this.mFromUserName = mBuffer.toString().trim();
		}
		else if (mTagName.equals("CreateTime")) 
		{
			String str = mBuffer.toString();
			long recvSecond = Long.parseLong(str.trim());
			Date dt = new Date(recvSecond * 1000);
			this.mCreateTime = dt;
		} 
		else if (mTagName.equals("MsgType"))
		{
			this.mMsgType = mBuffer.toString().trim();
		}
		else if (mTagName.equals("ArticleCount"))
		{
			String str = mBuffer.toString();
			this.mArticleCount = Integer.parseInt(str.trim());
		}
		else if (mInItem && mTagName.equals("Title"))
		{
			mItemEntity.setTitle(mBuffer.toString().trim());
		}
		else if (mInItem && mTagName.equals("Description"))
		{
			mItemEntity.setDescription(mBuffer.toString().trim());
		}
		else if (mInItem && mTagName.equals("PicUrl"))
		{
			mItemEntity.setPicUrl(mBuffer.toString().trim());
		}
		else if (mInItem && mTagName.equals("Url"))
		{
			mItemEntity.setUrl(mBuffer.toString().trim());
		}
		else if (mTagName.equals("Content"))
		{
			this.mContent = mBuffer.toString().trim();
		}
		else if ((!mInItem) && mTagName.equals("Url"))
		{
			this.mUrl = mBuffer.toString().trim();
		}
		
		mTagName = "";
		if (localName.equals("item")) {
			mArticles.add(mItemEntity);
			mInItem = false;
		}
		mBuffer = new StringBuffer();
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
							throws SAXException {
		mBuffer.append(new String(ch, start, length));
	}

	public BaseListItemEntity getData(){
		BaseListItemEntity entity = ListItemEntityFactory.createLisItemEntity(mMsgType);
		if (entity == null)
		{
			Log.e(TAG, "Oops, unknown msgType: " + mMsgType);
			return null;
		}
		
		if (mMsgType.equals("pubText") || mMsgType.equals("privateText"))
		{
			if (DEBUG) Log.d(TAG, "It's pubText or privateText");
			SimpleAnswerItemEntity sa = (SimpleAnswerItemEntity)entity;
			sa.setToUserName(mToUserName);
			sa.setFromUserName(mFromUserName);
			sa.setCreateTime(mCreateTime);
			sa.setMsgType(mMsgType);
			sa.setContent(mContent);
			sa.setUrl(mUrl);
			return sa;
		}
		else if (mMsgType.equals("pubArticle") || mMsgType.equals("privateArticle"))
		{
			if (DEBUG) Log.d(TAG, "It's pubArticle or privateArticle");
			ArticleListItemEntity article = (ArticleListItemEntity)entity;
			article.setToUserName(mToUserName);
			article.setFromUserName(mFromUserName);
			article.setCreateTime(mCreateTime);
			article.setMsgType(mMsgType);
			article.setArticles(mArticles);
			return article;
		}
		else if (mMsgType.equals("teachResp"))
		{
			Log.e(TAG, "Not implemented yet!");
			return null;
		}
		
		return null;
	}
}
