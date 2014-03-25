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
import com.example.gtsafe.model.ZoneInfo;

public class UpdateAllZoneInfoListener implements OnGetJSONListener {

	private List<OnDBUpdateListener<List<ZoneInfo>>> listener;
	
	public UpdateAllZoneInfoListener(List<OnDBUpdateListener<List<ZoneInfo>>> allZoneInfoListener)
	{
		this.listener = allZoneInfoListener;
	}

	@Override
	public void OnGetJSON(JSONObject object) throws JSONException {
		UpdateAllZoneInformation task = new UpdateAllZoneInformation();
		task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, object); //Using Serial to ensure that zone updates first since this is dependent on it.
	}

	private class UpdateAllZoneInformation extends
			AsyncTask<JSONObject, Void, List<ZoneInfo>> {
		@Override
		protected List<ZoneInfo> doInBackground(JSONObject... params) {
			JSONObject object = params[0];

			ContentValues val = new ContentValues();
			JSONArray jArray = null;
			
			List<ZoneInfo> list = new LinkedList<ZoneInfo>();

			try {
				jArray = object.getJSONArray("zone_info");
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject jObj = jArray.getJSONObject(i);
					int zoneID = jObj.getInt("zone_id");
					int zoneInfoID = jObj.getInt("zone_info_id");
					String description = jObj.getString("zone_description");
					
					DBManager.getInstance().getZone(zoneID).getZoneInformation().getDescription().add(description);
					
					list.add(DBManager.getInstance().getZone(zoneID).getZoneInformation());

					val.put("zone_info_id", zoneInfoID);
					val.put("zone_id", zoneID);
					val.put("zone_description", description);
					DBManager.getInstance().insert("zone_information", null, val);
					val.clear();
				}
			}
			catch (JSONException e) {}

			return list;
		}

		public void onPostExecute(List<ZoneInfo> result) {
			if(DBManager.getInstance().runningInit)
			{
				DBManager.getInstance().initCount--;
				
				if(DBManager.getInstance().initCount == 0 && DBManager.getInstance().initDialog != null)
				{
					DBManager.getInstance().initDialog.dismiss();
					DBManager.getInstance().runningInit = false;
				}
			}
			if (listener != null) {
				for(OnDBUpdateListener<List<ZoneInfo>> lis: listener)
				{
					lis.OnUpdate(result);
				}
			} 
			else 
			{
				Log.e("ZInfo:", "Done");
			}
		}
	}
}
