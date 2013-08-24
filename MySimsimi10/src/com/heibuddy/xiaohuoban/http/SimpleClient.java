package com.heibuddy.xiaohuoban.http;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.heibuddy.xiaohuoband.XiaohuobandSettings;
import com.heibuddy.xiaohuoban.util.NetworkHelper;

public class SimpleClient {

    private static final String TAG = SimpleClient.class.getSimpleName();

    private final Context mAppContext;

    public SimpleClient(final Context context) {
        this.mAppContext = context.getApplicationContext();
    }

    public HttpResponse exec(final SimpleRequest cr) throws IOException {
        if (TextUtils.isEmpty(cr.url)) {
            throw new IOException("request url must not be empty or null.");
        }
        signRequest(cr);
        return executeImpl(cr.getHttpRequest());
    }

    private final HttpResponse executeImpl(final HttpRequestBase request)
            throws IOException {
        if (request == null) {
            throw new IOException("http request is null");
        }
        final HttpClient client = getHttpClient();
        if (XiaohuobandSettings.DEBUG) {
            Log.d(SimpleClient.TAG, "[Request] "
                    + request.getRequestLine().toString());
        }
        final HttpResponse response = client.execute(request);
        if (XiaohuobandSettings.DEBUG) {
            Log.d(SimpleClient.TAG, "[Response] "
                    + response.getStatusLine().toString());
        }
        return response;
    }

    public final HttpResponse get(final String url) throws IOException {
        return executeImpl(new HttpGet(url));
    }

    public final Bitmap getBitmap(final String url) throws IOException {
        final HttpResponse response = get(url);
        final int statusCode = response.getStatusLine().getStatusCode();
        if (XiaohuobandSettings.DEBUG) {
            Log.d(SimpleClient.TAG, "getBitmap() statusCode=" + statusCode
                    + " [" + url + "]");
        }
        if (statusCode == 200) {
            return BitmapFactory
                    .decodeStream(response.getEntity().getContent());
        }
        return null;
    }

    protected HttpClient getHttpClient() {
        return NetworkHelper.createHttpClient(this.mAppContext);
    }

    public final HttpResponse post(final String url,
            final List<SimpleRequestParam> params) throws IOException {
        return executeImpl(SimpleRequest.newBuilder().url(url).params(params)
                .post().build().getHttpRequest());
    }

    protected void signRequest(final SimpleRequest cr) {
    }

}
