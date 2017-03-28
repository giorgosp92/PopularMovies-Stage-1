package eu.gpatsiaouras.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Network;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;

import eu.gpatsiaouras.popularmovies.Utilities.MovieDatabaseUtilities;
import eu.gpatsiaouras.popularmovies.Utilities.NetworkUtilities;

public class ReviewsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<MovieReview[]>{

    private static final int REVIEWS_LOADER_ID = 22;



    private static final String MOVIE_DB_ID_KEY = "movie_db_id_key";
    private String mMovieId;
    private TextView movieTitle;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private MovieReview[] mMovieReview;

    private Context mContext;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager layoutManager;
    private ReviewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        setTitle(getString(R.string.reviews_activity_title));
        mContext = this;
        movieTitle = (TextView) findViewById(R.id.tv_review_movie_title);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_review_error_message);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_reviews_loading);

        Intent intentCameFrom = getIntent();
        if (intentCameFrom.hasExtra("movieTitle")) {
            setTitle(intentCameFrom.getStringExtra("movieTitle") + " " + getString(R.string.reviews_activity_title));
            movieTitle.setText(intentCameFrom.getStringExtra("movieTitle"));
        }
        if (intentCameFrom.hasExtra("movieId"))
            mMovieId = intentCameFrom.getStringExtra("movieId");

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_reviews_list);
        layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new ReviewAdapter();
        mRecyclerView.setAdapter(mAdapter);

        Bundle reviewsBundle = new Bundle();
        reviewsBundle.putString(MOVIE_DB_ID_KEY, mMovieId);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> reviewsLoader = loaderManager.getLoader(REVIEWS_LOADER_ID);

        if (reviewsLoader == null) {
            loaderManager.initLoader(REVIEWS_LOADER_ID, reviewsBundle, this);
        } else {
            loaderManager.restartLoader(REVIEWS_LOADER_ID, reviewsBundle, this);
        }
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void showReviews() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }

    @Override
    public Loader<MovieReview[]> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<MovieReview[]>(this) {

            @Override
            protected void onStartLoading() {
                if (args == null)
                    return;

                mLoadingIndicator.setVisibility(View.VISIBLE);
                forceLoad();
            }

            @Override
            public MovieReview[] loadInBackground() {
                URL reviewsRequestUrl = NetworkUtilities.buildMovieReviewsURL(Integer.parseInt(args.getString(MOVIE_DB_ID_KEY)));
                try {
                    String jsonReviewsResponse = NetworkUtilities.getResponsefromHttpUrl(reviewsRequestUrl);
                    MovieReview[] movieReviews = MovieDatabaseUtilities.getMovieReviewsFromJson(jsonReviewsResponse);
                    return movieReviews;
                } catch (Exception e) {
                    System.out.println("BOOM");
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(MovieReview[] data) {
                mMovieReview = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<MovieReview[]> loader, MovieReview[] data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        if (data == null)
            showErrorMessage();
        else {
            mAdapter.setReviewsData(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<MovieReview[]> loader) {

    }


}
