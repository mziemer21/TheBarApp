package com.thebarapp;

public class MyMarker
{
    private String name, dealCount, reviewCount, rating;
    Double lat, lng;

    public MyMarker(String nameIn, String reviewCountIn, String dealCountIn, String ratingIn, Double latIn, Double lngIn)
    {
        this.name = nameIn;
        this.reviewCount = reviewCountIn;
        this.rating = ratingIn;
        this.dealCount = dealCountIn;
        this.lat = latIn;
        this.lng = lngIn;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDealCount()
    {
        return dealCount;
    }

    public void setDealCount(String dealCount)
    {
        this.dealCount = dealCount;
    }

    public String getReviewCount()
    {
        return reviewCount;
    }

    public void setReviewCount(String reviewCount)
    {
        this.reviewCount = reviewCount;
    }

    public String getRating()
    {
        return rating;
    }

    public void setRating(String rating)
    {
        this.rating = rating;
    }
    
    public Double getLat()
    {
    	return lat;
    }
    
    public void setLat(Double latIn)
    {
    	this.lat = latIn;
    }
    
    public Double getLng()
    {
    	return lng;
    }
    
    public void setLng(Double lngIn)
    {
    	this.lng = lngIn;
    }
}
