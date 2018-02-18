package io.magics.popularmovies.utils;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import io.magics.popularmovies.R;

/**
 * Utility class for the API Query and getting the values.
 * Created by Erik on 17.02.2018.
 */

public class ApiQueryHelper {
    public static final String BASE_QUERY_API_URL = "https://api.themoviedb.org/3";
    public static final String BASE_QUERY_IMG_URL = "https://image.tmdb.org/t/p";

    public static final String MOVIE_QUERY = "movie";
    public static final String GENRE_LIST_QUERY = "genre";
    public static final String LIST_QUERY = "list";

    public static final String POPULAR_QUERY = "popular";
    public static final String TOP_RATED_QUERY = "top_rated";

    public static final String DEFAULT_LOCALE = "en_US";

    private static final String IMAGE_SIZE_SMALL = "w92";
    private static final String IMAGE_SIZE_MEDIUM = "w342";
    private static final String IMAGE_SIZE_LARGE = "w500";
    private static final String IMAGE_SIZE_DEFAULT = "w185";

    /**
     *
     * @param sortingQuery The sorting configuration. This must be one of ApiQueryHelper.POPULAR_QUERY or ApiQueryHelper.TOP_RATED_QUERY
     * @return Url to query the TheMovieDB.org api
     * @throws IllegalArgumentException if the wrong query option has been used.
     */
    public static URL buildApiQueryUrl(Context context, String sortingQuery, int pageNumber){
        if (sortingQuery.equals(POPULAR_QUERY) || sortingQuery.equals(TOP_RATED_QUERY)){
            final String API_KEY = context.getResources().getString(R.string.THE_MOVIE_DB_API_TOKEN);
            Uri builtUri = Uri.parse(BASE_QUERY_API_URL).buildUpon()
                    .appendPath(MOVIE_QUERY)
                    .appendPath(sortingQuery)
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("language", DEFAULT_LOCALE)
                    .appendQueryParameter("page", Integer.toString(pageNumber))
                    .build();

            try{
                return new URL(builtUri.toString());
            } catch (MalformedURLException e){
                e.printStackTrace();
            }
        } else{
            throw new IllegalArgumentException();
        }
        return null;
    }

    private static URL buildGenreListQuery(Context context){
        final String API_KEY = context.getResources().getString(R.string.THE_MOVIE_DB_API_TOKEN);
        Uri builtUri = Uri.parse(BASE_QUERY_API_URL).buildUpon()
                .appendPath(GENRE_LIST_QUERY)
                .appendPath(MOVIE_QUERY)
                .appendPath(LIST_QUERY)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("language", DEFAULT_LOCALE)
                .build();
        try {
            return new URL(builtUri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
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
            case SIZE_DEFAULT:
                requestedImgSize = IMAGE_SIZE_DEFAULT;
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
