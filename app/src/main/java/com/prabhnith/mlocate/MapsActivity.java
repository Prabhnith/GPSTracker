package com.prabhnith.mlocate;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    public static GoogleMap mMap;
    protected LocationManager locationManager;
    double latitute, longitute;
    boolean isGPSEnabled = false;
    //    boolean canGetLocation = false;
    boolean isNetworkEnabled = false;
    private Location mlocation;
    private GPSTracker gpsTracker;
//    Location location;

    public static boolean hasPermissions(Context context, String... permissions) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        int Permission_All = 1;

        String[] Permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        if (!hasPermissions(this, Permissions)) {
            ActivityCompat.requestPermissions(this, Permissions, Permission_All);
        }
        chkGpsNetworkEnabled();
        gpsTracker = new GPSTracker(getApplicationContext());

        mlocation = gpsTracker.getLocation();

        if (mlocation != null) {
            latitute = mlocation.getLatitude();
            longitute = mlocation.getLongitude();
        }

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
        // Add a marker in mylocation and move the camera
        latitute = mlocation.getLatitude();
        longitute = mlocation.getLongitude();
        LatLng mylocation = new LatLng(latitute, longitute);
        mMap.addMarker(new MarkerOptions().position(mylocation).title("Prabh is here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mylocation));
    }

    public void chkGpsNetworkEnabled() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            String strMsg = "Please Enable your GPS or Location Service";

            if (!isGPSEnabled) {
                // notify user
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage(strMsg);
                dialog.setPositiveButton("GPS Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        getApplicationContext().startActivity(myIntent);
                        //get gps
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
                dialog.show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            Toast.makeText(this, "location :" + latitute + " " + longitute, Toast.LENGTH_SHORT).show();
        }
    }
}
