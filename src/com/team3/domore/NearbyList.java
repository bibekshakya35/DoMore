package com.team3.domore;

import java.util.ArrayList;
import java.util.HashMap;

import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
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

	// Places List
	PlacesList nearPlaces;

	// Places Listview
	ListView lv;

	// ListItems data
	ArrayList<HashMap<String, String>> placesListItems = new ArrayList<HashMap<String,String>>();

	// KEY Strings
	public static String KEY_REFERENCE = "reference"; // id of the place
	public static String KEY_NAME = "name"; // name of the place
	public static String KEY_VICINITY = "vicinity"; // Place area name

	public static LocationManager lm;
	public static double longitude;
	public static double latitude;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.nearby_list, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();

		// Getting listview
		lv = (ListView) getView().findViewById(R.id.list);

		// button show on map
		btnShowOnMap = (Button) getView().findViewById(R.id.btn_show_map);

		gps = new TrackGPS(getActivity());

		if (gps.canGetLocation()) {
			Log.d("Your Location", "latitude:" + gps.getLatitude() + ", longitude: " + gps.getLongitude());
		} else {
			Toast.makeText(getActivity(), "GPS signal not found", Toast.LENGTH_LONG)
			.show();
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
		}

		// calling background Async task to load Google Places
		// After getting places from Google all the data is shown in listview
		new LoadPlaces().execute();

		/** Button click event for shown on map */
		btnShowOnMap.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getActivity(),
						Nearby.class);

				// passing near places to map activity
				i.putExtra("near_places", nearPlaces);
				// staring activity
				startActivity(i);
			}
		});

		/**
		 * ListItem click event
		 * On selecting a listitem SinglePlaceActivity is launched
		 * */
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// getting values from selected ListItem
				String reference = ((TextView) view.findViewById(R.id.reference)).getText().toString();

				// Starting new intent
				Intent in = new Intent(getActivity(),
						SinglePlace.class);

				// Sending place refrence id to single place activity
				// place refrence id used to get "Place full details"
				in.putExtra(KEY_REFERENCE, reference);
				startActivity(in);
			}
		});
	}

	/**
	 * Background Async Task to Load Google places
	 * */
	class LoadPlaces extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage(Html.fromHtml("<b>Search</b><br/>Loading Places..."));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting Places JSON
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
		 * After completing background task Dismiss the progress dialog
		 * and show the data in UI
		 * Always use runOnUiThread(new Runnable()) to update UI from background
		 * thread, otherwise you will get error
		 * **/
		protected void onPostExecute(String file_url) {

			// Dismiss dialog
			pDialog.dismiss();

			/**
			 * Updating parsed Places into LISTVIEW
			 * */
			// Get json response status
			String status = nearPlaces.status;

			// Check for all possible status
			if(status.equals("OK")){
				// Successfully got places details
				if (nearPlaces.results != null) {
					// loop through each place
					for (Place p : nearPlaces.results) {
						HashMap<String, String> map = new HashMap<String, String>();

						// Place reference won't display in listview - it will be hidden
						// Place reference is used to get "place full details"
						map.put(KEY_REFERENCE, p.reference);

						// Place name
						map.put(KEY_NAME, p.name);

						// adding HashMap to ArrayList
						placesListItems.add(map);
					}
					// list adapter
					ListAdapter adapter = new SimpleAdapter(getActivity(), placesListItems,
							R.layout.nearby_row,
							new String[] { KEY_REFERENCE, KEY_NAME}, new int[] {
						R.id.reference, R.id.name });

					// Adding data into listview
					lv.setAdapter(adapter);
				}
			}
			else if(status.equals("ZERO_RESULTS")){
				// Zero results found

			}
			else if(status.equals("UNKNOWN_ERROR"))
			{

			}
			else if(status.equals("OVER_QUERY_LIMIT"))
			{

			}
			else if(status.equals("REQUEST_DENIED"))
			{

			}
			else if(status.equals("INVALID_REQUEST"))
			{

			}
			else
			{

			}
		}

	}
}