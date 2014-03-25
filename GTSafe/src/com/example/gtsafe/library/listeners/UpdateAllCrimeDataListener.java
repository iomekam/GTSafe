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
import com.example.gtsafe.library.DBManager.TableEntry;
import com.example.gtsafe.library.listeners.interfaces.OnDBUpdateListener;
import com.example.gtsafe.library.listeners.interfaces.OnGetJSONListener;
import com.example.gtsafe.model.CrimeData;
import com.example.gtsafe.model.OffenseType;
import com.example.gtsafe.model.ZoneData;
import com.google.android.gms.maps.model.LatLng;

public class UpdateAllCrimeDataListener implements OnGetJSONListener 
{
	private List<OnDBUpdateListener<List<CrimeData>>> listener;
	
	public UpdateAllCrimeDataListener(List<OnDBUpdateListener<List<CrimeData>>> allCrimeDataListener)
	{
		this.listener = allCrimeDataListener;
	}

	@Override
	public void OnGetJSON(JSONObject object) throws JSONException 
	{
		UpdateAllCrimeTask task = new UpdateAllCrimeTask();
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, object);	
	}
	
	private class UpdateAllCrimeTask extends AsyncTask<JSONObject, Void, List<CrimeData>>
	{
		@Override
		protected List<CrimeData> doInBackground(JSONObject... params)
		{
			List<CrimeData> list = new LinkedList<CrimeData>();
			
			try
			{
				JSONObject obj = params[0];
				
				ContentValues val = new ContentValues();
				JSONArray jArray = obj.getJSONArray("crimes");
	
				for(int i = 0; i < jArray.length(); i++)
				{
					JSONObject object = jArray.getJSONObject(i);
					int crimeID = object.getInt("crime_id");
					int zoneID = object.getInt("zone_id");
					String date = object.getString("crime_date");
					
					String location = object.getString("location");
					String offense = object.getString("offense");
					String offenseDesc = object.getString("off_desc");
					double lat = object.getDouble("latitude");
					double lon = object.getDouble("longitude");
					
					DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
							Locale.US);
					Date d = null;
					try {
						d = format.parse(date);
						list.add(new CrimeData(new LatLng(lat,lon), location, d, OffenseType.getOffenseType(offense), 
									offenseDesc, DBManager.getInstance().getZone(zoneID)));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					val.put("crime_id", crimeID);
					val.put("offense", offense);
					val.put("offense_desc", offenseDesc);
					val.put("location", location);
					val.put("zone_id", zoneID);
					val.put("latitude", lat);
					val.put("longitude", lon);
					val.put("crime_date", date);
					
					DBManager.getInstance().insert("crime_data", null, val);
				}
			}
			catch(JSONException e){}
			
			return list;
		}
		
		public void onPostExecute(List<CrimeData> result)
		{	
			/*DBManager.getInstance().updateTable(TableEntry.CRIMES_ALL);
			DBManager.getInstance().updateTable(TableEntry.CRIMES_ZONE);
			
			DBManager.getInstance().initCount++;
			DBManager.getInstance().getAllCrimeData(null);
			
			for(ZoneData zone: DBManager.getInstance().getAllZones())
			{
				DBManager.getInstance().initCount++;
				DBManager.getInstance().getCrimesByZone(zone.getZoneID(), null);
			}*/
			
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
				for(OnDBUpdateListener<List<CrimeData>> lis: listener)
				{
					lis.OnUpdate(result);
				}
			}
			else
			{
				Log.e("Crime Data:","Done");
			}
		}
	}
}
