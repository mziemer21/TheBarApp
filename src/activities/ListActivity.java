package activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import navigation.NavDrawer;
import yelp.API_Static_Stuff;
import yelp.Yelp;
import yelp.YelpParser;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.thebarapp.Business;
import com.thebarapp.BusinessBestMatchComparator;
import com.thebarapp.BusinessDistanceComparator;
import com.thebarapp.BusinessRatingComparator;
import com.thebarapp.EstablishmentListViewAdapter;
import com.thebarapp.EstablishmentRowItem;
import com.thebarapp.R;

public class ListActivity extends NavDrawer implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
	// Declare Variables
	ListView listview;
	List<ParseObject> ob = new ArrayList<ParseObject>();
	String query = "", distanceMiles, establishment_id, obLat, obLng, yelpQuery = "", day_of_week;
	Object loc;
	SearchView searchView;
	private Location currentLocation = null;
	Intent intent;
	Integer countPrev = 0, sort_mode, distanceMeters, loadOffset = 0;
	Boolean filter = false, onlyDeals;
	YelpParser yParser;
	ArrayList<Business> businesses = new ArrayList<Business>(), tempBusiness = new ArrayList<Business>();
	Business checkBusiness;
	ProgressDialog listProgressDialog;
	Calendar calendar = Calendar.getInstance();
	Boolean food, drinks, resumed = false;
	ParseObject deal_type = null;

	// Stores the current instantiation of the location client in this object
	private LocationClient locationClient;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Get the view from listview_main.xml
		setContentView(R.layout.listview_main);
		super.onCreate(savedInstanceState);

		intent = getIntent();

		locationClient = new LocationClient(this, this, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.filter_actions, menu);

		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * On selecting action bar icons
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Take appropriate action for each action item click
		switch (item.getItemId()) {
		case R.id.action_filter:
			loadOffset = 0;
			Intent i = new Intent(ListActivity.this, ListSearchActivity.class);
			finish();
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(i);
			return true;
		case R.id.action_clear_search:
			loadOffset = 0;
			Intent j = new Intent(ListActivity.this, ListActivity.class);
			finish();
			j.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(j);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// RemoteDataTask AsyncTask
	private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
		Context context;

		public RemoteDataTask(Context context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Create a progressdialog
			if (listProgressDialog != null) {
				listProgressDialog.dismiss();
				listProgressDialog = null;
			}
			listProgressDialog = new ProgressDialog(context);
			// Set progressdialog message
			listProgressDialog.setMessage("Searching Yelp...");
			listProgressDialog.setIndeterminate(false);
			listProgressDialog.setCancelable(false);
			// Show progressdialog
			listProgressDialog.show();

			if (loadOffset == 0) {
				businesses.clear();
			}
		}

		@Override
		protected Void doInBackground(Void... params) {

			currentLocation = getLocation();
			day_of_week = (intent.getStringExtra("day_of_week") == null) ? setDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)) : intent.getStringExtra("day_of_week");
			food = intent.getBooleanExtra("food", true);
			drinks = intent.getBooleanExtra("drinks", true);
			sort_mode = intent.getIntExtra("search_type", 1);
			onlyDeals = intent.getBooleanExtra("only_deals", false);
			query = (intent.getStringExtra("query") == null) ? "" : intent.getStringExtra("query");
			distanceMiles = (intent.getStringExtra("distance") == null) ? "3" : intent.getStringExtra("distance");
			distanceMeters = Integer.parseInt(distanceMiles) * 1609;

			// Locate the class table named "establishment" in Parse.com
			ParseQuery<ParseObject> queryDealSearch = new ParseQuery<ParseObject>("Deal");
			queryDealSearch.include("establishment");
			queryDealSearch.setLimit(20);
			if(ob.size() > 0){
				queryDealSearch.setSkip(ob.size());
			}
			if (day_of_week != "") {
				queryDealSearch.whereEqualTo("day", day_of_week);
			}
			if (distanceMiles != null) {
				queryDealSearch.whereWithinMiles("location", geoPointFromLocation(currentLocation), Double.parseDouble(distanceMiles));
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
					queryDealSearch.whereEqualTo("deal_type", deal_type);
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
					queryDealSearch.whereEqualTo("deal_type", deal_type);
				}
			}
			try {
				ob = queryDealSearch.find();
			} catch (Exception e) {
				// Log.e("Error", e.getMessage());
				// e.printStackTrace();
			}

			if (ob.size() > 0) {
				for (int j = 0; ob.size() > j; j++) {
					yelpQuery = ob.get(j).getString("yelp_id").toString();
					ParseObject curDeal = ob.get(j);
					ParseObject curEst = curDeal.getParseObject("establishment");
					String estabDealCount = curEst.getString("deal_count");

					tempBusiness = searchYelp(false, Double.toString(curDeal.getParseGeoPoint("location").getLatitude()), Double.toString(curDeal.getParseGeoPoint("location").getLongitude()),
							yelpQuery, true);
					if ((tempBusiness.size() > 0) && (!businesses.contains(tempBusiness.get(0)))) {

						if ((query != "") && (tempBusiness.get(0).getName().toLowerCase().contains(query.toLowerCase()))) {
							tempBusiness.get(0).setDealCount(estabDealCount);
							businesses.add(tempBusiness.get(0));
						} else if (query == "") {
							tempBusiness.get(0).setDealCount(estabDealCount);
							businesses.add(tempBusiness.get(0));
						}
					}
				}
			}

			if ((ob.size() < 20) && (!onlyDeals)) {
				if (intent.getStringExtra("query") != null) {
					yelpQuery = intent.getStringExtra("query");
				} else {
					yelpQuery = "";
				}

				tempBusiness = searchYelp(true, "", "", "", false);
				for (int m = 0; m < tempBusiness.size() - 1; m++) {
					checkBusiness = (Business) tempBusiness.get(m);
					if (!businesses.contains(checkBusiness)) {
						checkBusiness.setDealCount("0");
						businesses.add(checkBusiness);
					}
				}
			}

			if (sort_mode == 0) {
				Collections.sort(businesses, new BusinessBestMatchComparator());
			} else if (sort_mode == 1) {
				Collections.sort(businesses, new BusinessDistanceComparator());
			} else if (sort_mode == 2) {
				Collections.sort(businesses, new BusinessRatingComparator());
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			resumed = true;
			if ((businesses.size() - countPrev) < 1) {
				displayErrorStay();
				if (listProgressDialog != null) {
					// Close the progressdialog
					listProgressDialog.dismiss();
				}
			} else if (businesses.size() < 1) {
				displayError();
				if (listProgressDialog != null) {
					// Close the progressdialog
					listProgressDialog.dismiss();
				}
			} else {
				countPrev = businesses.size();
				// Locate the listview in listview_main.xml
				listview = (ListView) findViewById(R.id.listview);
				// Pass the results into an ArrayAdapter
				List<EstablishmentRowItem> rowItems = new ArrayList<EstablishmentRowItem>();

				// Retrieve object "title" from Parse.com database
				for (int k = 0; businesses.size() > k; k++) {
					EstablishmentRowItem item = new EstablishmentRowItem(businesses.get(k).getName(), Double.parseDouble(businesses.get(k).getRating()), businesses.get(k).getAddress(), businesses
							.get(k).getDistance(), businesses.get(k).getDealCount(), businesses.get(k).getRatingCount());
					rowItems.add(item);
				}

				if ((loadOffset == 0) && ((!onlyDeals) || ((onlyDeals) && (ob.size() >= 20)))) {

					// Getting listview from xml
					ListView lv = (ListView) findViewById(R.id.listview);

					// Creating a button - Load More
					Button loadMoreButton = new Button(ListActivity.this);
					loadMoreButton.setText("Load More");

					// Adding button to listview at footer
					lv.addFooterView(loadMoreButton);
					
					loadMoreButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							resumed = false;
							loadOffset += 20;
							locationClient.disconnect();
							locationClient.connect();
						}
					});
				}

				// Pass the results into an ArrayAdapter
				EstablishmentListViewAdapter establishmentAdapter = new EstablishmentListViewAdapter(ListActivity.this, R.layout.listview_item_establishment, rowItems);
				// Binds the Adapter to the ListView
				listview.setAdapter(establishmentAdapter);
				if (listProgressDialog != null) {
					// Close the progressdialog
					listProgressDialog.dismiss();
				}
				// Capture button clicks on ListView items
				listview.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

						ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Establishment");
						query.whereEqualTo("yelp_id", businesses.get(position).getYelpId());
						try {
							ob = query.find();
						} catch (Exception e) {
							Log.e("Error", e.getMessage());
							e.printStackTrace();
						}

						if (ob.size() == 0) {
							establishment_id = "empty";
						} else {
							establishment_id = ob.get(0).getObjectId().toString();
						}

						currentLocation = getLocation();
						// Send single item click data to SingleItemView Class
						Intent i = new Intent(ListActivity.this, DetailsActivity.class);
						// Pass data "name" followed by the position
						i.putExtra("establishment_id", establishment_id);
						i.putExtra("est_name", businesses.get(position).getName());
						i.putExtra("yelp_id", businesses.get(position).getYelpId());
						i.putExtra("name", businesses.get(position).getName());
						i.putExtra("rating", businesses.get(position).getRating());
						i.putExtra("rating_count", businesses.get(position).getRatingCount());
						i.putExtra("address", businesses.get(position).getAddress());
						i.putExtra("city", businesses.get(position).getCity());
						i.putExtra("state", businesses.get(position).getState());
						i.putExtra("zip", businesses.get(position).getZipcode());
						i.putExtra("phone", businesses.get(position).getPhone());
						i.putExtra("display_phone", businesses.get(position).getDisplayPhone());
						i.putExtra("distance", businesses.get(position).getDistance());
						i.putExtra("mobile_url", businesses.get(position).getMobileURL());
						i.putExtra("day_of_week", (day_of_week == "") ? setDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)) : day_of_week);
						i.putExtra("est_lat", businesses.get(position).getLatitude());
						i.putExtra("est_lng", businesses.get(position).getLongitude());
						i.putExtra("cur_lat", String.valueOf(currentLocation.getLatitude()));
						i.putExtra("cur_lng", String.valueOf(currentLocation.getLongitude()));
						i.putExtra("mob_url", businesses.get(position).getMobileURL());
						
						// Open SingleItemView.java Activity
						startActivity(i);
					}
				});
			}
		}
	}

	private Location getLocation() {
		return locationClient.getLastLocation();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		if(!resumed){
			new RemoteDataTask(ListActivity.this).execute();
		}
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	private ParseGeoPoint geoPointFromLocation(Location loc) {
		return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
	}

	@Override
	public void onStop() {
		super.onStop();
		// After disconnect() is called, the client is considered "dead".
		locationClient.disconnect();
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
		if (listProgressDialog != null) {
			listProgressDialog.dismiss();
			listProgressDialog = null;
		}
	}

	private ArrayList<Business> searchYelp(boolean location, String lat, String lng, String yelp_id, boolean businessSearch) {
		String response;
		ArrayList<Business> result = new ArrayList<Business>();
		API_Static_Stuff api_keys = new API_Static_Stuff();

		Yelp yelp = new Yelp(api_keys.getYelpConsumerKey(), api_keys.getYelpConsumerSecret(), api_keys.getYelpToken(), api_keys.getYelpTokenSecret());
		yParser = new YelpParser();
		if (businessSearch) {
			response = yelp.businessSearch(yelp_id);
			result = yParser.getBusinesses(response, location, lat, lng, businessSearch, currentLocation.getLatitude(), currentLocation.getLongitude());
		} else {
			response = yelp.search(yelp_id, currentLocation.getLatitude(), currentLocation.getLongitude(), String.valueOf(distanceMeters), sort_mode, loadOffset);
			result = yParser.getBusinesses(response, location, lat, lng, businessSearch, currentLocation.getLatitude(), currentLocation.getLongitude());
		}

		return result;
	}

	private String setDayOfWeek(int i) {
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

	public void displayError() {
		// no deals found so display a popup and return to search options
		AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);

		// set title
		builder.setTitle("No Results");

		// set dialog message
		builder.setMessage("Sorry, nothing was found.  Try and widen your search.").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				Intent i = new Intent(ListActivity.this, ListSearchActivity.class);
				finish();
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(i);
			}
		});
		// create alert dialog
		AlertDialog alertDialog = builder.create();

		// show it
		alertDialog.show();
	}
	
	public void displayErrorStay() {
		// no deals found so display a popup and return to search options
		AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);

		// set title
		builder.setTitle("No Results");

		// set dialog message
		builder.setMessage("Sorry, nothing was found.  Try and widen your search.").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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