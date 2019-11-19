package com.parse.starter;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.*;


import com.google.android.gms.instantapps.PackageManagerWrapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import static com.parse.starter.Dashboard.whatIs;


import static android.location.LocationManager.*;
import static java.lang.Thread.sleep;


public class MapByLocation extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {
    static int check = 0;
    private GoogleMap mMap;
    BitmapDescriptor fishLogo;
    static  Map<String, Bitmap> map = new HashMap <>();


    String coordinatesSelected;
    String titleSelected;

    static ArrayList<String> places = new ArrayList<String>();

    LocationManager locationManager;
    android.location.LocationListener locationListener;

    //////////////////////////////////////////////
    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (android.location.LocationListener) locationListener);
                    /// Location lastKNownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    //updateMap(lastKNownLocation);
                }

            }
        }
    }

    /////////////////////////////////////


    /////////////////////////////////////
    public LatLng updateMap(Location location) {
        //mMap.clear();
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10));
        //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,5));
        // Toast.makeText(this, "You are logged out", Toast.LENGTH_SHORT).show();
        mMap.addMarker(new MarkerOptions().position(userLocation).title("You're Here"));
        return userLocation;


    }

    /////////////////////////////////////


    /////////////////////////////////////

    @Override
    public boolean onMarkerClick(Marker marker) {
        // Toast.makeText(menu.this, marker.getTitle(), Toast.LENGTH_SHORT).show();// display toast
        Log.i("MARKERSSSSSSSS CHECK", marker.getTitle());
        marker.showInfoWindow();
        TextView tv = (TextView) findViewById(R.id.searchBar);
        tv.setText("You have selected: \n" + marker.getTitle());
        titleSelected = marker.getTitle();
        coordinatesSelected = Double.toString(marker.getPosition().latitude) + " " + Double.toString(marker.getPosition().longitude);
        getPictures(coordinatesSelected);
        Button exp = (Button) findViewById(R.id.explore);
        exp.setEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
      //  mMap.getUiSettings().setZoomControlsEnabled(true);

       // mMap.getUiSettings().setMapToolbarEnabled(true);
        return false;


    }


    /////////////////////////////////////


    /////////////////////////////////////
    @Override
    public void onMapClick(LatLng latLng) {

        TextView tv = (TextView) findViewById(R.id.searchBar);

        tv.setText("Welcome, select a marker to explore or hold a location on map to save");
        Button exp = (Button) findViewById(R.id.explore);
        exp.setEnabled(false);

    }


    /////////////////////////////////////


    /////////////////////////////////////

    public class DownloadTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlconnection = null;
            try {
                url = new URL(urls[0]);
                urlconnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlconnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();

                }
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                // Log.i("THIS",s);
                JSONObject jObject = new JSONObject(s);
                JSONArray geo = jObject.getJSONArray("geonames");

                for (int i = 0; i < geo.length(); i++) {

                    places.add(i, geo.getJSONObject(i).getString("title"));
                    // /Log.i("TESTTTT", geo.getJSONObject(i).getString("title");


                }
                Collections.sort(places, new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        return s1.compareToIgnoreCase(s2);
                    }
                });


                for (int i = 0; i < places.size(); i++) {
                    //String index =  new Integer(i).toString();
                    Log.i("___________________"+i, places.get(i));
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }




        }


    }


    /////////////////////////////////////


    /////////////////////////////////////

    public void GoBack(View view) {
        //ParseUser.logOut();
        // Toast.makeText(this, "You are logged out", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
        startActivity(intent);
        finish();

    }
    /////////////////////////////////////


    /////////////////////////////////////
    public void exploreFishingSpot(View view) {


        Intent intent = new Intent(getApplicationContext(), explore.class);
        intent.putExtra("coordinates", coordinatesSelected);
        intent.putExtra("placeTitle", titleSelected);
        startActivity(intent);
        finish();


    }
    /////////////////////////////////////


