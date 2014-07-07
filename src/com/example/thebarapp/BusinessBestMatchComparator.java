package com.example.thebarapp;

import java.util.Comparator;

public class BusinessBestMatchComparator implements Comparator<Business> {

	@Override
	public int compare(Business lhs, Business rhs) {
		// TODO Auto-generated method stub
		return lhs.name.compareTo(rhs.name);
	}
    
}
