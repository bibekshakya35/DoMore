package com.team3.domore;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

public class DoMoreAdapter extends BaseAdapter {
	private Activity activity;
	private ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
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

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null) {
			vi = inflater.inflate(R.layout.alarm_row, null);
		}

		TextView time = (TextView) vi.findViewById(R.id.time);
		TextView day = (TextView) vi.findViewById(R.id.day);
		ToggleButton onOff = (ToggleButton) vi.findViewById(R.id.on_off);

		HashMap<String, String> alarm = data.get(position);

		time.setText(alarm.get("time"));
		day.setText(alarm.get("day"));
		if (alarm.get("state").equals("On")) {
			onOff.setChecked(true);
		}

		return vi;
	}
}
