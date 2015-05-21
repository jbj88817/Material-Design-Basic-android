package com.bojie.materialtest.json;

import org.json.JSONObject;

/**
 * Created by bojiejiang on 5/21/15.
 */
public class Utils {

    public static boolean contains(JSONObject jsonObject, String key) {
        return jsonObject != null && jsonObject.has(key) && !jsonObject.isNull(key);
    }
}
