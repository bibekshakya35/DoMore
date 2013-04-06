package com.team3.domore;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

public class AlarmFrag extends Fragment implements OnClickListener {
	private ArrayList<CalendarInfo> data = new ArrayList<CalendarInfo>();
	public static DoMoreAdapter adapter;
	public static Database db;
	public ListView list;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.alarm_frag, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		
		View btnAdd = (Button) getView().findViewById(R.id.add_button);
		btnAdd.setOnClickListener(this);

		list = (ListView) getView().findViewById(R.id.alarm_list);

		data.clear();
		adapter = new DoMoreAdapter(getActivity(), data);

		list.setAdapter(adapter);

		db = new Database(getActivity(), "ALARMS.sqlite", "ALARM_TABLE");
		db.createDatabase();
		db.open();
		Cursor cursor = db.getData();

		while (cursor.getPosition() < cursor.getCount()) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, cursor.getInt(0));
			cal.set(Calendar.MINUTE, cursor.getInt(1));
			cal.set(Calendar.MONTH, cursor.getInt(2));
			cal.set(Calendar.DAY_OF_MONTH, cursor.getInt(3));
			cal.set(Calendar.YEAR, cursor.getInt(4));
			boolean state = Boolean.parseBoolean(cursor.getString(5));
			int id = cursor.getInt(6);
			CalendarInfo info = new CalendarInfo(cal, state, id);
			data.add(info);
			adapter.notifyDataSetChanged();
			cursor.moveToNext();
		}

		db.close();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_button:
			startActivity(new Intent(getActivity(), AddAlarm.class));
		}
	}
}
