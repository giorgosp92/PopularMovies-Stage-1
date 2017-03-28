package eu.gpatsiaouras.popularmovies.Data;


import android.net.Uri;
import android.provider.BaseColumns;

public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "eu.gpatsiaouras.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAVORITE = "favorite";

    public static final class FavoriteEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITE)
                .build();

        public static final String TABLE_NAME = "favorite_movies";
        public static final String COLUMN_MOVIESDBID = "movies_db_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_IMAGE_PATH = "image_path";


        public static Uri buildFavoriteUriWithId(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }
    }
}
