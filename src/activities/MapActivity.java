package activities;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.thebarapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
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

public class MapActivity extends FragmentActivity implements LocationListener,
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

	private GoogleMap myMap;
	List<ParseObject> ob, obSingle;
    ArrayAdapter<String> adapter;
    Map<Marker, Business> theMap = new HashMap<Marker, Business>();
    Button redoMapButton, filterMapButton;
    Integer day;
    String weekday, query = "", distanceMiles = "3", establishment_id, lat = null, lng = null, yelpQuery = "", estId;
    int obCount, distanceMeters = 4828;
    Boolean filter = false;
    YelpParser yParser;
    ArrayList<Business> businesses = new ArrayList<Business>();
    ArrayList<Business> tempBusiness = new ArrayList<Business>();
    private Location currentLocation;
    private LocationClient locationClient;
    LocationRequest mLocationRequest;
    Intent intent;
    ProgressDialog mapProgressDialog;
    Business checkBusiness, bus;
    Calendar calendar = Calendar.getInstance();
	String day_of_week;
	Boolean food, drinks, onlyDeals;
	ParseObject deal_type = null;
	Intent i;
    
 // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
	
 @Override
 protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_map);
  
  intent = getIntent();
  
  locationClient = new LocationClient(this, this, this); 
  
  mLocationRequest = LocationRequest.create();
  // Use high accuracy
  mLocationRequest.setPriority(
          LocationRequest.PRIORITY_HIGH_ACCURACY);
  // Set the update interval to 5 seconds
  mLocationRequest.setInterval(UPDATE_INTERVAL);
  // Set the fastest update interval to 1 second
  mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
  
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
  
//Setting a custom info window adapter for the google map
  myMap.setInfoWindowAdapter(new InfoWindowAdapter() {

      // Use default InfoWindow frame
      @Override
      public View getInfoWindow(Marker arg0) {
          return null;
      }

      // Defines the contents of the InfoWindow
      @Override
      public View getInfoContents(Marker arg0) {

          // Getting view from the layout file info_window_layout
          View v = getLayoutInflater().inflate(R.layout.map_marker_info, null);

          // Getting reference to the TextView to set latitude
          TextView markerName = (TextView) v.findViewById(R.id.markerName);
          
          markerName.setText(arg0.getTitle());

          myMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
              @Override
              public void onInfoWindowClick(Marker marker) {
            	  i = new Intent(MapActivity.this,
                  		DetailsActivity.class);
                      // Pass data "name" followed by the position
            	  bus = theMap.get(marker);
            	  
            	  ParseQuery<ParseObject> querySingle = new ParseQuery<ParseObject>(
  	                    "Establishment");
  	            querySingle.whereEqualTo("yelp_id", bus.getYelpId().toString());
  	            querySingle.findInBackground(new FindCallback<ParseObject>() {
  	                  public void done(List<ParseObject> estList, ParseException e) {
  	                    if (e == null) {
  	                        obSingle = estList;
  	                      if(obSingle.size() == 0){
  	      	            	estId = "empty";
  	      	            } else {
  	      	            	estId = obSingle.get(0).getObjectId().toString();
  	      	            }

  	                	  i.putExtra("establishment_id", estId);
  	                  	  i.putExtra("yelp_id", bus.getYelpId());
  	                      i.putExtra("name", bus.getName());
  	                      i.putExtra("rating", bus.getRating());
  	                      i.putExtra("address", bus.getAddress());
  	                      i.putExtra("city", bus.getCity());
  	                      i.putExtra("state", bus.getState());
  	                      i.putExtra("zip", bus.getZipcode());
  	                      i.putExtra("phone", bus.getPhone());
  	                      i.putExtra("display_phone", bus.getDisplayPhone());
  	                      i.putExtra("distance", bus.getDistance());
  	                      i.putExtra("mobile_url", bus.getMobileURL());
  	                      i.putExtra("day_of_week", (day_of_week == "") ? setDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)) : day_of_week);
  	                      
  	                      businesses.clear();
  	                          // Open SingleItemView.java Activity
  	                          startActivity(i);
  	                    } else {
  	                        Log.d("score", "Error: " + e.getMessage());
  	                    }
  	                }
  	            });
  	            
  	            
              }
          });

          // Returning the view containing InfoWindow contents
          return v;
      }
  });
  

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
     	i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
         startActivity(i);
         return true;
     case R.id.action_clear_search:
     	Intent j = new Intent(MapActivity.this, MapActivity.class);
     	finish();
     	j.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
     	startActivity(j);
     	return true;
     default:
         return super.onOptionsItemSelected(item);
     }
 }
 
