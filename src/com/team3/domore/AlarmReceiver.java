package com.team3.domore;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		try {	
			Bundle bundle = intent.getExtras();
			String message = bundle.getString("alarm_message");
			Toast.makeText(context, message, Toast.LENGTH_LONG).show();
			
			// Create the intent to launch when "nearby" button is clicked
			PendingIntent pendingIntent;
	        Intent i = new Intent(context, Nearby.class);
	        pendingIntent =  PendingIntent.getActivity(context, 0, i, 0);
			
			NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

			// Build the notification
			Notification noti = new NotificationCompat.Builder(context)
	         .setContentTitle("Food Time!")
	         .setSmallIcon(R.drawable.ic_launcher)
	         .setSound(uri)
	         .setWhen(System.currentTimeMillis())
	         .addAction(R.drawable.location, "Nearby", pendingIntent)
	         .build();
			
	        nm.notify(192837, noti);
			
		} catch (Exception e) {
			Toast.makeText(context, "There was an error somewhere, but alarm was received", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
}