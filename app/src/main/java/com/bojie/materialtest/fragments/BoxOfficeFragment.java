package com.bojie.materialtest.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bojie.materialtest.R;
import com.bojie.materialtest.adapters.BoxOfficeAdapter;
import com.bojie.materialtest.extras.Constants;
import com.bojie.materialtest.extras.MovieSorter;
import com.bojie.materialtest.extras.SortListener;
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

import static com.bojie.materialtest.extras.Keys.EndpointBoxOffice.KEY_AUDIENCE_SCORE;
import static com.bojie.materialtest.extras.Keys.EndpointBoxOffice.KEY_ID;
import static com.bojie.materialtest.extras.Keys.EndpointBoxOffice.KEY_MOVIES;
import static com.bojie.materialtest.extras.Keys.EndpointBoxOffice.KEY_POSTERS;
import static com.bojie.materialtest.extras.Keys.EndpointBoxOffice.KEY_RATINGS;
import static com.bojie.materialtest.extras.Keys.EndpointBoxOffice.KEY_RELEASE_DATES;
import static com.bojie.materialtest.extras.Keys.EndpointBoxOffice.KEY_SYNOPSIS;
import static com.bojie.materialtest.extras.Keys.EndpointBoxOffice.KEY_THEATER;
import static com.bojie.materialtest.extras.Keys.EndpointBoxOffice.KEY_THUMBNAIL;
import static com.bojie.materialtest.extras.Keys.EndpointBoxOffice.KEY_TITLE;
import static com.bojie.materialtest.extras.Keys.EndpointBoxOffice.*;
import static com.bojie.materialtest.extras.UriEndpoints.URL_BOX_OFFICE;
import static com.bojie.materialtest.extras.UriEndpoints.URL_CHAR_AMEPERSAND;
import static com.bojie.materialtest.extras.UriEndpoints.URL_CHAR_QUESTION;
import static com.bojie.materialtest.extras.UriEndpoints.URL_PARAM_API_KEY;
import static com.bojie.materialtest.extras.UriEndpoints.URL_PARAM_LIMIT;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BoxOfficeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BoxOfficeFragment extends Fragment implements SortListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String STATE_MOVIES = "state_movies";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private VolleySingleton mVolleySingleton;
    private ImageLoader mImageLoader;
    private RequestQueue mRequestQueue;
    private ArrayList<Movie> mMoviesList = new ArrayList<>();
    private DateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private RecyclerView mListMovieHits;
    private BoxOfficeAdapter mBoxOfficeAdapter;
    private TextView mVolleyError;
    private MovieSorter mMovieSorter;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BoxOfficeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BoxOfficeFragment newInstance(String param1, String param2) {
        BoxOfficeFragment fragment = new BoxOfficeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_MOVIES, mMoviesList);
    }

    public BoxOfficeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mVolleySingleton = VolleySingleton.getInstance();
        mRequestQueue = mVolleySingleton.getRequestQueue();
        sendJSONRequest();
    }

    public static String getRequestUrl(int limit) {

        return URL_BOX_OFFICE
                + URL_CHAR_QUESTION
                + URL_PARAM_API_KEY + MyApplication.API_KEY_ROTTEN_TOMATOES
                + URL_CHAR_AMEPERSAND
                + URL_PARAM_LIMIT + limit;

    }

    private void sendJSONRequest() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                getRequestUrl(30),
                (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mVolleyError.setVisibility(View.GONE);
                        mMoviesList = parseJSONResponse(response);
                        mBoxOfficeAdapter.setMovieArrayList(mMoviesList);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleVolleyError(error);
            }
        });
        mRequestQueue.add(request);
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

    private void handleVolleyError(VolleyError error) {
        mVolleyError.setVisibility(View.VISIBLE);
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            mVolleyError.setText(R.string.error_timeout);

        } else if (error instanceof AuthFailureError) {
            mVolleyError.setText(R.string.error_auth_failure);
            //TODO
        } else if (error instanceof ServerError) {
            mVolleyError.setText(R.string.error_auth_failure);
            //TODO
        } else if (error instanceof NetworkError) {
            mVolleyError.setText(R.string.error_network);
            //TODO
        } else if (error instanceof ParseError) {
            mVolleyError.setText(R.string.error_parser);
            //TODO
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_box_office, container, false);
        mMovieSorter = new MovieSorter();
        mVolleyError = (TextView) view.findViewById(R.id.tv_VolleyError);
        mListMovieHits = (RecyclerView) view.findViewById(R.id.listMovieHits);
        mListMovieHits.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBoxOfficeAdapter = new BoxOfficeAdapter(getActivity());
        mListMovieHits.setAdapter(mBoxOfficeAdapter);
        if (savedInstanceState != null) {
            mMoviesList = savedInstanceState.getParcelableArrayList(STATE_MOVIES);
            mBoxOfficeAdapter.setMovieArrayList(mMoviesList);
        } else {
            sendJSONRequest();
        }
        return view;
    }


    @Override
    public void onSortByName() {
        L.t(getActivity(), "BoxOffice sort by name");
        mMovieSorter.sortMoviesByName(mMoviesList);
        mBoxOfficeAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSortByDate() {
        mMovieSorter.sortMoviesByDate(mMoviesList);
        mBoxOfficeAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSortByRating() {
        mMovieSorter.sortMoviesByRating(mMoviesList);
        mBoxOfficeAdapter.notifyDataSetChanged();
    }
}