//RemoteDataTask AsyncTask
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
				obCount = queryDealSearch.count();
				ob = queryDealSearch.find();
			} catch (Exception e) {
				//Log.e("Error", e.getMessage());
				//e.printStackTrace();
			}

			if (obCount > 0) {
				for (int j = 0; obCount > j; j++) {
					yelpQuery = ob.get(j).getString("yelp_id").toString();
					tempBusiness = searchYelp(false, Double.toString(ob.get(j).getParseGeoPoint("location").getLatitude()), Double.toString(ob.get(j).getParseGeoPoint("location").getLongitude()), yelpQuery, true);
					if ((tempBusiness.size() > 0) && (!businesses.contains(tempBusiness.get(0)))) {
						ParseObject curDeal = ob.get(j);
						ParseObject curEst = curDeal.getParseObject("establishment");
						String estabDealCount = curEst.getString("deal_count");
						Business b = tempBusiness.get(0);
						if((query != "") && (b.getName().toLowerCase().contains(query.toLowerCase()))){
							tempBusiness.get(0).setDealCount(estabDealCount);
							businesses.add(tempBusiness.get(0));
						} else if(query == ""){
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
			if(businesses.size() < 1){
				displayError();
			} else {
			if(currentLocation!=null){
	      		LatLng coordinate = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
	      		CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 15);
	      		  myMap.animateCamera(yourLocation);
	      }
	  	 FragmentManager myFragmentManager = getSupportFragmentManager();
	  	  SupportMapFragment mySupportMapFragment 
	  	   = (SupportMapFragment)myFragmentManager.findFragmentById(R.id.map);
	  	  myMap = mySupportMapFragment.getMap();
	  	  
	  	  myMap.setMyLocationEnabled(true);
	  	  
	  	  myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	  	  
	  	  myMap.getUiSettings().setZoomControlsEnabled(true);
	  	  myMap.getUiSettings().setCompassEnabled(true);
	  	  myMap.getUiSettings().setMyLocationButtonEnabled(true);
	  	  myMap.getUiSettings().setTiltGesturesEnabled(false);
	  	  
			for (int i =0; businesses.size() > i; i++) {
				Business b = businesses.get(i);
				Marker marker = myMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(b.getLatitude()), Double.parseDouble(b.getLongitude()))).title(b.getName()));
				theMap.put(marker, businesses.get(i));
  	  }
  	  if(mapProgressDialog != null){
	    	  mapProgressDialog.dismiss();
	    	  mapProgressDialog = null;
  	  }
		}
		}
	}
 
 @Override
 public void onLocationChanged(Location location) {

 }
 
 public void loadMapOnUser(Location location) {

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
	new RemoteDataTask(MapActivity.this).execute();
	
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
	if(mapProgressDialog != null){
  	  mapProgressDialog.dismiss();
  	  mapProgressDialog = null;
	} 
}

private ParseGeoPoint geoPointFromLocation(Location loc) {
    return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
  }

public void displayError() {
	// no deals found so display a popup and return to search options
	AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);

	// set title
	builder.setTitle("No Results");

	// set dialog message
	builder.setMessage("Sorry, nothing was found.  Try and widen your search.")
			.setCancelable(false)
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
					Intent i = new Intent(MapActivity.this, MapSearchActivity.class);
			     	finish();
			     	i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
			         startActivity(i);
				}
			});
	// create alert dialog
	AlertDialog alertDialog = builder.create();

	// show it
	alertDialog.show();
}

private ArrayList<Business> searchYelp(boolean location, String lat, String lng, String yelp_id, boolean businessSearch) {
	String response;
	ArrayList<Business> result = new ArrayList<Business>();
	API_Static_Stuff api_keys = new API_Static_Stuff();

	Yelp yelp = new Yelp(api_keys.getYelpConsumerKey(), api_keys.getYelpConsumerSecret(), api_keys.getYelpToken(), api_keys.getYelpTokenSecret());
	yParser = new YelpParser();
	if(businessSearch){
		response = yelp.businessSearch(yelp_id);
		result = yParser.getBusinesses(response, location, lat, lng, businessSearch);
	}else {
		response = yelp.search(yelp_id, currentLocation.getLatitude(), currentLocation.getLongitude(), String.valueOf(distanceMeters), 0);
		result = yParser.getBusinesses(response, location, lat, lng, businessSearch);
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

}