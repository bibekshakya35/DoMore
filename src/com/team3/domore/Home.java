package com.team3.domore;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Home extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		
		View btnAlarm=(Button)findViewById(R.id.alarm);
		btnAlarm.setOnClickListener(this);

		View btnNearby=(Button)findViewById(R.id.nearby);
		btnNearby.setOnClickListener(this);
		
		View btnNearby2=(Button)findViewById(R.id.nearbylist);
		btnNearby2.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.alarm:
			startActivity(new Intent(this, AlarmFrag.class));
			break;
		case R.id.nearby:
			startActivity(new Intent(this, Nearby.class));
			break;
		case R.id.nearbylist:
			startActivity(new Intent(this, NearbyList.class));
			break;
		}
	}
}
