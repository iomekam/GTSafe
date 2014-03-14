package com.example.gtsafe.library.listeners;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.os.AsyncTask;

import com.example.gtsafe.library.DBManager;
import com.example.gtsafe.library.listeners.interfaces.OnDBUpdateListener;
import com.example.gtsafe.library.listeners.interfaces.OnGetJSONListener;
import com.example.gtsafe.model.CleryActModel;

public class UpdateCleryActListener implements OnGetJSONListener 
{
	private OnDBUpdateListener<CleryActModel> listener;
	
	public UpdateCleryActListener(OnDBUpdateListener<CleryActModel> listener)
	{
		this.listener = listener;
	}

	@Override
	public void OnGetJSON(JSONObject object) throws JSONException 
	{
		UpdateCleryActTask task = new UpdateCleryActTask();
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, object);
	}
	
	private class UpdateCleryActTask extends AsyncTask<JSONObject, Void, CleryActModel>
	{
		@Override
		protected CleryActModel doInBackground(JSONObject... params)
		{
			JSONObject jObj = params[0];
			
			CleryActModel clery = null;
			
			try 
			{
				ContentValues val = new ContentValues();
				
				int caID = jObj.getInt("ca_id");
				String date = jObj.getString("date");
				String title = jObj.getString("title");
				String text = jObj.getString("text");
				
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
						Locale.US);
				Date d = null;
				try {
					d = format.parse(date);
					clery = new CleryActModel(title, d, text);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				val.put("ca_id", caID);
				val.put("ca_date", date);
				val.put("ca_title", title);
				val.put("ca_text", text);
				
				DBManager.getInstance().insert("clery_acts", null, val);
			}
			catch(JSONException e){}
			
			return clery;
		}
		
		public void onPostExecute(CleryActModel result)
		{
			if(listener != null)
			{
				listener.OnUpdate(result);
			}
		}
	}
}