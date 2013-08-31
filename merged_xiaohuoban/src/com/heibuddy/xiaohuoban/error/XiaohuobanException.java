package com.heibuddy.xiaohuoban.error;

public class XiaohuobanException extends Exception {
    private static final long serialVersionUID = 1L;
    
    private String mExtra;

    public XiaohuobanException(String message) {
        super(message);
    }

    public XiaohuobanException(String message, String extra) {
        super(message);
        mExtra = extra;
    }
    
    public String getExtra() {
        return mExtra;
    }
}
