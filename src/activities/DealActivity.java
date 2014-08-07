package activities;

import java.util.ArrayList;
import java.util.List;

import navigation.NavDrawer;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.thebarapp.DealListViewAdapter;
import com.thebarapp.DealRowItem;
import com.thebarapp.Helper;
import com.thebarapp.ParseApplication;
import com.thebarapp.R;

public class DealActivity extends NavDrawer implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
	// Declare Variables
	private ListView listview;
	private List<ParseObject> ob, obE;
	private List<DealRowItem> rowItems = new ArrayList<DealRowItem>();
	private Location currentLocation = null;
	private Intent intent;
	private ProgressDialog ProgressDialog;

	private String distance, day_of_week, query;
	private Boolean food, drinks, moreButton = false, resumed = false;
	private ParseObject deal_type = null, est;
	private Integer search_type, countPrev = 0, loadOffset = 0, distanceMeters;

	// Stores the current instantiation of the location client in this object
	private LocationClient locationClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Get the view from deal_listview.xml
		setContentView(R.layout.deal_listview);
		super.onCreate(savedInstanceState);

		intent = getIntent();
		locationClient = new LocationClient(this, this, this);
		
		// Get tracker.
		((ParseApplication) getApplication()).getTracker(ParseApplication.TrackerName.APP_TRACKER);
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
		if (Helper.isConnectedToInternet(DealActivity.this)) {
			if (!resumed) {
				// Create a progressdialog
				if (ProgressDialog != null) {
					ProgressDialog.dismiss();
					ProgressDialog = null;
				}
				ProgressDialog = new ProgressDialog(DealActivity.this);
				// Set progressdialog message
				ProgressDialog.setMessage("Loading Yelp Data...");
				ProgressDialog.setIndeterminate(false);
				ProgressDialog.setCancelable(false);
				// Show progressdialog
				ProgressDialog.show();

				currentLocation = getLocation();
				distance = intent.getStringExtra("distance");
				distanceMeters = Integer.parseInt(distance) * 1609;
				day_of_week = intent.getStringExtra("day_of_week");
				food = intent.getBooleanExtra("food", true);
				drinks = intent.getBooleanExtra("drinks", true);
				query = intent.getStringExtra("query");
				search_type = intent.getIntExtra("search_type", 0);

				// Locate the class table named "establishment" in Parse.com
				ParseQuery<ParseObject> queryDealSearch = new ParseQuery<ParseObject>("Deal");
				
				queryDealSearch.setLimit(20);
				if (loadOffset > 0) {
					queryDealSearch.setSkip(loadOffset);
				}
				queryDealSearch.include("establishment");
				if (query != "") {
					queryDealSearch.whereContains("title", query);
				}
				if (day_of_week != null) {
					queryDealSearch.whereContains("day", day_of_week);
				}
				if (distance != null) {
					queryDealSearch.whereWithinMiles("location", geoPointFromLocation(currentLocation), Double.parseDouble(distance));
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

				queryDealSearch.findInBackground(new FindCallback<ParseObject>() {
					public void done(List<ParseObject> dealList, ParseException e) {
						if (e == null) {
							ob = dealList;
							// The count request succeeded. Log the
							// count
							if (((ob.size()) < 1) && (moreButton)) {
								Helper.displayErrorStay("Sorry, nothing was found.  Try and widen your search.", DealActivity.this);
								moreButton = false;
								if (ProgressDialog != null) {
									// Close the progressdialog
									ProgressDialog.dismiss();
								}
							} else if (ob.size() < 1) {
								Helper.displayError("Sorry, nothing was found.  Try and widen your search.", DealSearchActivity.class, DealActivity.this);
								if (ProgressDialog != null) {
									// Close the progressdialog
									ProgressDialog.dismiss();
								}
							} else {
								countPrev = ob.size();
								makeList();
							}
						} else {
							Log.d("Deal Search Error", e.toString());
						}
					}
				});
			}
		} else {
			Helper.displayError("Sorry, nothing was found.  Could not connect to the internet.", DealSearchActivity.class, DealActivity.this);
		}
	}

	private void makeList() {
		resumed = true;
		// Locate the listview in deal_listview.xml
		listview = (ListView) findViewById(R.id.deal_listview);

		if ((loadOffset == 0) && (countPrev >= 20)) {

			// Creating a button - Load More
			Button loadMoreButton = new Button(DealActivity.this);
			loadMoreButton.setText("Load More");

			// Adding button to listview at footer
			listview.addFooterView(loadMoreButton);

			loadMoreButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					resumed = false;
					loadOffset += 20;
					locationClient.disconnect();
					locationClient.connect();
					moreButton = true;
				}
			});
		}
		if (!moreButton) {
			rowItems.clear();
		}

		// Retrieve object "title" from Parse.com
		// database
		for (ParseObject deal : ob) {
			DealRowItem item = new DealRowItem(deal.get("title").toString(), deal.get("rating").toString(), Helper.formatTime(deal.getDate("time_start"), deal.getDate("time_end")));
			rowItems.add(item);
		}

		// Pass the results into an ArrayAdapter
		DealListViewAdapter adapter = new DealListViewAdapter(DealActivity.this, R.layout.listview_item_deal, rowItems);

		// Binds the Adapter to the ListView
		listview.setAdapter(adapter);
		// Close the progressdialog
		if (ProgressDialog != null) {
			ProgressDialog.dismiss();
			ProgressDialog = null;
		}
		// Capture button clicks on ListView items
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (Helper.isConnectedToInternet(DealActivity.this)) {
					Intent i = new Intent(DealActivity.this, DealDetailsActivity.class);
					est = (ParseObject) ob.get(position).get("establishment");
					ParseObject curEst = ob.get(position).getParseObject("establishment");
					
					// Pass data to next activity
					i.putExtra("deal_id", ob.get(position).getObjectId().toString());
					i.putExtra("deal_title", ob.get(position).getString("title").toString());
					i.putExtra("deal_details", ob.get(position).getString("details").toString());
					i.putExtra("deal_restrictions", ob.get(position).getInt("restrictions"));
					i.putExtra("yelp_id", ob.get(position).getString("yelp_id"));
					i.putExtra("establishment_id", est.getObjectId().toString());
					i.putExtra("est_name", curEst.getString("name"));
					i.putExtra("deal_time", Helper.formatTime(ob.get(position).getDate("time_start"), ob.get(position).getDate("time_end")));
					
					/*ArrayList<Business>tempBusiness = Helper.searchYelp(false, Double.toString(curEst.getParseGeoPoint("location").getLatitude()), Double.toString(curEst.getParseGeoPoint("location").getLongitude()),
							ob.get(position).getString("yelp_id"), true, currentLocation, distanceMeters, 0, 0);
					
					i.putExtra("rating", tempBusiness.get(0).getRating());
					i.putExtra("rating_count", tempBusiness.get(0).getRatingCount());
					i.putExtra("address", tempBusiness.get(0).getAddress());
					i.putExtra("city", tempBusiness.get(0).getCity());
					i.putExtra("state", tempBusiness.get(0).getState());
					i.putExtra("zip", tempBusiness.get(0).getZipcode());
					i.putExtra("phone", tempBusiness.get(0).getPhone());
					i.putExtra("display_phone", tempBusiness.get(0).getDisplayPhone());
					i.putExtra("distance", tempBusiness.get(0).getDistance());
					i.putExtra("mobile_url", tempBusiness.get(0).getMobileURL());
					Calendar calendar = Calendar.getInstance();
					i.putExtra("day_of_week", (day_of_week == "") ? Helper.setDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)) : day_of_week);
					i.putExtra("est_lat", tempBusiness.get(0).getLatitude());
					i.putExtra("est_lng", tempBusiness.get(0).getLongitude());
					i.putExtra("cur_lat", String.valueOf(currentLocation.getLatitude()));
					i.putExtra("cur_lng", String.valueOf(currentLocation.getLongitude()));*/
					// Open SingleItemView.java Activity
					startActivity(i);
				} else {
					Helper.displayErrorStay("Sorry, nothing was found.  Could not connect to the internet.", DealActivity.this);
				}
			}
		});
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onStart() {
		super.onStart();
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
		
		// Connect to the location services client
		locationClient.connect();
	}

	@Override
	public void onStop() {
		super.onStop();
		GoogleAnalytics.getInstance(this).reportActivityStop(this);

		// After disconnect() is called, the client is considered "dead".
		locationClient.disconnect();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (ProgressDialog != null) {
			ProgressDialog.dismiss();
			ProgressDialog = null;
		}
	}
}
