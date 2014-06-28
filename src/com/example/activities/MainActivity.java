package com.example.activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.navigation.NavDrawer;

/***
 * empty main page that loads the nav drawer and home fragment
 * @author zieme_000
 *
 */
public class MainActivity extends NavDrawer{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onBackPressed() {
		Intent i = new Intent(getApplicationContext(), LoginActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); 
		startActivity(i);
	}
	
}
