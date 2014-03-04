package com.example.gtsafe.library.listeners;


import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.os.AsyncTask;

import com.example.gtsafe.library.DBManager;
import com.example.gtsafe.library.listeners.interfaces.OnDBUpdateListener;
import com.example.gtsafe.library.listeners.interfaces.OnGetJSONListener;

public class UpdateZoneInfoListener implements OnGetJSONListener {

	private OnDBUpdateListener listener;
	
	public UpdateZoneInfoListener(OnDBUpdateListener listener)
	{
		this.listener = listener;
	}

	@Override
	public void OnGetJSON(JSONObject object) throws JSONException {
		UpdateZoneInformation task = new UpdateZoneInformation();
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, object);
	}

	private class UpdateZoneInformation extends
			AsyncTask<JSONObject, Void, Void> {
		@Override
		protected Void doInBackground(JSONObject... params) {
			JSONObject jObj = params[0];

			try {
				ContentValues val = new ContentValues();
				int zoneID = jObj.getInt("zone_id");
				int zoneInfoID= jObj.getInt("zone_info_id");
				String description = jObj.getString("zone_description");

				val.put("zone_info_id", zoneInfoID);
				val.put("zone_id", zoneID);
				val.put("zone_description", description);
				DBManager.getInstance().insert("zone_information", null, val);
				

			} 
			catch (JSONException e) {}

			return null;

		}
	}
	public void onPostExecute(Void result)
	{
		if(listener != null)
		{
			listener.OnUpdate();
		}
	}
}
