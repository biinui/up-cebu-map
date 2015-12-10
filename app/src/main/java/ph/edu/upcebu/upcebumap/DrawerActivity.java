package ph.edu.upcebu.upcebumap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LandmarkFragment.OnFragmentInteractionListener,
        CategoryFragment.OnFragmentInteractionListener, RoomFragment.OnFragmentInteractionListener, LandmarkDetailsFragment.OnFragmentInteractionListener {
    public static final String LAND_TITLE = "title";

    public void onFragmentInteraction(Uri uri) {
        //you can leave it empty
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        Intent intent = getIntent();
        int item = intent.getIntExtra("item", -1);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Fragment fragment;
        if (item == 3) {
            fragment = new RoomFragment();
        } else {
            String title = getIntent().getStringExtra(LAND_TITLE);
            fragment = new LandmarkFragment().newInstance(title);
            item = 1;

        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.flContent, fragment).commit();
            navigationView.getMenu().getItem(item).setChecked(true);
            setTitle(navigationView.getMenu().getItem(item).getTitle());
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*
        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //Intent intent = null;

        Fragment fragment = null;
        switch (id) {
            case R.id.nav_landmark:
                fragment = new LandmarkFragment();
                //Toast toast = Toast.makeText(this, "Wheeee!",Toast.LENGTH_SHORT);
                //toast.show();
                break;
            case R.id.nav_map:
                //fragment = new LoginFragment();
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(i);
                break;
            case R.id.nav_category:
                fragment = new CategoryFragment();
                break;
            case R.id.nav_office:
                fragment = new RoomFragment();
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.flContent, fragment).commit();

            // update selected item and title, then close the drawer
            /*
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
            */
            item.setChecked(true);
            setTitle(item.getTitle());
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
        /*
        if (id == R.id.nav_landmark) {
            intent = new Intent(this, LandActivity.class);
        } else if (id == R.id.nav_map) {
            intent = new Intent(this, MapsActivity.class);
        } else if (id == R.id.nav_category) {
            intent = new Intent(this, CategoryActivity.class);
        }
        */

        //startActivity(intent);
        return true;
    }
}
