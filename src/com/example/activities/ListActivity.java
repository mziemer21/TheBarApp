package com.example.activities;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.thebarapp.R;
import com.example.yelp.API_Static_Stuff;
import com.example.yelp.Business;
import com.example.yelp.Yelp;
import com.example.yelp.YelpParser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
 
public class ListActivity extends FragmentActivity implements LocationListener,
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener{
    // Declare Variables
    ListView listview;
    List<ParseObject> ob;
    ProgressDialog mProgressDialog;
    ArrayAdapter<String> adapter;
    String query = "", distanceMiles = "3", establishment_id;
	int distanceMeters = 4828;
    Object loc;
    SearchView searchView;
    private Location currentLocation = null;
    Intent intent;
    int obCount, sort_mode = 0;
    Boolean filter = false;
    YelpParser yParser;
    ArrayList<Business> businesses = new ArrayList<Business>();
    ArrayList<Business> tempBusiness = new ArrayList<Business>();
    
    // Stores the current instantiation of the location client in this object
    private LocationClient locationClient;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from listview_main.xml
        setContentView(R.layout.listview_main);
        
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
        	Intent i = new Intent(ListActivity.this, ListSearchActivity.class);
        	finish();
        	i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(i);
            return true;
        case R.id.action_clear_search:
        	Intent j = new Intent(ListActivity.this, ListActivity.class);
        	finish();
        	j.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        	startActivity(j);
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
 
    // RemoteDataTask AsyncTask
    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(ListActivity.this);
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
            
            businesses.clear();
        }
 
        @Override
        protected Void doInBackground(Void... params) {
        	
        	String day_of_week;
        	Boolean food, drinks;
        	ParseObject deal_type = null;
        	
        	currentLocation = getLocation();
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
            // Locate the listview in listview_main.xml
            listview = (ListView) findViewById(R.id.listview);
            // Pass the results into an ArrayAdapter
            adapter = new ArrayAdapter<String>(ListActivity.this,
                    R.layout.listview_item_list);
            
            // Retrieve object "name" from Parse.com database
            for (int i =0; businesses.size() > i; i++) {
				adapter.add(businesses.get(i).getName());
            }
            // Binds the Adapter to the ListView
            listview.setAdapter(adapter);
            // Close the progressdialog
            mProgressDialog.dismiss();
            // Capture button clicks on ListView items
            listview.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                        int position, long id) {
                	
                	ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
    	                    "Establishment");
    	            query.whereEqualTo("yelp_id", businesses.get(position).getYelpId());
    	            try {
    	                ob = query.find();
    	            } catch (Exception e) {
    	                Log.e("Error", e.getMessage());
    	                e.printStackTrace();
    	            }
    	            
    	            if(ob.size() == 0){
    	            	establishment_id = "empty";
    	            } else {
    	            	establishment_id = ob.get(0).getObjectId().toString();
    	            }
    				
                    // Send single item click data to SingleItemView Class
                Intent i = new Intent(ListActivity.this,
                		DetailsActivity.class);
                    // Pass data "name" followed by the position
                	i.putExtra("establishment_id", establishment_id);
                	i.putExtra("yelp_id", businesses.get(position).getYelpId());
                    i.putExtra("name", businesses.get(position).getName());
                    i.putExtra("rating", businesses.get(position).getRating());
                    i.putExtra("address", businesses.get(position).getAddress());
                    i.putExtra("city", businesses.get(position).getCity());
                    i.putExtra("state", businesses.get(position).getState());
                    i.putExtra("zip", businesses.get(position).getZipcode());
                    i.putExtra("phone", businesses.get(position).getPhone());
                    i.putExtra("display_phone", businesses.get(position).getDisplayPhone());
                    i.putExtra("distance", businesses.get(position).getDistance());
                    i.putExtra("mobile_url", businesses.get(position).getMobileURL());
                    
                    businesses.clear();
                    // Open SingleItemView.java Activity
                    startActivity(i);
                }
            });
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
		new RemoteDataTask().execute();
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
	
	private ArrayList<Business> searchYelp(){
		API_Static_Stuff api_keys = new API_Static_Stuff();
        
	    Yelp yelp = new Yelp(api_keys.getYelpConsumerKey(), api_keys.getYelpConsumerSecret(), 
	            api_keys.getYelpToken(), api_keys.getYelpTokenSecret());
	    String response = yelp.search(query, currentLocation.getLatitude(), currentLocation.getLongitude(), String.valueOf(distanceMeters), sort_mode);
	 
	    yParser = new YelpParser();
	    return yParser.getBusinesses(response);
	}
}
