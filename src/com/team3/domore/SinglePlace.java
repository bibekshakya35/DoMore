package com.team3.domore;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class SinglePlace extends Activity {
	Boolean isInternetPresent = false;
	GooglePlaces googlePlaces;
	PlaceDetails placeDetails;
	ProgressDialog pDialog;
	String name;
	String phone;
	String address;
	String latitude;
	String longitude;
	Button btnPhone;
	Button btnNav;

	// KEY Strings
	public static String KEY_REFERENCE = "reference"; // id of the place

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.single_place);

		Intent i = getIntent();

		String reference = i.getStringExtra(KEY_REFERENCE);

		new LoadSinglePlaceDetails().execute(reference);

		btnPhone = (Button) findViewById(R.id.btn_phone);
		btnNav = (Button) findViewById(R.id.btn_nav);

		/** 
		 * Button click for calling the restaurant 
		 * */
		btnPhone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String uri = "tel:"+phone.trim();
				Intent intent = new Intent(Intent.ACTION_DIAL);
				intent.setData(Uri.parse(uri));
				startActivity(intent);
			}
		});

		/** 
		 * Button click for navigating to the restaurant 
		 * */
		btnNav.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), Nearby.class);
				startActivity(i);
				
				String uri = address;
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
						Uri.parse("google.navigation:q="+uri));
				startActivity(intent);

			}
		});
	}

	/**
	 * Background Async Task to Load Google places
	 * */
	class LoadSinglePlaceDetails extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SinglePlace.this);
			pDialog.setMessage("Loading profile ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting Profile JSON
		 * */
		protected String doInBackground(String... args) {
			String reference = args[0];

			googlePlaces = new GooglePlaces();

			// Check if used is connected to Internet
			try {
				placeDetails = googlePlaces.getPlaceDetails(reference);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed Places into LISTVIEW
					 * */
					if(placeDetails != null){
						String status = placeDetails.status;

						// check place deatils status
						// Check for all possible status
						if(status.equals("OK")){
							if (placeDetails.result != null) {
								name = placeDetails.result.name;
								address = placeDetails.result.formatted_address;
								phone = placeDetails.result.formatted_phone_number;
								latitude = Double.toString(placeDetails.result.geometry.location.lat);
								longitude = Double.toString(placeDetails.result.geometry.location.lng);

								Log.d("Place ", name + address + phone + latitude + longitude);

								// Displaying all the details in the view
								// single_place.xml
								TextView lbl_name = (TextView) findViewById(R.id.name);
								TextView lbl_address = (TextView) findViewById(R.id.address);
								TextView lbl_phone = (TextView) findViewById(R.id.phone);
								TextView lbl_location = (TextView) findViewById(R.id.location);

								// Check for null data from google
								// Sometimes place details might missing
								name = name == null ? "Not present" : name; // if name is null display as "Not present"
								address = address == null ? "Not present" : address;
								phone = phone == null ? "Not present" : phone;
								latitude = latitude == null ? "Not present" : latitude;
								longitude = longitude == null ? "Not present" : longitude;

								lbl_name.setText(name);
								lbl_address.setText(address);
								lbl_phone.setText(Html.fromHtml("<b>Phone:</b> " + phone));
								lbl_location.setText(Html.fromHtml("<b>Latitude:</b> " + latitude + ", <b>Longitude:</b> " + longitude));
							}
						}
						else if(status.equals("ZERO_RESULTS")){

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
					else{

					}

				}
			});

		}

	}

}