package com.csitguys.happierhiking;

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
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
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
