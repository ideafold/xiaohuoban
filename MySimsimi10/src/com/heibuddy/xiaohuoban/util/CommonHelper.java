package com.heibuddy.xiaohuoban.util;

import java.util.Collection;

public final class CommonHelper {
    public static boolean isEmpty(final Collection<?> c) {
        return (c == null) || (c.size() == 0);
    }

    public static boolean isEmpty(final String str) {
        return (str == null) || str.equals("");
    }

}