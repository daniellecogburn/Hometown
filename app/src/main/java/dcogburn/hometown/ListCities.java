package dcogburn.hometown;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;
import java.util.Locale;

public class ListCities extends AppCompatActivity {
    String LIST_CITIES = "ListCities ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int MY_PERMISSIONS = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_cities);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION }, MY_PERMISSIONS);


        checkPermissions();
        listCities();
        Log.d(LIST_CITIES, "in onCreate");

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

// Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                //makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

// Register the listener with the Location Manager to receive location updates

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            String locationProvider = LocationManager.NETWORK_PROVIDER;
            Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
            Log.d(LIST_CITIES, lastKnownLocation.toString());
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return;
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_cities, menu);
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

    private void listCities(){

    }

    private void checkPermissions() {
        // Assume thisActivity is the current activity
        int permissionCoarse = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionFine = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        Log.d("", "Permission checks:");
        if (permissionCoarse == PackageManager.PERMISSION_GRANTED) {
            Log.d("","We have permission for coarse location.");
        } else {
            Log.d("","We DO NOT have permission for coarse location.");
        }
        if (permissionFine == PackageManager.PERMISSION_GRANTED) {
            Log.d("","We have permission for fine location.");
        } else {
            Log.d("","We DO NOT have permission for fine location.");
        }
    }

}
