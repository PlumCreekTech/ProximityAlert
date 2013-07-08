package com.plumcreektechnology.myandroidproximityalertproject;

import java.util.Locale;

public class MyGeofence implements Comparable<MyGeofence> {

	private String id;
	private double latitude;
	private double longitude;
	private float radius;
	private long expiration;
	private String uri;

	//TODO: interface that this & store implement to share constants
	public static final long INVALID_LONG_VALUE = -999l;
	public static final float INVALID_FLOAT_VALUE = -999.0f;
	public static final int INVALID_INT_VALUE = -999;
	
	public MyGeofence(String name){
		id = name;
		latitude = INVALID_FLOAT_VALUE;
		longitude = INVALID_FLOAT_VALUE;
		radius = INVALID_FLOAT_VALUE;
		expiration = INVALID_LONG_VALUE;
		//look up invalid double in store?
	}

	public MyGeofence(String id, double latitude, double longitude,
			float radius, long expiration, String uri) {
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.radius = radius;
		this.expiration = expiration;
		this.uri = uri;
	}

	public String getId() {
		return id;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public float getRadius() {
		return radius;
	}

	public long getExpiration() {
		return expiration;
	}
	
	public String getUri() {
		return uri;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public void setExpiration(long expiration) {
		this.expiration = expiration;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	@Override
	public int compareTo(MyGeofence fence) {
		return id.toLowerCase(Locale.getDefault()).compareTo(fence.getId().toLowerCase());
	}
	
	public String toString(){
		return "("+latitude+", "+longitude+")";
	}

}
