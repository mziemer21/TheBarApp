package activities;

import navigation.NavDrawer;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.thebarapp.R;

public class DealDetailsActivity extends NavDrawer {
	// Declare Variables
	private String deal_id, deal_title, deal_details, deal_restrictions, deal_time, est_name;
	private Integer rating, up_votes, down_votes;
	private Intent intent;
	private ToggleButton upVoteButton, downVoteButton;
	private ParseObject deal = null, dealVoteUser = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_deal_details);
		super.onCreate(savedInstanceState);

		intent = getIntent();

		// Get the arguments from intent
		deal_id = intent.getStringExtra("deal_id");
		deal_title = intent.getStringExtra("deal_title");
		deal_details = intent.getStringExtra("deal_details");
		deal_restrictions = intent.getStringExtra("deal_restrictions");
		deal_time = intent.getStringExtra("deal_time");
		est_name = intent.getStringExtra("est_name");

		TextView title = (TextView) findViewById(R.id.dealTitle);
		title.setText(deal_title);

		TextView details = (TextView) findViewById(R.id.dealDetails);
		details.setText(deal_details);

		TextView restrictions = (TextView) findViewById(R.id.dealRestrictions);
		restrictions.setText(deal_restrictions);

		TextView time = (TextView) findViewById(R.id.dealTime);
		time.setText(deal_time);

		TextView est = (TextView) findViewById(R.id.dealEst);
		est.setText(est_name);

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

	@Override
	public void onPause() {
		super.onPause();

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
