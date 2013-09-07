package com.heibuddy.xiaohuoban.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.heibuddy.xiaohuoban.http.SimpleRequestParam;
import com.heibuddy.xiaohuoban.http.support.GzipRequestInterceptor;
import com.heibuddy.xiaohuoban.http.support.GzipResponseInterceptor;
import com.heibuddy.xiaohuoban.http.support.RequestRetryHandler;
import com.heibuddy.xiaohuoband.XiaohuobandSettings;

public final class NetworkHelper {
    private static final String TAG = NetworkHelper.class.getSimpleName();
    public static final boolean DEBUG = XiaohuobandSettings.DEBUG;
    
    public static final int SOCKET_BUFFER_SIZE = 8 * 1024;
    public static final int CONNECTION_TIMEOUT_MS = 20000;
    public static final int SOCKET_TIMEOUT_MS = 20000;
    public static final int MAX_TOTAL_CONNECTIONS = 20;
    public static final int MAX_RETRY_TIMES = 3;
    private static final String WIFI = "WIFI";
    private static final String MOBILE_CTWAP = "ctwap";
    private static final String MOBILE_CMWAP = "cmwap";
    private static final String MOBILE_3GWAP = "3gwap";
    private static final String MOBILE_UNIWAP = "uniwap";

    /**
     * Based on network status, fill proxy param
     * @param context
     * @param httpParams
     */
    private static final void checkAndSetProxy(final Context context,
            final HttpParams httpParams) {
        boolean needCheckProxy = true;

        final ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if ((networkInfo == null)
                || NetworkHelper.WIFI.equalsIgnoreCase(networkInfo
                        .getTypeName()) || (networkInfo.getExtraInfo() == null)) {
            needCheckProxy = false;
        }
        if (needCheckProxy) {
            final String typeName = networkInfo.getExtraInfo();
            if (NetworkHelper.MOBILE_CTWAP.equalsIgnoreCase(typeName)) {
                final HttpHost proxy = new HttpHost("10.0.0.200", 80);
                httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
            } else if (NetworkHelper.MOBILE_CMWAP.equalsIgnoreCase(typeName)
                    || NetworkHelper.MOBILE_UNIWAP.equalsIgnoreCase(typeName)
                    || NetworkHelper.MOBILE_3GWAP.equalsIgnoreCase(typeName)) {
                final HttpHost proxy = new HttpHost("10.0.0.172", 80);
                httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
            }
        }

        // String defaultProxyHost = android.net.Proxy.getDefaultHost();
        // int defaultProxyPort = android.net.Proxy.getDefaultPort();
        // if (defaultProxyHost != null && defaultProxyHost.length() > 0
        // && defaultProxyPort > 0) {
        // HttpHost proxy = new HttpHost(defaultProxyHost, defaultProxyPort);
        // httpParams.setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
        // }
    }

    public static String sendRequestFromHttpClient(String url, String soapContent)
    { 
        HttpClient httpclient = null;  
        HttpPost httpPost = null;
        BufferedReader reader = null;  
        int i = 0;
  
    	final HttpParams httpParams = new BasicHttpParams();
    	HttpConnectionParams.setConnectionTimeout(httpParams, 10*1000);
        while (i < 2)
        {
            try 
            {
            	httpclient = new DefaultHttpClient(httpParams);  
                httpPost = new HttpPost(url);  
                StringEntity myEntity = new StringEntity(soapContent, "UTF-8");  
                httpPost.addHeader("Content-Type", "text/xml; charset=UTF-8");  
                httpPost.setEntity(myEntity);  
                HttpResponse response = httpclient.execute(httpPost);  
                HttpEntity resEntity = response.getEntity();  
                if (resEntity != null) {  
                    reader = new BufferedReader(new InputStreamReader(resEntity  
                            .getContent(), "UTF-8"));  
                    StringBuffer sb = new StringBuffer();  
                    String line = null;  
                    while ((line = reader.readLine()) != null) {  
                        sb.append(line);  
                        sb.append("\r\n");  
                    }  
                    return sb.toString();  
                }  
            } catch (Exception e) { 
            	if (DEBUG) Log.d(TAG, e.toString());
                i++;  
                if (i == 2) {
                    Log.e("HttpUtils sendRequestFromHttpClient", "not connect:" + url + "\n" + e.getMessage());  
                }  
            } finally {  
                if (httpPost != null) {  
                    httpPost.abort();  
                }  
                if (reader != null) {  
                    try {  
                        reader.close();  
                    } catch (IOException e) {  
                        Log.e(TAG, e.toString());  
                    }  
                }  
                if (httpclient != null) {  
                    httpclient.getConnectionManager().shutdown();  
                }  
            }  
        }
        
        return null; 
    }
	
