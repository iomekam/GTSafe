package com.example.gtsafe.library.listeners;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.os.AsyncTask;

import com.example.gtsafe.library.DBManager;
import com.example.gtsafe.library.listeners.interfaces.OnDBUpdateListener;
import com.example.gtsafe.library.listeners.interfaces.OnGetJSONListener;
import com.example.gtsafe.model.ZoneData;
import com.google.android.gms.maps.model.LatLng;

public class UpdateZoneListener implements OnGetJSONListener
{
	List<OnDBUpdateListener<ZoneData>> listener;
	public UpdateZoneListener(List<OnDBUpdateListener<ZoneData>> zoneListener)
	{
		this.listener = zoneListener;
	}
	@Override
	public void OnGetJSON(JSONObject jObj) throws JSONException {
		UpdateAllZoneTask task = new UpdateAllZoneTask();
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jObj);
	}
	
	private class UpdateAllZoneTask extends AsyncTask<JSONObject, Void, ZoneData>
	{
		@Override
		protected ZoneData doInBackground(JSONObject... params)
		{
			JSONObject jObj = params[0];
			ZoneData zone = null;
			LinkedList<LatLng> list = new LinkedList<LatLng>();
			
			try 
			{
				ContentValues val = new ContentValues();
				
				int zoneID = jObj.getInt("zone_id");
				String points = jObj.getString("points");
				
				JSONArray jArray = new JSONObject(points).getJSONArray("points");
				
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject obj = jArray.getJSONObject(i);
					double lat = obj.getDouble("lat");
					double lon = obj.getDouble("lon");

					list.add(new LatLng(lat, lon));
				}
				
				zone = new ZoneData(list, zoneID, null);
				
				val.put("zone_id", zoneID);
				val.put("points", points);
				DBManager.getInstance().insert("zones", null, val);
			}
			catch(JSONException e)
			{
				e.printStackTrace();
			}
			
			return zone;
		}
		
		public void onPostExecute(ZoneData result)
		{
			if(listener != null)
			{
				for(OnDBUpdateListener<ZoneData> lis: listener)
				{
					lis.OnUpdate(result);
				}
			}
		}
	}
}
