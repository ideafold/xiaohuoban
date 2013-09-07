package com.heibuddy.xiaohuoband.slidingmenuimp.row;

import com.heibuddy.xiaohuoband.slidingmenuimp.model.SlidingMenu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.heibuddy.R;

public class CategoryRow implements Row {
    private final SlidingMenu menuItem;
    private final LayoutInflater inflater;

    public CategoryRow(LayoutInflater inflater, SlidingMenu menuItem) {
        this.inflater = inflater;
        this.menuItem = menuItem;
    }

    public View getView(View convertView) {
        ViewHolder holder;
        View view;
        if (convertView == null) {
            ViewGroup viewGroup = (ViewGroup)inflater.inflate(R.layout.aa_sidebar_divider_title_layout, null);
            holder = new ViewHolder((TextView)viewGroup.findViewById(R.id.category_title));
            viewGroup.setTag(holder);
            view = viewGroup;
        } else {
            view = convertView;
            holder = (ViewHolder)convertView.getTag();
        }

        holder.titleView.setText(menuItem.getName());
        return view;
    }

    public int getViewType() {
        return RowType.CATEGORY_ROW.ordinal();
    }

    private static class ViewHolder {
        final TextView titleView;

        private ViewHolder(TextView titleView) {
            this.titleView = titleView;
        }
    }
}
