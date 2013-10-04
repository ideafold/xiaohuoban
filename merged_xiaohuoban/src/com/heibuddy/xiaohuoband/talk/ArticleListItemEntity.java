package com.heibuddy.xiaohuoband.talk;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

import com.heibuddy.R;
import com.heibuddy.xiaohuoband.XiaohuobandSettings;
import com.iiseeuu.asyncimage.image.ChainImageProcessor;
import com.iiseeuu.asyncimage.image.ImageProcessor;
import com.iiseeuu.asyncimage.image.MaskImageProcessor;
import com.iiseeuu.asyncimage.image.ScaleImageProcessor;
import com.iiseeuu.asyncimage.widget.AsyncImageView;

public class ArticleListItemEntity extends BaseListItemEntity {
    public static final String TAG = "ArticleListItemEntity";
    public static final boolean DEBUG = XiaohuobandSettings.DEBUG;
	
	private List<MsgRecvListItemEntity> mArticles;
	private ImageProcessor mImageProcessor;
	
	public ArticleListItemEntity(Context context) {
		prepareImageProcessor(context);
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
		//here, we make the item with image having higher priority!
		
		mArticles = new ArrayList<MsgRecvListItemEntity>();
		ArrayList<MsgRecvListItemEntity> notHavingImageItems = new ArrayList<MsgRecvListItemEntity>();
		
		for (int i = 0; i < articles.size(); i++)
		{
			MsgRecvListItemEntity item = articles.get(i);
			if (item.getPicUrl() != null && !item.getPicUrl().equals(""))
			{
				mArticles.add(item);
			}
			else
			{
				notHavingImageItems.add(item);
			}
		}
		
		mArticles.addAll(notHavingImageItems);
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
	
	private void prepareImageProcessor(Context context) {
		final int thumbnailSize = context.getResources()
				.getDimensionPixelSize(R.dimen.item_inner_thumbnail_size);
		final int thumbnailRadius = context.getResources()
				.getDimensionPixelSize(R.dimen.item_inner_thumbnail_radius);
		mImageProcessor = new ChainImageProcessor(
				new ScaleImageProcessor(thumbnailSize, thumbnailSize,
						ScaleType.FIT_XY), new MaskImageProcessor(
								thumbnailRadius));
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
			if (i == 0)
			{
				innerView.findViewById(R.id.divider_line).setVisibility(View.INVISIBLE);
			}
			TextView title = (TextView)innerView.findViewById(R.id.title_inner);
			title.setText(msgRecvListItemEntity.getTitle());
			Log.d(TAG, msgRecvListItemEntity.getTitle());
			
			TextView description = (TextView)innerView.findViewById(R.id.description_inner);
			if (msgRecvListItemEntity.getBitmap() != null){
				description.setText(msgRecvListItemEntity.getDescription());
			}
			else{
				description.setText(msgRecvListItemEntity.getDescription());
			}
			
			AsyncImageView imageInner = (AsyncImageView)innerView.findViewById(R.id.image_inner);
			if (msgRecvListItemEntity.getPicUrl() != null && !msgRecvListItemEntity.getPicUrl().equals(""))
			{
				//imageInner.setImageBitmap(msgRecvListItemEntity.getBitmap());
				imageInner.setImageProcessor(mImageProcessor);
				imageInner.setUrl(msgRecvListItemEntity.getPicUrl());
				imageInner.setVisibility(View.VISIBLE);
				//Log.d(TAG, "image VISIBLE: " + msgRecvListItemEntity.getPicUrl());
			}
			else{
				imageInner.setVisibility(View.GONE);
				//Log.d(TAG, "image GONE");
			}
			
			if (msgRecvListItemEntity.getUrl() == null || msgRecvListItemEntity.getUrl().equals(""))
			{
				ImageView arrowInner = (ImageView)innerView.findViewById(R.id.arrow_inner);
				arrowInner.setVisibility(View.GONE);
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
