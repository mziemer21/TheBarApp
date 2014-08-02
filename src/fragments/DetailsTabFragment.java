package fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
	private Button launch_directions, launch_phone, launch_info, launch_review;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootDetailsView = inflater.inflate(R.layout.fragment_details, container, false);

		/* get arguments from activity */
		Bundle extraDetails = getArguments();
		name = extraDetails.getString("name");
		address = extraDetails.getString("address");
		city = extraDetails.getString("city");
		state = extraDetails.getString("state");
		rating = Double.parseDouble(extraDetails.getString("rating"));
		review_count = extraDetails.getString("rating_count");
		phoneDisplay = extraDetails.getString("display_phone");
		phoneCall = extraDetails.getString("phone");
		estLat = extraDetails.getString("est_lat");
		estLng = extraDetails.getString("est_lng");
		curLat = extraDetails.getString("cur_lat");
		curLng = extraDetails.getString("cur_lng");
		mobUrl = extraDetails.getString("mob_url");


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

		// Load the text into the TextView
		txtName.setText(name);
		txtAddress.setText(address + " " + city + " " + state);
		txtReviewCount.setText(review_count);
		if(review_count.matches("1")){
			txtReviewWord.setText("Review");
		} else {
			txtReviewWord.setText("Reviews");
		}
		
		launch_phone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(phoneCall != null){
					Intent intent = new Intent(Intent.ACTION_DIAL);
					intent.setData(Uri.parse("tel:" + phoneCall));
					startActivity(intent); 
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			        builder.setMessage("Whoops...")
			               .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
				String url = "http://maps.google.com/maps?saddr="+curLat+","+curLng+"&daddr="+estLat+","+estLng;
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

		return rootDetailsView;
	}
}
