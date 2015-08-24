package com.example.android.moviemaniac.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.android.moviemaniac.R;
import com.example.android.moviemaniac.Utility;
import com.example.android.moviemaniac.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Tanushree on 2015-08-12.
 */
public class MovieSyncAdapter extends AbstractThreadedSyncAdapter{
    public final String LOG_TAG = MovieSyncAdapter.class.getSimpleName();
    // Interval at which to sync with the movie DB, in seconds.
    // 60 seconds (1 minute) * 540 = 9 hours
     public static final int SYNC_INTERVAL = 60 * 540;
     public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    ArrayList<URL> urlsReviews = new ArrayList<URL>();
    ArrayList<URL> urlsTrailers = new ArrayList<URL>();

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");

            String sortOrder = Utility.getPreferredSortOrder(getContext());
            String movieJsonStr = null;
            String reviewJsonStr= null;
            String trailerJsonStr= null;

            //Clear the urls stored during the previous Sync
            urlsReviews.clear();
            urlsTrailers.clear();

            URL urlMain;
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                    String MOVIE_BASE_URL =
                            "http://api.themoviedb.org/3/discover/movie?api_key=####&vote_count.gte=70&";

                    // Construct the URL for The Movie Database query
                    final String QUERY_PARAM = "sort_by";
                    Uri uri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                            .appendQueryParameter(QUERY_PARAM, sortOrder)
                            .build();

                    URL url = new URL(uri.toString());
                    urlMain= url;
                    Log.d(LOG_TAG, uri.toString());
                    //Maximum number of movies requested from API is 20*2 + 1 for the main url
                    int max=41;

                    // Opens the Main URL first to load the movie details and get urls for
                    // reviews and trailers and then opens the urls stored in the array lists
                    // urlReviews and urlTrailers
                    for (int i = 0; i < max ; ) {

                        //Create the request to The Movie Database, and open the connection
                        if(i==0)
                        {
                            urlConnection = (HttpURLConnection)urlMain.openConnection();
                            Log.v(LOG_TAG, "Main URL loaded");

                        }else if(i>=1 && i<21)
                        {
                            urlConnection=(HttpURLConnection)urlsReviews.get(i-1).openConnection();
                            Log.v(LOG_TAG, "Reviews opened");

                        }else if(i>=21)
                        {
                            urlConnection=(HttpURLConnection)urlsTrailers.get(i-21).openConnection();
                            Log.v(LOG_TAG, "Trailers opened");
                        }

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

                        //Load the movies table first then the the reviews
                        if (i == 0) {

                            movieJsonStr = buffer.toString();
                            getMovieDataFromJson(movieJsonStr);
                            Log.v(LOG_TAG, "Movie string: " + movieJsonStr);
                            int test1= urlsReviews.size();
                            int test2= urlsTrailers.size();
                            Log.v(LOG_TAG, "URL REVIEWS "+test1);
                            Log.v(LOG_TAG, "URL TRAILERS "+test2);
                            i++;
                        }else if (i >= 1 && i<21) {

                            reviewJsonStr = buffer.toString();
                            getReviewDataFromJson(reviewJsonStr);
                            Log.v(LOG_TAG, "Review: " + reviewJsonStr);
                            i++;
                        }  else if (i >= 21 ) {

                            trailerJsonStr = buffer.toString();
                            getTrailerDataFromJson(trailerJsonStr);
                            Log.v(LOG_TAG, "Trailer: " + trailerJsonStr);
                            i++;
                        }



                    }
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
        final String MOVIE_TAILER_APPEND_URL = "/videos?api_key=####";
        final String MOVIE_REVIEWS_APPEND_URL = "/reviews?api_key=####;

        //Strings containing the data
        String movieName, urlPart, releaseDate, rating, overview, trailerLinks, reviews;

        //Movie id to insert in BASE_URL
        int id;


