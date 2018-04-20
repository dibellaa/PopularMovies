package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {
    private List<Review> mReviews = new ArrayList<>();
    private ReviewAdapterOnClickHandler mClickHandler;

    public ReviewAdapter(ReviewAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }
    @Override
    public ReviewAdapter.ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int idReview = R.layout.review_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        return new ReviewAdapterViewHolder(inflater.inflate(idReview, parent, false));
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ReviewAdapterViewHolder holder, int position) {
        Review review = mReviews.get(position);
        holder.mAuthorTextView.setText(review.getAuthor());
        holder.mContentTextView.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mAuthorTextView;
        private TextView mContentTextView;
        private ReviewAdapterViewHolder(View itemView) {
            super(itemView);
            mAuthorTextView = itemView.findViewById(R.id.tv_review_author);
            mContentTextView = itemView.findViewById(R.id.tv_review_item);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Review reviewSelected = mReviews.get(adapterPosition);
            mClickHandler.onClick(reviewSelected);
        }
    }

    public interface ReviewAdapterOnClickHandler {
        void onClick(Review review);
    }

    public void setData(List<Review> reviews) {
        mReviews = reviews;
        notifyDataSetChanged();
    }
}
