package com.parse.starter;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.R.attr.data;
import static android.R.attr.wallpaperOpenEnterAnimation;

public class addComment extends AppCompatActivity implements View.OnClickListener{
    ParseObject object = new ParseObject("Places");
    ParseObject picObj  = new ParseObject("Pictures");
    ParseObject picObj2  = new ParseObject("Pictures");

    //   ParseObject obj = new ParseObject("Ratings");
    ParseFile file,file1,file2,file3;
    int fi,fi1,fi2,fi3 = 0;
    float stars;
    String a,b;
     double totalRating;
     double averageRating;
     int counter;
    boolean wait = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);

        Intent in = getIntent();
        a = in.getStringExtra("coordinates");
        b = in.getStringExtra("placeTitle");
        totalRating = in.getExtras().getDouble("totalRating");
        averageRating = in.getExtras().getDouble("averageRating");
        counter = in.getExtras().getInt("counter");








        Log.i("Canada",String.valueOf(this.counter));
        Log.i("Canada1",String.valueOf(this.totalRating));
        Log.i("Canada2",String.valueOf(this.averageRating));




        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setTitle("AnglersMate");

      //  showList();
        listenerForRating();




        TextView tv = (TextView) findViewById(R.id.textView);
        tv.setTextSize(25);
        tv.setTypeface(tv.getTypeface(), Typeface.BOLD);

        tv.setText("Write a review for: "+ b);


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
    public void choosePic(View view) {

        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        } else {
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
                    if (fi1 == 1) {

                        file2 = new ParseFile("image.png",byteArray);
                        Button aa = (Button) findViewById(R.id.button5);
                        aa.setText("2 Pictures selected");
                        aa.setEnabled(false);

                    }

                    else {


                        file = new ParseFile("image.png", byteArray);
                        fi1 = 1;

                        Button aa = (Button) findViewById(R.id.button5);
                        aa.setText("1 Picture selected, Add more");
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }




    public void addPlace(View view) throws ParseException {
        EditText headingList = (EditText) findViewById(R.id.editText2);
        EditText catchesBox = (EditText) findViewById(R.id.editText6);
        EditText weatherBox = (EditText) findViewById(R.id.editText7);
        // String name = headingList.getText().toString();
        String catches = catchesBox.getText().toString();
        String weather = weatherBox.getText().toString();

        Log.i("The Name of the place", b);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());


        Intent in = getIntent();
        Bundle bundle = in.getExtras();


        object.put("placeName", b);
        object.put("catches", catches);
        object.put("weather", weather);
        object.put("longitude", "");
        object.put("latitude", "");
        object.put("coordinates", a);
        object.put("addedBy", ParseUser.getCurrentUser().getUsername());
        object.put("uniqueId",ParseUser.getCurrentUser().getUsername()+" "+timeStamp  );


        object.put("rating", stars);

        if (file != null) {
            picObj.put("pictures", file);
            picObj.put("uniqueId", ParseUser.getCurrentUser().getUsername()+" "+timeStamp );
            picObj.put("coordinates", a);
            picObj.saveInBackground();

        }

        if (file2 != null) {

            picObj2.put("pictures", file2);
            picObj2.put("uniqueId", ParseUser.getCurrentUser().getUsername()+" "+timeStamp+"2" );
            picObj2.put("coordinates", a);
            picObj2.saveInBackground();

        }


        object.saveInBackground();
        updateRatings();
      //  Dashboard.ratingHash.put(a, Double.parseDouble(Float.toString(stars)));
        Dashboard.getRatings();
        goBackToMap();
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
    public void onClick(View view) {

    }







    private void updateRatings(){


        counter = counter +1 ;
        totalRating = totalRating + stars;
        averageRating = totalRating/counter;


Log.i("Pak", String.valueOf(counter));
        Log.i("Pak1", String.valueOf(averageRating));
        Log.i("Pak2", String.valueOf(totalRating));
        Log.i("pak3", a);



        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Ratings");
        query.whereEqualTo("coordinates",a);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e== null){
                    if (objects.size()>0){
                        for(ParseObject object: objects ){

                            object.put("counter",counter);
                            object.put("totalRating",totalRating);
                            object.put("averageRating", averageRating);
                            object.saveInBackground();



                        }


                    }





                }
            }
        });        ParseAnalytics.trackAppOpenedInBackground(getIntent());


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
        Intent intent = new Intent(getApplicationContext(), explore.class);
        intent.putExtra("coordinates", a);
        intent.putExtra("placeTitle", b);
        startActivity(intent);
        finish();


    }
}




