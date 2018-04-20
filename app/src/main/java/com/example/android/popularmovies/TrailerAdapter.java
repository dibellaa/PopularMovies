package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {
    private List<Trailer> mTrailers = new ArrayList<>();
    private TrailerAdapterOnClickHandler mClickHandler;

    public TrailerAdapter(TrailerAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public TrailerAdapter.TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int idTrailerItem = R.layout.trailer_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        return new TrailerAdapterViewHolder(inflater.inflate(idTrailerItem,
                parent, false));
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.TrailerAdapterViewHolder holder, int position) {
        final Context context = holder.mTrailerImageView.getContext();
        Trailer trailer = mTrailers.get(position);
        trailer.displayThumb(context, holder.mTrailerImageView);
        holder.mTrailerTextView.setText(trailer.getName());
    }

    @Override
    public int getItemCount() {
        return mTrailers.size();
    }

    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView mTrailerImageView;
        private TextView mTrailerTextView;
        private TrailerAdapterViewHolder(View itemView) {
            super(itemView);
            mTrailerImageView = itemView.findViewById(R.id.iv_trailer_item);
            mTrailerTextView = itemView.findViewById(R.id.tv_trailer_item_name);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Trailer trailerSelected = mTrailers.get(adapterPosition);
            mClickHandler.onClick(trailerSelected);
        }
    }

    public interface TrailerAdapterOnClickHandler {
        void onClick(Trailer trailer);
    }

    public void setData(List<Trailer> trailers) {
        mTrailers = trailers;
        notifyDataSetChanged();
    }
}
