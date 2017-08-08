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

    public AlbumGenerator() {

    }

    public AlbumInfo generateAlbum(String city, Scanner sFile) throws ExecutionException, InterruptedException {

        // get artist list
        String[] artistList = getArtistList(sFile);
        String artist = null;

        // generate random album and info
        AlbumEntry album = null;
        ArrayList<AlbumEntry> list;

        if(artistList != null) {
            do {
                do {
                    artist = chooseRandomArtist(artistList);
                    list = getAlbumList(artist);
                } while(list == null);
                album = getRandomAlbum(list);
            }
            while (album.imageLink.equals(""));
        }

        // return album
        return new AlbumInfo(album.name, artist, album.imageLink, city);
    }

    // get random album from artist
    private AlbumEntry getRandomAlbum(ArrayList<AlbumEntry> albumList) {
        int index;
        AlbumEntry album;

        // skip albums with firebase-unfriendly characters
        do{
            index = new Random().nextInt(albumList.size());
            album = albumList.get(index);
        } while (album.name.contains(".")
                || album.name.contains("$")
                || album.name.contains("[")
                || album.name.contains("]")
                || album.name.contains("#")
                || album.name.contains("/")
                || album.imageLink.contains(".gif"));
        return albumList.get(index);
    }

    // get artist array from city text file
    private String[] getArtistList(Scanner sFile) {
        ArrayList<String> artistList = new ArrayList<>();
        String token;
        while (sFile.hasNext()) {
            token = sFile.nextLine();
            artistList.add(token);
        }
        sFile.close();
        return artistList.toArray(new String[0]);
    }

    // pick a random artist from the artist list
    private String chooseRandomArtist(String[] artistList) {
        int index = new Random().nextInt(artistList.length);
        return artistList[index];
    }

    // last.fm api call
    private ArrayList<AlbumEntry> getAlbumList(String artist) throws ExecutionException, InterruptedException {

        // build URL
        StringBuilder sb = new StringBuilder();
        sb.append("http://ws.audioscrobbler.com/2.0/?method=artist.gettopalbums&artist=");
        sb.append(artist);
        sb.append("&api_key=");
        sb.append("dd9ced546f7bedffd7383459b13326e3");
        String url = sb.toString();
        Log.d("Last.fm api call:", url);

        // wait for AsyncTask to complete
        ArrayList<AlbumEntry> albumList = new Parse().execute(url).get();

        // return list of Album objects
        return albumList;
    }

    class Parse extends AsyncTask<String, Void, ArrayList<AlbumEntry>> {

        protected ArrayList<AlbumEntry> doInBackground(String... urls) {
            // search for url, returns xml of albums
            ArrayList<AlbumEntry> albumList = null;
            try {
                InputStream input = new URL(urls[0]).openStream();
                XMLParser xmlparser = new XMLParser();
                albumList = xmlparser.parse(input);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return albumList;
        }

    }

}
