package com.csitguys.happierhiking;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Brian on 12/15/2015.
 */
 
 //To help application connect to hike table in database REQUIREMENT #4
public class Hike {
    int id;
    String name;
    LatLng start_latlng;
    LatLng end_latlng;
    int rating;
    int difficulty;
}
