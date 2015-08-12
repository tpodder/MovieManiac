package com.example.android.moviemaniac.AsyncTasks;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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
public class FetchReviewTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchReviewTask.class.getSimpleName();
    Context context;

    public FetchReviewTask(Context c) {
        context = c;

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

            int inserted = 0;

            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = context.getContentResolver().bulkInsert(MovieContract.MovieReviewsEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchReviewTask Complete. " + inserted + " Inserted");

        }catch (JSONException e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        // Will contain the raw JSON response as a string.
        String reviewJsonStr = null;


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
            String REVIEW_BASE_URL= params[0];
            // Construct the URL for The Movie Database query
            Uri uri = Uri.parse(REVIEW_BASE_URL).buildUpon().build();

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
                return null;
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
                return null;
            }
            reviewJsonStr = buffer.toString();
            getReviewDataFromJson(reviewJsonStr);

            Log.v(LOG_TAG, "Review string: " + reviewJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
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
        return null;

    }


}
