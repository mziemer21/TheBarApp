package activities;


import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;

import com.thebarapp.R;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.thebarapp.LocationParser;

public class DealAddActivity extends Activity {

	private Button submitButton;
	Intent intent;
	ProgressDialog dealAddProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		intent = getIntent();

		setContentView(R.layout.activity_add_deal);

		submitButton = (Button) this.findViewById(R.id.submitDealButton);

		submitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (dealAddProgressDialog != null) {
					dealAddProgressDialog.dismiss();
					dealAddProgressDialog = null;
				}
				// Create a progressdialog
				dealAddProgressDialog = new ProgressDialog(DealAddActivity.this);
				// Set progressdialog message
				dealAddProgressDialog.setMessage("Saving...");
				dealAddProgressDialog.setIndeterminate(false);
				// Show progressdialog
				dealAddProgressDialog.show();

				new AsyncTask<Void, Void, Void>() {
					@Override
					protected Void doInBackground(Void... params) {
						EditText mEdit;
						Switch switchType;
						TimePicker tpResult;
						ParseObject establishment = null, deal_type = null;
						Calendar myCal = makeCalender();
						Date myDate;
						ParseGeoPoint location = null;
						String switchText, result, searchString, lat = null, lng = null;
						Spinner spinner;
						Integer deal_count = 0;
						LocationParser lParser;

						ParseObject deal = new ParseObject("Deal");
						mEdit = (EditText) findViewById(R.id.edit_deal_title);
						deal.put("title", mEdit.getText().toString());
						mEdit = (EditText) findViewById(R.id.edit_deal_details);
						deal.put("details", mEdit.getText().toString());
						mEdit = (EditText) findViewById(R.id.edit_deal_restrictions);
						deal.put("restrictions", mEdit.getText().toString());
						tpResult = (TimePicker) findViewById(R.id.edit_deal_timePicker_start);
						myCal.set(Calendar.HOUR_OF_DAY, tpResult.getCurrentHour());
						myCal.set(Calendar.MINUTE, tpResult.getCurrentMinute());
						myDate = myCal.getTime();
						deal.put("time_start", myDate);
						tpResult = (TimePicker) findViewById(R.id.edit_deal_timePicker_stop);
						myCal = makeCalender();
						myCal.set(Calendar.HOUR_OF_DAY, tpResult.getCurrentHour());
						myCal.set(Calendar.MINUTE, tpResult.getCurrentMinute());
						myDate = myCal.getTime();
						deal.put("time_end", myDate);
						deal.put("up_votes", 0);
						deal.put("down_votes", 0);
						// dpResult =
						// (DatePicker)findViewById(R.id.edit_deal_timePicker_start);
						deal.put("date_start", myDate);
						// dpResult =
						// (DatePicker)findViewById(R.id.edit_deal_timePicker_stop);
						deal.put("date_end", myDate);
						spinner = (Spinner) findViewById(R.id.deal_day_spinner);
						deal.put("day", spinner.getSelectedItem().toString());
						deal.put("yelp_id", intent.getStringExtra("yelp_id"));

						ParseQuery<ParseObject> queryEstablishment = ParseQuery
								.getQuery("Establishment");
						queryEstablishment.whereEqualTo("objectId",
								intent.getStringExtra("establishment_id"));
						try {
							establishment = queryEstablishment.getFirst();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						if (establishment != null) {
							Log.d("establishment", establishment.toString());
							location = establishment.getParseGeoPoint("location");
							deal_count = Integer.parseInt(establishment.getString("deal_count")) + 1;
							establishment.put("deal_count", deal_count.toString());
						} else {
							searchString = intent.getStringExtra("address").replaceAll("\\s+", "+")
									+ "+" + intent.getStringExtra("city").replaceAll("\\s+", "+")
									+ "+" + intent.getStringExtra("state").replaceAll("\\s+", "+")
									+ "+" + intent.getStringExtra("zip");
							OAuthRequest request = new OAuthRequest(Verb.GET,
									"http://maps.googleapis.com/maps/api/geocode/json?address="
											+ searchString + "&sensor=true");
							Response response = request.send();
							result = response.getBody();

							lParser = new LocationParser();
							lParser.setResponse(result);
							try {
								lParser.parseLocation();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								// Do whatever you want with the error, like
								// throw a Toast error report
							}

							try {
								lat = lParser.getLat();
								lng = lParser.getLng();
							} catch (JSONException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

							ParseGeoPoint newLocation = new ParseGeoPoint(Double.parseDouble(lat),
									Double.parseDouble(lng));

							Log.d("establishment", "CREATING ESTABLISHMENT");
							ParseObject addEstablishment = new ParseObject("Establishment");
							addEstablishment.put("location", newLocation);
							addEstablishment.put("yelp_id", intent.getStringExtra("yelp_id"));
							addEstablishment.put("deal_count", "1");
							try {
								addEstablishment.save();
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							establishment = addEstablishment;
							location = newLocation;
						}

						switchType = (Switch) findViewById(R.id.deal_type_switch);
						if (switchType.isChecked()) {
							switchText = "Food";
						} else {
							switchText = "Drinks";
						}
						Log.d("deal_type", switchText);
						ParseQuery<ParseObject> queryDealType = ParseQuery.getQuery("deal_type");
						queryDealType.whereEqualTo("name", switchText);
						try {
							deal_type = queryDealType.getFirst();
							Log.d("deal_type", deal_type.toString());
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						deal.put("establishment", establishment);
						deal.put("deal_type", deal_type);
						deal.put("user", ParseUser.getCurrentUser());
						deal.put("location", location);
						deal.put("up_votes", 0);
						deal.put("down_votes", 0);
						deal.put("rating", 0);
						try {
							deal.save();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						if (dealAddProgressDialog != null) {
							dealAddProgressDialog.dismiss();
							dealAddProgressDialog = null;
						}
						DealAddActivity.this.finish();
					}
				}.execute();
			}
		});
	}

	private Calendar makeCalender() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 0000);
		cal.set(Calendar.MONTH, 00);
		cal.set(Calendar.DATE, 00);
		cal.set(Calendar.HOUR_OF_DAY, 00);
		cal.set(Calendar.MINUTE, 00);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	@Override
	public void onPause() {
		super.onPause();
		if (dealAddProgressDialog != null) {
			dealAddProgressDialog.dismiss();
			dealAddProgressDialog = null;
		}
	}

}
