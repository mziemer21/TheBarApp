package com.example.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thebarapp.R;


/***
 * This is a fragment launched from the nav drawer
 * It is just a short about page
 * @author zieme_000
 *
 */
public class AboutFragment extends Fragment {
	
	public AboutFragment(){}
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
         
        return rootView;
    }
}
