package com.thebarapp;

public class DealRowItem {
	private String title, rating, time, name;
	private Boolean showName;

	public DealRowItem(String title, String rating, String time, String name, Boolean showName) {
		this.title = title;
		this.rating = rating;
		this.time = time;
		this.name = name;
		this.showName = showName;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Boolean getShowName(){
		return showName;
	}
	
	public void setShowName(Boolean showName){
		this.showName = showName;
	}

	@Override
	public String toString() {
		return title + "\n" + rating;
	}

}
