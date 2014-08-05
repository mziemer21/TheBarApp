package activities;

import java.util.Calendar;
import java.util.Date;

import navigation.NavDrawer;

import org.json.JSONException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.thebarapp.Helper;
import com.thebarapp.LocationParser;
import com.thebarapp.ParseApplication;
import com.thebarapp.R;

public class DealAddActivity extends NavDrawer {

	private Button submitButton, timeStartButton, timeEndButton;
	private Intent intent;
	private ProgressDialog ProgressDialog;
	private EditText mEdit;
	private Switch switchType;
	private ParseObject establishment = null, deal_type = null, deal = new ParseObject("Deal");
	private ParseGeoPoint location = null;
	private String switchText, result, searchString, lat = null, lng = null;
	static String timeOfDay;
	private Spinner day_of_week;
	private Calendar calendar = Calendar.getInstance();
	private Integer today = calendar.get(Calendar.DAY_OF_WEEK), deal_count = 0;
	private LocationParser lParser;
	static TextView timeStartText, timeEndText;
	static Date myDateStart, myDateEnd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_add_deal);
		super.onCreate(savedInstanceState);

		intent = getIntent();

		timeStartText = (TextView) findViewById(R.id.deal_time_start_text);
		timeEndText = (TextView) findViewById(R.id.deal_time_end_text);

		submitButton = (Button) this.findViewById(R.id.submitDealButton);
		timeStartButton = (Button) findViewById(R.id.time_start_button);
		timeEndButton = (Button) findViewById(R.id.time_end_button);
		day_of_week = (Spinner) findViewById(R.id.deal_day_spinner);

		Helper.setDate(today, day_of_week);
		
		// Get tracker.
		((ParseApplication) getApplication()).getTracker(ParseApplication.TrackerName.APP_TRACKER);

		timeStartButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				DialogFragment newFragment = new TimePickerFragmentStart();
				newFragment.show(getSupportFragmentManager(), "timePicker");
			}
		});

		timeEndButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				DialogFragment newFragment = new TimePickerFragmentEnd();
				newFragment.show(getSupportFragmentManager(), "timePicker");
			}
		});

		submitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				mEdit = (EditText) findViewById(R.id.edit_deal_title);
				if (mEdit.getText().toString().matches("")) {
					displayError("Please enter a title.");
				} else if (myDateStart == null) {
					displayError("Please select a start time.");
				} else if (myDateEnd == null) {
					displayError("Please select a end time.");
				} else {

					if (ProgressDialog != null) {
						ProgressDialog.dismiss();
						ProgressDialog = null;
					}
					// Create a progressdialog
					ProgressDialog = new ProgressDialog(DealAddActivity.this);
					// Set progressdialog message
					ProgressDialog.setMessage("Saving...");
					ProgressDialog.setIndeterminate(false);
					// Show progressdialog
					ProgressDialog.show();

					new AsyncTask<Void, Void, Void>() {
						@Override
						protected Void doInBackground(Void... params) {

							deal.put("title", mEdit.getText().toString());
							mEdit = (EditText) findViewById(R.id.edit_deal_details);
							deal.put("details", mEdit.getText().toString());
							mEdit = (EditText) findViewById(R.id.edit_deal_restrictions);
							deal.put("restrictions", mEdit.getText().toString());

							deal.put("up_votes", 0);
							deal.put("down_votes", 0);

							day_of_week = (Spinner) findViewById(R.id.deal_day_spinner);
							deal.put("day", day_of_week.getSelectedItem().toString());
							deal.put("yelp_id", intent.getStringExtra("yelp_id"));

							ParseQuery<ParseObject> queryEstablishment = ParseQuery.getQuery("Establishment");
							queryEstablishment.whereEqualTo("objectId", intent.getStringExtra("establishment_id"));
							try {
								establishment = queryEstablishment.getFirst();
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							try {
								Thread.sleep(101);
							} catch (InterruptedException ex) {
								Thread.currentThread().interrupt();
							}

							if (establishment != null) {
								Log.d("establishment", establishment.toString());
								location = establishment.getParseGeoPoint("location");
								deal_count = Integer.parseInt(establishment.getString("deal_count")) + 1;
								establishment.put("deal_count", deal_count.toString());
							} else {
								searchString = intent.getStringExtra("address").replaceAll("\\s+", "+") + "+" + intent.getStringExtra("city").replaceAll("\\s+", "+") + "+"
										+ intent.getStringExtra("state").replaceAll("\\s+", "+") + "+" + intent.getStringExtra("zip");
								OAuthRequest request = new OAuthRequest(Verb.GET, "http://maps.googleapis.com/maps/api/geocode/json?address=" + searchString + "&sensor=true");
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

								ParseGeoPoint newLocation = new ParseGeoPoint(Double.parseDouble(lat), Double.parseDouble(lng));

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
							deal.put("time_start", myDateStart);
							deal.put("time_end", myDateEnd);
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
							if (ProgressDialog != null) {
								ProgressDialog.dismiss();
								ProgressDialog = null;
							}
							DealAddActivity.this.finish();
							Toast.makeText(getApplicationContext(), "Deal Added!", Toast.LENGTH_LONG).show();
						}
					}.execute();
				}
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		if (ProgressDialog != null) {
			ProgressDialog.dismiss();
			ProgressDialog = null;
		}
	}

	public static class TimePickerFragmentStart extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);

			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// Do something with the time chosen by the user
			Calendar myCal = Calendar.getInstance();

			myCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
			myCal.set(Calendar.MINUTE, minute);
			myDateStart = myCal.getTime();
			if (hourOfDay < 12) {
				timeOfDay = "am";
			} else {
				timeOfDay = "pm";
			}
			timeStartText.setText(hourOfDay % 12 + ":" + minute + " " + timeOfDay);
		}
	}

	public static class TimePickerFragmentEnd extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);

			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// Do something with the time chosen by the user
			Calendar myCal = Calendar.getInstance();
			myCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
			myCal.set(Calendar.MINUTE, minute);
			myDateEnd = myCal.getTime();
			if (hourOfDay < 12) {
				timeOfDay = "am";
			} else {
				timeOfDay = "pm";
			}
			timeEndText.setText(hourOfDay % 12 + ":" + minute + " " + timeOfDay);
		}
	}

	private void displayError(String message) {
		// no deals found so display a popup and return to search options
		AlertDialog.Builder builder = new AlertDialog.Builder(DealAddActivity.this);

		// set title
		builder.setTitle("No Results");

		// set dialog message
		builder.setMessage(message).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		// create alert dialog
		AlertDialog alertDialog = builder.create();

		// show it
		alertDialog.show();
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
}
