package com.team3.domore;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public class Nearby extends FragmentActivity {

	private GoogleMap map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nearby);

		// Get places from NearbyList
		Intent i = getIntent();
		PlacesList nearPlaces = new PlacesList();

		nearPlaces = (PlacesList) i.getSerializableExtra("near_places");
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview)).getMap();
		
		TrackGPS gps = new TrackGPS(this);
		// Check for a data connection and GPS
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
		boolean enabledGPS = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

		// If not enabled, transfer to settings
		if (!enabledGPS) {
			gps.showGpsSettingsAlert();
		}

		map.setMyLocationEnabled(true);
		map.getMyLocation();

		// Display a red marker at user's position
		LatLng myLoc = new LatLng(gps.getLatitude(), gps.getLongitude());
		map.addMarker(new MarkerOptions()
		.position(myLoc)
		.title("You Are Here")
		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
		map.getUiSettings().setCompassEnabled(true);
		map.getUiSettings().setZoomControlsEnabled(true);
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 14));

		// Display a blue marker for each place
		try {
			for (Place p : nearPlaces.results) {
				LatLng placeLoc = new LatLng(p.geometry.location.lat, p.geometry.location.lng);	
				map.addMarker(new MarkerOptions()
				.position(placeLoc)
				.title(p.name)
				.snippet(p.vicinity)
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
			}
		}
		catch (Exception e){
			Toast.makeText(this, "No Data Connection", Toast.LENGTH_LONG)
			.show();
			gps.showWifiSettingsAlert();
		}
	}
}
