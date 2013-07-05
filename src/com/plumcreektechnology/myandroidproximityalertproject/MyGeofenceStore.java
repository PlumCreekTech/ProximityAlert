package com.plumcreektechnology.myandroidproximityalertproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class MyGeofenceStore {

	// Keys for flattened mygeofences stored in SharedPreferences
	public static final String KEY_LATITUDE = "com.plumcreektechnology.mygeofence.KEY_LATITUDE";
	public static final String KEY_LONGITUDE = "com.plumcreektechnology.mygeofence.KEY_LONGITUDE";
	public static final String KEY_RADIUS = "com.plumcreektechnology.mygeofence.KEY_RADIUS";
	public static final String KEY_EXPIRATION_DURATION = "com.plumcreektechnology.mygeofence.KEY_EXPIRATION_DURATION";
	// The prefix for flattened geofence keys
	public static final String KEY_PREFIX = "com.plumcreektechnology.mygeofence.KEY";

	/*
	 * Invalid values, used to test geofence storage when retrieving geofences
	 */
	public static final long INVALID_LONG_VALUE = -999l;
	public static final float INVALID_FLOAT_VALUE = -999.0f;
	public static final int INVALID_INT_VALUE = -999;

	private final SharedPreferences prefs;
	private static final String SHARED_PREFERENCES = "SharedPreferences";

	// create the SharedPreferences storage with private access only
	public MyGeofenceStore(Context context) {
		prefs = context.getSharedPreferences(SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
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

		// If none of the values is incorrect, return the object
		if (lat != INVALID_FLOAT_VALUE && lng != INVALID_FLOAT_VALUE
				&& radius != INVALID_FLOAT_VALUE
				&& expirationDuration != INVALID_LONG_VALUE) {
			// Return a true MyGeofence object
			return new MyGeofence(id, lat, lng, radius, expirationDuration);
			// Otherwise, return null.
		} else {
			return null;
		}
	}

	private String getMyGeofenceFieldKey(String id, String field) {
		return KEY_PREFIX + "_" + id + "_" + field;
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
}
