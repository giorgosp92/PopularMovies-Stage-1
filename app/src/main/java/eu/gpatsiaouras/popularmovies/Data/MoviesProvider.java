package eu.gpatsiaouras.popularmovies.Data;


import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import eu.gpatsiaouras.popularmovies.Movie;

public class MoviesProvider extends ContentProvider{

    public static final int CODE_FAVORITE = 100;
    public static final int CODE_FAVORITE_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper mOpenHelper;

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MoviesContract.PATH_FAVORITE, CODE_FAVORITE);
        matcher.addURI(authority, MoviesContract.PATH_FAVORITE + "/#", CODE_FAVORITE_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        return 0;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        final String listType = PopularMoviesPreferences.getListTypeSelected(getContext());

        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITE:
                cursor = mOpenHelper.getReadableDatabase().query(MoviesContract.FavoriteEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, MoviesContract.FavoriteEntry._ID);
                break;
            case CODE_FAVORITE_WITH_ID:
                String movieDbId =uri.getLastPathSegment();
                String[] selectionArguments = new String[]{movieDbId};
                cursor = mOpenHelper.getReadableDatabase().query(MoviesContract.FavoriteEntry.TABLE_NAME, projection, MoviesContract.FavoriteEntry.COLUMN_MOVIESDBID + " = ? ",selectionArguments, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITE:
                long _id = db.insert(MoviesContract.FavoriteEntry.TABLE_NAME, null, values);
                if (_id == -1)
                    throw new RuntimeException("Something Went Wrong" + uri);
                else {
                    int movieDbId = (int) values.get(MoviesContract.FavoriteEntry.COLUMN_MOVIESDBID);
                    return MoviesContract.FavoriteEntry.buildFavoriteUriWithId(movieDbId);
                }

            default:
                throw new UnsupportedOperationException("Unknwon Uri: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numRowsDeleted;

        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITE_WITH_ID:
                String movieDbId = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{movieDbId};
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MoviesContract.FavoriteEntry.TABLE_NAME,
                        MoviesContract.FavoriteEntry.COLUMN_MOVIESDBID + " = ?",
                        selectionArguments);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
