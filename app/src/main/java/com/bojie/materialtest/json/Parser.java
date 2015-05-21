package com.bojie.materialtest.json;

import com.bojie.materialtest.extras.Constants;
import com.bojie.materialtest.pojo.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.bojie.materialtest.extras.Keys.EndpointBoxOffice.KEY_AUDIENCE_SCORE;
import static com.bojie.materialtest.extras.Keys.EndpointBoxOffice.KEY_CAST;
import static com.bojie.materialtest.extras.Keys.EndpointBoxOffice.KEY_ID;
import static com.bojie.materialtest.extras.Keys.EndpointBoxOffice.KEY_LINKS;
import static com.bojie.materialtest.extras.Keys.EndpointBoxOffice.KEY_MOVIES;
import static com.bojie.materialtest.extras.Keys.EndpointBoxOffice.KEY_POSTERS;
import static com.bojie.materialtest.extras.Keys.EndpointBoxOffice.KEY_RATINGS;
import static com.bojie.materialtest.extras.Keys.EndpointBoxOffice.KEY_RELEASE_DATES;
import static com.bojie.materialtest.extras.Keys.EndpointBoxOffice.KEY_REVIEWS;
import static com.bojie.materialtest.extras.Keys.EndpointBoxOffice.KEY_SELF;
import static com.bojie.materialtest.extras.Keys.EndpointBoxOffice.KEY_SIMILAR;
import static com.bojie.materialtest.extras.Keys.EndpointBoxOffice.KEY_SYNOPSIS;
import static com.bojie.materialtest.extras.Keys.EndpointBoxOffice.KEY_THEATER;
import static com.bojie.materialtest.extras.Keys.EndpointBoxOffice.KEY_THUMBNAIL;
import static com.bojie.materialtest.extras.Keys.EndpointBoxOffice.KEY_TITLE;

import static com.bojie.materialtest.json.Utils.*;

/**
 * Created by bojiejiang on 5/21/15.
 */
public class Parser {

    public static ArrayList<Movie> parseJSONResponse(JSONObject response) {
        DateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        ArrayList<Movie> listMovies = new ArrayList<>();
        if (response != null && response.length() > 0) {
            try {
                JSONArray arrayMovies = response.getJSONArray(KEY_MOVIES);
                for (int i = 0; i < arrayMovies.length(); i++) {
                    long id = -1;
                    String title = Constants.NA;
                    String releaseDate = Constants.NA;
                    int audienceScore = -1;
                    String synopsis = Constants.NA;
                    String urlThumbnail = Constants.NA;
                    String urlSelf = Constants.NA;
                    String urlCast = Constants.NA;
                    String urlReviews = Constants.NA;
                    String urlSimilar = Constants.NA;
                    JSONObject currentMovie = arrayMovies.getJSONObject(i);
                    //get the id of the current movie
                    if (contains(currentMovie, KEY_ID)) {
                        id = currentMovie.getLong(KEY_ID);
                    }
                    //get the title of the current movie
                    if (contains(currentMovie, KEY_TITLE)) {
                        title = currentMovie.getString(KEY_TITLE);
                    }

                    //get the date in theaters for the current movie
                    if (contains(currentMovie, KEY_RELEASE_DATES)) {
                        JSONObject objectReleaseDates = currentMovie.getJSONObject(KEY_RELEASE_DATES);

                        if (contains(objectReleaseDates, KEY_THEATER)) {
                            releaseDate = objectReleaseDates.getString(KEY_THEATER);
                        }
                    }

                    //get the audience score for the current movie

                    if (contains(currentMovie, KEY_RATINGS)) {
                        JSONObject objectRatings = currentMovie.getJSONObject(KEY_RATINGS);
                        if (contains(objectRatings, KEY_AUDIENCE_SCORE)) {
                            audienceScore = objectRatings.getInt(KEY_AUDIENCE_SCORE);
                        }
                    }

                    // get the synopsis of the current movie
                    if (contains(currentMovie, KEY_SYNOPSIS)) {
                        synopsis = currentMovie.getString(KEY_SYNOPSIS);
                    }

                    //get the url for the thumbnail to be displayed inside the current movie result
                    if (contains(currentMovie, KEY_POSTERS)) {
                        JSONObject objectPosters = currentMovie.getJSONObject(KEY_POSTERS);

                        if (contains(objectPosters, KEY_THUMBNAIL)) {
                            urlThumbnail = objectPosters.getString(KEY_THUMBNAIL);
                        }
                    }

                    //get the url of the related links
                    if (contains(currentMovie, KEY_LINKS)) {
                        JSONObject objectLinks = currentMovie.getJSONObject(KEY_LINKS);
                        if (contains(objectLinks, KEY_SELF)) {
                            urlSelf = objectLinks.getString(KEY_SELF);
                        }
                        if (contains(objectLinks, KEY_CAST)) {
                            urlCast = objectLinks.getString(KEY_CAST);
                        }
                        if (contains(objectLinks, KEY_REVIEWS)) {
                            urlReviews = objectLinks.getString(KEY_REVIEWS);
                        }
                        if (contains(objectLinks, KEY_SIMILAR)) {
                            urlSimilar = objectLinks.getString(KEY_SIMILAR);
                        }
                    }
                    Movie movie = new Movie();
                    movie.setId(id);
                    movie.setTitle(title);
                    Date date = null;
                    try {
                        date = mDateFormat.parse(releaseDate);
                    } catch (ParseException e) {
                        //a parse exception generated here will store null in the release date, be sure to handle it
                    }
                    movie.setReleaseDateTheater(date);
                    movie.setAudienceScore(audienceScore);
                    movie.setSynopsis(synopsis);
                    movie.setUrlThumbnail(urlThumbnail);
                    movie.setUrlSelf(urlSelf);
                    movie.setUrlCast(urlCast);
                    movie.setUrlThumbnail(urlThumbnail);
                    movie.setUrlReviews(urlReviews);
                    movie.setUrlSimilar(urlSimilar);
//                    L.t(getActivity(), movie + "");
                    if (id != -1 && !title.equals(Constants.NA)) {
                        listMovies.add(movie);
                    }
                }

            } catch (JSONException e) {

            }
        }
        return listMovies;
    }
}
