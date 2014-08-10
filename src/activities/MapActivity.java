package activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import navigation.NavDrawer;
import yelp.YelpParser;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
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
import com.google.android.gms.maps.model.VisibleRegion;
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
	private Integer distanceMeters = 4828, listSize = 30;
	private YelpParser yParser;
	private ArrayList<Business> businesses = new ArrayList<Business>(), tempBusiness = new ArrayList<Business>();
	private Location currentLocation;
	private LocationClient locationClient;
	private LocationRequest mLocationRequest;
	private Intent intent, newIntent;
	private ProgressDialog mapProgressDialog;
	private Business checkBusiness, bus;
	private Calendar calendar = Calendar.getInstance();
	private Boolean onlyDeals, reload = false, reloadHere = false;
	private ParseObject deal_type = null;
	private LatLng currentLatLng;
	private VisibleRegion vr;

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
				reloadHere = true;
				reload = false;
				currentLatLng = myMap.getCameraPosition().target;
				vr = myMap.getProjection().getVisibleRegion();
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
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
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
			if (reloadHere) {

				currentLocation.setLatitude(currentLatLng.latitude);
				currentLocation.setLongitude(currentLatLng.longitude);

				Location MiddleLeftCorner = new Location("middleleftcorner");
				MiddleLeftCorner.setLatitude(vr.latLngBounds.getCenter().latitude);
				MiddleLeftCorner.setLongitude(vr.latLngBounds.southwest.longitude);
				Location center = new Location("center");
				center.setLatitude(vr.latLngBounds.getCenter().latitude);
				center.setLongitude(vr.latLngBounds.getCenter().longitude);
				distanceMeters = (int) center.distanceTo(MiddleLeftCorner) * 2;
				distanceMiles = String.valueOf(distanceMeters / 1609.0);
			} else {
				currentLocation = getLocation();
				distanceMiles = (intent.getStringExtra("distance") == null) ? "1" : intent.getStringExtra("distance");
				distanceMeters = Integer.parseInt(distanceMiles) * 1609;
			}

			day_of_week = (intent.getStringExtra("day_of_week") == null) ? Helper.setDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)) : intent.getStringExtra("day_of_week");
			onlyDeals = intent.getBooleanExtra("only_deals", false);

			query = (intent.getStringExtra("query") == null) ? "" : intent.getStringExtra("query");

			// Locate the class table named "establishment" in Parse.com
			ParseQuery<ParseObject> queryEstSearch = new ParseQuery<ParseObject>("Establishment");
			if (ob.size() > 0) {
				queryEstSearch.setSkip(ob.size());
			}
			if (distanceMiles != null) {
				queryEstSearch.whereWithinMiles("location", Helper.geoPointFromLocation(currentLocation), Double.parseDouble(distanceMiles));
			}
			ParseQuery<ParseObject> queryEstDeals = new ParseQuery<ParseObject>("establishment_day_deals");
			queryEstDeals.whereMatchesQuery("establishment", queryEstSearch);
			queryEstDeals.include("establishment");
			queryEstDeals.setLimit(20);
			if (day_of_week != "") {
				queryEstDeals.whereGreaterThan(day_of_week.toLowerCase(), 0);
			}

			try {
				ob = queryEstDeals.find();
			} catch (Exception e) {
				// Log.e("Error", e.getMessage());
				// e.printStackTrace();
			}

			if (ob.size() > 0) {
				for (int j = 0; ob.size() > j; j++) {
					ParseObject curEstDeal = ob.get(j);
					ParseObject curEst = curEstDeal.getParseObject("establishment");
					String estabDealCount = String.valueOf(curEstDeal.getInt(day_of_week.toLowerCase()));
					yelpQuery = curEst.getString("yelp_id").toString();

					tempBusiness = Helper.searchYelp(false, Double.toString(curEst.getParseGeoPoint("location").getLatitude()), Double.toString(curEst.getParseGeoPoint("location").getLongitude()),
							yelpQuery, true, currentLocation, distanceMeters, 0, 0);
					if ((tempBusiness.size() > 0) && (!businesses.contains(tempBusiness.get(0)))) {

						if ((query != "") && (tempBusiness.get(0).getName().toLowerCase().contains(query.toLowerCase()))) {
							tempBusiness.get(0).setDealCount(estabDealCount);
							tempBusiness.get(0).setEstablishmentId(curEst.getObjectId());
							businesses.add(tempBusiness.get(0));
						} else if (query == "") {
							tempBusiness.get(0).setDealCount(estabDealCount);
							tempBusiness.get(0).setEstablishmentId(curEst.getObjectId());
							businesses.add(tempBusiness.get(0));
						}
					}
				}
			}

			if ((businesses.size() < listSize) && (!onlyDeals)) {
				if (intent.getStringExtra("query") != null) {
					yelpQuery = intent.getStringExtra("query");
				} else {
					yelpQuery = "";
				}

				tempBusiness = Helper.searchYelp(true, "", "", "", false, currentLocation, distanceMeters, 0, 0);
				for (int m = 0; m < tempBusiness.size() - 1; m++) {
					checkBusiness = (Business) tempBusiness.get(m);
					if (!businesses.contains(checkBusiness)) {
						checkBusiness.setDealCount("0");
						businesses.add(checkBusiness);
					}
				}
			}
			listSize += businesses.size();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (businesses.size() < 1) {
				Helper.displayError("Sorry, nothing was found. Try and widen your search.", MapSearchActivity.class, MapActivity.this);
			} else {

				WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
				Display display = wm.getDefaultDisplay();

				// Display display = getWindowManager().getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);
				int width = size.x;
				Double[] zooms = { 21282.0, 16355.0, 10064.0, 5540.0, 2909.0, 1485.0, 752.0, 378.0, 190.0, 95.0, 48.0, 24.0, 12.0, 6.0, 3.0, 1.48, 0.74, 0.37, 0.19 };
				Integer z = 19;
				Double m;
				while (z > 0) {
					z--;
					m = zooms[z] * width;
					if (distanceMeters < m) {
						break;
					}
				}
				LatLng coordinate = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
				CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, z);
				myMap.animateCamera(yourLocation);

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
			if (!reload) {
				new RemoteDataTask(MapActivity.this).execute();
			}
		} else {
			Helper.displayError("Sorry, nothing was found. Could not connect to the internet.", MainActivity.class, MapActivity.this);
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
									newIntent.putExtra("day_of_week", (day_of_week == "") ? Helper.setDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)) : day_of_week);
									newIntent.putExtra("cur_lat", currentLocation.getLatitude());
									newIntent.putExtra("cur_lng", currentLocation.getLongitude());
									newIntent.putExtra("distance", distanceMiles);
									newIntent.putExtra("est_lat", Double.parseDouble(bus.getLatitude()));
									newIntent.putExtra("est_lng", Double.parseDouble(bus.getLongitude()));

									businesses.clear();
									reload = true;
									// Open SingleItemView.java Activity
									startActivity(newIntent);
								} else {
									Log.d("score", "Error: " + e.getMessage());
								}
							}
						});
					} else {
						Helper.displayErrorStay("Sorry, nothing was found. Could not connect to the internet.", MapActivity.this);
					}
				}
			});
			return v;
		}
	}
}