package com.example.android.moviemaniac;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.moviemaniac.sync.MovieSyncAdapter;


public class MainActivity extends ActionBarActivity implements MovieFragment.Callback{

    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private String sortOrder/*=getString(R.string.pref_sortOrder_mostPopular)*/;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sortOrder = Utility.getPreferredSortOrder(this);

        android.support.v7.app.ActionBar menu = getSupportActionBar();
        // http://stackoverflow.com/questions/19540648/android-how-to-show-app-logo-in-action-bar
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_main);


        if(findViewById((R.id.movie_detail_container))!=null)
        {
            //the detail container view will be present only in the large-screen layouts
            //(res/layout-sw600dp). If this view is present, then the activity should be
            //in two-pane mode.
            mTwoPane=true;
            //in the two-pane mode, show the detail view in this activity by
            //adding or replacing the detail fragment using a
            //fragment transaction
            if(savedInstanceState==null)
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container, new DetailFragment(),
                        DETAILFRAGMENT_TAG).commit();
            }

        } else {
            mTwoPane=false;
        }

        if(!getString(R.string.pref_sortOrder_favorite).equals(sortOrder)) {
            MovieSyncAdapter.initializeSyncAdapter(this);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    protected void onResume() {
        super.onResume();
        String order = Utility.getPreferredSortOrder(this);
        // update the movies in our second pane using the fragment manager
        if (order != null && !order.equals(sortOrder)) {
            MovieFragment mf = (MovieFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_movie);
            if (null != mf) {
                mf.onSortOrderChanged();
            }
            DetailFragment df= (DetailFragment)getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if(null != df)
            {
                df.onSortOrderChanged(order);
            }
            sortOrder = order;
        }
    }

    @Override
    public void onItemSelected(Uri contentUri) {

        if(mTwoPane)
        {
            //In the two-pane mode, show the detailview in this activity by
            //adding or replacing the detail fragment using a
            //fragment transaction
            Bundle args= new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, contentUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container,
                    fragment,DETAILFRAGMENT_TAG).commit();
        }
         else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }

}
