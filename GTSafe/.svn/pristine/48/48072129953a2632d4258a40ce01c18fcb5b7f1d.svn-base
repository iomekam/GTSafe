package com.example.gtsafe.library.listeners;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.os.AsyncTask;

import com.example.gtsafe.library.DBManager;
import com.example.gtsafe.library.listeners.interfaces.OnDBUpdateListener;
import com.example.gtsafe.library.listeners.interfaces.OnGetJSONListener;

public class UpdateZoneListener implements OnGetJSONListener
{
	OnDBUpdateListener listener;
	public UpdateZoneListener(OnDBUpdateListener listener)
	{
		this.listener = listener;
	}
	@Override
	public void OnGetJSON(JSONObject jObj) throws JSONException {
		UpdateAllZoneTask task = new UpdateAllZoneTask();
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jObj);
	}
	
	private class UpdateAllZoneTask extends AsyncTask<JSONObject, Void, Void>
	{
		@Override
		protected Void doInBackground(JSONObject... params)
		{
			JSONObject jObj = params[0];
			
			try 
			{
				ContentValues val = new ContentValues();
				
				int zoneID = jObj.getInt("zone_id");
				String points = jObj.getString("points");
				
				val.put("zone_id", zoneID);
				val.put("points", points);
				DBManager.getInstance().insert("zones", null, val);
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
