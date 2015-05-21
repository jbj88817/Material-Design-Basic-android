package com.bojie.materialtest.extras;

import com.android.volley.RequestQueue;
import com.bojie.materialtest.json.Endpoints;
import com.bojie.materialtest.json.Parser;
import com.bojie.materialtest.json.Requestor;
import com.bojie.materialtest.material.MyApplication;
import com.bojie.materialtest.pojo.Movie;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by bojiejiang on 5/21/15.
 */
public class MovieUtils {

    public static ArrayList<Movie> loadBoxOfficeMovie(RequestQueue requestQueue) {
        JSONObject response = Requestor.sendRequestBoxOfficeMovies(requestQueue, Endpoints.getRequestUrl(30));
        ArrayList<Movie> listMovies = Parser.parseJSONResponse(response);
        MyApplication.getWritableDatabase().insertMoviesBoxOffice(listMovies, true);
        return listMovies;
    }
}

