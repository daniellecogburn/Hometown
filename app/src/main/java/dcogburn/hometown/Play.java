package dcogburn.hometown;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by danielle on 8/3/17.
 */

public class Play {
    String TAG = "Play";

    public Play(){

    }

    public void makeURL(AlbumInfo album){
        URL url;
        new Parse().execute(album);

    }

    class Parse extends AsyncTask<AlbumInfo, Void, String> {
        private void searchAlbum(AlbumInfo album){
            Log.d(TAG, "in searchalbums");
            Log.d(TAG, album.getArtistName());
            try {
                URL search = new URL("http://api.deezer.com/search?q=artist:\""+album.getArtistName().toLowerCase() + "\" album:\"" + album.getAlbumName().toLowerCase()+ "\" &output=xml");
                Log.d(TAG, search.toString());
                InputStream s = search.openConnection().getInputStream();
                DeezerXMLParser parser = new DeezerXMLParser();
                URL clip = parser.parse(s);
                MediaPlayer mp = new MediaPlayer();
                mp.setDataSource(clip.toString());
                mp.prepare();
                mp.start();
                //Log.d(TAG, result);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }

        protected String doInBackground(AlbumInfo... albumInfos) {
            // search for url, returns xml of albums
            String token = "";
            searchAlbum(albumInfos[0]);
            return token;
        }

    }

}
