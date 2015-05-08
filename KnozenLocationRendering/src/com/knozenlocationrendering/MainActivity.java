package com.knozenlocationrendering;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.knozen.support.Users;
import com.knozen.support.XMLPullParserHandler;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;


public class MainActivity extends FragmentActivity {
	
	//List for Adapter
	private ListView listview;
	
	//Toggle button specific holders
	private TextView switchStatus;
	private Switch mySwitch;
	
	//Layouts to display list and Map
	private RelativeLayout relative_layout_1;
	private RelativeLayout relative_layout_2;
	
	//Layout of display no connection
	private RelativeLayout relative_layout_3;
	
	//Google Map object
	private GoogleMap map_object;
	
	//Static variable to hold the given url string
	private static final String URL_GIVEN="https://s3-us-west-2.amazonaws.com/interview.knozen.com/locations.xml";
	
	//Reference to the generate user list
	private List<Users> user_list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Get the object of listview from layout
		listview = (ListView) findViewById(R.id.list);
		
		//Get the Relative Layouts
		relative_layout_1 = (RelativeLayout)findViewById(R.id.contentLayoutId1);
		relative_layout_2 = (RelativeLayout)findViewById(R.id.contentLayoutId2);
		relative_layout_3 = (RelativeLayout)findViewById(R.id.contentLayoutId3);
		
		//Get the toggle switch from layout
		mySwitch = (Switch)findViewById(R.id.mySwitch);
		switchStatus = (TextView)findViewById(R.id.switchStatus);
		
		//get the map object from layout
		map_object = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		
		//check whether Internet connection exists or not
		Boolean connect=checkConnection(this);
		
