package activities;

import java.util.Calendar;

import navigation.NavDrawer;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.thebarapp.Helper;
import com.thebarapp.ParseApplication;
import com.thebarapp.R;

public class ListSearchActivity extends NavDrawer {

	private Button searchListButton;
	private ToggleButton oneMi, threeMi, fiveMi, tenMi, twentyMi;
	private String distance = "1";
	private Calendar calendar = Calendar.getInstance();
	private Integer today = calendar.get(Calendar.DAY_OF_WEEK), search_type;
	private Spinner day_of_week, search_type_spinner;
	private EditText query;
	private CheckBox only_deals;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Get the view from listview_main.xml
				setContentView(R.layout.list_search);
		super.onCreate(savedInstanceState);
		
		// Get tracker.
		((ParseApplication) getApplication()).getTracker(ParseApplication.TrackerName.APP_TRACKER);

		searchListButton = (Button) findViewById(R.id.list_filter_button);
		oneMi = (ToggleButton) findViewById(R.id.list_filter_one_mile);
		threeMi = (ToggleButton) findViewById(R.id.list_filter_three_miles);
		fiveMi = (ToggleButton) findViewById(R.id.list_filter_five_miles);
		tenMi = (ToggleButton) findViewById(R.id.list_filter_ten_miles);
		twentyMi = (ToggleButton) findViewById(R.id.list_filter_twenty_miles);
		search_type_spinner = (Spinner) findViewById(R.id.list_filter_search_type);
		day_of_week = (Spinner) findViewById(R.id.list_filter_day_of_week);
		query = (EditText) findViewById(R.id.list_filter_keyword);
		only_deals = (CheckBox) findViewById(R.id.list_filter_check_box_only_deals);

		Helper.setDate(today, day_of_week);

		oneMi.setChecked(true);

		searchListButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (search_type_spinner.getSelectedItem().toString().compareTo("Alphabetical") == 0) {
					search_type = 1;
				} else if (search_type_spinner.getSelectedItem().toString().compareTo("Distance") == 0) {
					search_type = 0;
				} else if (search_type_spinner.getSelectedItem().toString()
						.compareTo("Highest Rated") == 0) {
					search_type = 2;
				}

				Intent listActivity = new Intent(ListSearchActivity.this, ListActivity.class);
				listActivity.putExtra("search_type", search_type == null ? 1 : search_type);
				listActivity.putExtra("day_of_week", day_of_week.getSelectedItem().toString());
				listActivity.putExtra("distance", distance);
				listActivity.putExtra("query", query.getText().toString());
				listActivity.putExtra("only_deals", only_deals.isChecked());
				startActivity(listActivity);
			}
		});

		oneMi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (oneMi.isChecked()) {
					threeMi.setChecked(false);
					fiveMi.setChecked(false);
					tenMi.setChecked(false);
					twentyMi.setChecked(false);

					distance = "1";
				} 
			}
		});

		threeMi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (threeMi.isChecked()) {
					oneMi.setChecked(false);
					fiveMi.setChecked(false);
					tenMi.setChecked(false);
					twentyMi.setChecked(false);
					
					distance = "3";
				} else {
					distance = "1";
				}
				
			}
		});

		fiveMi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (fiveMi.isChecked()) {
					threeMi.setChecked(false);
					oneMi.setChecked(false);
					tenMi.setChecked(false);
					twentyMi.setChecked(false);

					distance = "5";
				} else {
					distance = "1";
				}
			}
		});

		tenMi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (tenMi.isChecked()) {
					threeMi.setChecked(false);
					fiveMi.setChecked(false);
					oneMi.setChecked(false);
					twentyMi.setChecked(false);

					distance = "10";
				} else {
					distance = "1";
				}
			}
		});

		twentyMi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (twentyMi.isChecked()) {
					threeMi.setChecked(false);
					fiveMi.setChecked(false);
					tenMi.setChecked(false);
					oneMi.setChecked(false);

					distance = "20";
				} else {
					distance = "1";
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.filter_clear, menu);

		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * On selecting action bar icons
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Take appropriate action for each action item click
		switch (item.getItemId()) {
		case R.id.filter_clear:
			threeMi.setChecked(false);
			oneMi.setChecked(true);
			fiveMi.setChecked(false);
			tenMi.setChecked(false);
			twentyMi.setChecked(false);
			Helper.setDate(today, day_of_week);
			query.setText("");
			search_type_spinner.setSelection(0);
			return true;
		default:
			return super.onOptionsItemSelected(item);
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
}
