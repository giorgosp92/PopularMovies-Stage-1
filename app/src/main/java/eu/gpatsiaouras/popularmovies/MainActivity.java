package eu.gpatsiaouras.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import java.net.URL;
import java.text.ParseException;

import eu.gpatsiaouras.popularmovies.Data.MoviesContract;
import eu.gpatsiaouras.popularmovies.Utilities.EndlessRecyclerViewScrollListener;
import eu.gpatsiaouras.popularmovies.Utilities.MovieDatabaseUtilities;
import eu.gpatsiaouras.popularmovies.Utilities.NetworkUtilities;
import eu.gpatsiaouras.popularmovies.adapter.MovieAdapter;

public class MainActivity extends AppCompatActivity implements
        MovieAdapter.MovieAdapterOnClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener
{

    private static final String TAG = MainActivity.class.getSimpleName();
    private MovieAdapter mAdapter;
    private Movie[] mMoviesDataset;
    private RecyclerView mRecyclerView;
    private ProgressBar loadingProgressBar;
    private TextView errorMessageDisplay;
    private GridLayoutManager layoutManager;
    private String dataSetType;
    /* Recycler View onScroll*/
    private EndlessRecyclerViewScrollListener scrollListener;
    private int currentPage = 1;
    private int mRecyclerViewPosition;
    private static final int REQUEST_CODE = 1;
    private static final String SAVED_RECYCLER_VIEW_POSITION_KEY = "recycler_view_position";
    private static final String MOVIE_DATASET_KEY = "movie_dataset";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = this;

        loadingProgressBar = (ProgressBar) findViewById(R.id.pb_loading);
        errorMessageDisplay = (TextView) findViewById(R.id.tv_error_message);
        //
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies_grid);
        layoutManager = new GridLayoutManager(this, calculateNoOfColumns(context), GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                currentPage = page;
                loadMovieData();
                Log.d(TAG, "Loading more. Current page to be loaded is "+currentPage);
                Log.d(TAG, "Total items count is: "+totalItemsCount);
            }
        };
        // Adds the scroll listener to RecyclerView
        mRecyclerView.addOnScrollListener(scrollListener);

        setupSharedPreferences();

        //Check if there is a saved instance, a saved position for the recycler view and a movies dataset
        if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_RECYCLER_VIEW_POSITION_KEY) && savedInstanceState.containsKey(MOVIE_DATASET_KEY)) {
            //Retrieve the movies dataset from the bundle
            mMoviesDataset = (Movie[]) savedInstanceState.getParcelableArray(MOVIE_DATASET_KEY);
            // Set the dataset to the mAdapter
            mAdapter.setMovieData(mMoviesDataset);
            // Retrieve the position where the user was when he left the app or rotated the device
            mRecyclerViewPosition = savedInstanceState.getInt(SAVED_RECYCLER_VIEW_POSITION_KEY);
            //if the position is valid go to that position
            if (mRecyclerViewPosition != RecyclerView.NO_POSITION) {
                layoutManager.scrollToPosition(mRecyclerViewPosition);
            }
        } else {
            // There was not a saved instance. Load data normally from network
            if (isOnline())
                loadMovieData();
            else{
                loadingProgressBar.setVisibility(View.INVISIBLE);
                showErrorMessage();
            }
        }
    }


    private void setTypeOfList(String typeOfList) {
        dataSetType = typeOfList;

        if (dataSetType.equals(getString(R.string.pref_type_list_popular_value))) {
            setTitle(getString(R.string.pref_type_list_popular_label) + " " + getString(R.string.movies));
        } else if (dataSetType.equals(getString(R.string.pref_type_list_top_rated_value))) {
            setTitle(getString(R.string.pref_type_list_top_rated_label) + " " + getString(R.string.movies));
        } else if (dataSetType.equals(getString(R.string.pref_type_list_favorites_value))) {
            setTitle(getString(R.string.pref_type_list_favorites_label) + " " + getString(R.string.movies));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItem = item.getItemId();
        if (selectedItem == R.id.item_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return true;
    }


    private void loadMovieData() {
        showMoviesGridView();
        new FetchMoviesTask().execute();
    }

    private void resetRecyclerView() {
        /* Force page to scroll to top*/
        currentPage = 1;
        /* Reset listener state */
        scrollListener.resetState();
    }


    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        errorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void showMoviesGridView() {
        /* First, make sure the error is invisible */
        errorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Make sure the movie grid is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(int movieId) {
        Context context = this;
        Intent intentToStartDetailActivity = new Intent(context, DetailActivity.class);
        intentToStartDetailActivity.putExtra("movieId", movieId);
        startActivityForResult(intentToStartDetailActivity, REQUEST_CODE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetRecyclerView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                resetRecyclerView();
                currentPage=1;
                loadMovieData();
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_type_list_key))) {
            setTypeOfList(sharedPreferences.getString(getString(R.string.pref_type_list_key), getString(R.string.pref_type_list_popular_value)));
            //Reset Recycler View
            resetRecyclerView();
            currentPage = 1;
            //Update data
            loadMovieData();
        }
    }

    public class FetchMoviesTask extends AsyncTask<Void, Void, Movie[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            loadingProgressBar.setVisibility(View.INVISIBLE);
            if (movies != null) {
                mMoviesDataset = movies;
                showMoviesGridView();
                if (currentPage == 1)
                    mAdapter.setMovieData(movies);
                else {
                    mAdapter.appendMovieData(movies);
                }

            } else {
                showErrorMessage();
            }
        }

        @Override
        protected Movie[] doInBackground(Void... params){
            if (dataSetType.equals(getString(R.string.pref_type_list_popular_value)) || dataSetType.equals(getString(R.string.pref_type_list_top_rated_value))) {
                URL moviesRequestUrl = NetworkUtilities.buildListURL(dataSetType, currentPage);
                try {
                    String jsonMovieResponse = NetworkUtilities.getResponsefromHttpUrl(moviesRequestUrl);
                    Movie[] movieObjects = MovieDatabaseUtilities.getMovieObjectsFromMoviesJson(jsonMovieResponse);
                    return movieObjects;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            } else if (dataSetType.equals(getString(R.string.pref_type_list_favorites_value))) {
                Movie[] movieObjects;
                if (currentPage > 1) {
                    movieObjects = new Movie[0];
                    return movieObjects;
                }
                //Make offline call to MoviesProvider to retrieve favorite movies
                Cursor favoriteMoviesCursor = getContentResolver().query(MoviesContract.FavoriteEntry.CONTENT_URI, null, null, null, MoviesContract.FavoriteEntry._ID);
                movieObjects = MovieDatabaseUtilities.getMovieObjectsFromCursor(favoriteMoviesCursor);
                return movieObjects;
            }

            return null;
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //type of list
        setTypeOfList(sharedPreferences.getString(getString(R.string.pref_type_list_key), getString(R.string.pref_type_list_popular_value)));
        // Register the listener
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 150);
        return noOfColumns;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Get Current Position from layout manager
        int currentPosition = layoutManager.findFirstVisibleItemPosition();
        // Put the position in bundle
        outState.putInt(SAVED_RECYCLER_VIEW_POSITION_KEY, currentPosition);
        // My recycler view has implemented the EndlessRecyclerViewScrollListener which means that
        // as the user scrolls more and more data are being loaded.
        // That's why if the user has reached to the position of 150 in layoutManager we have to preserve
        // the movies list so we can recreate the recyclerview as it was exactly and with the same dataset
        // We use the fuction getMovieData from the mAdapter because it the one that can return the whole dataset
        // and not just the last partion (the last page)
        outState.putParcelableArray(MOVIE_DATASET_KEY, mAdapter.getMovieData());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //Restoration of the Recyclerview and it's position is taking place in method onCreate
    }
}
