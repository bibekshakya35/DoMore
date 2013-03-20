package com.team3.domore;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.PopupMenu.OnMenuItemClickListener;

public class DoMoreAdapter extends BaseAdapter {
	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private HashMap<String, String> alarm;
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

		alarm = data.get(position);

		time.setText(alarm.get("time"));
		day.setText(alarm.get("day"));
		if (alarm.get("state").equals("On")) {
			onOff.setChecked(true);
		} else if (alarm.get("state").equals("Off")) {
			onOff.setChecked(false);
		}

		onOff.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Alarm alarmActivity = (Alarm) DoMoreAdapter.this.activity;
				alarmActivity.db.open();
				alarm = data.get(position);
				HashMap<String, String> update = new HashMap<String, String>();
				update.put("time", alarm.get("time"));
				update.put("day", alarm.get("day"));
				ToggleButton button = (ToggleButton) v;
				if (button.isChecked()) {
					button.setChecked(true);
					alarmActivity.db.update(alarm.get("time"),
							alarm.get("day"), "On");
					update.put("state", "On");
					data.set(position, update);
				} else {
					button.setChecked(false);
					alarmActivity.db.update(alarm.get("time"),
							alarm.get("day"), "Off");
					update.put("state", "Off");
					data.set(position, update);
				}
				alarmActivity.db.close();
			}
		});

		/** Add on long click listener for each item (ie. to delete or edit) */
		time.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(final View v) {
				PopupMenu popup = new PopupMenu(v.getContext(), v);
				popup.getMenuInflater().inflate(R.menu.alarm_popup, popup.getMenu());

				popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						// If selected item is delete, remove from database and refresh screen
						switch(item.getItemId()) {
						case R.id.edit:
							Toast.makeText(v.getContext(), "You selected the action : " + item.getTitle(), Toast.LENGTH_SHORT).show();		
							break;
						case R.id.delete:
							Toast.makeText(v.getContext(), "You selected the action : " + item.getTitle(), Toast.LENGTH_SHORT).show();
										
							alarm = data.get(position);
							System.out.println(alarm.get("time"));
							System.out.println(alarm.get("day"));
							Alarm.db.open();
							Alarm.db.deleteEntry(alarm.get("time"), alarm.get("day"));
							Alarm.db.close();
							
						    Intent intent = new Intent(v.getContext(), Alarm.class);
							v.getContext().startActivity(intent);

							break;
						}
						return true;
					}
				});				
				popup.show();

				return false;
			}			
		});

		return vi;
	}


}
