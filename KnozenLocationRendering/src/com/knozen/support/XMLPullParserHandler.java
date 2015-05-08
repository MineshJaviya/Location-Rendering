package com.knozen.support;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class XMLPullParserHandler {
	//Create a list to hold users object returned by parser
	List<Users> users;
	
	//single instance of Users to be added to list after parsing
	private Users user;
	
	//String object to hold the text for various fields of user
	private String text;
	
	//Constructor for parserHandler
	public XMLPullParserHandler(){
		this.users=new ArrayList<Users>();
	}
	
	//Return the list of users generated after parsing
	public List<Users> getUsers(){
		return users;
	}
	
	//Parse the xml file in the form of input stream and generate the users object list
	public List<Users> parse(InputStream in){
		
		//Create the instance of XMLPArser using PullParserfactory
		XmlPullParserFactory factory=null;
		XmlPullParser parser=null;
		try{
			factory=XmlPullParserFactory.newInstance();
			
			//set the parser as namespace aware
			factory.setNamespaceAware(true);
			
			//get the instance of parser with above set properties
			parser=factory.newPullParser();
			
			//Give parser the input and set inputEncoding to be null so that parser takes XML 1.0
			//as standard
			parser.setInput(in, null);
			
			//Start parsing
			int eventType=parser.getEventType();
			while(eventType!=XmlPullParser.END_DOCUMENT){
				//Get the name of the tag
				String tagname=parser.getName();
				switch(eventType){
				//Generate the users objects and set the fields based on eventType and tagnames
					case XmlPullParser.START_TAG:
											//Create the object if starting stag of last seen found
											if(tagname.equalsIgnoreCase("last_seen_at")){
												user=new Users();
											}
											break;
						
					case XmlPullParser.TEXT:
											//parse the text between the tags
											text=parser.getText();
											break;
						
					case XmlPullParser.END_TAG:
											//set elements of user object based on tagnames
											if(tagname.equalsIgnoreCase("last_seen_at")){
												users.add(user);
											}else if(tagname.equalsIgnoreCase("address")){
												user.setAddress(text);
											}else if(tagname.equalsIgnoreCase("lat")){
												user.setLatitude(text);
											}else if(tagname.equalsIgnoreCase("lon")){
												user.setLongitude(text);
											}else if(tagname.equalsIgnoreCase("timestamp")){
												user.setTimestamp(text);
											}
											break;
					default:
							break;
				}
				//get the next eventType
				eventType=parser.next();
			}
		}catch(XmlPullParserException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		return users;
	}
}
