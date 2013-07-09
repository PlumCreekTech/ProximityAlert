package com.plumcreektechnology.myandroidproximityalertproject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

// TODO
// make a Proximity Alert class named ProxAlert to be sent as PendingIntent
// make a Lister class called MyLocationListener()

public class ProxAlertActivity extends Fragment {

	private static final long MIN_DIST = 1; // meters
	private static final long MIN_TIME = 1000; // milliseconds
	private static final float RADIUS = 1000; // meters
	private static final long EXPIRATION = -1; // never expire
	private static final String PROX_ALERT_INTENT = "com.plumcreektechnology.myandroidproximityalertproject.ProxAlert";
	private static final NumberFormat nf = new DecimalFormat("##.########");
	private static final String DEFAULT_URI = "https://twitter.com/StealthMountain";
	
	private LocationManager locationManager;
	private EditText nameEditText;
	private EditText latitudeEditText;
	private EditText longitudeEditText;
	private EditText radiusEditText;
	private TextView treeprint;
	private Button savePointButton;
	private Button removePointButton;
	private Button autoButton;
	private MyGeofence recent;
	private MyGeofenceStore storage;
	private MyLocationListener listener;
	private TreeMap<String, MyGeofence> tree;
	private ProximityIntentReceiver receiver;
	
	private String alertName;
	private String alertUri;
	
	/**
	 * initialize location listener, location manager, storage, tree, north pole
	 * as recent, proximity receiver and collect view objects
	 */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		//initialize location things
		listener = new MyLocationListener();
		locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER,
				MIN_TIME, MIN_DIST, listener);
		
		// initialize prox things
		storage = new MyGeofenceStore(getActivity());
		recent = new MyGeofence("north pole", 90.0, 0.0, (float)0.0, EXPIRATION, DEFAULT_URI);
		receiver = new ProximityIntentReceiver();
		
		// fill the tree
		tree = storage.getStored();
		for(Map.Entry<String, MyGeofence> entry : tree.entrySet()){
			addProximityAlert(entry.getValue());
		}
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.proxalert, container, false);
		
		// initialize view objects
		treeprint = (TextView) v.findViewById(R.id.treeprint);
		treeprint.setText(tree.toString());
		
		nameEditText = (EditText) v.findViewById(R.id.name);
		latitudeEditText = (EditText) v.findViewById(R.id.point_latitude);
		longitudeEditText = (EditText) v.findViewById(R.id.point_longitude);
		radiusEditText = (EditText) v.findViewById(R.id.radius);
		savePointButton = (Button) v.findViewById(R.id.save_point_button);
		savePointButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveMyGeofence(v);			}
		});
		
		removePointButton = (Button) v.findViewById(R.id.remove_point_button);
		removePointButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				removeMyGeofence(v);			}
		});

		autoButton = (Button) v.findViewById(R.id.autopopulate);
		autoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				autopopulate(v);			}
		});
		
		return v;
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
				EXPIRATION, DEFAULT_URI);
		// TODO store mygeofence
		tree.put(geofence.getId(), geofence);
		addProximityAlert(geofence);
		
		//textview
		treeprint.setText(tree.toString());
	}
	
	/**
	 * make a proximity alert for coordinate (latitude, longitude) with radius RADIUS
	 * creates a PendingIntent for ProxAlert and sends it to locationManager in Proximity Alert request
	 * registers a receiver (ProximityIntentReceiver) with an intent filter ProxAlert
	 * @param latitude
	 * @param longitude
	 */
	private void addProximityAlert(MyGeofence geofence) {
		// Toast.makeText(this, "adding new coordinate", Toast.LENGTH_LONG).show();
		// add the latitude and longitude to extras for identification
		Intent intent = new Intent(PROX_ALERT_INTENT);
		intent.putExtra("POI", geofence.getId());
		intent.putExtra("URI", geofence.getUri());
		PendingIntent proximityIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		locationManager.addProximityAlert(geofence.getLatitude(),
				geofence.getLongitude(), geofence.getRadius(),
				geofence.getExpiration(), proximityIntent);
		
		recent = geofence; // for debugging
		
		IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT);
		getActivity().registerReceiver(receiver, filter);
	}
	
	/**
	 * remove a geofence based on name in nameEditText
	 * @param v
	 */
	public void removeMyGeofence(View v){
		String name = nameEditText.getText().toString();
		tree.remove(name);
		
		treeprint.setText(tree.toString());
	}
	
	/**
	 * remove all geofences in tree
	 * @param v
	 */
	public void removeAllMyGeofence(View v) {
		tree.clear();
		treeprint.setText("There are no stored geofences.");
	}
	
	public void autopopulate(View v) {
		MyGeofence geofence = new MyGeofence("bucknam", 42.326895, -71.104342, 200, EXPIRATION, "http://en.wikipedia.org/wiki/Adolph_R._Bucknam");
		tree.put(geofence.getId(), geofence);
		addProximityAlert(geofence);
		
		//textview
		treeprint.setText(tree.toString());
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
			Toast.makeText(getActivity(), "Distance from Point: "+distance, Toast.LENGTH_LONG).show();
		}

		@Override
		public void onProviderDisabled(String s) {}

		@Override
		public void onProviderEnabled(String s) {}

		@Override
		public void onStatusChanged(String s, int i, Bundle b) {}
	}
	
	public String getAlertName() {
		return alertName;
	}

	public void setAlertName(String alertName) {
		this.alertName = alertName;
	}

	public String getAlertUri() {
		return alertUri;
	}

	public void setAlertUri(String alertUri) {
		this.alertUri = alertUri;
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
		getActivity().unregisterReceiver(receiver);
		listener = null;
	}

}