package com.example.thebarapp;

public class Deal {
	String id, title, details, restrictions, establishment_id, yelp_id, type, day;
	Integer up_votes, down_votes;
	Double lat, lng;

	// getters
	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDetails() {
		return details;
	}

	public String getRestrictions() {
		return restrictions;
	}

	public String getEstablishmentId() {
		return establishment_id;
	}

	public String getYelpId() {
		return yelp_id;
	}

	public String getType() {
		return type;
	}

	public String getDay() {
		return day;
	}

	public Integer getUpVotes() {
		return up_votes;
	}

	public Integer getDownVotes() {
		return down_votes;
	}

	public Integer getRating() {
		if ((up_votes + down_votes) != 0) {
			return (up_votes / (up_votes + down_votes)) * 100;
		} else {
			return 0;
		}
	}

	public Double getLatitude() {
		return lat;
	}

	public Double getLongitude() {
		return lng;
	}

	// setters
	public void setId(String val) {
		id = val;
	}

	public void setTitle(String val) {
		title = val;
	}

	public void setDetails(String val) {
		details = val;
	}

	public void setRestrictions(String val) {
		restrictions = val;
	}

	public void setEstablishmentId(String val) {
		establishment_id = val;
	}

	public void setYelpId(String val) {
		yelp_id = val;
	}

	public void setType(String val) {
		type = val;
	}

	public void setDay(String val) {
		day = val;
	}

	public void addUpVote() {
		up_votes++;
	}

	public void addDownVote() {
		down_votes++;
	}

	public void setLatitude(String val) {
		lat = Double.parseDouble(val);
	}

	public void setLongitude(String val) {
		lng = Double.parseDouble(val);
	}
}
