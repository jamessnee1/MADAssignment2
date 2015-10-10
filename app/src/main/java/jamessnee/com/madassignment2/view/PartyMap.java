package jamessnee.com.madassignment2.view;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import jamessnee.com.madassignment2.R;

public class PartyMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TextView dateAndTime;
    private String address;
    private String date;
    private String time;
    private double latitude, longitude;
    private LatLng start;
    //listener, locationmanager and instances to GPS and Network
    private LocationListener listener = null;
    private LocationManager locationManager = null;
    private Location myLocation;
    private MarkerOptions startPointMarker, endPointMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_map);
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

        Intent intent = getIntent();
        address = intent.getStringExtra("address");
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");
        latitude = intent.getDoubleExtra("latitude", 0);
        longitude = intent.getDoubleExtra("longitude", 0);


        dateAndTime = (TextView)findViewById(R.id.dateAndTimeTextView);
        dateAndTime.setText("Party is at " + address + " on " + date + " at " + time);

        //Get locationmanager object from system service
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        //get last known location
        myLocation = getLastKnownLocation();

        //if no location was found, IE devices wifi and GPS are off, don't even go into map
        if(myLocation == null){
            //throw error
            Toast.makeText(this, "Error: Could not establish connection! Please ensure that Wifi and GPS " +
                    "are turned on in your device's settings, and that you are in range.", Toast.LENGTH_LONG).show();
            finish();
        }
        else {

            listener = new myLocationListener();
            listener.onLocationChanged(myLocation);

            //setup map type, normal as default
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            //add marker to current location
            startPointMarker = new MarkerOptions().position(start).title("My location");
            mMap.addMarker(startPointMarker);
        }

        // Add a marker at current location and move the camera to it
        //LatLng partyLocation = new LatLng(latitude, longitude);
        LatLng partyLocation = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(partyLocation).title("Movie party"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(partyLocation));
    }

    //Location Listener
    public class myLocationListener implements LocationListener {


        @Override
        public void onLocationChanged(Location location) {

            //create new LatLng for start point
            start = new LatLng(location.getLatitude(), location.getLongitude());

            //animate camera to current location, 1 is furthest away and 21 is closest
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(start, 20);
            mMap.animateCamera(cameraUpdate);

            //check if incoming position has come from GPS or Network
            if (locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)){
                locationManager.removeUpdates(this);
            }else {
                locationManager.removeUpdates(listener);
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

            locationManager.removeUpdates(this);
            locationManager.removeUpdates(listener);

        }
    } //end myLocationListener

    //get last known location
    public Location getLastKnownLocation(){

        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;

        //iterate through all available providers
        for(String provider : providers){

            Location l = locationManager.getLastKnownLocation(provider);

            if(l == null){
                continue;
            }

            //if a better location accuracy is found in providers, set to bestLocation
            if(bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()){

                bestLocation = l;

            }

        }

        if(bestLocation == null){
            return null;
        }

        return bestLocation;
    } //end getLastKnownLocation


}
