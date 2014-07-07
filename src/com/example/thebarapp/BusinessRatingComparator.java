package com.example.thebarapp;

import java.util.Comparator;

public class BusinessRatingComparator implements Comparator<Business> {

	@Override
	public int compare(Business arg0, Business arg1) {
		// TODO Auto-generated method stub
		return arg1.rating.compareTo(arg0.rating);
	}
}
