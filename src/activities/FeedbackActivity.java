package activities;

import navigation.NavDrawer;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger.LogLevel;
import com.google.android.gms.analytics.Tracker;
import com.parse.ParseUser;
import com.thebarapp.ParseApplication;
import com.thebarapp.ParseApplication.TrackerName;
import com.thebarapp.R;

/***
 * empty main page that loads the nav drawer and home fragment
 * 
 * @author zieme_000
 * 
 */
public class FeedbackActivity extends NavDrawer {

	private Button send;
	private EditText subject, message;
	private String subjectS, messageS;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_feedback);
		super.onCreate(savedInstanceState);
		GoogleAnalytics.getInstance(this).getLogger().setLogLevel(LogLevel.VERBOSE);
		// Get tracker.
		Tracker t = ((ParseApplication) this.getApplication()).getTracker(TrackerName.APP_TRACKER);

		// Set screen name.
		// Where path is a String representing the screen name.
		t.setScreenName("Feedback Activity");

		// Send a screen view.
		t.send(new HitBuilders.AppViewBuilder().build());
		GoogleAnalytics.getInstance(getBaseContext()).dispatchLocalHits();
		
		send = (Button) findViewById(R.id.send_button);
		
		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				subject = (EditText) findViewById(R.id.email_subject);
				message = (EditText) findViewById(R.id.email_message);
				
				subjectS = subject.getText().toString();
				messageS = message.getText().toString();
				sendEmail();
			}
		});

	}

	protected void sendEmail() {

	      String[] TO = {"contact.the.bar.app@gmail.com"};
	      String[] CC = {};
	      Intent emailIntent = new Intent(Intent.ACTION_SEND);
	      emailIntent.setData(Uri.parse("mailto:"));
	      emailIntent.setType("text/plain");


	      emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
	      emailIntent.putExtra(Intent.EXTRA_CC, CC);
	      emailIntent.putExtra(Intent.EXTRA_SUBJECT, subjectS + " " + ParseUser.getCurrentUser().getObjectId());
	      emailIntent.putExtra(Intent.EXTRA_TEXT, messageS);

	      try {
	         startActivity(Intent.createChooser(emailIntent, "Send mail..."));
	         finish();
	         Log.i("Finished sending email...", "");
	      } catch (android.content.ActivityNotFoundException ex) {
	         Toast.makeText(FeedbackActivity.this, 
	         "There is no email client installed.", Toast.LENGTH_SHORT).show();
	      }
	   }
}
