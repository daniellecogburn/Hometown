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
import java.util.Timer;

import javax.xml.transform.Result;

/**
 * Created by danielle on 8/3/17.
 */

public class Play {
    String TAG = "Play";

    public Play(){

    }

    public String makeURL(AlbumInfo album){
        URL url;
        Parse parse = new Parse();
        parse.execute(album);
        //parse.onPostExecute();
        return parse.c;

    }

    class Parse extends AsyncTask<AlbumInfo, Void, String> {
        public String c;
        private String getPreviewUrl(AlbumInfo album) {
            Log.d(TAG, "in searchalbums");
            Log.d(TAG, album.getArtistName());
            try {
                URL search = new URL("http://api.deezer.com/search?q=artist:\"" + album.getArtistName().toLowerCase() + "\" album:\"" + album.getAlbumName().toLowerCase() + "\" &output=xml");
                Log.d(TAG, search.toString());
                InputStream s = search.openConnection().getInputStream();
                DeezerXMLParser parser = new DeezerXMLParser();
                URL url = parser.parse(s);
                if (url != null) {
                    String clip = url.toString();
                    return clip;
                } else {
                    search = new URL("http://api.deezer.com/search?q=album:\"" + album.getArtistName().toLowerCase() + "&output=xml");
                    s = search.openConnection().getInputStream();
                    parser = new DeezerXMLParser();
                    url = parser.parse(s);
                    if (url != null) {
                        return url.toString();
                    }
                }
                return null;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected String doInBackground(AlbumInfo... albumInfos) {
            // search for url, returns xml of albums
            String clip = getPreviewUrl(albumInfos[0]);
            return clip;
        }

        protected void onPostExecute(String result){
        }
    }

}
