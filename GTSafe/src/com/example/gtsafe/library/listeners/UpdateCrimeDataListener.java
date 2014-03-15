package com.example.gtsafe.library.listeners;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.os.AsyncTask;

import com.example.gtsafe.library.DBManager;
import com.example.gtsafe.library.listeners.interfaces.OnDBUpdateListener;
import com.example.gtsafe.library.listeners.interfaces.OnGetJSONListener;
import com.example.gtsafe.model.CrimeData;
import com.example.gtsafe.model.OffenseType;
import com.example.gtsafe.model.ZoneData;
import com.google.android.gms.maps.model.LatLng;

public class UpdateCrimeDataListener implements OnGetJSONListener
{
	private List<OnDBUpdateListener<CrimeData>> listener;
	
	public UpdateCrimeDataListener(List<OnDBUpdateListener<CrimeData>> crimeDataListener)
	{
		this.listener = crimeDataListener;
	}

	@Override
	public void OnGetJSON(JSONObject object) throws JSONException {
		
		UpdateCrimeTask task = new UpdateCrimeTask();
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, object);	
	}
	
	private class UpdateCrimeTask extends AsyncTask<JSONObject, Void, CrimeData>
	{
		@Override
		protected CrimeData doInBackground(JSONObject... params)
		{
			CrimeData crime = null;
			try
			{
				JSONObject object = params[0];
				ContentValues val = new ContentValues();
				
				int crimeID = object.getInt("crime_id");
				int zoneID = object.getInt("zone_id");
				String date = object.getString("crime_date");
				
				String location = object.getString("location");
				String offense = object.getString("offense");
				String offenseDesc = object.getString("off_desc");
				double lat = object.getDouble("latitude");
				double lon = object.getDouble("longitude");
				
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
						Locale.US);
				Date d = null;
				try {
					d = format.parse(date);
					crime = new CrimeData(new LatLng(lat,lon), location, d, OffenseType.ALL_OTHER_OFFENSES.getOffenseType(offense), 
								offenseDesc, new ZoneData(null, zoneID, null));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				val.put("crime_id", crimeID);
				val.put("offense", offense);
				val.put("offense_desc", offenseDesc);
				val.put("location", location);
				val.put("zone_id", zoneID);
				val.put("latitude", lat);
				val.put("longitude", lon);
				val.put("crime_date", date);
				
				DBManager.getInstance().insert("crime_data", null, val);
			}
			catch(JSONException e){}
			
			return crime;
		}
		
		public void onPostExecute(CrimeData result)
		{
			if(listener != null)
			{
				for(OnDBUpdateListener<CrimeData> lis: listener)
				{
					lis.OnUpdate(result);
				}
			}
		}
	}
}
