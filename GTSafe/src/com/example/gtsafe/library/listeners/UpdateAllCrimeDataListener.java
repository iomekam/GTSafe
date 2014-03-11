package com.example.gtsafe.library.listeners;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

import com.example.gtsafe.library.DBManager;
import com.example.gtsafe.library.listeners.interfaces.OnDBUpdateListener;
import com.example.gtsafe.library.listeners.interfaces.OnGetJSONListener;

public class UpdateAllCrimeDataListener implements OnGetJSONListener 
{
	private OnDBUpdateListener listener;
	
	public UpdateAllCrimeDataListener(OnDBUpdateListener listener)
	{
		this.listener = listener;
	}

	@Override
	public void OnGetJSON(JSONObject object) throws JSONException 
	{
		UpdateAllCrimeTask task = new UpdateAllCrimeTask();
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, object);	
	}
	
	private class UpdateAllCrimeTask extends AsyncTask<JSONObject, Void, Void>
	{
		@Override
		protected Void doInBackground(JSONObject... params)
		{
			try
			{
				JSONObject obj = params[0];
				
				ContentValues val = new ContentValues();
				JSONArray jArray = obj.getJSONArray("crimes");
	
				for(int i = 0; i < jArray.length(); i++)
				{
					JSONObject object = jArray.getJSONObject(i);
					int crimeID = object.getInt("crime_id");
					int zoneID = object.getInt("zone_id");
					String date = object.getString("crime_date");
					
					String location = object.getString("location");
					String offense = object.getString("offense");
					double lat = object.getDouble("latitude");
					double lon = object.getDouble("longitude");
					
					val.put("crime_id", crimeID);
					val.put("offense", offense);
					val.put("location", location);
					val.put("zone_id", zoneID);
					val.put("latitude", lat);
					val.put("longitude", lon);
					val.put("crime_date", date);
					
					DBManager.getInstance().insert("crime_data", null, val);
				}
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
			else
			{
				Log.e("Crime Data:","Done");
			}
		}
	}
}
