/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;





public class Dashboard extends AppCompatActivity {
    TextView welcomeMessage;
    public static LatLng whatIs;
    float finalRating, totalRating;
    int counter;
    String coo;

    static Map<String, Double> ratingHash = new HashMap <>();





    public void showLoginPage(View view) {


    }

    public void showMapByLocation(View view) {


        Intent intent = new Intent(getApplicationContext(), MapByLocation.class);
        startActivity(intent);
        finish();

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        getRatings();




        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        String name = ParseUser.getCurrentUser().getUsername().toString();

        welcomeMessage = (TextView) findViewById(R.id.userNameWelcome);
        welcomeMessage.setText("Welcome to AnglersMate,  ");
        setTitle("AnglersMate");


    }


    public void getGeo(View view) throws IOException {


        EditText et = (EditText)findViewById(R.id.searchText);
        String location = et.getText().toString();


        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(location,1);

       if (list.size()>0) {
           Address add = list.get(0);
           String locality = add.getLocality();

           double lat = add.getLatitude();
           double lng = add.getLongitude();
           whatIs = new LatLng(lat, lng);
           Intent intent2 = new Intent(getApplicationContext(), MapByLocation.class);


           startActivity(intent2);
           finish();
       }
        else{

           Toast.makeText(this, "No Place Found ", Toast.LENGTH_SHORT).show();

       }


    }

   /* @Override
    public void buttonOnClick(View view) {

        Button signupButton = (Button) findViewById(R.id.signupButton);
        if (view.getId() == R.id.logoutButton) {

            Toast.makeText(this, " You wanna logout? ", Toast.LENGTH_SHORT).show();
        }

    }*/






    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.user_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){

        if (item.getItemId()== android.R.id.home){
            finish();
        }


        if(item.getItemId() == R.id.logoutButton ) {

            ParseUser.logOut();
            Toast.makeText(this, "You are logged out", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();


        }
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        startActivity(intent);
        finish();
        return true;
    }



    static public  void getRatings(){


        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Ratings");
        query1.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {

                        for (ParseObject object : objects) {

                            String a = object.getString("coordinates");
                            Log.i("THE 1 ", object.getString("coordinates"));
                            double b = object.getDouble("averageRating");
                            Log.i("THE 2", Double.toString(object.getDouble("averageRating")));
                            ratingHash.put(a,b);
                        }

                    }
                }
            }
        });


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
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        ParseUser.logOut();
        Toast.makeText(this, "You are logged out ", Toast.LENGTH_SHORT).show();

        startActivity(intent);
        finish();




    }




}



