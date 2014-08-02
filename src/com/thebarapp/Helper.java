package com.thebarapp;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.widget.Spinner;

public class Helper {
	public static void setDate(Integer day, Spinner selector) {
		if (day == 1) {
			selector.setSelection(0);
		} else if (day == 2) {
			selector.setSelection(1);
		} else if (day == 3) {
			selector.setSelection(2);
		} else if (day == 4) {
			selector.setSelection(3);
		} else if (day == 5) {
			selector.setSelection(4);
		} else if (day == 6) {
			selector.setSelection(5);
		} else if (day == 7) {
			selector.setSelection(6);
		}
	}
	
	public static String formatTime(Date start, Date end) {
		Date dateStart = start;
		Date dateEnd = end;
		SimpleDateFormat simpDate, simpDateNo;

		simpDateNo = new SimpleDateFormat("hh:mm a");
		simpDate = new SimpleDateFormat("hh:mm a");

		String startTime = simpDateNo.format(dateStart);
		String endTime = simpDate.format(dateEnd);

		if (startTime.charAt(0) == '0') {
			startTime.substring(1);
		}

		if (endTime.charAt(0) == '0') {
			endTime.substring(1);
		}

		return startTime + " - " + endTime;
	}
}
