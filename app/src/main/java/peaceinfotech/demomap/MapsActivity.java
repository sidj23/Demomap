package peaceinfotech.demomap;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import peaceinfotech.demomap.GetData.GetDirectionsData;
import peaceinfotech.demomap.GetData.GetNearbyPlacesData;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GetNearbyPlacesData.AsyncResponse {


    private static final int LOCATION_REQUEST = 500;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS=9002;
    GoogleMap mMap;
    List<LatLng> listPoints;
    Button school, hosp, rest, btsearch,direc,btclear;
    GoogleApiClient client;
        LocationRequest locationRequest;
    Location lastLocation;
    Marker currentLocatioMarker;
    public static final int REQUEST_LOCATION_CODE = 99;
    EditText etsearch;
    Double orglatitude, orglongitude,endlatitude,endlongitude,sorglat,sorglog;
    int PROXIMITY = 1000;
    LocationListener locationListener;
    FusedLocationProviderClient  mfusedLocationProviderClient;
    LatLng latLng;
    Boolean onMarkerclick=false;
    Boolean onSearchCick=false;
    String click;
    Boolean mLocationPermissionGranted=false;

//    SharedPreferences preferences;

    LocationManager locationManager;
    String provideinfo;
    Location location;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        checkLocationPermission();
        school = findViewById(R.id.school);
        hosp = findViewById(R.id.hosp);
        rest = findViewById(R.id.rest);
        btsearch = findViewById(R.id.search);
        etsearch = findViewById(R.id.etsearch);
        direc=findViewById(R.id.direc);
        btclear=findViewById(R.id.btclesr);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        listPoints = new ArrayList<>();

        btsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loc = etsearch.getText().toString();
                List<Address> addressList = null;
                MarkerOptions mo = new MarkerOptions();

                if (!loc.equals("")) {
                    mMap.clear();
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(loc, 5);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < addressList.size(); i++) {
                        Address myAdress = addressList.get(i);
                        LatLng latLng = new LatLng(myAdress.getLatitude(), myAdress.getLongitude());
                        mo.position(latLng);
                        mo.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                        mo.title(loc);
                        mMap.addMarker(mo);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                        sorglat=myAdress.getLatitude();
                        sorglog=myAdress.getLongitude();
                    }
                    onSearchCick=true;
                }
            }
        });

        btclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etsearch.setText("");
                onSearchCick=false;
                mMap.clear();
                getLocation();
            }
        });

        hosp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click="hosp";
                mMap.clear();
                if(onSearchCick){
                    getSearchedLocation();
                    String hospital = "hospital";
                    String url = getUrl(sorglat, sorglog, hospital);
                    Object dataTransfer[] = new Object[3];
                    dataTransfer[0] = mMap;
                    dataTransfer[1] = url;
                    dataTransfer[2] = click;
                    GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData(MapsActivity.this);
                    getNearbyPlacesData.execute(dataTransfer);
                    Toast.makeText(MapsActivity.this, "hospital", Toast.LENGTH_LONG).show();
                }
                else {
                    getLocation();
                    String hospital = "hospital";
                    String url = getUrl(orglatitude, orglongitude, hospital);
                    Object dataTransfer[] = new Object[3];
                    dataTransfer[0] = mMap;
                    dataTransfer[1] = url;
                    dataTransfer[2] = click;
                    GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData(MapsActivity.this);
                    getNearbyPlacesData.execute(dataTransfer);
                    Toast.makeText(MapsActivity.this, "hospital", Toast.LENGTH_LONG).show();
                }
            }
        });

        rest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                click="rest";
                mMap.clear();

                if(onSearchCick){
                    getSearchedLocation();
                    String restaurant = "restaurant";
                    String url = getUrl(sorglat, sorglog, restaurant);
                    Object dataTransfer[] = new Object[3];
                    dataTransfer[0] = mMap;
                    dataTransfer[1] = url;
                    dataTransfer[2] = click;
                    GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData(MapsActivity.this);
                    getNearbyPlacesData.execute(dataTransfer);
                    Toast.makeText(MapsActivity.this, "restaurant", Toast.LENGTH_LONG).show();
                }
                else{
                    getLocation();
                    String restaurant = "restaurant";
                    String url = getUrl(orglatitude, orglongitude, restaurant);
                    Object dataTransfer[] = new Object[3];
                    dataTransfer[0] = mMap;
                    dataTransfer[1] = url;
                    dataTransfer[2] = click;
                    GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData(MapsActivity.this);
                    getNearbyPlacesData.execute(dataTransfer);
                    Toast.makeText(MapsActivity.this, "restaurant", Toast.LENGTH_LONG).show();
                }
            }
        });

        school.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                click="school";
                mMap.clear();

                if(onSearchCick){
                    getSearchedLocation();
                    String school = "school";
                    String url = getUrl(sorglat, sorglog, school);
                    Object dataTransfer[] = new Object[3];
                    dataTransfer[0] = mMap;
                    dataTransfer[1] = url;
                    dataTransfer[2] = click;
                    GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData(MapsActivity.this);
                    getNearbyPlacesData.execute(dataTransfer);
                    Toast.makeText(MapsActivity.this, "school", Toast.LENGTH_LONG).show();
                }
                else{
                    getLocation();
                    String school = "school";
                    String url = getUrl(orglatitude, orglongitude, school);
                    Object dataTransfer[] = new Object[3];
                    dataTransfer[0] = mMap;
                    dataTransfer[1] = url;
                    dataTransfer[2] = click;
                    GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData(MapsActivity.this);
                    getNearbyPlacesData.execute(dataTransfer);
                    Toast.makeText(MapsActivity.this, "school", Toast.LENGTH_LONG).show();
                }
            }
        });

        direc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onMarkerclick) {
                    mMap.clear();
                    if(onSearchCick){
                        getSearchedLocation();
                        LatLng latLng = new LatLng(endlatitude, endlongitude);
                        String url = getDirectionUrl(sorglat,sorglog);
                        Object dataTransfer[] = new Object[4];
                        dataTransfer[0] = mMap;
                        dataTransfer[1] = url;
                        dataTransfer[2] = new LatLng(endlatitude, endlongitude);
                        dataTransfer[3]=click;

                        GetDirectionsData getDirectionsData = new GetDirectionsData(latLng);
                        getDirectionsData.execute(dataTransfer);

                        Toast.makeText(MapsActivity.this, "Direction", Toast.LENGTH_LONG).show();
                        onMarkerclick=false;
                    }
                    else {
                        getLocation();
                        LatLng latLng = new LatLng(endlatitude, endlongitude);
                        String url = getDirectionUrl(orglatitude, orglongitude);
                        Object dataTransfer[] = new Object[4];
                        dataTransfer[0] = mMap;
                        dataTransfer[1] = url;
                        dataTransfer[2] = new LatLng(endlatitude, endlongitude);
                        dataTransfer[3] = click;

                        GetDirectionsData getDirectionsData = new GetDirectionsData(latLng);
                        getDirectionsData.execute(dataTransfer);

                        Toast.makeText(MapsActivity.this, "Direction", Toast.LENGTH_LONG).show();
                        onMarkerclick = false;
                    }
                }
                else {
                    Toast.makeText(MapsActivity.this, "Select a Destination", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private String getUrl(double latitude,double longitude, String nearbyPlace){
        StringBuilder placeurl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json");

        placeurl.append("?location="+latitude+","+longitude);
        placeurl.append("&radius="+PROXIMITY);
        placeurl.append("&type="+nearbyPlace);
        placeurl.append("&sensor=true");
        placeurl.append("&key="+"AIzaSyAEdRkdwVissmatsKvama28utF65K-4ZA8");
        return placeurl.toString();
    }

    private String getDirectionUrl(Double orglatitude,Double orglongitude){

        StringBuilder directionUrl= new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        directionUrl.append("origin="+orglatitude+","+orglongitude);
        directionUrl.append("&destination="+endlatitude+","+endlongitude);
        directionUrl.append("&key="+"AIzaSyAEdRkdwVissmatsKvama28utF65K-4ZA8");

        return directionUrl.toString();

    }

    private void  getSearchedLocation(){
        MarkerOptions mo = new MarkerOptions();
        LatLng latLng = new LatLng(sorglat, sorglog);
        mo.position(latLng);
        mo.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mMap.addMarker(mo);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mfusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()){
                    Location clocation=(Location)task.getResult();
//                    GeoJsonPoint geoJsonPoint=new GeoJsonPoint(new LatLng(location.getLatitude(),location.getLongitude()));
//                    latitude=location.getLatitude();
//                    longitude=location.getLongitude();

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(clocation.getLatitude(),clocation.getLongitude())));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

                    MarkerOptions markerOptions=new MarkerOptions();
                    markerOptions.position(new LatLng(clocation.getLatitude(),clocation.getLongitude()));
                    markerOptions.title("Current Location");
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.person_doubt));
                    mMap.addMarker(markerOptions).showInfoWindow();

                    orglatitude=clocation.getLatitude();
                    orglongitude=clocation.getLongitude();
                }
                else{
                    Toast.makeText(MapsActivity.this,"Curent location not found check your gps",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public  boolean isMapEnabled(){
        final LocationManager manager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            buildAlertMessageNoGps();
            return false;
        }

        return true;
    }

    private  void buildAlertMessageNoGps(){

        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("This Application requires GPS to work,do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent enablegpsIntent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enablegpsIntent,PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert=builder.create();
        alert.show();
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                   LOCATION_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (mLocationPermissionGranted){
                    getLocation();
                }
                else {
                    getLocationPermission();
                }
            }
        }
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

        final float[] results = new float[1];
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        mMap.setMyLocationEnabled(true);
        getLocation();

    }

    protected synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        client.connect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        mLocationPermissionGranted=false;
        switch (requestCode) {
            case LOCATION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (client == null) {
                            buildGoogleApiClient();
                            mLocationPermissionGranted=true;
                        }
                    }
                    mMap.setMyLocationEnabled(true);
                } else {
                    Toast.makeText(this, "permission Denied", Toast.LENGTH_LONG).show();
                }
                return;
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        lastLocation = location;
        getLocation();
        if (currentLocatioMarker != null) {
            currentLocatioMarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentLocatioMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        if (client != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                mMap.setMyLocationEnabled(true);
            }

        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
    }

    public boolean checkLocationPermission(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){

                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_CODE);
            }
            else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_CODE);
            }
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void processFinish(Double endlat, Double endlog, Boolean clikon) {
        endlatitude=endlat;
        endlongitude=endlog;
        onMarkerclick=clikon;
     //   Log.d("location23", endlatitude+"/"+endlongitude+"/"+onMarkerclick);
    }
}