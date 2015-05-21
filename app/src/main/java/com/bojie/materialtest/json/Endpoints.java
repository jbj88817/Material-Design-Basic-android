package com.bojie.materialtest.json;

import com.bojie.materialtest.material.MyApplication;
import static com.bojie.materialtest.extras.UriEndpoints.*;

/**
 * Created by bojiejiang on 5/21/15.
 */
public class Endpoints {

    public static String getRequestUrl(int limit) {

        return URL_BOX_OFFICE
                + URL_CHAR_QUESTION
                + URL_PARAM_API_KEY + MyApplication.API_KEY_ROTTEN_TOMATOES
                + URL_CHAR_AMEPERSAND
                + URL_PARAM_LIMIT + limit;
    }
}
