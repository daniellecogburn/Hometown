package dcogburn.hometown;

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

import java.io.FileNotFoundException;

public class Favorites extends Drawer {

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
            public void onClick(View v) {
                AlbumGenerator gen = new AlbumGenerator();
                AlbumInfo album = null;
                try {
                    album = gen.generateAlbum("austin");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Log.d("ALBUM", "artist: " + album.getArtistName());
                Log.d("ALBUM", "album: " + album.getAlbumName());
                Log.d("ALBUM", "art: " + album.getAlbumArt());
            }
        });
    }

    public void testGenerator () throws FileNotFoundException {

        AlbumGenerator gen = new AlbumGenerator();
        AlbumInfo album = gen.generateAlbum("austin");
        Log.d("ALBUM", "artist: " + album.getArtistName());
        Log.d("ALBUM", "album: " + album.getAlbumName());
        Log.d("ALBUM", "art: " + album.getAlbumArt());
    }

}
