package yelp;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thebarapp.Business;

public class YelpParser {

	private String yelp_response;
	JSONArray businesses;
	JSONObject o1;
	public static final int DISTANCE_IN_FEET = 20924640;

	public void setResponse(String response) {
		yelp_response = response;
	}

	public String getResponse() {
		return yelp_response;
	}

	public void parseBusinesses() throws JSONException {
		o1 = new JSONObject(yelp_response);
		businesses = o1.getJSONArray("businesses");
	}

	public Integer getJSONSize() {
		int size;
		try {
			size = businesses.length();
		} catch (NullPointerException e1) {
			size = 1;
		}

		return size;
	}

	public String getBusinessName(int i) throws JSONException {
		return businesses.getJSONObject(i).get("name").toString();
	}

	public String getBusinessRating(int i) throws JSONException {
		return businesses.getJSONObject(i).get("rating").toString();
	}

	public String getBusinessRatingCount(int i) throws JSONException {
		return businesses.getJSONObject(i).get("review_count").toString();
	}

	public String getBusinessMobileURL(int i) throws JSONException {
		return businesses.getJSONObject(i).get("mobile_url").toString();
	}

	public String getBusinessId(int i) throws JSONException {
		return businesses.getJSONObject(i).get("id").toString();
	}

	public String getBusinessDisplayPhone(int i) throws JSONException {
		return businesses.getJSONObject(i).get("display_phone").toString();
	}

	public String getBusinessPhone(int i) throws JSONException {
		return businesses.getJSONObject(i).get("phone").toString();
	}

	public String getBusinessDistance(int i) throws JSONException {
		Double distance = Double.parseDouble(businesses.getJSONObject(i).get("distance").toString()) * 0.00062137119;
		return distance.toString().substring(0, 4);
	}

	public Object getBusinessLocation(int i) throws JSONException {
		return businesses.getJSONObject(i).get("location");
	}

	public String getBusinessAddress(JSONObject location) throws JSONException {
		return location.getString("address").toString().replaceAll("\\[", "").replaceAll("\\]", "").replace("\"", "");
	}

	public String getBusinessCity(JSONObject location) throws JSONException {
		return location.get("city").toString();
	}

	public String getBusinessZipcode(JSONObject location) throws JSONException {
		return location.get("postal_code").toString();
	}

	public String getBusinessState(JSONObject location) throws JSONException {
		return location.get("state_code").toString();
	}

	public ArrayList<Business> getBusinesses(String json, boolean location, String lat, String lng, Boolean searchBusiness, Double latCur, Double lngCur) {
		ArrayList<Business> BusinessList = new ArrayList<Business>();
		Object loc = null;

		setResponse(json);
		
		try {
			parseBusinesses();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (searchBusiness) {
			Business b = new Business();
			try {
				b.setMobileURL(o1.getString("mobile_url"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				b.setRating(o1.getString("rating"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				b.setRatingCount(o1.getString("review_count"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				b.setName(o1.getString("name"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				b.setYelpId(o1.getString("id"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				b.setPhone(o1.getString("phone"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				b.setDisplayPhone(o1.getString("display_phone"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				loc = (Object) o1.get("location");
				b.setAddress(getBusinessAddress((JSONObject) loc));
				b.setLat(lat);
				b.setLng(lng);
				b.setDistance(calculateDistance(Double.parseDouble(lat), Double.parseDouble(lng), latCur, lngCur));
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			try {
				b.setCity(getBusinessCity((JSONObject) loc));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				b.setState(getBusinessState((JSONObject) loc));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				b.setZipcode(getBusinessZipcode((JSONObject) loc));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			BusinessList.add(b);
		} else {
			

			for (int i = 0; getJSONSize() > i; i++) {
				Business b = new Business();

				try {
					b.setMobileURL(getBusinessMobileURL(i));
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					b.setRating(getBusinessRating(i));
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					b.setRatingCount(getBusinessRatingCount(i));
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					b.setName(getBusinessName(i));
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					b.setYelpId(getBusinessId(i));
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					b.setPhone(getBusinessPhone(i));
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					b.setDisplayPhone(getBusinessDisplayPhone(i));
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					b.setDistance(getBusinessDistance(i));
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					loc = getBusinessLocation(i);
					b.setAddress(getBusinessAddress((JSONObject) loc));
					b.setLatLng(getBusinessAddress((JSONObject) loc), getBusinessCity((JSONObject) loc), getBusinessState((JSONObject) loc), getBusinessZipcode((JSONObject) loc));
					
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					b.setCity(getBusinessCity((JSONObject) loc));
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					b.setState(getBusinessState((JSONObject) loc));
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					b.setZipcode(getBusinessZipcode((JSONObject) loc));
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				BusinessList.add(b);
			}
		}

		return BusinessList;
	}
	
	public static String calculateDistance(double latitudeA, double longitudeA, double latitudeB, double longitudeB){

	    Double distance;
	    
	    distance =  Math.acos(       

	                               Math.cos(Math.toRadians(latitudeA)) * Math.cos(Math.toRadians(latitudeB)) 
	                           *
	                               Math.cos(Math.toRadians(longitudeB) - Math.toRadians(longitudeA))
	                           +
	                               Math.sin(Math.toRadians(latitudeA))
	                           *
	                               Math.sin(Math.toRadians(latitudeB))

	                       );

	    return distance.toString().substring(0, 4);

	}

}
