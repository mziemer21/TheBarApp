package com.example.yelp;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class YelpParser {
    
    private String yelp_response;
    JSONArray businesses;
     
    public void setResponse(String response){yelp_response = response;}
     
    public String getResponse(){return yelp_response;}
     
    public void parseBusiness() throws JSONException{
        JSONObject o1 = new JSONObject(yelp_response);
        businesses = o1.getJSONArray("businesses");
    }
    
    public Integer getJSONSize(){return businesses.length();}
     
    public String getBusinessName(int i) throws JSONException{return businesses.getJSONObject(i).get("name").toString();}
    
    public String getBusinessRating(int i) throws JSONException{return businesses.getJSONObject(i).get("rating").toString();}
     
    public String getBusinessMobileURL(int i) throws JSONException{ return businesses.getJSONObject(i).get("mobile_url").toString();} 
    
    public String getBusinessId(int i) throws JSONException{ return businesses.getJSONObject(i).get("id").toString();}
    
    public String getBusinessDisplayPhone(int i) throws JSONException{ return businesses.getJSONObject(i).get("display_phone").toString();}
    
    public String getBusinessPhone(int i) throws JSONException{ return businesses.getJSONObject(i).get("phone").toString();}
    
    public String getBusinessDistance(int i) throws JSONException{ return businesses.getJSONObject(i).get("distance").toString();}
    
    public Object getBusinessLocation(int i) throws JSONException { return businesses.getJSONObject(i).get("location");}
    
    public String getBusinessAddress(JSONObject location) throws JSONException{ return location.get("address").toString().replaceAll("\\[", "").replaceAll("\\]","").replace("\"","");}
    
    public String getBusinessCity(JSONObject location) throws JSONException{ return location.get("city").toString();}
    
    public String getBusinessZipcode(JSONObject location) throws JSONException{ return location.get("postal_code").toString();}
    
    public String getBusinessState(JSONObject location) throws JSONException{ return location.get("state_code").toString();}
    
    public ArrayList<Business> getBusinesses(String json){
    	ArrayList<Business> BusinessList = new ArrayList<Business>();
    	Object loc = null;
    	
    	setResponse(json);
    	try {
			parseBusiness();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	for (int i =0; getJSONSize() > i; i++) {
    		Business b = new Business();
    		
    		try {
				b.setMobileURL(getBusinessMobileURL(i));
    	    } catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} try {
				b.setRating(getBusinessRating(i));
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} try {
				b.setName(getBusinessName(i));
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} try {
				b.setYelpId(getBusinessId(i));
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} try {
				b.setPhone(getBusinessPhone(i));
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}try {
				b.setDisplayPhone(getBusinessDisplayPhone(i));
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} try {
				b.setDistance(getBusinessDistance(i));
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}try {
				loc = getBusinessLocation(i);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} try { 
				b.setAddress(getBusinessAddress((JSONObject) loc));
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} try { 
				b.setCity(getBusinessCity((JSONObject) loc));
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} try { 
				b.setState(getBusinessState((JSONObject) loc));
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} try { 
				b.setZipcode(getBusinessZipcode((JSONObject) loc));
    	    } catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} try { 
				b.setLatLng(getBusinessAddress((JSONObject) loc), getBusinessCity((JSONObject) loc), getBusinessState((JSONObject) loc), getBusinessZipcode((JSONObject) loc));
    	    } catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			
			BusinessList.add(b);
    	}
    	
    	return BusinessList;
    }
    
}
