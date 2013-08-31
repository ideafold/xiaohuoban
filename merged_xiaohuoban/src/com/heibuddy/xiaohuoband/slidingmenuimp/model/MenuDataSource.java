package com.heibuddy.xiaohuoband.slidingmenuimp.model;

import java.util.ArrayList;
import java.util.List;

import com.heibuddy.R;

public class MenuDataSource {

    private final List<SlidingMenu> animals = new ArrayList<SlidingMenu>();

    public MenuDataSource() {
        animals.add(new SlidingMenu(R.drawable.aa_avatar_default_circle, "小伙伴！", "logo"));
        
        animals.add(new SlidingMenu(null, "惊呆了", null));
        animals.add(new SlidingMenu(R.drawable.aa_connect_usb_icon, "问一下", null));
        animals.add(new SlidingMenu(R.drawable.aa_connect_wifi_icon, "教教Ta", null));
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
