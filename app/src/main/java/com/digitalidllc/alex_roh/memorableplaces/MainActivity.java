package com.digitalidllc.alex_roh.memorableplaces;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ListView placesLV;
    private ArrayList<FavoritePlace> placesList;
    private ArrayAdapter<FavoritePlace> arrayAdapter;
    private LocationManager locationManager;
    private LocationListener locationListener;

    class FavoritePlace {
        private Location mPlace;
        private String mAddress;

        @Override
        public String toString() {
            return mAddress;
        }

        public FavoritePlace(Location place, String address) {
            this.mPlace = place;
            this.mAddress = address;
        }

        public Location getLocation() {
            return mPlace;
        }

        public String getAddress() {
            return mAddress;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set up location manager+listener
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("Current Location", location.toString());
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                try {
                    List<Address> addressesList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

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

                        Toast.makeText(MainActivity.this, address.toString(), Toast.LENGTH_SHORT).show();
                        Log.i("Address: ", address.toString());

                        updateCurrentLocation(location, address.toString());
                    } else {
                        Log.e("Address Gathering", "Failed");
                        updateCurrentLocation(location, "Address Unknown");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            startLocationListen();
            //set up list
            setUpListView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationListen();
        }
    }

    private void setUpListView() {
        placesList = new ArrayList<>();
        //add current location as first element

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        FavoritePlace currentPlace = new FavoritePlace(lastKnownLocation, "Find a place...");
        placesList.add(currentPlace);

        //set up adapter
        placesLV = findViewById(R.id.placesLV);
        arrayAdapter = new ArrayAdapter<FavoritePlace>(this, android.R.layout.simple_list_item_1,placesList);
        placesLV.setAdapter(arrayAdapter);
    }

    private void startLocationListen(){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 5, locationListener);
            }
    }

    private void updateCurrentLocation(Location location, String address){
        FavoritePlace currentPlace = new FavoritePlace(location, address);
        placesList.set(0,currentPlace);

        refreshList();
    }

    private void refreshList(){
        arrayAdapter.setNotifyOnChange(true);
        arrayAdapter.notifyDataSetChanged();
    }

}
