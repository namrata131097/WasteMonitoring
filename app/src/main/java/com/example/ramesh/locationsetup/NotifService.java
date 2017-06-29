package com.example.ramesh.locationsetup;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

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

/**
 * Created by RAMESH on 6/27/2017.
 */
public class NotifService extends BroadcastReceiver
{
    private Context c;
    static int reading=0;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        c=context;
        //reading=0;
        Toast.makeText(context, "I'm running" + reading, Toast.LENGTH_SHORT).show();
        final String serverURL = "https://api.thingspeak.com/channels/272109/feeds.json?api_key=70V19GOXF7NLTSWW&results=2";
        new LongOperation().execute(serverURL);
        NotificationCompat.Builder mbuilder;// = new NotificationCompat.Builder(context);
        // mbuilder.setSmallIcon(R.drawable.image);
        // mbuilder.setContentTitle("Notifications Example");
        // mbuilder.setContentText("This is a test notification");

        Intent notificationIntent = new Intent(context, MapsActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //mbuilder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager;// = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(reading==1) {
            mbuilder=new NotificationCompat.Builder(context);
            mbuilder.setSmallIcon(R.mipmap.ic_launcher);
            mbuilder.setContentTitle("Notifications Example");
            mbuilder.setContentText("This is a test notification");
            mbuilder.setContentIntent(contentIntent);
            manager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(0, mbuilder.build());
        }

    }

    private class LongOperation extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        private String Error = null;
        // private ProgressDialog Dialog = new ProgressDialog(AsyncronoustaskAndroidExample.this);

        //TextView uiUpdate = (TextView) findViewById(R.id.output);
        //TextView ui2 = (TextView) findViewById(R.id.tv2);

        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            //uiUpdate.setText("Output : ");
            //Dialog.setMessage("Downloading source..");
            Toast.makeText(c, "Downloading source..", Toast.LENGTH_SHORT).show();
            //Dialog.show();
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
            int i;
            try {
                obj = new JSONObject(Content);

                String str, str2 = "null";
                if (Error != null) {

                    //uiUpdate.setText("Output : " + Error);

                } else {
                    str = obj.getJSONObject("channel").getString("updated_at");
                    JSONArray feeds = obj.getJSONArray("feeds");


                    JSONObject obj2 = feeds.getJSONObject(feeds.length()-1);

                    str2 = obj2.getString("field1");
                    reading =Integer.parseInt(str2);
                    Toast.makeText(c, ""+reading, Toast.LENGTH_SHORT).show();

                    //uiUpdate.setText("Output : " + Content);
                    // ui2.setText(str2);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}

