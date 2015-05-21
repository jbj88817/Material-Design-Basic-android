package com.bojie.materialtest.services;

import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.bojie.materialtest.extras.Constants;
import com.bojie.materialtest.logging.L;
import com.bojie.materialtest.material.MyApplication;
import com.bojie.materialtest.network.VolleySingleton;
import com.bojie.materialtest.pojo.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import me.tatarka.support.job.JobParameters;
import me.tatarka.support.job.JobService;

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
import static com.bojie.materialtest.extras.UriEndpoints.URL_BOX_OFFICE;
import static com.bojie.materialtest.extras.UriEndpoints.URL_CHAR_AMEPERSAND;
import static com.bojie.materialtest.extras.UriEndpoints.URL_CHAR_QUESTION;
import static com.bojie.materialtest.extras.UriEndpoints.URL_PARAM_API_KEY;
import static com.bojie.materialtest.extras.UriEndpoints.URL_PARAM_LIMIT;

/**
 * Created by bojiejiang on 5/18/15.
 */
public class MyService extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        L.t(this, "job start");
        new MyTask(this).execute(jobParameters);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    private static class MyTask extends AsyncTask<JobParameters, Void, JobParameters> {

        MyService myService;
        private VolleySingleton mVolleySingleton;
        private ImageLoader mImageLoader;
        private RequestQueue mRequestQueue;
        private DateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");


        MyTask(MyService myService) {
            this.myService = myService;
            mVolleySingleton = VolleySingleton.getInstance();
            mRequestQueue = mVolleySingleton.getRequestQueue();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JobParameters doInBackground(JobParameters... params) {

            JSONObject response = sendJSONRequest();
            ArrayList<Movie> movieList = parseJSONResponse(response);
            MyApplication.getWritableDatabase().insertMoviesBoxOffice(movieList, true);
            return params[0];
        }

        @Override
        protected void onPostExecute(JobParameters jobParameters) {
            myService.jobFinished(jobParameters, false);
        }

        public static String getRequestUrl(int limit) {

            return URL_BOX_OFFICE
                    + URL_CHAR_QUESTION
                    + URL_PARAM_API_KEY + MyApplication.API_KEY_ROTTEN_TOMATOES
                    + URL_CHAR_AMEPERSAND
                    + URL_PARAM_LIMIT + limit;

        }

        private JSONObject sendJSONRequest() {

            JSONObject response = null;

            RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                    getRequestUrl(30),
                    (String) null,
                    requestFuture,
                    requestFuture
            );
            mRequestQueue.add(request);
            try {
                response = requestFuture.get(30000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                L.m(e + "");
            } catch (ExecutionException e) {
                L.m(e + "");
            } catch (TimeoutException e) {
                L.m(e + "");
            }

            return response;
        }


        private ArrayList<Movie> parseJSONResponse(JSONObject response) {
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

        private boolean contains(JSONObject jsonObject, String key) {
            return jsonObject != null && jsonObject.has(key) && !jsonObject.isNull(key);
        }
    }
}
