package com.example.gtsafe;

import java.util.ArrayList;
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
		OnItemSelectedListener, OnItemClickListener {

	final DBManager db = DBManager.getInstance();
	ListView crimes;
	Spinner searchCrimes;
	CharSequence result;
	String[] searchBy = { "Search By", "Zone", "Crime", "Date" };
	final ArrayList<CrimeData> crimeDataHolder = new ArrayList<CrimeData>();
	ArrayAdapter<CrimeData> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crime_log);

		adapter = new ArrayAdapter<CrimeData>(CrimeLogActivity.this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				crimeDataHolder);

		crimes = (ListView) findViewById(R.id.crimeLog);
		searchCrimes = (Spinner) findViewById(R.id.filterDate);
		final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, searchBy);

		// were gonna start of the list view with all of the crime data
		db.getAllCrimeData(new OnDBGetListener<CrimeData>() {
			@Override
			public void OnGet(List<CrimeData> list) {
				for (int i = 0; i < list.size(); i++) {
					crimeDataHolder.add(list.get(i));
				}

				crimes.setAdapter(adapter);
				crimes.setTextFilterEnabled(true);
				crimes.setOnItemClickListener(CrimeLogActivity.this);
				spinnerArrayAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The
																									// drop
																									// down
																									// view
				searchCrimes.setAdapter(spinnerArrayAdapter);
				searchCrimes.setOnItemSelectedListener(CrimeLogActivity.this);
				// crimes.setFilterText((String) result);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.crime_log, menu);
		return true;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		int position = searchCrimes.getSelectedItemPosition();
		if (position == 0) {
			// do nothing it acts as a header
		} else if (position == 1) {
			int j = 1;
			String[] zones = new String[db.getAllZones().size()];
			for (int i = 0; i < zones.length; i++) {
				zones[i] = "" + j;
				j++;
			}
			showPopUpZones("Zones", zones);

		} else if (position == 2) { 
			
			String [] crime = { OffenseType.HOMICIDE.toString(),
					OffenseType.RAPE.toString(),
					OffenseType.ROBBERY.toString(),
					OffenseType.AGG_ASSAULT.toString(),
					OffenseType.BURGLARY.toString(),
					OffenseType.LARCENY.toString(),
					OffenseType.AUTO_THEFT.toString(),
					OffenseType.ARSON.toString(),
					OffenseType.OTHER_ASSAULTS.toString(),
					OffenseType.FORGERY.toString(),
					OffenseType.FRAUD.toString(),
					OffenseType.EMBEZZLEMENT.toString(),
					OffenseType.STOLEN_PROPERTY.toString(),
					OffenseType.DAMAGE_TO_PROPERTY.toString(),
					OffenseType.WEAPONS_OFFENSE.toString(),
					OffenseType.PROSTITUTION.toString(),
					OffenseType.SEX_OFFENSE.toString(),
					OffenseType.DANGEROUS_DRUGS.toString(),
					OffenseType.GAMBLING_OFFENSE.toString(),
					OffenseType.FAMILY_OFFENSE.toString(),
					OffenseType.DUI.toString(),
					OffenseType.LIQUOR_LAWS.toString(),
					OffenseType.PUBLIC_PEACE_OFFENSE.toString(),
					OffenseType.VAGRANCY_OFFENSE.toString(),
					OffenseType.ALL_OTHER_OFFENSES.toString(),
					OffenseType.NO_CRIME.toString(),
					OffenseType.NON_CRIME.toString() };
			
			OffenseType [] offenses= { OffenseType.HOMICIDE ,
					OffenseType.RAPE ,
					OffenseType.ROBBERY ,
					OffenseType.AGG_ASSAULT ,
					OffenseType.BURGLARY ,
					OffenseType.LARCENY ,
					OffenseType.AUTO_THEFT ,
					OffenseType.ARSON ,
					OffenseType.OTHER_ASSAULTS ,
					OffenseType.FORGERY ,
					OffenseType.FRAUD ,
					OffenseType.EMBEZZLEMENT ,
					OffenseType.STOLEN_PROPERTY ,
					OffenseType.DAMAGE_TO_PROPERTY ,
					OffenseType.WEAPONS_OFFENSE ,
					OffenseType.PROSTITUTION ,
					OffenseType.SEX_OFFENSE ,
					OffenseType.DANGEROUS_DRUGS ,
					OffenseType.GAMBLING_OFFENSE ,
					OffenseType.FAMILY_OFFENSE ,
					OffenseType.DUI ,
					OffenseType.LIQUOR_LAWS ,
					OffenseType.PUBLIC_PEACE_OFFENSE ,
					OffenseType.VAGRANCY_OFFENSE ,
					OffenseType.ALL_OTHER_OFFENSES ,
					OffenseType.NO_CRIME ,
					OffenseType.NON_CRIME  };

			showPopUpCrimes("Crime Types", crime, offenses);

		} else {

		}
		searchCrimes.setSelection(-1);

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	public void showPopUpZones(String title, String[] options) {
		AlertDialog.Builder b = new Builder(this);
		b.setTitle(title);
		String[] types = options;
		b.setItems(types, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
				switch (which) {
				case 0:
					adapter.clear();
					db.getAllCrimeData(new OnDBGetListener<CrimeData>() {
						@Override
						public void OnGet(List<CrimeData> list) {
							for (int i = 0; i < list.size(); i++) {
								if (list.get(i).getZone().getZoneID() == 1) {
									adapter.add(list.get(i));
									crimeDataHolder.add(list.get(i));
								}
							}
							// set the changed data
							// notify that the model changed
							adapter.notifyDataSetChanged();

						}
					});

					break;
				case 1:
					adapter.clear();
					db.getAllCrimeData(new OnDBGetListener<CrimeData>() {
						@Override
						public void OnGet(List<CrimeData> list) {
							for (int i = 0; i < list.size(); i++) {
								if (list.get(i).getZone().getZoneID() == 2) {
									adapter.add(list.get(i));
									crimeDataHolder.add(list.get(i));
								}
							}
							// set the changed data
							// notify that the model changed
							adapter.notifyDataSetChanged();

						}
					});
					break;

				case 2:
					adapter.clear();
					db.getAllCrimeData(new OnDBGetListener<CrimeData>() {
						@Override
						public void OnGet(List<CrimeData> list) {
							for (int i = 0; i < list.size(); i++) {
								if (list.get(i).getZone().getZoneID() == 3) {
									adapter.add(list.get(i));
									crimeDataHolder.add(list.get(i));
								}
							}
							// set the changed data
							// notify that the model changed
							adapter.notifyDataSetChanged();

						}
					});
					break;

				case 3:
					adapter.clear();
					db.getAllCrimeData(new OnDBGetListener<CrimeData>() {
						@Override
						public void OnGet(List<CrimeData> list) {
							for (int i = 0; i < list.size(); i++) {
								if (list.get(i).getZone().getZoneID() == 4) {
									adapter.add(list.get(i));
									crimeDataHolder.add(list.get(i));
								}
							}
							// set the changed data
							// notify that the model changed
							adapter.notifyDataSetChanged();

						}
					});

					break;
				case 4:
					adapter.clear();
					db.getAllCrimeData(new OnDBGetListener<CrimeData>() {
						@Override
						public void OnGet(List<CrimeData> list) {
							for (int i = 0; i < list.size(); i++) {
								if (list.get(i).getZone().getZoneID() == 5) {
									adapter.add(list.get(i));
									crimeDataHolder.add(list.get(i));
								}
							}
							// set the changed data
							// notify that the model changed
							adapter.notifyDataSetChanged();

						}
					});
					break;
				}

			}

		});

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

	@SuppressWarnings("deprecation")
	@Override
	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		String view=l.getChildAt(position).toString();
		alertDialog.setTitle("Crime Info");
		alertDialog.setMessage(view);
		alertDialog.show();
		
	}
	

}
