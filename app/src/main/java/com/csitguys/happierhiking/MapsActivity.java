package com.csitguys.happierhiking;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

//import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Start the login activity if login is successful then load maps else login does not finish
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        /**
         *Test block to debug why emulator was not running APP emulator was running play services 6.7
         * while Gradle compiler was running 8.3.0
         * dropped gradle to
         ***/
        try {
            int v = getPackageManager().getPackageInfo("com.google.android.gms", 0).versionCode;
            Log.i("Hiker App", Integer.toString(v));
        } catch(Exception e){

        }
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * temporary marker placed at CSUMB campus
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng csumb = new LatLng(36.65481,-121.8062);
       // mMap.moveCamera(CameraUpdateFactory.zoomIn());

        mMap.addMarker(new MarkerOptions().position(csumb).title("Marker at CSUMB"));

      //  mMap.moveCamera(CameraUpdateFactory.zoomIn());

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(csumb,10));
    }
    @Override
    public void onConnected(Bundle bundle){

    }
    @Override
    public void onConnectionSuspended(int arg0){

    }
}
