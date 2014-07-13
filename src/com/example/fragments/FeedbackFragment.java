package com.example.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thebarapp.R;

/****
 * This is a fragment launched from the nav drawer It allows users to send us an
 * email
 * 
 * @author zieme_000
 * 
 */
public class FeedbackFragment extends Fragment {

	public FeedbackFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_feedback, container, false);

		return rootView;
	}
}
