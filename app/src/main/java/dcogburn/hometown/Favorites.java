package dcogburn.hometown;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import static dcogburn.hometown.R.id.imageView;

// Danielle's responsibility to make this activity

public class Favorites extends Drawer {
    private GridView albumGridView;
    public ArrayList<AlbumInfo> favoriteAlbumList;
    private String TAG = "Favorites";
    private static Context context;
    String city = "austin";
    UserInformation userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userInfo = new UserInformation();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initializeGrid();
    }

    private void doDatabase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dReference = database.getReference();

        Query query = dReference.child("noIdea").equalTo(true==true){

        }
    }

    private void initializeGrid(){
        albumGridView = (GridView) findViewById(R.id.album_grid);
        albumGridView.setAdapter(new ImageAdapter(this));
        setGridViewListener();
    }

    public class ImageAdapter extends BaseAdapter {   // copied from https://developer.android.com/guide/topics/ui/layout/gridview.html and adapted for use here
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return 1; //hardcoded for testing
            //return favoriteAlbumList.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                //imageView.setLayoutParams(new GridView.LayoutParams(500,500));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            URL url = null;
                // hardcoded for testing
                AlbumURL albumUrl = new AlbumURL();
                //url = new URL(favoriteAlbumList.get(position).getAlbumArt());
            Bitmap bmp = null;
            try {
                bmp = albumUrl.execute("").get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(bmp);
            return imageView;
        }

        // references to our images

    }

    class AlbumURL extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... urls) {
            // search for url, returns xml of albums
            URL url = null;
            try {
                url = new URL("http://www.fuse.tv/image/56fe73a1e05e186b2000009b/768/512/the-boxer-rebellion-ocean-by-ocean-album-cover-full-size.jpg");
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
/*
        @Override
        protected void onPostExecute(ArrayList<AlbumEntry> list) {
            xmlResponse = list;
        }
*/
    }

    private void setGridViewListener() {
        albumGridView.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        Log.d(TAG, "id of selected item: " + position);
                        Intent intentFavorites = new Intent(context, Favorites.class);
                        intentFavorites.putExtra("test", id);
                        startActivity(intentFavorites);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // nothing to do
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
