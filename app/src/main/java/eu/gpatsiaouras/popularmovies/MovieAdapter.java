package eu.gpatsiaouras.popularmovies;


import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private static final String TAG = MovieAdapter.class.getSimpleName();
    private Movie[] moviesArray;
    private Context context;

    private final MovieAdapterOnClickHandler clickHandler;

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }


    public interface MovieAdapterOnClickHandler {
        void onClick(int movieId);
    }

    @Override
    public MovieAdapter.MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(R.layout.movie_grid_item, viewGroup, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapter.MovieAdapterViewHolder holder, int position) {
        Movie currentMovie = moviesArray[position];
        Picasso.with(context).load(currentMovie.getImageUri()).placeholder(R.drawable.poster_placeholder).into(holder.mPosterImage);
    }

    @Override
    public int getItemCount() {
        if (null == moviesArray) return 0;
        return moviesArray.length;
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView mPosterImage;

        public MovieAdapterViewHolder(View view) {
            super(view);
            mPosterImage = (ImageView) view.findViewById(R.id.iv_poster);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            int movieId = moviesArray[adapterPosition].getId();
            clickHandler.onClick(movieId);
        }
    }

    public void setMovieData(Movie[] moviesData) {
        moviesArray = moviesData;
        notifyDataSetChanged();
    }

    public void appendMovieData(Movie[] moviesData) {
        int oldArrayLength = moviesArray.length;
        int toBeAppendedArrayLength = moviesData.length;
        int finalArrayLength = oldArrayLength + toBeAppendedArrayLength;
        // Create a new Array of Movie objects with the the summary of both old and new array
        Movie[] newData = new Movie[finalArrayLength];
        // Populate new array with both old and new arrays.
        for (int i=0; i < finalArrayLength;i++) {
            // if i is less than the oldArraylength put data from the old array
            if (i < oldArrayLength)
                newData[i] = moviesArray[i];
            //if i is more than the oldArray. Start appending new data
            else
                newData[i] = moviesData[i-oldArrayLength];
        }
        //populate dataset
        moviesArray = newData;
        //Notify adapter about the change in size.
        notifyItemRangeInserted(oldArrayLength, toBeAppendedArrayLength);
    }
}
