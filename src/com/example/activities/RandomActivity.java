package com.example.activities;

import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class RandomActivity extends Activity implements LocationListener,
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {
	// Declare Variables
	List<ParseObject> ob;
	Integer position;
	ArrayAdapter<String> adapter;
	private Location currentLocation = null;
	Intent intent;
	ProgressDialog randomProgressDialog;

	// Stores the current instantiation of the location client in this object
	private LocationClient locationClient;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		intent = getIntent();
		locationClient = new LocationClient(this, this, this);
	}

	private ParseGeoPoint geoPointFromLocation(Location loc) {
		return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
	}

	private Location getLocation() {
		return locationClient.getLastLocation();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// Create a progressdialog
		if (randomProgressDialog != null) {
			randomProgressDialog.dismiss();
			randomProgressDialog = null;
		}
		randomProgressDialog = new ProgressDialog(RandomActivity.this);
		// Set progressdialog message
		randomProgressDialog.setMessage("Loading Yelp Data...");
		randomProgressDialog.setIndeterminate(false);
		randomProgressDialog.setCancelable(false);
		// Show progressdialog
		randomProgressDialog.show();

		String distance, day_of_week, query;
		Boolean food, drinks;
		ParseObject deal_type = null;

		currentLocation = getLocation();
		distance = intent.getStringExtra("distance");
		day_of_week = intent.getStringExtra("day_of_week");
		food = intent.getBooleanExtra("food", true);
		drinks = intent.getBooleanExtra("drinks", true);
		query = intent.getStringExtra("query");

		// Locate the class table named "establishment" in Parse.com
		ParseQuery<ParseObject> queryRandomSearch = new ParseQuery<ParseObject>("Deal");
		queryRandomSearch.setLimit(10);
		if (query != "") {
			queryRandomSearch.whereContains("title", query);
		}
		if (day_of_week != null) {
			queryRandomSearch.whereContains("day", day_of_week);
		}
		if (distance != null) {
			queryRandomSearch.whereWithinMiles("location", geoPointFromLocation(currentLocation),
					Double.parseDouble(distance));
		}
		if ((food == true) || (drinks == true)) {
			if (food == false) {
				ParseQuery<ParseObject> queryDealType = ParseQuery.getQuery("deal_type");
				queryDealType.whereEqualTo("name", "Drinks");
				try {
					deal_type = queryDealType.getFirst();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				queryRandomSearch.whereEqualTo("deal_type", deal_type);
			}
			if (drinks == false) {
				ParseQuery<ParseObject> queryDealType = ParseQuery.getQuery("deal_type");
				queryDealType.whereEqualTo("name", "Food");
				try {
					deal_type = queryDealType.getFirst();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				queryRandomSearch.whereEqualTo("deal_type", deal_type);
			}
		}

		final ParseQuery<ParseObject> queryRandomSearchCount = queryRandomSearch;

		queryRandomSearch.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> dealList, ParseException e) {
				if (e == null) {
					ob = dealList;

					queryRandomSearchCount.countInBackground(new CountCallback() {
						public void done(int count, ParseException e) {
							if (e == null) {
								// The count request succeeded. Log the
								// count
								if (count > 0) {
									Random random = new Random();
									position = random.nextInt(count);
									Intent i = new Intent(RandomActivity.this,
											DealsDetailsActivity.class);
									ParseObject establishment = (ParseObject) ob.get(position).get(
											"establishment");
									// Pass data "name" followed by the
									// position
									i.putExtra("deal_id", ob.get(position).getObjectId().toString());
									i.putExtra("deal_details", ob.get(position)
											.getString("details").toString());
									i.putExtra("deal_title", ob.get(position).getString("title")
											.toString());
									i.putExtra("establishment_id", establishment.getObjectId());
									i.putExtra("deal_restrictions",
											ob.get(position).getInt("description"));
									i.putExtra("yelp_id", ob.get(position).getString("yelp_id"));
									// Open SingleItemView.java Activity
									ob = null;
									startActivity(i);
									RandomActivity.this.finish();
								} else {
									displayError();
								}
							} else {
								Log.d("Deal Count Error", e.toString());
							}
						}
					});
				} else {
					Log.d("Deal Search Error", e.toString());
				}
			}
		});

	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStop() {
		// After disconnect() is called, the client is considered "dead".
		locationClient.disconnect();

		super.onStop();
	}

	/*
	 * Called when the Activity is restarted, even before it becomes visible.
	 */
	@Override
	public void onStart() {
		super.onStart();

		// Connect to the location services client
		locationClient.connect();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (randomProgressDialog != null) {
			randomProgressDialog.dismiss();
			randomProgressDialog = null;
		}
	}

	public void displayError() {
		// no deals found so display a popup and return to search options
		AlertDialog.Builder builder = new AlertDialog.Builder(RandomActivity.this);

		// set title
		builder.setTitle("No Results");

		// set dialog message
		builder.setMessage("Sorry, nothing was found.  Try and widen your search.")
				.setCancelable(false)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						finish();
					}
				});
		// create alert dialog
		AlertDialog alertDialog = builder.create();

		// show it
		alertDialog.show();
	}
}
