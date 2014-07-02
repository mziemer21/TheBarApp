package com.example.activities;

import java.util.ArrayList;
import java.util.List;

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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.thebarapp.DealListViewAdapter;
import com.example.thebarapp.DealRowItem;
import com.example.thebarapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class DealActivity extends FragmentActivity implements LocationListener,
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener{
	// Declare Variables
    ListView listview;
    List<ParseObject> ob;
    ArrayAdapter<DealRowItem> adapter;
    private Location currentLocation = null;
    Intent intent;
    Integer obCount;
    ParseObject est;

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
    
 // RemoteDataTask AsyncTask
    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
    	Context context;
   	 ProgressDialog dealProgressDialog;
   	 
   	  public RemoteDataTask(Context context){
   	   this.context=context;
   	  }
   	  
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
            // Create a progressdialog
            if(dealProgressDialog != null){
            	dealProgressDialog.dismiss();
            }
            dealProgressDialog = new ProgressDialog(context);
            // Set progressdialog message
            dealProgressDialog.setMessage("Loading Yelp Data...");
            dealProgressDialog.setIndeterminate(false);
            // Show progressdialog
            dealProgressDialog.show();
        }
 
        @Override
        protected Void doInBackground(Void... params) {
        	String distance, day_of_week, query;
        	Boolean food, drinks;
        	ParseObject deal_type = null;
        	
        	currentLocation = getLocation();
        	distance  = intent.getStringExtra("distance");
        	day_of_week = intent.getStringExtra("day_of_week");
        	food = intent.getBooleanExtra("food", true);
        	drinks = intent.getBooleanExtra("drinks", true);
        	query = intent.getStringExtra("query");
        	
            // Locate the class table named "establishment" in Parse.com
            ParseQuery<ParseObject> queryDealSearch = new ParseQuery<ParseObject>("Deal");
            queryDealSearch.setLimit(10);
            if(query != ""){
            	queryDealSearch.whereContains("title", query);
            }
            if(day_of_week != null)
            {
            	queryDealSearch.whereContains("day", day_of_week);
            }
            if(distance != null)
            {
            	queryDealSearch.whereWithinMiles("location", geoPointFromLocation(currentLocation), Double.parseDouble(distance));
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
						e.printStackTrace();
					}
					queryDealSearch.whereEqualTo("deal_type", deal_type);
	            }
            }
            
            queryDealSearch.orderByDescending("rating");
            try {
            	obCount = queryDealSearch.count();
                ob = queryDealSearch.find();
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
        	// if we found deals
        	if(obCount > 0)
        	{
        		List<DealRowItem> rowItems = new ArrayList<DealRowItem>();
        	
	            // Locate the listview in deal_listview.xml
	            listview = (ListView) findViewById(R.id.deal_listview);
	            
	         // Retrieve object "title" from Parse.com database
	            for (ParseObject deal : ob) {
	            	DealRowItem item = new DealRowItem(deal.get("title").toString(), deal.get("rating").toString());
	                rowItems.add(item);
	            }

	            // Pass the results into an ArrayAdapter
	            DealListViewAdapter adapter = new DealListViewAdapter(context, R.layout.listview_item_deal, rowItems);
	            
	            // Binds the Adapter to the ListView
	            listview.setAdapter(adapter);
	            // Close the progressdialog
	            dealProgressDialog.dismiss();
	            // Capture button clicks on ListView items
	            listview.setOnItemClickListener(new OnItemClickListener() {
	                @Override
	                public void onItemClick(AdapterView<?> parent, View view,
	                        int position, long id) {
	                    // Send single item click data to SingleItemView Class
	                	Intent i = new Intent(DealActivity.this,DealsDetailsActivity.class);
	                	est = (ParseObject) ob.get(position).get("establishment");
	                    // Pass data to next activity
	                	i.putExtra("deal_id", ob.get(position).getObjectId().toString());
	                    i.putExtra("deal_title", ob.get(position).getString("title")
	                            .toString());
	                    i.putExtra("deal_details", ob.get(position).getString("details")
	                            .toString());
	                    i.putExtra("deal_restrictions", ob.get(position).getInt("description"));
	                    i.putExtra("yelp_id", ob.get(position).getString("yelp_id"));
	                    i.putExtra("establishment_id", est.getObjectId().toString());
	                    // Open SingleItemView.java Activity
	                    startActivity(i);
	                    
	                }
	            });
	        } else { // no deals found so display a popup and return to search options
        		AlertDialog.Builder builder = new AlertDialog.Builder(DealActivity.this);
				 
				// set title
				builder.setTitle("No Results");
	 
				// set dialog message
				builder
					.setMessage("Sorry, nothing was found.  Try and widen your search.")
					.setCancelable(false)
					.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
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
	new RemoteDataTask(DealActivity.this).execute();
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
}
