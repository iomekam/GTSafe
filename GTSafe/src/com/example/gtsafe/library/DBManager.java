package com.example.gtsafe.library;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import com.example.gtsafe.library.listeners.UpdateAllCleryActListener;
import com.example.gtsafe.library.listeners.UpdateAllCrimeDataListener;
import com.example.gtsafe.library.listeners.UpdateAllZoneInfoListener;
import com.example.gtsafe.library.listeners.UpdateAllZoneListener;
import com.example.gtsafe.library.listeners.UpdateCleryActListener;
import com.example.gtsafe.library.listeners.UpdateCrimeDataListener;
import com.example.gtsafe.library.listeners.UpdateZoneInfoListener;
import com.example.gtsafe.library.listeners.UpdateZoneListener;
import com.example.gtsafe.library.listeners.interfaces.OnDBGetListener;
import com.example.gtsafe.library.listeners.interfaces.OnDBUpdateListener;
import com.example.gtsafe.model.CleryActModel;
import com.example.gtsafe.model.CrimeData;
import com.example.gtsafe.model.LocationCode;
import com.example.gtsafe.model.OffenseType;
import com.example.gtsafe.model.ZoneData;
import com.example.gtsafe.model.ZoneInfo;
import com.google.android.gms.maps.model.LatLng;

public class DBManager {
	private AtomicInteger mOpenCounter = new AtomicInteger();

	private static DBManager instance;
	private static SQLiteOpenHelper dbHelper;
	private SQLiteDatabase db;
	private DBRequester request;

	private OnDBUpdateListener zoneListener;
	private OnDBUpdateListener allZoneListener;
	private OnDBUpdateListener crimeDataListener;
	private OnDBUpdateListener allCrimeDataListener;
	private OnDBUpdateListener zoneInfoListener;
	private OnDBUpdateListener allZoneInfoListener;
	
	private boolean useDefaultDB;

	private OnDBUpdateListener cleryActListener;

	private OnDBUpdateListener allCleryActListener;

	public static synchronized void initializeInstance(SQLiteOpenHelper helper) {
		if (instance == null) {
			instance = new DBManager();
			dbHelper = helper;
			instance.db = dbHelper.getWritableDatabase();
		}
	}

	public static synchronized DBManager getInstance() {
		if (instance == null) {
			throw new IllegalStateException(
					DBManager.class.getSimpleName()
							+ " is not initialized, call initializeInstance(..) method first.");
		}

		return instance;
	}

	public SQLiteDatabase openDatabase() {
		if (!useDefaultDB) {
			if (mOpenCounter.incrementAndGet() == 1) {
				// Opening new database
				db = dbHelper.getWritableDatabase();
			}
		}

		return db;
	}

	public void closeDatabase() {
		
		if (mOpenCounter.decrementAndGet() == 0) {
			// Closing database
			db.close();
		}
	}

	public void initLocalDB(SQLiteDatabase db) {
		SQLiteDatabase temp = this.db;

		this.db = db;
		this.useDefaultDB = true;

		updateAllZones();
		updateAllCrimes();
		updateAllCleryAct();

		this.db = temp;
		this.useDefaultDB = false;
	}

	public long insert(String table, String nullColumnHack, ContentValues values) {
		long success = -1;
		SQLiteDatabase db = instance.openDatabase();

		try {
			db.insertWithOnConflict(table, nullColumnHack, values, SQLiteDatabase.CONFLICT_REPLACE);
		} catch (SQLException e) {
			Log.e("DB Insert Error", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}

		instance.closeDatabase();
		return success;
	}

	public void updateAllZones() {
		request = new DBRequester();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "get_zones"));

		instance.openDatabase().delete("zones", null, null);

