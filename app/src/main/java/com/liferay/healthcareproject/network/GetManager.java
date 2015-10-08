package com.liferay.healthcareproject.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Created by flatfisher on 10/7/15.
 */
public abstract class GetManager implements Response.Listener<JSONObject>, Response.ErrorListener {

    private RequestQueue requestQueue;

    public GetManager(Context context,String requestUrl) {

        requestQueue = Volley.newRequestQueue(context);

        requestQueue.add(new JsonObjectRequest(Request.Method.GET,requestUrl,null, this, this));

    }
}
