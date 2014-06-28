package com.example.thebarapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LocationParser {
    
    private String location_response;
    JSONArray locations;
	JSONObject geometry;
     
    /**
     * Sets Yelp's response for this class
     * @param response
     */
    public void setResponse(String response){location_response = response;}
     
    /**
     * Returns the set Yelp response
     * @return string location_response
     */
    public String getResponse(){return location_response;}
     
    /**
     * Parse's yelp's response for the business name; mobile url; and ratings url.
     * Mobile url and ratings url is separated by " ,,, "
     * @sets yelp_bundle(key = business name)
     * @sets keys arraylist with business name
     * @throws JSONException
     */
    public void parseLocation() throws JSONException{
        JSONObject o1 = new JSONObject(location_response);
        locations = o1.getJSONArray("results");
        JSONObject o2 = (JSONObject) locations.getJSONObject(0).get("geometry");
        geometry = o2.getJSONObject("location");
	
    }
    
    public Integer getJSONSize(){return locations.length();}
     
    /**
     * This gets the business's name, which is stored in the ArrayList keys, using
     * this class's stored results.
     * @param i
     * @return
     * @throws JSONException 
     */
    public String getLat() throws JSONException{return geometry.get("lat").toString();}
    
    public String getLng() throws JSONException{return geometry.get("lng").toString();}
    
}
