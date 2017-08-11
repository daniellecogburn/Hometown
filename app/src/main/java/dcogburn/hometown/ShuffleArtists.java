package dcogburn.hometown;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class ShuffleArtists extends AppCompatActivity implements RateFavoriteDialog.NoticeDialogListener  {

    static Context context;
    String TAG = "ShuffleArtists";

    // Various display
    private TextView mArtist;
    private TextView mAlbum;
    private String city;
    private ImageView mImage;

    private static RatingBar ratingBar;

    // Firebase
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    // Buttons
    ImageButton generateAlbum;
    ImageButton favoriteAlbum;
    ImageButton saveAlbum;
    private boolean saveAlbumPressed = false;
    ImageButton playButton;

    // album info
    AlbumInfo thisAlbum;
    private ArrayList<AlbumInfo> albumQueue = new ArrayList<>();

    // media player
    MediaPlayer mp;
    int mpPosition;
    AlbumInfo currentPlayingAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        city = intent.getStringExtra("city");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shuffle_artists);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(city);
        ShuffleArtists.context = getApplicationContext();
        setSupportActionBar(toolbar);
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);

        // firebase authentication
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // album details listed on screen
        mArtist = (TextView) findViewById(R.id.artist);
        mAlbum = (TextView) findViewById(R.id.album);
        mImage = (ImageView) findViewById(R.id.art);

        // media player globals
        mp = new MediaPlayer();
        currentPlayingAlbum = null;

        // initial album
        try {
            generateAlbum();
            displayAlbum();
            playSong(null);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        // initialize buttons
        createButtons();

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mpPosition = 0;
                //playButton.setImageResource(R.drawable.ic_action_play);
            }
        });

        playButton.setImageResource(R.drawable.ic_action_pause);

    }

    @Override
    protected void onPause(){
        super.onPause();
        mp.stop();
    }

    @Override
    protected void onResume(){
        super.onResume();
        Intent intent = getIntent();
        city = intent.getStringExtra("city");
        mp.pause();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(city);
    }

    @Override
    public void onDialogPositiveClick(float rating) {
        // User touched the dialog's positive button
        thisAlbum.setRating((int) rating);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        assert user != null;
        String uid = user.getUid();
        Log.d("SHUFFLE", uid);
        Toast.makeText(ShuffleArtists.context, "Album saved to favorites", Toast.LENGTH_SHORT).show();
        databaseReference.child("users").child(uid).child("favorites").child(thisAlbum.getAlbumName() + " - " + thisAlbum.getArtistName()).setValue(thisAlbum);
        favoriteAlbum.setImageResource(R.drawable.ic_action_favorite_pressed);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button

    }

    public void createButtons(){

        // new album button
        generateAlbum = (ImageButton) findViewById(R.id.generatealbum);
        generateAlbum.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                saveAlbumPressed = false;

                // reset buttons
                saveAlbum.setImageResource(R.drawable.ic_action_save);
                favoriteAlbum.setImageResource(R.drawable.ic_action_favorite);
                try {
                    mImage.setImageResource(android.R.color.transparent);
                    generateAlbum();
                    displayAlbum();
                    playSong(null);

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                playButton.setImageResource(R.drawable.ic_action_pause);
            }
        });

        // favorite album button
        favoriteAlbum = (ImageButton) findViewById(R.id.favoriteAlbum);
        favoriteAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                DialogFragment rfg = new RateFavoriteDialog();
                rfg.show(ft, "dialog");

            }
        });

        // save album button
        saveAlbum = (ImageButton) findViewById(R.id.saveAlbum);
        saveAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlbumInfo album = thisAlbum;

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                assert user != null;
                String uid = user.getUid();
                Log.d("SHUFFLE", uid);

                if(!saveAlbumPressed) {
                    saveAlbum.setImageResource(R.drawable.ic_action_save_pressed);
                    databaseReference.child("users").child(uid).child("save").child(album.getAlbumName() + " - " + album.getArtistName()).setValue(album);
                } else {
                    saveAlbum.setImageResource(R.drawable.ic_action_save);
                    databaseReference.child("users").child(uid).child("save").child(album.getAlbumName() + " - " + album.getArtistName()).removeValue();
                }

                saveAlbumPressed = !saveAlbumPressed;

            }
        });

        // play song button
        playButton = (ImageButton) findViewById(R.id.playSong);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mp.isPlaying()) {
                    playButton.setImageResource(R.drawable.ic_action_pause);
                } else {
                    playButton.setImageResource(R.drawable.ic_action_play);
                }
                playSong(view);
            }
        });
    }

    public void playSong(View view){
        Log.d(TAG, "0");
        if (currentPlayingAlbum == null){
            Log.d(TAG, "1");
            Parse parse = new Parse();
            parse.execute(thisAlbum);
            currentPlayingAlbum = thisAlbum;
        }
        else if (currentPlayingAlbum.equals(thisAlbum)){
            if (mp.isPlaying()) {
                Log.d(TAG, "3");
                mpPosition = mp.getCurrentPosition();
                mp.pause();
            } else if (mpPosition == 0) {
                Log.d(TAG, "4");
                Parse parse = new Parse();
                parse.execute(thisAlbum);
                currentPlayingAlbum = thisAlbum;
            } else {
                Log.d(TAG, "5");
                mp.seekTo(mpPosition);
                mp.start();
            }
        }
        else {
            Log.d(TAG, "2");
            mp.pause();
            Parse parse = new Parse();
            parse.execute(thisAlbum);
            currentPlayingAlbum = thisAlbum;
        }
        Log.d(TAG, currentPlayingAlbum.getAlbumName()+ " "+ thisAlbum.getAlbumName());
    }

    // display album on page
    public void displayAlbum() {
        thisAlbum = albumQueue.remove(0);
        mArtist.setText(thisAlbum.getArtistName());
        mAlbum.setText(thisAlbum.getAlbumName());
        new AlbumURL().execute(thisAlbum.getAlbumArt());
    }

    // generate new AlbumInfo
    public void generateAlbum() throws IOException, InterruptedException {
        AlbumGenerator gen = new AlbumGenerator();
        AlbumInfo album = null;
        InputStream is = null;

        // get text file of artists
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
            case "san antonio":
                is = getResources().openRawResource(R.raw.san_antonio);
                break;
            case "new york":
                is = getResources().openRawResource(R.raw.new_york);
                break;
            case "los angeles":
                is = getResources().openRawResource(R.raw.los_angeles);
                break;
            case "seattle":
                is = getResources().openRawResource(R.raw.seattle);
                break;
            case "nashville":
                is = getResources().openRawResource(R.raw.nashville);
                break;
        }

        // generate album
        try {
            album = gen.generateAlbum(city, new Scanner(is));
            albumQueue.add(album);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.d("ALBUM", "artist: " + album.getArtistName());
        Log.d("ALBUM", "album: " + album.getAlbumName());
        Log.d("ALBUM", "art: " + album.getAlbumArt());
    }

    // image request
    class AlbumURL extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... image) {
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
            mImage.setImageBitmap(bmp);
        }
    }

    class Parse extends AsyncTask<AlbumInfo, Void, String> {

        public String c;
        private String getPreviewUrl(AlbumInfo album){
            Log.d(TAG, "in searchalbums");
            Log.d(TAG, album.getArtistName());
            try {
                URL search = new URL("http://api.deezer.com/search?q=artist:\""+album.getArtistName().toLowerCase() + "\" album:\"" + album.getAlbumName().toLowerCase()+ "\" &output=xml");
                InputStream s = search.openConnection().getInputStream();
                DeezerXMLParser parser = new DeezerXMLParser();
                URL url = parser.parse(s);
                if (url != null) {
                    Log.d(TAG, "found track by album " + thisAlbum.getArtistName());
                    String clip = url.toString();
                    return clip;
                } else {
                    Log.d(TAG, "artist" + album.getArtistName());
                    search = new URL("http://api.deezer.com/search?q=artist:\"" + album.getArtistName().toLowerCase() + "\"&output=xml");
                    s = search.openConnection().getInputStream();
                    parser = new DeezerXMLParser();
                    url = parser.parse(s);
                    if (url != null) {
                        Log.d(TAG, "found track by artist: "+ thisAlbum.getArtistName());
                        return url.toString();
                    }
                }
            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected String doInBackground(AlbumInfo... albumInfos) {
            // search for url, returns xml of albums
            return getPreviewUrl(albumInfos[0]);
        }

        protected void onPostExecute(String result){
            if (result == null){
                mp.reset();
                Toast.makeText(ShuffleArtists.context, "Album cannot be found. Please try next album.", Toast.LENGTH_SHORT).show();
                playButton.setImageResource(R.drawable.ic_action_play);
                return;
            }
            try {
                mp.reset();
                mp.setDataSource(result);
                mp.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mp.start();
        }
    }

}

