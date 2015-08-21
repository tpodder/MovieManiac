package com.example.android.moviemaniac;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.moviemaniac.data.MovieContract;
import com.example.android.moviemaniac.sync.MovieSyncAdapter;



/* http://maciejpasynkiewicz.com/?p=79
   Encapsulates fetching Movie Data and displaying it in a Grid View format
 */

public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MovieFragment.class.getSimpleName();

    private static final int MOVIE_LOADER= 0;
    private static final String SELECTED_KEY = "selected_position";

    //Declaring variables for the movie adapter and gridview
    private MovieAdapter movieAdapter;
    GridView gridView;

    private int mPosition=gridView.INVALID_POSITION;



    private static final String[] MOVIE_COLUMNS ={
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER_LINK,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_TRAILER_LINKS,
            MovieContract.MovieEntry.COLUMN_REVIEWS
    };

    private static final String[] FAVORITE_COLUMNS ={
            MovieContract.MovieEntry._ID,
            MovieContract.MovieFavoriteEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieFavoriteEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieFavoriteEntry.COLUMN_POSTER_LINK,
            MovieContract.MovieFavoriteEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieFavoriteEntry.COLUMN_RATING,
            MovieContract.MovieFavoriteEntry.COLUMN_OVERVIEW,
            MovieContract.MovieFavoriteEntry.COLUMN_TRAILER_LINKS,
            MovieContract.MovieFavoriteEntry.COLUMN_REVIEWS
    };

    static final int COLUMN_ID=0;
    static final int COLUMN_MOVIE_ID = 1;
    static final int COLUMN_MOVIE_TITLE = 2;
    static final int COLUMN_POSTER_LINK = 3;
    static final int COLUMN_RELEASE_DATE = 4;
    static final int COLUMN_RATING = 5;
    static final int COLUMN_OVERVIEW = 6;
    static final int COLUMN_TRAILER_LINKS = 7;
    static final int COLUMN_REVIEWS = 8;


    public MovieFragment() {}

    public interface Callback{
        public void onItemSelected(Uri uri);
    }

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
        //Action performed by refresh button in the menu
        if (id == R.id.action_refresh) {
            updateMovie();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Get cursor and Initialize adapter, get a reference to GridView, set adapter to ot and update data
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        movieAdapter = new MovieAdapter(getActivity(),null,0);
        gridView = (GridView) rootView.findViewById(R.id.gridview);
        gridView.setAdapter(movieAdapter);

         // The CursorAdapter will take data from our cursor and populate the ListView
        // However, we cannot use FLAG_AUTO_REQUERY since it is deprecated, so we will end
        // up with an empty list the first time we run.

        // We'll call our MainActivity

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                Log.v(LOG_TAG, "Position=" + position);
                String sortOrder=Utility.getPreferredSortOrder(getActivity());
                if (cursor != null) {
                    if (getString(R.string.pref_sortOrder_favorite).equals(sortOrder)) {
                        ((Callback) getActivity()).onItemSelected(
                                MovieContract.MovieFavoriteEntry.buildMovieUriWithMovieID
                                        (cursor.getString(COLUMN_MOVIE_ID)));
                    } else {
                        ((Callback) getActivity()).onItemSelected(
                                MovieContract.MovieEntry.buildMovieUriWithMovieID
                                        (cursor.getString(COLUMN_MOVIE_ID)));
                    }

                }

                mPosition = position;

            }
        });

            if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
                    // The listview probably hasn't even been populated yet.  Actually perform the
                    // swapout in onLoadFinished.
                    mPosition = savedInstanceState.getInt(SELECTED_KEY);
            }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    void onSortOrderChanged( ) {
        String sortOrder=Utility.getPreferredSortOrder(getActivity());
        if(!getString(R.string.pref_sortOrder_favorite).equals(sortOrder)) {
            updateMovie();
            movieAdapter.notifyDataSetChanged();
        }
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);


     }

    //Executes FetchMovieTask
    private void updateMovie() {
        MovieSyncAdapter.syncImmediately(getActivity());
   }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String sortOrder=Utility.getPreferredSortOrder(getActivity());

        if(getString(R.string.pref_sortOrder_favorite).equals(sortOrder))
        {
            Uri uri = MovieContract.MovieFavoriteEntry.CONTENT_URI;
            return new CursorLoader(getActivity(),
                    uri,
                    FAVORITE_COLUMNS,
                    null,
                    null,
                    null);

        }else {
            Uri uri = MovieContract.MovieEntry.CONTENT_URI;
            return new CursorLoader(getActivity(),
                    uri,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        // When tablets rotate, then currently selected item needs to be saved.
        // When no item is selected, mPosition will be set to Gridview.INVALID_POSITION,
        // so check for that before storing.
        if(mPosition != GridView.INVALID_POSITION)
        {
            outState.putInt(SELECTED_KEY,mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        movieAdapter.swapCursor(cursor);
//        gridView.setAdapter(movieAdapter);
        if (mPosition != GridView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            gridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        movieAdapter.swapCursor(null);

    }

}



