package com.parse.starter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.BLUE;
import static android.graphics.Color.GREEN;

public class explore extends AppCompatActivity {






    int counter = 0;
    double totalRating = 0;
    double finalRating = 0;
    int counter2= 0;
    String coor;
    String pTitle;

     double totalRatingForComments;
     double averageRatingForComments;
     int counterForComments;








   //static  Map<String, Bitmap> map = new HashMap <>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTotals();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        setTitle("AnglersMate");
        setContentView(R.layout.activity_explore);
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);


        Intent intent = getIntent();
        coor  = intent.getStringExtra("coordinates");
        Log.i("THE COOOR", coor);


        pTitle = intent.getStringExtra("placeTitle");
      //  getPictures(coor, linearLayout);

        getRatingsForComments();
        TextView htext =new TextView(explore.this);
        htext.setText(pTitle);
        htext.setTextColor(Color.BLACK);
        htext.setTypeface(null, Typeface.BOLD);
        htext.setId(5);
        htext.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        htext.setTextSize(30);
        linearLayout.addView(htext);



        getRatingsForComments();
        final TextView hrtext =new TextView(explore.this);
        hrtext.setTypeface(null, Typeface.BOLD_ITALIC);
        hrtext.setId(5);
        hrtext.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        hrtext.setTextSize(18);
        linearLayout.addView(hrtext);

        final RatingBar rb= new RatingBar(explore.this);
        Drawable drawable = rb.getProgressDrawable();
       // drawable.setColorFilter(Color.parseColor("#0064A8"), PorterDuff.Mode.SRC_ATOP);
        rb.setNumStars(5);
        rb.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        LayoutParams ratingBarParams = rb.getLayoutParams();
        rb.setEnabled(false);
        rb.setRating(Float.valueOf((float) finalRating));
        linearLayout.addView(rb);



        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Places");
        query.whereEqualTo("coordinates",coor);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e== null){
                    if (objects.size()>0){
                        for(ParseObject object: objects ){
                            counter ++;
                            totalRating = totalRating +  Float.valueOf(object.getLong("rating"));
                        }


                    }

                    finalRating = totalRating/counter;
                    rb.setRating(Float.valueOf((float) finalRating));
                    hrtext.setText("Angler's Rating: "+ String.valueOf((float) finalRating));



                }
            }
        });        ParseAnalytics.trackAppOpenedInBackground(getIntent());


        setItUp(linearLayout,coor);









    }



