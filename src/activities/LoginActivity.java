package activities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.thebarapp.R;

public class LoginActivity extends Activity {

	private Dialog loginProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Check if there is a currently logged in user
				// and they are linked to a Facebook account.
				ParseUser currentUser = ParseUser.getCurrentUser();
				if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
					// Go to the user info activity
					showNextActivity();
				}
				
				
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		// Add code to print out the key hash
		try{ 
			Log.d("Hash start", "Checking signs");
		    PackageInfo info = getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_SIGNATURES);
		    for (Signature signature : info.signatures) {
		        MessageDigest md = MessageDigest.getInstance("SHA");
		        md.update(signature.toByteArray());
		        Log.d("Hash reslut", Base64.encodeToString(md.digest(), Base64.DEFAULT));
		    }
		} catch (NameNotFoundException e) {
		    e.printStackTrace();
		    Log.d("Hash error", e.getMessage());
		} catch (NoSuchAlgorithmException e) {
		    e.printStackTrace();
		    Log.d("Hash error", e.getMessage());
		}
	}
	
	public void onButtonClick(View v){

        switch (v.getId()){

            case R.id.btnLogin:
            	onLoginButtonClicked();
            	break;
            	
            case R.id.btnSkip:
            	Intent i = new Intent(getApplicationContext(), MainActivity.class);
            	startActivity(i);   	
        } 
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}

	private void onLoginButtonClicked() {
		if(loginProgressDialog != null){
			loginProgressDialog.dismiss();
			loginProgressDialog = null;
		}
		
		loginProgressDialog = ProgressDialog.show(
				LoginActivity.this, "", "Logging in...", true);
		List<String> permissions = Arrays.asList("public_profile", "user_about_me",
				"user_relationships", "user_birthday", "user_location");
		ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException err) {
				if(loginProgressDialog != null){
					loginProgressDialog.dismiss();
					loginProgressDialog = null;
				}
				
				if (user == null) {
					Log.d("The Bar App",
							"Uh oh. The user cancelled the Facebook login.");
				} else if (user.isNew()) {
					Log.d("The Bar App",
							"User signed up and logged in through Facebook!");
					showNextActivity();
				} else {
					Log.d("The Bar App",
							"User logged in through Facebook!");
					showNextActivity();
				}
			}
		});
	}

	private void showNextActivity() {
		if(isTaskRoot())
		{
		Intent i = new Intent(getApplicationContext(), MainActivity.class);
    	startActivity(i);
		} else 
		{
			finish();
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(loginProgressDialog != null){
			loginProgressDialog.dismiss();
			loginProgressDialog = null;
		}
	}
}