package eu.gpatsiaouras.popularmovies;

import android.net.Network;
import android.net.Uri;

import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import eu.gpatsiaouras.popularmovies.Utilities.NetworkUtilities;


public class Movie {
    private static final String TAG = Movie.class.getSimpleName();
    /* Movie Title */
    private String title;
    /* Movie ID */
    private int id;
    private String image_path;

    public Movie(int id, String title, String image_path) throws ParseException {
        this.id = id;
        this.title = title;
        if (String.valueOf(image_path.charAt(0)).equals("/")) {
            this.image_path = image_path.substring(1);/* First letter is a slash. Remove the slash*/
        } else {
            this.image_path = image_path;
        }
    }

    /* Returns the image url of this object*/
    public String getImagePath() { return this.image_path;}
    public Uri getImageUri() {
        return NetworkUtilities.buildImageURI(this.image_path);
    }
    public String getTitle() {
        return this.title;
    }
    public int getId() {
        return this.id;
    }
    /* Remaining functions for the rest of the attributes will be created upon need*/
}