/*
    private void getPictures(final String coo, final LinearLayout linearLayout){


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

        setItUp(linearLayout,coor);
    }

*/




    private void setItUp(final LinearLayout linearLayout, String coo) {
        Log.i("Sould be last", String.valueOf(MapByLocation.map.size()));

        View divider = new View(explore.this);
        divider.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        LayoutParams params = divider.getLayoutParams();
        params.height = 2;
        divider.setBackgroundColor(BLACK);
        linearLayout.addView(divider);

        final Button adComment = new Button(explore.this);

        adComment.setText("Add another Review");
        adComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddComment(v);
            }
        });
        ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Places");
        query2.whereEqualTo("coordinates", coo);
        query2.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (ParseObject object : objects) {
                            counter2++;


                            TextView headingtext = new TextView(explore.this);
                            headingtext.setTypeface(null, Typeface.BOLD);
                            String op = object.getString("addedBy").substring(0, 1).toUpperCase() + object.getString("addedBy").substring(1);
                            headingtext.setText( op +"'s Trip");
                            headingtext.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                            headingtext.setTextSize(25);
                            headingtext.setTextColor(Color.BLACK);
                            linearLayout.addView(headingtext);

                            TextView headingtextd = new TextView(explore.this);
                            headingtextd.setTypeface(null, Typeface.BOLD);
                            headingtextd.setText("Rated the trip " + String.valueOf(object.getDouble("rating")) + "/5 \n");
                            headingtextd.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                            headingtextd.setTextSize(15);
                            headingtextd.setTextColor(Color.DKGRAY);
                            linearLayout.addView(headingtextd);








                            View dividere = new View(explore.this);
                            dividere.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                            LayoutParams paramse = dividere.getLayoutParams();
                            paramse.height = 2;
                            dividere.setBackgroundColor(BLACK);
                            linearLayout.addView(dividere);





                            TextView htext = new TextView(explore.this);
                            htext.setText("Catch:");
                            htext.setTypeface(null, Typeface.BOLD);
                            htext.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                            htext.setTextSize(19);
                            htext.setTextColor(Color.BLACK);
                            linearLayout.addView(htext);


                            TextView cattext = new TextView(explore.this);
                            cattext.setText(object.getString("catches"));
                            cattext.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                            cattext.setTextSize(16);
                            linearLayout.addView(cattext);


                            TextView wtext = new TextView(explore.this);
                            wtext.setText("Weather and Techniques used:");
                            wtext.setTypeface(null, Typeface.BOLD);
                            wtext.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                            wtext.setTextSize(19);
                            wtext.setTextColor(Color.BLACK);
                            linearLayout.addView(wtext);





                            TextView wetext = new TextView(explore.this);
                            wetext.setText(object.getString("weather"));
                            wetext.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                            wetext.setTextSize(16);
                            linearLayout.addView(wetext);



                            if(MapByLocation.map.containsKey(object.getString("uniqueId"))){



                                ImageView imageViewd = new ImageView(getApplicationContext());

                                imageViewd.setLayoutParams(new ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT
                                ));
                                imageViewd.setImageBitmap(MapByLocation.map.get(object.getString("uniqueId")));
                               // map.remove(map.get(object.getString("addedBy")+" "+String.valueOf(object.getCreatedAt())));



                                linearLayout.addView(imageViewd);


                                TextView qtext = new TextView(explore.this);
                                qtext.setText("                   ");
                                qtext.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                                qtext.setTextSize(20);
                                qtext.setTextColor(Color.DKGRAY);
                                linearLayout.addView(qtext);




                            }




                            if(MapByLocation.map.containsKey(object.getString("uniqueId")+"2")){




                                TextView stext = new TextView(explore.this);
                                stext.setText(" ");
                                stext.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                                stext.setTextSize(19);
                                stext.setTextColor(Color.DKGRAY);
                                linearLayout.addView(stext);

                                ImageView imageView = new ImageView(getApplicationContext());

                                imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT
                                ));

                                imageView.setImageBitmap(MapByLocation.map.get(object.getString("uniqueId")+"2"));


                                linearLayout.addView(imageView);


                            };


                            TextView owntext = new TextView(explore.this);
                            owntext.setText("Added by " + object.getString("addedBy") + ", " +String.valueOf(object.getCreatedAt()) + " \n");
                            owntext.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                            owntext.setTextSize(16);
                            linearLayout.addView(owntext);


                            View divider = new View(explore.this);
                            divider.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                            LayoutParams params = divider.getLayoutParams();
                            params.height = 2;
                            divider.setBackgroundColor(BLACK);
                            linearLayout.addView(divider);




                        }


                    }




                    adComment.setText("Add another Review");
                    linearLayout.addView(adComment);
                    //map.clear();

                }
            }
        });
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

    }


    @Override
    public boolean onOptionsItemSelected (MenuItem item){

        if (item.getItemId()== android.R.id.home){
            finish();
        }
        Dashboard.getRatings();
        Intent intent = new Intent(getApplicationContext(), MapByLocation.class);

        startActivity(intent);
        finish();
        return true;
    }

    public void AddComment(View view){

        Intent intent = new Intent(getApplicationContext(), addComment.class);
        Log.i("Sugarrrrrrr", coor);
        Log.i("Sugaaarrr3",pTitle );
        intent.putExtra("coordinates", coor);
        intent.putExtra("placeTitle", pTitle);
        intent.putExtra("averageRating", averageRatingForComments);
        intent.putExtra("totalRating", totalRatingForComments);
        intent.putExtra("counter",counterForComments);
        //intent.putExtra("coordinates", coordinatesSelected);
        //intent.putExtra("placeTitle", titleSelected);

        startActivity(intent);
        finish();
    }




    public void  getTotals(){

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Ratings");
        query.whereEqualTo("coordinates",coor);





        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e== null){
                    if (objects.size()>0) {
                        for (ParseObject object : objects) {
                            totalRatingForComments = Float.valueOf(object.getLong("totalRating"));
                            averageRatingForComments = Float.valueOf(object.getLong("averageRating"));
                            counterForComments = Integer.valueOf(object.getString("counter"));


                        }


                    }



                }
            }
        });        ParseAnalytics.trackAppOpenedInBackground(getIntent());




    }







    private void getRatingsForComments(){

        Log.i("Caaa", coor);
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Ratings");
        query.whereEqualTo("coordinates",coor);


        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e== null){
                    if (objects.size()>0){
                        for(ParseObject object: objects ){

                            counterForComments = object.getInt("counter");
                            Log.i("CAAAA", String.valueOf(object.getInt("counter")));
                            totalRatingForComments= object.getDouble("totalRating");
                            averageRatingForComments = object.getDouble("averageRating");


                        }


                    }





                    Log.i("Caa",String.valueOf(counterForComments));

                    Log.i("Caa1",String.valueOf(totalRatingForComments));
                    Log.i("Caa2",String.valueOf(averageRatingForComments));       }
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
        Dashboard.getRatings();
        Intent intent = new Intent(getApplicationContext(), MapByLocation.class);

        startActivity(intent);
        finish();




    }








}




