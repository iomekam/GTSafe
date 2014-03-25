package com.example.gtsafe.library;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import com.example.gtsafe.MainActivity;
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
	
	//private Hashtable<String, Hashtable<String, List<CrimeData>>> table = new Hashtable<String, Hashtable<String, List<CrimeData>>>();
	
	private Context context;
	private String FILENAME = "table.ht";

	private List<OnDBUpdateListener<ZoneData>> zoneListener = new LinkedList<OnDBUpdateListener<ZoneData>>();
	private List<OnDBUpdateListener<List<ZoneData>>> allZoneListener = new LinkedList<OnDBUpdateListener<List<ZoneData>>>();
	private List<OnDBUpdateListener<CrimeData>> crimeDataListener = new LinkedList<OnDBUpdateListener<CrimeData>>();
	private List<OnDBUpdateListener<List<CrimeData>>> allCrimeDataListener = new LinkedList<OnDBUpdateListener<List<CrimeData>>>();
	private List<OnDBUpdateListener<ZoneInfo>> zoneInfoListener = new LinkedList<OnDBUpdateListener<ZoneInfo>>();
	private List<OnDBUpdateListener<List<ZoneInfo>>> allZoneInfoListener = new LinkedList<OnDBUpdateListener<List<ZoneInfo>>>();
	private List<OnDBUpdateListener<CleryActModel>> cleryActListener = new LinkedList<OnDBUpdateListener<CleryActModel>>();
	private List<OnDBUpdateListener<List<CleryActModel>>> allCleryActListener = new LinkedList<OnDBUpdateListener<List<CleryActModel>>>();
	
	private boolean useDefaultDB;
	
	public enum TableEntry{ CRIMES_ALL, CRIMES_TYPE, CRIMES_ZONE };
	
	public String crimeAllEntry = "get_all_crime";
	public String crimeTypeEntry = "get_all_crime_type";
	public String crimeZoneEntry = "get_all_crime_zone";
	
	public boolean runningInit = false;
	public int initCount;
	public ProgressDialog initDialog = null;
	
	public ProgressDialog loadingScreen = null;

	public static synchronized void initializeInstance(SQLiteOpenHelper helper, Context context) {
		if (instance == null) {
			instance = new DBManager();
			dbHelper = helper;
			instance.context = context;
			instance.db = dbHelper.getWritableDatabase();
			//instance.deserializeTable();
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

	public synchronized SQLiteDatabase openDatabase() {
		if (!useDefaultDB && mOpenCounter.incrementAndGet() == 1) {
			// Opening new database
			db = dbHelper.getWritableDatabase();
		}

		return db;
	}

	public synchronized void closeDatabase() {
		
		if (!useDefaultDB && mOpenCounter.decrementAndGet() == 0) {
			// Closing database
			db.close();
		}
	}

	public void initLocalDB(SQLiteDatabase db) {
		SQLiteDatabase temp = this.db;

		this.db = db;
		this.useDefaultDB = true;
		runningInit = true;
		initCount = 3;
		
		synchronized(db)
		{
			updateAllZones();
			updateAllCrimes();
			updateAllCleryAct();
		}
		//db.close();
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
	
	/*public void updateTable(TableEntry option)
	{
		if(option == TableEntry.CRIMES_ALL)
		{
			table.remove(crimeAllEntry);
		}
		else if(option == TableEntry.CRIMES_TYPE)
		{
			table.remove(crimeTypeEntry);
		}
		else if(option == TableEntry.CRIMES_ZONE)
		{
			table.remove(crimeZoneEntry);
		}
		
		instance.serializeTable();
	}*/
	
	/*public void insertTable(TableEntry option, CrimeData data)
	{
		Log.e(data.getOffense().toString(), "s");
		CrimeData.sorted = false;
		if(option == TableEntry.CRIMES_ALL)
		{
			List<CrimeData> updatedData = table.get(crimeAllEntry).get("ALL");
			synchronized(table.get(crimeAllEntry))
			{
				updatedData.add(data);
			}
		}
		else if(option == TableEntry.CRIMES_ZONE)
		{
			if(!table.get(crimeZoneEntry).contains("" + data.getZone().getZoneID()))
			{
				table.get(crimeZoneEntry).put("" + data.getZone().getZoneID(), new LinkedList<CrimeData>());
			}
			
			List<CrimeData> updatedData = table.get(crimeAllEntry).get("" + data.getZone().getZoneID());
			
			table.get(crimeAllEntry).get("" + data.getZone()).clear();
			table.get(crimeAllEntry).put("" + data.getZone().getZoneID(), updatedData);
		}
		else if(option == TableEntry.CRIMES_TYPE)
		{
			table.remove(crimeZoneEntry);
		}
		
		instance.serializeTable();
	}*/
	
	/*private void serializeTable()
	{
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				FileOutputStream fos;
				try 
				{
					fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(table);
			        oos.close();
				} 
				catch (FileNotFoundException e) 
				{
					e.printStackTrace();
					Log.e("serialize", "failed");
				} 
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("serialize", "failed");
				}
				
				return null;
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	@SuppressWarnings("unchecked")
	public void deserializeTable()
	{
		new AsyncTask<Void, Void, Void>() {
			@Override
	        protected void onPreExecute()
			{
				if(loadingScreen != null)
				{
					loadingScreen.setMessage("Loading data, please wait.");
					loadingScreen.show();
				}
			}
			@Override
			protected Void doInBackground(Void... params) {
				FileInputStream fis;
				
				try {
					fis = context.openFileInput(FILENAME);
					ObjectInputStream ois = new ObjectInputStream(fis);
			        table = (Hashtable<String, Hashtable<String, List<CrimeData>>>)ois.readObject();
			        ois.close();
				} 
				catch (FileNotFoundException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				catch (StreamCorruptedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return null;
			}
			
			public void onPostExecute(Void result) {
				if(loadingScreen != null)
				{
					loadingScreen.dismiss();
					loadingScreen = null;
				}
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}*/

	public void updateAllZones() {
		request = new DBRequester();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "get_zones"));

		instance.openDatabase().delete("zones", null, null);
		instance.closeDatabase();

		request.setOnJSONEventListener(new UpdateAllZoneListener(
				allZoneListener));
		request.getJSON(params);
	}

	public void setOnAllZoneUpdateEventListener(OnDBUpdateListener<List<ZoneData>> listener) {
		this.allZoneListener.add(listener);
	}

	public void updateZone(int zoneIDServer) {
		request = new DBRequester();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "get_individual_zone"));
		params.add(new BasicNameValuePair("zone_id", "" + zoneIDServer));

		request.setOnJSONEventListener(new UpdateZoneListener(zoneListener));
		request.getJSON(params);
	}

	public void setOnZoneUpdateEventListener(OnDBUpdateListener<ZoneData> listener) {
		this.zoneListener.add(listener);
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
	
	public void setOnZoneInfoUpdateEventListener(OnDBUpdateListener<ZoneInfo> listener) {
		this.zoneInfoListener.add(listener);
	}
	
	public void updateAllZoneInfo() {
		request = new DBRequester();
		
		instance.openDatabase().delete("zone_information", null, null);
		instance.closeDatabase();
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "get_zone_info"));

		request.setOnJSONEventListener(new UpdateAllZoneInfoListener(allZoneInfoListener));
		request.getJSON(params);
	}

	public void setOnAllZoneInfoUpdateEventListener(OnDBUpdateListener<List<ZoneInfo>> listener) {
		this.allZoneInfoListener.add(listener);
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
	
	public void setOnCleryActUpdateListener(OnDBUpdateListener<CleryActModel> listener)
	{
		this.cleryActListener.add(listener);
	}
	
	public void updateAllCleryAct() {
		request = new DBRequester();
		
		instance.openDatabase().delete("clery_acts", null, null);
		instance.closeDatabase();
		
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "get_clery_act"));
		
		request.setOnJSONEventListener(new UpdateAllCleryActListener(allCleryActListener));
		request.getJSON(params);
	}

	public void setOnAllCleryActUpdateEventListener(OnDBUpdateListener<List<CleryActModel>> listener) {
		this.allCleryActListener.add(listener);
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

	public void setOnCrimeUpdateEventListener(OnDBUpdateListener<CrimeData> listener) {
		this.crimeDataListener.add(listener);
	}

	public void updateAllCrimes() {
		request = new DBRequester();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "get_crime_data"));

		instance.openDatabase().delete("crime_data", null, null);
		instance.closeDatabase();

		request.setOnJSONEventListener(new UpdateAllCrimeDataListener(
				allCrimeDataListener));
		request.getJSON(params);
	}

	public void setOnAllCrimeUpdateEventListener(OnDBUpdateListener<List<CrimeData>> listener) {
		this.allCrimeDataListener.add(listener);
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

		zInfo = new ZoneInfo(zoneID, description);

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
		String selectQuery = "SELECT crime_id, offense, offense_desc, location, zone_id,"
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
						.getColumnIndex("location")), date, OffenseType.getOffenseType(c.getString(c.getColumnIndex("offense"))),
						c.getString(c.getColumnIndex("offense_desc")), getZone(c.getInt(c.getColumnIndex("zone_id"))));
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
				/*if(table.containsKey(instance.crimeAllEntry))
				{
					Hashtable<String, List<CrimeData>> entry = (Hashtable<String, List<CrimeData>>)table.get("get_all_crime");
					if(!CrimeData.isSorted())
					{
						Collections.sort(entry.get("ALL"), CrimeData.getComparator());
						CrimeData.sorted = true;
					}
					
					return (List<CrimeData>)entry.get("ALL");
				}*/
				
				List<CrimeData> crimeList = new LinkedList<CrimeData>();

				SQLiteDatabase db = instance.openDatabase();
				String selectQuery = "SELECT crime_id FROM crime_data ORDER BY crime_date DESC";
				Cursor c = db.rawQuery(selectQuery, null);

				boolean hasNext = c.moveToFirst();
				while (hasNext) 
				{
					crimeList.add(getCrimeData(c.getInt(c.getColumnIndex("crime_id"))));
					hasNext = c.moveToNext();
				}

				c.close();
				instance.closeDatabase();
				
				//Hashtable<String, List<CrimeData>> entry = new Hashtable<String, List<CrimeData>>();
				//entry.put("ALL", crimeList);
				
				//table.put(instance.crimeAllEntry, entry);
				//instance.serializeTable();

				return crimeList;
			}

			public void onPostExecute(List<CrimeData> result) {
				/*if(runningInit)
				{
					initCount--;
					
					if(initCount == 0 && initDialog != null)
					{
						initDialog.dismiss();
						runningInit = false;
					}
				}*/
				
				if(listener != null)
				{
					listener.OnGet(result);
				}
				else
				{
					Log.e("Table:", "DONE");
				}
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

				data = new CleryActModel(caID, title, date, text);
			} 
			catch (ParseException e) 
			{
				Log.e("Parse Exception", e.getMessage());
			}
		}
		
		c.close();
		instance.closeDatabase();
		
		return data;
	}
	
	public List<CleryActModel> getAllCleryActs()
	{
		List<CleryActModel> dataList = new LinkedList<CleryActModel>();
		
		SQLiteDatabase db = instance.openDatabase();
		String selectQuery = "SELECT ca_id FROM clery_acts ORDER BY ca_date DESC";
		
		Cursor c = db.rawQuery(selectQuery, null);
		boolean hasNext = c.moveToFirst();

		while (hasNext) 
		{
			dataList.add(getCleryAct(c.getInt(c.getColumnIndex("ca_id"))));
			hasNext = c.moveToNext();
		}

		c.close();
		instance.closeDatabase();

		return dataList;
	}

	public void getCrimesByZone(final int zoneID,
			final OnDBGetListener<CrimeData> listener) {
		
		new AsyncTask<Void, Void, List<CrimeData>>() {
			@Override
			protected List<CrimeData> doInBackground(Void... arg0) {
				
				/*if(table.containsKey(instance.crimeZoneEntry) && table.get(instance.crimeZoneEntry).containsKey("" + zoneID))
				{
					Hashtable<String, List<CrimeData>> entry = (Hashtable<String, List<CrimeData>>)table.get(instance.crimeZoneEntry);
					if(!CrimeData.isSorted())
					{
						Collections.sort(entry.get("" + zoneID), CrimeData.getComparator());
						CrimeData.sorted = true;
					}
					
					return (List<CrimeData>)entry.get("" + zoneID);
				}*/
				
				List<CrimeData> dataList = new LinkedList<CrimeData>();

				SQLiteDatabase db = instance.openDatabase();
				String selectQuery = "SELECT crime_id FROM crime_data WHERE zone_id = " + zoneID + " ORDER BY crime_date DESC";

				Cursor c = db.rawQuery(selectQuery, null);
				boolean hasNext = c.moveToFirst();

				while (hasNext) {
					dataList.add(getCrimeData(c.getInt(c
							.getColumnIndex("crime_id"))));
					hasNext = c.moveToNext();
				}

				c.close();
				instance.closeDatabase();
				
				/*if(!table.containsKey(instance.crimeZoneEntry))
				{
					table.put(instance.crimeZoneEntry, new Hashtable<String, List<CrimeData>>());
				}
		
				table.get(instance.crimeZoneEntry).put("" + zoneID, dataList);
				instance.serializeTable();*/

				return dataList;
			}

			public void onPostExecute(List<CrimeData> result) {
				/*if(runningInit)
				{
					initCount--;
					
					if(initCount == 0 && initDialog != null)
					{
						initDialog.dismiss();
						runningInit = false;
					}
				}*/
				
				if(listener != null)
				{
					listener.OnGet(result);
				}
				else
				{
					Log.e("Zone Table", "Done");
				}
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
				String selectQuery = "SELECT crime_id FROM crime_data WHERE crime_date BETWEEN '" + date.toString() + "' AND date('now')" +
										"ORDER BY crime_date DESC";

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
				String selectQuery = "SELECT crime_id FROM crime_data WHERE crime_date BETWEEN '" + date.toString() + "' AND '" 
										+ dateB.toString() + "' ORDER BY crime_date DESC";

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
	
	public void getCrimesByType(final OffenseType type, final OnDBGetListener<CrimeData> listener)
	{
		new AsyncTask<Void, Void, List<CrimeData>>() {
			@Override
			protected List<CrimeData> doInBackground(Void... arg0) {
				List<CrimeData> dataList = new LinkedList<CrimeData>();

				SQLiteDatabase db = instance.openDatabase();
				String selectQuery = "SELECT crime_id FROM crime_data WHERE offense = '" + type.toString() + "' ORDER BY crime_date DESC";

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
	
	public void getCrimesByType(final OffenseType type, final int zoneID, final OnDBGetListener<CrimeData> listener)
	{
		new AsyncTask<Void, Void, List<CrimeData>>() {
			@Override
			protected List<CrimeData> doInBackground(Void... arg0) {
				List<CrimeData> dataList = new LinkedList<CrimeData>();

				SQLiteDatabase db = instance.openDatabase();
				String selectQuery = "SELECT crime_id FROM crime_data WHERE offense = '" + type.toString() + "' AND zone_id = " + zoneID + 
										" ORDER BY crime_date DESC";

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
	
//	public void getCrimeTypeByMonth(final OffenseType type, final OnDBGetListener<List<CrimeData>> listener)
//	{
//		new AsyncTask<Void, Void, List<List<CrimeData>>>() {
//			@SuppressWarnings("deprecation")
//			@Override
//			protected List<List<CrimeData>> doInBackground(Void... arg0) {
//				
//				List<List<CrimeData>> superList = new LinkedList<List<CrimeData>>();
//				List<CrimeData> dataList = new LinkedList<CrimeData>();
//						
//				SQLiteDatabase db = instance.openDatabase();
//				
//				DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
//						Locale.US);
//				Date date = null;
//				String selectQuery = "SELECT crime_id FROM crime_data WHERE offense = '" + type.toString() + "'";
//				
//				int currentMonth = 1;
//
//				Cursor c = db.rawQuery(selectQuery, null);
//				boolean hasNext = c.moveToFirst();
//
//				while (hasNext) {
//					try 
//					{
//						date = format.parse(c.getString(c.getColumnIndex("crime_date")));
//						
//						if(date.getMonth() > currentMonth)
//						{
//							superList.add(dataList);
//							dataList = new LinkedList<CrimeData>();
//							currentMonth++;
//						}
//						
//						dataList.add(getCrimeData(c.getInt(c
//								.getColumnIndex("crime_id"))));
//					} 
//					catch (ParseException e) {}
//					
//					hasNext = c.moveToNext();
//				}
//
//				c.close();
//				instance.closeDatabase();
//
//				return superList;
//			}
//
//			public void onPostExecute(List<List<CrimeData>> result) {
//				listener.OnGet(result);
//			}
//		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//	}
}
