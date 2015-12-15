package com.csitguys.happierhiking;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import java.util.ArrayList;

//import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, LocationListener, LocationSource {

    private GoogleMap mMap;

    private final static int MY_PERMISSION_ACCESS_FINE_LOCATION = 1;
    private LocationSource.OnLocationChangedListener mListener;
    private SharedPreferences sharedpreferences;
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
                //Toast.makeText(this, "provider enabled", Toast.LENGTH_SHORT).show();
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
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d("lat long: " , latLng.toString());
            }
        });
        //coordinates of CSUMB
        LatLng csumb = new LatLng(36.65481, -121.8062);
        //place marker on map
       // mMap.addMarker(new MarkerOptions().position(csumb).title("Marker at CSUMB"));
       mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(csumb, 10));
       // setMapLocation();
        Log.e("Hiker App", "map is ready");
        mMap.setLocationSource(this);
        mMap.setMyLocationEnabled(true);
        //makes sure app permissions are set if not prompts to allow
        checkGPSPermission();

        sharedpreferences = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
        String userName = sharedpreferences.getString(getString(R.string.saved_user_name), null);
        if(userName!=null){
            Toast.makeText(this, "Logged in as: " + userName, Toast.LENGTH_SHORT).show();
        }
        Log.e("Hiker App", Boolean.toString(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)));
        drawPolyLines();
    }

    private void drawPolyLines() {
        //will get polylines from database then I will draw them

        HikeList hikes = getPolyLines();
        int i = 0;

        for(LatLngList l : hikes.list){
            i++;
            Log.d("Hapy hiker:", l.list.toString());
            mMap.addPolyline(new PolylineOptions()
                    .addAll(l.list)
                    .color(Color.rgb(((255 + (i*10))%255), (0 + (i * 50))%255, (40 +(i*30))%255))
                    .width(10)
                    .geodesic(true)
            );
        }

    }

    private HikeList getPolyLines() {
        HikeList list = new HikeList();
        LatLngList coords= new LatLngList();
        LatLngList coords2= new LatLngList();
        coords.list.add(new LatLng(36.65481, -121.8062));
        coords.list.add( new LatLng(36.65481, -121.81));
        coords.list.add(new LatLng(36.65481, -121.82));
        coords.list.add(new LatLng(36.67, -121.83));
        coords.list.add(new LatLng(36.68, -121.84));
        coords.list.add(new LatLng(36.7, -121.85));
        Log.d("hapy hiker coords:",coords.list.toString());
        list.list.add(coords);
       // coords.list.clear();
        coords2.list.add(new LatLng(36.642121739738876, -121.76879335194826));
        coords2.list.add(new LatLng(36.64065261569098,-121.76833570003508));
        coords2.list.add(new LatLng(36.638724230157635,-121.76593244075777));
        coords2.list.add(new LatLng(36.63780602404419,-121.76478814333677));
        coords2.list.add(new LatLng(36.63633654865388,-121.76272820681334));
        coords2.list.add(new LatLng(36.633765302852304,-121.76055360585451));
        coords2.list.add(new LatLng(36.63156123271358,-121.76055360585451));
        coords2.list.add(new LatLng(36.629448849985955,-121.76055360585451));
        coords2.list.add(new LatLng(36.627703958275646,-121.76055360585451));
        coords2.list.add(new LatLng(36.62595902705994,-121.76055360585451));
        coords2.list.add(new LatLng(36.62403027377779,-121.75998162478209));
        coords2.list.add(new LatLng(36.62145834826537,-121.75975263118744));
        coords2.list.add(new LatLng(36.618886606041805,-121.76055360585451));
        coords2.list.add(new LatLng(36.61870281077862,-121.76158357411623));
        coords2.list.add(new LatLng(36.61778436609727,-121.76478814333677));
        coords2.list.add(new LatLng(36.617141475220876,-121.76719140261412));
        coords2.list.add(new LatLng(36.617141475220876,-121.76719140261412));
        coords2.list.add(new LatLng(36.61521250131533,-121.76993798464537));
        coords2.list.add(new LatLng(36.612181159104814,-121.7731422185898));
        coords2.list.add(new LatLng(36.60703670082988,-121.7731422185898));
        coords2.list.add(new LatLng(36.60428064794366,-121.77211225032806));
        coords2.list.add(new LatLng(36.60097325449251,-121.77096795290707));
        coords2.list.add(new LatLng(36.597757507410755,-121.7697089910507));
        coords2.list.add(new LatLng(36.59408239604083,-121.76536012440921));
        list.list.add(coords2);
        return list;
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
