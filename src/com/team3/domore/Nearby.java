package com.team3.domore;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.Toast;

public class Nearby extends FragmentActivity {

	private GoogleMap map;
	private LocationManager locationManager;
	private Location location;
	private String provider;
	public static double longitude;
	public static double latitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nearby);

		map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.mapview)).getMap();

		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
		boolean enabledGPS = service
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean enabledWiFi = service
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if (!enabledGPS) {
			Toast.makeText(this, "GPS signal not found", Toast.LENGTH_LONG)
					.show();
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
		}

		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		map.setMyLocationEnabled(true);
		map.getMyLocation();

		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 10,
				locationListener);
	}

	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			longitude = location.getLongitude();
			latitude = location.getLatitude();
			System.out.println(latitude + ", " + longitude);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nearby, menu);
		return true;
	}

}
