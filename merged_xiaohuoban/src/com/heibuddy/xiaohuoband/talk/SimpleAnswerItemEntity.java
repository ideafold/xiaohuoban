package com.heibuddy.xiaohuoband.talk;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.heibuddy.R;
import com.heibuddy.xiaohuoband.XiaohuobandSettings;

public class SimpleAnswerItemEntity extends BaseListItemEntity {
    public static final String TAG = "SimpleAnswerItemEntity";
    public static final boolean DEBUG = XiaohuobandSettings.DEBUG;
	
    private String mContent;
    private String mUrl;
    
    public SimpleAnswerItemEntity() {
    }
    
    public SimpleAnswerItemEntity(final String ct) {
    	this.mContent = ct;
    }
    
    public SimpleAnswerItemEntity(final String ct, final String url) {
    	this.mContent = ct;
    	this.mUrl = url;
    }

	public SimpleAnswerItemEntity(final String toUserName, final String fromUserName,
			final Date createTime, final String msgType, final String ct, final String url) {
		super();
		super.setToUserName(toUserName);
		super.setFromUserName(fromUserName);
		super.setCreateTime(createTime);
		super.setMsgType(msgType);
		this.setContent(ct);
		this.setUrl(url);
	}
	
	public String getContent(){
		return this.mContent;
	}
	
	public void setContent(final String ct){
		this.mContent = ct;
	}
	
	public String getUrl(){
		return this.mUrl;
	}
	
	public void setUrl(final String url){
		this.mUrl = url;
	}
	
	@Override
	public String toString() {
		return "ListItemEntity [toUserName=" + getToUserName()
				+ ", fromUserName=" + getFromUserName() + ", createTime="
				+ getCreateTime() + ", msgType=" + getMsgType() + ", content="
				+ getContent() + ", url=" + getUrl() + "]";
	}
	
    public int getViewType() {
        return ListItemEntityType.SIMPLE_ANSWER_ENTITY.ordinal();
    }

	public ListItemEntityType getItemType() {
		return ListItemEntityType.SIMPLE_ANSWER_ENTITY;
	}

	public int getItemLayout() {
		return R.layout.item_answer_text1;
	}

	public View getView(Context context, OnClickListener onClickListener, View convertView) {
		LinearLayout view = new LinearLayout(context);
		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(this.getItemLayout(), view, true);			
		
		TextView textView = (TextView) view.findViewById(R.id.tv_item_answer);
		textView.setText(this.getContent());
		
		ImageView arrow = (ImageView) view.findViewById(R.id.arrow_item_answer_text);
		if (this.getUrl() != null && (!this.getUrl().equals(""))){
			arrow.setBackgroundResource(R.drawable.chevron_inverse);
			view.setTag(this.getUrl());
			view.setOnClickListener(onClickListener);
		}
		else{
			arrow.setVisibility(View.GONE);
		}
		
		return view;
	}

	public View getView(Context context, View convertView) {
		return null;
	}
}
