package com.example.android.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.util.NetworkUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


/**
 * Created by adibella on 22/02/2018.
 */

public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterAdapterViewHolder>{
    private Movie[] mMovies;
    private PosterAdapterOnClickHandler mClickHandler;

    public PosterAdapter(PosterAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }
    @Override
    public PosterAdapter.PosterAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int idPosterItem = R.layout.poster_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        return new PosterAdapterViewHolder(inflater.inflate(idPosterItem,
                parent, false));
    }

    @Override
    public void onBindViewHolder(PosterAdapter.PosterAdapterViewHolder holder, int position) {
        final Context context = holder.mPosterImageView.getContext();
        Movie movie = mMovies[position];
        Uri imageToDisplay = movie.getPathPoster();
        Picasso.with(context)
                .load(imageToDisplay)
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.image_error)
                .into(holder.mPosterImageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        NetworkUtils.checkConnection(context);
                    }
                });
    }

    @Override
    public int getItemCount() {
        if (null == mMovies) return 0;
        return mMovies.length;
    }

    public class PosterAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final ImageView mPosterImageView;
        public PosterAdapterViewHolder(View itemView) {
            super(itemView);
            mPosterImageView = itemView.findViewById(R.id.poster_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Movie movieSelected = mMovies[adapterPosition];
            mClickHandler.onClick(movieSelected);
        }
    }

    public void setData(Movie[] movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }

    public interface PosterAdapterOnClickHandler {
        void onClick(Movie movie);
    }
}
