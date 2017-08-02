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
    private String city;

    int MY_PERMISSIONS = 0;

    private ArrayList<AlbumInfo> albumQueue;
    private final String[] cityNames = {"ustin", "dallas", "denton", "el paso", "houston", "lubbock", "san antonio"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        city = intent.getStringExtra("city");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shuffle_artists);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(city);
        setSupportActionBar(toolbar);

        mArtist = (TextView) findViewById(R.id.artist);
        mAlbum = (TextView) findViewById(R.id.album);

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

    @Override
    protected void onResume(){
        super.onResume();
        Intent intent = getIntent();
        city = intent.getStringExtra("city");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(city);
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

        switch (city.toLowerCase()) {
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
