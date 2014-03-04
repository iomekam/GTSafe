package com.example.gtsafe.library.listeners;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.os.AsyncTask;

import com.example.gtsafe.library.DBManager;
import com.example.gtsafe.library.listeners.interfaces.OnDBUpdateListener;
import com.example.gtsafe.library.listeners.interfaces.OnGetJSONListener;

public class UpdateCleryActListener implements OnGetJSONListener 
{
	private OnDBUpdateListener listener;
	
	public UpdateCleryActListener(OnDBUpdateListener listener)
	{
		this.listener = listener;
	}

	@Override
	public void OnGetJSON(JSONObject object) throws JSONException 
	{
		UpdateCleryActTask task = new UpdateCleryActTask();
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, object);
	}
	
	private class UpdateCleryActTask extends AsyncTask<JSONObject, Void, Void>
	{
		@Override
		protected Void doInBackground(JSONObject... params)
		{
			JSONObject jObj = params[0];
			
			try 
			{
				ContentValues val = new ContentValues();
				
				int caID = jObj.getInt("ca_id");
				String date = jObj.getString("date");
				String title = jObj.getString("title");
				String text = jObj.getString("text");
				
				val.put("ca_id", caID);
				val.put("ca_date", date);
				val.put("ca_title", title);
				val.put("ca_text", text);
				
				DBManager.getInstance().insert("clery_acts", null, val);
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