package eu.gpatsiaouras.popularmovies;

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
    /* release data */
    private Date release_date;
    private boolean adult;
    private String overview;
    /* Original Data */
    private String original_title;
    private String original_language;
    /* Votes and Rating*/
    private int vote_count;
    private double vote_average;

    private double popularity;
    private Uri image_uri;

    public Movie(String title, int id, Date release_date,String adult, String overview, String original_title, String original_language, double vote_average, int vote_count, double popularity, String poster_path) throws ParseException {
        this.title = title;
        this.id = id;
        this.overview = overview;
        this.original_title = original_title;
        this.original_language = original_language;
        this.vote_count = vote_count;
        this.vote_average = vote_average;
        this.popularity = popularity;
        this.image_uri = NetworkUtilities.buildImageURI(poster_path.substring(1));/* First letter is a slash. Remove the slash*/
        this.adult = Boolean.valueOf(adult);
        this.release_date = release_date;
    }

    /* Returns the image url of this object*/
    public Uri getImageUri() {
        return this.image_uri;
    }
    public String getTitle() {
        return this.title;
    }
    public int getId() {
        return this.id;
    }
    /* Remaining functions for the rest of the attributes will be created upon need*/
}
