package com.example.gtsafe.library.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.gtsafe.library.DBManager;

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        //GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        //String messageType = gcm.getMessageType(intent);
        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
        		DBManager db = DBManager.getInstance();
        		
                // This loop represents the service doing some work.
        		String tag = intent.getExtras().getString("tag");
        		int id = 0;
        		
        		if(tag.equals("update_zone"))
        		{
    				id = Integer.parseInt(intent.getExtras().getString("zone_id"));
    				db.updateZone(id);
        		}
        		else if(tag.equals("update_all_zone"))
        		{
    				db.updateAllZones();
        		}
        		else if(tag.equals("update_zone_info"))
        		{
    				id = Integer.parseInt(intent.getExtras().getString("zone_info_id"));
    				db.updateZoneInfo(id);
        		}
        		else if(tag.equals("update_all_zone_info"))
        		{
        			db.updateAllZoneInfo();
        		}
        		else if(tag.equals("update_crime_data"))
        		{
    				id = Integer.parseInt(intent.getExtras().getString("crime_id"));
    				db.updateCrimeData(id);
        		}
        		else if(tag.equals("update_all_crime_data"))
        		{
        			db.updateAllCrimes();
        		}
        		else if(tag.equals("update_all_clery_act"))
        		{
        			db.updateAllCleryAct();
        		}
        		else if(tag.equals("update_clery_act"))
        		{
    				id = Integer.parseInt(intent.getExtras().getString("ca_id"));
    				db.updateCleryAct(id);
        		}
        		
				Log.e("t", "" + tag);
				Log.e("t", "" + id);
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}