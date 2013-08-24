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

package com.heibuddy.slidingmenuimp.model;

import java.util.ArrayList;
import java.util.List;

import com.heibuddy.R;

public class MenuDataSource {

    private final List<SlidingMenu> animals = new ArrayList<SlidingMenu>();

    public MenuDataSource() {
        animals.add(new SlidingMenu(R.drawable.aa_avatar_default_circle, "小伙伴！", "logo"));
        
        animals.add(new SlidingMenu(null, "惊呆了", null));
        animals.add(new SlidingMenu(R.drawable.aa_connect_usb_icon, "问一下", null));
        animals.add(new SlidingMenu(R.drawable.aa_connect_wifi_icon, "教教TA", null));
        animals.add(new SlidingMenu(R.drawable.aa_management_apps_icon, "浏览器", null));
        
        animals.add(new SlidingMenu(null, "常用", null));
        animals.add(new SlidingMenu(R.drawable.aa_management_pictures_icon, "小说", null));
        animals.add(new SlidingMenu(R.drawable.aa_management_video_icon, "视频", null));
        animals.add(new SlidingMenu(R.drawable.aa_management_music_icon, "新闻", null));
        animals.add(new SlidingMenu(R.drawable.aa_management_music_icon, "音乐", null));
        animals.add(new SlidingMenu(R.drawable.aa_management_video_icon, "应用", null));
        animals.add(new SlidingMenu(R.drawable.aa_management_video_icon, "导航", null));
    }

    public List<SlidingMenu> getAnimals() {
        return animals;
    }
}
