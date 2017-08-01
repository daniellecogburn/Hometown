package dcogburn.hometown;


/**
 * Created by travisgarbe on 7/30/17.
 */

public class UserInformation {

    public String name;
    public String emailAddress;
    public int albumsLogged;

    public UserInformation() {

    }

    public void setName (String name) {
        this.name = name;
    }

    public void setEmailAddress (String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setAlbumsLogged (int albumsLogged) {
        this.albumsLogged = albumsLogged;
    }

    public String getName() {
        return name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public int getAlbumsLogged() {
        return albumsLogged;
    }

    public UserInformation(String name, String emailAddress, int albumsLogged) {
        this.name = name;
        this.emailAddress = emailAddress;
        this.albumsLogged = albumsLogged;
    }

}
