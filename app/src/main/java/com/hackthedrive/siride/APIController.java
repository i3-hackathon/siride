package com.hackthedrive.siride;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.apache.http.Header;
import com.fasterxml.jackson.databind.ObjectMapper;

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
}
