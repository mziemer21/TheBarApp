package activities;

import navigation.NavDrawer;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger.LogLevel;
import com.google.android.gms.analytics.Tracker;
import com.thebarapp.ParseApplication;
import com.thebarapp.ParseApplication.TrackerName;
import com.thebarapp.R;


/***
 * empty main page that loads the nav drawer and home fragment
 * 
 * @author zieme_000
 * 
 */
public class MainActivity extends NavDrawer {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_main);
		super.onCreate(savedInstanceState);
		// Get tracker.
		((ParseApplication) getApplication()).getTracker(ParseApplication.TrackerName.APP_TRACKER);
        
        Button bars, random, map, deals, favorites;

		bars = (Button) findViewById(R.id.buttonList);
		random = (Button) findViewById(R.id.buttonRandom);
		map = (Button) findViewById(R.id.buttonMap);
		deals = (Button) findViewById(R.id.buttonDeals);
		favorites = (Button) findViewById(R.id.buttonFavorites);

		bars.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent ListActivity = new Intent(MainActivity.this, ListActivity.class);
				startActivity(ListActivity);
			}
		});

		random.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent RandomActivity = new Intent(MainActivity.this, RandomSearchActivity.class);
				startActivity(RandomActivity);
			}
		});

		map.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent MapActivity = new Intent(MainActivity.this, MapActivity.class);
				startActivity(MapActivity);
			}
		});

		deals.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent DealActivity = new Intent(MainActivity.this, DealSearchActivity.class);
				startActivity(DealActivity);
			}
		});
		
		favorites.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent FavoritesActivity = new Intent(MainActivity.this, FavoritesActivity.class);
				startActivity(FavoritesActivity);
			}
		});
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