//Returns the location object of the current user//

    public Location calculateDistance() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (android.location.LocationListener) locationListener);
        Location lastKNownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        return lastKNownLocation;
    }


    /////////////////////////////////////


    /////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setTitle("AnglersMate");


        setContentView(R.layout.activity_map_by_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /////////////////////////////////////


    /////////////////////////////////////
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if (Dashboard.whatIs != null) {

            Log.i("TEEEHEE", String.valueOf(Dashboard.whatIs.longitude));
            Log.i("TEEEHEE", String.valueOf(Dashboard.whatIs.latitude));


            //mMap.addMarker(new MarkerOptions().position(Dashboard.whatIs).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Dashboard.whatIs, 14));
            Dashboard.whatIs = null;


        }


        //mMap.setMyLocationEnabled(true);
        //mMap.getUiSettings().setMapToolbarEnabled(true);

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {


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

        if (Build.VERSION.SDK_INT < 23) {

            locationManager.requestLocationUpdates(GPS_PROVIDER, 0, 0, locationListener);

        }// first if
        else {

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            }//inside else if
            else {
                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, (android.location.LocationListener) locationListener);
                Location lastKNownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


            }//nest else -> if > else


        }//first else


        // locationManager.requestLocationUpdates(GPS_PROVIDER, 0, 0, (android.location.LocationListener) locationListener);


        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        Button expo = (Button) findViewById(R.id.explore);
        expo.setEnabled(false);

        String a;
        Double b;


















        ParseQuery<ParseObject> query = ParseQuery.getQuery("Places");
       Log.i("MALIK MALIK", String.valueOf(Dashboard.ratingHash.size()));

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {

                        for (ParseObject object : objects) {


                            if (!object.getString("longitude").isEmpty() && !object.getString("latitude").isEmpty()) {
                                double l1 = Double.parseDouble(object.getString("longitude"));
                                double l2 = Double.parseDouble(object.getString("latitude"));


                                LatLng place = new LatLng(l2, l1);
                                String co = String.valueOf(l2) + " " + String.valueOf(l1);
                                Double markerRating = Dashboard.ratingHash.get(String.valueOf(l2) + " " + String.valueOf(l1));
                                //   Log.i("THIS THIS THIS ", Double.toString(markerRating));

                                if (markerRating <= 5 && markerRating >= 4) {
                                    fishLogo = BitmapDescriptorFactory.fromResource(R.drawable.fish_green);

                                }

                                if (markerRating < 4 && markerRating >= 3) {

                                    fishLogo = BitmapDescriptorFactory.fromResource(R.drawable.fish_yellow);
                                }


                                if (markerRating < 3) {

                                    fishLogo = BitmapDescriptorFactory.fromResource(R.drawable.fish_red);
                                }


                                LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
                                boolean gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

                                if (ParseUser.getCurrentUser().getUsername().equalsIgnoreCase(object.getString("addedBy"))) {
                                    Location ul = calculateDistance();

                                    if (gps_enabled && ul != null) {
                                        ul.getLatitude();
                                        distance(l2, l1, ul.getLatitude(), ul.getLongitude(), 'K');
                                        mMap.addMarker(new MarkerOptions().position(place).title(object.getString("placeName")).icon(BitmapDescriptorFactory.fromResource(R.drawable.fishyou)).snippet(String.valueOf(distance(l2, l1, ul.getLatitude(), ul.getLongitude(), 'K')) + " Kilometers away"));
                                    } else {


                                        mMap.addMarker(new MarkerOptions().position(place).title(object.getString("placeName")).icon(BitmapDescriptorFactory.fromResource(R.drawable.fishyou)).snippet("Location services not available"));

                                    }
                                } else {

                                    Location ul = calculateDistance();


                                    if (gps_enabled && ul != null) {
                                        ul.getLatitude();
                                        distance(l2, l1, ul.getLatitude(), ul.getLongitude(), 'K');
                                        mMap.addMarker(new MarkerOptions().position(place).title(object.getString("placeName")).snippet((String.valueOf(distance(l2, l1, ul.getLatitude(), ul.getLongitude(), 'K')) + " Kilometers away")).icon(fishLogo));
                                    } else {

                                        mMap.addMarker(new MarkerOptions().position(place).title(object.getString("placeName")).snippet("Location services not available").icon(fishLogo));
                                    }
                                }

                            }
                        }
                    }
               Dashboard.getRatings(); }
            }
        });
        ParseAnalytics.trackAppOpenedInBackground(getIntent());


    }

    ;


    @Override
    public void onMapLongClick(final LatLng latLng) {


        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String address = "";
        try {
            List<Address> listAddresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);


            // address += listAddresses.get(0).getThoroughfare();

            address += latLng.longitude + " " + latLng.latitude;
        } catch (IOException e) {
            e.printStackTrace();
        }

        //location=-33.8670522,151.1957362&radius=500&type=restaurant&keyword=cruise&key=YOUR_API_KEY
        //http://api.geonames.org/findNearbyPOIsOSMJSON?lat=37.451&lng=-122.18&username=demo
        String rqsurl = "http://api.geonames.org/findNearbyWikipediaJSON?formatted=true&lat=" + latLng.latitude + "&lng=+" + latLng.longitude + "&radius=20&maxRows=6&username=malike&style=full";

                /*
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
+ "location=" + latLng.latitude

                + ","+ latLng.longitude

                + "&radius=2000&key=AIzaSyCbbmF4HW5v4S3dGe0CrJXuxdck_Tihr0c";
*/
        Log.i("o", rqsurl);
        DownloadTask task = new DownloadTask();
        task.execute(rqsurl);


        final String finalAddress = address;
        new AlertDialog.Builder(this)
                .setTitle("Save Current Location")
                .setMessage("Would you like to save this location?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        mMap.addMarker(new MarkerOptions().position(latLng).title(finalAddress));
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                        Intent intent = new Intent(getApplicationContext(), informationBox.class);
                        intent.putExtra("lati", latLng.latitude);
                        intent.putExtra("longi", latLng.longitude);
                        //if (!places.isEmpty()) {
                        //  String[] bar = places.toArray(new String[places.size()]);
                        //  intent.putExtra("pl", bar);
                        // Log.i("INTENT PUT EXTRA", "!!!!!!!!!!");
                        /// }

                        startActivity(intent);
                        finish();

                    }
                })
                .setNegativeButton(android.R.string.no, null).show();

    }


    private double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }
        return Math.round(dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/*::  This function converts decimal degrees to radians             :*/
/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/*::  This function converts radians to decimal degrees             :*/
/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(getApplicationContext(), Dashboard.class);
            startActivity(intent);
            finish();
        }

        if (item.getItemId() == R.id.logoutButton) {

            ParseUser.logOut();
            Toast.makeText(this, "You are logged out", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();


        }

        if (item.getItemId() == R.id.myLocation) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return true;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (android.location.LocationListener) locationListener);
            Location lastKNownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

            boolean gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!gps_enabled || lastKNownLocation == null) {
                Toast.makeText(this, "Location permission needed", Toast.LENGTH_SHORT).show();


            } else {

                updateMap(lastKNownLocation);
            }


        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.map_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }



    private void getPictures(final String coo){


        ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Pictures");
        query2.whereEqualTo("coordinates", coo);
        Log.i("GEO NEWS", coo);
        query2.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (ParseObject object : objects) {

                            Log.i("GEO","GEO");
                            ParseFile file = (ParseFile) object.get("pictures");
                            final String uiq= object.getString("uniqueId");
                            Log.i("GEOOOOO", uiq) ;
                            if (file != null) {
                                file.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] data, ParseException e) {

                                        if (e == null && data != null) {

                                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                            map.put(uiq,bitmap);
                                            Log.i("Should be first", String.valueOf(map.size()));




                                        }



                                    }

                                });



                            }


                        }Log.i("Should be second 21", String.valueOf(map.size()));

                        ///  setItUp(linearLayout,coor);

                    }  Log.i("Should be second 22", String.valueOf(map.size()));

                    ///  setItUp(linearLayout,coor);

                }Log.i("Should be second 23", String.valueOf(map.size()));

                ///  setItUp(linearLayout,coor);


            }
        });

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        Log.i("Should be second 24", String.valueOf(map.size()));

      //  setItUp(linearLayout,coor);
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            onBackPressed();
            return false;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
      //  Dashboard.getRatings();
        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
        startActivity(intent);
        finish();




    }










}

























