package com.hackthedrive.siride;

import android.util.Log;
import java.util.Map;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.apache.http.Header;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.RequestParams;

import java.io.IOException;
import java.util.HashMap;

public class APIController {
    public String VIN;
    private String URLBase;
    private ObjectMapper mapper;
    private MainActivity main;
    private ActionController control;

    public APIController(String vin, MainActivity m) {
        VIN = vin;
        URLBase = "http://api.hackthedrive.com/vehicles/"+VIN+"/";
        mapper = new ObjectMapper();
        main = m;
    }

    public void setActionController(ActionController control){
        this.control = control;
    }
    public void getCall(final String val, final ActionController.Action action){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(URLBase+val, new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.v("Got Result", new String(responseBody));
                try {
                    control.process(action, main, mapper.readValue(responseBody, HashMap.class));
                }
                catch (IOException e){
                    main.display("Siride", "Unable to map results. Result irregular.");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.v("APIController", "GET "+URLBase+val+" Failed.");
                main.display("Siride", "API request failed. Check your internet.");
            }
        });
    }

    public void postAddress(final String label, final String lat, final String lon){
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();
        param.add("label", label);
        param.add("lat", lat);
        param.add("lon", lon);

        client.post(URLBase+"navigate", param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                main.display("Siride", "Address of "+label+" was sent to your car.");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                main.display("Siride", "API request failed. Check your internet.");
            }
        });
    }

    public void postLight(final String count){
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();
        param.add("count", count);

        client.post(URLBase+"lights", param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                main.display("Siride", "Your car's headlight was set to "+count+".");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                main.display("Siride", "API request failed. Check your internet.");
            }
        });
    }

    public void postLock(final String key){
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();
        param.add("key", key);

        client.post(URLBase+"lock", param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                main.display("Siride", "Your car has been locked.");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                main.display("Siride", "API request failed. Check your internet.");
            }
        });
    }

    public void postHorn(final String key, final String count){
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();
        param.add("key", key);
        param.add("count", count);

        client.post(URLBase+"horn", param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                main.display("Siride", "Your car honked "+count+" times.");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                main.display("Siride", "API request failed. Check your internet.");
            }
        });
    }
}
