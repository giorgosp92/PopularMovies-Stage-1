package eu.gpatsiaouras.popularmovies;

import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.Date;

import eu.gpatsiaouras.popularmovies.Utilities.NetworkUtilities;

/**
 * Class that holds the details for a movie
 */

public class MovieDetails {
    boolean adult;
    Long budget;
    String homepage;
    int id;
    String imdb_id;
    String original_language;
    String original_title;
    String overview;
    Long popularity;
    String image_uri;
    String backdrop_uri;
    Date release_date;
    Long revenue;
    int runtime;
    String status;
    String tagline;
    String title;
    double vote_average;
    long vote_count;
    String genres;

    public MovieDetails(
            boolean adult,
            Long budget,
            String homepage,
            int id,
            String imdb_id,
            String original_language,
            String original_title,
            String overview,
            Long popularity,
            String backdrop_path,
            String poster_path,
            Date release_date,
            Long revenue,
            int runtime,
            String status,
            String tagline,
            String title,
            double vote_average,
            long vote_count,
            String genres
    ) {
        this.adult =  adult;
        this.budget =  budget;
        this.homepage =  homepage;
        this.id =  id;
        this.imdb_id =  imdb_id;
        this.original_language =  original_language;
        this.original_title =  original_title;
        this.overview =  overview;
        this.popularity =  popularity;
        this.image_uri =  poster_path.substring(1);
        this.backdrop_uri =  backdrop_path.substring(1);
        this.release_date =  release_date;
        this.revenue =  revenue;
        this.runtime =  runtime;
        this.status =  status;
        this.tagline =  tagline;
        this.title =  title;
        this.vote_average =  vote_average;
        this.vote_count =  vote_count;
        this.genres = genres;
    }

    public Uri getImageUri(String size) {
        return NetworkUtilities.buildImageURI(this.image_uri, size);
    }
    public Uri getBackdropUri(String size) {
        return NetworkUtilities.buildImageURI(this.backdrop_uri, size);
    }
    public String getImagePath() {
        return this.image_uri;
    }
    public String getTitle() {
        return this.title;
    }
    public String getOverview() {
        return this.overview;
    }
    public String getStatus() {
        return this.status;
    }
    public String getGenres() {
        return this.genres;
    }
    public String getDate() {
        String prettydate = new SimpleDateFormat("yyyy/MM/dd").format(this.release_date);
        return prettydate;
    }
    public String getImdbId() { return this.imdb_id;}
    public Long getBudget() {
        return this.budget;
    }
    public int getRuntime() {
        return this.runtime;
    }
    public double getVoteAverage() {
        return this.vote_average;
    }
    /* Remaining functions for the rest of the attributes will be created upon need*/
}
