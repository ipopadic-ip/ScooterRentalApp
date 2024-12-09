package com.example.luxdrive_1.auth;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AuthUtils {
    public static void makeAuthenticatedRequest(Context context, String url, int method, JSONObject requestBody,
                                                Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);

        SharedPreferences preferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        String token = preferences.getString("jwt_token", null);

        JsonObjectRequest request = new JsonObjectRequest(method, url, requestBody, listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (token != null) {
                    headers.put("Authorization", "Bearer " + token);
                }
                return headers;
            }
        };

        queue.add(request);
    }
}

