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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.thebarapp.Helper;
import com.thebarapp.R;

public class DealSearchActivity extends NavDrawer {

	private Button searchDealButton;
	private ToggleButton oneMi, threeMi, fiveMi, tenMi, twentyMi;
	private String distance = "3";
	private Calendar calendar = Calendar.getInstance();
	int today = calendar.get(Calendar.DAY_OF_WEEK), search_type;
	private Spinner day_of_week, search_type_spinner;
	private ToggleButton food, drinks;
	private EditText query;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Get the view from listview_main.xml
		setContentView(R.layout.deal_search);
		super.onCreate(savedInstanceState);

		searchDealButton = (Button) findViewById(R.id.deal_filter_button);
		oneMi = (ToggleButton) findViewById(R.id.deal_filter_one_mile);
		threeMi = (ToggleButton) findViewById(R.id.deal_filter_three_miles);
		fiveMi = (ToggleButton) findViewById(R.id.deal_filter_five_miles);
		tenMi = (ToggleButton) findViewById(R.id.deal_filter_ten_miles);
		twentyMi = (ToggleButton) findViewById(R.id.deal_filter_twenty_miles);
		search_type_spinner = (Spinner) findViewById(R.id.deal_filter_search_type);
		day_of_week = (Spinner) findViewById(R.id.deal_filter_day_of_week);
		food = (ToggleButton) findViewById(R.id.deal_filter_type_food);
		drinks = (ToggleButton) findViewById(R.id.deal_filter_type_drinks);
		query = (EditText) findViewById(R.id.deal_filter_keyword);

		Helper.setDate(today, day_of_week);

		threeMi.setChecked(true);

		searchDealButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (search_type_spinner.getSelectedItem().toString().compareTo("Distance") == 0) {
					search_type = 0;
				} else if (search_type_spinner.getSelectedItem().toString()
						.compareTo("Highest Rated") == 0) {
					search_type = 1;
				}

				Intent dealSearchActivity = new Intent(DealSearchActivity.this, DealActivity.class);
				dealSearchActivity
						.putExtra("day_of_week", day_of_week.getSelectedItem().toString());
				dealSearchActivity.putExtra("distance", distance);
				dealSearchActivity.putExtra("food", food.isChecked());
				dealSearchActivity.putExtra("drinks", drinks.isChecked());
				dealSearchActivity.putExtra("query", query.getText().toString());
				dealSearchActivity.putExtra("search_type", search_type);
				startActivity(dealSearchActivity);
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
				} else {
					distance = "3";
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
				}
				distance = "3";
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
					distance = "3";
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
					distance = "3";
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
					distance = "3";
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
			threeMi.setChecked(true);
			oneMi.setChecked(false);
			fiveMi.setChecked(false);
			tenMi.setChecked(false);
			twentyMi.setChecked(false);
			Helper.setDate(today, day_of_week);
			query.setText("");
			food.setChecked(false);
			drinks.setChecked(false);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}