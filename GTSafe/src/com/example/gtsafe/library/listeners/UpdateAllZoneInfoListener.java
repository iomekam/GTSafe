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

public class UpdateAllZoneInfoListener implements OnGetJSONListener {

	private OnDBUpdateListener listener;
	
	public UpdateAllZoneInfoListener(OnDBUpdateListener listener)
	{
		this.listener = listener;
	}

	@Override
	public void OnGetJSON(JSONObject object) throws JSONException {
		UpdateAllZoneInformation task = new UpdateAllZoneInformation();
		task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, object); //Using Serial to ensure that zone updates first since this is dependent on it.
	}

	private class UpdateAllZoneInformation extends
			AsyncTask<JSONObject, Void, Void> {
		@Override
		protected Void doInBackground(JSONObject... params) {
			JSONObject object = params[0];

			ContentValues val = new ContentValues();
			JSONArray jArray = null;

			try {
				jArray = object.getJSONArray("zone_info");
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject jObj = jArray.getJSONObject(i);
					int zoneID = jObj.getInt("zone_id");
					int zoneInfoID = jObj.getInt("zone_info_id");
					String description = jObj.getString("zone_description");

					val.put("zone_info_id", zoneInfoID);
					val.put("zone_id", zoneID);
					val.put("zone_description", description);
					DBManager.getInstance().insert("zone_information", null,
							val);
					val.clear();
				}
			}
			catch (JSONException e) {
			
			}

			return null;
		}

		public void onPostExecute(Void result) {
			if (listener != null) {
				listener.OnUpdate();
			} else {
				Log.e("ZInfo:", "Done");
			}
		}
	}
}
