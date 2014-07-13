package com.example.thebarapp;

public class DealRowItem {
	private String title;
	private String rating;

	public DealRowItem(String title, String rating) {
		this.title = title;
		this.rating = rating;
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

	@Override
	public String toString() {
		return title + "\n" + rating;
	}
}
