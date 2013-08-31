package com.heibuddy.xiaohuoband.slidingmenuimp;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.heibuddy.xiaohuoband.slidingmenuimp.row.LogoRow;
import com.heibuddy.xiaohuoband.slidingmenuimp.row.Row;
import com.heibuddy.xiaohuoband.slidingmenuimp.row.RowType;

public class SlidingMenuFragment extends ListFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
/*		
		String[] colors = getResources().getStringArray(R.array.color_names);
		ArrayAdapter<String> colorAdapter = new ArrayAdapter<String>(getActivity(), 
				android.R.layout.simple_list_item_1, android.R.id.text1, colors);
*/		
		setListAdapter(new AnimalAdapter(new MenuDataSource().getAnimals()));	
		//setListAdapter(colorAdapter);
	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		Fragment newContent = null;
		Class<?> cls = null;
		Intent intent = null;
		String url;
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
			//browser activity
			url = "http://www.google.com.hk";
			UrlUtils.goToTheUrl(getActivity(), url);
			break;
		case 5:
			//general use category, do nothing
			break;
		case 6:
			url = "http://www.36kr.com";
			UrlUtils.goToTheUrl(getActivity(), url);
			break;
		default:
			url = "http://www.douban.com/";
			UrlUtils.goToTheUrl(getActivity(), url);
			break;
		}
		
		if (newContent != null)
			switchFragment(newContent);
	}

	// the meat of switching the above fragment
	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;
	}

    private class AnimalAdapter extends BaseAdapter {
        final List<Row> rows;

        AnimalAdapter(List<SlidingMenu> animals) {
            rows = new ArrayList<Row>();//member variable

            for (SlidingMenu animal : animals) {
            	if (animal.getDescription() != null) {
            		rows.add(new LogoRow(LayoutInflater.from(getActivity()), animal));
            		continue;
            	}
            	
                //if it has an image, use an ImageRow
                if (animal.getImageId() != null) {
                    rows.add(new ImageRow(LayoutInflater.from(getActivity()), animal));
                } else {//otherwise use a DescriptionRow
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
