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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.moviemaniac.AsyncTasks.FetchReviewTask;
import com.example.android.moviemaniac.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by Tanushree on 2015-07-27.
 *
 * Detail Fragment Class
 */
public  class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    static final String DETAIL_URI = "URI";

    private String movie;
    private Uri mUri;
    private static final int DETAIL_LOADER=0;
    TrailerAdapter trailerAdapter;
    ReviewAdapter reviewAdapter;
    Cursor mCursor;

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

            MovieContract.MovieReviewsEntry.COLUMN_AUTHOR,
            MovieContract.MovieReviewsEntry.COLUMN_CONTENT,
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

    //Views
    TextView textTitle,textRD,textRating,textOverview;
    ImageView imgView;
    ListView videoList, reviewList;

    public DetailFragment() {

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
//        trailerAdapter = new TrailerAdapter(getActivity(),null,0);
//        videoList=(ListView)rootView.findViewById(R.id.list_trailer);
//
        //Reviews
        reviewAdapter = new ReviewAdapter(getActivity(),null,0);
        reviewList=(ListView)rootView.findViewById(R.id.list_reviews);
//        FetchReviewTask reviewTask = new FetchReviewTask(getActivity());
//        reviewTask.execute(mCursor.getString(COLUMN_REVIEWS));



        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detail, menu);

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
            String id = MovieContract.MovieEntry.getIDFromUri(uri);
            Uri updatedUri= MovieContract.MovieEntry.buildMovieUriWithMovieID(id);
            mUri=updatedUri;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);

        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        if(mUri!=null)
        {return new CursorLoader(
                getActivity(),
                mUri,
                MOVIE_COLUMNS,
                null,
                null,
                null
        );}
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        reviewAdapter.swapCursor(data);
        data.moveToFirst();
        if(data!=null && data.moveToFirst())
        {
//            do {

                int position = data.getPosition();
                Log.v(LOG_TAG, "Position=" + position);
                data.moveToPosition(position);


                //Detail Activity via Intent
                //The detail Activity called via intent.  Inspect the intent for movie data.
                //Movie Name
                String movieTitle = data.getString(COLUMN_MOVIE_TITLE);
                textTitle.setText(movieTitle);

                //Image- Movie Poster
                String movieLink = data.getString(COLUMN_POSTER_LINK);
                Picasso.with(getActivity()).load(movieLink).placeholder(R.drawable.image_url).into(imgView);

                //Release Date
                String movieReleaseDate = data.getString(COLUMN_RELEASE_DATE);
                textRD.setText(movieReleaseDate);

                //Rating
                String movieRating = data.getString(COLUMN_RATING)+"/10";
                textRating.setText(movieRating);

                //Overview
                String movieOverview = data.getString(COLUMN_OVERVIEW);
                textOverview.setText(movieOverview);

//                //Review
                reviewList.setAdapter(reviewAdapter);
                FetchReviewTask reviewTask = new FetchReviewTask(getActivity());
                reviewTask.execute(data.getString(COLUMN_REVIEWS));

//            }
//            while(data.moveToNext());


            //Set Adapters
//            videoList.setAdapter(trailerAdapter);
//            reviewList.setAdapter(reviewAdapter);

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {reviewAdapter.swapCursor(null); }


}

