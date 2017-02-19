package eu.gpatsiaouras.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


import java.io.IOException;
import java.net.URL;

import eu.gpatsiaouras.popularmovies.Utilities.MovieDatabaseUtilities;
import eu.gpatsiaouras.popularmovies.Utilities.NetworkUtilities;


/**
 * Movie details activity
 */

public class DetailActivity extends AppCompatActivity{
    private static final String TAG = DetailActivity.class.getSimpleName();

    private ImageView dBackdrop;
    private ImageView dPoster;
    private TextView dTitle;
    private TextView dOverview;
    private ProgressBar dLoading;
    private TextView dVoteAverage;
    private TextView dRuntime;
    private TextView dGenres;
    private ScrollView dDetailMain;
    private TextView dErrorMessage;
    private TextView dBudget;
    private TextView dReleaseDate;
    private Context context;
    private MovieDetails dMovieDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        context = this;

        dBackdrop   = (ImageView) findViewById(R.id.iv_detail_backdrop);
        dPoster     = (ImageView) findViewById(R.id.iv_detail_poster);
        dTitle      = (TextView) findViewById(R.id.tv_detail_title);
        dOverview   = (TextView) findViewById(R.id.tv_detail_overview);
        dLoading    = (ProgressBar) findViewById(R.id.pb_detail_loading);
        dVoteAverage= (TextView) findViewById(R.id.tv_detail_vote_average);
        dRuntime    = (TextView) findViewById(R.id.tv_detail_runtime);
        dGenres     = (TextView) findViewById(R.id.tv_detail_genres);
        dBudget     = (TextView) findViewById(R.id.tv_detail_budget);
        dDetailMain = (ScrollView) findViewById(R.id.sv_detail_main);
        dErrorMessage= (TextView) findViewById(R.id.tv_detail_error_message);
        dReleaseDate= (TextView) findViewById(R.id.tv_detail_release_date);

        Intent intentCameFrom = getIntent();
        if (intentCameFrom.hasExtra("movieId")) {
            //Fetch movie Detail Data
            Log.d(TAG, "Fetching data for movie details for id: "+intentCameFrom.getIntExtra("movieId", 0));
            loadMovieDetailData(intentCameFrom.getIntExtra("movieId", 0));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int optionSelected = item.getItemId();
        if (optionSelected == R.id.item_details_imdb) {
            Uri imdbUri = NetworkUtilities.builtImdbURI(dMovieDetails.getImdbId());
            Intent imdbIntent = new Intent(Intent.ACTION_VIEW, imdbUri);
            if (imdbIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(imdbIntent);
            }
        } else if (optionSelected == R.id.item_details_share){
            ShareCompat.IntentBuilder.from(this)
                    .setType("text/plain")
                    .setText(getResources().getString(R.string.share_message)+dMovieDetails.getTitle()+" \n\n"+NetworkUtilities.builtImdbURI(dMovieDetails.getImdbId()))
                    .setChooserTitle(getResources().getString(R.string.share_chooser_title))
                    .startChooser();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMovieDetailData(int movieId) {
        dDetailMain.setVisibility(View.INVISIBLE);
        new FetchMovieDetailsTask().execute(movieId);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        dDetailMain.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        dErrorMessage.setVisibility(View.VISIBLE);
    }


    public class FetchMovieDetailsTask extends AsyncTask<Integer, Void, MovieDetails> {

        @Override
        protected MovieDetails doInBackground(Integer... params) {
            if (params.length == 0) {
                return null;
            }

            int movieId = params[0];
            URL movieDetailsUrl = NetworkUtilities.buildDetailURL(movieId);
            try {
                String jsonMovieDetailsResponse = NetworkUtilities.getResponsefromHttpUrl(movieDetailsUrl);
                MovieDetails movieDetails = MovieDatabaseUtilities.getMovieDetailsFromMovieJson(jsonMovieDetailsResponse);
                return movieDetails;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(MovieDetails movieDetails) {
            super.onPostExecute(movieDetails);
            dLoading.setVisibility(View.INVISIBLE);
            if (movieDetails != null) {
                dMovieDetails = movieDetails;
                populateView(movieDetails);
                dDetailMain.setVisibility(View.VISIBLE);
            } else {
                showErrorMessage();
            }

        }
    }

    private void populateView(MovieDetails movieDetails) {
        dTitle.setText(movieDetails.getTitle());
        /* Images */
        Picasso.with(context).load(movieDetails.getImageUri("w500")).placeholder(R.drawable.poster_placeholder).into(dPoster);
        Picasso.with(context).load(movieDetails.getBackdropUri("w1000")).placeholder(R.drawable.backdrop_placeholder).into(dBackdrop);
        /* Right Column */
        dVoteAverage.setText(movieDetails.getVoteAverage()+"/10");
        dRuntime.setText(movieDetails.getRuntime()+" min");
        dGenres.setText(movieDetails.getGenres());
        /* Under poster and right column*/
        dOverview.setText(movieDetails.getOverview());
        if (movieDetails.getBudget() > 0)
            dBudget.setText(movieDetails.getBudget()+" $");
        else
            dBudget.setText(getString(R.string.no_data));
        dReleaseDate.setText(movieDetails.getDate());
        /* Set Window Title */
        setTitle(movieDetails.getTitle());
    }
}
