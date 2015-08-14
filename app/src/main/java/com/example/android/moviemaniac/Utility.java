package com.example.android.moviemaniac;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Tanushree on 2015-07-26.
 */
public class Utility {

    public static String getPreferredSortOrder(Context context) {
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPrefs.getString(
                context.getString(R.string.pref_sortOrder_key),
                context.getString(R.string.pref_sortOrder_mostPopular));
    }

}
