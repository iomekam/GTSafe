package com.example.gtsafe.library.listeners;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.os.AsyncTask;

import com.example.gtsafe.library.DBManager;
import com.example.gtsafe.library.listeners.interfaces.OnDBUpdateListener;
import com.example.gtsafe.library.listeners.interfaces.OnGetJSONListener;

public class UpdateCrimeDataListener implements OnGetJSONListener
{
	private OnDBUpdateListener listener;
	
	public UpdateCrimeDataListener(OnDBUpdateListener listener)
	{
		this.listener = listener;
	}

	@Override
	public void OnGetJSON(JSONObject object) throws JSONException {
		
		UpdateCrimeTask task = new UpdateCrimeTask();
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, object);	
	}
	
	private class UpdateCrimeTask extends AsyncTask<JSONObject, Void, Void>
	{
		@Override
		protected Void doInBackground(JSONObject... params)
		{
			try
			{
				JSONObject object = params[0];
				ContentValues val = new ContentValues();
				
				int crimeID = object.getInt("crime_id");
				int zoneID = object.getInt("zone_id");
				String date = object.getString("crime_date");
				
				String location = object.getString("location");
				String locationCode = object.getString("location_code");
				String offense = object.getString("offense");
				double lat = object.getDouble("latitude");
				double lon = object.getDouble("longitude");
				
				val.put("crime_id", crimeID);
				val.put("offense", offense);
				val.put("location_code", locationCode);
				val.put("location", location);
				val.put("zone_id", zoneID);
				val.put("latitude", lat);
				val.put("longitude", lon);
				val.put("crime_date", date);
				
				DBManager.getInstance().insert("crime_data", null, val);
			}
			catch(JSONException e){}
			
			return null;
		}
		
		public void onPostExecute(Void result)
		{
			if(listener != null)
			{
				listener.OnUpdate();
			}
		}
	}
}
