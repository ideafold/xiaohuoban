package com.heibuddy.xiaohuoban.http;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SimpleResponse implements ResponseCode {
    private static final String TAG = SimpleResponse.class.getSimpleName();

    private BufferedHttpEntity entity;
    public final StatusLine statusLine;
    public final int statusCode;

    public SimpleResponse(final HttpResponse response) {
        this.statusLine = response.getStatusLine();
        this.statusCode = this.statusLine.getStatusCode();

        final HttpEntity wrappedHttpEntity = response.getEntity();
        if (wrappedHttpEntity != null) {
            try {
                this.entity = new BufferedHttpEntity(wrappedHttpEntity);
            } catch (final IOException e) {
                this.entity = null;
            }
        }
    }

    public final String getContent() throws IOException {
        if (this.entity != null) {
            return EntityUtils.toString(this.entity, HTTP.UTF_8);
        }
        return null;
    }

    public final JSONArray getJSONArray() throws IOException {
        JSONArray json = null;
        try {
            final String content = getContent();
            if (content != null) {
                json = new JSONArray(content);
            }
        } catch (final JSONException e) {
        }
        return json;
    }

    public final JSONObject getJSONObject() throws IOException {
        JSONObject json = null;
        try {
            final String content = getContent();
            if (content != null) {
                json = new JSONObject(content);
            }
        } catch (final JSONException e) {
        }
        return json;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("SimpleResponse [entity=");
        builder.append(this.entity);
        builder.append(", statusLine=");
        builder.append(this.statusLine);
        builder.append(", statusCode=");
        builder.append(this.statusCode);
        builder.append("]");
        return builder.toString();
    }
}
