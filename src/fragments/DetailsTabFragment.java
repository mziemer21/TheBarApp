package fragments;

import activities.LoginActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.thebarapp.Helper;
import com.thebarapp.ParseApplication;
import com.thebarapp.ParseApplication.TrackerName;
import com.thebarapp.R;

/***
 * Tab used by details fragment It contains info about an establishment
 * 
 * @author zieme_000
 * 
 */
public class DetailsTabFragment extends Fragment {

	// Declare Variables
	private String name, address, city, state, review_count, phoneDisplay, phoneCall, estLat, estLng, curLat, curLng, mobUrl;
	private TextView txtName, txtAddress, txtReviewCount, txtReviewWord;
	private Double rating;
	private ImageView ratingImg;
	private Button launch_directions, launch_phone, launch_info, launch_review, favorite;
	private Bundle extrasDeal;
	private ParseObject est = null, estFav = null;
	private Boolean delete;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootDetailsView = inflater.inflate(R.layout.fragment_details, container, false);

		new RemoteDataTaskFavorite().execute();

		/* get arguments from activity */
		extrasDeal = getArguments();
		name = extrasDeal.getString("est_name");
		address = extrasDeal.getString("address");
		city = extrasDeal.getString("city");
		state = extrasDeal.getString("state");
		rating = Double.parseDouble(extrasDeal.getString("rating"));
		review_count = extrasDeal.getString("rating_count");
		phoneDisplay = extrasDeal.getString("display_phone");
		phoneCall = extrasDeal.getString("phone");
		estLat = String.valueOf(extrasDeal.getDouble("est_lat"));
		estLng = String.valueOf(extrasDeal.getDouble("est_lng"));
		curLat = String.valueOf(extrasDeal.getDouble("cur_lat"));
		curLng = String.valueOf(extrasDeal.getDouble("cur_lng"));
		mobUrl = extrasDeal.getString("mob_url");

		// Locate the TextView in xml
		txtName = (TextView) rootDetailsView.findViewById(R.id.name);
		ratingImg = (ImageView) rootDetailsView.findViewById(R.id.rating_imageview);
		txtAddress = (TextView) rootDetailsView.findViewById(R.id.address);
		txtReviewCount = (TextView) rootDetailsView.findViewById(R.id.review_count);
		txtReviewWord = (TextView) rootDetailsView.findViewById(R.id.review_count_word);
		launch_phone = (Button) rootDetailsView.findViewById(R.id.phone_button);
		launch_directions = (Button) rootDetailsView.findViewById(R.id.directions_button);
		launch_info = (Button) rootDetailsView.findViewById(R.id.info_button);
		launch_review = (Button) rootDetailsView.findViewById(R.id.review_button);
		favorite = (Button) rootDetailsView.findViewById(R.id.favorite_button);

		// Load the text into the TextView
		txtName.setText(name);
		txtAddress.setText(address + " " + city + " " + state);
		txtReviewCount.setText(review_count);
		if (review_count.matches("1")) {
			txtReviewWord.setText("Review");
		} else {
			txtReviewWord.setText("Reviews");
		}

		launch_phone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (phoneCall != null) {
					Intent intent = new Intent(Intent.ACTION_DIAL);
					intent.setData(Uri.parse("tel:" + phoneCall));
					startActivity(intent);
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setMessage("Whoops...").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {

						}
					});
					builder.create();
				}
			}
		});

		launch_directions.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String url = "http://maps.google.com/maps?saddr=" + curLat + "," + curLng + "&daddr=" + estLat + "," + estLng;
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
				intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
				startActivity(intent);
			}
		});

		launch_info.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mobUrl));
				startActivity(browserIntent);
			}
		});

		launch_review.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mobUrl));
				startActivity(browserIntent);
			}
		});

		if (rating < .5) {
			ratingImg.setImageResource(R.drawable.zero_stars_lg);
		} else if (rating < 1) {
			ratingImg.setImageResource(R.drawable.one_stars_lg);
		} else if (rating < 1.5) {
			ratingImg.setImageResource(R.drawable.one_half_stars_lg);
		} else if (rating < 2) {
			ratingImg.setImageResource(R.drawable.two_stars_lg);
		} else if (rating < 2.5) {
			ratingImg.setImageResource(R.drawable.two_half_stars_lg);
		} else if (rating < 3) {
			ratingImg.setImageResource(R.drawable.three_stars_lg);
		} else if (rating < 3.5) {
			ratingImg.setImageResource(R.drawable.three_half_stars_lg);
		} else if (rating < 4) {
			ratingImg.setImageResource(R.drawable.four_stars_lg);
		} else if (rating < 4.5) {
			ratingImg.setImageResource(R.drawable.four_half_stars_lg);
		} else if (rating < 5) {
			ratingImg.setImageResource(R.drawable.five_stars_lg);
		}

		Tracker t = ((ParseApplication) getActivity().getApplication()).getTracker(TrackerName.APP_TRACKER);
		t.setScreenName("Details Tab Fragment");
		t.send(new HitBuilders.AppViewBuilder().build());

		return rootDetailsView;
	}

	// RemoteDataTask AsyncTask
	private class RemoteDataTaskFavorite extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			ParseQuery<ParseObject> queryEstablishment = ParseQuery.getQuery("Establishment");
			queryEstablishment.whereEqualTo("objectId", extrasDeal.getString("establishment_id"));
			try {
				est = queryEstablishment.getFirst();
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (est != null) {
				ParseQuery<ParseObject> queryFav = ParseQuery.getQuery("user_favorite_establishments");
				queryFav.whereEqualTo("user", ParseUser.getCurrentUser());
				queryFav.whereEqualTo("establishment", est);
				try {
					estFav = queryFav.getFirst();
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (estFav != null) {
				delete = true;
				favorite.setText("Remove From Favorites");

			} else {
				delete = false;
				favorite.setText("Add To Favorites");
			}

			favorite.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (ParseUser.getCurrentUser().getCreatedAt() == null) {
						AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

						// set title
						builder.setTitle("Cannot Add Deal");

						// set dialog message
						builder.setMessage("You must be logged in to add favorites.").setCancelable(false).setPositiveButton("Login", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent loginActivity = new Intent(getActivity(), LoginActivity.class);
								startActivity(loginActivity);
								dialog.dismiss();
							}
						}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
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
						if (delete) {
							estFav.deleteInBackground();
							favorite.setText("Add To Favorites");
							Toast.makeText(getActivity(), "Deleted From Favorites", Toast.LENGTH_LONG).show();
						} else {
							if (extrasDeal.getString("establishment_id").contains("empty")) {
								Helper.displayErrorStay("Sorry, a bar must have deals posted before you can favorite it.", getActivity());
							} else {
								favorite.setText("Add To Favorites");
								ParseObject newFav = new ParseObject("user_favorite_establishments");
								newFav.put("user", ParseUser.getCurrentUser());
								newFav.put("establishment", est);
								newFav.put("establishment_days", est.get("days"));
								try {
									newFav.save();
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								favorite.setText("Remove From Favorites");
								Toast.makeText(getActivity(), "Added To Favorites", Toast.LENGTH_LONG).show();
							}
						}

					}
				}
			});
		}
	}
}
