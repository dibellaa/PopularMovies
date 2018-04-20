package com.example.android.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.example.android.popularmovies.util.NetworkUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class Trailer implements Parcelable {
    private String id;
    private String key;
    private String name;
    private Uri thumbUri;
    private Uri appUri;
    private Uri browserUri;

    public Trailer(String id, String key, String name) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.thumbUri = NetworkUtils.buildYouTubeThumbUrl(key);
        this.appUri = NetworkUtils.buildYouTubeAppUrl(key);
        this.browserUri = NetworkUtils.buildYouTubeBrowserUrl(key);
    }

    protected Trailer(Parcel in) {
        id = in.readString();
        key = in.readString();
        name = in.readString();
        thumbUri = in.readParcelable(Uri.class.getClassLoader());
        appUri = in.readParcelable(Uri.class.getClassLoader());
        browserUri = in.readParcelable(Uri.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(key);
        dest.writeString(name);
        dest.writeParcelable(thumbUri, flags);
        dest.writeParcelable(appUri, flags);
        dest.writeParcelable(browserUri, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getThumbUri() {
        return thumbUri;
    }

    public Uri getAppUri() {
        return appUri;
    }

    public Uri getBrowserUri() {
        return browserUri;
    }

    public void displayThumb(final Context context, ImageView mTrailerImageView) {
        Uri imageToDisplay = this.thumbUri;
        Picasso.with(context)
                .load(imageToDisplay)
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.image_error)
                .into(mTrailerImageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        NetworkUtils.checkConnection(context);
                    }
                });
    }
}
