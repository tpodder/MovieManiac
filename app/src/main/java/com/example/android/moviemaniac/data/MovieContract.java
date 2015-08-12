package com.example.android.moviemaniac.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Tanushree on 2015-07-25.
 */
public class MovieContract {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.android.moviemaniac";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)

    public static final String PATH_MOVIE = "movie";


    /* Inner class that defines the table contents of the movie table */
    public static final class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "movie";

        // Column with the foreign key into the movie table.
       // public static final String COLUMN_MOVIE_KEY = "movie_id";

//        // Movie id as returned by API
//        public static final String COLUMN_MOVIE_ID = "movie_id";

        //Movie Name
        public static final String COLUMN_MOVIE_TITLE = "movie_title";

        //Movie ID
        public static final String COLUMN_MOVIE_ID="id";

        //Movie Image Link
        public static final String COLUMN_POSTER_LINK = "movie_link";

        //Movie Release Date
        public static final String COLUMN_RELEASE_DATE = "movie_release_date";

        //Movie rating
        public static final String COLUMN_RATING= "movie_rating";

        //Movie Overview
        public static final String COLUMN_OVERVIEW= "movie_overview";

        //Movie Trailer Links
        public static final String COLUMN_TRAILER_LINKS= "movie_trailer_links";

        //Movie Reviews
        public static final String COLUMN_REVIEWS= "movie_reviews";

//        //Movie duration
//        public static final String COLUMN_DURATION= "movie_duration";


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;


        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieUriWithName(String movieID) {
            return CONTENT_URI.buildUpon().appendPath(movieID)
                    .build();
        }

        public static String getIDFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    //Stage 2 - Trailers, Reviews Favourites

//    /* Inner class that defines the table contents of the trailer table */
//    public static final class MovieTrailerEntry implements BaseColumns {
//            //Store key and name
//    }
//
    /* Inner class that defines the table contents of the trailer table */
    public static final class MovieReviewsEntry implements BaseColumns {

              public static final String TABLE_NAME = "reviews";
              //Store id, author and content
              //Movie ID
              public static final String COLUMN_MOVIE_ID="id";

              //Review Author
              public static final String COLUMN_AUTHOR = "review_author";

              //Review Author
              public static final String COLUMN_CONTENT = "review_content";

            public static final Uri CONTENT_URI =
                    BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

            public static final String CONTENT_TYPE =
                    ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
            public static final String CONTENT_ITEM_TYPE =
                    ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;


            public static Uri buildMovieUri(long id) {
                return ContentUris.withAppendedId(CONTENT_URI, id);
    }



        }
//
//    /* Inner class that defines the table contents of the table for FAVOURITE movies*/
//    public static final class MovieFavouriteEntry implements BaseColumns {
//
//    }
}
