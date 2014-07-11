package com.example.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.thebarapp.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class DealsDetailsActivity extends Activity {
	// Declare Variables
	String deal_id, deal_title, deal_details;
	Integer rating, price, up_votes, down_votes;
	Intent intent;
	ParseObject est = null;
	Button upVoteButton, downVoteButton;
	ParseObject deal = null, dealVoteUser = null;
	ProgressDialog upVoteProgressDialog, downVoteProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		intent = getIntent();

		// Get the arguments from intent
		deal_id = intent.getStringExtra("deal_id");
		deal_title = intent.getStringExtra("deal_title");
		deal_details = intent.getStringExtra("deal_details");

		ParseQuery<ParseObject> queryDeal = ParseQuery
				.getQuery("Establishment");
		queryDeal.whereEqualTo("objectId",
				intent.getStringExtra("establishment_id"));
		try {
			est = queryDeal.getFirst();
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		setContentView(R.layout.activity_deal_details);

		TextView title = (TextView) findViewById(R.id.dealTitle);
		title.setText(deal_title);

		TextView details = (TextView) findViewById(R.id.dealDetails);
		details.setText(deal_details);

		upVoteButton = (Button) findViewById(R.id.deal_up_vote_button);
		upVoteButton.setOnClickListener(new OnClickListener() {
			@Override			            
			public void onClick(View view) {
				
					queryParse();

					if (deal != null) {
						if (dealVoteUser == null) {
							// create new and assign vote to 1
							dealVoteUser = new ParseObject("deal_vote_users");
							dealVoteUser.put("deal", deal);
							dealVoteUser.put("user", ParseUser.getCurrentUser());
							dealVoteUser.put("vote", 1);
							up_votes = deal.getInt("up_votes") + 1;
							down_votes = deal.getInt("down_votes");
						} else if (dealVoteUser.get("vote").toString()
								.equals("0")) {
							// change vote to 1
							dealVoteUser.put("vote", 1);
							up_votes = deal.getInt("up_votes") + 1;
							down_votes = deal.getInt("down_votes") - 1;
						} else if (dealVoteUser.get("vote").toString()
								.equals("1")) {
							// already voted up
							dealVoteUser.put("vote", 2);
							up_votes = deal.getInt("up_votes") - 1;
							down_votes = deal.getInt("down_votes");
						} else if (dealVoteUser.get("vote").toString()
								.equals("2")) {
							// change vote to 1
							dealVoteUser.put("vote", 1);
							up_votes = deal.getInt("up_votes") + 1;
							down_votes = deal.getInt("down_votes");
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

					}  else {
						// deal not found problem
					}
					setButtons(false);
			}
		});

		downVoteButton = (Button) findViewById(R.id.deal_down_vote_button);
		downVoteButton.setOnClickListener(new OnClickListener() {
			@Override			            
			public void onClick(View view) {

				queryParse();

				if (deal != null) {
					if (dealVoteUser == null) {
						// create new and assign vote to 1
						dealVoteUser = new ParseObject("deal_vote_users");
						dealVoteUser.put("deal", deal);
						dealVoteUser.put("user", ParseUser.getCurrentUser());
						dealVoteUser.put("vote", 0);
						down_votes = deal.getInt("down_votes") + 1;
						up_votes = deal.getInt("up_votes");
					} else if (dealVoteUser.get("vote").toString().equals("0")) {
						// already voted down
						dealVoteUser.put("vote", 2);
						down_votes = deal.getInt("down_votes") - 1;
					} else if (dealVoteUser.get("vote").toString().equals("1")) {
						dealVoteUser.put("vote", 0);
						down_votes = deal.getInt("down_votes") + 1;
						up_votes = deal.getInt("up_votes") - 1;
					} else if (dealVoteUser.get("vote").toString().equals("2")) {
						dealVoteUser.put("vote", 0);
						down_votes = deal.getInt("down_votes") + 1;
						up_votes = deal.getInt("up_votes");
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

					setButtons(false);
				} else {
					// deal not found problem
				}
			}
		});

		setButtons(true);
	}

	private void setButtons(Boolean queryDb) {

		if (queryDb == true) {
			queryParse();
		}

		if (deal != null) {
			if (dealVoteUser != null) {
				if (dealVoteUser.get("vote").toString().equals("0")) {
					upVoteButton.setBackgroundResource(android.R.drawable.btn_default);
					downVoteButton.setBackgroundColor(Color.RED);
				} else if (dealVoteUser.get("vote").toString().equals("1")) {
					upVoteButton.setBackgroundColor(Color.GREEN);
					downVoteButton.setBackgroundResource(android.R.drawable.btn_default);
				} else if (dealVoteUser.get("vote").toString().equals("2")) {
					upVoteButton.setBackgroundResource(android.R.drawable.btn_default);
					downVoteButton.setBackgroundResource(android.R.drawable.btn_default);
				}
			}
		}
	}

	private void queryParse() {
		ParseQuery<ParseObject> queryDeal = ParseQuery.getQuery("Deal");
		queryDeal.whereEqualTo("objectId", deal_id);

		queryDeal.getFirstInBackground(new GetCallback<ParseObject>() {
			public void done(ParseObject dealObject, ParseException e) {
				if (dealObject == null) {
					Log.d("get deal", e.toString());
				} else {
					deal = dealObject;
				}

				ParseQuery<ParseObject> queryDealVoteUser = ParseQuery
						.getQuery("deal_vote_users");
				ParseUser user = ParseUser.getCurrentUser();
				queryDealVoteUser.whereEqualTo("deal", deal);
				queryDealVoteUser.whereEqualTo("user", user);

				queryDealVoteUser
						.getFirstInBackground(new GetCallback<ParseObject>() {
							public void done(ParseObject dealUserObject,
									ParseException e) {
								if (dealUserObject == null) {
									Log.d("get deal user", e.toString());
								} else {
									dealVoteUser = dealUserObject;
								}
							}
						});
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		if(deal.isDirty()){
		deal.saveInBackground();
		}
		if(dealVoteUser.isDirty()){
		dealVoteUser.saveInBackground();
		}
		
		if (downVoteProgressDialog != null) {
			downVoteProgressDialog.dismiss();
			downVoteProgressDialog = null;
		}
		if (upVoteProgressDialog != null) {
			upVoteProgressDialog.dismiss();
			upVoteProgressDialog = null;
		}
	}
}
