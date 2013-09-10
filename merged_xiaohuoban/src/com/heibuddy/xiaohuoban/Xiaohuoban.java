package com.heibuddy.xiaohuoban;

import android.content.SharedPreferences;
import android.util.Log;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import com.heibuddy.xiaohuoban.http.SimpleClient;
import com.heibuddy.xiaohuoban.http.SimpleRequest;
import com.heibuddy.xiaohuoban.http.SimpleResponse;
import com.heibuddy.xiaohuoband.Xiaohuoband;
import com.heibuddy.xiaohuoband.XiaohuobandSettings;
import com.heibuddy.xiaohuoband.preferences.Preferences;

public class Xiaohuoban
{
    private static final String TAG = Xiaohuoban.class.getSimpleName();
    
	private static final String SIGNUP_URL = "http://www.360island.com:8080/signup/";
	private static final String SIGNIN_URL = "http://www.360island.com:8080/signin/";
	
    private String mUserName;
    private String mPassword;
    
    public boolean hasUsernameAndPassword(SharedPreferences prefs) {
    	String username = Preferences.getUserName(prefs);
    	String userId = Preferences.getUserId(prefs);
    	if (username != null && userId != null)
    	{
    		return true;
    	}
    	
        return false;
    }
    
    public void setCredentials(String username, String password) {
    	mUserName = username;
        mPassword = password;
    }
    
    //success: return user id, failed: return -1 and reason
    public String[] verifyUser(final String username, final String password) throws IOException
    {
    	SimpleRequest.Builder signupBuilder = SimpleRequest.newBuilder();
    	signupBuilder.url(SIGNIN_URL);
    	signupBuilder.param("u", username);
    	signupBuilder.param("p", password);
    	signupBuilder.param("android", "flag");
    	signupBuilder.post();
        SimpleRequest nr = signupBuilder.build();
        
        final SimpleClient client = new SimpleClient(Xiaohuoband.getAppContext());
        final HttpResponse response = client.exec(nr);
        final SimpleResponse res = new SimpleResponse(response);
        final JSONObject root = res.getJSONObject();
       
        String[] result = new String[2];
        if (XiaohuobandSettings.DEBUG) {
        	Log.d(TAG, "requestOAuthAccessToken() code=" + res.statusCode
                    + " response=" + res.getContent());
        }
        if (res.statusCode == 200) {
        	if (doCheckStatus(root))
        	{
        		//TODO
        		result[0] = getUserId(root);
//        		result[0] = "10";
        		return result;
        	}
        	else
        	{
        		result[0] = "-1";
        		result[1] = getReason(root);
        		return result;
        	}
        }
        
        result[0] = "-1";
    	result[1] = "In verifyUser, Can't access Xiaohuoban Server! http status code is: " + res.statusCode;      	
    	return result;
    }
    
    //success: return user id, failed: return -1 and reason
    public String[] signupUser(final String username, final String email, final String password) throws IOException
    {
    	SimpleRequest.Builder signupBuilder = SimpleRequest.newBuilder();
    	signupBuilder.url(SIGNUP_URL);
    	signupBuilder.param("u", username);
    	signupBuilder.param("p", password);
    	signupBuilder.param("e", email);
    	signupBuilder.param("android", "flag");
    	signupBuilder.post();
        SimpleRequest nr = signupBuilder.build();
        
        final SimpleClient client = new SimpleClient(Xiaohuoband.getAppContext());
        final HttpResponse response = client.exec(nr);
        final SimpleResponse res = new SimpleResponse(response);
        final JSONObject root = res.getJSONObject();
        
        String[] result = new String[2];
        if (XiaohuobandSettings.DEBUG) {
        	Log.d(TAG, "requestOAuthAccessToken() code=" + res.statusCode
                    + " response=" + res.getContent());
        }
        if (res.statusCode == 200) {
        	if (doCheckStatus(root))
        	{
        		//TODO
        		result[0] = getUserId(root);
        		//result[0] = "10";
        		return result;
        	}
        	else
        	{
        		result[0] = "-1";
        		result[1] = getReason(root);
        		return result;
        	}
        }
        
        result[0] = "-1";
    	result[1] = "In signupUser, Can't access Xiaohuoban Server! http status code is: " + res.statusCode;
    	return result;
    }
    
    private boolean doCheckStatus(final JSONObject root)
    {
    	if (root == null)
    	{
    		return false;
    	}
    		
    	try {
			if (root.getString("status").equals("ok"))
			{
				return true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	
    	return false;
    }
    
    public String getReason(final JSONObject root)
    {
    	if (root == null)
    	{
    		return "";
    	}
    	try {
		    return root.getString("response");
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	
    	return "";
    }
    
    public String getUserId(final JSONObject root)
    {
    	if (root == null)
    	{
    		return "-1";
    	}
    	try {
			final JSONObject response = root.getJSONObject("response");  
		    final JSONObject data = response.getJSONObject("data");
		    return data.getString("id");
		} catch (JSONException e) {
			e.printStackTrace();
		}   	
    	
    	return "-1";
    }
    
    public static class Location {
        String geolat = null;
        String geolong = null;
        String geohacc = null;
        String geovacc = null;
        String geoalt = null;

        public Location() {
        }

        public Location(final String geolat, final String geolong, final String geohacc,
                final String geovacc, final String geoalt) {
            this.geolat = geolat;
            this.geolong = geolong;
            this.geohacc = geohacc;
            this.geovacc = geovacc;
            this.geoalt = geovacc;
        }

        public Location(final String geolat, final String geolong) {
            this(geolat, geolong, null, null, null);
        }
    }

}