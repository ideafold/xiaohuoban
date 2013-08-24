package com.heibuddy.xiaohuoband.talk;

import android.util.Log;

public class ListItemEntityFactory
{
	public static BaseListItemEntity createLisItemEntity(final String msgType)
	{
		ListItemEntityType type =  mapMsgTypeToListItemType(msgType);
		if (type == ListItemEntityType.SIMPLE_ANSWER_ENTITY)
		{
			return new SimpleAnswerItemEntity();
		}
		if (type == ListItemEntityType.ARTICLE_LIST_ENTITY)
		{
			return new ArticleListItemEntity();
		}
		
		return null;
	}
	
	public static ListItemEntityType mapMsgTypeToListItemType(final String msgType)
	{
		if (msgType.equals("pubText") || msgType.equals("privateText"))
		{
			return ListItemEntityType.SIMPLE_ANSWER_ENTITY;
		}
		else if (msgType.equals("pubArticle") || msgType.equals("privateArticle"))
		{
			return ListItemEntityType.ARTICLE_LIST_ENTITY;
		}
		else if (msgType.equals("teachResp"))
		{
			Log.e("ListItemEntityFactory", "Not implemented yet!");
		}
		
		return ListItemEntityType.UNKNOWN_ENTITY;
	}
}