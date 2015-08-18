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
    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 540 = 9 hours
     public static final int SYNC_INTERVAL = 60 * 540;
     public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    ArrayList<URL> urls = new ArrayList<URL>();


    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");
        String sortOrder = Utility.getPreferredSortOrder(getContext());
        String movieJsonStr = null;
        String reviewJsonStr= null;

//
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
//        HttpURLConnection urlConnection = null;
//        BufferedReader reader = null;

//        for( int k=0; k<1; k++)
        urls.clear();
        int k=0;

            URL urlMain;
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {/*
                   If the preference is highest rated movies then the base URL will contain a vote count key also
                   so that movies which have less than 70 votes are eliminated from the list

                   If the preference is popularity, base url contains only api_key
                 */
                    String MOVIE_BASE_URL;
//            String preferenceChange= this.getString(R.string.pref_sortOrder_highestRated);
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
                    urlMain= url;
                    Log.d(LOG_TAG, uri.toString());
                    urls.add(url);
                Log.v(LOG_TAG, "Outside for loop size " + urls.size());
                int max=21;
                for (int i = 0; i < max ; i++) {
                    Log.v(LOG_TAG, "Size of urls array list " + urls.size() + "number of time for loop executed"+ i);
                     //Create the request to The Movie Database, and open the connection
                    if(k==0)
                    {
                        urlConnection = (HttpURLConnection)urlMain.openConnection();
                    }else if(k==1)
                    {
                        urlConnection=(HttpURLConnection)urls.get(i).openConnection();
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

                    if (k == 1 ) {
                        reviewJsonStr = buffer.toString();
                        getReviewDataFromJson(reviewJsonStr);
                        Log.v(LOG_TAG, "Review: " + reviewJsonStr + " Value of k: " + k);
                    } else if (k == 0) {
                        movieJsonStr = buffer.toString();
                        getMovieDataFromJson(movieJsonStr);
                        Log.v(LOG_TAG, "Movie string: " + movieJsonStr);
                        k++;
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
                    urls.add(url);
                }catch (MalformedURLException e){
                    Log.e(LOG_TAG, "Malformed URL", e);
                }
//                FetchReviewTask reviewTask = new FetchReviewTask(getContext());
//                reviewTask.execute(reviews);

                cVVector.add(movieValues);

            }

            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,null,null);
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
            for (int i = 0; i < reviewArray.length(); i++) {

                // Get the JSON object representing the day
                JSONObject review = reviewArray.getJSONObject(i);

                //Get review data
                authorName = review.getString(AUTHOR);
                content = review.getString(CONTENT);

                //Store in database
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
                getContext().getContentResolver().delete(MovieContract.MovieReviewsEntry.CONTENT_URI, null, null);
                Log.d(LOG_TAG, "Reviews deleted from table");
                getContext().getContentResolver().bulkInsert(MovieContract.MovieReviewsEntry.CONTENT_URI, cvArray);
                Log.d(LOG_TAG, "Reviews added to table");
            }

            Log.d(LOG_TAG, "FetchReviewTask Complete.  Inserted");

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
