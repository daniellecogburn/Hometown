package dcogburn.hometown;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class Drawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        // get a list of running processes and iterate through them
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
// get the info from the currently running task
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

        Log.i("current task :", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());
        String currentActivity = taskInfo.get(0).topActivity.getClassName();
        Log.i("current task :", "toSubstring ::" + currentActivity.substring(currentActivity.length()-6, currentActivity.length()));
        Intent intent;

        if (id == R.id.nav_cities) {
            if (!currentActivity.substring(currentActivity.length()-10, currentActivity.length()).equals("ListCities") ) {
                intent = new Intent(this, ListCities.class);
                startActivity(intent);
            }
        } else if (id == R.id.nav_favorites) {
            if (!currentActivity.substring(currentActivity.length()-9, currentActivity.length()).equals("Favorites") ) {
                intent = new Intent(this, Favorites.class);
                startActivity(intent);
            }
        } else if (id == R.id.nav_saved) {
            if (!currentActivity.substring(currentActivity.length()-5, currentActivity.length()).equals("Saved") ) {
                intent = new Intent(this, Saved.class);
                startActivity(intent);
            }
        } else {
            if (!currentActivity.substring(currentActivity.length()-8, currentActivity.length()).equals("Settings") ) {
                intent = new Intent(this, Settings.class);
                startActivity(intent);
            }

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
