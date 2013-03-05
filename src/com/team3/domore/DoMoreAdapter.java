package com.team3.domore;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class DoMoreAdapter extends BaseAdapter {
	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;

	public DoMoreAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return data.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null) {
			vi = inflater.inflate(R.layout.alarm_row, null);
		}

		TextView time = (TextView) vi.findViewById(R.id.time);
		TextView day = (TextView) vi.findViewById(R.id.day);
		ToggleButton onOff = (ToggleButton) vi.findViewById(R.id.on_off);

		final HashMap<String, String> alarm = data.get(position);

		Log.i("", alarm.get("time"));
		Log.i("", alarm.get("day"));
		Log.i("", alarm.get("state"));
		Log.i("", "----------------");

		time.setText(alarm.get("time"));
		day.setText(alarm.get("day"));
		if (alarm.get("state").equals("On")) {
			onOff.setChecked(true);
		} else if (alarm.get("state").equals("Off")) {
			onOff.setChecked(false);
		}

		onOff.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Alarm alarmActivity = (Alarm) DoMoreAdapter.this.activity;
				alarmActivity.db.open();
				if (isChecked) {
					buttonView.setChecked(true);
					alarmActivity.db.update(alarm.get("time"),
							alarm.get("day"), "On");
					data.remove(position);
					HashMap<String, String> update = new HashMap<String, String>();
					update.put("time", alarm.get("time"));
					update.put("day", alarm.get("day"));
					update.put("state", "On");
					data.add(position, update);
				} else {
					buttonView.setChecked(false);
					alarmActivity.db.update(alarm.get("time"),
							alarm.get("day"), "Off");
					data.remove(position);
					HashMap<String, String> update = new HashMap<String, String>();
					update.put("time", alarm.get("time"));
					update.put("day", alarm.get("day"));
					update.put("state", "Off");
					data.add(position, update);
				}
				alarmActivity.db.close();
			}
		});

		return vi;
	}
}
