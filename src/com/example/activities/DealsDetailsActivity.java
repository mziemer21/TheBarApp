package com.example.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.thebarapp.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class DealsDetailsActivity extends Activity{
	//Declare Variables
	String deal_id, deal_title, deal_details;
	Integer rating, price;
	Intent intent;
	ParseObject est = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		intent = getIntent();
		
		// Get the arguments from intent
        deal_id = intent.getStringExtra("deal_id");
        deal_title = intent.getStringExtra("deal_title");
        deal_details = intent.getStringExtra("deal_details");
        
        ParseQuery<ParseObject> queryEstablishment = ParseQuery.getQuery("Establishment");
		queryEstablishment.whereEqualTo("objectId", intent.getStringExtra("establishment_id"));
		try {
			est = queryEstablishment.getFirst();
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        setContentView(R.layout.activity_deal_details);
		
        TextView title = (TextView) findViewById(R.id.dealTitle);
        title.setText(deal_title);
        
		TextView details = (TextView) findViewById(R.id.dealDetail);
		details.setText(deal_details);
		
		TextView establishment = (TextView) findViewById(R.id.dealEstablishment);
		//establishment.setText(est.getString("name").toString());
		
		
	}
}
