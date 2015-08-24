package com.example.android.moviemaniac;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.moviemaniac.data.MovieContract;
import com.example.android.moviemaniac.data.MovieDbHelper;
import com.squareup.picasso.Picasso;

/**
 * Created by Tanushree on 2015-07-27.
 *
 * Detail Fragment Class
 *
 */
public  class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    static final String DETAIL_URI = "URI";

    private Uri mUri;
    private static final int DETAIL_LOADER=0;

    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;

    //Refrence to the cursor containing details.
    // Used to ontain Movie ID to insert in Favorites table.
    private Cursor mCursor;

    private ShareActionProvider mShareActionProvider;
    private String shareKey;

    // Projections for Movies, Movie joined with Review, Movie joined with Trailer, Favorite,
    // Favorite joined with FavoriteReview and Favorite joined with FavoriteTrailers Table
    private static final String[] MOVIE_COLUMNS ={
            MovieContract.MovieEntry.TABLE_NAME+"."+MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER_LINK,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_TRAILER_LINKS,
            MovieContract.MovieEntry.COLUMN_REVIEWS,

    };

    private static final String[] REVIEW_COLUMNS ={
            MovieContract.MovieEntry.TABLE_NAME+"."+MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER_LINK,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_TRAILER_LINKS,
            MovieContract.MovieEntry.COLUMN_REVIEWS,

            MovieContract.FavoriteReviewsEntry.COLUMN_AUTHOR,
            MovieContract.FavoriteReviewsEntry.COLUMN_CONTENT,

    };

    private static final String[] TRAILER_COLUMNS ={
            MovieContract.MovieEntry.TABLE_NAME+"."+MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER_LINK,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_TRAILER_LINKS,
            MovieContract.MovieEntry.COLUMN_REVIEWS,

            MovieContract.FavoriteTrailerEntry.COLUMN_NAME,
            MovieContract.FavoriteTrailerEntry.COLUMN_KEY,

    };

    private static final String[] FAVORITE_COLUMNS ={
            MovieContract.MovieFavoriteEntry.TABLE_NAME+"."+MovieContract.MovieEntry._ID,
            MovieContract.MovieFavoriteEntry.TABLE_NAME+"."+
                    MovieContract.MovieFavoriteEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieFavoriteEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieFavoriteEntry.COLUMN_POSTER_LINK,
            MovieContract.MovieFavoriteEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieFavoriteEntry.COLUMN_RATING,
            MovieContract.MovieFavoriteEntry.COLUMN_OVERVIEW,
            MovieContract.MovieFavoriteEntry.COLUMN_TRAILER_LINKS,
            MovieContract.MovieFavoriteEntry.COLUMN_REVIEWS,

    };

    private static final String[] FAVORITE_REVIEW_COLUMNS ={
            MovieContract.MovieFavoriteEntry.TABLE_NAME+"."+MovieContract.MovieEntry._ID,
            MovieContract.MovieFavoriteEntry.TABLE_NAME+"."+
                    MovieContract.MovieFavoriteEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieFavoriteEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieFavoriteEntry.COLUMN_POSTER_LINK,
            MovieContract.MovieFavoriteEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieFavoriteEntry.COLUMN_RATING,
            MovieContract.MovieFavoriteEntry.COLUMN_OVERVIEW,
            MovieContract.MovieFavoriteEntry.COLUMN_TRAILER_LINKS,
            MovieContract.MovieFavoriteEntry.COLUMN_REVIEWS,

            MovieContract.MovieReviewsEntry.COLUMN_AUTHOR,
            MovieContract.MovieReviewsEntry.COLUMN_CONTENT,

    };

    private static final String[] FAVORITE_TRAILER_COLUMNS ={
            MovieContract.MovieFavoriteEntry.TABLE_NAME+"."+MovieContract.MovieEntry._ID,

            MovieContract.MovieFavoriteEntry.TABLE_NAME+"."+MovieContract.MovieFavoriteEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieFavoriteEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieFavoriteEntry.COLUMN_POSTER_LINK,
            MovieContract.MovieFavoriteEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieFavoriteEntry.COLUMN_RATING,
            MovieContract.MovieFavoriteEntry.COLUMN_OVERVIEW,
            MovieContract.MovieFavoriteEntry.COLUMN_TRAILER_LINKS,
            MovieContract.MovieFavoriteEntry.COLUMN_REVIEWS,

            MovieContract.MovieTrailerEntry.COLUMN_NAME,
            MovieContract.MovieTrailerEntry.COLUMN_KEY
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

    static final int COLUMN_AUTHOR= 9;
    static final int COLUMN_CONTENT= 10;

    static final int COLUMN_KEY= 10;

    //Views
    private TextView textTitle,textRD,textRating,textOverview;
    private ImageView imgView;
    private ListView videoList, reviewList;
    private ImageButton favorite;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }


        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        textTitle=(TextView)rootView.findViewById(R.id.detail_text);
        imgView = (ImageView)rootView.findViewById(R.id.detail_img);
        textRD=(TextView)rootView.findViewById(R.id.detail_releaseDate);
        textRating=(TextView)rootView.findViewById(R.id.detail_rating);
        textOverview=(TextView)rootView.findViewById(R.id.detail_overview);

        //trailers
        trailerAdapter = new TrailerAdapter(getActivity(),null,0);
        videoList=(ListView)rootView.findViewById(R.id.list_trailer);

        //Reviews
        reviewAdapter = new ReviewAdapter(getActivity(),null,0);
        reviewList=(ListView)rootView.findViewById(R.id.list_reviews);

        favorite=(ImageButton)rootView.findViewById(R.id.favorite);

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        menu.clear();
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);
        inflater.inflate(R.menu.detail, menu);
        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (shareKey != null) {
            mShareActionProvider.setShareIntent(createShareVideoIntent());
        }
    }

    private Intent createShareVideoIntent() {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "http://www.youtube.com/watch?v=" + shareKey);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    void onSortOrderChanged(String newOrder)
    {
        Uri uri=mUri;
        if( null != uri)
        {
            if(getString(R.string.pref_sortOrder_favorite).equals(newOrder)){
                String id = MovieContract.MovieFavoriteEntry.getIDFromUri(uri);
                Uri updatedUri = MovieContract.MovieFavoriteEntry.buildMovieUriWithMovieID(id);
                mUri = updatedUri;
            }else {

                String id = MovieContract.MovieEntry.getIDFromUri(uri);
                Uri updatedUri = MovieContract.MovieEntry.buildMovieUriWithMovieID(id);
                mUri = updatedUri;

            }
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        if(mUri!=null)
        {
            //Checks the sort order and accordingly loads data from either
            // the Movie Table or Favorite Table
            String sortOrder=Utility.getPreferredSortOrder(getActivity());

            if(getString(R.string.pref_sortOrder_favorite).equals(sortOrder)){
                return new CursorLoader(
                        getActivity(),
                        mUri,
                        FAVORITE_COLUMNS,
                        null,
                        null,
                        null);
            } else {return new CursorLoader(
                getActivity(),
                mUri,
                MOVIE_COLUMNS,
                null,
                null,
                null
            );}
        }
        return null;
    }


    /**Copy Data from one table to another with same Movie ID
     *
     * @param tableName1
     * @param tableName2
     *
     * Sources:
     * http://stackoverflow.com/questions/25155412/android-copy-values-from-table-and-insert-in-another
     */
    public void copyIntoTable1FromTable2(String tableName1, String tableName2){
        MovieDbHelper mOpenHelper = new MovieDbHelper(getActivity());
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String sql = "INSERT INTO "+tableName1+
                " SELECT * FROM "+tableName2+" WHERE "+MovieContract.MovieFavoriteEntry.COLUMN_MOVIE_ID
                +" = "+ mCursor.getInt(COLUMN_MOVIE_ID)+" ;";
        db.execSQL(sql);

        db.close();

    }

    public void favoriteButtonAction(Cursor detCursor)
    {
        // First, check if the favorite with this movie id exists in the db
        Cursor favoriteCursor = getActivity().getContentResolver().query(
                MovieContract.MovieFavoriteEntry.CONTENT_URI,
                new String[]{MovieContract.MovieFavoriteEntry.COLUMN_MOVIE_ID},
                MovieContract.MovieFavoriteEntry.COLUMN_MOVIE_ID + " = ? ",
                new String[]{detCursor.getString(COLUMN_MOVIE_ID)},
                null);
        //If the movie exists in the favorite's list, then display a toast
        if (favoriteCursor.moveToFirst()) {
            Context context = getActivity();
            CharSequence text = "This movie already exists in the favorites";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else {
            //Otherwise, add to Favorite db
            final String MOVIE_BASE_URL = "http://image.tmdb.org/t/p/w154/";
            final String BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String MOVIE_TAILER_APPEND_URL = "/videos?api_key=84604ead3481bd3bbd687f383f87e738";
            final String MOVIE_REVIEWS_APPEND_URL = "/reviews?api_key=84604ead3481bd3bbd687f383f87e738";


            // Get movie Data
            String movieName = detCursor.getString(COLUMN_MOVIE_TITLE);
            String posterPart = detCursor.getString(COLUMN_POSTER_LINK);
            String releaseDate = detCursor.getString(COLUMN_RELEASE_DATE);
            String rating = detCursor.getString(COLUMN_RATING);
            String overview = detCursor.getString(COLUMN_OVERVIEW);
            int id = detCursor.getInt(COLUMN_MOVIE_ID);
            String id_insert = Integer.toString(id);
            String trailerLinks = BASE_URL + id_insert + MOVIE_TAILER_APPEND_URL;
            String reviews = BASE_URL + id_insert + MOVIE_REVIEWS_APPEND_URL;

            //Store in database
            ContentValues movieValues = new ContentValues();

            //Store in database
            ContentValues favoriteValues = new ContentValues();

            favoriteValues.put(MovieContract.MovieFavoriteEntry.COLUMN_MOVIE_ID, id);
            favoriteValues.put(MovieContract.MovieFavoriteEntry.COLUMN_MOVIE_TITLE, movieName);
            favoriteValues.put(MovieContract.MovieFavoriteEntry.COLUMN_POSTER_LINK, MOVIE_BASE_URL + posterPart);
            favoriteValues.put(MovieContract.MovieFavoriteEntry.COLUMN_RELEASE_DATE, releaseDate);
            favoriteValues.put(MovieContract.MovieFavoriteEntry.COLUMN_RATING, rating);
            favoriteValues.put(MovieContract.MovieFavoriteEntry.COLUMN_OVERVIEW, overview);
            favoriteValues.put(MovieContract.MovieFavoriteEntry.COLUMN_TRAILER_LINKS, trailerLinks);
            favoriteValues.put(MovieContract.MovieFavoriteEntry.COLUMN_REVIEWS, reviews);


            Uri insertedUri = getActivity().getContentResolver()
                    .insert(MovieContract.MovieFavoriteEntry.CONTENT_URI, favoriteValues);

            copyIntoTable1FromTable2(MovieContract.FavoriteReviewsEntry .TABLE_NAME,
                    MovieContract.MovieReviewsEntry.TABLE_NAME);
            copyIntoTable1FromTable2(MovieContract.FavoriteTrailerEntry .TABLE_NAME,
                    MovieContract.MovieTrailerEntry.TABLE_NAME);

            //Display a toast so that user knows that the item is inserted in favorites
            Context context = getActivity();
            CharSequence text = "Movie added to favorites";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

        }

    }

    /**
     * Get ListView Height based on children
     *
     * @param listView           reviewList/trailerList
     * @return void
     *
     * Source:
     * http://stackoverflow.com/questions/18367522/android-list-view-inside-a-scroll-view
     */
    public static void setListViewHeightBasedOnChildren(ListView listView)
    {
        ListAdapter listAdapter = listView.getAdapter();
        if(listAdapter == null) return;
        if(listAdapter.getCount() <= 1) return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.AT_MOST);
        int totalHeight = 0;
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        View view = null;
        for(int i = 0; i < listAdapter.getCount(); i++)
        {
            view = listAdapter.getView(i, view, listView);
            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }

        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        mCursor = data;
        data.moveToFirst();
        DatabaseUtils.dumpCursor(data);

        if(data!=null && data.moveToFirst())
        {
                int position = data.getPosition();
                data.moveToPosition(position);

                //Detail Activity via Intent
                //The detail Activity called via intent.  Inspect the intent for movie data.
                //Movie Name
                String movieTitle = data.getString(COLUMN_MOVIE_TITLE);
                textTitle.setText(movieTitle);

                //Image- Movie Poster
                String movieLink = data.getString(COLUMN_POSTER_LINK);
                Picasso.with(getActivity()).load(movieLink).placeholder(R.drawable.image_url)
                        .into(imgView);

                //Release Date
                String movieReleaseDate = data.getString(COLUMN_RELEASE_DATE);
                textRD.setText(movieReleaseDate);

                //Rating
                String movieRating = data.getString(COLUMN_RATING)+"/10";
                textRating.setText(movieRating);

                //Overview
                String movieOverview = data.getString(COLUMN_OVERVIEW);
                textOverview.setText(movieOverview);

                //Reviews and Trailers
                //Check sort order and query join on Movie-Review, Movie-Trailer,
                //Or query join on Favorite-FavoriteReview, Favorite-FavoriteTrailer
                //To get reviews and trailers data
                Cursor review, trailer;
                String sortOrder=Utility.getPreferredSortOrder(getActivity());
                if(getString(R.string.pref_sortOrder_favorite).equals(sortOrder)){
                    review=getActivity().getContentResolver().query(
                            MovieContract.FavoriteReviewsEntry.buildMovieUriWithMovieID(
                                    data.getString(COLUMN_MOVIE_ID)),
                            FAVORITE_REVIEW_COLUMNS,
                            null,
                            null,
                            null);
                    trailer=getActivity().getContentResolver().query(
                            MovieContract.FavoriteTrailerEntry.buildMovieUriWithMovieID(
                                    data.getString(COLUMN_MOVIE_ID)),
                            FAVORITE_TRAILER_COLUMNS,
                            null,
                            null,
                            null);

                }else{
                    review=getActivity().getContentResolver().query(
                            MovieContract.MovieReviewsEntry.buildMovieUriWithMovieID(
                                    data.getString(COLUMN_MOVIE_ID)),
                            REVIEW_COLUMNS,
                            null,
                            null,
                            null);
                    trailer=getActivity().getContentResolver().query(
                            MovieContract.MovieTrailerEntry
                                    .buildMovieUriWithMovieID(data.getString(COLUMN_MOVIE_ID)),
                            TRAILER_COLUMNS,
                            null,
                            null,
                            null);

                }


                reviewAdapter.swapCursor(review);
                trailerAdapter.swapCursor(trailer);

                trailer.moveToFirst();
                review.moveToFirst();

                reviewList.setAdapter(reviewAdapter);
                videoList.setAdapter(trailerAdapter);

                setListViewHeightBasedOnChildren(reviewList);
                setListViewHeightBasedOnChildren(videoList);

                // Displays videos on clicking list item through either the youtube app or through the
                // website for the video
                //Source:
                // http://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
                videoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                                    "vnd.youtube:" + cursor.getString(COLUMN_KEY)));
                            startActivity(intent);
                        } catch (ActivityNotFoundException ex) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                                    "http://www.youtube.com/watch?v=" +
                                            cursor.getString(COLUMN_KEY)));
                            startActivity(intent);
                        }
                    }
                });

                if(trailer!=null && trailer.moveToFirst()) {
                    shareKey=trailer.getString(COLUMN_KEY);
                }

                favorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        favoriteButtonAction(mCursor);
                    }
                });

                // If onCreateOptionsMenu has already happened, we need to update the share intent now.
                if (mShareActionProvider != null) {
                    mShareActionProvider.setShareIntent(createShareVideoIntent());
                }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        reviewAdapter.swapCursor(null);
        trailerAdapter.swapCursor(null);
    }


}

