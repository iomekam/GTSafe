package com.example.gtsafe;


import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.example.gtsafe.library.DBHelper;
import com.example.gtsafe.library.DBManager;
import com.example.gtsafe.library.listeners.interfaces.Listable;
import com.example.gtsafe.library.listeners.interfaces.OnDBGetListener;
import com.example.gtsafe.library.listeners.interfaces.OnDBUpdateListener;
import com.example.gtsafe.model.CrimeData;
import com.example.gtsafe.model.OffenseType;
import com.example.gtsafe.model.ZoneData;

public class CrimeStatActivity extends SuperActivity {
	
	private enum Search
	{
		ALL("All Crimes", null), 
				CRIME_TYPE("Crime Type", OffenseType.values());
		
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
	
	private XYPlot plot;
	private static List<CrimeData> crimeData = new LinkedList<CrimeData>();
	CustomSpinner searchCrimes;
	CharSequence result;
	ArrayAdapter<CrimeData> adapter;
	Search currentSelection = Search.CRIME_TYPE;
	Object selectedItem = null;
	ListView crimes;



	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crime_stat);
		
		adapter = new ArrayAdapter<CrimeData>(CrimeStatActivity.this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				new LinkedList<CrimeData>());
		
		crimes = (ListView) findViewById(R.id.crimeLog);
		searchCrimes = (CustomSpinner) findViewById(R.id.filterCrime);
		
		
		final ArrayAdapter<Search> spinnerArrayAdapter = new ArrayAdapter<Search>(
				this, android.R.layout.simple_spinner_item, Search.values());
		spinnerArrayAdapter
		.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		
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
		
		
		
		/*manager.getCrimesByDate(d1, d2,new OnDBGetListener<CrimeData>(){
			@Override
			public void OnGet(List<CrimeData> list) 
			{
				crimeData.addAll(list);
			}
		});*/
		/*final DBManager db = DBManager.getInstance();
		
		db.getCrimesByType(OffenseType.NON_CRIME,  new OnDBGetListener<CrimeData>(){

			@Override
			public void OnGet(List<CrimeData> list) {
				//view.setText("Crimes by Type: " + list.size());
				//Toast.makeText(getApplicationContext(), "Crimes Length: " + list.size(),
						   //Toast.LENGTH_LONG).show();
			}
		});*/
		// fun little snippet that prevents users from taking screenshots
        // on ICS+ devices :-)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                                 WindowManager.LayoutParams.FLAG_SECURE);
        manager.setOnCrimeUpdateEventListener(new OnDBUpdateListener<CrimeData>()
        		{
        			@Override
        			public void OnUpdate(CrimeData item) {
        				int position = crimes.getSelectedItemPosition();
        				
        				if(
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
        	
 
 
        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
 
        // Create a couple arrays of y-values to plot:
        Number[] series1Numbers = {1, 8, 5, 2, 7, 4};
        Number[] series2Numbers = {4, 6, 3, 8, 2, 10};
 
        // Turn the above arrays into XYSeries':
        XYSeries series1 = new SimpleXYSeries(
                Arrays.asList(series1Numbers),          // SimpleXYSeries takes a List so turn our array into a List
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
                "Larceny");	 
        // same as above
        XYSeries series2 = new SimpleXYSeries(Arrays.asList(series2Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Burglary");
 
        // Create a formatter to use for drawing a series using LineAndPointRenderer
        // and configure it from xml:
        LineAndPointFormatter series1Format = new LineAndPointFormatter();
        series1Format.setPointLabelFormatter(new PointLabelFormatter());
        series1Format.configure(getApplicationContext(),
                R.xml.line_point_formatter_with_plf1);
 
        // add a new series' to the xyplot:
        plot.addSeries(series1, series1Format);
 
        // same as above:
        LineAndPointFormatter series2Format = new LineAndPointFormatter();
        series2Format.setPointLabelFormatter(new PointLabelFormatter());
        series2Format.configure(getApplicationContext(),
                R.xml.line_point_formatter_with_plf2);
        plot.addSeries(series2, series2Format);
 
        // reduce the number of range labels
        plot.setTicksPerRangeLabel(3);
        plot.getGraphWidget().setDomainLabelOrientation(-45);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.crime_stat, menu);
		return true;
	}
	public void showPopUp(String title, final String[] options, Search type) {
		AlertDialog.Builder b = new Builder(this);
		b.setTitle(title);
		
		
		if(type == Search.CRIME_TYPE)
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
					Calendar date = Calendar.getInstance();//gives current date at given time
					date.set(Calendar.YEAR, 2012 );
				    date.set(Calendar.MONTH, Calendar.JANUARY );
				    date.set(Calendar.DATE, 01 );
				    
				    Calendar dateB = Calendar.getInstance();
					dateB.set(Calendar.YEAR, 2012 );
				    dateB.set(Calendar.MONTH, Calendar.DECEMBER );
				    dateB.set(Calendar.DATE, 31 );
					manager.getCrimesByDate(new java.sql.Date(date.getTimeInMillis()), new java.sql.Date(dateB.getTimeInMillis()), new OnDBGetListener<CrimeData>() {
						@Override
						public void OnGet(List<CrimeData> list1) {
							for(int i = 0;i< list1.size();i++){
								Log.w(DISPLAY_SERVICE, "" + (list1.get(i)));
								
								
							}
						}
					});
				}
			});
		}
		
		b.show();
	}

}
