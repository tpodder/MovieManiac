package com.example.android.moviemaniac;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity implements MovieFragment.Callback{
    private final String MOVIEFRAGMENT_TAG = "MFTAG";
    private String sortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sortOrder = Utility.getPreferredSortOrder(this);
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar menu = getSupportActionBar();
        // http://stackoverflow.com/questions/19540648/android-how-to-show-app-logo-in-action-bar
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_main);
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//            .add(R.id.container, new MovieFragment(), MOVIEFRAGMENT_TAG)
//                    .commit();
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
        // update the location in our second pane using the fragment manager
        if (order != null && !order.equals(sortOrder)) {
            MovieFragment ff = (MovieFragment) getSupportFragmentManager().findFragmentByTag(MOVIEFRAGMENT_TAG);
            if (null != ff) {
                ff.onSortOrderChanged();
            }
            sortOrder = order;
        }
    }

    @Override
    public void onItemSelected(Uri contentUri) {

            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
    }

}