		if(connect){
			//if Internet connection is present, download the xml file
			new DownloadXml().execute(URL_GIVEN);
			
		}else{
			//Show the error Page
			//Hide switch ans switch status
			mySwitch.setVisibility(View.GONE);
			switchStatus.setVisibility(View.GONE);
			
			//Hide the list layout
			relative_layout_1.setVisibility(View.GONE);
			
			//Make Click-able Error page visible
			relative_layout_3.setVisibility(View.VISIBLE);
			
			//Set up the onclick listener
			relative_layout_3.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//check for the connectivity
					ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo info=conMgr.getActiveNetworkInfo();
					if(info!=null && info.isAvailable() && info.isConnected()){
						//Network is now available. restart the activity
						Intent intent = new Intent(MainActivity.this, MainActivity.class);
						finish();
						startActivity(intent);     
					}
				}
			});
		}
		
		//Set toggle switch on list view
		mySwitch.setChecked(true);
		
		//Change the text to display within the switch
		mySwitch.setTextOn("List");
		mySwitch.setTextOff("Map");
		
		//Set the onclickListener for the toggle switch to toggle between list and map
		mySwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				
				if(isChecked){
					//Toggle switch is on list mode
					//Hide the map layout
			    	relative_layout_2.setVisibility(View.GONE);
			    	//Make List layout Visible
			    	relative_layout_1.setVisibility(View.VISIBLE);
			    	//Change the text that displays the status of switch
			    	switchStatus.setText("Switch To view Map");
			    }else{
			    	//Toggle switch is on Map mode
					//Hide the List layout
			    	relative_layout_1.setVisibility(View.GONE);
			    	//Make Map layout Visible
			    	relative_layout_2.setVisibility(View.VISIBLE);
			    	//Change the text that displays the status of switch
			    	switchStatus.setText("Switch to View List");
			    }
				
			}
			
		});
		
		//check the current state before displaying the screen
		if(mySwitch.isChecked()){
			//Switch is on list mode
			switchStatus.setText("Switch To view Map");
		}else{
			//Switch is on Map mode
			switchStatus.setText("Switch To view List");
		}
		
		//Make the elements of list click-able
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				//Retrieve the object that is clicked on list
				Users usr=user_list.get(position);
				
				//Generate the intent with corresponding values from the user object
				Intent intent = new Intent(MainActivity.this, ShowMap.class);
				intent.putExtra("name", usr.getName());
	            intent.putExtra("latitude", usr.getLatitude());
	            intent.putExtra("longitude", usr.getLongitude());
	            
	            //Start the activity to show the map
	            startActivity(intent);
	            
			}
			
		});
		
	}
	
	//Function to check the availability of Internet connection
	private Boolean checkConnection(Context context){
		
		//Get the instance of Connectivity Manager to access Network Information
		ConnectivityManager conMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info=conMgr.getActiveNetworkInfo();

		if(info==null){
			//If info is null means unable to get the connection info. Return false
			return false;
		}else if(!info.isAvailable()){
			//Connection unavailable. Return false
			return false;
		}else if(!info.isConnected()){
			//Not connected to Internet. Return false
			return false;
		}
		//If none of above is true, means connection is available. Return true
		return true;
	}
	
	//Asynchronous task to download the xml file from Internet and parse it after downloading
	private class DownloadXml extends AsyncTask<String, Void, List<Users>>{
		//create progress dialog object
		private ProgressDialog dialog;
		
		//Constructor
		public DownloadXml(){
			dialog = new ProgressDialog(MainActivity.this);
		}
		
		@Override
	    protected void onPreExecute() {
			//Set the message to show on progress dialog
	        dialog.setMessage("Loading list. Please Wait...");
	        //show the progress dialog
	        dialog.show();
	    }
		
		@Override
		protected List<Users> doInBackground(String... urls) {
			// TODO Auto-generated method stub
			try{
				//Call the loadXML method to load the file from network 
				//and parse it to return list of user objects
				return loadXmlFromNetwork(urls[0]);
			}catch(IOException e){
				e.printStackTrace();
				return null;
			}catch(XmlPullParserException e){
				e.printStackTrace();
				return null;
			}
			
		}
		
		//After getting the user list, populate it on the UI using ArrayAdapter
		@Override
	    protected void onPostExecute(List<Users> result) {
			//get the instance of adapter initialized with our user list in result variable
			ArrayAdapter<Users> adapter = new ArrayAdapter<Users>(getApplicationContext(),R.layout.list_item,result);
			//if progress dialog is showing, close it
			if (dialog.isShowing()){
				dialog.dismiss();
	        }
			//Populate the UI by setting the listView
			listview.setAdapter(adapter);
			
			//Assign user_list to result
			user_list=result;
			//Populate the map using user list just generated
			populateMap(result);
	    }

	}	//End of Download XML class
	
	//Function to parse the input stream and return the user list
	private List<Users> loadXmlFromNetwork(String urlString) throws XmlPullParserException,IOException{
		
		InputStream in=null;
		//Get the instance of the parser we defined in XMLPullParserHandler
		XMLPullParserHandler parser=new XMLPullParserHandler();
		
		//Declare the user list
		List<Users> users=null;
		
		try{
			//DownloadURL function takes URL as argument and returns the XML file in input stream form
			in=downloadURL(urlString);
			
			//call the parse function of parser class
			users=parser.parse(in);
			
		}finally{
			//Close the input stream
			if(in!=null){
				in.close();
			}
			
		}
		//return the list of users
		return users;
	}
	
	private InputStream downloadURL(String urlString) throws IOException{
		//Get the url object from the url string
		URL url=new URL(urlString);
		//Open the Http connection
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setReadTimeout(10000 /* milliseconds */);
	    conn.setConnectTimeout(15000 /* milliseconds */);
	    conn.setRequestMethod("GET");
	    conn.setDoInput(true);
	    // Starts the query
	    conn.connect();
	    //return the file in the form of input stream
	    return conn.getInputStream();
	}
	
	private void populateMap(List<Users> result){
		//Get the size of the list
		int size=result.size();
		int i=0;
		
		//Reference to one of the marker on map
		LatLng start_pos=new LatLng(0, 0);
		
		//Start placing the markers on map
		while(i<size){
			Users user=result.get(i);
			double lat=Double.parseDouble(user.getLatitude());
			double lon=Double.parseDouble(user.getLongitude());
			LatLng pos = new LatLng(lat, lon);
			if(i==0){
				start_pos=pos;
			}
			map_object.addMarker(new MarkerOptions().position(pos).title(user.getName()));
			i++;
		}
		map_object.moveCamera(CameraUpdateFactory.newLatLngZoom(start_pos, 15));

	    // Zoom in, animating the camera.
	    map_object.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Boolean connect=checkConnection(this);
		if(!connect){
			//Show the error Page
			//Hide switch ans switch status
			mySwitch.setVisibility(View.GONE);
			switchStatus.setVisibility(View.GONE);
			
			//Hide the list layout
			relative_layout_1.setVisibility(View.GONE);
			
			//Make Click-able Error page visible
			relative_layout_3.setVisibility(View.VISIBLE);
			
			//Set up the onclick listener
			relative_layout_3.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//check for the connectivity
					Boolean connect2=checkConnection(MainActivity.this);
					if(connect2){
						//Network is now available. restart the activity
						Intent intent = new Intent(MainActivity.this, MainActivity.class);
						finish();
						startActivity(intent);     
					}
				}
			});
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Users.resetGlobalTotal();
		this.finish();
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Users.resetGlobalTotal();
		this.finish();
		super.onBackPressed();
	}
	
}
