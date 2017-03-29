package eu.gpatsiaouras.popularmovies;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;


import java.net.URL;

import eu.gpatsiaouras.popularmovies.Data.MoviesContract;
import eu.gpatsiaouras.popularmovies.Utilities.MovieDatabaseUtilities;
import eu.gpatsiaouras.popularmovies.Utilities.NetworkUtilities;


/**
 * Movie details activity
 */

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Boolean>{
    private static final String TAG = DetailActivity.class.getSimpleName();

    public static final int DETAIL_LOADER_ID = 23;
    public static final String MOVIE_DB_ID_KEY = "movie_db_id_key";
    private Uri mUriWithId;
    private Uri mUri;

    private ImageView dBackdrop;
    private ImageView dPoster;
    private TextView dTitle;
    private TextView dOverview;
    private ProgressBar dLoading;
    private TextView dVoteAverage;
    private TextView dRuntime;
    private TextView dGenres;
    private ConstraintLayout dDetailMain;
    private TextView dErrorMessage;
    private TextView dBudget;
    private TextView dReleaseDate;
    private Context context;
    private MovieDetails dMovieDetails;
    private MovieVideo[] dMovieVideos;
    private ImageButton dFavoriteButton;
    private GridView dGridView;
    private int dMovieId;

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
        dDetailMain = (ConstraintLayout) findViewById(R.id.cl_detail_main);
        dErrorMessage= (TextView) findViewById(R.id.tv_detail_error_message);
        dReleaseDate= (TextView) findViewById(R.id.tv_detail_release_date);
        dFavoriteButton = (ImageButton) findViewById(R.id.ib_detail_favorite_button);
        dFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavorite();
            }
        });

        //Set invisible because there is no data yet
        dDetailMain.setVisibility(View.INVISIBLE);

        Intent intentCameFrom = getIntent();
        if (intentCameFrom.hasExtra("movieId")) {
            //Fetch movie Detail Data
            dMovieId = intentCameFrom.getIntExtra("movieId", 0);
            Log.d(TAG, "Fetching data for movie details for id: "+dMovieId);
            mUriWithId = MoviesContract.FavoriteEntry.buildFavoriteUriWithId(dMovieId);
            mUri = MoviesContract.FavoriteEntry.CONTENT_URI;
        }

        Bundle detailsBundle = new Bundle();
        detailsBundle.putInt(MOVIE_DB_ID_KEY, dMovieId);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> detailLoader = loaderManager.getLoader(DETAIL_LOADER_ID);

        dGridView = (GridView) findViewById(R.id.gv_videos);
        dGridView.setNumColumns(3);
        dGridView.setHorizontalSpacing(8);
        dGridView.setVerticalSpacing(8);
        dGridView.setVerticalScrollBarEnabled(false);

        if (detailLoader == null) {
            loaderManager.initLoader(DETAIL_LOADER_ID, detailsBundle, this);
        } else {
            loaderManager.restartLoader(DETAIL_LOADER_ID, detailsBundle, this);
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
        } else if (optionSelected == R.id.item_details_reviews) {
            Intent intentToStartReviews = new Intent(context, ReviewsActivity.class);
            intentToStartReviews.putExtra("movieId", String.valueOf(dMovieId));
            intentToStartReviews.putExtra("movieTitle", getTitle());
            startActivity(intentToStartReviews);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        dDetailMain.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        dErrorMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<Boolean> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<Boolean>(this) {
            @Override
            protected void onStartLoading() {
                if (args == null)
                    return;
                dLoading.setVisibility(View.VISIBLE);
                forceLoad();
            }

            @Override
            public Boolean loadInBackground() {
                int movieId = args.getInt(MOVIE_DB_ID_KEY);
                URL movieDetailsUrl = NetworkUtilities.buildDetailURL(movieId);
                URL movieVideosUrl = NetworkUtilities.buildMovieVideosURL(movieId);
                try {
                    String jsonMovieDetailsResponse = NetworkUtilities.getResponsefromHttpUrl(movieDetailsUrl);
                    dMovieDetails = MovieDatabaseUtilities.getMovieDetailsFromMovieJson(jsonMovieDetailsResponse);
                    //Fetch Movie Trailers
                    String jsonMovieVideosResponse = NetworkUtilities.getResponsefromHttpUrl(movieVideosUrl);
                    dMovieVideos = MovieDatabaseUtilities.getMovieVideosFromJson(jsonMovieVideosResponse);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            public void deliverResult(Boolean data) {
                super.deliverResult(data);
                dLoading.setVisibility(View.INVISIBLE);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Boolean> loader, Boolean data) {
        dLoading.setVisibility(View.INVISIBLE);
        if (dMovieDetails != null) {
            populateView(dMovieDetails);
            dDetailMain.setVisibility(View.VISIBLE);
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<Boolean> loader) {
        //I am not using this function. Its just mandatory to implement Loader
    }

    private void populateView(MovieDetails movieDetails) {
        dTitle.setText(movieDetails.getTitle());
        /* Images */
        Picasso.with(context).load(movieDetails.getImageUri(NetworkUtilities.IMAGE_SIZE_MEDIUM)).placeholder(R.drawable.poster_placeholder).into(dPoster);
        Picasso.with(context).load(movieDetails.getBackdropUri(NetworkUtilities.BACKDROP_SIZE_LARGE)).placeholder(R.drawable.backdrop_placeholder).into(dBackdrop);
        /* Right Column */
        dVoteAverage.setText(movieDetails.getVoteAverage()+"/10");
        dRuntime.setText(movieDetails.getRuntime() + " " + getString(R.string.duration_minutes_value_small));
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
        if (is_favorite()) {
            dFavoriteButton.setImageResource(R.drawable.favorite_activated);
        } else {
            dFavoriteButton.setImageResource(R.drawable.favorite_deactivated);
        }
        //Populate Videos from dMovieVideos
        dGridView.setAdapter(new VideoAdapter(context));
        dGridView.setVisibility(View.VISIBLE);

    }

    public boolean is_favorite() {
        Cursor cursor = getContentResolver().query(mUriWithId, null, null, null, null);
        if (cursor.getCount() > 0)
            return true;
        return false;
    }

    public void toggleFavorite() {

        if (is_favorite()) {
            //Movie is already a favorite. So remove it
            int numDeleted = getContentResolver().delete(mUriWithId, null, null);
            if (numDeleted > 0){
                dFavoriteButton.setImageResource(R.drawable.favorite_deactivated);
                Toast.makeText(this, getString(R.string.favorite_removed), Toast.LENGTH_SHORT).show();
                //
                setResult(Activity.RESULT_OK);
            }
        } else {
            // Movie is not a favorite. So add it.
            ContentValues values = new ContentValues(3);
            values.put(MoviesContract.FavoriteEntry.COLUMN_MOVIESDBID, dMovieId);
            values.put(MoviesContract.FavoriteEntry.COLUMN_TITLE, dMovieDetails.getTitle());
            values.put(MoviesContract.FavoriteEntry.COLUMN_IMAGE_PATH, dMovieDetails.getImagePath());

            mUriWithId = getContentResolver().insert(mUri, values);
            if (mUriWithId != null) {
                dFavoriteButton.setImageResource(R.drawable.favorite_activated);
                Toast.makeText(this, getString(R.string.favorite_saved), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class VideoAdapter extends BaseAdapter implements View.OnClickListener{
        private Context vContext;
        private LayoutInflater mLayoutInflater;
        private MovieVideo[] vMovieVideos = dMovieVideos;

        public VideoAdapter(Context context) {
            vContext = context;
            mLayoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return vMovieVideos.length;
        }

        @Override
        public Object getItem(int position) {
            return vMovieVideos[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = mLayoutInflater.inflate(R.layout.video_list_item, parent, false);
            } else {
                view = convertView;
            }

            ImageView imageView = (ImageView) view.findViewById(R.id.iv_video_thumbnail);
            ImageButton imageButton = (ImageButton) view.findViewById(R.id.iv_play_button);
            String video_key = vMovieVideos[position].getKey();
            imageButton.setTag(video_key);
            Picasso.with(context).load(NetworkUtilities.builtYoutubeImageUri(video_key)).placeholder(R.drawable.poster_placeholder).into(imageView);
            imageButton.setOnClickListener(this);
            return view;
        }

        @Override
        public void onClick(View v) {
            String video_id = (String) v.getTag();
            Uri video_uri = NetworkUtilities.builtYoutubeUri(video_id);
            /* Used vode from http://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent */
            Intent youtubeAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + video_id));
            Intent youtubeWebIntent = new Intent(Intent.ACTION_VIEW,video_uri);
            try {
                startActivity(youtubeAppIntent);
            } catch (ActivityNotFoundException ex) {
                startActivity(youtubeWebIntent);
            }
        }
    }

}
