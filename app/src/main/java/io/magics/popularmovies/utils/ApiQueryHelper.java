package io.magics.popularmovies.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import io.magics.popularmovies.R;

import static io.magics.popularmovies.utils.ApiQueryHelper.ImageSize.*;

/**
 * Utility class for the API Query and getting the values.
 * Created by Erik on 17.02.2018.
 */

public class ApiQueryHelper {
    private static final String TAG = ApiQueryHelper.class.getSimpleName();

    private static final String BASE_QUERY_API_URL = "https://api.themoviedb.org/3";
    private static final String BASE_QUERY_IMG_URL = "https://image.tmdb.org/t/p";

    private static final String MOVIE_PATH = "movie";

    private static final String POPULAR_QUERY = "popular";
    private static final String TOP_RATED_QUERY = "top_rated";

    private static final String DEFAULT_LOCALE = "en_US";

    private static final String IMAGE_SIZE_SMALL = "w92";
    private static final String IMAGE_SIZE_MEDIUM = "w342";
    private static final String IMAGE_SIZE_LARGE = "w500";
    private static final String IMAGE_SIZE_DEFAULT = "w185";

    //Builds the URLS to call the API

    public static URL buildApiQueryUrl(Context context, SortingMethod sortingMethod, int pageNumber){
        String sorting;
        switch (sortingMethod){
            case POPULAR:
                sorting = POPULAR_QUERY;
                break;
            case TOP_RATED:
                sorting = TOP_RATED_QUERY;
                break;
            default:
                sorting = POPULAR_QUERY;
                break;
        }
        /* Needed context to get the API get from resources.
         To build the app, create a XML file called api_keys.xml in the values folder
         create a string called THE_MOVIE_DB_API and put your API key there.
        */
        final String API_KEY = context.getResources().getString(R.string.THE_MOVIE_DB_API_TOKEN);
        Uri builtUri = Uri.parse(BASE_QUERY_API_URL).buildUpon()
                .appendPath(MOVIE_PATH)
                .appendPath(sorting)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("language", DEFAULT_LOCALE)
                .appendQueryParameter("page", Integer.toString(pageNumber))
                .build();

            try{
                return new URL(builtUri.toString());
            } catch (MalformedURLException e){
                Log.e(TAG, "Malformed URL", e);
            }
        return null;
    }

    public static URL buildMovieUrl(Context context, String movieId){
        final String API_KEY = context.getResources().getString(R.string.THE_MOVIE_DB_API_TOKEN);
        Uri builtUri = Uri.parse(BASE_QUERY_API_URL).buildUpon()
                .appendPath(MOVIE_PATH)
                .appendPath(movieId)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("language", DEFAULT_LOCALE)
                .build();
        try {
            return new URL(builtUri.toString());
        }catch (MalformedURLException e){
            Log.e(TAG, "malformed Movie details URL", e);
        }
        return null;
    }

    public static String buildImageUrl(String posterPath, ImageSize imageSize){
        String requestedImgSize;
        switch (imageSize){
            case SIZE_LARGE:
                requestedImgSize = IMAGE_SIZE_LARGE;
                break;
            case SIZE_MEDIUM:
                requestedImgSize = IMAGE_SIZE_MEDIUM;
                break;
            case SIZE_SMALL:
                requestedImgSize = IMAGE_SIZE_SMALL;
                break;
            default:
                requestedImgSize = IMAGE_SIZE_DEFAULT;
                break;
        }

        Uri builtUri = Uri.parse(BASE_QUERY_IMG_URL).buildUpon()
                .appendPath(requestedImgSize)
                .appendEncodedPath(posterPath)
                .build();

        return builtUri.toString();
    }

    public static String doQuery(URL url) throws IOException{
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

        try {
            Scanner scanner = new Scanner(urlConnection.getInputStream());
            scanner.useDelimiter("\\A");

            if (scanner.hasNext()){
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
    /* Gets phones screen size to figure the optimal ImageSize to get from the API
       In case of a future ImageSize setting I'm implementing a method instead of resolving it directly in the parser.
     */
    public static ImageSize getOptimalImgSize(Context context){
        float density = context.getResources().getDisplayMetrics().density;
        if (density >= 3.0) return SIZE_MEDIUM;
        else return SIZE_DEFAULT;
    }

    public enum ImageSize{
        SIZE_LARGE,
        SIZE_MEDIUM,
        SIZE_SMALL,
        SIZE_DEFAULT
    }

    public enum SortingMethod{
        POPULAR,
        TOP_RATED
    }

}
