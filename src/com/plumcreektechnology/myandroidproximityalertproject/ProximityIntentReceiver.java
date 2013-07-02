package com.plumcreektechnology.myandroidproximityalertproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
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
		
		// create a notification of the proximity alert
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("We did it!")
				.setContentText("You're near the POI, sort of.");
		
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(17, builder.build());
		
		//PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, null, 0);
		
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
