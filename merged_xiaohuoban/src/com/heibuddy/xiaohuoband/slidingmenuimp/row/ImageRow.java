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

package com.heibuddy.xiaohuoband.slidingmenuimp.row;

import com.heibuddy.xiaohuoband.slidingmenuimp.model.SlidingMenu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.heibuddy.R;

public class ImageRow implements Row {
    private final SlidingMenu menuItem;
    private final LayoutInflater inflater;
    private boolean isLogo;

    public ImageRow(LayoutInflater inflater, SlidingMenu menuItem, boolean isLogo) {
        this.menuItem = menuItem;
        this.inflater = inflater;
        this.isLogo = isLogo;
    }

    public View getView(View convertView) {
        ViewHolder holder;
        View view;
        //we have a don't have a converView so we'll have to create a new one
        if (convertView == null) {
            ViewGroup viewGroup = null;
            if (isLogo){
            	viewGroup = (ViewGroup)inflater.inflate(R.layout.aa_sidebar_account_layout, null);
            }
            else{
            	viewGroup = (ViewGroup)inflater.inflate(R.layout.aa_sidebar_home_category_layout, null);
            }

            //use the view holder pattern to save of already looked up subviews
            holder = new ViewHolder((ImageView)viewGroup.findViewById(R.id.category_icon),
                    (TextView)viewGroup.findViewById(R.id.category_title));
            viewGroup.setTag(holder);

            view = viewGroup;
        } else {
            //get the holder back out
            holder = (ViewHolder)convertView.getTag();

            view = convertView;
        }

        //actually setup the view
        holder.imageView.setImageResource(menuItem.getImageId());
        holder.titleView.setText(menuItem.getName());

        return view;
    }

    public int getViewType() {
        return RowType.IMAGE_ROW.ordinal();
    }

    private static class ViewHolder {
        final ImageView imageView;
        final TextView titleView;

        private ViewHolder(ImageView imageView, TextView titleView) {
            this.imageView = imageView;
            this.titleView = titleView;
        }
    }
}
