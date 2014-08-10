package activities;

import java.util.ArrayList;
import java.util.Calendar;

import navigation.NavDrawer;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.thebarapp.Business;
import com.thebarapp.Helper;
import com.thebarapp.ParseApplication;
import com.thebarapp.R;

public class DealDetailsActivity extends NavDrawer {
	// Declare Variables
	private String deal_id, deal_title, deal_details, deal_restrictions, deal_time, est_name, created_by;
	private Integer rating, up_votes, down_votes;
	private Intent intent;
	private ToggleButton upVoteButton, downVoteButton;
	private ParseObject deal = null, dealVoteUser = null, ob = null;
	private Button deleteButton;
	private Boolean delete = false;
	private ProgressDialog ProgressDialog;
	private Calendar calendar = Calendar.getInstance();
	private String distanceMiles, yelpQuery = "", day_of_week;
	private Location currentLocation = new Location("");
	private Integer distanceMeters;
	private ArrayList<Business> businesses = new ArrayList<Business>(), tempBusiness = new ArrayList<Business>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_deal_details);
		super.onCreate(savedInstanceState);

		intent = getIntent();

		// Get tracker.
		((ParseApplication) getApplication()).getTracker(ParseApplication.TrackerName.APP_TRACKER);

		// Get the arguments from intent
		deal_id = intent.getStringExtra("deal_id");
		deal_title = intent.getStringExtra("deal_title");
		deal_details = intent.getStringExtra("deal_details");
		deal_restrictions = intent.getStringExtra("deal_restrictions");
		deal_time = intent.getStringExtra("deal_time");
		est_name = intent.getStringExtra("est_name");
		created_by = intent.getStringExtra("created_by");
		currentLocation.setLatitude(intent.getDoubleExtra("cur_lat", 0));
		currentLocation.setLongitude(intent.getDoubleExtra("cur_lng", 0));

		TextView title = (TextView) findViewById(R.id.dealTitle);
		title.setText(deal_title);

		TextView details = (TextView) findViewById(R.id.dealDetails);
		details.setText(deal_details);

		TextView restrictions = (TextView) findViewById(R.id.dealRestrictions);
		restrictions.setText(deal_restrictions);

		TextView time = (TextView) findViewById(R.id.dealTime);
		time.setText(deal_time);

		Button est = (Button) findViewById(R.id.dealEstButton);
		est.setText(est_name);
		est.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				new RemoteDataTask(DealDetailsActivity.this).execute();
			}
		});

		upVoteButton = (ToggleButton) findViewById(R.id.deal_up_vote_button);
		upVoteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				if (downVoteButton.isChecked()) {
					downVoteButton.setChecked(false);
				}
			}
		});

		downVoteButton = (ToggleButton) findViewById(R.id.deal_down_vote_button);
		downVoteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (upVoteButton.isChecked()) {
					upVoteButton.setChecked(false);
				}
			}
		});

		deleteButton = (Button) findViewById(R.id.deal_delete_button);
		deleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(DealDetailsActivity.this);

				// set title
				builder.setTitle("Delete?");

				// set dialog message
				builder.setMessage("Are you sure you want to delete this deal?").setCancelable(false).setPositiveButton("Delete", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						delete = true;
						dialog.dismiss();
						finish();
					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						delete = false;
						dialog.cancel();
					}
				});

				// create alert dialog
				AlertDialog alertDialog = builder.create();

				// show it
				alertDialog.show();
			}
		});

		if (created_by.equals(ParseUser.getCurrentUser().getObjectId())) {
			deleteButton.setVisibility(View.VISIBLE);
		} else {
			deleteButton.setVisibility(View.GONE);
		}

		queryParse(true);
	}

	private void setButtons() {

		if (deal != null) {
			if (dealVoteUser != null) {
				if (dealVoteUser.get("vote").toString().equals("0")) {
					upVoteButton.setChecked(false);
					downVoteButton.setChecked(true);
				} else if (dealVoteUser.get("vote").toString().equals("1")) {
					upVoteButton.setChecked(true);
					downVoteButton.setChecked(false);
				} else if (dealVoteUser.get("vote").toString().equals("2")) {
					upVoteButton.setChecked(false);
					downVoteButton.setChecked(false);
				}
			}
		}
	}

	private void queryParse(final boolean setButtons) {
		if (Helper.isConnectedToInternet(DealDetailsActivity.this)) {
			ParseQuery<ParseObject> queryDeal = ParseQuery.getQuery("Deal");
			queryDeal.whereEqualTo("objectId", deal_id);

			queryDeal.getFirstInBackground(new GetCallback<ParseObject>() {
				public void done(ParseObject dealObject, ParseException e) {
					if (dealObject == null) {
						Log.d("get deal", e.toString());
					} else {
						deal = dealObject;
					}

					ParseQuery<ParseObject> queryDealVoteUser = ParseQuery.getQuery("deal_vote_users");
					ParseUser user = ParseUser.getCurrentUser();
					queryDealVoteUser.whereEqualTo("deal", deal);
					queryDealVoteUser.whereEqualTo("user", user);

					queryDealVoteUser.getFirstInBackground(new GetCallback<ParseObject>() {
						public void done(ParseObject dealUserObject, ParseException e) {
							if (dealUserObject == null) {
								Log.d("get deal user", e.toString());
							} else {
								dealVoteUser = dealUserObject;

								if (setButtons) {
									setButtons();
								}
							}
						}
					});
				}
			});
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		if ((deal != null) && (delete)) {
			deal.deleteInBackground();
			Toast.makeText(getApplicationContext(), "Deal Added!", Toast.LENGTH_LONG).show();
		} else {
			if (upVoteButton.isChecked()) {
				queryParse(false);

				if (deal != null) {
					down_votes = deal.getInt("down_votes");
					if (dealVoteUser == null) {
						// create new and assign vote to 1
						dealVoteUser = new ParseObject("deal_vote_users");
						dealVoteUser.put("deal", deal);
						dealVoteUser.put("user", ParseUser.getCurrentUser());
						dealVoteUser.put("vote", 1);
						up_votes = deal.getInt("up_votes") + 1;
					} else if (dealVoteUser.get("vote").toString().equals("0")) {
						// change vote to 1
						dealVoteUser.put("vote", 1);
						up_votes = deal.getInt("up_votes") + 1;
						down_votes--;
					} else if (dealVoteUser.get("vote").toString().equals("1")) {
						// already voted up
						dealVoteUser.put("vote", 2);
						up_votes = deal.getInt("up_votes") - 1;
					} else if (dealVoteUser.get("vote").toString().equals("2")) {
						// change vote to 1
						dealVoteUser.put("vote", 1);
						up_votes = deal.getInt("up_votes") + 1;
					}

					if ((up_votes + down_votes) != 0) {
						rating = (up_votes / (up_votes + down_votes)) * 100;
					} else if ((up_votes == 0) && (down_votes == 0)) {
						rating = 0;
					} else {
						rating = 50;
					}

					deal.put("rating", rating);
					deal.put("down_votes", down_votes);
					deal.put("up_votes", up_votes);

				} else {
					// deal not found problem
				}
			}

			if (downVoteButton.isChecked()) {
				queryParse(false);

				if (deal != null) {
					up_votes = deal.getInt("up_votes");
					if (dealVoteUser == null) {
						// create new and assign vote to 1
						dealVoteUser = new ParseObject("deal_vote_users");
						dealVoteUser.put("deal", deal);
						dealVoteUser.put("user", ParseUser.getCurrentUser());
						dealVoteUser.put("vote", 0);
						down_votes = deal.getInt("down_votes") + 1;
					} else if (dealVoteUser.get("vote").toString().equals("0")) {
						// already voted down
						dealVoteUser.put("vote", 2);
						down_votes = deal.getInt("down_votes") - 1;
					} else if (dealVoteUser.get("vote").toString().equals("1")) {
						dealVoteUser.put("vote", 0);
						down_votes = deal.getInt("down_votes") + 1;
						up_votes--;
					} else if (dealVoteUser.get("vote").toString().equals("2")) {
						dealVoteUser.put("vote", 0);
						down_votes = deal.getInt("down_votes") + 1;
					}

					if ((up_votes + down_votes) != 0) {
						rating = (up_votes / (up_votes + down_votes)) * 100;
					} else if ((up_votes == 0) && (down_votes == 0)) {
						rating = 0;
					} else {
						rating = 50;
					}

					deal.put("rating", rating);
					deal.put("down_votes", down_votes);
					deal.put("up_votes", up_votes);
				} else {
					// deal not found problem
				}
			}

			if ((deal != null) && (deal.isDirty())) {
				deal.saveInBackground();
			}
			if ((dealVoteUser != null) && (dealVoteUser.isDirty())) {
				dealVoteUser.saveInBackground();
			}
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
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
			if (ProgressDialog != null) {
				ProgressDialog.dismiss();
				ProgressDialog = null;
			}
			ProgressDialog = new ProgressDialog(context);
			// Set progressdialog message
			ProgressDialog.setMessage("Searching Yelp...");
			ProgressDialog.setIndeterminate(false);
			ProgressDialog.setCancelable(false);
			// Show progressdialog
			ProgressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			day_of_week = (intent.getStringExtra("day_of_week") == null) ? Helper.setDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)) : intent.getStringExtra("day_of_week");
			distanceMiles = (intent.getStringExtra("distance") == null) ? "1" : intent.getStringExtra("distance");
			distanceMeters = Integer.parseInt(distanceMiles) * 1609;
			
			ParseQuery<ParseObject> queryEst = ParseQuery.getQuery("Establishment");
			queryEst.whereEqualTo("objectId", intent.getStringExtra("establishment_id"));
			try {
				ob = queryEst.getFirst();
			} catch (Exception e) {
				/*Log.e("Error", e.getMessage());
				e.printStackTrace();*/
			}

			if (ob != null) {
				yelpQuery = ob.getString("yelp_id").toString();

				tempBusiness = Helper.searchYelp(false, Double.toString(ob.getParseGeoPoint("location").getLatitude()), Double.toString(ob.getParseGeoPoint("location").getLongitude()), yelpQuery,
						true, currentLocation, distanceMeters, 0, 0);
				tempBusiness.get(0).setEstablishmentId(ob.getObjectId());
				businesses.add(tempBusiness.get(0));
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (ProgressDialog != null) {
				// Close the progressdialog
				ProgressDialog.dismiss();
			}
			
			Business bus = businesses.get(0);

			// Send single item click data to SingleItemView
			// Class
			Intent i = new Intent(DealDetailsActivity.this, DetailsActivity.class);
			// Pass data "name" followed by the position
			i.putExtra("establishment_id", bus.getEstablishmentId());
			i.putExtra("est_name", bus.getName());
			i.putExtra("yelp_id", bus.getYelpId());
			i.putExtra("rating", bus.getRating());
			i.putExtra("rating_count", bus.getRatingCount());
			i.putExtra("address", bus.getAddress());
			i.putExtra("city", bus.getCity());
			i.putExtra("state", bus.getState());
			i.putExtra("zip", bus.getZipcode());
			i.putExtra("phone", bus.getPhone());
			i.putExtra("display_phone", bus.getDisplayPhone());
			i.putExtra("distance", bus.getDistance());
			i.putExtra("mobile_url", bus.getMobileURL());
			i.putExtra("day_of_week", (day_of_week == "") ? Helper.setDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)) : day_of_week);
			i.putExtra("cur_lat", currentLocation.getLatitude());
			i.putExtra("cur_lng", currentLocation.getLongitude());
			i.putExtra("est_lat", bus.getLatitude());
			i.putExtra("est_lng", bus.getLongitude());

			// Open SingleItemView.java Activity
			startActivity(i);
		}
	}

}
