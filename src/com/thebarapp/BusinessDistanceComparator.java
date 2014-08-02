package com.thebarapp;

import java.util.Comparator;

public class BusinessDistanceComparator implements Comparator<Business> {

	@Override
	public int compare(Business lhs, Business rhs) {
		return lhs.distance.compareTo(rhs.distance);
	}

}
