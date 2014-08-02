package com.thebarapp;

import java.text.SimpleDateFormat;
import java.util.Date;

import activities.ListActivity;
import activities.MapActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

	public static boolean isConnectedToInternet(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
		}
		return false;
	}
	
	public static void displayError(String message, final Class<?> activity, final Context context) {
		// no deals found so display a popup and return to search options
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		// set title
		builder.setTitle("No Results");

		// set dialog message
		builder.setMessage(message).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				Intent i = new Intent(context, activity);
				((Activity)(context)).finish();
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
				context.startActivity(i);
			}
		});
		// create alert dialog
		AlertDialog alertDialog = builder.create();

		// show it
		alertDialog.show();
	}
	
	public static void displayErrorStay(String message, Context context) {
		// no deals found so display a popup and return to search options
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		// set title
		builder.setTitle("No Results");

		// set dialog message
		builder.setMessage(message).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		// create alert dialog
		AlertDialog alertDialog = builder.create();

		// show it
		alertDialog.show();
	}
}
