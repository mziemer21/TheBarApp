package com.example.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.thebarapp.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class DealsDetailsActivity extends Activity{
	//Declare Variables
	String deal_id, deal_title, deal_details;
	Integer rating, price, up_votes, down_votes;
	Intent intent;
	ParseObject est = null;
	Button upVoteButton, downVoteButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		intent = getIntent();

		// Get the arguments from intent
		deal_id = intent.getStringExtra("deal_id");
		deal_title = intent.getStringExtra("deal_title");
		deal_details = intent.getStringExtra("deal_details");

		ParseQuery<ParseObject> queryDeal = ParseQuery.getQuery("Establishment");
		queryDeal.whereEqualTo("objectId", intent.getStringExtra("establishment_id"));
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

		//TextView establishment = (TextView) findViewById(R.id.dealEstablishment);
		//establishment.setText(est.getString("name").toString());

		upVoteButton = (Button) findViewById(R.id.deal_up_vote_button);
		upVoteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ParseObject deal = null, dealVoteUser = null;

				ParseQuery<ParseObject> queryDeal = ParseQuery.getQuery("Deal");
				queryDeal.whereEqualTo("objectId", deal_id);
				try {
					deal = queryDeal.getFirst();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ParseQuery<ParseObject> queryDealVoteUser = ParseQuery.getQuery("deal_vote_users");
				queryDealVoteUser.whereEqualTo("deal", deal).whereEqualTo("user", ParseUser.getCurrentUser());
				try {
					dealVoteUser = queryDealVoteUser.getFirst();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if(deal != null){
					if(dealVoteUser == null){
						// create new and assign vote to 1
						dealVoteUser = new ParseObject("deal_vote_users");
						dealVoteUser.put("deal", deal);
						dealVoteUser.put("user", ParseUser.getCurrentUser());
						dealVoteUser.put("vote", 1);
						up_votes = deal.getInt("up_votes") +1;
						down_votes = deal.getInt("down_votes");
					} else if(dealVoteUser.get("vote").toString().equals("0")){
						// change vote to 1
						dealVoteUser.put("vote", 1);
						up_votes = deal.getInt("up_votes") +1;
						down_votes = deal.getInt("down_votes") -1;
					} else if(dealVoteUser.get("vote").toString().equals("1")){
						// already voted up
						dealVoteUser.put("vote", 2);
						up_votes = deal.getInt("up_votes") -1;
						down_votes = deal.getInt("down_votes");
					} else if(dealVoteUser.get("vote").toString().equals("2")){
						// change vote to 1
						dealVoteUser.put("vote", 1);
						up_votes = deal.getInt("up_votes") +1;
						down_votes = deal.getInt("down_votes");
					} 

					if((up_votes + down_votes) != 0){
						rating = (up_votes/(up_votes + down_votes))*100;
					} else if((up_votes == 0) && (down_votes == 0)){
						rating = 0;
					} else {
						rating = 50;
					}

					deal.put("rating", rating);
					deal.put("up_votes", up_votes);

					try {
						deal.save();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						dealVoteUser.save();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					// deal not found problem
				}
			}
		});

		downVoteButton = (Button) findViewById(R.id.deal_down_vote_button);
		downVoteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ParseObject deal = null, dealVoteUser = null;
				
				ParseQuery<ParseObject> queryDeal = ParseQuery.getQuery("Deal");
				queryDeal.whereEqualTo("objectId", deal_id);
				try {
					deal = queryDeal.getFirst();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				ParseQuery<ParseObject> queryDealVoteUser = ParseQuery.getQuery("deal_vote_users");
				queryDealVoteUser.whereEqualTo("deal", deal).whereEqualTo("user", ParseUser.getCurrentUser());
				try {
					dealVoteUser = queryDealVoteUser.getFirst();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if(deal != null){
					if(dealVoteUser == null){
						// create new and assign vote to 1
						dealVoteUser = new ParseObject("deal_vote_users");
						dealVoteUser.put("deal", deal);
						dealVoteUser.put("user", ParseUser.getCurrentUser());
						dealVoteUser.put("vote", 0);
						down_votes = deal.getInt("down_votes") +1;
						up_votes = deal.getInt("up_votes");
					} else if(dealVoteUser.get("vote").toString().equals("0")){
						// already voted down
						dealVoteUser.put("vote", 2);
						down_votes = deal.getInt("down_votes") -1;
					} else if (dealVoteUser.get("vote").toString().equals("1")) {
						dealVoteUser.put("vote", 0);
						down_votes = deal.getInt("down_votes") +1;
						up_votes = deal.getInt("up_votes") -1;
					} else if (dealVoteUser.get("vote").toString().equals("2")) {
						dealVoteUser.put("vote", 0);
						down_votes = deal.getInt("down_votes") +1;
						up_votes = deal.getInt("up_votes");
					}

					if((up_votes + down_votes) != 0){
						rating = (up_votes/(up_votes + down_votes))*100;
					} else if((up_votes == 0) && (down_votes == 0)){
						rating = 0;
					} else {
						rating = 50;
					}

					deal.put("rating", rating);
					deal.put("down_votes", down_votes);

					deal.saveInBackground();
					dealVoteUser.saveInBackground();
				} else {
					// deal not found problem
				}
			}
		});


	}
}
