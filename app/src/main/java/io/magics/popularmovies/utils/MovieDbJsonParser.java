package io.magics.popularmovies.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.magics.popularmovies.models.MovieForGrid;


/**
 * MovieDB parser
 * Created by Erik on 17.02.2018.
 */

public class MovieDbJsonParser {

    private static final String TAG = "MovieDbJsonParser.class";

    public static MovieForGrid[] parseMovieListForGridView(String json) throws JSONException{

        final String RESULTS = "results";
        final String POSTER_PATH = "poster_path";
        final String ORIGINAL_TITLE = "original_title";
        final String STATUS_CODE = "status_code";
        final String STATUS_MESSAGE = "status_message";

        JSONObject movieJson = new JSONObject(json);
        MovieForGrid[] movieForGrids = null;

        if (movieJson.has(STATUS_CODE)){
            Log.e(TAG, "Status code: " + movieJson.optInt(STATUS_CODE) + ". Status Message: " + movieJson.optString(STATUS_MESSAGE));
            return movieForGrids;
        }

        if (!movieJson.has(RESULTS)){
            throw new JSONException("Invalid JSON data");
        }

        JSONArray rawMovieArray = movieJson.getJSONArray(RESULTS);

        if (rawMovieArray.length() == 0){
            throw new JSONException("Array from \"result\" key is empty");
        }

        for (int i = 0; i < rawMovieArray.length(); i++){
            JSONObject rawMovieData = rawMovieArray.getJSONObject(i);
            if (!rawMovieData.has(POSTER_PATH) || !rawMovieData.has(ORIGINAL_TITLE)){
                continue;
            }
            String posterPath = rawMovieData.optString(POSTER_PATH);
            String originalTitle = rawMovieData.optString(ORIGINAL_TITLE);

            movieForGrids[i] = new MovieForGrid(posterPath, originalTitle);

        }

        return movieForGrids;
    }



}
