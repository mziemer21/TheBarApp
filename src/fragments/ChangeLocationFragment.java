package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebarapp.R;

/****
 * This is a fragment launched from the nav drawer It allows the user to force a
 * location
 * 
 * @author zieme_000
 * 
 */
public class ChangeLocationFragment extends Fragment {

	public ChangeLocationFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_change_location, container, false);

		return rootView;
	}
	
	
}
