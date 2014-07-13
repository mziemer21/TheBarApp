package com.example.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.activities.DealSearchActivity;
import com.example.activities.ListActivity;
import com.example.activities.MapActivity;
import com.example.activities.RandomSearchActivity;
import com.example.thebarapp.R;

/****
 * This is a fragment launched from the nav drawer It contains buttons to list,
 * map, random, deals
 * 
 * @author zieme_000
 * 
 */
public class HomeFragment extends Fragment {

	public HomeFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_home, container, false);

		Button bars, random, map, deals;

		bars = (Button) rootView.findViewById(R.id.buttonList);
		random = (Button) rootView.findViewById(R.id.buttonRandom);
		map = (Button) rootView.findViewById(R.id.buttonMap);
		deals = (Button) rootView.findViewById(R.id.buttonDeals);

		bars.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent ListActivity = new Intent(getActivity(), ListActivity.class);
				startActivity(ListActivity);
			}
		});

		random.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent RandomActivity = new Intent(getActivity(), RandomSearchActivity.class);
				startActivity(RandomActivity);
			}
		});

		map.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent MapActivity = new Intent(getActivity(), MapActivity.class);
				startActivity(MapActivity);
			}
		});

		deals.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent DealActivity = new Intent(getActivity(), DealSearchActivity.class);
				startActivity(DealActivity);
			}
		});

		return rootView;
	}

}
