package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thebarapp.R;

/***
 * Tab used by details fragment It contains info about an establishment
 * 
 * @author zieme_000
 * 
 */
public class DetailsTabFragment extends Fragment {

	// Declare Variables
	String name, rating, address;
	TextView txtName, txtRating, txtAddress;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootDetailsView = inflater.inflate(R.layout.fragment_details, container, false);

		/* get arguments from activity */
		Bundle extraDetails = getArguments();
		name = extraDetails.getString("name");
		rating = extraDetails.getString("rating");
		address = extraDetails.getString("address");

		// Locate the TextView in xml
		txtName = (TextView) rootDetailsView.findViewById(R.id.name);
		txtRating = (TextView) rootDetailsView.findViewById(R.id.rating);
		txtAddress = (TextView) rootDetailsView.findViewById(R.id.address);

		// Load the text into the TextView
		txtName.setText(" " + name);
		txtRating.setText(" " + rating);
		txtAddress.setText(" " + address);

		return rootDetailsView;
	}
}
