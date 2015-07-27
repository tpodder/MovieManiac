package com.example.android.moviemaniac;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
/*
   Detail Activity Class to get the details of a movie
 */

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
//    public static class DetailFragment extends Fragment implements LoaderCallbacks<Cursor> {
//
//        private static final String LOG_TAG = DetailFragment.class.getSimpleName();
//        static final String DETAIL_URI = "URI";
//
//        private String movie;
//        private Uri mUri;
//        private static final int DETAIL_LOADER=0;
//
//        private static final String[] MOVIE_COLUMNS ={
//                MovieContract.MovieEntry._ID,
//                MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
//                MovieContract.MovieEntry.COLUMN_POSTER_LINK,
//                MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
//                MovieContract.MovieEntry.COLUMN_RATING,
//                MovieContract.MovieEntry.COLUMN_OVERVIEW,
////                MovieContract.MovieEntry.COLUMN_TRAILER_LINKS,
////                MovieContract.MovieEntry.COLUMN_REVIEWS
//        };
//
//        static final int COLUMN_ID=0;
//        static final int COLUMN_MOVIE_TITLE = 1;
//        static final int COLUMN_POSTER_LINK = 2;
//        static final int COLUMN_RELEASE_DATE = 3;
//        static final int COLUMN_RATING = 4;
//        static final int COLUMN_OVERVIEW = 5;
////        static final int COLUMN_TRAILER_LINKS = 6;
////        static final int COLUMN_REVIEWS = 7;
//
//        TextView textTitle,textRD,textRating,textOverview;
//        ImageView imgView;
//
//        public DetailFragment() {
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//
//            Bundle arguments = getArguments();
//            if (arguments != null) {
//                mUri = arguments.getParcelable(DetailActivity.DetailFragment.DETAIL_URI);
//            }
//
//            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
//            textTitle=(TextView)rootView.findViewById(R.id.detail_text);
//            imgView = (ImageView)rootView.findViewById(R.id.detail_img);
//            textRD=(TextView)rootView.findViewById(R.id.detail_releaseDate);
//            textRating=(TextView)rootView.findViewById(R.id.detail_rating);
//            textOverview=(TextView)rootView.findViewById(R.id.detail_overview);
//            //Detail Activity via Intent
////            Intent intent = getActivity().getIntent();
////            if(intent!=null)
////            {// The detail Activity called via intent.  Inspect the intent for forecast data.
////                //Movie Name
////                String movieTitle = intent.getStringExtra(MovieFragment.EXTRA_NAME);
////                if(movieTitle!=null)
////                {   ((TextView) rootView.findViewById(R.id.detail_text))
////                        .setText(movieTitle);}
////
////                //Image- Movie Poster
////                String movieLink = intent.getStringExtra(MovieFragment.EXTRA_LINK);
////                if(movieLink!=null)
////                {
////                    ImageView imgView = (ImageView)rootView.findViewById(R.id.detail_img);
////                    Picasso.with(getActivity()).load(movieLink).resize(150,200).placeholder(R.drawable.image_url).into(imgView);
////
////                }
////
////                //Release Date
////                String movieReleaseDate = intent.getStringExtra(MovieFragment.EXTRA_RDATE);
////                if(movieReleaseDate!=null)
////                { ((TextView) rootView.findViewById(R.id.detail_releaseDate))
////                        .setText(movieReleaseDate);}
////
////                //Rating
////                String movieRating = intent.getStringExtra(MovieFragment.EXTRA_RATING);
////                if(movieRating!=null)
////                {((TextView) rootView.findViewById(R.id.detail_rating))
////                        .setText(movieRating);}
////
////                //Overview
////                String movieOverview = intent.getStringExtra(MovieFragment.EXTRA_OVERVIEW);
////                if(movieOverview!=null)
////                {((TextView) rootView.findViewById(R.id.detail_overview))
////                        .setText(movieOverview);}
////             }
//           // final ScrollView scroll = (ScrollView) rootView.findViewById(R.id.scroll);
//                return rootView;
//            }
//
//
//        @Override
//        public void onActivityCreated(Bundle savedInstanceState) {
//            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
//            super.onActivityCreated(savedInstanceState);
//        }
//
////        void onSortOrderChanged( String newSortOrder ) {
////            // replace the uri, since the location has changed
////            Uri uri = mUri;
////
//////            if (null != uri) {
//////                Uri updatedUri = MovieContract.MovieEntry.buildMovieUri();
//////                mUri = updatedUri;
//////                getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
//////            }
////        }
//
//        @Override
//        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//            Log.v(LOG_TAG, "In onCreateLoader");
////            Intent intent = getActivity().getIntent();
////            if (intent == null) {
////                return null;
////            }
//            if(mUri!=null)
//            {return new CursorLoader(
//                    getActivity(),
//                    mUri,
//                    MOVIE_COLUMNS,
//                    null,
//                    null,
//                    null
//            );}
//            return null;
//        }
//
//        @Override
//        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//            Log.v(LOG_TAG, "In onLoadFinished");
//            if (data != null && data.moveToFirst()) {
//                //Detail Activity via Intent
//                // The detail Activity called via intent.  Inspect the intent for forecast data.
//                //Movie Name
//                String movieTitle = data.getString(COLUMN_MOVIE_TITLE);
//                textTitle.setText(movieTitle);
//
//                //Image- Movie Poster
//                String movieLink = data.getString(COLUMN_POSTER_LINK);
//                Picasso.with(getActivity()).load(movieLink).resize(150, 200).placeholder(R.drawable.image_url).into(imgView);
//
//                //Release Date
//                String movieReleaseDate = data.getString(COLUMN_RELEASE_DATE);
//                textRD.setText(movieReleaseDate);
//
//                //Rating
//                String movieRating = data.getString(COLUMN_RATING);
//                textRating.setText(movieRating);
//
//                //Overview
//                String movieOverview = data.getString(COLUMN_OVERVIEW);
//                textOverview.setText(movieOverview);
//
//            }
//        }
//
//        @Override
//        public void onLoaderReset(Loader<Cursor> loader) { }
//    }
//
}