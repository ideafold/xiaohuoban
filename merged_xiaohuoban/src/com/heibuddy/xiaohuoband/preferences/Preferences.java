package com.heibuddy.xiaohuoband.preferences;

import com.heibuddy.xiaohuoband.XiaohuobandSettings;
import com.heibuddy.xiaohuoban.Xiaohuoban;
import com.heibuddy.xiaohuoban.error.XiaohuobanCredentialsException;
import com.heibuddy.xiaohuoban.error.XiaohuobanException;
import com.heibuddy.xiaohuoban.types.User;
import com.heibuddy.xiaohuoband.XiaohuobandSettings;

import com.heibuddy.R;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import org.json.JSONException;

public class Preferences {
    private static final String TAG = "Preferences";
    private static final boolean DEBUG = XiaohuobandSettings.DEBUG;

    // Credentials related preferences
    public static final String PREFERENCE_USER_NAME = "username";
    public static final String PREFERENCE_USER_PASSWORD = "password";

    // Extra info for getUserId
    private static final String PREFERENCE_USER_ID = "id";
    
    // Extra for storing user's supplied email address.
    private static final String PREFERENCE_USER_EMAIL = "user_email";
    
    // Not-in-XML preferences for dumpcatcher
    public static final String PREFERENCE_DUMPCATCHER_CLIENT = "dumpcatcher_client";
 
    public static void setupDefaults(SharedPreferences preferences, Resources resources) {
    }
    
    public static String createUniqueId(SharedPreferences preferences) {
        String uniqueId = preferences.getString(PREFERENCE_DUMPCATCHER_CLIENT, null);
        if (uniqueId == null) {
            uniqueId = UUID.randomUUID().toString();
            Editor editor = preferences.edit();
            editor.putString(PREFERENCE_DUMPCATCHER_CLIENT, uniqueId);
            editor.commit();
        }
        return uniqueId;
    }

    public static boolean loginUser(Xiaohuoban xiaohuoban, final String username, final String password,
    		Editor editor) throws XiaohuobanException, IOException {
        if (DEBUG) Log.d(Preferences.TAG, "Trying to log in.");

        String[] signInResult = null;
        try {
        	signInResult = xiaohuoban.verifyUser(username, password);
//        	signInResult = new String[2];
//        	signInResult[0] = "10";
        	if (signInResult == null || signInResult[0] == "-1")
        	{
        		throw new XiaohuobanException(signInResult[1]);
        	}
		} catch (IOException e) {
//        } catch (Exception e) {	
			if (DEBUG) Log.d(Preferences.TAG, "verifyUser occur io exception!.");
			throw new XiaohuobanException("verifyUser occur io exception!.");
		}
        
        String userId = signInResult[0];
        xiaohuoban.setCredentials(username, password);
        storeUserInfo(editor, userId, username, null, password);
        if (!editor.commit()) {
            if (DEBUG) Log.d(TAG, "storeLoginAndPassword commit failed");
            return false;
        }

        return true;
    }

    public static boolean registerUser(Xiaohuoban xiaohuoban, final String username, final String email, final String password,
    		Editor editor) throws XiaohuobanException, IOException {
        if (DEBUG) Log.d(Preferences.TAG, "Trying to register.");

        String[] signInResult = null;
        try {
        	signInResult = xiaohuoban.signupUser(username, email, password);
        	//signInResult = new String[2];
        	//signInResult[0] = "10";
        	if (signInResult == null || signInResult[0] == "-1")
        	{
        		throw new XiaohuobanException(signInResult[1]);
        	}
		} catch (IOException e) {
        //} catch (Exception e) {	
			if (DEBUG) Log.d(Preferences.TAG, "signupUser occur io exception!.");
			throw new XiaohuobanException("signupUser occur io exception!.");
		}
        
        String userId = signInResult[0];
        xiaohuoban.setCredentials(username, password);
        storeUserInfo(editor, userId, username, email, password);
        if (!editor.commit()) {
            if (DEBUG) Log.d(TAG, "storeLoginAndPassword commit failed");
            return false;
        }

        return true;
    }
    
    public static boolean logoutUser(Xiaohuoban xiaohuoban, Editor editor) {
        if (DEBUG) Log.d(Preferences.TAG, "Trying to log out.");
        xiaohuoban.setCredentials(null, null);
        return editor.clear().commit();
    }

    public static String getUserId(SharedPreferences prefs) {
        return prefs.getString(PREFERENCE_USER_ID, null);
    }
    
    public static String getUserName(SharedPreferences prefs) {
        return prefs.getString(PREFERENCE_USER_NAME, null);
    }
    
    public static String getPassword(SharedPreferences prefs){
    	return prefs.getString(PREFERENCE_USER_PASSWORD, null);
    }
    
    public static String getUserEmail(SharedPreferences prefs) {
        return prefs.getString(PREFERENCE_USER_EMAIL, null);
    }

    public static void storeUserInfo(final Editor editor, final String userId, final String username, 
    								final String email, final String password) {
    	editor.putString(PREFERENCE_USER_ID, userId);
        editor.putString(PREFERENCE_USER_NAME, username);
        editor.putString(PREFERENCE_USER_EMAIL, email);
        editor.putString(PREFERENCE_USER_PASSWORD, password);
        
    }
}