		request.setOnJSONEventListener(new UpdateAllZoneListener(
				allZoneListener));
		request.getJSON(params);
	}

	public void setOnAllZoneUpdateEventListener(OnDBUpdateListener listener) {
		this.allZoneListener = listener;
	}

	public void updateZone(int zoneIDServer) {
		request = new DBRequester();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "get_individual_zone"));
		params.add(new BasicNameValuePair("zone_id", "" + zoneIDServer));

		request.setOnJSONEventListener(new UpdateZoneListener(zoneListener));
		request.getJSON(params);
	}

	public void setOnZoneUpdateEventListener(OnDBUpdateListener listener) {
		this.zoneListener = listener;
	}
	
	public void updateZoneInfo(int zoneInfoID)
	{
		request = new DBRequester();
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "get_individual_zone_info"));
		params.add(new BasicNameValuePair("zone_info_id", "" + zoneInfoID));

		request.setOnJSONEventListener(new UpdateZoneInfoListener(zoneInfoListener));
		request.getJSON(params);
	}
	
	public void setOnZoneInfoUpdateEventListener(OnDBUpdateListener listener) {
		this.zoneInfoListener = listener;
	}
	
	public void updateAllZoneInfo() {
		request = new DBRequester();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "get_zone_info"));

		request.setOnJSONEventListener(new UpdateAllZoneInfoListener(allZoneInfoListener));
		request.getJSON(params);
	}

	public void setOnAllZoneInfoUpdateEventListener(OnDBUpdateListener listener) {
		this.allZoneInfoListener = listener;
	}
	
	public void updateCleryAct(int caID)
	{
		request = new DBRequester();
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "get_individual_clery_act"));
		params.add(new BasicNameValuePair("ca_id", "" + caID));

		request.setOnJSONEventListener(new UpdateCleryActListener(cleryActListener));
		request.getJSON(params);
	}
	
	public void setOnCleryActUpdateListener(OnDBUpdateListener listener)
	{
		this.cleryActListener = listener;
	}
	
	public void updateAllCleryAct() {
		request = new DBRequester();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "get_clery_act"));

		request.setOnJSONEventListener(new UpdateAllCleryActListener(allCleryActListener));
		request.getJSON(params);
	}

	public void setOnAllCleryActUpdateEventListener(OnDBUpdateListener listener) {
		this.allCleryActListener = listener;
	}

	public void updateCrimeData(int crimeID) {
		request = new DBRequester();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "get_individual_crime_data"));
		params.add(new BasicNameValuePair("crime_data_id", "" + crimeID));

		request.setOnJSONEventListener(new UpdateCrimeDataListener(
				crimeDataListener));
		request.getJSON(params);
	}

	public void setOnCrimeUpdateEventListener(OnDBUpdateListener listener) {
		this.crimeDataListener = listener;
	}

	public void updateAllCrimes() {
		request = new DBRequester();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "get_crime_data"));

		instance.openDatabase().delete("crime_data", null, null);

		request.setOnJSONEventListener(new UpdateAllCrimeDataListener(
				allCrimeDataListener));
		request.getJSON(params);
	}

	public void setOnAllCrimeUpdateEventListener(OnDBUpdateListener listener) {
		this.allCrimeDataListener = listener;
	}

	public ZoneData getZone(int zoneID) {
		List<LatLng> list = new LinkedList<LatLng>();

		SQLiteDatabase db = instance.openDatabase();

		String selectQuery = "SELECT zone_description FROM zone_information WHERE zone_id="
				+ zoneID;
		ZoneInfo zInfo = null;
		List<String> description = new ArrayList<String>();
		Cursor c = db.rawQuery(selectQuery, null);
		
		boolean hasItem = c.moveToFirst();
		while (hasItem) {
			description.add(c.getString(c.getColumnIndex("zone_description")));
			hasItem = c.moveToNext();
		}
		
		c.close();

		zInfo = new ZoneInfo(description);

		selectQuery = "SELECT zone_id, points FROM zones WHERE zone_id = "
				+ zoneID;
		c = db.rawQuery(selectQuery, null);

		ZoneData data = null;

		hasItem = c.moveToFirst();
		if (hasItem) {
			try {
				JSONArray jArray = new JSONObject(c.getString(c
						.getColumnIndex("points"))).getJSONArray("points");

				for (int i = 0; i < jArray.length(); i++) {
					JSONObject jObj = jArray.getJSONObject(i);
					double lat = jObj.getDouble("lat");
					double lon = jObj.getDouble("lon");

					list.add(new LatLng(lat, lon));
				}

				data = new ZoneData(list, c.getInt(c
						.getColumnIndex("zone_id")), zInfo);
			} catch (JSONException e) {
				Log.e("GetZones JSON Error", e.getMessage());
			}
		}

		c.close();
		instance.closeDatabase();

		return data;
	}

	public List<ZoneData> getAllZones() {
		List<ZoneData> zoneList = new LinkedList<ZoneData>();

		SQLiteDatabase db = instance.openDatabase();
		String selectQuery = "SELECT zone_id FROM zones";

		Cursor c = db.rawQuery(selectQuery, null);

		boolean hasItem = c.moveToFirst();
		
		while (hasItem) 
		{			
			zoneList.add(getZone(c.getInt(c
					.getColumnIndex("zone_id"))));
			hasItem = c.moveToNext();
		}

		c.close();
		instance.closeDatabase();

		return zoneList;
	}

	public CrimeData getCrimeData(int crimeID) {
		CrimeData data = null;

		SQLiteDatabase db = instance.openDatabase();
		String selectQuery = "SELECT crime_id, offense, location_code, location, zone_id,"
				+ "latitude, longitude, crime_date FROM crime_data WHERE crime_id= "
				+ crimeID;
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			try {
				LatLng location = new LatLng(c.getDouble(c
						.getColumnIndex("latitude")), c.getDouble(c
						.getColumnIndex("longitude")));
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
						Locale.US);
				Date date = format.parse(c.getString(c
						.getColumnIndex("crime_date")));

				data = new CrimeData(location, c.getString(c
						.getColumnIndex("location")), date, null, null,
						getZone(c.getInt(c.getColumnIndex("zone_id"))));
			} catch (ParseException e) {
				Log.e("Parse Exception", e.getMessage());
			}
		}

		c.close();
		instance.closeDatabase();

		return data;
	}

	public void getAllCrimeData(final OnDBGetListener<CrimeData> listener) {
		new AsyncTask<Void, Void, List<CrimeData>>() {
			@Override
			protected List<CrimeData> doInBackground(Void... params) {
				List<CrimeData> crimeList = new LinkedList<CrimeData>();

				SQLiteDatabase db = instance.openDatabase();
				String selectQuery = "SELECT crime_id FROM crime_data";
				Cursor c = db.rawQuery(selectQuery, null);

				boolean hasNext = c.moveToFirst();
				while (hasNext) 
				{
					crimeList.add(getCrimeData(c.getInt(c.getColumnIndex("crime_id"))));
					hasNext = c.moveToNext();
				}

				c.close();
				instance.closeDatabase();

				return crimeList;
			}

			public void onPostExecute(List<CrimeData> result) {
				listener.OnGet(result);
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	public CleryActModel getCleryAct(int caID)
	{
		CleryActModel data = null;

		SQLiteDatabase db = instance.openDatabase();
		String selectQuery = "SELECT ca_date, ca_title, ca_text FROM clery_acts WHERE ca_id= "
				+ caID;
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) 
		{
			try {
				String title = c.getString(c
						.getColumnIndex("ca_title"));
				
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
						Locale.US);
				Date date = format.parse(c.getString(c
						.getColumnIndex("ca_date")));
				
				String text = c.getString(c
						.getColumnIndex("ca_text"));

				data = new CleryActModel(title, date, text);
			} 
			catch (ParseException e) 
			{
				Log.e("Parse Exception", e.getMessage());
			}
		}
		
		return data;
	}

	public void getCrimesByZone(final int zoneID,
			final OnDBGetListener<CrimeData> listener) {
		new AsyncTask<Void, Void, List<CrimeData>>() {
			@Override
			protected List<CrimeData> doInBackground(Void... arg0) {
				List<CrimeData> dataList = new LinkedList<CrimeData>();

				SQLiteDatabase db = instance.openDatabase();
				String selectQuery = "SELECT crime_id FROM crime_data WHERE zone_id = " + zoneID;

				Cursor c = db.rawQuery(selectQuery, null);
				boolean hasNext = c.moveToFirst();

				while (hasNext) {
					dataList.add(getCrimeData(c.getInt(c
							.getColumnIndex("crime_id"))));
					hasNext = c.moveToNext();
				}

				c.close();
				instance.closeDatabase();

				return dataList;
			}

			public void onPostExecute(List<CrimeData> result) {
				listener.OnGet(result);
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	public void getCrimesByDate(final java.sql.Date date, final OnDBGetListener<CrimeData> listener)
	{
		new AsyncTask<Void, Void, List<CrimeData>>() {
			@Override
			protected List<CrimeData> doInBackground(Void... arg0) {
				List<CrimeData> dataList = new LinkedList<CrimeData>();

				SQLiteDatabase db = instance.openDatabase();
				String selectQuery = "SELECT crime_id FROM crime_data WHERE Datetime(crime_date) >=  Datetime('" + date.toString() + "')";

				Cursor c = db.rawQuery(selectQuery, null);
				boolean hasNext = c.moveToFirst();

				while (hasNext) {
					dataList.add(getCrimeData(c.getInt(c
							.getColumnIndex("crime_id"))));
					hasNext = c.moveToNext();
				}

				c.close();
				instance.closeDatabase();

				return dataList;
			}

			public void onPostExecute(List<CrimeData> result) {
				listener.OnGet(result);
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	public void getCrimesByDate(final java.sql.Date date, final java.sql.Date dateB, final OnDBGetListener<CrimeData> listener)
	{
		new AsyncTask<Void, Void, List<CrimeData>>() {
			@Override
			protected List<CrimeData> doInBackground(Void... arg0) {
				List<CrimeData> dataList = new LinkedList<CrimeData>();

				SQLiteDatabase db = instance.openDatabase();
				String selectQuery = "SELECT crime_id FROM crime_data WHERE Datetime(crime_date) >=  Datetime('" + date.toString() + "')" +
						"AND Datetime(crime_date) <= " + dateB.toString() + "')";

				Cursor c = db.rawQuery(selectQuery, null);
				boolean hasNext = c.moveToFirst();

				while (hasNext) {
					dataList.add(getCrimeData(c.getInt(c
							.getColumnIndex("crime_id"))));
					hasNext = c.moveToNext();
				}

				c.close();
				instance.closeDatabase();

				return dataList;
			}

			public void onPostExecute(List<CrimeData> result) {
				listener.OnGet(result);
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
}
