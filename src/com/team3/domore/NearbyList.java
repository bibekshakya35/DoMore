package com.team3.domore;

import java.util.ArrayList;
import java.util.HashMap;

import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class NearbyList extends Fragment {
	Boolean isInternetPresent = false;
	TrackGPS gps;
	GooglePlaces googlePlaces;
	Button btnShowOnMap;
	ProgressDialog pDialog;
	PlacesList nearPlaces;
	ListView lv;
	ArrayList<HashMap<String, String>> placesListItems = new ArrayList<HashMap<String, String>>();

	// ID of place
	public static String KEY_REFERENCE = "reference";

	// Name of the place
	public static String KEY_NAME = "name";

	// Area of the place (address)
	public static String KEY_VICINITY = "vicinity"; 

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.nearby_list, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();

		// If it is possible to get location
		gps = new TrackGPS(getActivity());
		if (gps.canGetLocation()) {
			Log.d("Your Location", "latitude:" + gps.getLatitude()
					+ ", longitude: " + gps.getLongitude());
		} 
		else {
			gps.showGpsSettingsAlert();
		}

		placesListItems.clear();
		lv = (ListView) getView().findViewById(R.id.list);
		btnShowOnMap = (Button) getView().findViewById(R.id.btn_show_map);


		// Calling background Async task to load Google Places
		// After getting places from Google all the data is shown in listview
		new LoadPlaces().execute();

		/** Button click event for shown on map */
		btnShowOnMap.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getActivity(), Nearby.class);

				// Pass places to map activity
				i.putExtra("near_places", nearPlaces);
				startActivity(i);
			}
		});

		/**
		 * ListItem click event On selecting a listitem SinglePlace is
		 * launched
		 * */
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// Getting values from selected ListItem
				String reference = ((TextView) view
						.findViewById(R.id.reference)).getText().toString();

				// Starting new intent
				Intent in = new Intent(getActivity(), SinglePlace.class);

				// Sending place reference id to single place activity
				in.putExtra(KEY_REFERENCE, reference);
				startActivity(in);
			}
		});
	}

	/**
	 * Background Async Task to load Google places
	 * */
	class LoadPlaces extends AsyncTask<String, String, String> {

		
		
		/**
		 * Before starting background thread show ProgressDialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage(Html
					.fromHtml("<b>Search</b><br/>Loading Places..."));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * Get all Places as JSON file
		 * */
		protected String doInBackground(String... args) {
			
			
			googlePlaces = new GooglePlaces();

			try {
				String types = "restaurant|cafe";
				int radius = 1000;
				nearPlaces = googlePlaces.search(gps.getLatitude(),
						gps.getLongitude(), radius, types);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * After completing background task dismiss the progress dialog and show
		 * the data 
		 * **/
		protected void onPostExecute(String file_url) {

			pDialog.dismiss();

			/**
			 * Updating parsed places into listview
			 * */
			
			String status = "NO CONNECTION";
			// Get JSON response status
			try {
				status = nearPlaces.status;
			}
			catch (NullPointerException e){
				gps.showWifiSettingsAlert();
			}

			// Check for all possible status
			if (status.equals("OK")) {

				// Successfully got places details
				if (nearPlaces.results != null) {

					// loop through each place
					for (Place p : nearPlaces.results) {
						HashMap<String, String> map = new HashMap<String, String>();

						// Place reference is used to get details (not displayed in list)
						map.put(KEY_REFERENCE, p.reference);

						// Place name
						map.put(KEY_NAME, p.name);

						// add HashMap to ArrayList
						placesListItems.add(map);
					}
					ListAdapter adapter = new SimpleAdapter(getActivity(),
							placesListItems, R.layout.nearby_row, new String[] {
						KEY_REFERENCE, KEY_NAME }, new int[] {
						R.id.reference, R.id.name });

					// Adding data into listview
					lv.setAdapter(adapter);
				}
			} 
			// If status does not return results
			else if (status.equals("ZERO_RESULTS")) {
				Toast.makeText(getActivity(), "No restaurants found", Toast.LENGTH_LONG)
				.show();
			} 
			else if (status.equals("UNKNOWN_ERROR")) {
				Toast.makeText(getActivity(), "Error retrieving list", Toast.LENGTH_LONG)
				.show();
			} 
			else if (status.equals("OVER_QUERY_LIMIT")) {
				Toast.makeText(getActivity(), "Too many requests, try later", Toast.LENGTH_LONG)
				.show();
			} 
			else if (status.equals("REQUEST_DENIED")) {
				Toast.makeText(getActivity(), "Error retrieving list", Toast.LENGTH_LONG)
				.show();
			} 
			else if (status.equals("INVALID_REQUEST")) {
				Toast.makeText(getActivity(), "Error retrieving list", Toast.LENGTH_LONG)
				.show();
			} 
			else {
				Toast.makeText(getActivity(), "Error retrieving list", Toast.LENGTH_LONG)
				.show();
			}
		}

	}
}