package com.plumcreektechnology.myandroidproximityalertproject;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
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
	private static final String POINT_LATITUDE_KEY = "POINT_LATITUDE_KEY";
	private static final String POINT_LONGITUDE_KEY = "POINT_LONGITUDE_KEY";
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		listener = new MyLocationListener();
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER,
				MIN_TIME, MIN_DIST, listener);
		
		storage = new MyGeofenceStore(this);
		receiver = new ProximityIntentReceiver();
		
		// initialize view objects
		nameEditText = (EditText) findViewById(R.id.name);
		latitudeEditText = (EditText) findViewById(R.id.point_latitude);
		longitudeEditText = (EditText) findViewById(R.id.point_longitude);
		radiusEditText = (EditText) findViewById(R.id.radius);
//		savePointButton = (Button) findViewById(R.id.save_point_button);
//		removePointButton = (Button) findViewById(R.id.remove_point_button);
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
		storage.setMyGeofence(geofence.getId(), geofence);
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
		recent = geofence;
		IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT);
		registerReceiver(receiver, filter);
	}
	
	public void removeMyGeofence(View v){
		//storage.clearMyGeofence(nameEditText.getText().toString());

		SharedPreferences prefs = this.getSharedPreferences(getClass().getSimpleName(), Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.remove(POINT_LATITUDE_KEY);
		editor.remove(POINT_LONGITUDE_KEY);
	}
	
//	/**
//	 * as long as there is a last known location
//	 * display the coordinates on screen in the
//	 * EditText objects
//	 */
//	private void populateCoordinatesFromLastKnownLocation() {
//		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//		if(location!=null) {
//			latitudeEditText.setText(nf.format(location.getLatitude()));
//			longitudeEditText.setText(nf.format(location.getLongitude()));
//		}
//	}
	
//	/**
//	 * stores latitude and longitude in the shared preferences
//	 * only accessible by this application
//	 * @param latitude
//	 * @param longitude
//	 */
//	private void saveCoordinatesInPreferences(float latitude, float longitude) {
//		SharedPreferences prefs = this.getSharedPreferences(getClass().getSimpleName(), Context.MODE_PRIVATE);
//		SharedPreferences.Editor prefsEditor = prefs.edit(); // must make editor to change preferences
//		prefsEditor.putFloat(POINT_LATITUDE_KEY, latitude);
//		prefsEditor.putFloat(POINT_LONGITUDE_KEY, longitude);
//		prefsEditor.commit();
//	}
	
	/**
	 * retrieves a saved location from saved preferences
	 * @return
	 */
	private Location retrieveLocationFromPreferences() {
		Location location = new Location(recent.getId());
		location.setLatitude(recent.getLatitude());
		location.setLongitude(recent.getLongitude());
		return location;
//		Location location = new Location("POINT_LOCATION");
//		location.setLatitude(prefs.getFloat(POINT_LATITUDE_KEY, 0));
//		location.setLongitude(prefs.getFloat(POINT_LONGITUDE_KEY, 0));
//		return location;
	}
	
	/**
	 * every time the location is changed, it toasts
	 * the distance between the proximity alert point
	 * and the current location
	 * @author devinfrenze
	 *
	 */
	public class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location location) {
			Location pointLocation = retrieveLocationFromPreferences();
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
	
	public void onDestroy(){
		super.onDestroy();
		unregisterReceiver(receiver);
		listener = null;
	}

}