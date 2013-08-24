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

package com.heibuddy.slidingmenuimp;

import java.util.ArrayList;
import java.util.List;

import com.heibuddy.slidingmenuimp.model.MenuDataSource;
import com.heibuddy.slidingmenuimp.model.SlidingMenu;
import com.heibuddy.slidingmenuimp.row.CategoryRow;
import com.heibuddy.slidingmenuimp.row.ImageRow;
import com.heibuddy.slidingmenuimp.row.Row;
import com.heibuddy.slidingmenuimp.row.RowType;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.heibuddy.R;

public class MenuHome extends Activity
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sliding_menu_main);

        ListView listView = (ListView)findViewById(R.id.sliding_menu_home_list);
        listView.setAdapter(new AnimalAdapter(new MenuDataSource().getAnimals()));
    }

    private class AnimalAdapter extends BaseAdapter {
        final List<Row> rows;

        AnimalAdapter(List<SlidingMenu> animals) {
            rows = new ArrayList<Row>();//member variable

            for (SlidingMenu animal : animals) {
                //if it has an image, use an ImageRow
                if (animal.getImageId() != null) {
                    rows.add(new ImageRow(LayoutInflater.from(MenuHome.this), animal));
                } else {//otherwise use a DescriptionRow
                    rows.add(new CategoryRow(LayoutInflater.from(MenuHome.this), animal));
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
