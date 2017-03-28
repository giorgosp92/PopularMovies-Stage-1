package eu.gpatsiaouras.popularmovies.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import eu.gpatsiaouras.popularmovies.Data.MoviesContract;
import eu.gpatsiaouras.popularmovies.Movie;
import eu.gpatsiaouras.popularmovies.MovieDetails;
import eu.gpatsiaouras.popularmovies.MovieReview;
import eu.gpatsiaouras.popularmovies.MovieVideo;

/**
 * This class will contain all methods to manipulate data from the db movie database api
 */

public class MovieDatabaseUtilities {
    private static final String TAG = MovieDatabaseUtilities.class.getSimpleName();

    public static Movie[] getMovieObjectsFromMoviesJson(String moviesJsonString)
            throws JSONException{
        Movie[] moviesObjects = null;
        JSONObject moviesJson = new JSONObject(moviesJsonString);
        JSONArray moviesArray = moviesJson.getJSONArray("results");

        moviesObjects = new Movie[moviesArray.length()];

        for (int i=0; i < moviesArray.length();i++) {
            JSONObject movieObject = moviesArray.getJSONObject(i);
            int id                      = movieObject.getInt("id");
            String title                = movieObject.getString("title");
            String image_path           = movieObject.getString("poster_path");
            /*Date format*/
            Date release_date = null;
            Movie currentMovieObject = null;
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                release_date = formatter.parse(movieObject.getString("release_date"));
                currentMovieObject = new Movie(id, title, image_path);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }


            moviesObjects[i] = currentMovieObject;
        }
        Log.d(TAG, moviesObjects.toString());
        return moviesObjects;

    }

    public static MovieDetails getMovieDetailsFromMovieJson(String movieDetailsJson)
        throws JSONException {
        JSONObject movieDetailsObject = new JSONObject(movieDetailsJson);
        //Populate Movie Details Object
        boolean adult = movieDetailsObject.getBoolean("adult");
        Long budget = movieDetailsObject.getLong("budget");
        String homepage = movieDetailsObject.getString("homepage");
        int id = movieDetailsObject.getInt("id");
        String  imdb_id = movieDetailsObject.getString("imdb_id");
        String original_language = movieDetailsObject.getString("original_language");
        String original_title = movieDetailsObject.getString("original_title");
        String overview = movieDetailsObject.getString("overview");
        Long popularity = movieDetailsObject.getLong("popularity");
        String backdrop_path = movieDetailsObject.getString("backdrop_path");
        String poster_path = movieDetailsObject.getString("poster_path");
        Long revenue = movieDetailsObject.getLong("revenue");
        int runtime = movieDetailsObject.getInt("runtime");
        String status = movieDetailsObject.getString("status");
        String tagline = movieDetailsObject.getString("tagline");
        String title = movieDetailsObject.getString("title");
        boolean video = movieDetailsObject.getBoolean("video");
        double vote_average = movieDetailsObject.getDouble("vote_average");
        long vote_count = movieDetailsObject.getLong("vote_count");
        /* Genres */
        JSONArray genresArray = movieDetailsObject.getJSONArray("genres");
        String genresString = "";
        if (genresArray.length()>0) {
            for (int i=0; i<genresArray.length();i++) {
                JSONObject thisgenre = genresArray.getJSONObject(i);
                genresString += thisgenre.getString("name")+", ";
            }
            /* remove the last comma and space*/
            genresString = genresString.substring(0, genresString.length() -2);
        } else {
            genresString = "";
        }

        Date release_date;
        MovieDetails movieDetails = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            release_date = formatter.parse(movieDetailsObject.getString("release_date"));
            movieDetails = new MovieDetails(adult,
             budget,
             homepage,
             id,
             imdb_id,
             original_language,
             original_title,
             overview,
             popularity,
             backdrop_path,
             poster_path,
             release_date,
             revenue,
             runtime,
             status,
             tagline,
             title,
             vote_average,
             vote_count,
             genresString);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        Log.d(TAG, movieDetails.toString());
        return movieDetails;
    }


    public static MovieVideo[] getMovieVideosFromJson(String movieVideosJsonString)
        throws JSONException {
        JSONObject videosJson = new JSONObject(movieVideosJsonString);

        JSONArray videosArray = videosJson.getJSONArray("results");

        MovieVideo[] movieVideosArray = new MovieVideo[videosArray.length()];

        for (int i=0; i < videosArray.length();i++) {

            JSONObject videoCurrentObject = videosArray.getJSONObject(i);

            String key  = videoCurrentObject.getString("key");
            String name = videoCurrentObject.getString("name");
            String site = videoCurrentObject.getString("site");
            String type = videoCurrentObject.getString("type");

            MovieVideo currentMovieVideo = new MovieVideo(key, name, site, type);

            movieVideosArray[i] = currentMovieVideo;
        }
        return movieVideosArray;
    }

    public static MovieReview[] getMovieReviewsFromJson(String movieReviewsJsonString)
        throws JSONException {
        JSONObject reviewsJson = new JSONObject(movieReviewsJsonString);

        JSONArray reviewsArray = reviewsJson.getJSONArray("results");

        MovieReview[] movieReviewsArray = new MovieReview[reviewsArray.length()];

        for (int i=0; i < reviewsArray.length();i++) {
            JSONObject reviewCurrentObject = reviewsArray.getJSONObject(i);

            String id           = reviewCurrentObject.getString("id");
            String author       = reviewCurrentObject.getString("author");
            String content      = reviewCurrentObject.getString("content");
            String url          = reviewCurrentObject.getString("url");

            MovieReview currentMovieReview = new MovieReview(id, author, content, url);

            movieReviewsArray[i] = currentMovieReview;
        }
        return movieReviewsArray;
    }

    public static Movie[] getMovieObjectsFromCursor(Cursor movieResultsCursor) {
        if (movieResultsCursor == null) {
            return null;
        }

        int movieDbIdCol    = movieResultsCursor.getColumnIndex(MoviesContract.FavoriteEntry.COLUMN_MOVIESDBID);
        int titleCol        = movieResultsCursor.getColumnIndex(MoviesContract.FavoriteEntry.COLUMN_TITLE);
        int imageCol        = movieResultsCursor.getColumnIndex(MoviesContract.FavoriteEntry.COLUMN_IMAGE_PATH);

        Movie[] movieObjects = new Movie[movieResultsCursor.getCount()];

        while (movieResultsCursor.moveToNext()) {
            int movieDbId    = movieResultsCursor.getInt(movieDbIdCol);
            String title        = movieResultsCursor.getString(titleCol);
            String image_path   = movieResultsCursor.getString(imageCol);
            try {
                Movie currentMovieObject = new Movie(movieDbId, title, image_path);
                movieObjects[movieResultsCursor.getPosition()] = currentMovieObject;
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }

        }

        return movieObjects;


    }
}
