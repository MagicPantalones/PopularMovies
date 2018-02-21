package io.magics.popularmovies.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.magics.popularmovies.models.Movie;
import io.magics.popularmovies.models.MovieForGrid;


/**
 * MovieDB JSON parser
 * Created by Erik on 17.02.2018.
 */

public class MovieJsonParser {

    private static final String TAG = MovieJsonParser.class.getSimpleName();

    private static final String RESULTS = "results";
    private static final String POSTER_PATH = "poster_path";
    private static final String TITLE = "title";
    private static final String MOVIE_ID = "id";
    private static final String OVERVIEW = "overview";
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String VOTE_COUNT = "vote_count";
    private static final String RELEASE_DATE = "release_date";

    private static final String STATUS_CODE = "status_code";
    private static final String STATUS_MESSAGE = "status_message";

    private MovieJsonParser(){}

    /** Parses the Json response for the movie list.
     *  Why it takes an Activity param is explained at {@link MovieJsonParser#isSuccess(JSONObject, Activity)}
     */
    public static List<MovieForGrid> parseForGridView(String json, Activity activity) throws JSONException{

        JSONObject movieJson = new JSONObject(json);
        MovieForGrid[] moviesForGrid;

        if (!isSuccess(movieJson, activity)){
            return null;
        }
        if (!movieJson.has(RESULTS)){
            Log.d(TAG, "parseForGridView: JSON did not return result.");
            return null;
        }

        JSONArray rawMovieArray = movieJson.optJSONArray(RESULTS);

        moviesForGrid = new MovieForGrid[rawMovieArray.length()];

        for (int i = 0; i < rawMovieArray.length(); i++){
            JSONObject rawMovieData = rawMovieArray.optJSONObject(i);
            if (!rawMovieData.has(POSTER_PATH) || !rawMovieData.has(TITLE)){
                continue;
            }
            String posterPath = rawMovieData.optString(POSTER_PATH);
            String originalTitle = rawMovieData.optString(TITLE);
            String movieId = rawMovieData.optString(MOVIE_ID);

            moviesForGrid[i] = new MovieForGrid(posterPath, originalTitle, movieId);

        }

        return new ArrayList<>(Arrays.asList(moviesForGrid));
    }

    //Parses the JSON response from a movieId query.
    public static Movie parseMovieDetails(String json, Activity activity) throws JSONException{
        String posterPath;
        String title;
        String overview;
        String voteAverage;
        String voteCount;
        String releaseDate;

        JSONObject movieDetails = new JSONObject(json);
        if (!isSuccess(movieDetails, activity)){
            return null;
        }

        posterPath = movieDetails.optString(POSTER_PATH);
        title = movieDetails.optString(TITLE);
        overview = movieDetails.optString(OVERVIEW);
        voteAverage = Double.toString(movieDetails.optDouble(VOTE_AVERAGE));
        voteCount = Integer.toString(movieDetails.optInt(VOTE_COUNT));
        releaseDate = formatDate(movieDetails.optString(RELEASE_DATE));

        return new Movie(posterPath, title, overview, voteAverage, voteCount, releaseDate);
    }

    /* Checks for an error response in the JSON.
     * Calls the UIThread runnable from the activity and queues a toast if the server gave an error response.
     */
    private static Boolean isSuccess(final JSONObject jsonObject, final Activity activity){

        if (jsonObject.has(STATUS_CODE)){
            Log.d(TAG, "Status code: " + jsonObject.optString(STATUS_CODE) + " " + jsonObject.optString(STATUS_MESSAGE));
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "Error: " + jsonObject.optString(STATUS_CODE) + " " + jsonObject.optString(STATUS_MESSAGE), Toast.LENGTH_SHORT).show();
                }
            });
            return false;
        }
        return true;
    }

    //Formats date to "MMM dd(ordinal number), yyyy

    public static String formatDate(String dateString){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        SimpleDateFormat sdf1 = new SimpleDateFormat("MMMM", Locale.ENGLISH);
        String[] dateSplit = dateString.split("-");

        try{
            Date month = sdf.parse(dateString);
            String day = dateSplit[2] + getOrdinal(dateSplit[2]);

            return sdf1.format(month) + " " + day + ", " + dateSplit[0];
        } catch (ParseException e){
            Log.e(TAG, "formatDate: ", e);
            return dateString;
        }

    }

    /*copied way to format the correct ordinal from Greg Mattes answer on
    https://stackoverflow.com/questions/4011075/how-do-you-format-the-day-of-the-month-to-say-11th-21st-or-23rd-ordinal
    */

    private static String getOrdinal(String dayString){
        int dayInt = Integer.parseInt(dayString);
        if (dayInt >= 11 && dayInt <= 13){
            return "th";
        }
        switch (dayInt % 10){
            case 1: return "st";
            case 2: return "nd";
            case 3: return "rd";
            default: return "th";
        }
    }
}
