package com.thebarapp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import yelp.API_Static_Stuff;
import yelp.Yelp;
import yelp.YelpParser;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
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
	
	public static String setDayOfWeek(int i) {
		String day_of_week = "";
		if (i == 1) {
			day_of_week = "Sunday";
		} else if (i == 2) {
			day_of_week = "Monday";
		} else if (i == 3) {
			day_of_week = "Tuesday";
		} else if (i == 4) {
			day_of_week = "Wednesday";
		} else if (i == 5) {
			day_of_week = "Thursday";
		} else if (i == 6) {
			day_of_week = "Friday";
		} else if (i == 7) {
			day_of_week = "Saturday";
		}
		return day_of_week;
	}
	
	public static ArrayList<Business> searchYelp(boolean location, String lat, String lng, String yelp_id, boolean businessSearch, Location currentLocation, Integer distanceMeters, Integer sort_mode, Integer loadOffset) {
		String response;
		ArrayList<Business> result = new ArrayList<Business>();
		API_Static_Stuff api_keys = new API_Static_Stuff();

		Yelp yelp = new Yelp(api_keys.getYelpConsumerKey(), api_keys.getYelpConsumerSecret(), api_keys.getYelpToken(), api_keys.getYelpTokenSecret());
		YelpParser yParser = new YelpParser();
		if (businessSearch) {
			response = yelp.businessSearch(yelp_id);
			result = yParser.getBusinesses(response, location, lat, lng, businessSearch, currentLocation.getLatitude(), currentLocation.getLongitude());
		} else {
			response = yelp.search(yelp_id, currentLocation.getLatitude(), currentLocation.getLongitude(), String.valueOf(distanceMeters), sort_mode, loadOffset);
			result = yParser.getBusinesses(response, location, lat, lng, businessSearch, currentLocation.getLatitude(), currentLocation.getLongitude());
		}

		return result;
	}
}