        try {

            JSONObject movieJson = new JSONObject(movieJSONStr);
            JSONArray movieArray = movieJson.getJSONArray(RESULTS);
            // Insert the new weather information into the database
            Vector<ContentValues> cVVector;

            cVVector= new Vector<ContentValues>(movieArray.length());

            cVVector.clear();

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


                try {
                    Uri uri = Uri.parse(reviews).buildUpon()
                            .build();

                    URL url = new URL(uri.toString());
//                    urls.add(url);
                    urlsReviews.add(url);
                }catch (MalformedURLException e){
                    Log.e(LOG_TAG, "Malformed URL", e);
                }
                try {
                    Uri uri = Uri.parse(trailerLinks).buildUpon()
                            .build();

                    URL url = new URL(uri.toString());
//                    urls.add(url);
                    urlsTrailers.add(url);
                }catch (MalformedURLException e){
                    Log.e(LOG_TAG, "Malformed URL", e);
                }
                cVVector.add(movieValues);

            }

            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                //delete data stored in previous sync
                getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, null, null);
                getContext().getContentResolver().delete(MovieContract.MovieReviewsEntry.CONTENT_URI, null, null);
                getContext().getContentResolver().delete(MovieContract.MovieTrailerEntry.CONTENT_URI, null, null);

                getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "MovieService Complete. Inserted");

        } catch (JSONException e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
    }

    private void getReviewDataFromJson(String reviewJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String RESULTS = "results";
        final String AUTHOR = "author";
        final String CONTENT = "content";
        final String MOVIE_ID = "id";

        String authorName, content;
        int id;

        try {

            JSONObject reviewJson = new JSONObject(reviewJsonStr);
            JSONArray reviewArray = reviewJson.getJSONArray(RESULTS);
            id = reviewJson.getInt(MOVIE_ID);

            // Insert the new review information into the database
            Vector<ContentValues> cVVector = new Vector<ContentValues>(reviewArray.length());
            cVVector.clear();

            // If there are no reviews for a movie, then add a no reviews message
            if(reviewArray.length()>0)
            {
                for (int i = 0; i < reviewArray.length(); i++) {

                    // Get the JSON object representing the day
                    JSONObject review = reviewArray.getJSONObject(i);

                    //Get review data
                    authorName = review.getString(AUTHOR)+":";
                    content = review.getString(CONTENT);

                    //Store in database
                    ContentValues reviewValues = new ContentValues();

                    reviewValues.put(MovieContract.MovieReviewsEntry.COLUMN_MOVIE_ID, id);
                    reviewValues.put(MovieContract.MovieReviewsEntry.COLUMN_AUTHOR, authorName);
                    reviewValues.put(MovieContract.MovieReviewsEntry.COLUMN_CONTENT, content);

                    cVVector.add(reviewValues);


                }
            } else if(reviewArray.length()==0){

                authorName = "";
                content = "Sorry, there are no reviews for this movie.";

                ContentValues reviewValues = new ContentValues();

                reviewValues.put(MovieContract.MovieReviewsEntry.COLUMN_MOVIE_ID, id);
                reviewValues.put(MovieContract.MovieReviewsEntry.COLUMN_AUTHOR, authorName);
                reviewValues.put(MovieContract.MovieReviewsEntry.COLUMN_CONTENT, content);

                cVVector.add(reviewValues);

            }

            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                getContext().getContentResolver().bulkInsert(MovieContract.MovieReviewsEntry.CONTENT_URI, cvArray);
                Log.d(LOG_TAG, "Reviews added to table");
            }

            Log.d(LOG_TAG, "FetchReviewTask Complete.  Inserted");

        }catch (JSONException e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
    }

    private void getTrailerDataFromJson(String trailerJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String RESULTS = "results";
        final String KEY = "key";
        final String NAME = "name";
        final String MOVIE_ID = "id";

        String key, name;
        int id;

        try {

            JSONObject trailerJson = new JSONObject(trailerJsonStr);
            JSONArray trailerArray = trailerJson.getJSONArray(RESULTS);
            id = trailerJson.getInt(MOVIE_ID);

            // Insert the new trailer information into the database
            Vector<ContentValues> cVVector = new Vector<ContentValues>(trailerArray.length());
            cVVector.clear();
            // If there are no trailers for a movie, then add a no trailers message
            if(trailerArray.length()>0) {
                for (int i = 0; i < trailerArray.length(); i++) {

                    // Get the JSON object representing the day
                    JSONObject trailer = trailerArray.getJSONObject(i);

                    //Get trailer data
                    key = trailer.getString(KEY);
                    name = trailer.getString(NAME);

                    //Store in database
                    ContentValues trailerValues = new ContentValues();

                    trailerValues.put(MovieContract.MovieTrailerEntry.COLUMN_MOVIE_ID, id);
                    trailerValues.put(MovieContract.MovieTrailerEntry.COLUMN_KEY, key);
                    trailerValues.put(MovieContract.MovieTrailerEntry.COLUMN_NAME, name);

                    cVVector.add(trailerValues);


                }
            }else if(trailerArray.length()==0)
            {
                name = "Sorry, there are no trailers.";
                key = "";

                ContentValues trailerValues = new ContentValues();

                trailerValues.put(MovieContract.MovieTrailerEntry.COLUMN_MOVIE_ID, id);
                trailerValues.put(MovieContract.MovieTrailerEntry.COLUMN_KEY, key);
                trailerValues.put(MovieContract.MovieTrailerEntry.COLUMN_NAME, name);


                cVVector.add(trailerValues);
            }

            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                getContext().getContentResolver().bulkInsert(MovieContract.MovieTrailerEntry.CONTENT_URI, cvArray);
                Log.d(LOG_TAG, "Trailers added to table");
            }

            Log.d(LOG_TAG, "Trailers inserted");

        }catch (JSONException e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
    }



    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */

        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
