package activities;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.thebarapp.R;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.thebarapp.DealListViewAdapter;
import com.thebarapp.DealRowItem;

public class DealActivity extends FragmentActivity implements LocationListener,
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {
	// Declare Variables
	ListView listview;
	List<ParseObject> ob;
	ArrayAdapter<DealRowItem> adapter;
	private Location currentLocation = null;
	Intent intent;
	ParseObject est;
	ProgressDialog dealProgressDialog;

	String distance, day_of_week, query;
	Boolean food, drinks;
	ParseObject deal_type = null;
	Integer search_type;

	// Stores the current instantiation of the location client in this object
	private LocationClient locationClient;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get the view from deal_listview.xml
		setContentView(R.layout.deal_listview);

		intent = getIntent();
		locationClient = new LocationClient(this, this, this);
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
		if (dealProgressDialog != null) {
			dealProgressDialog.dismiss();
			dealProgressDialog = null;
		}
		dealProgressDialog = new ProgressDialog(DealActivity.this);
		// Set progressdialog message
		dealProgressDialog.setMessage("Loading Yelp Data...");
		dealProgressDialog.setIndeterminate(false);
		dealProgressDialog.setCancelable(false);
		// Show progressdialog
		dealProgressDialog.show();

		currentLocation = getLocation();
		distance = intent.getStringExtra("distance");
		day_of_week = intent.getStringExtra("day_of_week");
		food = intent.getBooleanExtra("food", true);
		drinks = intent.getBooleanExtra("drinks", true);
		query = intent.getStringExtra("query");
		search_type = intent.getIntExtra("search_type", 0);

		// Locate the class table named "establishment" in Parse.com
		ParseQuery<ParseObject> queryDealSearch = new ParseQuery<ParseObject>("Deal");
		queryDealSearch.setLimit(10);
		if (query != "") {
			queryDealSearch.whereContains("title", query);
		}
		if (day_of_week != null) {
			queryDealSearch.whereContains("day", day_of_week);
		}
		if (distance != null) {
			queryDealSearch.whereWithinMiles("location", geoPointFromLocation(currentLocation),
					Double.parseDouble(distance));
		}
		if ((food == true) || (drinks == true)) {
			if (food == false) {
				ParseQuery<ParseObject> queryDealType = ParseQuery.getQuery("deal_type");
				queryDealType.whereEqualTo("name", "Drinks");
				try {
					deal_type = queryDealType.getFirst();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				queryDealSearch.whereEqualTo("deal_type", deal_type);
			}
			if (drinks == false) {
				ParseQuery<ParseObject> queryDealType = ParseQuery.getQuery("deal_type");
				queryDealType.whereEqualTo("name", "Food");
				try {
					deal_type = queryDealType.getFirst();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				queryDealSearch.whereEqualTo("deal_type", deal_type);
			}
		}
		if (search_type == 0) {
			// already distance sorted
		} else if (search_type == 1) {
			queryDealSearch.orderByDescending("rating");
		}

		final ParseQuery<ParseObject> queryDealSearchCount = queryDealSearch;

		queryDealSearch.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> dealList, ParseException e) {
				if (e == null) {
					ob = dealList;

					queryDealSearchCount.countInBackground(new CountCallback() {
						public void done(int count, ParseException e) {
							if (e == null) {
								// The count request succeeded. Log the count
								if (count > 0) {
									makeList();
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

	public void makeList() {
		List<DealRowItem> rowItems = new ArrayList<DealRowItem>();

		// Locate the listview in deal_listview.xml
		listview = (ListView) findViewById(R.id.deal_listview);

		// Retrieve object "title" from Parse.com
		// database
		for (ParseObject deal : ob) {
			DealRowItem item = new DealRowItem(deal.get("title").toString(), deal.get("rating")
					.toString());
			rowItems.add(item);
		}

		// Pass the results into an ArrayAdapter
		DealListViewAdapter adapter = new DealListViewAdapter(DealActivity.this,
				R.layout.listview_item_deal, rowItems);

		// Binds the Adapter to the ListView
		listview.setAdapter(adapter);
		// Close the progressdialog
		if (dealProgressDialog != null) {
			dealProgressDialog.dismiss();
			dealProgressDialog = null;
		}
		// Capture button clicks on ListView items
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Send single item click data to
				// SingleItemView
				// Class
				Intent i = new Intent(DealActivity.this, DealsDetailsActivity.class);
				est = (ParseObject) ob.get(position).get("establishment");
				// Pass data to next activity
				i.putExtra("deal_id", ob.get(position).getObjectId().toString());
				i.putExtra("deal_title", ob.get(position).getString("title").toString());
				i.putExtra("deal_details", ob.get(position).getString("details").toString());
				i.putExtra("deal_restrictions", ob.get(position).getInt("restrictions"));
				i.putExtra("yelp_id", ob.get(position).getString("yelp_id"));
				i.putExtra("establishment_id", est.getObjectId().toString());
				
				Date dateStart = ob.get(position).getDate("time_start");
				Date dateEnd = ob.get(position).getDate("time_end");
			    SimpleDateFormat simpDate, simpDateNo;

			    simpDateNo = new SimpleDateFormat("hh:mm");
			    simpDate = new SimpleDateFormat("hh:mm a");	
			    
			    String start = simpDateNo.format(dateStart);
			    String end = simpDate.format(dateEnd);
			    
			    if(start.charAt(0) == '0'){
			    	start.substring(1);
			    }
			    
			    if(simpDate.format(dateEnd).charAt(0) == '0'){
			    	end.substring(1);
			    }
			    	
				
				i.putExtra("deal_time", start + " - " + end);
				// Open SingleItemView.java Activity
				startActivity(i);

			}
		});
	}

	public void displayError() {
		// no deals found so display a popup and return to search options
		AlertDialog.Builder builder = new AlertDialog.Builder(DealActivity.this);

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

	@Override
	public void onDisconnected() {
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
		if (dealProgressDialog != null) {
			dealProgressDialog.dismiss();
			dealProgressDialog = null;
		}
	}
}
