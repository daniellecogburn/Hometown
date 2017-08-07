package dcogburn.hometown;

import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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

public class Favorites extends AppCompatActivity {
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

        //hardcoded
        favoriteAlbumList = new ArrayList<>();
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                Log.d(TAG, dataSnapshot.getKey());
                favoriteAlbumList.add(dataSnapshot.getValue(AlbumInfo.class));
                initializeGrid();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } ;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        mDatabase.child("users").child(uid).child("favorites").addChildEventListener(childEventListener);
        initializeGrid();
    }

    private void initializeGrid(){
        albumGridView = (GridView) findViewById(R.id.album_grid);
        albumGridView.setAdapter(new ImageAdapter(this));
        albumGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Log.d(TAG, "clicked");
                showDialog(favoriteAlbumList.get(position));
            }
        });
    }

    private void showDialog(AlbumInfo albumInfo){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(albumInfo.getAlbumName() + "\nRating:" + albumInfo.getRating())
                .setTitle(albumInfo.getArtistName());
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public class ImageAdapter extends BaseAdapter {   // copied from https://developer.android.com/guide/topics/ui/layout/gridview.html and adapted for use here
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            //Log.d(TAG, "Lenght in ImageAdapter "+ favoriteAlbumList.size());
            //return 8;
            return favoriteAlbumList.size();
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
                imageView.setLayoutParams(new GridLayout.LayoutParams(GridLayout.spec(85),GridLayout.spec(85)));
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setMaxHeight(45);
            } else {
                imageView = (ImageView) convertView;
            }
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            int width = metrics.widthPixels;
            String urlStr = favoriteAlbumList.get(position).getAlbumArt();
            //urlStr = "http://www.billboard.com/files/styles/900_wide/public/media/Joy-Division-Unknown-Pleasures-album-covers-billboard-1000x1000.jpg";
            URL url = null;
                // hardcoded for testing
                AlbumURL albumUrl = new AlbumURL();
            Bitmap bmp = null;
            try {
                bmp = albumUrl.execute(urlStr).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(bmp);
            imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, width/3, width/3, false));
            return imageView;
        }

        // references to our images

    }

    class AlbumURL extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... urls) {
            // search for url, returns xml of albums
            URL url = null;
            try {
                url = new URL(urls[0]);
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
    }
}
