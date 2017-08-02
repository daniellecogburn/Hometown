package dcogburn.hometown;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class ShuffleArtists extends AppCompatActivity {

    // Various text display
    private TextView mArtist;
    private TextView mAlbum;
    private ImageView mArt;

    int MY_PERMISSIONS = 0;

    private ArrayList<AlbumInfo> albumQueue;
    private final String[] cityNames = {"austin", "dallas", "denton", "el paso", "houston", "lubbock", "san antonio"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shuffle_artists);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mArtist = (TextView) findViewById(R.id.artist);
        mAlbum = (TextView) findViewById(R.id.album);

        final String city = getClosestCity();

        albumQueue = new ArrayList<>();

        for(int i = 0; i < 2; i ++) {
            try {
                albumQueue.add(generateAlbum(city));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        displayAlbum();

        Button button = (Button) findViewById(R.id.generatealbum);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    displayAlbum();
                    Log.d("DEBUG", " between displayalbum and generate album");
                    generateAlbum(city);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void displayAlbum() {
        AlbumInfo album = albumQueue.remove(0);
        mArtist.setText(album.getArtistName());
        mAlbum.setText(album.getAlbumName());
        // TODO : THIS CODE TAKES FOREVER
        // make it better
        new AlbumURL().execute(album.getAlbumArt());
    }

    public AlbumInfo generateAlbum(String city) throws IOException {
        AlbumGenerator gen = new AlbumGenerator();
        AlbumInfo album = null;
        InputStream is = null;

        switch (city) {
            case "austin":
                is = getResources().openRawResource(R.raw.austin);
                break;
            case "dallas":
                is = getResources().openRawResource(R.raw.dallas);
                break;
            case "denton":
                is = getResources().openRawResource(R.raw.denton);
                break;
            case "el paso":
                is = getResources().openRawResource(R.raw.el_paso);
                break;
            case "fort worth":
                is = getResources().openRawResource(R.raw.fort_worth);
                break;
            case "houston":
                is = getResources().openRawResource(R.raw.houston);
                break;
            case "lubbock":
                is = getResources().openRawResource(R.raw.lubbock);
                break;
        }

        try {
             album = gen.generateAlbum("city", new Scanner(is));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d("ALBUM", "artist: " + album.getArtistName());
        Log.d("ALBUM", "album: " + album.getAlbumName());
        Log.d("ALBUM", "art: " + album.getAlbumArt());

        albumQueue.add(album);

        return album;

    }

    private String getClosestCity(){
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                //makeUseOfNewLocation(location);
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
            public void onProviderEnabled(String provider) {
            }
            public void onProviderDisabled(String provider) {
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION }, MY_PERMISSIONS);
            return null;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        Log.d("location", lastKnownLocation.toString());

        double userLat = Math.abs(lastKnownLocation.getLatitude());
        double userLong = Math.abs(lastKnownLocation.getLongitude());
        Log.d("userlat", String.valueOf(userLat));
        Log.d("userlong", String.valueOf(userLong));

        HashMap<String, Double> latMap = new HashMap();
        latMap.put("austin", 30.2672);
        latMap.put("dallas", 32.7767);
        latMap.put("denton", 33.2148);
        latMap.put("el paso", 31.7619);
        latMap.put("houston", 29.7604);
        latMap.put("lubbock", 33.5779);
        latMap.put("san antonio", 29.4241);

        HashMap<String, Double> longMap = new HashMap();
        longMap.put("austin", 97.7431);
        longMap.put("dallas", 96.7970);
        longMap.put("denton", 97.1331);
        longMap.put("el paso", 106.4850);
        longMap.put("houston", 95.3698);
        longMap.put("lubbock", 101.8552);
        longMap.put("san antonio", 98.4936);


        double shortestDist = 1000;
        String closestCity = "";
        double dist = 0;
        for (int i = 0; i < cityNames.length; i++){
            double cityLat = Math.abs(latMap.get(cityNames[i]));
            double cityLong = Math.abs(longMap.get(cityNames[i]));
            dist = Math.sqrt((Math.pow(cityLat - userLat,2) + Math.pow(cityLong-userLong, 2)));
            Log.d(cityNames[i], String.valueOf(dist));
            if (shortestDist > dist){
                shortestDist = dist;
                closestCity = cityNames[i];
            }
        }
        if (dist > 8){
            closestCity = "none";
        }
        Log.d("city: ", closestCity);
        return closestCity;
    }

    class AlbumURL extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... image) {
            // search for url, returns xml of albums
            URL url = null;
            try {
                url = new URL(image[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            Bitmap bmp = null;
            try {
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap bmp) {
            ImageView image = (ImageView) findViewById(R.id.art);
            image.setImageBitmap(bmp);
        }
    }

}
