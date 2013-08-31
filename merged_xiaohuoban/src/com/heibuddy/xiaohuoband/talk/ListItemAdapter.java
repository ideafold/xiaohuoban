package com.heibuddy.xiaohuoband.talk;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import com.heibuddy.browser.ui.activities.BrowserMainActivity;
import com.heibuddy.xiaohuoband.XiaohuobandSettings;

public class ListItemAdapter implements ListAdapter{
    public static final String TAG = "ListItemAdapter";
    public static final boolean DEBUG = XiaohuobandSettings.DEBUG;
    
	private List<BaseListItemEntity> list;
	private Context context;
	private BaseListItemEntity listItemEntity;

	public ListItemAdapter(Context context, List<BaseListItemEntity> list)
	{
		this.context = context;
		this.list = list;
	}

	@Override
	public boolean areAllItemsEnabled()
	{
		return true;
	}

	/**
	 * Ϊtrue����Item
	 */
	@Override
	public boolean isEnabled(int arg0)
	{
		return true;
	}

	@Override
	public int getCount()
	{
		return list.size();
	}

	@Override
	public Object getItem(int position)
	{
		return list.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public int getItemViewType(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		listItemEntity = list.get(position);
		
		ListItemEntityType type = listItemEntity.getItemType();
		if (type == ListItemEntityType.QUESTION_ENTITY){
			QuestionItemEntity qItem = (QuestionItemEntity)listItemEntity;
			return qItem.getView(context, convertView);
		}
		else if(type == ListItemEntityType.SIMPLE_ANSWER_ENTITY){
			SimpleAnswerItemEntity saItem = (SimpleAnswerItemEntity)listItemEntity;
			return saItem.getView(context, onClickListener, convertView);
		}
		else if(type == ListItemEntityType.ARTICLE_LIST_ENTITY){
			ArticleListItemEntity alItem = (ArticleListItemEntity)listItemEntity;
			return alItem.getView(context, onClickListener, convertView);
		}
		else
		{
			Log.e(TAG, "Wrong ListItemEntityType!");
		}

		return convertView;
	}
	
	private OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(context, BrowserMainActivity.class);
			intent.putExtra("url", (String)v.getTag());
			context.startActivity(intent);
		}
	};

	@Override
	public int getViewTypeCount()
	{
		return list.size();
	}

	@Override
	public boolean hasStableIds()
	{
		return true;
	}

	@Override
	public boolean isEmpty()
	{
		return true;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer)
	{
		// TODO Auto-generated method stub
	}
}
