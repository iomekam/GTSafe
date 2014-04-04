package com.example.gtsafe;


import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
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
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;
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
	CustomSpinner searchCrimes2;
	CharSequence result;
	ArrayAdapter<CrimeData> adapter;
	Search currentSelection = Search.CRIME_TYPE;
	Object selectedItem = null;
	ListView crimes;
	String toc;
	String toc2;
	private TextView view;
	private TextView view2;

	XYSeries series1;
	XYSeries series2;
	String bla;
	Number[] series1Numbers = {};
	Number[] series2Numbers = {};
	final String[] xLabels = {"Jan", "Feb", "Mar", "Apr", "May","June", "July","Aug","Sept","Oct","Nov","Dec"};
	int JanL = 0,FebL = 0,MarL = 0,AprilL = 0,MayL = 0,JuneL = 0,JulyL = 0,AugL = 0,SeptL = 0,OctL = 0,NovL = 0,DecL = 0;
	int JanR = 0,FebR = 0,MarR = 0,AprilR = 0,MayR = 0,JuneR = 0,JulyR = 0,AugR = 0,SeptR = 0,OctR = 0,NovR = 0,DecR = 0;



	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crime_stat);
		
		adapter = new ArrayAdapter<CrimeData>(CrimeStatActivity.this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				new LinkedList<CrimeData>());
		
		crimes = (ListView) findViewById(R.id.crimeLog);
		searchCrimes = (CustomSpinner) findViewById(R.id.filterCrime);
		searchCrimes2 = (CustomSpinner) findViewById(R.id.filterCrime2);
		
		view =  (TextView)findViewById(R.id.textView1);
		view.setMovementMethod(new ScrollingMovementMethod());
		view2 =  (TextView)findViewById(R.id.textView1);
		view2.setMovementMethod(new ScrollingMovementMethod());
		
		plot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
		
		
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
				else if (type == Search.CRIME_TYPE)
				{
					//Where the list of different crimes comes from
					showPopUp(type.toString(), type.getContent(), type);
					searchCrimes.setSelection(-1);
					
					
				}
			}
			
		
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
			
		});
		
		searchCrimes2.setAdapter(spinnerArrayAdapter);
		searchCrimes2.setOnItemSelectedListener(new OnItemSelectedListener()
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
				else if (type == Search.CRIME_TYPE)
				{
					//Where the list of different crimes comes from
					showPopUp2(type.toString(), type.getContent(), type);
					searchCrimes2.setSelection(-1);
					
					
				}
			}
			
		
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
			
		});
		
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
        Calendar date = Calendar.getInstance();//gives current date at given time
		date.set(Calendar.YEAR, 2012 );
	    date.set(Calendar.MONTH, Calendar.JANUARY );
	    date.set(Calendar.DATE, 01 );
	    
	    Calendar dateB = Calendar.getInstance();
		dateB.set(Calendar.YEAR, 2013 );
	    dateB.set(Calendar.MONTH, Calendar.DECEMBER );
	    dateB.set(Calendar.DATE, 31 );
	    
	    final Context context = this;
	    
		manager.getCrimesByDate(new java.sql.Date(date.getTimeInMillis()), new java.sql.Date(dateB.getTimeInMillis()), new OnDBGetListener<CrimeData>() {
			@SuppressWarnings("deprecation")
			@Override
			public void OnGet(List<CrimeData> list1) {
				
				for(int i = 0;i< list1.size();i++){
					//Log.d(DISPLAY_SERVICE, "" + (list1.get(i)));
					if (list1.get(i).getDate().getMonth() == 1 && list1.get(i).getOffense() == OffenseType.LARCENY){
						JanL++;
					}
					else if (list1.get(i).getDate().getMonth() == 2 && list1.get(i).getOffense() == OffenseType.LARCENY){
						FebL++;
					}
					else if (list1.get(i).getDate().getMonth() == 3 && list1.get(i).getOffense() == OffenseType.LARCENY){
						MarL++;
					}
					else if (list1.get(i).getDate().getMonth() == 4 && list1.get(i).getOffense() == OffenseType.LARCENY){
						AprilL++;
					}
					else if (list1.get(i).getDate().getMonth() == 5 && list1.get(i).getOffense() == OffenseType.LARCENY){
						MayL++;
					}
					else if (list1.get(i).getDate().getMonth() == 6 && list1.get(i).getOffense() == OffenseType.LARCENY){
						JuneL++;
					}
					else if (list1.get(i).getDate().getMonth() == 7 && list1.get(i).getOffense() == OffenseType.LARCENY){
						JulyL++;
					}
					else if (list1.get(i).getDate().getMonth() == 8 && list1.get(i).getOffense() == OffenseType.LARCENY){
						AugL++;
					}
					else if (list1.get(i).getDate().getMonth() == 9 && list1.get(i).getOffense() == OffenseType.LARCENY){
						SeptL++;
					}
					else if (list1.get(i).getDate().getMonth() == 10 && list1.get(i).getOffense() == OffenseType.LARCENY){
						OctL++;
					}
					else if (list1.get(i).getDate().getMonth() == 11 && list1.get(i).getOffense() == OffenseType.LARCENY){
						NovL++;
					}
					else if (list1.get(i).getDate().getMonth() == 12 && list1.get(i).getOffense() == OffenseType.LARCENY){
						DecL++;
					}
					else if (list1.get(i).getDate().getMonth() == 1 && list1.get(i).getOffense() == OffenseType.ROBBERY){
						JanR++;
					}
					else if (list1.get(i).getDate().getMonth() == 2 && list1.get(i).getOffense() == OffenseType.ROBBERY){
						FebR++;
					}
					else if (list1.get(i).getDate().getMonth() == 3 && list1.get(i).getOffense() == OffenseType.ROBBERY){
						MarR++;
					}
					else if (list1.get(i).getDate().getMonth() == 4 && list1.get(i).getOffense()== OffenseType.ROBBERY){
						AprilR++;
					}
					else if (list1.get(i).getDate().getMonth() == 5 && list1.get(i).getOffense()== OffenseType.ROBBERY){
						MayR++;
					}
					else if (list1.get(i).getDate().getMonth() == 6 && list1.get(i).getOffense()== OffenseType.ROBBERY){
						JuneR++;
					}
					else if (list1.get(i).getDate().getMonth() == 7 && list1.get(i).getOffense()== OffenseType.ROBBERY){
						JulyR++;
					}
					else if (list1.get(i).getDate().getMonth() == 8 && list1.get(i).getOffense()== OffenseType.ROBBERY){
						AugR++;
					}
					else if (list1.get(i).getDate().getMonth() == 9 && list1.get(i).getOffense()== OffenseType.ROBBERY){
						SeptR++;
					}
					else if (list1.get(i).getDate().getMonth() == 10 && list1.get(i).getOffense()== OffenseType.ROBBERY){
						OctR++;
					}
					else if (list1.get(i).getDate().getMonth() == 11 && list1.get(i).getOffense()== OffenseType.ROBBERY){
						NovR++;
					}
					else if (list1.get(i).getDate().getMonth() == 12 && list1.get(i).getOffense()== OffenseType.ROBBERY){
						DecR++;
					}
					//Log.i(DISPLAY_SERVICE, "" + (JanL+FebL+MarL+AprilL+MayL+JuneL+JulyL+AugL+SeptL+OctL+NovL+DecL));
					//Log.d(DISPLAY_SERVICE, "" + (JanR+FebR+MarR+AprilR+MayR+JuneR+JulyR+AugR+SeptR+OctR+NovR+DecR));
					
				}
				
				bla = "" +  list1.size();//+FebL+MarL+AprilL+MayL+JuneL+JulyL+AugL+SeptL+OctL+NovL+DecL;
				
				CharSequence text = "" + bla;
				int duration = Toast.LENGTH_LONG;

				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
				drawGraph();
			}
		});
		
        	
 
 
        
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
					if (selectedItem == OffenseType.LARCENY){
						plot.clear();// why does this make my app crash
						series1Numbers = new Number[]{ JanL, FebL, MarL, AprilL, MayL, JuneL,JulyL, AugL, SeptL, OctL, NovL, DecL };
						/*series2Numbers = new Number[]{ JanR, FebR, MarR, AprilR, MayR, JuneR,
								JulyR, AugR, SeptR, OctR, NovR, DecR };*/
						//JanL = 5;
						//FebL = 7;
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.ARSON){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.AGG_ASSAULT){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.ALL_OTHER_OFFENSES){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.AUTO_THEFT){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.DAMAGE_TO_PROPERTY){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.BURGLARY){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.DANGEROUS_DRUGS){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.DUI){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.EMBEZZLEMENT){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.FAMILY_OFFENSE){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.FORGERY){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.FRAUD){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.GAMBLING_OFFENSE){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.HOMICIDE){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.LIQUOR_LAWS){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.NO_CRIME){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.NON_CRIME){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.OTHER_ASSAULTS){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.PROSTITUTION){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.RAPE){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.PUBLIC_PEACE_OFFENSE){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.ROBBERY){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.SEX_OFFENSE){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.STOLEN_PROPERTY){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.VAGRANCY_OFFENSE){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.WEAPONS_OFFENSE){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
				}
			});
		}
		
		
		b.show();
		// initialize our XYPlot reference:
        
      
	}
	public void showPopUp2(String title, final String[] options, Search type) {
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
					if (selectedItem == OffenseType.LARCENY){
						//plot.clear();// why does this make my app crash
						/*series1Numbers = new Number[]{ JanL, FebL, MarL, AprilL, MayL, JuneL,JulyL, AugL, SeptL, OctL, NovL, DecL };*/
						
						//JanL = 5;
						//FebL = 7;
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.ARSON){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.AGG_ASSAULT){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.ALL_OTHER_OFFENSES){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.AUTO_THEFT){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.DAMAGE_TO_PROPERTY){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.BURGLARY){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.DANGEROUS_DRUGS){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.DUI){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.EMBEZZLEMENT){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.FAMILY_OFFENSE){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.FORGERY){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.FRAUD){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.GAMBLING_OFFENSE){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.HOMICIDE){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.LIQUOR_LAWS){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.NO_CRIME){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.NON_CRIME){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.OTHER_ASSAULTS){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.PROSTITUTION){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.RAPE){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.PUBLIC_PEACE_OFFENSE){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.ROBBERY){
						plot.clear();
						series2Numbers = new Number[]{ JanR, FebR, MarR, AprilR, MayR, JuneR,
								JulyR, AugR, SeptR, OctR, NovR, DecR };
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.SEX_OFFENSE){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.STOLEN_PROPERTY){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.VAGRANCY_OFFENSE){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.WEAPONS_OFFENSE){
						plot.clear();
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
				}
			});
		}
		
		
		b.show();
		// initialize our XYPlot reference:
        
      
	}
	class GraphXLabelFormat extends Format {

	    @Override
	    public StringBuffer format(Object arg0, StringBuffer arg1, FieldPosition arg2) {
	        // TODO Auto-generated method stub

	        int parsedInt = Math.round(Float.parseFloat(arg0.toString()));
	        Log.d("test", parsedInt + " " + arg1 + " " + arg2);
	        String labelString = xLabels[parsedInt];
	        arg1.append(labelString);
	        return arg1;
	    }

	    @Override
	    public Object parseObject(String arg0, ParsePosition arg1) {
	        // TODO Auto-generated method stub
	        return java.util.Arrays.asList(xLabels).indexOf(arg0);
	    }
	}
	void drawGraph(){
		
		 //plot = (MultitouchPlot) findViewById(R.id.mySimpleXYPlot);
		//plot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
	 
		// Create a couple arrays of y-values to plot:
		/*series1Numbers = {};
		series2Numbers = {};*/ 
		Log.e("g","g");
		plot.setTicksPerRangeLabel(3);
		plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);

		//plot.setTicksPerDomainLabel(12);
		plot.setDomainLabel("Month");
		plot.setRangeLabel("Occurances");
		plot.getGraphWidget().setDomainValueFormat(new GraphXLabelFormat());

		// Turn the above arrays into XYSeries':
		series1 = new SimpleXYSeries(Arrays.asList(series1Numbers), 
				// SimpleXYSeries takes a List so turn our array into a List
				SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use
														// the element index as
														// the x value
				"" + toc);
		// same as above
		series2 = new SimpleXYSeries(Arrays.asList(series2Numbers),
				SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "" + toc2);

		// Create a formatter to use for drawing a series using
		// LineAndPointRenderer
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

		/*
		 * manager.getCrimesByDate(d1, d2,new OnDBGetListener<CrimeData>(){
		 * 
		 * @Override public void OnGet(List<CrimeData> list) {
		 * crimeData.addAll(list); } });
		 */
		/*
		 * final DBManager db = DBManager.getInstance();
		 * 
		 * db.getCrimesByType(OffenseType.NON_CRIME, new
		 * OnDBGetListener<CrimeData>(){
		 * 
		 * @Override public void OnGet(List<CrimeData> list) {
		 * //view.setText("Crimes by Type: " + list.size());
		 * //Toast.makeText(getApplicationContext(), "Crimes Length: " +
		 * list.size(), //Toast.LENGTH_LONG).show(); } });
		 */
		// fun little snippet that prevents users from taking screenshots
		// on ICS+ devices :-)
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
				WindowManager.LayoutParams.FLAG_SECURE);

	}

}
