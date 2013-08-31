package com.heibuddy.xiaohuoband;

import com.heibuddy.xiaohuoban.Xiaohuoban;
import com.heibuddy.xiaohuoband.error.LocationException;
import com.heibuddy.xiaohuoband.location.BestLocationListener;
import com.heibuddy.xiaohuoband.preferences.Preferences;
import com.heibuddy.xiaohuoban.util.JavaLoggingHandler;
import com.heibuddy.xiaohuoban.util.NullDiskCache;
import com.heibuddy.xiaohuoban.util.RemoteResourceManager;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Xiaohuoband extends Application {
    private static final String TAG = "Xiaohuoband";
    private static final boolean DEBUG = XiaohuobandSettings.DEBUG;
    static {
        Logger.getLogger("com.heibuddy.xiaohuoban").addHandler(new JavaLoggingHandler());
        Logger.getLogger("com.heibuddy.xiaohuoban").setLevel(Level.ALL);
    }

    public static final String PACKAGE_NAME = "com.heibuddy";
    public static final String INTENT_ACTION_LOGGED_OUT = "com.heibuddy.xiaohuoband.intent.action.LOGGED_OUT";
    public static final String INTENT_ACTION_LOGGED_IN = "com.heibuddy.xiaohuoband.intent.action.LOGGED_IN";

    private String mVersion = null;
    private static Xiaohuoband sInstance;
    
    private TaskHandler mTaskHandler;
    private HandlerThread mTaskThread;

    private SharedPreferences mPrefs;
    private RemoteResourceManager mRemoteResourceManager;

    private Xiaohuoban mXiaohuoban;

    private BestLocationListener mBestLocationListener = new BestLocationListener();
    
    private boolean mIsFirstRun;
    
    @Override
    public void onCreate() {
        Log.i(TAG, "Using Debug Log:\t" + DEBUG);

        mVersion = getVersionString(this);
        Xiaohuoband.sInstance = this;
        
        // Check if this is a new install by seeing if our preference file exists on disk.
        mIsFirstRun = checkIfIsFirstRun();

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        // Setup some defaults in our preferences if not set yet.
        Preferences.setupDefaults(mPrefs, getResources());
 
        // Sometimes we want the application to do some work on behalf of the
        // Activity. Lets do that asynchronously.
        mTaskThread = new HandlerThread(TAG + "-AsyncThread");
        mTaskThread.start();
        mTaskHandler = new TaskHandler(mTaskThread.getLooper());

        // Set up storage cache.
        loadResourceManagers();

        // Catch sdcard state changes
        new MediaCardStateBroadcastReceiver().register();

        // Catch logins or logouts.
        new LoggedInOutBroadcastReceiver().register();

        // produce a Xiaohuoban instance
        loadXiaohuoban();
    }

    public static Xiaohuoband getAppContext() {
        return Xiaohuoband.sInstance;
    }
    
    public boolean isReady() {
        return getXiaohuoban().hasUsernameAndPassword(mPrefs) && !TextUtils.isEmpty(getUserId());
    }

    public Xiaohuoban getXiaohuoban() {
        return mXiaohuoban;
    }

    public String getUserId() {
        return Preferences.getUserId(mPrefs);
    }
    
    public String getUserName() {
        return Preferences.getUserName(mPrefs);
    }
    
    public String getUserEmail() {
        return Preferences.getUserEmail(mPrefs);
    }
    
    public String getVersion() {

        if (mVersion != null) {
            return mVersion;
        } else {
            return "";
        }
    }
    
    public RemoteResourceManager getRemoteResourceManager() {
        return mRemoteResourceManager;
    }

    public BestLocationListener requestLocationUpdates(boolean gps) {
        mBestLocationListener.register(
                (LocationManager) getSystemService(Context.LOCATION_SERVICE), gps);
        return mBestLocationListener;
    }

    public BestLocationListener requestLocationUpdates(Observer observer) {
        mBestLocationListener.addObserver(observer);
        mBestLocationListener.register(
                (LocationManager) getSystemService(Context.LOCATION_SERVICE), true);
        return mBestLocationListener;
    }

    public void removeLocationUpdates() {
        mBestLocationListener
                .unregister((LocationManager) getSystemService(Context.LOCATION_SERVICE));
    }

    public void removeLocationUpdates(Observer observer) {
        mBestLocationListener.deleteObserver(observer);
        this.removeLocationUpdates();
    }

    public Location getLastKnownLocation() {
        return mBestLocationListener.getLastKnownLocation();
    }

    public Location getLastKnownLocationOrThrow() throws LocationException {
        Location location = mBestLocationListener.getLastKnownLocation();
        if (location == null) {
            throw new LocationException();
        }
        return location;
    }
    
    public void clearLastKnownLocation() {
        mBestLocationListener.clearLastKnownLocation();
    }

    public void requestStartService() {
        mTaskHandler.sendMessage(mTaskHandler.obtainMessage(TaskHandler.MESSAGE_START_SERVICE));
    }

    public void requestUpdateUser() {
        mTaskHandler.sendEmptyMessage(TaskHandler.MESSAGE_UPDATE_USER);
    }

    private void loadXiaohuoban() {
        // Try logging in and setting up xiaohuoban oauth, then user
        // credentials.
        mXiaohuoban = new Xiaohuoban();

        Log.d(TAG, "loadCredentials()");
        String username = mPrefs.getString(Preferences.PREFERENCE_USER_NAME, null);
        String password = mPrefs.getString(Preferences.PREFERENCE_USER_PASSWORD, null);
        mXiaohuoban.setCredentials(username, password);
        if (mXiaohuoban.hasUsernameAndPassword(mPrefs)) {
            sendBroadcast(new Intent(INTENT_ACTION_LOGGED_IN));
        } else {
            sendBroadcast(new Intent(INTENT_ACTION_LOGGED_OUT));
        }
    }

    public static Xiaohuoban createXiaohuoban(Context context) {
        return new Xiaohuoban();
    }

    private static String getVersionString(Context context) {
        // Get a version string for the app.
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(PACKAGE_NAME, 0);
            return PACKAGE_NAME + ":" + String.valueOf(pi.versionCode);
        } catch (NameNotFoundException e) {
            if (DEBUG) Log.d(TAG, "Could not retrieve package info", e);
            throw new RuntimeException(e);
        }
    }

    private void loadResourceManagers() {
        // We probably don't have SD card access if we get an
        // IllegalStateException. If it did, lets
        // at least have some sort of disk cache so that things don't npe when
        // trying to access the
        // resource managers.
        try {
            Log.d(TAG, "Attempting to load RemoteResourceManager(cache)");
            mRemoteResourceManager = new RemoteResourceManager("cache");
        } catch (IllegalStateException e) {
            Log.d(TAG, "Falling back to NullDiskCache for RemoteResourceManager");
            mRemoteResourceManager = new RemoteResourceManager(new NullDiskCache());
        }
    }

    public boolean getIsFirstRun() {
        return mIsFirstRun;
    }

    private boolean checkIfIsFirstRun() {
        File file = new File(
            "/data/data/com.heibuddy.xiaohuoband/shared_prefs/com.heibuddy.xiaohuoband_preferences.xml");
        return !file.exists();
    }
    
    /**
     * Set up resource managers on the application depending on SD card state.
     */
    private class MediaCardStateBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Media state changed, reloading resource managers:"
                                + intent.getAction());
            if (Intent.ACTION_MEDIA_UNMOUNTED.equals(intent.getAction())) {
                getRemoteResourceManager().shutdown();
                loadResourceManagers();
            } else if (Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction())) {
                loadResourceManagers();
            }
        }

        public void register() {
            // Register our media card broadcast receiver so we can
            // enable/disable the cache as
            // appropriate.
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
            intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
            intentFilter.addDataScheme("file");
            registerReceiver(this, intentFilter);
        }
    }

    private class LoggedInOutBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (INTENT_ACTION_LOGGED_IN.equals(intent.getAction())) {
                requestUpdateUser();
            }
        }

        public void register() {
            // Register our media card broadcast receiver so we can
            // enable/disable the cache as
            // appropriate.
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(INTENT_ACTION_LOGGED_IN);
            intentFilter.addAction(INTENT_ACTION_LOGGED_OUT);
            registerReceiver(this, intentFilter);
        }
    }

    private class TaskHandler extends Handler {

        private static final int MESSAGE_UPDATE_USER = 1;
        private static final int MESSAGE_START_SERVICE = 2;

        public TaskHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (DEBUG) Log.d(TAG, "handleMessage: " + msg.what);

            switch (msg.what) {
                case MESSAGE_UPDATE_USER:
                    return;

                case MESSAGE_START_SERVICE:
                    return;
            }
        }
    }
}
