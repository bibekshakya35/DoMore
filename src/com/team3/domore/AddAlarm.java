package com.team3.domore;

import java.util.Calendar;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import java.text.DateFormat;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddAlarm extends Activity implements OnClickListener {

	private Button btnSave;
	DateFormat formatDateTime = DateFormat.getDateTimeInstance();
	Calendar dateTime = Calendar.getInstance();
	private TextView timeLabel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_alarm);

		btnSave = (Button) findViewById(R.id.save_button);
		btnSave.setOnClickListener(this);

		timeLabel = (TextView) findViewById(R.id.timeTxt);
		updateLabel();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.save_button:
			int seed = getId();
			Intent intent = new Intent(v.getContext(), AlarmReceiver.class);
			intent.putExtra("alarm_message", "This is an alarm!!!");
			PendingIntent sender = PendingIntent.getBroadcast(this, seed,
					intent, PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			am.set(AlarmManager.RTC_WAKEUP, dateTime.getTimeInMillis(), sender);

			AlarmFrag.db.open();
			boolean success = AlarmFrag.db.addEntry(dateTime, seed);
			AlarmFrag.db.close();
			if (success) {
				this.finish();
			} else {
				Toast.makeText(this, "Duplicate found!", Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	public void chooseDate(View v) {
		new DatePickerDialog(AddAlarm.this, d, dateTime.get(Calendar.YEAR),
				dateTime.get(Calendar.MONTH),
				dateTime.get(Calendar.DAY_OF_MONTH)).show();
	}

	public void chooseTime(View v) {
		new TimePickerDialog(AddAlarm.this, t,
				dateTime.get(Calendar.HOUR_OF_DAY),
				dateTime.get(Calendar.MINUTE), true).show();
	}

	DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			dateTime.set(Calendar.YEAR, year);
			dateTime.set(Calendar.MONTH, monthOfYear);
			dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			updateLabel();
		}
	};

	TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
			dateTime.set(Calendar.MINUTE, minute);
			updateLabel();
		}
	};

	private void updateLabel() {
		timeLabel.setText(formatDateTime.format(dateTime.getTime()));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_alarm, menu);
		return true;
	}

	private int getId() {
		int id = (int) (Math.random() * Integer.MAX_VALUE);
		AlarmFrag.db.open();
		Cursor cursor = AlarmFrag.db.getData();
		while (cursor.getPosition() < cursor.getCount()) {
			if (cursor.getInt(6) == id) {
				cursor.moveToFirst();
			} else {
				cursor.moveToNext();
			}
		}
		AlarmFrag.db.close();
		return id;
	}
}
