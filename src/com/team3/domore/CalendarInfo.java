package com.team3.domore;

import java.util.Calendar;

public class CalendarInfo {
	public Calendar cal;
	public boolean state;
	public int id;

	public CalendarInfo(Calendar c, boolean s, int i) {
		cal = c;
		state = s;
		id = i;
	}
}
