package com.csitguys.happierhiking;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

//import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, LocationListener, LocationSource {

    private GoogleMap mMap;

    private final static int MY_PERMISSION_ACCESS_FINE_LOCATION = 1;
    private LocationSource.OnLocationChangedListener mListener;
    private LocationManager locationManager;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Start the login activity if login is successful then load maps else login does not finish
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //initialize geo locate variables
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Log.e("Hiker App", locationManager.toString());
        if (locationManager != null) {
            Log.e("Hiker App", "location manager not null");
            //booleans that are used to determine the type of locator is available on the device
            boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            Log.e("Hiker App", Boolean.toString(gpsEnabled));
            boolean networkLocationEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.e("Hiker App", Boolean.toString(networkLocationEnabled));
            //if GPS is enabled than use it for accuracy
            checkGPSPermission();
            if(gpsEnabled){
                Toast.makeText(this, "provider enabled", Toast.LENGTH_SHORT).show();
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, this);

            } else if(networkLocationEnabled){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,this);
            }else{
                //do nothing
            }

        }
    }


    /**
     * Manipulates the map once available.
     * temporary marker placed at CSUMB campus
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //coordinates of CSUMB
        LatLng csumb = new LatLng(36.65481, -121.8062);
        //place marker on map
       // mMap.addMarker(new MarkerOptions().position(csumb).title("Marker at CSUMB"));
       mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(csumb, 10));
       // setMapLocation();
        Log.e("Hiker App", "map is ready");
        mMap.setLocationSource(this);
        mMap.setMyLocationEnabled(true);
        checkGPSPermission();
        Log.e("Hiker App", Boolean.toString(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)));
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int arg0) {

    }

    @Override
    public void onLocationChanged(Location location) {

        //
        if(mListener != null){
            mListener.onLocationChanged(location);
            LatLng currentLocation = new LatLng(location.getLatitude(),location.getLongitude());
            Log.i ("Hiker App", currentLocation.toString());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,10));
        } else {
            Log.e("Hiker App","mlistener is null");
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "provider enabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "provider disabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mListener = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission was granted
                    Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void checkGPSPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //TODO here is where we would show async request
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
                }
            }
        }
    }

    private void setMapLocation(){
        mMap.setMyLocationEnabled(true);
    }
}
