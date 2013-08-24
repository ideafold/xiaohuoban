package com.heibuddy.xiaohuoban.types;

public class User 
{
	private String mId;
	private String mName;
	private String mEmail;
	private String mPassword;
	
    public User() 
    {
    }

    public User(String name, String email, String password) 
    {
    	mId = null;
    	mName = name;
    	mEmail = email;
    	mPassword = password;
    }
    
    public String getId()
    {
    	return mId;
    }
    
    public String getName()
    {
    	return mName;
    }
    
    public String getEmail()
    {
    	return mEmail;
    }
    
    public String getPassword()
    {
    	return mPassword;
    }
    
}
