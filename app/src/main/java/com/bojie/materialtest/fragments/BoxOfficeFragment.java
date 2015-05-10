package com.bojie.materialtest.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bojie.materialtest.R;
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
public class BoxOfficeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private VolleySingleton mVolleySingleton;
    private ImageLoader mImageLoader;
    private RequestQueue mRequestQueue;
    private ArrayList<Movie> mMoviesList = new ArrayList<>();
    private DateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");


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

    public static String getRequestUrl(int limit) {

        return URL_BOX_OFFICE
                + URL_CHAR_QUESTION
                + URL_PARAM_API_KEY + MyApplication.API_KEY_ROTTEN_TOMATOES
                + URL_CHAR_AMEPERSAND
                + URL_PARAM_LIMIT + limit;

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

    private void sendJSONRequest() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                getRequestUrl(10),
                (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        parseJSONResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mRequestQueue.add(request);
    }

    private void parseJSONResponse(JSONObject response) {
        if (response == null || response.length() == 0) {
            return;
        }

        try {
            JSONArray arrayMovies = response.getJSONArray(KEY_MOVIES);
            for (int i = 0; i < arrayMovies.length(); i++) {
                JSONObject currentMovie = arrayMovies.getJSONObject(i);

                // Basic
                long id = currentMovie.getLong(KEY_ID);
                String title = currentMovie.getString(KEY_TITLE);

                // Release Date
                JSONObject objectReleaseDate = currentMovie.getJSONObject(KEY_RELEASE_DATES);
                String releaseDate = null;
                if (objectReleaseDate.has(KEY_THEATER)) {
                    releaseDate = objectReleaseDate.getString(KEY_THEATER);
                } else {
                    releaseDate = "NA";
                }

                // Audience Score
                JSONObject objectRatings = currentMovie.getJSONObject(KEY_RATINGS);
                int audienceScore = -1;
                if (objectRatings.has(KEY_AUDIENCE_SCORE)) {
                    audienceScore = objectRatings.getInt(KEY_AUDIENCE_SCORE);
                }

                // Synopsis
                String synopsis = currentMovie.getString(KEY_SYNOPSIS);

                // Poster image
                JSONObject objectPosters = currentMovie.getJSONObject(KEY_POSTERS);
                String urlThumbnail = null;
                if (objectPosters.has(KEY_THUMBNAIL)) {
                    urlThumbnail = objectPosters.getString(KEY_THUMBNAIL);
                }

                // Set to movie object
                Movie movie = new Movie();
                movie.setId(id);
                movie.setTitle(title);
                Date date = mDateFormat.parse(releaseDate);
                movie.setReleaseDateTheater(date);

                // Add to movieList
                mMoviesList.add(movie);

            }

            L.T(getActivity(), mMoviesList.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_box_office, container, false);
    }


}
