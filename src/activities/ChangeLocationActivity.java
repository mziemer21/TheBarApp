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
public class ChangeLocationActivity extends NavDrawer {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_change_location);
		super.onCreate(savedInstanceState);
		
		 // Get tracker.
		((ParseApplication) getApplication()).getTracker(ParseApplication.TrackerName.APP_TRACKER);
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
