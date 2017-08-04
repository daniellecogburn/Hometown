package dcogburn.hometown;

import java.net.URL;

/**
 * Created by Benjamin on 7/28/2017.
 */

public class AlbumInfo {

    // info
    private String albumName;
    private String artistName;
    private String albumArt;
    private String city;

    // user submitted info
    private int rating; // rating 1-10, step size = .5
    private boolean saved;

    // constructor used when album is generated
    public AlbumInfo(String albumName, String artistName, String albumArt, String city) {
        this.albumName = albumName;
        this.artistName = artistName;
        this.albumArt = albumArt;
        this.city = city;
        saved = false;
        rating = 0;
    }

    public AlbumInfo(){

    }

    // getters
    public String getAlbumName() {
        return albumName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public String getCity() {
        return city;
    }

    public int getRating() {
        return rating;
    }

    public boolean getSaved() {
        return saved;
    }


    // setters
    public void setRating(int newRating) {
        this.rating = newRating;
    }

    public void setSaved(boolean newSaved) {
        this.saved = newSaved;
    }

}
