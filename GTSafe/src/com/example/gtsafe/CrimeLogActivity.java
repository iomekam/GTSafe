package com.example.gtsafe;

import java.util.ArrayList;
import java.util.List;

import com.example.gtsafe.library.DBManager;
import com.example.gtsafe.library.listeners.interfaces.OnDBGetListener;
import com.example.gtsafe.model.CrimeData;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class CrimeLogActivity extends Activity {

	ListView crimes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crime_log);

		crimes = (ListView) findViewById(R.id.crimeLog);
		final ArrayList<String> crimeDataHolder = new ArrayList<String>();
		final DBManager db = DBManager.getInstance();
		// were gonna start of the list view with all of the crime data
		db.getAllCrimeData(new OnDBGetListener<CrimeData>() {
			@Override
			public void OnGet(List<CrimeData> list) {
				ArrayAdapter<String> adapter;
				for (int i = 0; i < list.size(); i++) {
					crimeDataHolder.add(list.get(i).toString());
				}
				
				adapter = new ArrayAdapter<String>(CrimeLogActivity.this,
						android.R.layout.simple_list_item_1, android.R.id.text1,
						crimeDataHolder);
				
				crimes.setAdapter(adapter);
			}
		});

		/*crimes.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// ListView Clicked item index
				int itemPosition = position;

				// ListView Clicked item value
				String itemValue = (String) crimes
						.getItemAtPosition(position);

				// Show Alert
				Toast.makeText(
						getApplicationContext(),
						"Position :" + itemPosition + "  ListItem : "
								+ itemValue, Toast.LENGTH_LONG).show();
			}
		});*/

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.crime_log, menu);
		return true;
	}

}
