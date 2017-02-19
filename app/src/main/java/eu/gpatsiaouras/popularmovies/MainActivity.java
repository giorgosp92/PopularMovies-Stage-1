package eu.gpatsiaouras.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import eu.gpatsiaouras.popularmovies.Utilities.EndlessRecyclerViewScrollListener;
import eu.gpatsiaouras.popularmovies.Utilities.MovieDatabaseUtilities;
import eu.gpatsiaouras.popularmovies.Utilities.NetworkUtilities;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler{

    private static final String TAG = MainActivity.class.getSimpleName();
    private MovieAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar loadingProgressBar;
    private TextView errorMessageDisplay;
    private GridLayoutManager layoutManager;
    private String dataSetType;
    /* Recycler View onScroll*/
    private EndlessRecyclerViewScrollListener scrollListener;
    private int currentPage = 1;
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
                loadMoreMovies();
                Log.d(TAG, "Loading more. Current page to be loaded is "+currentPage);
            }
        };
        // Adds the scroll listener to RecyclerView
        mRecyclerView.addOnScrollListener(scrollListener);

        /* Application starts with popular movies as default*/
        dataSetType = "popular";
        /* Fetch movies Data */
        if (isOnline())
            loadMovieData(dataSetType);
        else{
            loadingProgressBar.setVisibility(View.INVISIBLE);
            showErrorMessage();
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
        if (selectedItem == R.id.item_switch_sort) {
            switchDataSetAndTitle();
            if (item.getTitle().equals(getResources().getString(R.string.popular)))
                item.setTitle(R.string.top_rated);
            else
                item.setTitle(R.string.popular);
        } else {

        }
        return true;
    }

    private void loadMoreMovies() {
        loadMovieData(dataSetType);
    }

    private void loadMovieData(String type_of_list) {
        showMoviesGridView();
        new FetchMoviesTask().execute(type_of_list);
    }

    private void resetRecyclerView() {
        /* Force page to scroll to top*/
        currentPage = 1;
        /* Reset listener state */
        scrollListener.resetState();
    }

    private void switchDataSetAndTitle() {
        resetRecyclerView();
        if (dataSetType == "popular") {
            loadMovieData("toprated");
            setTitle(getResources().getString(R.string.top_rated)+" "+ getResources().getString(R.string.movies));
            dataSetType = "toprated";
        } else {
            loadMovieData("popular");
            setTitle(getResources().getString(R.string.popular)+" "+ getResources().getString(R.string.movies));
            dataSetType = "popular";
        }
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
        startActivity(intentToStartDetailActivity);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            loadingProgressBar.setVisibility(View.INVISIBLE);
            if (movies != null) {
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
        protected Movie[] doInBackground(String... params) {
            /* If there is not a type of fetching defined return null */
            if (params.length == 0) {
                return null;
            }
            String type_of_list = params[0];
            URL moviesRequestUrl = NetworkUtilities.buildListURL(type_of_list, currentPage);
            try {
                String jsonMovieResponse = NetworkUtilities.getResponsefromHttpUrl(moviesRequestUrl);
                Movie[] justMovieObjects = MovieDatabaseUtilities.getMovieObjectsFromMoviesJson(jsonMovieResponse);
                return justMovieObjects;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }
}
