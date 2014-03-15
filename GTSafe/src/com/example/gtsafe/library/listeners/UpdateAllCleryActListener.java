package com.example.gtsafe.library.listeners;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

import com.example.gtsafe.library.DBManager;
import com.example.gtsafe.library.listeners.interfaces.OnDBUpdateListener;
import com.example.gtsafe.library.listeners.interfaces.OnGetJSONListener;
import com.example.gtsafe.model.CleryActModel;

public class UpdateAllCleryActListener implements OnGetJSONListener 
{
	private List<OnDBUpdateListener<List<CleryActModel>>> listener;
	
	public UpdateAllCleryActListener(List<OnDBUpdateListener<List<CleryActModel>>> allCleryActListener)
	{
		this.listener = allCleryActListener;
	}

	@Override
	public void OnGetJSON(JSONObject object) throws JSONException 
	{
		UpdateAllCleryActTask task = new UpdateAllCleryActTask();
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, object);
	}
	
	private class UpdateAllCleryActTask extends AsyncTask<JSONObject, Void, List<CleryActModel>>
	{
		@Override
		protected List<CleryActModel> doInBackground(JSONObject... params)
		{
			JSONObject object = params[0];
			
			ContentValues val = new ContentValues();
			JSONArray jArray = null;
			
			List<CleryActModel> list = new LinkedList<CleryActModel>();
			
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
					
					DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
							Locale.US);
					Date d = null;
					try {
						d = format.parse(date);
						list.add(new CleryActModel(caID, title, d, text));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					val.put("ca_id", caID);
					val.put("ca_date", date);
					val.put("ca_title", title);
					val.put("ca_text", text);
				
					DBManager.getInstance().insert("clery_acts", null, val);
					val.clear();
				}
			}
			catch(JSONException e){}
			
			return list;
		}
		
		public void onPostExecute(List<CleryActModel> result)
		{
			if(listener != null)
			{
				for(OnDBUpdateListener<List<CleryActModel>> lis: listener)
				{
					lis.OnUpdate(result);
				}
			}
			else
			{
				Log.e("Clery Acts:", "Done");
			}
		}
	}
}
	
