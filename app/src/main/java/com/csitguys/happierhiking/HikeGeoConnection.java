package com.csitguys.happierhiking;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import  java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Brian on 12/15/2015.
 */
public class HikeGeoConnection {
    private static final String SERVER_URL = "http://cstserver2b.bitnamiapp.com/happyhiker/geohike";

    public static HikeList getHikes(LatLng latLng, boolean coords)throws Exception {
       String urlPath = SERVER_URL + "/" + latLng.latitude + ":" + latLng.longitude ;
        if(coords){
            urlPath = urlPath.concat("&true");
        }
        URL url = new URL(urlPath);
        Log.e("serverUrl", url.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            InputStreamReader in = new InputStreamReader(connection.getInputStream());
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();

            HikeList hikeList = gson.fromJson(in, HikeList.class);
            for (LatLngList l:
                hikeList.list ) {
                Log.e("gson input", l.list.toString());
            }
            Log.e("gson input", hikeList.list.toString());
            return hikeList;
        } finally {
            connection.disconnect();
        }

    }
    public static Hike getHike(LatLng start, LatLng end)throws Exception{
        String urlPath = SERVER_URL + "/" + start.latitude + ":" + start.longitude +'$'+end.latitude+':' + end.longitude;
        URL url = new URL(urlPath);
        Log.e("serverUrl", url.toString());
        Log.e("new server url", urlPath);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            InputStreamReader in = new InputStreamReader(connection.getInputStream());
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            Hike hike = gson.fromJson(in,Hike.class);
            return hike;
        } finally {
            connection.disconnect();
        }
    }

}
