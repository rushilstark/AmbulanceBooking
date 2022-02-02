package com.example.myapplication;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.datatransport.runtime.Destination;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.MapFragment;
import com.google.android.libraries.places.api.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CustomerMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    private FusedLocationProviderClient mFusedLocationClient;

    private Button mLogout,mRequest,mSettings,mHistory,mRequest1;
    private LatLng pickupLocation;
    private Boolean requestBol = false;
    private Marker pickupMarker;

    private SupportMapFragment mapFragment;

    private String destination;

    private LatLng destinationLatlng;


    public int counter;

    private LinearLayout mDriverInfo;

    private TextView mDriverName, mDriverPhone, mDriverCar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        destinationLatlng = new LatLng(0.0, 0.0);


        mDriverName = (TextView) findViewById(R.id.driverName);
        mDriverPhone = (TextView) findViewById(R.id.driverPhone);
        mDriverCar = (TextView) findViewById(R.id.driverCar);

        mRequest = (Button) findViewById(R.id.request);
        mRequest1 = (Button) findViewById(R.id.button2);
        mSettings = (Button) findViewById(R.id.settings);

        mHistory = (Button) findViewById(R.id.history);
        mLogout = (Button) findViewById(R.id.logout);
        mDriverName.setText("BOB");
        mDriverPhone.setText("9390554435");
        mDriverCar.setText("TS04YG2744");
        int min = 0;
        int max = 5;
        final double a = Math.floor((Math.random()*(max-min+1)+min) * 100) / 100;

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(CustomerMapActivity.this, Welcome_Activity.class);
                startActivity(intent);
                finish();
            }

        });
        mRequest1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               int time= (int) ((a/30)*(60)*(60)*(1000));
                new CountDownTimer(time, 1000) {
                    public void onTick(long millisUntilFinished) {
                        NumberFormat f = new DecimalFormat("00");
                        long hour = (millisUntilFinished / 3600000) % 24;
                        long min = (millisUntilFinished / 60000) % 60;
                        long sec = (millisUntilFinished / 1000) % 60;
                        mRequest1.setText(f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
                    }
                    public void onFinish() {
                        mRequest1.setText("Ambulance Arrived");
                    }
                }.start();
            }
        });

        mRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestBol = true;

                pickupLocation = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
                pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Pickup Here").icon(BitmapDescriptorFactory.fromResource(R.mipmap.patient)));

                mRequest.setText("Getting Your Ambulance...");

                double LocationLat = 17.3958;
                double LocationLng = 78.4312;
                mRequest.setText("Ambulance Found");
                LatLng driverLatLng = new LatLng(LocationLat, LocationLng);
                if(mDriverMarker!=null){
                    mDriverMarker.remove();
                }
                Location loc1 = new Location("");
                loc1.setLatitude(pickupLocation.latitude);
                loc1.setLongitude(pickupLocation.longitude);

                Location loc2 = new Location("");
                loc2.setLatitude(driverLatLng.latitude);
                loc2.setLongitude(driverLatLng.longitude);

                float distance = loc1.distanceTo(loc2);
                if(distance<100){
                    mRequest.setText("Ambulance Arrived");
                }else{
                    mRequest.setText("Ambulance Found: "+ String.valueOf(a)+ " Kms away...");
                }


            }
        });


        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(CustomerMapActivity.this,CustomerSettingsActivity.class);
                startActivity(intent);

            }

        });


        mHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CustomerMapActivity.this,HistoryActivity.class);
                intent.putExtra("customerOrDriver","Customers");
                startActivity(intent);


            }
        });

    }

    private int radius=1;
    private Boolean driverFound = false;
    private String driverFoundID;

    private Marker mDriverMarker;


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            }else{
                checkLocationPermission();
            }
        }

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);

    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {

                mLastLocation = location;

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

                if(!getDriversAroundStarted)
                    getDriversAround();

            }
        }

    };

    boolean getDriversAroundStarted = false;
    List<Marker> markers = new ArrayList<Marker>();
    private void getDriversAround(){
        getDriversAroundStarted = true;
        LatLng sydney = new LatLng(17.3958, 78.4312);
        LatLng sydney1 = new LatLng(17.3608, 78.3978);
        LatLng sydney2 = new LatLng(17.3993, 78.4627);
        LatLng sydney3 = new LatLng(17.4435, 78.3772);
        LatLng sydney4 = new LatLng(17.3782, 78.4208);
        LatLng sydney5 = new LatLng(17.3990, 78.4157);
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("driversAvailable");
        Marker mDriverMarker1 = mMap.addMarker(new MarkerOptions().position(sydney).title("cddsd").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
        mDriverMarker1.setTag("cddsd");
        Marker mDriverMarker2 = mMap.addMarker(new MarkerOptions().position(sydney1).title("cdzcsd").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
        mDriverMarker2.setTag("cdzcsd");
        Marker mDriverMarker3 = mMap.addMarker(new MarkerOptions().position(sydney2).title("cdsdzc").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
        mDriverMarker3.setTag("cdsdzc");
        Marker mDriverMarker4 = mMap.addMarker(new MarkerOptions().position(sydney3).title("cdsdcv").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
        mDriverMarker4.setTag("cdsdcv");
        Marker mDriverMarker5 = mMap.addMarker(new MarkerOptions().position(sydney4).title("cdsdfv").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
        mDriverMarker5.setTag("cdsdfv");
        Marker mDriverMarker6 = mMap.addMarker(new MarkerOptions().position(sydney5).title("cdsddcd").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
        mDriverMarker6.setTag("cdsddcd");
        Intent intent = new Intent(CustomerMapActivity.this , final1.class);

        GeoFire geoFire = new GeoFire(driverLocation);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLongitude(), mLastLocation.getLatitude()), 999999999);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {



                    LatLng driverLocation = new LatLng(location.latitude, location.longitude);

                    Marker mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLocation).title(key).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance)));
                    mDriverMarker.setTag(key);

                    markers.add(mDriverMarker);

                    for(Marker markerIt : markers){
                        if(markerIt.getTag().equals(key))
                            return;
                    }





            }

            @Override
            public void onKeyExited(String key) {
                for(Marker markerIt : markers){
                    if(markerIt.getTag().equals(key)){
                        markerIt.remove();
                    }
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                for(Marker markerIt : markers){
                    if(markerIt.getTag().equals(key)){
                        markerIt.setPosition(new LatLng(location.latitude, location.longitude));
                    }
                }
            }

            @Override
            public void onGeoQueryReady() {
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                new  android.app.AlertDialog.Builder(this)
                        .setTitle("Please give permission...")
                        .setMessage("Please give permission...")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(CustomerMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

                            }
                        })
                        .create()
                        .show();
            }
            else{
                ActivityCompat.requestPermissions(CustomerMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please provide the permission...", Toast.LENGTH_LONG).show();
                }
                break;
            }


        }}

        private DatabaseReference driveHasEndedRef;
    private ValueEventListener driveHasEndedRefListener;
    private void getHasRideEnded(){
       // String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        driveHasEndedRef  = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest").child("customerRideId");
        driveHasEndedRefListener = driveHasEndedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                }else{

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

   }