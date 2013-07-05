package com.plumcreektechnology.myandroidproximityalertproject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

// TODO
// make a Proximity Alert class named ProxAlert to be sent as PendingIntent
// make a Lister class called MyLocationListener()

public class ProxAlertActivity extends Activity {

	private static final long MIN_DIST = 1; // meters
	private static final long MIN_TIME = 1000; // milliseconds
	private static final float RADIUS = 1000; // meters
	private static final long EXPIRATION = -1; // never expire
	private static final String PROX_ALERT_INTENT = "com.plumcreektechnology.myandroidproximityalertproject.ProxAlert";
	private static final NumberFormat nf = new DecimalFormat("##.########");
	
	private LocationManager locationManager;
	private EditText nameEditText;
	private EditText latitudeEditText;
	private EditText longitudeEditText;
	private EditText radiusEditText;
	private Button savePointButton;
	private Button removePointButton;
	private MyGeofence recent;
	private MyGeofenceStore storage;
	private ProximityIntentReceiver receiver;
	private MyLocationListener listener;
	private TreeMap<String, MyGeofence> tree;
	
	/**
	 * initialize location listener, location manager, storage, tree, north pole
	 * as recent, proximity receiver and collect view objects
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		listener = new MyLocationListener();
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER,
				MIN_TIME, MIN_DIST, listener);
		// initialize important things
		storage = new MyGeofenceStore(this);
		tree = new TreeMap<String,MyGeofence>();
		recent = new MyGeofence("north pole", 0.0, 0.0, (float)0.0, EXPIRATION);
		receiver = new ProximityIntentReceiver();
		// TODO fill the tree
		// initialize view objects
		nameEditText = (EditText) findViewById(R.id.name);
		latitudeEditText = (EditText) findViewById(R.id.point_latitude);
		longitudeEditText = (EditText) findViewById(R.id.point_longitude);
		radiusEditText = (EditText) findViewById(R.id.radius);
//		savePointButton = (Button) findViewById(R.id.save_point_button);
//		removePointButton = (Button) findViewById(R.id.remove_point_button);
	}
	
	public void onStart() {
		super.onStart();
		
	}
	
	/**
	 * when a user chooses to save a point, the button calls this method
	 * if there is no known last location, it aborts
	 * otherwise, it saves the coordinates in preferences and calls addProximityAlert
	 */
	public void saveMyGeofence(View v) {
		// TODO get textedit info
		MyGeofence geofence = new MyGeofence(nameEditText.getText().toString(),
				Double.parseDouble(latitudeEditText.getText().toString()),
				Double.parseDouble(longitudeEditText.getText().toString()),
				Float.parseFloat(radiusEditText.getText().toString()),
				EXPIRATION);
		// TODO store mygeofence
		tree.put(geofence.getId(), geofence);
		addProximityAlert(geofence);
	}
	
	/**
	 * make a proximity alert for coordinate (latitude, longitude) with radius RADIUS
	 * creates a PendingIntent for ProxAlert and sends it to locationManager in Proximity Alert request
	 * registers a receiver (ProximityIntentReceiver) with an intent filter ProxAlert
	 * @param latitude
	 * @param longitude
	 */
	private void addProximityAlert(MyGeofence geofence) {
		Toast.makeText(this, "adding new coordinate", Toast.LENGTH_LONG).show();
		// add the latitude and longitude to extras for identification
		Intent intent = new Intent(PROX_ALERT_INTENT);
		PendingIntent proximityIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

		locationManager.addProximityAlert(geofence.getLatitude(),
				geofence.getLongitude(), geofence.getRadius(),
				geofence.getExpiration(), proximityIntent);
		
		recent = geofence; // for debugging
		
		IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT);
		registerReceiver(receiver, filter);
	}
	
	/**
	 * remove a geofence based on name in nameEditText
	 * @param v
	 */
	public void removeMyGeofence(View v){
		String name = nameEditText.getText().toString();
		tree.remove(name);
	}
	
	/**
	 * remove all geofences in tree
	 * @param v
	 */
	public void removeAllMyGeofence(View v) {
		tree.clear();
	}
	
	/**
	 * retrieves a saved location from saved preferences
	 * @return
	 */
	private Location retrieveLocationFromPreferences() {
		Location location = new Location(recent.getId()); // for debugging, actually retrieving from recent
		location.setLatitude(recent.getLatitude());
		location.setLongitude(recent.getLongitude());
		return location;
	}
	
	/**
	 * every time the location is changed, it toasts
	 * the distance between the most recently created
	 * proximity alert and the current location
	 * @author devinfrenze
	 *
	 */
	public class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location location) {
			Location pointLocation = retrieveLocationFromPreferences(); // for debugging see above
			float distance = location.distanceTo(pointLocation);
			Toast.makeText(ProxAlertActivity.this, "Distance from Point: "+distance, Toast.LENGTH_LONG).show();
		}

		@Override
		public void onProviderDisabled(String s) {}

		@Override
		public void onProviderEnabled(String s) {}

		@Override
		public void onStatusChanged(String s, int i, Bundle b) {}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.android_proximity_alert_project, menu);
		return true;
	}
	
	/**
	 * store any remaining geofences for next time in case
	 * this activity is destroyed
	 */
	public void onStop() {
		super.onStop();
		Iterator<Entry<String, MyGeofence>> it = tree.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, MyGeofence> ent = it.next();
			storage.setMyGeofence(ent.getKey(), ent.getValue());
		}
	}
	
	/**
	 * before program ends, store any existing
	 * fences in shared preferences
	 * delete all remaining from the tree
	 * free the listener and receiver
	 */
	public void onDestroy(){
		super.onDestroy();
		tree.clear();
		// clear receiver and listener
		unregisterReceiver(receiver);
		listener = null;
	}

}