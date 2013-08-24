package com.heibuddy.xiaohuoband.talk;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.heibuddy.xiaohuoband.R;
import com.heibuddy.xiaohuoband.XiaohuobandSettings;

public class ArticleListItemEntity extends BaseListItemEntity {
    public static final String TAG = "ArticleListItemEntity";
    public static final boolean DEBUG = XiaohuobandSettings.DEBUG;
	
	private List<MsgRecvListItemEntity> mArticles;
	
	public ArticleListItemEntity() {
    }
	
	public ArticleListItemEntity(final String toUserName, final String fromUserName,
			final Date createTime, final String msgType, int articleCount,
			final List<MsgRecvListItemEntity> articles) {
		super();
		super.setToUserName(toUserName);
		super.setFromUserName(fromUserName);
		super.setCreateTime(createTime);
		super.setMsgType(msgType);
		this.setArticles(articles);
	}
	
	public int getArticleCount() {
		return this.mArticles.size();
	}
	
	public List<MsgRecvListItemEntity> getArticles() {
		return this.mArticles;
	}

	public void setArticles(List<MsgRecvListItemEntity> articles) {
		this.mArticles = articles;
	}
	
	@Override
	public String toString() {
		return "ListItemEntity [toUserName=" + getToUserName()
				+ ", fromUserName=" + getFromUserName() + ", createTime="
				+ getCreateTime() + ", msgType=" + getMsgType() + ", articleCount="
				+ getArticleCount() + ", articles=" + mArticles + "]";
	}

    public int getViewType() {
        return ListItemEntityType.ARTICLE_LIST_ENTITY.ordinal();
    }

	public ListItemEntityType getItemType() {
		return ListItemEntityType.ARTICLE_LIST_ENTITY;
	}

	public int getItemLayout() {
		return R.layout.item_answer_list1;
	}
	
	public View getView(Context context, View convertView) {
		return null;
	}
	
	public View getView(Context context, OnClickListener onClickListener, View convertView) {
		LinearLayout view = new LinearLayout(context);
		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(this.getItemLayout(), view, true);
		
		LinearLayout ll = (LinearLayout)view.findViewById(R.id.ll_answer_list);
		
		for (int i = 0; i < this.getArticles().size(); i++)
		{
			RelativeLayout innerView = new RelativeLayout(context);
			layoutInflater.inflate(R.layout.item_inner, innerView, true);
			
			MsgRecvListItemEntity msgRecvListItemEntity = this.getArticles().get(i);
			TextView title = (TextView)innerView.findViewById(R.id.title_inner);
			title.setText(msgRecvListItemEntity.getTitle());
			
			TextView description = (TextView)innerView.findViewById(R.id.description_inner);
			description.setText(msgRecvListItemEntity.getDescription());
			
			if ((msgRecvListItemEntity.getPicUrl() != null) && !(msgRecvListItemEntity.getPicUrl().equals("")))
			{
				ImageView image_inner = (ImageView)innerView.findViewById(R.id.image_inner);
				image_inner.setImageBitmap(msgRecvListItemEntity.getBitmap());
			}
			
			if (msgRecvListItemEntity.getUrl() == null || msgRecvListItemEntity.getUrl().equals(""))
			{
				ImageView arrow_inner = (ImageView)innerView.findViewById(R.id.arrow_inner);
				arrow_inner.setBackground(null);
			}
			else
			{
				innerView.setTag(msgRecvListItemEntity.getUrl());
				innerView.setOnClickListener(onClickListener);
			}
			
			ll.addView(innerView);
		}
		
		return view;
	}
}
