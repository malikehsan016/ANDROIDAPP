package com.parse.starter;

import android.*;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Geocoder;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.media.Rating;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.*;


import com.google.android.gms.instantapps.PackageManagerWrapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.text.Text;
import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import static com.parse.starter.MapByLocation.places;
import static com.parse.starter.R.id.ratingBar;

public class informationBox extends AppCompatActivity implements View.OnClickListener {

    ParseObject object = new ParseObject("Places");
    ParseObject obj = new ParseObject("Ratings");
    ParseObject picObj = new ParseObject("Pictures");
    ParseObject picObj2 = new ParseObject("Pictures");
    ParseFile file,file1,file2,file3;
    int f,f1,f2,f3 = 0;
    float stars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_box);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setTitle("AnglersMate");

        showList();
        listenerForRating();



    }

    public void getPhoto(){
        Intent intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent1,1);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getPhoto();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void choosePic(View view){

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) !=  PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1) ;

        }else{
            getPhoto();
        }

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();

            //convert to bitmap image
            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte [] byteArray = stream.toByteArray();
                if (f1 == 1) {

                    file2 = new ParseFile("image.png",byteArray);
                    Button aa = (Button) findViewById(R.id.button5);
                    aa.setText("2 Pictures selected");
                    aa.setEnabled(false);

                }

                else {


                    file = new ParseFile("image.png", byteArray);


                    Button aa = (Button) findViewById(R.id.button5);
                    aa.setText("1 Picture selected, Add more");
                    f1 = 1;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private  void showList(){
        final ListView placesList = (ListView) findViewById(R.id.listOfPlaces);
        TextView headingList = (TextView) findViewById(R.id.textView7);

        if (places.size() < 1){

            headingList.setText("No suggestions found by Wiki Places");
        }
        else{

           headingList.setText( places.size() + " place(s) suggested by Wiki Places") ;
        }
        for (int i = 0 ; i< places.size(); i++){

            Log.i("______________", places.get(i));

        }
        placesList.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
        String[] bar = places.toArray(new String[places.size()]);
        ArrayAdapter <String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, bar);
        placesList.setAdapter(arrayAdapter);
        Log.i("dddddddddd SIZE", String.valueOf(places.size()));
        places.clear();
        bar = null;

        placesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String selectedFromList = (String) (placesList.getItemAtPosition(position));
                    Log.i("THE SELECTED ITME IS", selectedFromList);
                EditText nameGiven = (EditText) findViewById(R.id.editText2);
                nameGiven.setText(selectedFromList);


            }});
    }


    public void addPlace(View view){
        EditText headingList = (EditText) findViewById(R.id.editText2);
        EditText catchesBox = (EditText) findViewById(R.id.editText6);
        EditText weatherBox = (EditText) findViewById(R.id.editText7);
        String name = headingList.getText().toString();
        String catches = catchesBox.getText().toString();
        String weather = weatherBox.getText().toString();

        Log.i("The Name of the place", name);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());


        Intent in = getIntent();
        Bundle bundle = in.getExtras();

        String givenLat = Double.toString(bundle.getDouble("lati"));
        String givenLon = Double.toString(bundle.getDouble("longi"));

        Log.i("The latitude is", givenLat);
        Log.i("The longitude is",  givenLon);

        //Log.i("THE CURRENT USER IS", currentUserName);
        object.put("placeName", name);
        object.put("catches", catches);
        object.put("weather", weather);
        object.put("longitude",givenLon);
        object.put("latitude", givenLat);
        String co = givenLat+" "+givenLon;
        object.put("uniqueId",ParseUser.getCurrentUser().getUsername()+" "+timeStamp  );
        object.put("coordinates", co);
        object.put("addedBy",ParseUser.getCurrentUser().getUsername());


        object.put("rating",stars);

        if (file != null){
            picObj.put("pictures",file);
            picObj.put("uniqueId",ParseUser.getCurrentUser().getUsername()+" "+timeStamp  );
            picObj.put("coordinates", co);

            picObj.saveInBackground();



        }

        if (f1 == 1 && file2 != null ){

            picObj2.put("pictures",file2);
            picObj2.put("uniqueId",ParseUser.getCurrentUser().getUsername()+" "+timeStamp+"2"  );
            picObj2.put("coordinates", co);
            picObj2.saveInBackground();
        }



        obj.put("coordinates", co);
        Dashboard.ratingHash.put(co, Double.parseDouble(Float.toString(stars)));
        Log.i("THE STARS ", Double.toString(Double.parseDouble(Float.toString(stars))));
        obj.put("counter",1);
        obj.put("averageRating",stars);
        obj.put("totalRating", stars);
        obj.saveInBackground();
        object.saveInBackground();

        Dashboard.getRatings();
        goBackToMap();


    }
















    @Override
    public void onClick(View view) {

    }




    public void listenerForRating(){


        RatingBar rb = (RatingBar) findViewById(R.id.ratingBar);
        rb.setOnRatingBarChangeListener(
                new RatingBar.OnRatingBarChangeListener(){


                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                     stars = v;
                    }
                });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.user_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }


    @Override
    public boolean onOptionsItemSelected (MenuItem item){

        if (item.getItemId()== android.R.id.home){
            Intent intent = new Intent(getApplicationContext(), MapByLocation.class);

            startActivity(intent);
            finish();

        }



        if(item.getItemId() == R.id.logoutButton ) {

            ParseUser.logOut();
            Toast.makeText(this, "You are logged out", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();


        }

        return true;
    }

    private void goBackToMap(){

        Intent intent = new Intent(getApplicationContext(), MapByLocation.class);
        startActivity(intent);
        finish();

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
        Dashboard.getRatings();
        Intent intent = new Intent(getApplicationContext(), MapByLocation.class);

        startActivity(intent);
        finish();




    }




}








