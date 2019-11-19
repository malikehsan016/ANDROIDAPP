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
        import android.view.MotionEvent;
        import android.view.View;
        import android.widget.ArrayAdapter;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.lang.reflect.Array;
        import java.util.*;


        import com.google.android.gms.instantapps.PackageManagerWrapper;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.MarkerOptions;
        import com.google.android.gms.vision.text.Text;

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

public class placeSuggestions extends AppCompatActivity {

        ListView placesList = (ListView) findViewById(R.id.listOfPlaces);

        @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
                setTitle("AnglersMate");

                setContentView(R.layout.activity_place_suggestions);

            showList();



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

                ArrayList<Bitmap> uploadedImages = new ArrayList<Bitmap>();


                if(requestCode == 1 && resultCode == RESULT_OK && data != null){
                        Uri selectedImage = data.getData();
                        
                        //convert to bitmap image
                        try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                                uploadedImages.add(bitmap);
                                Log.i("))))))))))))))))))))", Integer.toString(uploadedImages.size()));
                                bitmap.recycle();
                                bitmap= null;
                                
                        } catch (IOException e) {
                                e.printStackTrace();
                        }

                }
        }

        private  void showList(){
                TextView headingList = (TextView) findViewById(R.id.count);

                if (places.size() < 1){

                        headingList.setText("No suggestions found from Wikipedia");
                }
                else{

                        headingList.setText("Wikipedia found " + places.size() + " place(s) nearby") ;
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



        }







}
