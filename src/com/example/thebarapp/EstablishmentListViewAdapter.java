package com.example.thebarapp;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class EstablishmentListViewAdapter extends ArrayAdapter<EstablishmentRowItem> {
 
    Context context;
 
    public EstablishmentListViewAdapter(Context context, int resourceId,
            List<EstablishmentRowItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }
     
    /*private view holder class*/
    private class ViewHolder {
        TextView txtTitle, txtAddress, txtDistance, txtDealCount, txtRatingCount;
		ImageView rating;
		
    }
     
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        EstablishmentRowItem rowItem = getItem(position);
         
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_item_establishment, null);
            holder = new ViewHolder();
            holder.rating = (ImageView) convertView.findViewById(R.id.est_list_rating);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.est_list_title);
			holder.txtAddress = (TextView) convertView.findViewById(R.id.est_list_address);
			holder.txtDistance = (TextView) convertView.findViewById(R.id.est_list_distance);
			holder.txtDealCount = (TextView) convertView.findViewById(R.id.est_list_deal_count);
			holder.txtRatingCount = (TextView) convertView.findViewById(R.id.est_list_rating_count);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
			
        if(rowItem.getRating() < .5){		
			holder.rating.setImageResource(R.drawable.zero_stars);       	
		} else if(rowItem.getRating() < 1) {
			holder.rating.setImageResource(R.drawable.one_stars);
		} else if(rowItem.getRating() < 1.5) {
			holder.rating.setImageResource(R.drawable.one_half_stars);
		} else if(rowItem.getRating() < 2) {
			holder.rating.setImageResource(R.drawable.two_stars);
		} else if(rowItem.getRating() < 2.5) {
			holder.rating.setImageResource(R.drawable.two_half_stars);
		} else if(rowItem.getRating() < 3) {
			holder.rating.setImageResource(R.drawable.three_stars);
		} else if(rowItem.getRating() < 3.5) {
			holder.rating.setImageResource(R.drawable.three_half_stars);
		} else if(rowItem.getRating() < 4) {
			holder.rating.setImageResource(R.drawable.four_stars);
		} else if(rowItem.getRating() < 4.5) {
			holder.rating.setImageResource(R.drawable.four_half_stars);
		} else if(rowItem.getRating() < 5) {
			holder.rating.setImageResource(R.drawable.five_stars);
		}
        holder.txtTitle.setText(rowItem.getTitle());
		holder.txtAddress.setText(rowItem.getAddress());
		holder.txtDistance.setText(rowItem.getDistance() + " mi");
		holder.txtDealCount.setText(rowItem.getDealCount() + " Deals");
		holder.txtRatingCount.setText(rowItem.getRatingCount() + " Reviews");
         
        return convertView;
    }
}