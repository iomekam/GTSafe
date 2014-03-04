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

public class UpdateAllCleryActListener implements OnGetJSONListener 
{
	private OnDBUpdateListener listener;
	
	public UpdateAllCleryActListener(OnDBUpdateListener listener)
	{
		this.listener = listener;
	}

	@Override
	public void OnGetJSON(JSONObject object) throws JSONException 
	{
		UpdateAllCleryActTask task = new UpdateAllCleryActTask();
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, object);
	}
	
	private class UpdateAllCleryActTask extends AsyncTask<JSONObject, Void, Void>
	{
		@Override
		protected Void doInBackground(JSONObject... params)
		{
			JSONObject object = params[0];
			
			ContentValues val = new ContentValues();
			JSONArray jArray = null;
			
			try 
			{
				jArray = object.getJSONArray("clery_acts");
				
				for(int i = 0; i < jArray.length(); i++)
				{
					JSONObject jObj = jArray.getJSONObject(i);
				
					int caID = jObj.getInt("ca_id");
					String date = jObj.getString("date");
					String title = jObj.getString("title");
					String text = jObj.getString("text");
					
					val.put("ca_id", caID);
					val.put("ca_date", date);
					val.put("ca_title", title);
					val.put("ca_text", text);
				
					DBManager.getInstance().insert("clery_acts", null, val);
					val.clear();
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
				Log.e("Clery Acts:", "Done");
			}
		}
	}
}
	
