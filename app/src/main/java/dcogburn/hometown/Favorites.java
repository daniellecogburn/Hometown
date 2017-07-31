package dcogburn.hometown;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import static dcogburn.hometown.R.id.imageView;

public class Favorites extends Drawer {
    
    String city = "austin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    generateAlbum(view);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public AlbumInfo generateAlbum(View v) throws IOException {
        AlbumGenerator gen = new AlbumGenerator();
        AlbumInfo album = null;

        // TODO : hard-coded Austin
        Scanner sFile = new Scanner(getResources().openRawResource(R.raw.austin));

        try {
            album = gen.generateAlbum("austin", sFile);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d("ALBUM", "artist: " + album.getArtistName());
        Log.d("ALBUM", "album: " + album.getAlbumName());
        Log.d("ALBUM", "art: " + album.getAlbumArt());

//        URL url = new URL(album.getAlbumArt());
//        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//        ImageView tv1 = (ImageView) findViewById(R.id.albumArt);
//        tv1.setImageBitmap(bmp);

        return album;


    }

}
