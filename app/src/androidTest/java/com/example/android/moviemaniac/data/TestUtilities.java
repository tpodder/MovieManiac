package com.example.android.moviemaniac.data;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.example.android.moviemaniac.data.MovieContract.MovieEntry;
import com.example.android.moviemaniac.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/**
 * Created by Tanushree on 2015-07-25.
 */
public class TestUtilities extends AndroidTestCase {

    //Test Data s
    static final String TEST_NAME="Intestellar";
    static final int TEST_ID = 10;
    static final String TEST_LINK="http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg ";
    static final String TEST_DATE = "2014-10-21";  // December 20th, 2014
    static final String TEST_RATING="8.1";
    static final String TEST_OVERVIEW="Interstellar chronicles the adventures of a group of"+
            "explorers who make use of a newly discovered wormhole to surpass the limitations on human"+ "" +
            "space travel and conquer the vast distances involved in an interstellar voyage.";
    static final String TEST_TRAILER_LINKS="https://www.themoviedb.org/movie/157336-interstellar?language=en#play=zSWdZVtXT7E";
    static final String TEST_REVIEWS="Good movie";
    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    /*
        Students: Use this to create some default weather values for your database tests.
     */
    static ContentValues createMovieValues() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieEntry.COLUMN_MOVIE_ID, TEST_ID);
        movieValues.put(MovieEntry.COLUMN_MOVIE_TITLE, TEST_NAME);
        movieValues.put(MovieEntry.COLUMN_POSTER_LINK, TEST_LINK);
        movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, TEST_DATE);
        movieValues.put(MovieEntry.COLUMN_RATING, TEST_RATING);
        movieValues.put(MovieEntry.COLUMN_OVERVIEW, TEST_OVERVIEW);
        movieValues.put(MovieEntry.COLUMN_TRAILER_LINKS, TEST_TRAILER_LINKS);
        movieValues.put(MovieEntry.COLUMN_REVIEWS, TEST_REVIEWS);


        return movieValues;
    }



    /*
        Students: The functions we provide inside of TestProvider use this utility class to test
        the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
        CTS tests.

        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}

