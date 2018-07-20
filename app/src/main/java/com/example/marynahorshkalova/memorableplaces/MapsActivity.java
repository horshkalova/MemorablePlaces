package com.example.marynahorshkalova.memorableplaces;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;

    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);

        Intent intent = getIntent();

        if (intent.getIntExtra("Place number", 0) == 0) {

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

//                mMap.clear();
//                mMap.addMarker(new MarkerOptions().position(userLocation).title("Your location"));
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14));
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };

            // ASK FOR PERMISSIONS

            if (Build.VERSION.SDK_INT < 23) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            } else {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                } else {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                    // show user's current location when the app is lounged
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    mMap.clear();

                    mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));

                }
            }

        } else {

            LatLng userLocation = new LatLng(MainActivity.locations.get(intent.getIntExtra("Place number", 0)).latitude, MainActivity.locations.get(intent.getIntExtra("Place number", 0)).longitude);

            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(userLocation).title(MainActivity.places.get(intent.getIntExtra("Place number", 0))));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                }
            }
        }

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        // REVERSE GEOCODING
        // ---> getting names from coordinates

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        List<Address> addressList = null;

        String address = "";

        try {

            addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if (addressList != null && addressList.size() > 0) {

                Log.i("Place Info", addressList.get(0).toString());

                // get information from addressList

                if (addressList.get(0).getThoroughfare() != null) {

                    address += addressList.get(0).getThoroughfare();

                    if (addressList.get(0).getFeatureName() != null) {

                        address = address + " " + addressList.get(0).getFeatureName() + ", ";

                    } else {

                        address += ", ";
                    }
                    if (addressList.get(0).getLocality() != null) {

                        address += addressList.get(0).getLocality() + ", ";

                    }
                    if (addressList.get(0).getCountryName() != null) {

                        address += addressList.get(0).getCountryName();

                    }

                    Toast.makeText(MapsActivity.this, "Saved address: " + address, Toast.LENGTH_LONG).show();
                }
            }

        } catch (IOException e) {

            e.printStackTrace();

        } catch (Exception e) {

            e.printStackTrace();
            Toast.makeText(this, "Can not save this address", Toast.LENGTH_SHORT).show();
        }

        // IF ADDESS IS EMPTY --> SAVE A DATE

        if (address == "") {

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm yyyy-MM-dd");

            address = sdf.format(new Date());

            Toast.makeText(MapsActivity.this, "Saved date: " + address, Toast.LENGTH_LONG).show();
        }

        mMap.clear();

        // MAKE A DRAGGABLE MARKER

        MarkerOptions options = new MarkerOptions()
                .title(addressList.get(0).getAddressLine(0))
                .position(latLng);

        mMap.addMarker(options);

        // SAVE PLACE AND LOCATION IN ARRAYS

        MainActivity.places.add(address);
        MainActivity.locations.add(latLng);

        MainActivity.arrayAdapter.notifyDataSetChanged();
    }
}

