package com.bojie.materialtest.callbacks;

import com.bojie.materialtest.pojo.Movie;

import java.util.ArrayList;

/**
 * Created by bojiejiang on 5/21/15.
 */
public interface BoxOfficeMoviesLoaderListener {

    public void onBoxOfficeMoviesLoaded(ArrayList<Movie> movieArrayList);
}
