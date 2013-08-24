/*
Copyright (C) 2011 by Indrajit Khare

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/

package com.heibuddy.slidingmenuimp.row;

import com.heibuddy.slidingmenuimp.model.SlidingMenu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.heibuddy.R;

public class CategoryRow implements Row {
    private final SlidingMenu menuItem;
    private final LayoutInflater inflater;

    public CategoryRow(LayoutInflater inflater, SlidingMenu menuItem) {
        this.menuItem = menuItem;
        this.inflater = inflater;
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
