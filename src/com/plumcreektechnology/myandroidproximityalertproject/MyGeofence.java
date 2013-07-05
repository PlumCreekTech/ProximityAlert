package com.plumcreektechnology.myandroidproximityalertproject;

public class MyGeofence {

	private final String id;
	private final double latitude;
	private final double longitude;
	private final float radius;
	private long expiration;
	private int transition;

	public MyGeofence(String id, double latitude, double longitude,
			float radius, long expiration, int transition) {
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.radius = radius;
		this.expiration = expiration;
		this.transition = transition;
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

	public int getTransition() {
		return transition;
	}

}
