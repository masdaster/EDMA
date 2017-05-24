package fr.corenting.edcompanion.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import fr.corenting.edcompanion.R;
import fr.corenting.edcompanion.fragments.CommunityGoalsFragment;
import fr.corenting.edcompanion.fragments.GalnetFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Setup navigation view and fake click the first item
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set initial fragment
        fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction().replace(R.id.fragmentContent, new CommunityGoalsFragment())
                .commit();

        // Select the first item in menu as the fragment was loaded
        navigationView.setCheckedItem(navigationView.getMenu().getItem(0).getItemId());
        setTitle(getString(R.string.community_goals));
        getSupportActionBar().setSubtitle(R.string.data_credits);
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (id == R.id.nav_cg) {
            fragmentManager
                    .beginTransaction().replace(R.id.fragmentContent, new CommunityGoalsFragment())
                    .commit();
            setTitle(getString(R.string.community_goals));
            getSupportActionBar().setSubtitle(R.string.data_credits);
        } else if (id == R.id.nav_galnet) {
            fragmentManager
                    .beginTransaction().replace(R.id.fragmentContent, new GalnetFragment())
                    .commit();
            setTitle(getString(R.string.galnet));
            getSupportActionBar().setSubtitle("");
        } else if (id == R.id.nav_about) {
            Intent i = new Intent(this, AboutActivity.class);
            startActivity(i);
            drawer.closeDrawer(GravityCompat.START);
            return false;
        } else if (id == R.id.nav_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            drawer.closeDrawer(GravityCompat.START);
            return false;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
