package com.example.ramesh.locationsetup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MapsActivity extends FragmentActivity implements LocationListener {

    //private GoogleMap mMap;
    public GoogleMap googleMap;
    String lati="0",longi="0";
    android.os.Handler h = new android.os.Handler();
    final int delay=10000;
    Runnable run =new Runnable() {
        @Override
        public void run() {
            new LongOperation().execute("https://api.thingspeak.com/channels/272109/feeds.json?api_key=70V19GOXF7NLTSWW&results=2");
            h.postDelayed(this,delay);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        //      .findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);
        SupportMapFragment supportMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        googleMap = supportMapFragment.getMap();
        googleMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);
        //startService(new Intent(getBaseContext(), DownloadService.class));


        h.postDelayed(run, delay);
        Intent ai=new Intent(getBaseContext(),NotifService.class);
        PendingIntent pi=PendingIntent.getBroadcast(MapsActivity.this, 0, ai, 0);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 8000;

        manager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), interval, pi);
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onLocationChanged(Location location) {
        TextView locationTv = (TextView) findViewById(R.id.latlongLocation);
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        //googleMap.addMarker(new MarkerOptions().position(latLng));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        locationTv.setText("Latitude:" + latitude + ", Longitude:" + longitude);
    }

    @Override
    public void onBackPressed()
    {
        //stopService(new Intent(getBaseContext(),DownloadService.class));
        h.removeCallbacks(run);
        super.onBackPressed();
    }
    private class LongOperation extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        private String Error = null;
        //private ProgressDialog Dialog = new ProgressDialog(MapsActivity.this);

        //TextView uiUpdate = (TextView) findViewById(R.id.output);
        //TextView ui2 = (TextView) findViewById(R.id.tv2);

        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            //  uiUpdate.setText("Output : ");
            //Dialog.setMessage("Downloading source..");
            //Dialog.show();
            Toast.makeText(getApplicationContext(), "Downloading...", Toast.LENGTH_SHORT).show();

        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            try {

                // Call long running operations here (perform background computation)
                // NOTE: Don't call UI Element here.

                // Server url call by GET method
                HttpGet httpget = new HttpGet(urls[0]);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                Content = Client.execute(httpget, responseHandler);


            } catch (ClientProtocolException e) {
                Error = e.getMessage();
                cancel(true);
            } catch (IOException e) {
                Error = e.getMessage();
                cancel(true);
            }

            return null;
        }

        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.

            // Close progress dialog
            //Dialog.dismiss();
            JSONObject obj;

            try {
                obj = new JSONObject(Content);

                String str2;
                if (Error != null) {

                    //uiUpdate.setText("Output : " + Error);

                } else {
                    lati = obj.getJSONObject("channel").getString("latitude");
                    longi= obj.getJSONObject("channel").getString("longitude");
                    JSONArray feeds = obj.getJSONArray("feeds");


                    JSONObject obj2 = feeds.getJSONObject(feeds.length()-1);

                    str2 = obj2.getString("field1");
                    int a=Integer.parseInt(str2);

                    //uiUpdate.setText("Output : " + Content);
                    //ui2.setText(str2);
                    Toast.makeText(getApplicationContext(), ""+str2, Toast.LENGTH_SHORT).show();
                    double latid=Double.parseDouble(lati);
                    //double latid=31;
                    double longid=Double.parseDouble(longi);
                    //double longid=-54;
                    LatLng mapobj=new LatLng(latid,longid);
                    //Toast.makeText(getApplicationContext(), ""+lati, Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(), ""+longi, Toast.LENGTH_SHORT).show();
                    GoogleMap gm=googleMap;
                    //gm.addMarker(new MarkerOptions().position(mapobj));

                    if(a==0) {
                        //mark = gm.addMarker(new MarkerOptions().position(mapobj));
                        gm.clear();
                    }
                    else
                        gm.addMarker(new MarkerOptions().position(mapobj));
                /*
                    else {
                        mark.remove();
                        mark.setVisible(false);
                    }*/

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

}



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
   /* @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}*/
