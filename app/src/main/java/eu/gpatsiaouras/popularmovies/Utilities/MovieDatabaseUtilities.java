package eu.gpatsiaouras.popularmovies.Utilities;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import eu.gpatsiaouras.popularmovies.Movie;
import eu.gpatsiaouras.popularmovies.MovieDetails;

/**
 * This class will contain all methods to manipulate data from the db movie database api
 */

public class MovieDatabaseUtilities {
    private static final String TAG = MovieDatabaseUtilities.class.getSimpleName();
    public static String[] getSimpleTitlesFromMoviesJson(String moviesJsonString)
        throws JSONException {

        /* String array to hold titles and be returned*/
        String[] movie_titles = null;

        JSONObject moviesJson = new JSONObject(moviesJsonString);
        JSONArray moviesArray = moviesJson.getJSONArray("results");

        movie_titles = new String[moviesArray.length()];

        for (int i=0; i < moviesArray.length();i++) {

            /* Get the current movie json object*/
            JSONObject movieObject = moviesArray.getJSONObject(i);
            /* Find the title and put it in movie titles string array*/
            movie_titles[i] = movieObject.getString("title");
        }

        return movie_titles;
    }

    public static Movie[] getMovieObjectsFromMoviesJson(String moviesJsonString)
            throws JSONException{
        Movie[] moviesObjects = null;
        JSONObject moviesJson = new JSONObject(moviesJsonString);
        JSONArray moviesArray = moviesJson.getJSONArray("results");

        moviesObjects = new Movie[moviesArray.length()];

        for (int i=0; i < moviesArray.length();i++) {
            JSONObject movieObject = moviesArray.getJSONObject(i);
            int id                      = movieObject.getInt("id");
            int vote_count              = movieObject.getInt("vote_count");
            double vote_average         = movieObject.getDouble("vote_average");
            double popularity           = movieObject.getDouble("popularity");
            String adult                = movieObject.getString("adult");
            String title                = movieObject.getString("title");
            String original_title       = movieObject.getString("original_title");
            String original_language    = movieObject.getString("original_language");
            String poster_path          = movieObject.getString("poster_path");
            String overview             = movieObject.getString("overview");
            /*Date format*/
            Date release_date = null;
            Movie currentMovieObject = null;
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                release_date = formatter.parse(movieObject.getString("release_date"));
                currentMovieObject = new Movie(title, id, release_date, adult, overview, original_title, original_language, vote_average, vote_count, popularity, poster_path);
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
             video,
             vote_average,
             vote_count,
             genresString);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        Log.d(TAG, movieDetails.toString());
        return movieDetails;
    }
}
