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
import com.example.gtsafe.library.DBManager.TableEntry;
import com.example.gtsafe.library.listeners.interfaces.OnDBUpdateListener;
import com.example.gtsafe.library.listeners.interfaces.OnGetJSONListener;
import com.example.gtsafe.model.ZoneData;
import com.google.android.gms.maps.model.LatLng;

public class UpdateAllZoneListener implements OnGetJSONListener
{
	List<OnDBUpdateListener<List<ZoneData>>> listener;
	public UpdateAllZoneListener(List<OnDBUpdateListener<List<ZoneData>>> allZoneListener)
	{
		this.listener = allZoneListener;
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
			List<LatLng> latList = new LinkedList<LatLng>();
			
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
					
					JSONArray jArr = new JSONObject(points).getJSONArray("points");

					for (int j = 0; j < jArr.length(); j++) {
						JSONObject obj = jArr.getJSONObject(j);
						double lat = obj.getDouble("lat");
						double lon = obj.getDouble("lon");

						latList.add(new LatLng(lat, lon));
					}
					
					list.add(new ZoneData(latList, zoneID, null));
					
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
			DBManager.getInstance().initCount++;
			DBManager.getInstance().updateAllZoneInfo();
			
			if(DBManager.getInstance().runningInit)
			{
				DBManager.getInstance().initCount--;
				
				if(DBManager.getInstance().initCount == 0 && DBManager.getInstance().initDialog != null)
				{
					DBManager.getInstance().initDialog.dismiss();
					DBManager.getInstance().runningInit = false;
				}
			}
			
			if(listener != null)
			{
				for(OnDBUpdateListener<List<ZoneData>> lis: listener)
				{
					lis.OnUpdate(result);
				}
			}
			else
			{
				Log.e("Zone:", "Done");
			}
		}
	}
}
