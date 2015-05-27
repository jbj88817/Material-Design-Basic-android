package com.bojie.materialtest.callbacks;

import com.bojie.materialtest.pojo.Movie;

import java.util.ArrayList;

/**
 * Created by bojiejiang on 5/25/15.
 */
public interface UpcomingMoviesLoadedListener {

        public void onUpcomingMoviesLoaded(ArrayList<Movie> listMovies);
}
