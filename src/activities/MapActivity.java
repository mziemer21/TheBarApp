package activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import navigation.NavDrawer;
import yelp.API_Static_Stuff;
import yelp.Yelp;
import yelp.YelpParser;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.thebarapp.Business;
import com.thebarapp.Helper;
import com.thebarapp.MyMarker;
import com.thebarapp.R;

public class MapActivity extends NavDrawer implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener,
		com.google.android.gms.location.LocationListener {

	private GoogleMap myMap;
	private List<ParseObject> ob = new ArrayList<ParseObject>(), obSingle;
	private Map<Marker, Business> theMap = new HashMap<Marker, Business>();
	private HashMap<Marker, MyMarker> mMarkersHashMap;
	private Button redoMapButton;
	private String query = "", distanceMiles = "3", yelpQuery = "", estId, day_of_week;
	private Integer distanceMeters = 4828;
	private YelpParser yParser;
	private ArrayList<Business> businesses = new ArrayList<Business>(), tempBusiness = new ArrayList<Business>();
	private Location currentLocation;
	private LocationClient locationClient;
	private LocationRequest mLocationRequest;
	private Intent intent, newIntent;
	private ProgressDialog mapProgressDialog;
	private Business checkBusiness, bus;
	private Calendar calendar = Calendar.getInstance();
	private Boolean food, drinks, onlyDeals;
	private ParseObject deal_type = null;

	// Milliseconds per second
	private static final int MILLISECONDS_PER_SECOND = 1000;
	// Update frequency in seconds
	public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
	// Update frequency in milliseconds
	private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
	// The fastest update frequency, in seconds
	private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
	// A fast frequency ceiling in milliseconds
	private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		intent = getIntent();

		locationClient = new LocationClient(this, this, this);

		mLocationRequest = LocationRequest.create();
		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		// Set the update interval to 5 seconds
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		// Set the fastest update interval to 1 second
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

		mMarkersHashMap = new HashMap<Marker, MyMarker>();

		redoMapButton = (Button) findViewById(R.id.redo_map_button);

		redoMapButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				LatLng currentLatLng = myMap.getCameraPosition().target;
				currentLocation.setLatitude(currentLatLng.latitude);
				currentLocation.setLongitude(currentLatLng.longitude);
				new RemoteDataTask(MapActivity.this).execute();

			}
		});

		// Getting reference to the SupportMapFragment of activity_main.xml
		SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

		// Getting GoogleMap object from the fragment
		myMap = fm.getMap();

		// Enabling MyLocation Layer of Google Map
		myMap.setMyLocationEnabled(true);

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
			Intent i = new Intent(MapActivity.this, MapSearchActivity.class);
			finish();
			newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(i);
			return true;
		case R.id.action_clear_search:
			Intent j = new Intent(MapActivity.this, MapActivity.class);
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
			if (mapProgressDialog != null) {
				mapProgressDialog.dismiss();
				mapProgressDialog = null;
			}
			mapProgressDialog = new ProgressDialog(context);
			// Set progressdialog message
			mapProgressDialog.setMessage("Searching Yelp...");
			mapProgressDialog.setIndeterminate(false);
			mapProgressDialog.setCancelable(false);
			// Show progressdialog
			mapProgressDialog.show();

			businesses.clear();
		}

		@Override
		protected Void doInBackground(Void... params) {

			currentLocation = getLocation();
			day_of_week = (intent.getStringExtra("day_of_week") == null) ? setDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)) : intent.getStringExtra("day_of_week");
			food = intent.getBooleanExtra("food", true);
			drinks = intent.getBooleanExtra("drinks", true);
			onlyDeals = intent.getBooleanExtra("only_deals", false);

			query = (intent.getStringExtra("query") == null) ? "" : intent.getStringExtra("query");
			distanceMiles = (intent.getStringExtra("distance") == null) ? "3" : intent.getStringExtra("distance");
			distanceMeters = Integer.parseInt(distanceMiles) * 1609;
			// Locate the class table named "establishment" in Parse.com
			ParseQuery<ParseObject> queryDealSearch = new ParseQuery<ParseObject>("Deal");
			queryDealSearch.include("establishment");
			queryDealSearch.setLimit(20);
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
					tempBusiness = searchYelp(false, Double.toString(ob.get(j).getParseGeoPoint("location").getLatitude()), Double.toString(ob.get(j).getParseGeoPoint("location").getLongitude()),
							yelpQuery, true);
					if ((tempBusiness.size() > 0) && (!businesses.contains(tempBusiness.get(0)))) {
						ParseObject curDeal = ob.get(j);
						ParseObject curEst = curDeal.getParseObject("establishment");
						String estabDealCount = curEst.getString("deal_count");
						Business b = tempBusiness.get(0);
						if ((query != "") && (b.getName().toLowerCase().contains(query.toLowerCase()))) {
							tempBusiness.get(0).setDealCount(estabDealCount);
							businesses.add(tempBusiness.get(0));
						} else if (query == "") {
							tempBusiness.get(0).setDealCount(estabDealCount);
							businesses.add(tempBusiness.get(0));
						}
					}
				}
			}

			if ((businesses.size() < 20) && (!onlyDeals)) {
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

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (businesses.size() < 1) {
				Helper.displayError("Sorry, nothing was found.  Try and widen your search.", MapSearchActivity.class, MapActivity.this);
			} else {
				if (currentLocation != null) {
					LatLng coordinate = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
					CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 15);
					myMap.animateCamera(yourLocation);
				}

				myMap.setMyLocationEnabled(true);

				myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

				myMap.getUiSettings().setZoomControlsEnabled(true);
				myMap.getUiSettings().setCompassEnabled(true);
				myMap.getUiSettings().setMyLocationButtonEnabled(true);
				myMap.getUiSettings().setTiltGesturesEnabled(false);

				for (int i = 0; businesses.size() > i; i++) {
					Business b = businesses.get(i);
					Marker marker = myMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(b.getLatitude()), Double.parseDouble(b.getLongitude()))).title(b.getName()));
					theMap.put(marker, b);
					MyMarker myMarker = new MyMarker(b.getName(), b.getRatingCount(), b.getDealCount(), b.getRating(), Double.parseDouble(b.getLatitude()), Double.parseDouble(b.getLongitude()));
					// mMyMarkersArray.add(myMarker);
					mMarkersHashMap.put(marker, myMarker);
					myMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
				}

				setUpMap();

				// plotMarkers(mMyMarkersArray);

				if (mapProgressDialog != null) {
					mapProgressDialog.dismiss();
					mapProgressDialog = null;
				}
			}
		}
	}

	@Override
	public void onLocationChanged(Location location) {

	}

	private void loadMapOnUser(Location location) {

		// Getting latitude of the current location
		double latitude = location.getLatitude();

		// Getting longitude of the current location
		double longitude = location.getLongitude();

		// Creating a LatLng object for the current location
		LatLng latLng = new LatLng(latitude, longitude);

		// Showing the current location in Google Map
		myMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

		// Zoom in the Google Map
		myMap.animateCamera(CameraUpdateFactory.zoomTo(15));

	}

	@Override
	public void onProviderDisabled(String arg0) {
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

	private Location getLocation() {
		return locationClient.getLastLocation();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		locationClient.requestLocationUpdates(mLocationRequest, this);
		currentLocation = getLocation();
		if (Helper.isConnectedToInternet(MapActivity.this)) {
			new RemoteDataTask(MapActivity.this).execute();
		} else {
			Helper.displayError("Sorry, nothing was found.  Could not connect to the internet.", MainActivity.class, MapActivity.this);
		}

	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

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
		if (mapProgressDialog != null) {
			mapProgressDialog.dismiss();
			mapProgressDialog = null;
		}
	}

	private static ParseGeoPoint geoPointFromLocation(Location loc) {
		return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
	}

	private ArrayList<Business> searchYelp(boolean location, String lat, String lng, String yelp_id, boolean businessSearch) {
		String response;
		ArrayList<Business> result = new ArrayList<Business>();
		API_Static_Stuff api_keys = new API_Static_Stuff();

		Yelp yelp = new Yelp(api_keys.getYelpConsumerKey(), api_keys.getYelpConsumerSecret(), api_keys.getYelpToken(), api_keys.getYelpTokenSecret());
		YelpParser yParser = new YelpParser();
		if (businessSearch) {
			response = yelp.businessSearch(yelp_id);
			result = yParser.getBusinesses(response, location, lat, lng, businessSearch, currentLocation.getLatitude(), currentLocation.getLongitude());
		} else {
			response = yelp.search(yelp_id, currentLocation.getLatitude(), currentLocation.getLongitude(), String.valueOf(distanceMeters), 0, 0);
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

	/*
	 * private void plotMarkers(ArrayList<MyMarker> markers) { if
	 * (markers.size() > 0) { for (MyMarker myMarker : markers) {
	 * 
	 * // Create user marker with custom icon and other options MarkerOptions
	 * markerOption = new MarkerOptions().position(new LatLng(myMarker.getLat(),
	 * myMarker.getLng())); //
	 * markerOption.icon(BitmapDescriptorFactory.fromResource
	 * (R.drawable.currentlocation_icon));
	 * 
	 * Marker currentMarker = myMap.addMarker(markerOption);
	 * mMarkersHashMap.put(currentMarker, myMarker);
	 * 
	 * MyMarker myMarker2 = mMarkersHashMap.get(currentMarker); Boolean ya =
	 * myMarker.equals(myMarker2);
	 * 
	 * myMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter()); } } }
	 */

	private void setUpMap() {
		// Check if we were successful in obtaining the map.

		if (myMap != null) {
			myMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
				@Override
				public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
					marker.showInfoWindow();
					return true;
				}
			});
		} else {
			Toast.makeText(getApplicationContext(), "Unable to create Maps", Toast.LENGTH_SHORT).show();
		}
	}

	public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
		public MarkerInfoWindowAdapter() {
		}

		@Override
		public View getInfoWindow(Marker marker) {
			return null;
		}

		@Override
		public View getInfoContents(Marker marker) {
			View v = getLayoutInflater().inflate(R.layout.map_marker_info, null);

			MyMarker myMarker = mMarkersHashMap.get(marker);

			TextView title = (TextView) v.findViewById(R.id.est_map_title);

			TextView dealCount = (TextView) v.findViewById(R.id.est_map_deal_count);

			TextView reviewCount = (TextView) v.findViewById(R.id.est_map_rating_count);

			ImageView rating = (ImageView) v.findViewById(R.id.est_map_rating);

			Double ratingIn = Double.parseDouble(myMarker.getRating());

			TextView ratingWord = (TextView) v.findViewById(R.id.est_map_rating_word);

			if (ratingIn < .5) {
				rating.setImageResource(R.drawable.zero_stars_md);
			} else if (ratingIn < 1) {
				rating.setImageResource(R.drawable.one_stars_md);
			} else if (ratingIn < 1.5) {
				rating.setImageResource(R.drawable.one_half_stars_md);
			} else if (ratingIn < 2) {
				rating.setImageResource(R.drawable.two_stars_md);
			} else if (ratingIn < 2.5) {
				rating.setImageResource(R.drawable.two_half_stars_md);
			} else if (ratingIn < 3) {
				rating.setImageResource(R.drawable.three_stars_md);
			} else if (ratingIn < 3.5) {
				rating.setImageResource(R.drawable.three_half_stars_md);
			} else if (ratingIn < 4) {
				rating.setImageResource(R.drawable.four_stars_md);
			} else if (ratingIn < 4.5) {
				rating.setImageResource(R.drawable.four_half_stars_md);
			} else if (ratingIn < 5) {
				rating.setImageResource(R.drawable.five_stars_md);
			}

			title.setText(myMarker.getName());
			dealCount.setText(myMarker.getDealCount() + " Deals");
			reviewCount.setText(myMarker.getReviewCount());
			if (myMarker.getDealCount().matches("1")) {
				ratingWord.setText("Review");
			}

			myMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
				@Override
				public void onInfoWindowClick(Marker marker) {
					if (Helper.isConnectedToInternet(MapActivity.this)) {
						newIntent = new Intent(MapActivity.this, DetailsActivity.class);
						// Pass data "name" followed by the position
						bus = theMap.get(marker);

						ParseQuery<ParseObject> querySingle = new ParseQuery<ParseObject>("Establishment");
						querySingle.whereEqualTo("yelp_id", bus.getYelpId().toString());
						querySingle.findInBackground(new FindCallback<ParseObject>() {
							public void done(List<ParseObject> estList, ParseException e) {
								if (e == null) {
									obSingle = estList;
									if (obSingle.size() == 0) {
										estId = "empty";
									} else {
										estId = obSingle.get(0).getObjectId().toString();
									}

									newIntent.putExtra("establishment_id", estId);
									newIntent.putExtra("est_name", bus.getName());
									newIntent.putExtra("yelp_id", bus.getYelpId());
									newIntent.putExtra("name", bus.getName());
									newIntent.putExtra("rating", bus.getRating());
									newIntent.putExtra("rating_count", bus.getRatingCount());
									newIntent.putExtra("address", bus.getAddress());
									newIntent.putExtra("city", bus.getCity());
									newIntent.putExtra("state", bus.getState());
									newIntent.putExtra("zip", bus.getZipcode());
									newIntent.putExtra("phone", bus.getPhone());
									newIntent.putExtra("display_phone", bus.getDisplayPhone());
									newIntent.putExtra("distance", bus.getDistance());
									newIntent.putExtra("mobile_url", bus.getMobileURL());
									newIntent.putExtra("day_of_week", (day_of_week == "") ? setDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)) : day_of_week);
									newIntent.putExtra("est_lat", bus.getLatitude());
									newIntent.putExtra("est_lng", bus.getLongitude());
									newIntent.putExtra("cur_lat", String.valueOf(currentLocation.getLatitude()));
									newIntent.putExtra("cur_lng", String.valueOf(currentLocation.getLongitude()));
									newIntent.putExtra("mob_url", bus.getMobileURL());

									businesses.clear();
									// Open SingleItemView.java Activity
									startActivity(newIntent);
								} else {
									Log.d("score", "Error: " + e.getMessage());
								}
							}
						});
					} else {
						Helper.displayErrorStay("Sorry, nothing was found.  Could not connect to the internet.", MapActivity.this);
					}
				}
			});

			return v;
		}

	}

}