package com.thebarapp;

import org.json.JSONException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

public class Business {

	String establishment_id, mobile_url, rating, rating_count, deal_count = "NA", name, yelp_id,
			address, city, state, zip, display_phone, phone, distance, latitude, longitude;

	public String getEstablishmentId() {
		return establishment_id;
	}

	public String getName() {
		return name;
	}

	public String getRating() {
		return rating;
	}

	public String getRatingCount() {
		return rating_count;
	}

	public String getDealCount() {
		return deal_count;
	}

	public String getMobileURL() {
		return mobile_url;
	}

	public String getYelpId() {
		return yelp_id;
	}

	public String getDisplayPhone() {
		return display_phone;
	}

	public String getPhone() {
		return phone;
	}

	public String getDistance() {
		return distance;
	}

	public String getAddress() {
		return address;
	}

	public String getCity() {
		return city;
	}

	public String getZipcode() {
		return zip;
	}

	public String getState() {
		return state;
	}

	public String getLatitude() {
		return latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	// setters

	public void setEstablishmentId(String val) {
		establishment_id = val;
	}

	public void setName(String val) {
		name = val;
	}

	public void setRating(String val) {
		rating = val;
	}

	public void setRatingCount(String val) {
		rating_count = val;
	}

	public void setDealCount(String val) {
		deal_count = val;
	}

	public void setMobileURL(String val) {
		mobile_url = val;
	}

	public void setYelpId(String val) {
		yelp_id = val;
	}

	public void setDisplayPhone(String val) {
		display_phone = val;
	}

	public void setPhone(String val) {
		phone = val;
	}

	public void setDistance(String val) {
		distance = val;
	}

	public void setAddress(String val) {
		address = val;
	}

	public void setCity(String val) {
		city = val;
	}

	public void setZipcode(String val) {
		zip = val;
	}

	public void setState(String val) {
		state = val;
	}

	public void setLat(String val) {
		latitude = val;
	}

	public void setLng(String val) {
		longitude = val;
	}

	public void setLatLng(String addressIn, String cityIn, String stateIn, String zipIn) {
		String searchString = addressIn.replaceAll("\\s+", "+") + "+" + cityIn.replaceAll("\\s+", "+")
				+ "+" + stateIn.replaceAll("\\s+", "+") + "+" + zipIn;
		OAuthRequest request = new OAuthRequest(Verb.GET,
				"http://maps.googleapis.com/maps/api/geocode/json?address=" + searchString
						+ "&sensor=true");
		Response response = request.send();
		try {
			Thread.sleep(101);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
		String sResult = response.getBody();

		LocationParser lParser = new LocationParser();
		lParser.setResponse(sResult);
		try {
			lParser.parseLocation();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// Do whatever you want with the error, like throw a Toast error
			// report
		}

		try {
			latitude = lParser.getLat();
			longitude = lParser.getLng();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((yelp_id == null) ? 0 : yelp_id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Business other = (Business) obj;
		if (yelp_id == null) {
			if (other.yelp_id != null)
				return false;
		} else if (!yelp_id.equals(other.yelp_id))
			return false;
		return true;
	}

}
