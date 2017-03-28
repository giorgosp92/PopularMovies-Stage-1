package eu.gpatsiaouras.popularmovies.Utilities;


import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

import static android.content.ContentValues.TAG;

public final class NetworkUtilities {

    private static final String TAG = NetworkUtilities.class.getSimpleName();

    private static final String MOVIES_DB_URL = "https://api.themoviedb.org/3/";
    private static final String IMAGES_MOVIES_DB_URL = "https://image.tmdb.org/t/p/";
    private static final String IMDB_MOVIE_URL = "http://www.imdb.com/title/";
    private static final String YOUTUBE_IMAGE_URL = "https://img.youtube.com/vi/";
    private static final String YOUTUBE_VIDEO_URL = "https://www.youtube.com/watch";
    /* INSERT YOUR API HERE*/
    private static final String MOVIES_DB_API_KEY = "";

    final static String MOVIE_PARAM = "movie";
    final static String REVIEW_PARAM = "reviews";
    final static String VIDEO_PARAM = "videos";
    final static String WATCH_PARAM = "v";
    final static String API_PARAM = "api_key";
    final static String POPULAR_PARAM = "popular";
    final static String TOP_RATED_PARAM = "top_rated";
    final static String PAGE_PARAM = "page";

    public final static String IMAGE_SIZE_SMALL = "w185";
    public final static String IMAGE_SIZE_MEDIUM = "w500";
    public final static String IMAGE_SIZE_LARGE = "w780";

    public final static String BACKDROP_SIZE_SMALL = "w300";
    public final static String BACKDROP_SIZE_MEDIUM = "w780";
    public final static String BACKDROP_SIZE_LARGE = "w1280";

    public static URL buildListURL(String type_of_list, int page) {
        Uri builtUri = Uri.parse(MOVIES_DB_URL).buildUpon()
                .appendPath(MOVIE_PARAM)
                .appendPath((type_of_list.equals(POPULAR_PARAM)? POPULAR_PARAM : TOP_RATED_PARAM))
                .appendQueryParameter(API_PARAM, MOVIES_DB_API_KEY)
                .appendQueryParameter(PAGE_PARAM, String.valueOf(page))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Build URI is " + url);
        return url;
    }

    public static URL buildDetailURL(int movieId) {

        Uri builtUri = Uri.parse(MOVIES_DB_URL).buildUpon()
                .appendPath(MOVIE_PARAM)
                .appendPath(String.valueOf(movieId))
                .appendQueryParameter(API_PARAM, MOVIES_DB_API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Build URI is " + url);
        return url;
    }

    public static URL buildMovieVideosURL(int movieId) {
        Uri builtUri = Uri.parse(MOVIES_DB_URL).buildUpon()
                .appendPath(MOVIE_PARAM)
                .appendPath(String.valueOf(movieId))
                .appendPath(VIDEO_PARAM)
                .appendQueryParameter(API_PARAM, MOVIES_DB_API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Build URI is " + url);
        return url;
    }

    public static URL buildMovieReviewsURL(int movieId) {
        Uri builtUri = Uri.parse(MOVIES_DB_URL).buildUpon()
                .appendPath(MOVIE_PARAM)
                .appendPath(String.valueOf(movieId))
                .appendPath(REVIEW_PARAM)
                .appendQueryParameter(API_PARAM, MOVIES_DB_API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Build URI is " + url);
        return url;
    }

    public static Uri buildImageURI(String image_id) {
        return buildImageURI(image_id, IMAGE_SIZE_SMALL);
    }
    public static Uri buildImageURI(String image_id, String size) {
        Uri builtUri = Uri.parse(IMAGES_MOVIES_DB_URL).buildUpon()
                .appendPath(size)
                .appendPath(image_id)
                .build();

        Log.v(TAG, "Build Image URI is " + builtUri);
        return builtUri;
    }

    public static Uri builtImdbURI(String imdbId) {
        Uri builtUri = Uri.parse(IMDB_MOVIE_URL).buildUpon()
                .appendPath(imdbId)
                .build();
        return builtUri;
    }

    public static Uri builtYoutubeImageUri(String key) {
        Uri builtUri = Uri.parse(YOUTUBE_IMAGE_URL).buildUpon()
                .appendPath(key)
                .appendPath("0.jpg")
                .build();

        return builtUri;
    }

    public static Uri builtYoutubeUri(String key) {
        Uri builtUri = Uri.parse(YOUTUBE_VIDEO_URL).buildUpon()
                .appendQueryParameter(WATCH_PARAM, key)
                .build();

        return builtUri;
    }

    public static String getResponsefromHttpUrl(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = conn.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            conn.disconnect();
        }
    }
}
