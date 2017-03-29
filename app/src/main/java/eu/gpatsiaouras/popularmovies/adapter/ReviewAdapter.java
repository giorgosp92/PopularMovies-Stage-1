package eu.gpatsiaouras.popularmovies.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import eu.gpatsiaouras.popularmovies.MovieReview;
import eu.gpatsiaouras.popularmovies.R;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder>{

    private static final String TAG = ReviewAdapter.class.getSimpleName();
    private MovieReview[] mReviewsArray;
    private Context context;

    @Override
    public ReviewAdapter.ReviewAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(R.layout.review_list_item, viewGroup, shouldAttachToParentImmediately);
        return new ReviewAdapter.ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ReviewAdapterViewHolder holder, int position) {
        holder.mAuthor.setText(mReviewsArray[position].getAuthor());
        holder.mContent.setText(mReviewsArray[position].getContent());

    }

    @Override
    public int getItemCount() {
        if (null == mReviewsArray) return 0;
        return mReviewsArray.length;
    }


    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mAuthor;
        private TextView mContent;

        public ReviewAdapterViewHolder(View itemView) {
            super(itemView);
            mAuthor = (TextView) itemView.findViewById(R.id.tv_review_author);
            mContent = (TextView) itemView.findViewById(R.id.tv_review_content);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public void setReviewsData(MovieReview[] reviewsData) {
        mReviewsArray = reviewsData;
        notifyDataSetChanged();
    }

    public void appendReviewsData(MovieReview[] reviewsData) {
        int oldArrayLength = mReviewsArray.length;
        int toBeAppendedArrayLength = reviewsData.length;
        int finalArrayLength = oldArrayLength + toBeAppendedArrayLength;
        // Create a new Array of Movie objects with the the summary of both old and new array
        MovieReview[] newData = new MovieReview[finalArrayLength];
        // Populate new array with both old and new arrays.
        for (int i=0; i < finalArrayLength;i++) {
            // if i is less than the oldArraylength put data from the old array
            if (i < oldArrayLength)
                newData[i] = mReviewsArray[i];
                //if i is more than the oldArray. Start appending new data
            else
                newData[i] = reviewsData[i-oldArrayLength];
        }
        //populate dataset
        mReviewsArray = newData;
        //Notify adapter about the change in size.
        notifyItemRangeInserted(oldArrayLength, toBeAppendedArrayLength);
    }
}
