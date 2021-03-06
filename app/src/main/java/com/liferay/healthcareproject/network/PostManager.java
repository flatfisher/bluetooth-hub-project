package com.liferay.healthcareproject.network;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Map;

/**
 * Created by flatfisher on 10/1/15.
 */


public class PostManager implements Response.Listener<String>, Response.ErrorListener {

    private Context context;

    private RequestQueue requestQueue;
    private ResponseListener responseListener;
    private String POST_URL;
    private String POST_TAG;

    private Toolbar toolbar;
    private ProgressBar materialProgressBar;

    public interface ResponseListener {

        public Response.Listener<String> onResponse(String response);

        public Response.ErrorListener onErrorResponse();

    }

    public PostManager(Context context, String posturl, ResponseListener responseListener) {

        this.context = context;

        POST_URL = posturl;

        POST_TAG = posturl;

        this.responseListener = responseListener;
    }

    public void setMaterialProgressBar(Toolbar toolbar) {

        this.toolbar = toolbar;

        materialProgressBar = new ProgressBar(context);

        toolbar.addView(materialProgressBar);

        materialProgressBar.setVisibility(View.GONE);

        stopProgressBar();

    }

    public void setTAG(String tag) {

        POST_TAG = tag;

    }

    public void connect(final Map<String, String> params) {

        requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, POST_URL, this, this) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        requestQueue.add(stringRequest);

        if (toolbar != null) {
            showProgressBar();
        }

    }

    private void stop() {

        if (requestQueue != null) {
            requestQueue.cancelAll(POST_TAG);
        }

    }

    @Override
    public void onResponse(String response) {

        responseListener.onResponse(response);
        if (toolbar != null) {
            stopProgressBar();
        }

    }

    @Override
    public void onErrorResponse(VolleyError error) {

        responseListener.onErrorResponse();

        if (toolbar != null) {
            stopProgressBar();
        }

    }

    private void showProgressBar() {
        materialProgressBar.setVisibility(View.VISIBLE);
    }

    private void stopProgressBar() {
        materialProgressBar.setVisibility(View.GONE);
    }
}
