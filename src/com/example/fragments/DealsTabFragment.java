package com.example.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.activities.DealAddActivity;
import com.example.activities.DealsDetailsActivity;
import com.example.activities.LoginActivity;
import com.example.thebarapp.DealListViewAdapter;
import com.example.thebarapp.DealRowItem;
import com.example.thebarapp.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/***
 * Tab used by details fragment
 * It is a list of deals
 * @author zieme_000
 *
 */
public class DealsTabFragment extends Fragment {

	private Button addDealButton;
	Bundle extrasDeal;
	// Declare Variables
    ListView dealListview;
    List<ParseObject> obDeal;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		extrasDeal = getArguments();
		
		View rootDealView = inflater.inflate(R.layout.fragment_details_deals, container, false);
		
		addDealButton = (Button) rootDealView.findViewById(R.id.add_deal);
		 
		addDealButton.setOnClickListener(new OnClickListener() {
 
			  @Override
			  public void onClick(View arg0) {
				  
				  if(ParseUser.getCurrentUser().getCreatedAt() == null){
					  AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				 
							// set title
							builder.setTitle("Cannot Add Deal");
				 
							// set dialog message
							builder
								.setMessage("You must be logged in to add a deal.")
								.setCancelable(false)
								.setPositiveButton("Login",new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int id) {
										Intent loginActivity = new Intent(getActivity(), LoginActivity.class);
								        startActivity(loginActivity);
										dialog.dismiss();
									}
								  })
								.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int id) {
										// if this button is clicked, just close
										// the dialog box and do nothing
										dialog.cancel();
									}
								});
				 
								// create alert dialog
								AlertDialog alertDialog = builder.create();
				 
								// show it
								alertDialog.show();
				  } else {
					  Intent dealAddFragment = new Intent(getActivity(), DealAddActivity.class);
					// Pass data "name" followed by the position
	              	dealAddFragment.putExtra("establishment_id", extrasDeal.getString("establishment_id"));
	                dealAddFragment.putExtra("name", extrasDeal.getString("name")
	                          .toString());
	                dealAddFragment.putExtra("rating", extrasDeal.getString("rating"));
	                dealAddFragment.putExtra("address", extrasDeal.getString("address")
	                          .toString());
	                dealAddFragment.putExtra("city", extrasDeal.getString("city"));
	                dealAddFragment.putExtra("state", extrasDeal.getString("state"));
	                dealAddFragment.putExtra("zip", extrasDeal.getString("zip"));
	                dealAddFragment.putExtra("yelp_id", extrasDeal.getString("yelp_id"));
	                
					  startActivity(dealAddFragment);
				  }
			  }
		});
		
		if(extrasDeal.getString("establishment_id").contentEquals("empty")){
			//display some text
		} else {
			new RemoteDataTaskDeal().execute();
		}
		return rootDealView;
	}
	
	// RemoteDataTask AsyncTask
    private class RemoteDataTaskDeal extends AsyncTask<Void, Void, Void> {
    	ProgressDialog mProgressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(getActivity());
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }
 
        @Override
        protected Void doInBackground(Void... params) {
        	ParseObject est = null;
        	ParseQuery<ParseObject> queryEstablishment = ParseQuery.getQuery("Establishment");
			queryEstablishment.whereEqualTo("objectId", extrasDeal.getString("establishment_id"));
			try {
				est = queryEstablishment.getFirst();
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            // Locate the class table named "establishment" in Parse.com
            ParseQuery<ParseObject> queryDeal = new ParseQuery<ParseObject>(
                    "Deal").whereEqualTo("establishment", est);
            queryDeal.orderByDescending("_created_at");
            try {
                obDeal = queryDeal.find();
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            // Locate the listview in listview_main.xml
            dealListview = (ListView) getView().findViewById(R.id.deal_tab_listview);
            // Pass the results into an ArrayAdapter
            List<DealRowItem> rowItems = new ArrayList<DealRowItem>();
            
         // Retrieve object "title" from Parse.com database
            for (ParseObject deal : obDeal) {
            	DealRowItem item = new DealRowItem(deal.get("title").toString(), deal.get("rating").toString());
                rowItems.add(item);
            }

            // Pass the results into an ArrayAdapter
            DealListViewAdapter dealAdapter = new DealListViewAdapter(getActivity(), R.layout.listview_item_deal, rowItems);
            // Binds the Adapter to the ListView
            dealListview.setAdapter(dealAdapter);
            // Close the progressdialog
            //mProgressDialog.dismiss();
            // Capture button clicks on ListView items
            mProgressDialog.dismiss();
            dealListview.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                        int position, long id) {
                    // Send single item click data to SingleItemView Class
                 Intent iDeal = new Intent(getActivity(),
                		DealsDetailsActivity.class);
                    // Pass data "name" followed by the position
                 	iDeal.putExtra("establishment_id", extrasDeal.getString("establishment_id").toString());
                	iDeal.putExtra("deal_id", obDeal.get(position).getObjectId().toString());
                    iDeal.putExtra("deal_details", obDeal.get(position).getString("details").toString());
                    iDeal.putExtra("deal_title", obDeal.get(position).getString("title").toString());
                    
                    
                    // Open SingleItemView.java Activity
                    startActivity(iDeal);
                }
            });
        }
    }
}
