package com.example.android.moviemaniac.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Tanushree on 2015-07-25.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.moviemaniac";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)

    public static final String PATH_MOVIE = "movie";
    public static final String PATH_REVIEW= "review";
    public static final String PATH_TRAILER= "trailer";
    public static final String PATH_FAVORITE= "favorite";
    public static final String PATH_FAVORITE_REVIEW= "favoriteReview";
    public static final String PATH_FAVORITE_TRAILER= "favoriteTrailer";

    /* Inner class that defines the table contents of the movie table */
    public static final class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_MOVIE_TITLE = "movie_title";

        public static final String COLUMN_MOVIE_ID="movie_id";

        public static final String COLUMN_POSTER_LINK = "movie_link";

        public static final String COLUMN_RELEASE_DATE = "movie_release_date";

        public static final String COLUMN_RATING= "movie_rating";

        public static final String COLUMN_OVERVIEW= "movie_overview";

        public static final String COLUMN_TRAILER_LINKS= "movie_trailer_links";

        public static final String COLUMN_REVIEWS= "movie_reviews";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieUriWithMovieID(String movieID) {
            return CONTENT_URI.buildUpon().appendPath(movieID)
                    .build();
        }

        public static String getIDFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    /* Inner class that defines the table contents of the trailer table */
    public static final class MovieTrailerEntry implements BaseColumns {

        public static final String TABLE_NAME = "trailer";

        public static final String COLUMN_MOVIE_ID = "id";

        public static final String COLUMN_KEY = "trailer_key";

        public static final String COLUMN_NAME = "trailer_name";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildMovieUriWithMovieID(String movieID) {
            return CONTENT_URI.buildUpon().appendPath(movieID)
                    .build();
        }

    }

    /* Inner class that defines the table contents of the reviews table */
    public static final class MovieReviewsEntry implements BaseColumns {

        public static final String TABLE_NAME = "reviews";

        public static final String COLUMN_MOVIE_ID="id";

        public static final String COLUMN_AUTHOR = "review_author";

        public static final String COLUMN_CONTENT = "review_content";

        public static final Uri CONTENT_URI =
                    BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE =
                    ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static Uri buildMovieUri(long id) {
                return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieUriWithMovieID(String movieID) {
            return CONTENT_URI.buildUpon().appendPath(movieID)
                    .build();
        }

    }

    /* Inner class that defines the table contents of the table for favorite movies*/
    public static final class MovieFavoriteEntry implements BaseColumns {

        public static final String TABLE_NAME = "favorite";

        public static final String COLUMN_MOVIE_TITLE = "movie_title";

        public static final String COLUMN_MOVIE_ID="id";

        public static final String COLUMN_POSTER_LINK = "movie_link";

        public static final String COLUMN_RELEASE_DATE = "movie_release_date";

        public static final String COLUMN_RATING= "movie_rating";

        public static final String COLUMN_OVERVIEW= "movie_overview";

        public static final String COLUMN_TRAILER_LINKS= "movie_trailer_links";

        public static final String COLUMN_REVIEWS= "movie_reviews";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieUriWithMovieID(String movieID) {
            return CONTENT_URI.buildUpon().appendPath(movieID)
                    .build();
        }

        public static String getIDFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }


    }

    /* Inner class that defines the table contents of the Favorite Trailer table */
    public static final class FavoriteTrailerEntry implements BaseColumns {

        public static final String TABLE_NAME = "trailer_favorite";

        public static final String COLUMN_MOVIE_ID = "id";

        public static final String COLUMN_KEY = "trailer_key";

        public static final String COLUMN_NAME = "trailer_name";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE_TRAILER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieUriWithMovieID(String movieID) {
            return CONTENT_URI.buildUpon().appendPath(movieID)
                    .build();
        }

    }
    //
    /* Inner class that defines the table contents of the favorite review table */
    public static final class FavoriteReviewsEntry implements BaseColumns {

        public static final String TABLE_NAME = "reviews_favorite";

        public static final String COLUMN_MOVIE_ID = "id";

        public static final String COLUMN_AUTHOR = "review_author";

        public static final String COLUMN_CONTENT = "review_content";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieUriWithMovieID(String movieID) {
            return CONTENT_URI.buildUpon().appendPath(movieID)
                    .build();
        }

    }
}
