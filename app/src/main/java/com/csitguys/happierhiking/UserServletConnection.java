package com.csitguys.happierhiking;

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

    private static final String SERVER_URL = "http://cstserver2b.bitnamiapp.com/happyhiker/user";

    public static User getUser(String userName) throws Exception {
        URL url = new URL(SERVER_URL + "/" + userName);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            InputStreamReader in = new InputStreamReader(connection.getInputStream());
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            User user = gson.fromJson(in, User.class);
            return user;
        } finally {
            connection.disconnect();
        }
    }
}
