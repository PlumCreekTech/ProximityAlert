package com.plumcreektechnology.myandroidproximityalertproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class ProximityIntentReceiver extends BroadcastReceiver {

	private static final int NOTIFICATION_ID = 1000;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String key = LocationManager.KEY_PROXIMITY_ENTERING;
		Boolean entering = intent.getBooleanExtra(key, false);
		
		// log enter or exit
		if(entering) {
			Log.d(getClass().getSimpleName(), "entering");
		} else {
			Log.d(getClass().getSimpleName(), "exiting");
		}
		
		// create a pending intent to be activated from notificaiton
		Uri uri = Uri.parse("https://twitter.com/StealthMountain");
		Intent pending = new Intent(Intent.ACTION_VIEW, uri);
		pending.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//context.startActivity(pending);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, pending, 0);
		
		// create a notification of the proximity alert
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("We did it!")
				.setContentText("You're near the POI, sort of.")
				.addAction(R.drawable.ic_launcher, "sneak peek of what's to come", pendingIntent);
		
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(NOTIFICATION_ID, builder.build());
		
		
		
//		Notification notification = new Notification.Builder(context)
//				.setContentTitle("Proximity Alert!")
//				.setContentText("You are near your point of interest.")
//				//.addAction(R.drawable.ic_launcher, "Action!", pendingIntent)
//				.build();
		//notification = formatNotification(notification);
	}
	
	/**
	 * format notification
	 * @return
	 */
	private Notification formatNotification(Notification notification) {
		
		notification.when = System.currentTimeMillis();
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.ledARGB = Color.WHITE;
		notification.ledOnMS = 1500;
		notification.ledOffMS = 1500;
		
		return notification;
	}

}
