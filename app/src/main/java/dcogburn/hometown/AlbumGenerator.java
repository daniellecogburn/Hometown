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

/**
 * Created by Benjamin on 7/28/2017.
 * Generates albums based on location.
 * uses last.fm api
 */

public class AlbumGenerator {

    // random number generator
    private Random rand;

    public AlbumGenerator() {
        rand = new Random();
        // set up api stuff here
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
                Log.d("ARTIST", artist);
                album = getRandomAlbum(getAlbumList(artist));
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

        artistList.add("animalcollective");

        return artistList.toArray(new String[0]);

    }

    private String chooseRandomArtist(String[] artistList) {
        int index = new Random().nextInt(artistList.length);
        return artistList[index];
    }

    private ArrayList<AlbumEntry> getAlbumList(String artist) {

        // build URL
        StringBuilder sb = new StringBuilder();
        sb.append("http://ws.audioscrobbler.com/2.0/?method=artist.gettopalbums&artist=");
        sb.append(artist);
        sb.append("&api_key=");
        sb.append("dd9ced546f7bedffd7383459b13326e3");
        String url = sb.toString();

        Log.d("Last.fm api call:", url);

        new Parse().execute(url);

        // TODO testCode, implement real lastfm code later
        ArrayList<AlbumEntry> tempAlbumList = new ArrayList<AlbumEntry>();
        tempAlbumList.add(new AlbumEntry("Merriwether Post Pavilion", "https://lastfm-img2.akamaized.net/i/u/300x300/ff4d87fef6994cb397f7f8cd98614170.png"));
        tempAlbumList.add(new AlbumEntry("Feels", "https://lastfm-img2.akamaized.net/i/u/300x300/fa7a08f4416041aab670992ae3bc52d8.png"));
        tempAlbumList.add(new AlbumEntry("Sung Tongs", "https://lastfm-img2.akamaized.net/i/u/300x300/76a6ba269784483d974f7594a671581f.png"));
        tempAlbumList.add(new AlbumEntry("Strawberry Jam", "https://lastfm-img2.akamaized.net/i/u/300x300/360dca58f95a480a916b37a5d0f8f7fd.png"));
        tempAlbumList.add(new AlbumEntry("Here Comes the Indian", "https://lastfm-img2.akamaized.net/i/u/300x300/6e3266929db2482182d039f9e221b32a.png"));
        tempAlbumList.add(new AlbumEntry("Centipede Hz", "https://lastfm-img2.akamaized.net/i/u/300x300/de477d524b724574abebd3e2fab76ff9.png"));

        // return list of Album objects
        return tempAlbumList;
    }

    class Parse extends AsyncTask<String, Void, ArrayList<AlbumEntry>> {

        // TODO this doesn't work at all pls help

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected ArrayList<AlbumEntry> doInBackground(String... urls) {
            // search for url, returns xml of albums
            ArrayList<AlbumEntry> albumList = null;
            Log.d("DEBUG", "in doInBackground");
            try {
                InputStream input = new URL(urls[0]).openStream();
                XMLParser xmlparser = new XMLParser();
                albumList = xmlparser.parse(input);
                Log.d("DEBUG", urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return albumList;
        }

        @Override
        protected void onPostExecute(ArrayList<AlbumEntry> list) {
            //tempAlbumList = list;
            super.onPreExecute();

        }

    }

}
