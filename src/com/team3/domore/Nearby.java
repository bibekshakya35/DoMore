package com.team3.domore;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.Toast;

public class Nearby extends FragmentActivity {

	private MapView mapView;
    private MyLocationOverlay myLocOverlay;
    MapController mc;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nearby);
		
		// mapView = (MapView) findViewById(R.id.map);
		
		LocationManager mlocManager =
				(LocationManager)getSystemService(Context.LOCATION_SERVICE);
		LocationListener mlocListener = new MyLocationListener();
		mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
		

	}

	/* Class My Location Listener */
	public class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location loc) {
			int lat = (int) (loc.getLatitude() * 1E6);
			int lng = (int) (loc.getLongitude() * 1E6);

			String Text = "My current location is: " + "Latitude = " + lat +
					"Longitude = " + lng;

			Toast.makeText( getApplicationContext(),
					Text,
					Toast.LENGTH_SHORT).show();

			GeoPoint point = new GeoPoint(40, -83);
			mc.setCenter(point);
			
			mc.animateTo(point);
			mapView.invalidate();
				
		}

		@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText( getApplicationContext(),
					"Gps Disabled",
					Toast.LENGTH_SHORT ).show();
		}

		@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText( getApplicationContext(),
					"Gps Enabled",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nearby, menu);
		return true;
	}

}
