package com.yahoo.training.mdrake.instagramclient;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.*;

import org.apache.http.Header;

import java.net.URL;

/**
 * Created by mdrake on 1/21/15.
 */
public class InstagramClient {

    private final String CLIENT_ID;

    public InstagramClient(Context context){
        CLIENT_ID = context.getResources().getString(R.string.instagram_id);
    }

    void getHomeTimeline(int index, JsonHttpResponseHandler handler){
        String path = "/v1/media/popular";
        RequestParams params = new RequestParams();
        params.put("client_id",CLIENT_ID);
        client.get(getAbsoluteUrl(path), params,  handler);
        //client.get(getAbsoluteUrl(path) + "?client_id="+CLIENT_ID, null, handler);
    }

    private static final String BASE_URL = "https://api.instagram.com";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
