package com.team3.domore;

import java.util.ArrayList;
import java.util.HashMap;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class Alarm extends Activity implements OnClickListener{
	private ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
	public static DoMoreAdapter adapter;
	public static Database db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm);

		View btnAdd=(Button)findViewById(R.id.add_button);
		btnAdd.setOnClickListener(this);
		
		final ListView list = (ListView) findViewById(R.id.alarm_list);

		adapter = new DoMoreAdapter(this, data);
		list.setAdapter(adapter);

		db = new Database(this, "ALARMS.sqlite", "ALARM_TABLE");
		db.createDatabase();
		db.open();
		Cursor cursor = db.getData();

		while (cursor.getPosition() < cursor.getCount()) {
			HashMap<String, String> alarm = new HashMap<String, String>();
			alarm.put("time", cursor.getString(0));
			alarm.put("day", cursor.getString(1));
			alarm.put("state", cursor.getString(2));
			data.add(alarm);
			adapter.notifyDataSetChanged();
			cursor.moveToNext();
		}

		db.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.alarm, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.add_button:
			finish();
			startActivity(new Intent(this, AddAlarm.class));
		}
	}

}
