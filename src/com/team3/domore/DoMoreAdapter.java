package com.team3.domore;

import java.util.ArrayList;
import java.util.Calendar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.view.View.OnLongClickListener;
import android.widget.PopupMenu.OnMenuItemClickListener;

public class DoMoreAdapter extends BaseAdapter {
	private Activity activity;
	private ArrayList<CalendarInfo> data;
	private CalendarInfo alarm;
	private static LayoutInflater inflater = null;

	public DoMoreAdapter(Activity a, ArrayList<CalendarInfo> d) {
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
		TextView date = (TextView) vi.findViewById(R.id.date);
		ToggleButton onOff = (ToggleButton) vi.findViewById(R.id.on_off);

		alarm = data.get(position);
		Calendar cal = alarm.cal;

		time.setText(cal.get(Calendar.HOUR_OF_DAY) + ":"
				+ cal.get(Calendar.MINUTE));
		date.setText(cal.get(Calendar.MONTH) + 1 + "/"
				+ cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.YEAR));
		if (alarm.state) {
			onOff.setChecked(true);
		} else if (!alarm.state) {
			onOff.setChecked(false);
		}

		/**
		 * Button to toggle the alarm on or off
		 */
		onOff.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlarmFrag.db.open();
				alarm = data.get(position);
				ToggleButton button = (ToggleButton) v;
				if (button.isChecked()) {
					button.setChecked(true);
					AlarmFrag.db.update(alarm.cal, true);
					alarm.state = true;

					if (Calendar.getInstance().before(alarm.cal)) {
						Intent intent = new Intent(v.getContext(),
								AlarmReceiver.class);
						intent.putExtra("alarm_message", "This is an alarm!!!");
						PendingIntent sender = PendingIntent.getBroadcast(
								v.getContext(), alarm.id, intent,
								PendingIntent.FLAG_UPDATE_CURRENT);
						AlarmManager am = (AlarmManager) v.getContext()
								.getSystemService(Context.ALARM_SERVICE);
						am.set(AlarmManager.RTC_WAKEUP,
								alarm.cal.getTimeInMillis(), sender);
						Log.w("", "Alarm enabled by toggle");
					}
				} else {
					button.setChecked(false);
					AlarmFrag.db.update(alarm.cal, false);
					alarm.state = false;

					if (Calendar.getInstance().before(alarm.cal)) {
						Intent intent = new Intent(v.getContext(),
								AlarmReceiver.class);
						intent.putExtra("alarm_message",
								"This alarm is cancelled!");
						PendingIntent sender = PendingIntent.getBroadcast(
								v.getContext(), alarm.id, intent,
								PendingIntent.FLAG_UPDATE_CURRENT);
						AlarmManager am = (AlarmManager) v.getContext()
								.getSystemService(Context.ALARM_SERVICE);
						am.cancel(sender);
						Log.w("", "Alarm cancelled by toggle");
					}
				}
				AlarmFrag.db.close();
			}
		});

		/**
		 * Add on long click listener for each item to with option to delete
		 */
		time.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(final View v) {
				PopupMenu popup = new PopupMenu(v.getContext(), v);
				popup.getMenuInflater().inflate(R.menu.alarm_popup,
						popup.getMenu());

				popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {

						switch (item.getItemId()) {
						case R.id.edit:
							Toast.makeText(
									v.getContext(),
									"You selected the action : "
											+ item.getTitle(),
									Toast.LENGTH_SHORT).show();
							break;
						case R.id.delete:
							alarm = data.remove(position);
							AlarmFrag.db.open();
							AlarmFrag.db.deleteEntry(alarm.cal);
							AlarmFrag.db.close();
							AlarmFrag.adapter.notifyDataSetChanged();

							Intent intent = new Intent(v.getContext(),
									AlarmReceiver.class);
							intent.putExtra("alarm_message",
									"This alarm is cancelled!");
							PendingIntent sender = PendingIntent.getBroadcast(
									v.getContext(), alarm.id, intent,
									PendingIntent.FLAG_UPDATE_CURRENT);
							AlarmManager am = (AlarmManager) v.getContext()
									.getSystemService(Context.ALARM_SERVICE);
							am.cancel(sender);

							Log.w("", "Alarm cancelled by deletion");

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