	public static byte[] getImage(String path) throws Exception {
        HttpURLConnection conn = null;
        InputStream inStream = null;
        ByteArrayOutputStream outStream = null;
		URL url = new URL(path);
        int i = 0;
        
        while (i < 1)
        {
            try 
            {
            	conn = (HttpURLConnection)url.openConnection();
            	conn.setRequestMethod("GET");
            	conn.setConnectTimeout(6*1000);
            	inStream = conn.getInputStream();
            	
            	outStream = new ByteArrayOutputStream();
        		byte[] buffer = new byte[1024];
        		int len = 0;
        		while ((len = inStream.read(buffer)) != -1){
        			outStream.write(buffer, 0, len);
        		}
        		
        		inStream.close();
        		return outStream.toByteArray();           	
            } catch (Exception e) {  
                i++;  
                if (i == 1) {  
                    Log.e("HttpUtils getImage", "Fail to fetch url:" + url + "\n" + e.getMessage());  
                }  
            } finally {  
                if (conn != null) {  
                	conn.disconnect();  
                }  
                if (inStream != null) {  
                    try {  
                    	inStream.close();  
                    } catch (IOException e) {  
                    	Log.e(TAG, e.toString());
                    }  
                }
            }
        }
		return null;
	}
	
    public final static DefaultHttpClient createHttpClient(final Context context) {
        final HttpParams params = NetworkHelper.createHttpParams();
        final DefaultHttpClient client = new DefaultHttpClient(params);
        client.addRequestInterceptor(new GzipRequestInterceptor());
        client.addResponseInterceptor(new GzipResponseInterceptor());
        client.setHttpRequestRetryHandler(new RequestRetryHandler(
                NetworkHelper.MAX_RETRY_TIMES));
        NetworkHelper.checkAndSetProxy(context, params);
        return client;
    }

    private static final HttpParams createHttpParams() {
        final HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setUseExpectContinue(params, false);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        ConnManagerParams.setTimeout(params, NetworkHelper.SOCKET_TIMEOUT_MS);
        HttpConnectionParams.setConnectionTimeout(params,
                NetworkHelper.CONNECTION_TIMEOUT_MS);
        HttpConnectionParams.setSoTimeout(params,
                NetworkHelper.SOCKET_TIMEOUT_MS);

        ConnManagerParams.setMaxConnectionsPerRoute(params,
                new ConnPerRouteBean(NetworkHelper.MAX_TOTAL_CONNECTIONS));
        ConnManagerParams.setMaxTotalConnections(params,
                NetworkHelper.MAX_TOTAL_CONNECTIONS);

        HttpConnectionParams.setStaleCheckingEnabled(params, false);
        HttpConnectionParams.setTcpNoDelay(params, true);
        HttpConnectionParams.setSocketBufferSize(params,
                NetworkHelper.SOCKET_BUFFER_SIZE);
        HttpClientParams.setRedirecting(params, false);
        HttpProtocolParams.setUserAgent(params, "Xiaohuoban for Android");
        return params;
    }

    private static String encode(final String input) {
        try {
            return URLEncoder.encode(input, HTTP.UTF_8);
        } catch (final UnsupportedEncodingException e) {
        }
        return input;
    }

    public static MultipartEntity encodeMultipartParameters(
            final List<SimpleRequestParam> params) {
        if (CommonHelper.isEmpty(params)) {
            return null;
        }
        final MultipartEntity entity = new MultipartEntity();
        try {
            for (final SimpleRequestParam param : params) {
                if (param.isFile()) {
                    entity.addPart(param.getName(),
                            new FileBody(param.getFile()));
                } else {
                    entity.addPart(
                            param.getName(),
                            new StringBody(param.getValue(), Charset
                                    .forName(HTTP.UTF_8)));
                }
            }
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return entity;
    }

    public static HttpEntity encodePostParameters(
            final List<SimpleRequestParam> params) {
        HttpEntity entity = null;
        if (!CommonHelper.isEmpty(params)) {
            try {
                entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            } catch (final UnsupportedEncodingException e) {
            }
        }
        return entity;
    }

    public static String encodeQueryParameters(
            final List<SimpleRequestParam> params) {
        if (CommonHelper.isEmpty(params)) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            final SimpleRequestParam p = params.get(i);
            if (p.isFile()) {
                throw new IllegalArgumentException("The param of GET can't be File!");
            }
            if (i > 0) {
                sb.append("&");
            }
            sb.append(NetworkHelper.encode(p.getName())).append("=")
                    .append(NetworkHelper.encode(p.getValue()));
        }

        return sb.toString();
    }

    public static final boolean isConnected(final Context context) {
        final ConnectivityManager connec = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        
        if (connec != null) 
        {
			NetworkInfo info = connec.getActiveNetworkInfo(); 
			if (info != null&& info.isConnected()) { 
				if (info.getState() == NetworkInfo.State.CONNECTED) { 
					return true; 
				}
			}
		}
        
        return false;
    }

    public static final boolean isNotConnected(final Context context) {
        return !NetworkHelper.isConnected(context);
    }

    public static final boolean isWifi(final Context context) {
        final ConnectivityManager connec = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo info = connec.getActiveNetworkInfo();
        return (info != null)
                && (info.getType() == ConnectivityManager.TYPE_WIFI);
    }
}
