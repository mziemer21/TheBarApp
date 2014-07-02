package com.example.activities;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
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

import com.example.thebarapp.Business;
import com.example.thebarapp.R;
import com.example.yelp.API_Static_Stuff;
import com.example.yelp.Yelp;
import com.example.yelp.YelpParser;
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
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class MapActivity extends FragmentActivity implements LocationListener,
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

	private GoogleMap myMap;
	List<ParseObject> ob;
    ArrayAdapter<String> adapter;
    Map<Marker, Business> theMap = new HashMap<Marker, Business>();
    Button redoMapButton, filterMapButton;
    Integer day;
    String weekday, query = "", distanceMiles = "3", establishment_id, lat = null, lng = null;
    int obCount, sort_mode = 0, distanceMeters = 4828;
    Boolean filter = false;
    YelpParser yParser;
    ArrayList<Business> businesses = new ArrayList<Business>();
    ArrayList<Business> tempBusiness = new ArrayList<Business>();
    private Location currentLocation;
    private LocationClient locationClient;
    LocationRequest mLocationRequest;
    Intent intent;
    
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
            	  Intent i = new Intent(MapActivity.this,
                  		DetailsActivity.class);
                      // Pass data "name" followed by the position
            	  Business bus = theMap.get(marker);
            	  
            	  String estId = null;
            	  
            	  ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
  	                    "Establishment");
  	            query.whereEqualTo("yelp_id", bus.getYelpId());
  	            try {
  	                ob = query.find();
  	            } catch (Exception e) {
  	                Log.e("Error", e.getMessage());
  	                e.printStackTrace();
  	            }
  	            
  	            if(ob.size() == 0){
  	            	estId = "empty";
  	            } else {
  	            	estId = ob.get(0).getObjectId().toString();
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
                  
                  businesses.clear();
                      // Open SingleItemView.java Activity
                      startActivity(i);
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
	 ProgressDialog mapProgressDialog;
	 
	  public RemoteDataTask(Context context){
	   this.context=context;
	  }
	 
	 @Override
     protected void onPreExecute() {
         super.onPreExecute();
         
         // Create a progressdialog
         if(mapProgressDialog != null){
         	mapProgressDialog.dismiss();
         }
         mapProgressDialog = new ProgressDialog(context);
         // Set progressdialog message
         mapProgressDialog.setMessage("Searching Yelp...");
         mapProgressDialog.setIndeterminate(false);
         // Show progressdialog
         mapProgressDialog.show();
         
         businesses.clear();
     }

     @Override
     protected Void doInBackground(Void... params) {
    	 String day_of_week;
     	Boolean food, drinks;
     	ParseObject deal_type = null;
     	
     	day_of_week = intent.getStringExtra("day_of_week");
     	food = intent.getBooleanExtra("food", true);
     	drinks = intent.getBooleanExtra("drinks", true);
     	sort_mode = intent.getIntExtra("search_type", 0);
     	
     	if((day_of_week != null) || (food == false) || (drinks == false)) {
     		filter = true;
     	}
     	
     	if(filter){
     		query = intent.getStringExtra("query");
     		distanceMiles  = intent.getStringExtra("distance");
         	distanceMeters = Integer.parseInt(distanceMiles)*1028;
     		// Locate the class table named "establishment" in Parse.com
             ParseQuery<ParseObject> queryDealSearch = new ParseQuery<ParseObject>("Deal");
             queryDealSearch.setLimit(20);
             if(day_of_week != null)
             {
             	queryDealSearch.whereContains("day", day_of_week);
             }
             if(distanceMiles != null)
             {
             	queryDealSearch.whereWithinMiles("location", geoPointFromLocation(currentLocation), Double.parseDouble(distanceMiles));
             }
             if((food == true)|| (drinks == true))
             {
 	            if(food == false)
 	            {
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
 	            if(drinks == false) {
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
                 Log.e("Error", e.getMessage());
                 e.printStackTrace();
             }
             
             if(obCount > 0){
             	for(int j = 0; obCount > j; j++){
             		query = ob.get(j).get("yelp_id").toString();
             		tempBusiness = searchYelp();
             		if(tempBusiness.size() > 0){
             			businesses.add(tempBusiness.get(0));
             		}
             	}
             }
     	} else {
     		if(intent.getStringExtra("query") != null){
     			query = intent.getStringExtra("query");
     		}
     		
     		businesses = searchYelp();
     	}
					
         return null;
     }
     
     @Override
     protected void onPostExecute(Void result) {
    	 
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
    	  
    	  myMap.getUiSettings().setZoomControlsEnabled(false);
    	  myMap.getUiSettings().setCompassEnabled(true);
    	  myMap.getUiSettings().setMyLocationButtonEnabled(true);
    	  myMap.getUiSettings().setTiltGesturesEnabled(false);
    	  
    	  for (int i =0; businesses.size() > i; i++) {
				
				Marker marker = myMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(businesses.get(i).getLatitude()), Double.parseDouble(businesses.get(i).getLongitude()))).title(businesses.get(i).getName()));
				theMap.put(marker, businesses.get(i));
    	  }
    	  
    	  mapProgressDialog.dismiss();
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

private ParseGeoPoint geoPointFromLocation(Location loc) {
    return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
  }

private ArrayList<Business> searchYelp(){
	API_Static_Stuff api_keys = new API_Static_Stuff();
    
    Yelp yelp = new Yelp(api_keys.getYelpConsumerKey(), api_keys.getYelpConsumerSecret(), 
            api_keys.getYelpToken(), api_keys.getYelpTokenSecret());
    String response = yelp.search(query, currentLocation.getLatitude(), currentLocation.getLongitude(), String.valueOf(distanceMeters), sort_mode);
 
    yParser = new YelpParser();
    return yParser.getBusinesses(response);
}

}