package com.plumcreektechnology.myandroidproximityalertproject;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class MyGeofenceStore {

	// Keys for flattened mygeofences stored in SharedPreferences
	public static final String KEY_LATITUDE = "LATITUDE";
	public static final String KEY_LONGITUDE = "LONGITUDE";
	public static final String KEY_RADIUS = "RADIUS";
	public static final String KEY_EXPIRATION_DURATION = "EXPIRATION";
	public static final String KEY_URI = "URI";
	// The prefix for flattened geofence keys
	public static final String KEY_PREFIX = "com.plumcreektechnology.mygeofence";

	/*
	 * Invalid values, used to test geofence storage when retrieving geofences
	 */
	public static final long INVALID_LONG_VALUE = -999l;
	public static final float INVALID_FLOAT_VALUE = -999.0f;
	public static final int INVALID_INT_VALUE = -999;

	private final SharedPreferences prefs;
	private static final String SHARED_PREFERENCES = "tree_preferences";

	// create the SharedPreferences storage with private access only
	public MyGeofenceStore(Context context) {
		prefs = context.getSharedPreferences(SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
	}
	
	/**
	 * Returns a TreeMap<String, MyGeofence> of values that were stored in
	 * SharedPreferences and clears SharedPreferences once values are 
	 * transferred.
	 * @return
	 */
	public TreeMap<String, MyGeofence> getStored(){
		 Map<String,?> map = prefs.getAll();
		 
		 
		 TreeMap<String,MyGeofence> fences = new TreeMap<String, MyGeofence>();
		 String[] data;
		 for(Map.Entry<String,?> entry : map.entrySet()) {
			 data = getDataFromFieldKey(entry.getKey());
			 if(fences.equals(null)){
				 Log.e("null", "fences is null");
			 } else{
				 Log.e("safe", "fences should be fine");
			 }
			 
			 if(!fences.containsKey(data[1])) {//data[1] = id
				 fences.put(data[1], new MyGeofence(data[1]));
			 }
			 if(data[2].equals(KEY_LATITUDE)){
				 float lat = (Float) entry.getValue();
				 double lat2 = lat;
				 fences.get(data[1]).setLatitude(lat2);
			 }else if(data[2].equals(KEY_LONGITUDE)){
				 float lng = (Float) entry.getValue();
				 double lng2 = lng;
				 fences.get(data[1]).setLongitude(lng2);
			 }else if(data[2].equals(KEY_RADIUS)){
				 fences.get(data[1]).setRadius((Float) entry.getValue());
			 }else if(data[2].equals(KEY_EXPIRATION_DURATION)){ 
				 fences.get(data[1]).setExpiration((Long) entry.getValue());
			 }else if(data[2].equals(KEY_URI)){
				 fences.get(data[1]).setUri((String) entry.getValue());
			 }
		 }
		 // clear the preferences
		 prefs.edit().clear().commit();
		 return fences;
	}

	public MyGeofence getMyGeofence(String id) {
		// lookup key-value pairs in SharedPreferences and save each geofence
		// parameter
		double lat = prefs.getFloat(getMyGeofenceFieldKey(id, KEY_LATITUDE),
				INVALID_FLOAT_VALUE);
		double lng = prefs.getFloat(getMyGeofenceFieldKey(id, KEY_LONGITUDE),
				INVALID_FLOAT_VALUE);
		float radius = prefs.getFloat(getMyGeofenceFieldKey(id, KEY_RADIUS),
				INVALID_FLOAT_VALUE);
		long expirationDuration = prefs.getLong(
				getMyGeofenceFieldKey(id, KEY_EXPIRATION_DURATION),
				INVALID_LONG_VALUE);
		String uriString = prefs.getString(getMyGeofenceFieldKey(id, KEY_URI), null);

		// If none of the values is incorrect, return the object
		if (lat != INVALID_FLOAT_VALUE && lng != INVALID_FLOAT_VALUE
				&& radius != INVALID_FLOAT_VALUE
				&& expirationDuration != INVALID_LONG_VALUE && !uriString.equals(null)) {
			// Return a true MyGeofence object
			return new MyGeofence(id, lat, lng, radius, expirationDuration, uriString);
			// Otherwise, return null.
		} else {
			return null;
		}
	}

	private String getMyGeofenceFieldKey(String id, String field) {
		return KEY_PREFIX + "_" + id + "_" + field;
	}
	
	private String[] getDataFromFieldKey(String fieldkey){
		return fieldkey.split("_");
	}

	public void setMyGeofence(String id, MyGeofence geofence) {
		Editor editor = prefs.edit();
		editor.putFloat(getMyGeofenceFieldKey(id, KEY_LATITUDE),
				(float) geofence.getLatitude());
		editor.putFloat(getMyGeofenceFieldKey(id, KEY_LONGITUDE),
				(float) geofence.getLongitude());
		editor.putFloat(getMyGeofenceFieldKey(id, KEY_RADIUS),
				geofence.getRadius());
		editor.putLong(getMyGeofenceFieldKey(id, KEY_EXPIRATION_DURATION),
				geofence.getExpiration());
		editor.putString(getMyGeofenceFieldKey(id, KEY_URI),
				geofence.getUri());
		editor.commit();
	}

	public void clearMyGeofence(String id) {
		Editor editor = prefs.edit();
		editor.remove(getMyGeofenceFieldKey(id, KEY_LATITUDE));
		editor.remove(getMyGeofenceFieldKey(id, KEY_LONGITUDE));
		editor.remove(getMyGeofenceFieldKey(id, KEY_RADIUS));
		editor.remove(getMyGeofenceFieldKey(id, KEY_EXPIRATION_DURATION));
		editor.commit();
	}
	
	public void clearAllMyGeofence() {
		Editor editor = prefs.edit();
		editor.clear();
		editor.commit();
	}
}
