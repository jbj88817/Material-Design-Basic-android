package com.bojie.materialtest.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.bojie.materialtest.R;
import com.bojie.materialtest.adapters.MovieAdapter;
import com.bojie.materialtest.callbacks.BoxOfficeMoviesLoaderListener;
import com.bojie.materialtest.database.MoviesDatabase;
import com.bojie.materialtest.extras.MovieSorter;
import com.bojie.materialtest.extras.SortListener;
import com.bojie.materialtest.logging.L;
import com.bojie.materialtest.material.MyApplication;
import com.bojie.materialtest.network.VolleySingleton;
import com.bojie.materialtest.pojo.Movie;
import com.bojie.materialtest.task.TaskLoadMoviesBoxOffice;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BoxOfficeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BoxOfficeFragment extends Fragment implements SortListener,
        BoxOfficeMoviesLoaderListener, SwipeRefreshLayout.OnRefreshListener {

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
    private MovieAdapter mBoxOfficeAdapter;
    private TextView mVolleyError;
    private MovieSorter mMovieSorter;
    private SwipeRefreshLayout mSwipeRefreshLayout;


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


        //sendJSONRequest();
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
        mBoxOfficeAdapter = new MovieAdapter(getActivity());
        mListMovieHits.setAdapter(mBoxOfficeAdapter);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeMovieHits);
        

        if (savedInstanceState != null) {
            mMoviesList = savedInstanceState.getParcelableArrayList(STATE_MOVIES);
        } else {
            mMoviesList = MyApplication.getWritableDatabase().readMovies(MoviesDatabase.BOX_OFFICE);
            if (mMoviesList.isEmpty()) {
                L.t(getActivity(), "executing task from fragment");
                new TaskLoadMoviesBoxOffice(this).execute();
            }
        }
        mBoxOfficeAdapter.setMovies(mMoviesList);
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

    @Override
    public void onBoxOfficeMoviesLoaded(ArrayList<Movie> movieArrayList) {
        L.t(getActivity(), "onBoxOfficeMoviesLoaded Fragment");
        if (mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(false);
        }
        mBoxOfficeAdapter.setMovies(movieArrayList);
    }

    @Override
    public void onRefresh() {
        L.t(getActivity(), "onRefresh");
        new TaskLoadMoviesBoxOffice(this).execute();
    }
}
