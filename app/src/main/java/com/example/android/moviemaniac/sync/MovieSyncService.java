package com.example.android.moviemaniac.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Tanushree on 2015-08-12.
 */
public class MovieSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static MovieSyncAdapter sMovieSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("SunshineSyncService", "onCreate - SunshineSyncService");
        synchronized (sSyncAdapterLock) {
            if (sMovieSyncAdapter == null) {
                sMovieSyncAdapter = new MovieSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sMovieSyncAdapter.getSyncAdapterBinder();
    }
}
