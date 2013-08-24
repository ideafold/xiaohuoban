package com.heibuddy.xiaohuoband.talk;

import java.util.Date;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.heibuddy.xiaohuoband.R;
import com.heibuddy.xiaohuoband.XiaohuobandSettings;

public class QuestionItemEntity extends BaseListItemEntity {
    public static final String TAG = "QuestionItemEntity";
    public static final boolean DEBUG = XiaohuobandSettings.DEBUG;
    
	private String mContent;
	
    public QuestionItemEntity() {
    }

    public QuestionItemEntity(final String ct) {
    	this.mContent = ct;
    }
    
	public QuestionItemEntity(final String toUserName, final String fromUserName,
							  final Date createTime, final String msgType, final String ct) {
		super();
		super.setToUserName(toUserName);
		super.setFromUserName(fromUserName);
		super.setCreateTime(createTime);
		super.setMsgType(msgType);
		this.setContent(ct);
	}
    
    public void setContent(String ct)
    {
    	this.mContent = ct;
    }
    
    public String getContent()
    {
    	return this.mContent;
    }
    
	@Override
	public String toString() {
		return "ListItemEntity [toUserName=" + getToUserName()
				+ ", fromUserName=" + getFromUserName() + ", createTime="
				+ getCreateTime() + ", msgType=" + getMsgType() + ", content="
				+ getContent() + "]";
	}
	
    public int getViewType() {
        return ListItemEntityType.QUESTION_ENTITY.ordinal();
    }

	public ListItemEntityType getItemType() {
		return ListItemEntityType.QUESTION_ENTITY;
	}

	public int getItemLayout() {
		return R.layout.item_ask1;
	}

	public View getView(Context context, View convertView) {
		LinearLayout view = new LinearLayout(context);
		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(this.getItemLayout(), view, true);	
		
		TextView textView = (TextView) view.findViewById(R.id.tv_ask_item);
		textView.setText(this.getContent());
		
		return view;
	}

	public View getView(Context context, OnClickListener onClickListener,
			View convertView) {
		return null;
	}
}
