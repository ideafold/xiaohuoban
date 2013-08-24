package com.heibuddy.xiaohuoband.error;

import com.heibuddy.xiaohuoban.error.XiaohuobanException;

public class LocationException extends XiaohuobanException {

    public LocationException() {
        super("Unable to determine your location.");
    }

    public LocationException(String message) {
        super(message);
    }

    private static final long serialVersionUID = 1L;

}
