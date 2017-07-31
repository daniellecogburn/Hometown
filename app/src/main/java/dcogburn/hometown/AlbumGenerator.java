package dcogburn.hometown;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

/**
 * Created by Benjamin on 7/28/2017.
 * Generates albums based on location.
 * uses last.fm web api
 */

public class AlbumGenerator {

    // random number generator
    private Random rand;

    private ArrayList<AlbumEntry> xmlResponse;

    public AlbumGenerator() {
        rand = new Random();
    }

    public AlbumInfo generateAlbum(String city) throws FileNotFoundException {

        // get artist list
        String[] artistList = getArtistList(city);
        String artist = null;

        // generate random album and info
        AlbumEntry album = null;
        //do {
            if(artistList != null) {
                artist = chooseRandomArtist(artistList);
                try {
                    album = getRandomAlbum(getAlbumList(artist));
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        //} while (albumInfo exists in any list); // TODO: search user's lists for album info

        // return album
        return new AlbumInfo(album.name, artist, album.imageLink, city);
    }

    private AlbumEntry getRandomAlbum(ArrayList<AlbumEntry> albumList) {
        int index;

        if (albumList == null) {
            index = 0; // error
            Log.d("ERROR", "Album list null");
        } else {
            index = new Random().nextInt(albumList.size());
        }

        return albumList.get(index);
    }

    // get artist array from city text file
    private String[] getArtistList(String city) throws FileNotFoundException {

        ArrayList<String> artistList = new ArrayList<String>();

        // TODO: how to actually find file ????
        // Use firebase? need to figure this out.
        // for now, use test array.

//        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/assets/";
//        File file = new File(path + city + ".txt");
//
//        // scan file
//        String token = "";
//        Scanner inFile = new Scanner(file).useDelimiter(",\\s*");

//        while (inFile.hasNext()) {
//            // find next line
//            token = inFile.next();
//            System.out.print(token);
//            artistList.add(token);
//        }
//        inFile.close();


        
        // TEST CODE

        artistList.add("...And You Will Know Us by the Trail of Dead");
        artistList.add("Arc Angels");
        artistList.add("Asleep at the Wheel");
        artistList.add("Asylum Street Spankers");
        artistList.add("At All Cost");
        artistList.add("Austin Lounge Lizards");
        artistList.add("Averse Sefira");
        artistList.add("Bad Livers");
        artistList.add("Balmorhea");

        return artistList.toArray(new String[0]);

    }

    private String chooseRandomArtist(String[] artistList) {
        int index = new Random().nextInt(artistList.length);
        return artistList[index];
    }

    private ArrayList<AlbumEntry> getAlbumList(String artist) throws ExecutionException, InterruptedException {

        // build URL
        StringBuilder sb = new StringBuilder();
        sb.append("http://ws.audioscrobbler.com/2.0/?method=artist.gettopalbums&artist=");
        sb.append(artist);
        sb.append("&api_key=");
        sb.append("dd9ced546f7bedffd7383459b13326e3");
        String url = sb.toString();
        Log.d("Last.fm api call:", url);

        ArrayList<AlbumEntry> tempAlbumList = new ArrayList<>();

        tempAlbumList = new Parse().execute(url).get();

        // TODO: race condition ?

        //tempAlbumList = xmlResponse;

        // return list of Album objects
        return tempAlbumList;
    }

    class Parse extends AsyncTask<String, Void, ArrayList<AlbumEntry>> {

        protected ArrayList<AlbumEntry> doInBackground(String... urls) {
            // search for url, returns xml of albums
            ArrayList<AlbumEntry> albumList = null;
            //Log.d("DEBUG", "in doInBackground");
            try {
                InputStream input = new URL(urls[0]).openStream();
                XMLParser xmlparser = new XMLParser();
                albumList = xmlparser.parse(input);
                //Log.d("DEBUG", "out of XMLParser");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return albumList;
        }

        @Override
        protected void onPostExecute(ArrayList<AlbumEntry> list) {
            xmlResponse = list;
        }

    }

}
