package com.heibuddy.xiaohuoban.util;

import com.heibuddy.xiaohuoban.error.XiaohuobanCredentialsException;
import com.heibuddy.xiaohuoban.error.XiaohuobanException;
import com.heibuddy.xiaohuoband.XiaohuobandSettings;
import com.heibuddy.xiaohuoband.error.LocationException;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class NotificationsUtil {
    private static final String TAG = "NotificationsUtil";
    private static final boolean DEBUG = XiaohuobandSettings.DEBUG;

    public static void ToastReasonForFailure(Context context, Exception e) {
        if (DEBUG) Log.d(TAG, "Toasting for exception: ", e);

        if (e == null) {
            Toast.makeText(context, "A surprising new problem has occured. Try again!",
                    Toast.LENGTH_SHORT).show();
        } else if (e instanceof SocketTimeoutException) {
            Toast.makeText(context, "Xiaohuoban over capacity, server request timed out!", Toast.LENGTH_SHORT).show();
            
        } else if (e instanceof SocketException) {
            Toast.makeText(context, "Xiaohuoban server not responding", Toast.LENGTH_SHORT).show();

        } else if (e instanceof IOException) {
            Toast.makeText(context, "Network unavailable", Toast.LENGTH_SHORT).show();

        } else if (e instanceof LocationException) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

        } else if (e instanceof XiaohuobanCredentialsException) {
            Toast.makeText(context, "Authorization failed.", Toast.LENGTH_SHORT).show();

        } else if (e instanceof XiaohuobanException) {
            // FoursquareError is one of these
            String message;
            int toastLength = Toast.LENGTH_SHORT;
            if (e.getMessage() == null) {
                message = "Invalid Request";
            } else {
                message = e.getMessage();
                toastLength = Toast.LENGTH_LONG;
            }
            Toast.makeText(context, message, toastLength).show();
        } else {
            Toast.makeText(context, "A surprising new problem has occured. Try again!",
                    Toast.LENGTH_SHORT).show();
            //TODO
            //DumpcatcherHelper.sendException(e);
        }
    }
}
