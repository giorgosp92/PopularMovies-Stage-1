package eu.gpatsiaouras.popularmovies.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class MoviesDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "popularmovies.db";

    public static final int DATABASE_VERSION = 4;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_FAVORITE_TABLE =
                "CREATE TABLE " + MoviesContract.FavoriteEntry.TABLE_NAME + " (" +
                        MoviesContract.FavoriteEntry._ID                                    + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MoviesContract.FavoriteEntry.COLUMN_MOVIESDBID                      + " INTEGER NOT NULL, " +
                        MoviesContract.FavoriteEntry.COLUMN_TITLE                           + " TEXT NOT NULL, " +
                        MoviesContract.FavoriteEntry.COLUMN_IMAGE_PATH                      + " TEXT, " +

                        " UNIQUE (" + MoviesContract.FavoriteEntry.COLUMN_MOVIESDBID + ") ON CONFLICT REPLACE);";

        // Finally execute script
        db.execSQL(SQL_CREATE_FAVORITE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ MoviesContract.FavoriteEntry.TABLE_NAME);
        onCreate(db);
    }
}
