package com.example.android.moviemaniac.sync;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.example.android.moviemaniac.MovieAdapter;
import com.example.android.moviemaniac.R;
import com.example.android.moviemaniac.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by Tanushree on 2015-08-12.
 */
public class MovieService extends IntentService {

    private MovieAdapter movieAdapter;
    public static final String SORT_ORDER= "sort_order";
    private final String LOG_TAG = MovieService.class.getSimpleName();

    public MovieService() {
        super("MovieManiac");
    }



    @Override
    protected void onHandleIntent(Intent intent) {

        String sortOrder = intent.getStringExtra(SORT_ORDER);
        String movieJsonStr = null;


            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;


            try {
                /*
                   If the preference is highest rated movies then the base URL will contain a vote count key also
                   so that movies which have less than 70 votes are eliminated from the list

                   If the preference is popularity, base url contains only api_key
                 */
                String MOVIE_BASE_URL;
                String preferenceChange= this.getString(R.string.pref_sortOrder_highestRated);
//                if(sortOrder == getString(R.string.pref_sortOrder_highestRated)) {
//                    MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie?api_key=84604ead3481bd3bbd687f383f87e738&vote_count.gte=500&";
//                } else {
                    MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie?api_key=84604ead3481bd3bbd687f383f87e738&vote_count.gte=70&";
//                }

                // Construct the URL for The Movie Database query
                final String QUERY_PARAM = "sort_by";
                Uri uri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, sortOrder)
                        .build();

                URL url = new URL(uri.toString());
                Log.d(LOG_TAG, uri.toString());

                // Create the request to The Movie Database, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return;
                }
                movieJsonStr = buffer.toString();
                getMovieDataFromJson(movieJsonStr);

                Log.v(LOG_TAG, "Movie string: " + movieJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return;
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }

            }
            return;


    }

    /**
     * Take the String representing movie details in JSON Format in the API and
     * pull out the data
     */
    public void getMovieDataFromJson(String movieJSONStr) throws JSONException {


        //Constants for the base URL and the API keys
        final String MOVIE_BASE_URL = "http://image.tmdb.org/t/p/w154/";
        final String RESULTS = "results";
        final String POSTER_PATH = "poster_path";
        final String TITLE = "title";
        final String RELEASE_DATE = "release_date";
        final String RATING = "vote_average";
        final String OVERVIEW = "overview";
        final String ID = "id";
        final String BASE_URL = "http://api.themoviedb.org/3/movie/";
        final String MOVIE_TAILER_APPEND_URL = "/videos?api_key=84604ead3481bd3bbd687f383f87e738";
        final String MOVIE_REVIEWS_APPEND_URL = "/reviews?api_key=84604ead3481bd3bbd687f383f87e738";

        //Strings containing the data
        String movieName, urlPart, releaseDate, rating, overview, trailerLinks, reviews;

        //Movie id to insert in BASE_URL
        int id;


        try {

            JSONObject movieJson = new JSONObject(movieJSONStr);
            JSONArray movieArray = movieJson.getJSONArray(RESULTS);
            // Insert the new weather information into the database
            Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

            for (int i = 0; i < movieArray.length(); i++) {


                // Get the JSON object representing the movie
                JSONObject movie = movieArray.getJSONObject(i);


                // Get movie Data
                movieName = movie.getString(TITLE);
                urlPart = movie.getString(POSTER_PATH);
                releaseDate = movie.getString(RELEASE_DATE);
                rating = movie.getString(RATING);
                overview = movie.getString(OVERVIEW);
                id = movie.getInt(ID);
                String id_insert = Integer.toString(id);
                trailerLinks = BASE_URL + id_insert + MOVIE_TAILER_APPEND_URL;
                reviews = BASE_URL + id_insert + MOVIE_REVIEWS_APPEND_URL;

                //Store in database
                ContentValues movieValues = new ContentValues();

                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id);
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movieName);
                movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_LINK, MOVIE_BASE_URL + urlPart);
                movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
                movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, rating);
                movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
                movieValues.put(MovieContract.MovieEntry.COLUMN_TRAILER_LINKS, trailerLinks);
                movieValues.put(MovieContract.MovieEntry.COLUMN_REVIEWS, reviews);

                cVVector.add(movieValues);

            }

            int inserted = 0;


            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = this.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "MovieService Complete. " + inserted + " Inserted");

        } catch (JSONException e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
    }

    public static class AlarmReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent)
        {
            Intent sendIntent = new Intent(context, MovieService.class);
            sendIntent.putExtra(MovieService.SORT_ORDER,intent.getStringExtra(MovieService.SORT_ORDER));
            context.startService(sendIntent);
        }
    }



}
