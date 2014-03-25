package com.example.gtsafe.library;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper
{
	private final static int DB_VERSION = 7;
	private final static String DB_NAME = "gtsafe.db";
	
	public DBHelper(Context context)
	{
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		String CREATE_ZONE_TABLE = 
				"CREATE TABLE zones" +
				"(" +
					"zone_id INTEGER PRIMARY KEY NOT NULL,\n" +
					"points varchar(10000) NOT NULL" +
				");";
		String CREATE_ZONE_INFO_TABLE = 
				"CREATE TABLE zone_information" +
				"(" +
					"zone_info_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
					"zone_id int NOT NULL,\n" +
					"zone_description text NOT NULL, \n"+
					"FOREIGN KEY(zone_id) REFERENCES zones(zone_id)" +
				");";
		String CREATE_CRIME_INFO_TABLE =
				"CREATE TABLE crime_data" +
				"(" +
					"crime_id INTEGER PRIMARY KEY NOT NULL,\n" +
					"crime_date DATETIME NOT NULL," +
					"offense varchar(255) NOT NULL,\n" +
					"offense_desc varchar(255) NOT NULL,\n" +
					"location varchar(255) NOT NULL, \n" +
					"zone_id int NOT NULL, \n" +
					"latitude float NOT NULL, \n" +
					"longitude float NOT NULL, \n" +
					"FOREIGN KEY(zone_id) REFERENCES zones(zone_id)" +
				");";
		String CREATE_CLERY_ACTS_TABLE = "CREATE TABLE clery_acts\n" + 
				"(" +
					"ca_id INTEGER PRIMARY KEY NOT NULL,\n" + 
					"ca_date date NOT NULL,\n" + 
					"ca_title varchar(255),\n" + 
					"ca_text TEXT NOT NULL\n" + 
				");";
				
		
        db.execSQL(CREATE_ZONE_TABLE);
        db.execSQL(CREATE_ZONE_INFO_TABLE);
        db.execSQL(CREATE_CRIME_INFO_TABLE);
        db.execSQL(CREATE_CLERY_ACTS_TABLE);
        
        DBManager.getInstance().initLocalDB(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		db.execSQL("DROP TABLE IF EXISTS zones");
		db.execSQL("DROP TABLE IF EXISTS zone_information");
		db.execSQL("DROP TABLE IF EXISTS crime_data");
		db.execSQL("DROP TABLE IF EXISTS clery_acts"); 
		
        // Create tables again
        onCreate(db);		
	}
}
