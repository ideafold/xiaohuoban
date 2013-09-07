package com.heibuddy.xiaohuoband.slidingmenuimp;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.heibuddy.R;
import com.heibuddy.browser.utils.*;
import com.heibuddy.xiaohuoband.TalkActivity;
import com.heibuddy.xiaohuoband.TeachActivity;
import com.heibuddy.xiaohuoband.slidingmenuimp.model.MenuDataSource;
import com.heibuddy.xiaohuoband.slidingmenuimp.model.SlidingMenu;
import com.heibuddy.xiaohuoband.slidingmenuimp.row.CategoryRow;
import com.heibuddy.xiaohuoband.slidingmenuimp.row.ImageRow;
import com.heibuddy.xiaohuoband.slidingmenuimp.row.Row;
import com.heibuddy.xiaohuoband.slidingmenuimp.row.RowType;

public class SlidingMenuFragment extends ListFragment {
	final static String BAIDU_URL = "http://www.baidu.com/";
	final static String NEWS_URL = "http://info.3g.qq.com/";
	final static String MUSIC_URL = "http://m.ttpod.com/";
	final static String VIDEO_URL = "http://www.youku.com/";
	final static String TAOBAO_URL = "http://r.m.taobao.com/m3?p=mm_46101947_4214102_13744538&c=1043";
	final static String NOVEL_URL = "http://duokoo.baidu.com/xs/";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(new AnimalAdapter(new MenuDataSource().getAnimals()));	
	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		Class<?> cls = null;
		Intent intent = null;
		switch (position) {
		case 0:
			//account item, so do nothing
			break;
		case 1:
			//surprise category, do nothing
			break;
		case 2:
			//talk activity
			cls = TalkActivity.class;
			intent = new Intent(getActivity(), cls);
			getActivity().startActivity(intent);
			break;
		case 3:
			//teach activity
			cls = TeachActivity.class;
			intent = new Intent(getActivity(), cls);
			getActivity().startActivity(intent);
			break;
		case 4:
			//general use category, do nothing
			break;
		case 5:
			//baidu activity
			UrlUtils.goToTheUrl(getActivity(), BAIDU_URL);
			break;
		case 6:
			//news activity
			UrlUtils.goToTheUrl(getActivity(), NEWS_URL);
			break;
		case 7:
			//music
			UrlUtils.goToTheUrl(getActivity(), MUSIC_URL);
			break;
		case 8:
			//video
			UrlUtils.goToTheUrl(getActivity(), VIDEO_URL);
			break;
		case 9:
			//taobao
			UrlUtils.goToTheUrl(getActivity(), TAOBAO_URL);
			break;
		default:
			//novel
			UrlUtils.goToTheUrl(getActivity(), NOVEL_URL);
			break;
		}
	}

    private class AnimalAdapter extends BaseAdapter {
        final List<Row> rows;

        AnimalAdapter(List<SlidingMenu> animals) {
            rows = new ArrayList<Row>();//member variable

            for (SlidingMenu animal : animals) {
            	if (animal.getDescription() != null) {
            		rows.add(new ImageRow(LayoutInflater.from(getActivity()), animal, true));
            		//rows.add(new CategoryRow(LayoutInflater.from(getActivity()), animal));
            		continue;
            	}
            	
                //if it has an image, use an ImageRow
                if (animal.getImageId() != null) {
                    rows.add(new ImageRow(LayoutInflater.from(getActivity()), animal, false));
                } else {
                	//otherwise use a DescriptionRow
                    rows.add(new CategoryRow(LayoutInflater.from(getActivity()), animal));
                }
            }
        }

        @Override
        public int getViewTypeCount() {
            return RowType.values().length;
        }

        @Override
        public int getItemViewType(int position) {
            return rows.get(position).getViewType();
        }

        public int getCount() {
            return rows.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            return rows.get(position).getView(convertView);
        }
    }
}
