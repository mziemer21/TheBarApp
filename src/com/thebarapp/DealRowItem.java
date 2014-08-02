package com.thebarapp;

public class DealRowItem {
	private String title;
	private String rating;
	private String time;

	public DealRowItem(String title, String rating, String time) {
		this.title = title;
		this.rating = rating;
		this.time = time;
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

	@Override
	public String toString() {
		return title + "\n" + rating;
	}
}
