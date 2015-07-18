package com.example.android.moviemaniac;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MovieFragment extends Fragment {

    private MovieAdapter movieAdapter;
    GridView gridview;
    Movie[] testMovies = {
            new Movie("Jurassic World", R.drawable.jw),
            new Movie("Terminator Genesis", R.drawable.tg),
            new Movie("Minions", R.drawable.m)
    };

    public MovieFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            FetchMovieTask movieTask = new FetchMovieTask();
            movieTask.execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        movieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());
        gridview = (GridView) rootView.findViewById(R.id.gridview);
        gridview.setAdapter(movieAdapter);
        String url = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=84604ead3481bd3bbd687f383f87e738";
        FetchMovieTask movieTask = new FetchMovieTask();
        //Pass parameters
        movieTask.execute(url);
        return rootView;

    }


    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<Movie>> {


        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        public ArrayList<Movie> getMovieDataFromJson(String movieJSONStr) throws JSONException {

            ArrayList<Movie> movieArr = new ArrayList<>();
            ArrayList<String> moviePosters= new ArrayList<>();
            ArrayList<String> movieTitles= new ArrayList<>();
            final String MOVIE_BASE_URL = "http://image.tmdb.org/t/p/w92/";
            final String RESULTS = "results";
            final String POSTER_PATH = "poster_path";
            final String TITLE = "title";
            String movieName;
            String urlPart;
            JSONObject movieJson = new JSONObject(movieJSONStr);
            JSONArray movieArray = movieJson.getJSONArray(RESULTS);
            try {
                for (int i = 0; i < movieArray.length(); i++) {


                    // Get the JSON object representing the movie
                    JSONObject movie = movieArray.getJSONObject(i);

                    // Movie Name
                    movieName = movie.getString(TITLE);

                    urlPart = movie.getString(POSTER_PATH);

                    movieTitles.add( movieName );
                    moviePosters.add( MOVIE_BASE_URL + urlPart );
                    movieArr.add( new Movie(movieTitles.get(i), moviePosters.get(i)));


                }
            } catch (JSONException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            return movieArr;
        }


        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;
            String sort_by = "popularity.desc";
            String ampersand = "&";

            try {

                /*final String FORECAST_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?api_key=84604ead3481bd3bbd687f383f87e738&sort_by=popularity.desc";
                final String QUERY_PARAM = "sort_by";
               */

                URL url = new URL("http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=84604ead3481bd3bbd687f383f87e738");

                // Create the request to OpenWeatherMap, and open the connection
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
                movieJsonStr = buffer.toString();

                Log.v(LOG_TAG, "Movie string: " + movieJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
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

                try {
                    return getMovieDataFromJson(movieJsonStr);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(ArrayList<Movie> movies) /*throws JSONException*/ {

                movieAdapter.clear();
                for (Movie movie : movies) {
                    movieAdapter.add(movie);
                }
               Log.d(LOG_TAG, "movielist updated");

        }
    }

}

