package com.example.gtsafe.library.listeners;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

import com.example.gtsafe.library.DBManager;
import com.example.gtsafe.library.listeners.interfaces.OnDBUpdateListener;
import com.example.gtsafe.library.listeners.interfaces.OnGetJSONListener;
import com.example.gtsafe.model.ZoneData;

public class UpdateAllZoneListener implements OnGetJSONListener
{
	OnDBUpdateListener<List<ZoneData>> listener;
	public UpdateAllZoneListener(OnDBUpdateListener<List<ZoneData>> listener)
	{
		this.listener = listener;
	}
	
	@Override
	public void OnGetJSON(JSONObject object) throws JSONException {
		UpdateAllZoneTask task = new UpdateAllZoneTask();
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, object);
	}
	
	private class UpdateAllZoneTask extends AsyncTask<JSONObject, Void, List<ZoneData>>
	{
		@Override
		protected List<ZoneData> doInBackground(JSONObject... params)
		{
			JSONObject object = params[0];
			
			ContentValues val = new ContentValues();
			JSONArray jArray = null;
			
			List<ZoneData> list = new LinkedList<ZoneData>();
			
			try 
			{
				jArray = object.getJSONArray("zones");
			
				for(int i = 0; i < jArray.length(); i++)
				{
					JSONObject jObj = jArray.getJSONObject(i);
					int zoneID = jObj.getInt("zone_id");
					String points = jObj.getString("points");
					
					list.add(new ZoneData(null, zoneID, null));
					
					val.put("zone_id", zoneID);
					val.put("points", points);
					DBManager.getInstance().insert("zones", null, val);
					
					val.clear();
				}
			}
			catch(JSONException e){}
			
			return list;
		}
		
		public void onPostExecute(List<ZoneData> result)
		{
			DBManager.getInstance().updateAllZoneInfo();
			if(listener != null)
			{
				listener.OnUpdate(result);
			}
			else
			{
				Log.e("Zone:", "Done");
			}
		}
	}
}
