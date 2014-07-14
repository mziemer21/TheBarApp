package com.example.yelp;

import java.util.concurrent.ExecutionException;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import android.os.AsyncTask;

/**
 * Example for accessing the Yelp API.
 */
public class Yelp {

	OAuthService service;
	Token accessToken;
	String term, distance, result;
	Double latitude, longitude;
	Integer sort_mode;

	/**
	 * Setup the Yelp API OAuth credentials.
	 * 
	 * OAuth credentials are available from the developer site, under Manage API
	 * access (version 2 API).
	 * 
	 * @param consumerKey
	 *            Consumer key
	 * @param consumerSecret
	 *            Consumer secret
	 * @param token
	 *            Token
	 * @param tokenSecret
	 *            Token secret
	 */
	public Yelp(String consumerKey, String consumerSecret, String token, String tokenSecret) {
		this.service = new ServiceBuilder().provider(YelpApi2.class).apiKey(consumerKey)
				.apiSecret(consumerSecret).build();
		this.accessToken = new Token(token, tokenSecret);
	}

	/**
	 * Search with term and location.
	 * 
	 * @param term
	 *            Search term
	 * @param latitude
	 *            Latitude
	 * @param longitude
	 *            Longitude
	 * @return JSON string response
	 */
	public String search(String termIn, double latitudeIn, double longitudeIn, String distanceIn,
			int sortModeIn) {
		
		term = termIn;
		latitude = latitudeIn;
		longitude = longitudeIn;
		distance = distanceIn;
		sort_mode = sortModeIn;
		
		try {
			result = new YelpFetchTask().execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
				
	}
	
	private class YelpFetchTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
	          
	    	OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
	  		request.addQuerystringParameter("category_filter", "bars");
	  		request.addQuerystringParameter("term", term);
	  		request.addQuerystringParameter("ll", latitude + "," + longitude);
	  		request.addQuerystringParameter("limit", "15");
	  		request.addQuerystringParameter("radius_filter", distance);
	  		request.addQuerystringParameter("sort", String.valueOf(sort_mode));

	  		service.signRequest(accessToken, request);
	  		Response response = request.send();
	  		return response.getBody();
	      }
	 }
}
