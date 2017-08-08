package dcogburn.hometown;

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

public class Spotify {
    String TAG = "Spotify";

    public Spotify(){

    }

    public void makeURL(AlbumInfo album){
        URL url;
        new Parse().execute("");

    }

    class Parse extends AsyncTask<AlbumInfo, Void, String> {
        private void searchAlbum(){

        }

        protected String doInBackground(AlbumInfo... albumInfos) {
            // search for url, returns xml of albums
            String token = "";
            //Log.d("DEBUG", "in doInBackground");
            try {
                searchAlbum();

                URL url = new URL("http://api.deezer.com/album/302127");
                InputStream deezer = url.openConnection().getInputStream();
                Scanner d = new Scanner(deezer).useDelimiter("\\A");
                String result = d.hasNext() ? d.next() : "";
                Log.d(TAG, result);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                //connection.setRequestProperty ("Authorization", "Basic MDIyOGY3NmZhNWQ1NGY0MmFiNzUxYjhmOTE0YzZiZTg6YTdiMDMxZDczNGFlNDIwNWE3NDNlMGMwZGQxOTZmMDg=");
                //connection.setRequestMethod("POST");

                OutputStream out = new BufferedOutputStream(connection.getOutputStream());
                //out.write("-d grant_type=client_credentials ".getBytes());

                connection.connect();
                Log.d(TAG, connection.getResponseMessage());

                InputStream err = new BufferedInputStream(connection.getErrorStream());
                Scanner e = new Scanner(err).useDelimiter("\\A");
                String error = e.hasNext() ? e.next() : "";
                Log.d(TAG, error);


                InputStream in = new BufferedInputStream(connection.getInputStream());
                Scanner s = new Scanner(in).useDelimiter("\\A");
                //String result = s.hasNext() ? s.next() : "";
                //Log.d(TAG, result);
                //connection.disconnect();
                //InputStream input = new URL(urls[0]).openStream();
                //XMLParser xmlparser = new XMLParser();
                //token = xmlparser.parseSpotifyToken(input);
                //Log.d("DEBUG", "out of XMLParser");
            } catch (IOException e) {
                e.printStackTrace();
            } //catch (XmlPullParserException e) {
//                e.printStackTrace();
//            }
            return token;
        }



//        @Override
//        protected void onPostExecute(ArrayList<AlbumEntry> list) {
//            xmlResponse = list;
//        }

    }
}
