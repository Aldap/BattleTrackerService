package ru.umicron.foregroundservice;

import java.text.DateFormat;
import java.util.Date;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class ForegroundService extends Activity implements LocationListener {
	private TextView txResult;
	private TextView txStatus;
	private Button btn;
	private EditText txEdit;
	private LocationManager myManager;	
	private Location mLastLocation = null;
	private boolean isGPSFix = false;
	private static long mLastLocationMillis;
	private MyGPSListener Listener;
    private Resources res;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
        myManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Listener = new MyGPSListener();
        myManager.addGpsStatusListener(Listener);
        txResult = (TextView) findViewById(R.id.result);
        txStatus = (TextView) findViewById(R.id.status);
        btn = (Button) findViewById(R.id.btn);
        txEdit = (EditText) findViewById(R.id.loginEdit);
        res = (Resources) getResources();
	    Intent i=new Intent(this, FService.class);
	    startService(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onBtnExit(View view){
    	    stopService(new Intent(this, FService.class));
            finish();
            System.exit(0);
	    }
	
	public void btnClick(View view){
	     if (btn.getText() == res.getString(R.string.start)){
	    	 if (txEdit.getText().toString().length() == 5) {
		 myManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
		 btn.setText(res.getString(R.string.stop));
	 		}
	 		else {
	 			txStatus.setText("логин не подходит");
	 			}
	     }
	     else{
	    	 myManager.removeUpdates(this);
	    	 btn.setText(res.getString(R.string.start));
	     }
	    }
	class BattleTrackerAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... url) {
        	try{
         		HttpClient httpclient = new DefaultHttpClient();
        		HttpGet request = new HttpGet(url[0]);
        		httpclient.execute(request);
        		}catch(Exception e){
        		 
        		Log.e("log_tag", "Error in http connection "+e.toString());
        		 
        		}

        	return null;
        }
        
        @Override
        protected void onProgressUpdate(Void... values) {

        }
        
        @Override
        protected void onPostExecute(Void unused) {

        }        
        
    }	
	
    public void onLocationChanged(Location location) {
  	   if (location == null) return;
  	//  if (isGPSFix) {
   	    mLastLocationMillis = SystemClock.elapsedRealtime(); 
 		// TODO Auto-generated method stub
 		 Date date = new Date(location.getTime()); 
 			 txResult.setText("Широта: "	+ String.format("%.5f", location.getLongitude()).replace(",",".") + "\nДолгота: " + String.format("%.5f", location.getLatitude()).replace(",",".") + 
 					   "\nВремя: " + DateFormat.getDateTimeInstance().format(date) + 
 					   "\nТочность" + location.getAccuracy() + "\n");
 			    mLastLocation = location;
 	    	//  }
 			new BattleTrackerAsyncTask().execute("http://62.182.29.199/tracker.php?key=" + txEdit.getText().toString() + "&x=" + String.format("%.5f", location.getLongitude()).replace(",",".")  + "&y=" + String.format("%.5f", location.getLatitude()).replace(",","."));
 			//new BattleTrackerAsyncTask().execute("http://gps.umicron.ru/index.php?lat=" + location.getLatitude() + "&lng=" + location.getLongitude() + "&userid=" + txEdit.getText().toString());
 		
      }
 	public void onProviderDisabled(String provider) {}

 	public void onProviderEnabled(String provider) {}

 	public void onStatusChanged(String provider, int status, Bundle extras) {
 	}	
	
    private class MyGPSListener implements GpsStatus.Listener { 
        public void onGpsStatusChanged(int event) { 
            switch (event) { 
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS: 
                    if (mLastLocation != null) 
                        isGPSFix = (SystemClock.elapsedRealtime() - mLastLocationMillis) < 20000; 
     
                    if (isGPSFix) { // A fix has been acquired. 
                    	txStatus.setText("Координаты получены"); 
                    } else { // The fix has been lost. 
                    	txStatus.setText("Поиск спутников");
                    } 
     
                    break; 
                case GpsStatus.GPS_EVENT_FIRST_FIX: 
                	txStatus.setText("Спутники найдены"); 
                    isGPSFix = true; 
     
                    break; 
            } 
        } 
    }

}
