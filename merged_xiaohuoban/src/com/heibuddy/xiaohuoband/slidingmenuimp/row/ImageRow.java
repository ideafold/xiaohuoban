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
        this.inflater = inflater;
        this.menuItem = menuItem;
        this.isLogo = isLogo;
    }

    public View getView(View convertView) {
        ViewHolder holder;
        View view;
        //we have a don't have a converView so we'll have to create a new one
        if (convertView == null) {
            ViewGroup viewGroup = null;
            if (isLogo){
            	//viewGroup = (ViewGroup)inflater.inflate(R.layout.aa_sidebar_account_layout, null);
            	viewGroup = (ViewGroup)inflater.inflate(R.layout.aa_sidebar_home_category_layout, null);
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
