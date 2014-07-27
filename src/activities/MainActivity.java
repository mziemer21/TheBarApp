package activities;

import navigation.NavDrawer;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger.LogLevel;
import com.google.android.gms.analytics.Tracker;
import com.thebarapp.ParseApplication;
import com.thebarapp.R;
import com.thebarapp.ParseApplication.TrackerName;


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
		GoogleAnalytics.getInstance(this).getLogger().setLogLevel(LogLevel.VERBOSE);
		 // Get tracker.
        Tracker t = ((ParseApplication) this.getApplication()).getTracker(TrackerName.APP_TRACKER);

        // Set screen name.
        // Where path is a String representing the screen name.
        t.setScreenName("Main Activity");

        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());
        GoogleAnalytics.getInstance(getBaseContext()).dispatchLocalHits();
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(getApplicationContext(), LoginActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
	}
}
