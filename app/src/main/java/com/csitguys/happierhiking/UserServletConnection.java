package com.csitguys.happierhiking;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import  java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by Brian on 12/11/2015.
 */
public class UserServletConnection {
    private static final int NO_UPDATE = 304;

    private static final String SERVER_URL = "http://cstserver2b.bitnamiapp.com/happyhiker/user";

    public static User getUser(User user) throws Exception {
        URL url = new URL(SERVER_URL + "/" + user.emailAddress + ":" + user.password);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            InputStreamReader in = new InputStreamReader(connection.getInputStream());
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            User userOutput = gson.fromJson(in, User.class);
            return userOutput;
        } finally {
            connection.disconnect();
        }
    }
    public static boolean putUser(User user) throws Exception {
        URL url = new URL(SERVER_URL);
        boolean accountCreated = true;
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        Log.i("UserServlet put user", "open connection "+SERVER_URL);
        try {
            connection.setDoOutput(true);  // indicate this is a POST request
            connection.setRequestMethod("PUT");
            PrintWriter out = new PrintWriter( connection.getOutputStream());
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            gson.toJson(user, out);

            out.close();

            int responseCode = connection.getResponseCode();
            if(responseCode == NO_UPDATE) accountCreated =false;
            Log.e("userservlet:putuser   ",Integer.toString(responseCode));

        } finally {
            connection.disconnect();
            Log.i("ServerFetcher.putCrime", "close connection");
            return accountCreated;
        }
    }
}
