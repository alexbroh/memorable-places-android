package com.digitalidllc.alex_roh.memorableplaces;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<MainActivity.FavoritePlace> newPlaces;
    private ArrayList<String> latitudes;
    private ArrayList<String> longitudes;
    private ArrayList<String> addresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //init variables
        newPlaces = new ArrayList<>();

        latitudes = new ArrayList<>();
        longitudes = new ArrayList<>();
        addresses = new ArrayList<>();

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

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                String address = getAddress(latLng);
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(address)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                latitudes.add(Double.toString(latLng.latitude));
                longitudes.add(Double.toString(latLng.longitude));
                addresses.add(getAddress(latLng));

                Location newLocation = new Location(LocationManager.GPS_PROVIDER);
                newLocation.setLatitude(latLng.latitude);
                newLocation.setLongitude(latLng.longitude);
                newPlaces.add(new MainActivity.FavoritePlace(newLocation, address));
            }
        });

        //retrieve passed in data
        Intent intent = getIntent();
        String address = intent.getStringExtra("address");
        double latitude = intent.getDoubleExtra("latitude",0);
        double longitude = intent.getDoubleExtra("longitude",0);

        // Add a marker in Sydney and move the camera
        LatLng favoritePlace = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(favoritePlace).title(address)).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(favoritePlace, 5));
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("latitudes", latitudes);
        resultIntent.putExtra("longitudes", longitudes);
        resultIntent.putExtra("addresses", addresses);
        setResult(MainActivity.RESULT_OK, resultIntent);

        super.onBackPressed();
        finish();
    }

    private String getAddress(LatLng latLng){
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            List<Address> addressesList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if (addressesList != null && addressesList.size() > 0) {
                StringBuilder address = new StringBuilder();

                if (addressesList.get(0).getThoroughfare() != null) {
                    address.append(addressesList.get(0).getThoroughfare() + " ");
                }

                if (addressesList.get(0).getLocality() != null) {
                    address.append(addressesList.get(0).getLocality() + "\n");
                }

                if (addressesList.get(0).getPostalCode() != null) {
                    address.append(addressesList.get(0).getPostalCode() + " ");
                }

                if (addressesList.get(0).getAdminArea() != null) {
                    address.append(addressesList.get(0).getAdminArea());
                }

                Log.i("Address: ", address.toString());
                return address.toString();
            }
            else return "Address Not Found";
        } catch (Exception e){
                e.printStackTrace();
                return "Address Not Found";
            }
    }
}
