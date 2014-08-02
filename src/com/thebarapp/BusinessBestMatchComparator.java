package com.thebarapp;

import java.util.Comparator;

public class BusinessBestMatchComparator implements Comparator<Business> {

	@Override
	public int compare(Business lhs, Business rhs) {
		return lhs.name.compareTo(rhs.name);
	}

}
