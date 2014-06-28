package com.example.thebarapp;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;
 
public class ParseApplication extends Application {
 
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Add your initialization code here
        Parse.initialize(this, "Ksym3TM0XpfJtOBUEuv7Htd8hvA8gb5a8RjgIV1n", "7LCbwiH8Z54OLqv47E3ZuBrklqphw6yYssOT8ZuH");
 
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        
        ParseFacebookUtils.initialize("1403369603273131");
        
        // If you would like all objects to be private by default, remove this line.
        defaultACL.setPublicReadAccess(true);
 
        ParseACL.setDefaultACL(defaultACL, true);
    }
 
}
