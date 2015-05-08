package com.knozen.support;

//Class to hold the objects parsed by XML Parser
public class Users {
	
	//Global counter for number of users
	private static int global_total=0;
	//Variables to hold the elements
	private String Name;
	private String Address;
	private String Latitude;
	private String Longitude;
	private String Timestamp;

	//Constructor for Users class
	public Users(){
		this.Name="User_"+(global_total+1);
		global_total++;	
	}
	
	public String getName(){
		return Name;
	}
	
	public static void resetGlobalTotal(){
		global_total=0;
	}
	public String getAddress(){
		return Address;
	}
	
	public void setAddress(String address){
		Address=address;
	}
	
	public String getLatitude() {
		return Latitude;
	}

	public void setLatitude(String latitude) {
		Latitude = latitude;
	}

	public String getLongitude() {
		return Longitude;
	}

	public void setLongitude(String longitude) {
		Longitude = longitude;
	}

	public String getTimestamp() {
		return Timestamp;
	}

	public void setTimestamp(String timestamp) {
		Timestamp = timestamp;
	}

	@Override
	public String toString(){
		return Name + "\n" + Address + "\n" + "Lat="+Latitude+", Lon="+Longitude+"\nTimestamp="+Timestamp;
	}
}
