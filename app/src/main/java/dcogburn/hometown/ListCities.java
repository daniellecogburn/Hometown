package dcogburn.hometown;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.HashMap;

import static android.R.id.list;

public class ListCities extends AppCompatActivity {
    String LIST_CITIES = "ListCities ";
    int MY_PERMISSIONS = 0;
    private ListView mListView;
    String[] cityNames = {"austin", "dallas", "denton", "el paso", "houston", "lubbock", "san antonio"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_cities);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION }, MY_PERMISSIONS);
        getClosestCity();
        Log.d(LIST_CITIES, "in onCreate");

        mListView = (ListView) findViewById(R.id.cities_list_view);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, cityNames);
        mListView.setAdapter(adapter);
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

    public void openDrawer(View view){
        Intent intentFavorites = new Intent(this, Drawer.class);
        startActivity(intentFavorites);
        Log.d(LIST_CITIES, "in openDrawer");
    }

    private String getClosestCity(){
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION }, MY_PERMISSIONS);
            return null;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        Log.d(LIST_CITIES, lastKnownLocation.toString());

        double userLat = Math.abs(lastKnownLocation.getLatitude());
        double userLong = Math.abs(lastKnownLocation.getLongitude());
        Log.d("userlat", String.valueOf(userLat));
        Log.d("userlong", String.valueOf(userLong));

        HashMap<String, Double> latMap = new HashMap();
        latMap.put("austin", 30.2672);
        latMap.put("dallas", 32.7767);
        latMap.put("denton", 33.2148);
        latMap.put("el paso", 31.7619);
        latMap.put("houston", 29.7604);
        latMap.put("lubbock", 33.5779);
        latMap.put("san antonio", 29.4241);

        HashMap<String, Double> longMap = new HashMap();
        longMap.put("austin", 97.7431);
        longMap.put("dallas", 96.7970);
        longMap.put("denton", 97.1331);
        longMap.put("el paso", 106.4850);
        longMap.put("houston", 95.3698);
        longMap.put("lubbock", 101.8552);
        longMap.put("san antonio", 98.4936);


        double shortestDist = 1000;
        String closestCity = "";
        double dist = 0;
        for (int i = 0; i < cityNames.length; i++){
            double cityLat = Math.abs(latMap.get(cityNames[i]));
            double cityLong = Math.abs(longMap.get(cityNames[i]));
            dist = Math.sqrt((Math.pow(cityLat - userLat,2) + Math.pow(cityLong-userLong, 2)));
            Log.d(cityNames[i], String.valueOf(dist));
            if (shortestDist > dist){
                shortestDist = dist;
                closestCity = cityNames[i];
            }
        }
        if (dist > 8){
            closestCity = "none";
        }
        Log.d(LIST_CITIES, closestCity);
        return closestCity;
    }
}
