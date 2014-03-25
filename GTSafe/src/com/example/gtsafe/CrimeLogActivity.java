package com.example.gtsafe;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gtsafe.library.listeners.interfaces.Listable;
import com.example.gtsafe.library.listeners.interfaces.OnDBGetListener;
import com.example.gtsafe.library.listeners.interfaces.OnDBUpdateListener;
import com.example.gtsafe.model.CrimeData;
import com.example.gtsafe.model.OffenseType;
import com.example.gtsafe.model.ZoneData;

public class CrimeLogActivity extends SuperActivity
{
	private static List<CrimeData> crimeData = new LinkedList<CrimeData>();

	private enum Search
	{
		ALL("All Crimes", null),
		ZONE("Zone", manager.getAllZones().toArray(new ZoneData[manager.getAllZones().size()])), 
				CRIME_TYPE("Crime Type", OffenseType.values()), DATE("Date", null);
		
		String name;
		Listable[] searchContent = {}; //all items should implement Listable
		
		private Search(String name, Listable[] searchContent)
		{
			this.name = name;
			
			if(searchContent != null)
			{
				this.searchContent = searchContent;
			}
		}
		
		public String[] getContent() 
		{ 
			List<String> list = new LinkedList<String>();
			for(Listable item: searchContent)
			{
				list.add(((Listable)item).listString());
			}
			
			return list.toArray(new String[list.size()]);
		}
		
		@Override
		public String toString(){ return name; }

	}
	
	ListView crimes;
	CustomSpinner searchCrimes;
	CharSequence result;
	List<String> searchBy;
	ArrayAdapter<CrimeData> adapter;
	Search currentSelection = Search.ALL;
	Object selectedItem = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crime_log);
		
		adapter = new ArrayAdapter<CrimeData>(CrimeLogActivity.this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				new LinkedList<CrimeData>());

		crimes = (ListView) findViewById(R.id.crimeLog);
		searchCrimes = (CustomSpinner) findViewById(R.id.filterDate);
		
		final ArrayAdapter<Search> spinnerArrayAdapter = new ArrayAdapter<Search>(
				this, android.R.layout.simple_spinner_item, Search.values());
		
		// were gonna start of the list view with all of the crime data
		manager.getAllCrimeData(new OnDBGetListener<CrimeData>() 
		{
			@Override
			public void OnGet(List<CrimeData> list) 
			{
				crimeData = list;
				adapter.addAll(list);
				crimes.setAdapter(adapter);
				crimes.setTextFilterEnabled(true);
				crimes.setOnItemClickListener(new OnItemClickListener()
				{
					@SuppressWarnings("deprecation")
					@Override
					public void onItemClick(AdapterView<?> arg0, View view,
							int position, long id) 
					{
						AlertDialog alertDialog = new AlertDialog.Builder(view.getContext()).create();
						CrimeData crime = (CrimeData)crimes.getItemAtPosition(position);
						alertDialog.setTitle(crime.getOffense().toString());
						View v = getLayoutInflater().inflate(R.layout.dialogboxy, null);
						alertDialog.setView(v);
						
			            TextView dates = (TextView) v.findViewById(R.id.list_date);
			            TextView latlngs = (TextView) v.findViewById(R.id.list_lat_lang);
			            TextView locs = (TextView) v.findViewById(R.id.list_loc);
			            TextView descriptions = (TextView) v.findViewById(R.id.list_description);
			            
			            dates.setText("Date: " + crime.getDate().toLocaleString());
			            latlngs.setText("GPS Location: " + "(" + crime.getLocation().latitude + ", " + crime.getLocation().longitude + ")");
			            locs.setText("Location: " + crime.getLocationName());
			            descriptions.setText("Description: " + crime.getOffenseDescription());	
			            alertDialog.setCancelable(true);
			            alertDialog.setButton("Dismiss", new OnClickListener(){

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								arg0.dismiss();
							}
			            	
			            });
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
						Search type = (Search)searchCrimes.getItemAtPosition(position);
						currentSelection = type;
						
						if(type == Search.ALL)
						{
							adapter.clear();
							adapter.addAll(crimeData);
							adapter.notifyDataSetChanged();
						}
						else
						{
							showPopUp(type.toString(), type.getContent(), type);
							searchCrimes.setSelection(-1);
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {}
					
				});
				
				// crimes.setFilterText((String) result);
			}
		});
		
		manager.setOnCrimeUpdateEventListener(new OnDBUpdateListener<CrimeData>()
		{
			@Override
			public void OnUpdate(CrimeData item) {
				int position = crimes.getSelectedItemPosition();
				
				if((currentSelection == Search.ZONE && item.getZone().getZoneID() == (Integer)selectedItem) ||
						(currentSelection == Search.CRIME_TYPE && item.getOffense() == (OffenseType)selectedItem) ||
						(currentSelection == Search.ALL))
				{
				
					adapter.insert(item, 0);
					adapter.sort(CrimeData.getComparator());
					
					runOnUiThread(new Runnable() {
				        @Override
				        public void run() {
				                adapter.notifyDataSetChanged();
				        }
				    });
				}
				
				crimes.setSelection(position);
				
				crimeData.add(item);
				Collections.sort(crimeData, CrimeData.getComparator());
			}
		});
	}

	public void showPopUp(String title, final String[] options, Search type) {
		AlertDialog.Builder b = new Builder(this);
		b.setTitle(title);
		
		if(type == Search.ZONE)
		{
			b.setItems(options, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					adapter.clear();
					
					selectedItem = Integer.parseInt(options[which]);
					
					manager.getCrimesByZone(Integer.parseInt(options[which]), new OnDBGetListener<CrimeData>() {
						@Override
						public void OnGet(List<CrimeData> list) 
						{
							adapter.addAll(list);
							adapter.notifyDataSetChanged();
						}
					});
				}
			});
		}
		else if(type == Search.CRIME_TYPE)
		{
			b.setItems(options, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int position) {
					dialog.dismiss();
					adapter.clear();
					final OffenseType offType = OffenseType.getOffenseType(options[position]);
					selectedItem = offType;
					manager.getCrimesByType(offType, new OnDBGetListener<CrimeData>() {
						@Override
						public void OnGet(List<CrimeData> list) {
							adapter.addAll(list);
							adapter.notifyDataSetChanged();
						}
					});
				}
			});
		}
		
		b.show();
	}
}