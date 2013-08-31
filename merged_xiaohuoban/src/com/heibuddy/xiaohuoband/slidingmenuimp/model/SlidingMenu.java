package com.heibuddy.xiaohuoband.slidingmenuimp.model;

public class SlidingMenu {
    private final Integer imageId;
    private final String name;
    private final String description;

    public SlidingMenu(Integer imageId, String name, String description) {
        this.imageId = imageId;
        this.name = name;
        this.description = description;
    }

    public Integer getImageId() {
        return imageId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
