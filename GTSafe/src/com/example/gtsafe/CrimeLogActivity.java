package com.example.gtsafe;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.example.gtsafe.library.DBManager;
import com.example.gtsafe.library.listeners.interfaces.OnDBGetListener;
import com.example.gtsafe.model.CrimeData;
import com.example.gtsafe.model.OffenseType;
import com.example.gtsafe.model.ZoneData;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ApplicationErrorReport.AnrInfo;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CrimeLogActivity extends Activity implements
		OnItemSelectedListener, OnItemClickListener 
{
	private enum Search
	{
		ZONE("Zone"), CRIME_TYPE("Crime Type"), DATE("Date");
		
		String name;
		
		private search(String name)
		{
			this.name = name;
		}
		
		public String toString()
		{
			return name;
		}
	}
	
	final DBManager db = DBManager.getInstance();
	ListView crimes;
	Spinner searchCrimes;
	CharSequence result;
	List<String> searchBy;
	ArrayList<CrimeData> crimeDataHolder;
	ArrayAdapter<CrimeData> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crime_log);
		
		crimeDataHolder = new ArrayList<CrimeData>();
		adapter = new ArrayAdapter<CrimeData>(CrimeLogActivity.this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				crimeDataHolder);

		crimes = (ListView) findViewById(R.id.crimeLog);
		searchCrimes = (Spinner) findViewById(R.id.filterDate);
		searchBy = new LinkedList<String>();
		
		for(int i = 0; i < Search.values().length; i++)
		{
			searchBy.add(Search.values()[i].toString());
		}
		
		final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, searchBy);

		// were gonna start of the list view with all of the crime data
		db.getAllCrimeData(new OnDBGetListener<CrimeData>() 
		{
			@Override
			public void OnGet(List<CrimeData> list) 
			{
				crimeDataHolder.addAll(list);
				crimes.setAdapter(adapter);
				crimes.setTextFilterEnabled(true);
				crimes.setOnItemClickListener(new OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long id) 
					{
						AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create();
						String view= crimes.getChildAt(position).toString();
						alertDialog.setTitle("Crime Info");
						alertDialog.setMessage(view);
						alertDialog.show();
					}	
				});
				
				spinnerArrayAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The
																									// drop
																									// down		
				// view
				searchCrimes.setAdapter(spinnerArrayAdapter);
				searchCrimes.setOnItemSelectedListener(new OnItemSelectedListener()
				{
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int position, long id) 
					{
						if (position == 1) 
						{
							List<String> zones = new LinkedList<String>();
							for(ZoneData data: db.getAllZones())
							{
								zones.add("" + data.getZoneID());
							}
							
							showPopUp("Zones", zones);

						} 
						else if (position == 2) 
						{
							List<String> crimeTypes = new LinkedList<String>();
							for(int i = 0; i < OffenseType.values().length; i++)
							{
								crimeTypes.add(OffenseType.values()[i].toString());
							}

							showPopUpCrimes("Crime Types", crimeTypes, OffenseType.values());

						} 
						
						searchCrimes.setSelection(-1);
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {}
					
				});
				
				// crimes.setFilterText((String) result);
			}
		});
	}

	public void showPopUp(String title, List<String> options, Search type) {
		AlertDialog.Builder b = new Builder(this);
		b.setTitle(title);
		
		if(type == Search.ZONE)
		{
			b.setItems((CharSequence[]) options.toArray(), new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					adapter.clear();
					
					db.getCrimesByZone(which, new OnDBGetListener<CrimeData>() {
						@Override
						public void OnGet(List<CrimeData> list) {
							adapter.addAll(list);
							crimeDataHolder.addAll(list);
	
							// set the changed data
							// notify that the model changed
							adapter.notifyDataSetChanged();
						}
					});
				}
			});
		}
		else if(type == Search.CRIME_TYPE)
		{
			
		}

		b.show();
	}

	
	public void showPopUpCrimes(String title, String[] options, OffenseType [] offenses) {
		AlertDialog.Builder b = new Builder(this);
		b.setTitle(title);
		final String[] types = options;
		b.setItems(types, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				adapter.clear();
				final OffenseType offType = OffenseType.AGG_ASSAULT.getOffenseType(types[which]);
				db.getCrimesByType(offType, new OnDBGetListener<CrimeData>() {

					@Override
					public void OnGet(List<CrimeData> list) {
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getOffense()== offType) {
								adapter.add(list.get(i));
								crimeDataHolder.add(list.get(i));
							}
						}
						// set the changed data
						// notify that the model changed
						adapter.notifyDataSetChanged();
						
					}
					
				});

			}

		});

		b.show();
	}
}
