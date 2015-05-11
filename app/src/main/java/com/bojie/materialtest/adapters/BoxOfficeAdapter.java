package com.bojie.materialtest.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.bojie.materialtest.R;
import com.bojie.materialtest.network.VolleySingleton;
import com.bojie.materialtest.pojo.Movie;

import java.util.ArrayList;

/**
 * Created by bojiejiang on 5/10/15.
 */
public class BoxOfficeAdapter extends RecyclerView.Adapter<BoxOfficeAdapter.BoxOfficeViewHolder> {

    private LayoutInflater mLayoutInflater;
    private ArrayList<Movie> mMovieArrayList = new ArrayList<>();
    private VolleySingleton mVolleySingleton;
    private ImageLoader mImageLoader;
    private Context mContext;

    public BoxOfficeAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mVolleySingleton = VolleySingleton.getInstance();
        mImageLoader = mVolleySingleton.getImageLoader();
        mContext = context;
    }

    public void setMovieArrayList(ArrayList<Movie> movieArrayList) {
        mMovieArrayList = movieArrayList;
        notifyItemRangeChanged(0, movieArrayList.size());
    }

    @Override
    public BoxOfficeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.custom_movie_box_office, parent, false);
        BoxOfficeViewHolder viewHolder = new BoxOfficeViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final BoxOfficeViewHolder holder, int position) {
        Movie currentMovie = mMovieArrayList.get(position);
        holder.movieTitle.setText(currentMovie.getTitle());
        holder.moveReleaseDate.setText(currentMovie.getReleaseDateTheater().toString());
        holder.movieAudienceScore.setRating(currentMovie.getAudienceScore() / 20.0F);

        //
        String urlThumbnail = currentMovie.getUrlThumbnail();
        if (urlThumbnail != null) {
            mImageLoader.get(urlThumbnail, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    holder.movieThumbnail.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mMovieArrayList.size();
    }

    static class BoxOfficeViewHolder extends RecyclerView.ViewHolder {

        private ImageView movieThumbnail;
        private TextView movieTitle;
        private TextView moveReleaseDate;
        private RatingBar movieAudienceScore;

        public BoxOfficeViewHolder(View itemView) {
            super(itemView);
            movieThumbnail = (ImageView) itemView.findViewById(R.id.movieThumbnail);
            movieTitle = (TextView) itemView.findViewById(R.id.movieTitle);
            moveReleaseDate = (TextView) itemView.findViewById(R.id.movieReleaseDate);
            movieAudienceScore = (RatingBar) itemView.findViewById(R.id.movieAudienceScore);
        }
    }
}
