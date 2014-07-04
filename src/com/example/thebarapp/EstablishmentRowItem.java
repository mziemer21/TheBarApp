package com.example.thebarapp;

public class EstablishmentRowItem {
    private String title, address, distance, dealCount, ratingCount;
	private Double rating;
     
    public EstablishmentRowItem(String title, Double rating, String address, String distance, String dealCount, String ratingCount) {
        this.title = title;
        this.rating = rating;
		this.address = address;
		this.distance = distance;
		this.dealCount = dealCount;
		this.ratingCount = ratingCount;
    }
	
	// getters
    public Double getRating() {
        return rating;
    }
	public String getTitle() {
        return title;
    }
	public String getAddress() {
		return address;
	}
	public String getDistance() {
		return distance;
	}
	public String getDealCount() {
		return dealCount;
	}
	public String getRatingCount() {
		return ratingCount;
	}
	
	// setters
    public void setRating(Double rating) {
        this.rating = rating;
    }
    public void setTitle(String title) {
        this.title = title;
    }
	public void setAddress(String address) {
		this.address = address;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public void setDealCount(String dealCount) {
		this.dealCount = dealCount;
	}
	public void setRatingCount(String ratingCount) {
		this.ratingCount = ratingCount;
	}
	   
}