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
	int JanL = 0,FebL = 0,MarL = 0,AprilL = 0,MayL = 0,JuneL = 0,JulyL = 0,AugL = 0,SeptL = 0,OctL = 0,NovL = 0,DecL = 0;//larceny count
	int JanR = 0,FebR = 0,MarR = 0,AprilR = 0,MayR = 0,JuneR = 0,JulyR = 0,AugR = 0,SeptR = 0,OctR = 0,NovR = 0,DecR = 0;//roberry count
	int JanH = 0,FebH = 0,MarH = 0,AprilH = 0,MayH = 0,JuneH = 0,JulyH = 0,AugH = 0,SeptH = 0,OctH = 0,NovH = 0,DecH = 0;//homocide count
	int JanRA = 0,FebRA = 0,MarRA = 0,AprilRA = 0,MayRA = 0,JuneRA = 0,JulyRA = 0,AugRA = 0,SeptRA = 0,OctRA = 0,NovRA = 0,DecRA = 0;//rape count
	int JanAA = 0,FebAA = 0,MarAA = 0,AprilAA = 0,MayAA = 0,JuneAA = 0,JulyAA = 0,AugAA = 0,SeptAA = 0,OctAA = 0,NovAA = 0,DecAA = 0;//agg assault count
	int JanB = 0,FebB = 0,MarB = 0,AprilB = 0,MayB = 0,JuneB = 0,JulyB = 0,AugB = 0,SeptB = 0,OctB = 0,NovB = 0,DecB = 0;//burglary count
	int JanAT = 0,FebAT = 0,MarAT = 0,AprilAT = 0,MayAT = 0,JuneAT = 0,JulyAT = 0,AugAT = 0,SeptAT = 0,OctAT = 0,NovAT = 0,DecAT = 0;//auto theft count
	int JanA = 0,FebA = 0,MarA = 0,AprilA = 0,MayA = 0,JuneA = 0,JulyA = 0,AugA = 0,SeptA = 0,OctA = 0,NovA = 0,DecA = 0;//arson count
	int JanOA = 0,FebOA = 0,MarOA = 0,AprilOA = 0,MayOA = 0,JuneOA = 0,JulyOA = 0,AugOA = 0,SeptOA = 0,OctOA = 0,NovOA = 0,DecOA = 0;//other assault count
	int JanF = 0,FebF = 0,MarF = 0,AprilF = 0,MayF = 0,JuneF = 0,JulyF = 0,AugF = 0,SeptF = 0,OctF = 0,NovF = 0,DecF = 0;//forgery count
	int JanFR = 0,FebFR = 0,MarFR = 0,AprilFR = 0,MayFR = 0,JuneFR = 0,JulyFR = 0,AugFR = 0,SeptFR = 0,OctFR = 0,NovFR = 0,DecFR = 0;//fraud count
	int JanE = 0,FebE = 0,MarE = 0,AprilE = 0,MayE = 0,JuneE = 0,JulyE = 0,AugE = 0,SeptE = 0,OctE = 0,NovE = 0,DecE = 0;//embeezz count
	int JanSP = 0,FebSP = 0,MarSP = 0,AprilSP = 0,MaySP = 0,JuneSP = 0,JulySP = 0,AugSP = 0,SeptSP = 0,OctSP = 0,NovSP = 0,DecSP = 0;//stolen prop count
	int JanDP = 0,FebDP = 0,MarDP = 0,AprilDP = 0,MayDP = 0,JuneDP = 0,JulyDP = 0,AugDP = 0,SeptDP = 0,OctDP = 0,NovDP = 0,DecDP = 0;// damaage to property
	int JanWO = 0,FebWO = 0,MarWO = 0,AprilWO = 0,MayWO = 0,JuneWO = 0,JulyWO = 0,AugWO= 0,SeptWO = 0,OctWO = 0,NovWO = 0,DecWO = 0;//weapon offense count
	int JanP = 0,FebP = 0,MarP = 0,AprilP = 0,MayP = 0,JuneP = 0,JulyP = 0,AugP = 0,SeptP = 0,OctP = 0,NovP = 0,DecP = 0;//prostitution count
	int JanSO = 0,FebSO = 0,MarSO = 0,AprilSO = 0,MaySO = 0,JuneSO = 0,JulySO = 0,AugSO = 0,SeptSO = 0,OctSO = 0,NovSO = 0,DecSO = 0;//sexual offense
	int JanDD = 0,FebDD = 0,MarDD = 0,AprilDD = 0,MayDD = 0,JuneDD = 0,JulyDD = 0,AugDD = 0,SeptDD = 0,OctDD = 0,NovDD = 0,DecDD = 0;//dangerous drug count
	int JanGO = 0,FebGO = 0,MarGO = 0,AprilGO = 0,MayGO = 0,JuneGO = 0,JulyGO = 0,AugGO = 0,SeptGO = 0,OctGO = 0,NovGO = 0,DecGO = 0;// gamb offense count
	int JanFO = 0,FebFO = 0,MarFO = 0,AprilFO = 0,MayFO = 0,JuneFO = 0,JulyFO = 0,AugFO = 0,SeptFO = 0,OctFO = 0,NovFO = 0,DecFO = 0;//family offense count
	int JanD = 0,FebD = 0,MarD = 0,AprilD = 0,MayD = 0,JuneD = 0,JulyD = 0,AugD = 0,SeptD = 0,OctD = 0,NovD = 0,DecD = 0;//dui count
	int JanLL = 0,FebLL = 0,MarLL = 0,AprilLL = 0,MayLL = 0,JuneLL = 0,JulyLL = 0,AugLL = 0,SeptLL = 0,OctLL = 0,NovLL= 0,DecLL = 0;//liquor laws
	int JanPP = 0,FebPP = 0,MarPP = 0,AprilPP = 0,MayPP = 0,JunePP = 0,JulyPP = 0,AugPP = 0,SeptPP = 0,OctPP = 0,NovPP = 0,DecPP = 0;//public peace offense count
	int JanVO = 0,FebVO = 0,MarVO = 0,AprilVO = 0,MayVO = 0,JuneVO = 0,JulyVO = 0,AugVO = 0,SeptVO = 0,OctVO = 0,NovVO = 0,DecVO = 0;//vagrancy offense count
	int JanAO = 0,FebAO = 0,MarAO = 0,AprilAO = 0,MayAO = 0,JuneAO = 0,JulyAO = 0,AugAO = 0,SeptAO = 0,OctAO = 0,NovAO = 0,DecAO = 0;//all other offenses count
	int JanNC = 0,FebNC = 0,MarNC = 0,AprilNC = 0,MayNC = 0,JuneNC = 0,JulyNC = 0,AugNC = 0,SeptNC = 0,OctNC = 0,NovNC = 0,DecNC = 0;//no_crime count
	int JanNCR = 0,FebNCR = 0,MarNCR = 0,AprilNCR = 0,MayNCR = 0,JuneNCR = 0,JulyNCR = 0,AugNCR = 0,SeptNCR = 0,OctNCR = 0,NovNCR = 0,DecNCR = 0;//non crime
	
	
	
	
	



	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crime_stat);
		if(getActionBar()!= null){
			getActionBar().setTitle("Crime Stats");
		}
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
					else if (list1.get(i).getDate().getMonth() == 1 && list1.get(i).getOffense() == OffenseType.HOMICIDE){
						JanH++;
					}
					else if (list1.get(i).getDate().getMonth() == 2 && list1.get(i).getOffense() == OffenseType.HOMICIDE){
						FebH++;
					}
					else if (list1.get(i).getDate().getMonth() == 3 && list1.get(i).getOffense() == OffenseType.HOMICIDE){
						MarH++;
					}
					else if (list1.get(i).getDate().getMonth() == 4 && list1.get(i).getOffense()== OffenseType.HOMICIDE){
						AprilH++;
					}
					else if (list1.get(i).getDate().getMonth() == 5 && list1.get(i).getOffense()== OffenseType.ROBBERY){
						MayH++;
					}
					else if (list1.get(i).getDate().getMonth() == 6 && list1.get(i).getOffense()== OffenseType.HOMICIDE){
						JuneH++;
					}
					else if (list1.get(i).getDate().getMonth() == 7 && list1.get(i).getOffense()== OffenseType.HOMICIDE){
						JulyH++;
					}
					else if (list1.get(i).getDate().getMonth() == 8 && list1.get(i).getOffense()== OffenseType.HOMICIDE){
						AugH++;
					}
					else if (list1.get(i).getDate().getMonth() == 9 && list1.get(i).getOffense()== OffenseType.HOMICIDE){
						SeptH++;
					}
					else if (list1.get(i).getDate().getMonth() == 10 && list1.get(i).getOffense()== OffenseType.HOMICIDE){
						OctH++;
					}
					else if (list1.get(i).getDate().getMonth() == 11 && list1.get(i).getOffense()== OffenseType.HOMICIDE){
						NovH++;
					}
					else if (list1.get(i).getDate().getMonth() == 12 && list1.get(i).getOffense()== OffenseType.HOMICIDE){
						DecH++;
					}
					else if (list1.get(i).getDate().getMonth() == 1 && list1.get(i).getOffense() == OffenseType.RAPE){
						JanRA++;
					}
					else if (list1.get(i).getDate().getMonth() == 2 && list1.get(i).getOffense() == OffenseType.RAPE){
						FebRA++;
					}
					else if (list1.get(i).getDate().getMonth() == 3 && list1.get(i).getOffense() == OffenseType.RAPE){
						MarRA++;
					}
					else if (list1.get(i).getDate().getMonth() == 4 && list1.get(i).getOffense()== OffenseType.RAPE){
						AprilRA++;
					}
					else if (list1.get(i).getDate().getMonth() == 5 && list1.get(i).getOffense()== OffenseType.RAPE){
						MayRA++;
					}
					else if (list1.get(i).getDate().getMonth() == 6 && list1.get(i).getOffense()== OffenseType.RAPE){
						JuneRA++;
					}
					else if (list1.get(i).getDate().getMonth() == 7 && list1.get(i).getOffense()== OffenseType.RAPE){
						JulyRA++;
					}
					else if (list1.get(i).getDate().getMonth() == 8 && list1.get(i).getOffense()== OffenseType.RAPE){
						AugRA++;
					}
					else if (list1.get(i).getDate().getMonth() == 9 && list1.get(i).getOffense()== OffenseType.RAPE){
						SeptRA++;
					}
					else if (list1.get(i).getDate().getMonth() == 10 && list1.get(i).getOffense()== OffenseType.RAPE){
						OctRA++;
					}
					else if (list1.get(i).getDate().getMonth() == 11 && list1.get(i).getOffense()== OffenseType.RAPE){
						NovRA++;
					}
					else if (list1.get(i).getDate().getMonth() == 12 && list1.get(i).getOffense()== OffenseType.RAPE){
						DecRA++;
					}
					else if (list1.get(i).getDate().getMonth() == 1 && list1.get(i).getOffense() == OffenseType.AGG_ASSAULT){
						JanAA++;
					}
					else if (list1.get(i).getDate().getMonth() == 2 && list1.get(i).getOffense() == OffenseType.AGG_ASSAULT){
						FebAA++;
					}
					else if (list1.get(i).getDate().getMonth() == 3 && list1.get(i).getOffense() == OffenseType.AGG_ASSAULT){
						MarAA++;
					}
					else if (list1.get(i).getDate().getMonth() == 4 && list1.get(i).getOffense()== OffenseType.AGG_ASSAULT){
						AprilAA++;
					}
					else if (list1.get(i).getDate().getMonth() == 5 && list1.get(i).getOffense()== OffenseType.AGG_ASSAULT){
						MayAA++;
					}
					else if (list1.get(i).getDate().getMonth() == 6 && list1.get(i).getOffense()== OffenseType.AGG_ASSAULT){
						JuneAA++;
					}
					else if (list1.get(i).getDate().getMonth() == 7 && list1.get(i).getOffense()== OffenseType.AGG_ASSAULT){
						JulyAA++;
					}
					else if (list1.get(i).getDate().getMonth() == 8 && list1.get(i).getOffense()== OffenseType.AGG_ASSAULT){
						AugAA++;
					}
					else if (list1.get(i).getDate().getMonth() == 9 && list1.get(i).getOffense()== OffenseType.AGG_ASSAULT){
						SeptAA++;
					}
					else if (list1.get(i).getDate().getMonth() == 10 && list1.get(i).getOffense()== OffenseType.AGG_ASSAULT){
						OctAA++;
					}
					else if (list1.get(i).getDate().getMonth() == 11 && list1.get(i).getOffense()== OffenseType.AGG_ASSAULT){
						NovAA++;
					}
					else if (list1.get(i).getDate().getMonth() == 12 && list1.get(i).getOffense()== OffenseType.AGG_ASSAULT){
						DecAA++;
					}
					else if (list1.get(i).getDate().getMonth() == 1 && list1.get(i).getOffense() == OffenseType.ALL_OTHER_OFFENSES){
						JanAO++;
					}
					else if (list1.get(i).getDate().getMonth() == 2 && list1.get(i).getOffense() == OffenseType.ALL_OTHER_OFFENSES){
						FebAO++;
					}
					else if (list1.get(i).getDate().getMonth() == 3 && list1.get(i).getOffense() == OffenseType.ALL_OTHER_OFFENSES){
						MarAO++;
					}
					else if (list1.get(i).getDate().getMonth() == 4 && list1.get(i).getOffense()== OffenseType.ALL_OTHER_OFFENSES){
						AprilAO++;
					}
					else if (list1.get(i).getDate().getMonth() == 5 && list1.get(i).getOffense()== OffenseType.ALL_OTHER_OFFENSES){
						MayAO++;
					}
					else if (list1.get(i).getDate().getMonth() == 6 && list1.get(i).getOffense()== OffenseType.ALL_OTHER_OFFENSES){
						JuneAO++;
					}
					else if (list1.get(i).getDate().getMonth() == 7 && list1.get(i).getOffense()== OffenseType.ALL_OTHER_OFFENSES){
						JulyAO++;
					}
					else if (list1.get(i).getDate().getMonth() == 8 && list1.get(i).getOffense()== OffenseType.ALL_OTHER_OFFENSES){
						AugAO++;
					}
					else if (list1.get(i).getDate().getMonth() == 9 && list1.get(i).getOffense()== OffenseType.ALL_OTHER_OFFENSES){
						SeptAO++;
					}
					else if (list1.get(i).getDate().getMonth() == 10 && list1.get(i).getOffense()== OffenseType.ALL_OTHER_OFFENSES){
						OctAO++;
					}
					else if (list1.get(i).getDate().getMonth() == 11 && list1.get(i).getOffense()== OffenseType.ALL_OTHER_OFFENSES){
						NovAO++;
					}
					else if (list1.get(i).getDate().getMonth() == 12 && list1.get(i).getOffense()== OffenseType.ALL_OTHER_OFFENSES){
						DecAO++;
					}
					else if (list1.get(i).getDate().getMonth() == 1 && list1.get(i).getOffense() == OffenseType.ARSON){
						JanA++;
					}
					else if (list1.get(i).getDate().getMonth() == 2 && list1.get(i).getOffense() == OffenseType.ARSON){
						FebA++;
					}
					else if (list1.get(i).getDate().getMonth() == 3 && list1.get(i).getOffense() == OffenseType.ARSON){
						MarA++;
					}
					else if (list1.get(i).getDate().getMonth() == 4 && list1.get(i).getOffense()== OffenseType.ARSON){
						AprilA++;
					}
					else if (list1.get(i).getDate().getMonth() == 5 && list1.get(i).getOffense()== OffenseType.ARSON){
						MayA++;
					}
					else if (list1.get(i).getDate().getMonth() == 6 && list1.get(i).getOffense()== OffenseType.ARSON){
						JuneA++;
					}
					else if (list1.get(i).getDate().getMonth() == 7 && list1.get(i).getOffense()== OffenseType.ARSON){
						JulyA++;
					}
					else if (list1.get(i).getDate().getMonth() == 8 && list1.get(i).getOffense()== OffenseType.ARSON){
						AugA++;
					}
					else if (list1.get(i).getDate().getMonth() == 9 && list1.get(i).getOffense()== OffenseType.ARSON){
						SeptA++;
					}
					else if (list1.get(i).getDate().getMonth() == 10 && list1.get(i).getOffense()== OffenseType.ARSON){
						OctA++;
					}
					else if (list1.get(i).getDate().getMonth() == 11 && list1.get(i).getOffense()== OffenseType.ARSON){
						NovA++;
					}
					else if (list1.get(i).getDate().getMonth() == 12 && list1.get(i).getOffense()== OffenseType.ARSON){
						DecA++;
					}
					else if (list1.get(i).getDate().getMonth() == 1 && list1.get(i).getOffense() == OffenseType.AUTO_THEFT){
						JanAT++;
					}
					else if (list1.get(i).getDate().getMonth() == 2 && list1.get(i).getOffense() == OffenseType.AUTO_THEFT){
						FebAT++;
					}
					else if (list1.get(i).getDate().getMonth() == 3 && list1.get(i).getOffense() == OffenseType.AUTO_THEFT){
						MarAT++;
					}
					else if (list1.get(i).getDate().getMonth() == 4 && list1.get(i).getOffense()== OffenseType.AUTO_THEFT){
						AprilAT++;
					}
					else if (list1.get(i).getDate().getMonth() == 5 && list1.get(i).getOffense()== OffenseType.AUTO_THEFT){
						MayAT++;
					}
					else if (list1.get(i).getDate().getMonth() == 6 && list1.get(i).getOffense()== OffenseType.AUTO_THEFT){
						JuneAT++;
					}
					else if (list1.get(i).getDate().getMonth() == 7 && list1.get(i).getOffense()== OffenseType.AUTO_THEFT){
						JulyAT++;
					}
					else if (list1.get(i).getDate().getMonth() == 8 && list1.get(i).getOffense()== OffenseType.AUTO_THEFT){
						AugAT++;
					}
					else if (list1.get(i).getDate().getMonth() == 9 && list1.get(i).getOffense()== OffenseType.AUTO_THEFT){
						SeptAT++;
					}
					else if (list1.get(i).getDate().getMonth() == 10 && list1.get(i).getOffense()== OffenseType.AUTO_THEFT){
						OctAT++;
					}
					else if (list1.get(i).getDate().getMonth() == 11 && list1.get(i).getOffense()== OffenseType.AUTO_THEFT){
						NovAT++;
					}
					else if (list1.get(i).getDate().getMonth() == 12 && list1.get(i).getOffense()== OffenseType.AUTO_THEFT){
						DecAT++;
					}
					else if (list1.get(i).getDate().getMonth() == 1 && list1.get(i).getOffense() == OffenseType.BURGLARY){
						JanB++;
					}
					else if (list1.get(i).getDate().getMonth() == 2 && list1.get(i).getOffense() == OffenseType.BURGLARY){
						FebB++;
					}
					else if (list1.get(i).getDate().getMonth() == 3 && list1.get(i).getOffense() == OffenseType.BURGLARY){
						MarB++;
					}
					else if (list1.get(i).getDate().getMonth() == 4 && list1.get(i).getOffense()== OffenseType.BURGLARY){
						AprilB++;
					}
					else if (list1.get(i).getDate().getMonth() == 5 && list1.get(i).getOffense()== OffenseType.BURGLARY){
						MayB++;
					}
					else if (list1.get(i).getDate().getMonth() == 6 && list1.get(i).getOffense()== OffenseType.BURGLARY){
						JuneB++;
					}
					else if (list1.get(i).getDate().getMonth() == 7 && list1.get(i).getOffense()== OffenseType.BURGLARY){
						JulyB++;
					}
					else if (list1.get(i).getDate().getMonth() == 8 && list1.get(i).getOffense()== OffenseType.BURGLARY){
						AugB++;
					}
					else if (list1.get(i).getDate().getMonth() == 9 && list1.get(i).getOffense()== OffenseType.BURGLARY){
						SeptB++;
					}
					else if (list1.get(i).getDate().getMonth() == 10 && list1.get(i).getOffense()== OffenseType.BURGLARY){
						OctB++;
					}
					else if (list1.get(i).getDate().getMonth() == 11 && list1.get(i).getOffense()== OffenseType.BURGLARY){
						NovB++;
					}
					else if (list1.get(i).getDate().getMonth() == 12 && list1.get(i).getOffense()== OffenseType.BURGLARY){
						DecB++;
					}
					else if (list1.get(i).getDate().getMonth() == 1 && list1.get(i).getOffense() == OffenseType.DAMAGE_TO_PROPERTY){
						JanDP++;
					}
					else if (list1.get(i).getDate().getMonth() == 2 && list1.get(i).getOffense() == OffenseType.DAMAGE_TO_PROPERTY){
						FebDP++;
					}
					else if (list1.get(i).getDate().getMonth() == 3 && list1.get(i).getOffense() == OffenseType.DAMAGE_TO_PROPERTY){
						MarDP++;
					}
					else if (list1.get(i).getDate().getMonth() == 4 && list1.get(i).getOffense()== OffenseType.DAMAGE_TO_PROPERTY){
						AprilDP++;
					}
					else if (list1.get(i).getDate().getMonth() == 5 && list1.get(i).getOffense()== OffenseType.DAMAGE_TO_PROPERTY){
						MayDP++;
					}
					else if (list1.get(i).getDate().getMonth() == 6 && list1.get(i).getOffense()== OffenseType.DAMAGE_TO_PROPERTY){
						JuneDP++;
					}
					else if (list1.get(i).getDate().getMonth() == 7 && list1.get(i).getOffense()== OffenseType.DAMAGE_TO_PROPERTY){
						JulyDP++;
					}
					else if (list1.get(i).getDate().getMonth() == 8 && list1.get(i).getOffense()== OffenseType.DAMAGE_TO_PROPERTY){
						AugDP++;
					}
					else if (list1.get(i).getDate().getMonth() == 9 && list1.get(i).getOffense()== OffenseType.DAMAGE_TO_PROPERTY){
						SeptDP++;
					}
					else if (list1.get(i).getDate().getMonth() == 10 && list1.get(i).getOffense()== OffenseType.DAMAGE_TO_PROPERTY){
						OctDP++;
					}
					else if (list1.get(i).getDate().getMonth() == 11 && list1.get(i).getOffense()== OffenseType.DAMAGE_TO_PROPERTY){
						NovDP++;
					}
					else if (list1.get(i).getDate().getMonth() == 12 && list1.get(i).getOffense()== OffenseType.DAMAGE_TO_PROPERTY){
						DecDP++;
					}
					else if (list1.get(i).getDate().getMonth() == 1 && list1.get(i).getOffense() == OffenseType.DANGEROUS_DRUGS){
						JanDD++;
					}
					else if (list1.get(i).getDate().getMonth() == 2 && list1.get(i).getOffense() == OffenseType.DANGEROUS_DRUGS){
						FebDD++;
					}
					else if (list1.get(i).getDate().getMonth() == 3 && list1.get(i).getOffense() == OffenseType.DANGEROUS_DRUGS){
						MarDD++;
					}
					else if (list1.get(i).getDate().getMonth() == 4 && list1.get(i).getOffense()== OffenseType.DANGEROUS_DRUGS){
						AprilDD++;
					}
					else if (list1.get(i).getDate().getMonth() == 5 && list1.get(i).getOffense()== OffenseType.DANGEROUS_DRUGS){
						MayDD++;
					}
					else if (list1.get(i).getDate().getMonth() == 6 && list1.get(i).getOffense()== OffenseType.DANGEROUS_DRUGS){
						JuneDD++;
					}
					else if (list1.get(i).getDate().getMonth() == 7 && list1.get(i).getOffense()== OffenseType.DANGEROUS_DRUGS){
						JulyDD++;
					}
					else if (list1.get(i).getDate().getMonth() == 8 && list1.get(i).getOffense()== OffenseType.DANGEROUS_DRUGS){
						AugDD++;
					}
					else if (list1.get(i).getDate().getMonth() == 9 && list1.get(i).getOffense()== OffenseType.DANGEROUS_DRUGS){
						SeptDD++;
					}
					else if (list1.get(i).getDate().getMonth() == 10 && list1.get(i).getOffense()== OffenseType.DANGEROUS_DRUGS){
						OctDD++;
					}
					else if (list1.get(i).getDate().getMonth() == 11 && list1.get(i).getOffense()== OffenseType.DANGEROUS_DRUGS){
						NovDD++;
					}
					else if (list1.get(i).getDate().getMonth() == 12 && list1.get(i).getOffense()== OffenseType.DANGEROUS_DRUGS){
						DecDD++;
					}
					else if (list1.get(i).getDate().getMonth() == 1 && list1.get(i).getOffense() == OffenseType.DUI){
						JanD++;
					}
					else if (list1.get(i).getDate().getMonth() == 2 && list1.get(i).getOffense() == OffenseType.DUI){
						FebD++;
					}
					else if (list1.get(i).getDate().getMonth() == 3 && list1.get(i).getOffense() == OffenseType.DUI){
						MarD++;
					}
					else if (list1.get(i).getDate().getMonth() == 4 && list1.get(i).getOffense()== OffenseType.DUI){
						AprilD++;
					}
					else if (list1.get(i).getDate().getMonth() == 5 && list1.get(i).getOffense()== OffenseType.DUI){
						MayD++;
					}
					else if (list1.get(i).getDate().getMonth() == 6 && list1.get(i).getOffense()== OffenseType.DUI){
						JuneD++;
					}
					else if (list1.get(i).getDate().getMonth() == 7 && list1.get(i).getOffense()== OffenseType.DUI){
						JulyD++;
					}
					else if (list1.get(i).getDate().getMonth() == 8 && list1.get(i).getOffense()== OffenseType.DUI){
						AugD++;
					}
					else if (list1.get(i).getDate().getMonth() == 9 && list1.get(i).getOffense()== OffenseType.DUI){
						SeptD++;
					}
					else if (list1.get(i).getDate().getMonth() == 10 && list1.get(i).getOffense()== OffenseType.DUI){
						OctD++;
					}
					else if (list1.get(i).getDate().getMonth() == 11 && list1.get(i).getOffense()== OffenseType.DUI){
						NovD++;
					}
					else if (list1.get(i).getDate().getMonth() == 12 && list1.get(i).getOffense()== OffenseType.DUI){
						DecD++;
					}
					else if (list1.get(i).getDate().getMonth() == 1 && list1.get(i).getOffense() == OffenseType.EMBEZZLEMENT){
						JanE++;
					}
					else if (list1.get(i).getDate().getMonth() == 2 && list1.get(i).getOffense() == OffenseType.EMBEZZLEMENT){
						FebE++;
					}
					else if (list1.get(i).getDate().getMonth() == 3 && list1.get(i).getOffense() == OffenseType.EMBEZZLEMENT){
						MarE++;
					}
					else if (list1.get(i).getDate().getMonth() == 4 && list1.get(i).getOffense()== OffenseType.EMBEZZLEMENT){
						AprilE++;
					}
					else if (list1.get(i).getDate().getMonth() == 5 && list1.get(i).getOffense()== OffenseType.EMBEZZLEMENT){
						MayE++;
					}
					else if (list1.get(i).getDate().getMonth() == 6 && list1.get(i).getOffense()== OffenseType.EMBEZZLEMENT){
						JuneE++;
					}
					else if (list1.get(i).getDate().getMonth() == 7 && list1.get(i).getOffense()== OffenseType.EMBEZZLEMENT){
						JulyE++;
					}
					else if (list1.get(i).getDate().getMonth() == 8 && list1.get(i).getOffense()== OffenseType.EMBEZZLEMENT){
						AugE++;
					}
					else if (list1.get(i).getDate().getMonth() == 9 && list1.get(i).getOffense()== OffenseType.EMBEZZLEMENT){
						SeptE++;
					}
					else if (list1.get(i).getDate().getMonth() == 10 && list1.get(i).getOffense()== OffenseType.EMBEZZLEMENT){
						OctE++;
					}
					else if (list1.get(i).getDate().getMonth() == 11 && list1.get(i).getOffense()== OffenseType.EMBEZZLEMENT){
						NovE++;
					}
					else if (list1.get(i).getDate().getMonth() == 12 && list1.get(i).getOffense()== OffenseType.EMBEZZLEMENT){
						DecE++;
					}
					else if (list1.get(i).getDate().getMonth() == 1 && list1.get(i).getOffense() == OffenseType.FAMILY_OFFENSE){
						JanFO++;
					}
					else if (list1.get(i).getDate().getMonth() == 2 && list1.get(i).getOffense() == OffenseType.FAMILY_OFFENSE){
						FebFO++;
					}
					else if (list1.get(i).getDate().getMonth() == 3 && list1.get(i).getOffense() == OffenseType.FAMILY_OFFENSE){
						MarFO++;
					}
					else if (list1.get(i).getDate().getMonth() == 4 && list1.get(i).getOffense()== OffenseType.FAMILY_OFFENSE){
						AprilFO++;
					}
					else if (list1.get(i).getDate().getMonth() == 5 && list1.get(i).getOffense()== OffenseType.FAMILY_OFFENSE){
						MayFO++;
					}
					else if (list1.get(i).getDate().getMonth() == 6 && list1.get(i).getOffense()== OffenseType.FAMILY_OFFENSE){
						JuneFO++;
					}
					else if (list1.get(i).getDate().getMonth() == 7 && list1.get(i).getOffense()== OffenseType.FAMILY_OFFENSE){
						JulyFO++;
					}
					else if (list1.get(i).getDate().getMonth() == 8 && list1.get(i).getOffense()== OffenseType.FAMILY_OFFENSE){
						AugFO++;
					}
					else if (list1.get(i).getDate().getMonth() == 9 && list1.get(i).getOffense()== OffenseType.FAMILY_OFFENSE){
						SeptFO++;
					}
					else if (list1.get(i).getDate().getMonth() == 10 && list1.get(i).getOffense()== OffenseType.FAMILY_OFFENSE){
						OctFO++;
					}
					else if (list1.get(i).getDate().getMonth() == 11 && list1.get(i).getOffense()== OffenseType.FAMILY_OFFENSE){
						NovFO++;
					}
					else if (list1.get(i).getDate().getMonth() == 12 && list1.get(i).getOffense()== OffenseType.FAMILY_OFFENSE){
						DecFO++;
					}
					
					else if (list1.get(i).getDate().getMonth() == 1 && list1.get(i).getOffense() == OffenseType.FORGERY){
						JanF++;
					}
					else if (list1.get(i).getDate().getMonth() == 2 && list1.get(i).getOffense() == OffenseType.FORGERY){
						FebF++;
					}
					else if (list1.get(i).getDate().getMonth() == 3 && list1.get(i).getOffense() == OffenseType.FORGERY){
						MarF++;
					}
					else if (list1.get(i).getDate().getMonth() == 4 && list1.get(i).getOffense()== OffenseType.FORGERY){
						AprilF++;
					}
					else if (list1.get(i).getDate().getMonth() == 5 && list1.get(i).getOffense()== OffenseType.FORGERY){
						MayF++;
					}
					else if (list1.get(i).getDate().getMonth() == 6 && list1.get(i).getOffense()== OffenseType.FORGERY){
						JuneF++;
					}
					else if (list1.get(i).getDate().getMonth() == 7 && list1.get(i).getOffense()== OffenseType.FORGERY){
						JulyF++;
					}
					else if (list1.get(i).getDate().getMonth() == 8 && list1.get(i).getOffense()== OffenseType.FORGERY){
						AugF++;
					}
					else if (list1.get(i).getDate().getMonth() == 9 && list1.get(i).getOffense()== OffenseType.FORGERY){
						SeptF++;
					}
					else if (list1.get(i).getDate().getMonth() == 10 && list1.get(i).getOffense()== OffenseType.FORGERY){
						OctF++;
					}
					else if (list1.get(i).getDate().getMonth() == 11 && list1.get(i).getOffense()== OffenseType.FORGERY){
						NovF++;
					}
					else if (list1.get(i).getDate().getMonth() == 12 && list1.get(i).getOffense()== OffenseType.FORGERY){
						DecF++;
					}
					else if (list1.get(i).getDate().getMonth() == 1 && list1.get(i).getOffense() == OffenseType.FRAUD){
						JanFR++;
					}
					else if (list1.get(i).getDate().getMonth() == 2 && list1.get(i).getOffense() == OffenseType.FRAUD){
						FebFR++;
					}
					else if (list1.get(i).getDate().getMonth() == 3 && list1.get(i).getOffense() == OffenseType.FRAUD){
						MarFR++;
					}
					else if (list1.get(i).getDate().getMonth() == 4 && list1.get(i).getOffense()== OffenseType.FRAUD){
						AprilFR++;
					}
					else if (list1.get(i).getDate().getMonth() == 5 && list1.get(i).getOffense()== OffenseType.FRAUD){
						MayFR++;
					}
					else if (list1.get(i).getDate().getMonth() == 6 && list1.get(i).getOffense()== OffenseType.FRAUD){
						JuneFR++;
					}
					else if (list1.get(i).getDate().getMonth() == 7 && list1.get(i).getOffense()== OffenseType.FRAUD){
						JulyFR++;
					}
					else if (list1.get(i).getDate().getMonth() == 8 && list1.get(i).getOffense()== OffenseType.FRAUD){
						AugFR++;
					}
					else if (list1.get(i).getDate().getMonth() == 9 && list1.get(i).getOffense()== OffenseType.FRAUD){
						SeptFR++;
					}
					else if (list1.get(i).getDate().getMonth() == 10 && list1.get(i).getOffense()== OffenseType.FRAUD){
						OctFR++;
					}
					else if (list1.get(i).getDate().getMonth() == 11 && list1.get(i).getOffense()== OffenseType.FRAUD){
						NovFR++;
					}
					else if (list1.get(i).getDate().getMonth() == 12 && list1.get(i).getOffense()== OffenseType.FRAUD){
						DecFR++;
					}else if (list1.get(i).getDate().getMonth() == 1 && list1.get(i).getOffense() == OffenseType.GAMBLING_OFFENSE){
						JanGO++;
					}
					else if (list1.get(i).getDate().getMonth() == 2 && list1.get(i).getOffense() == OffenseType.GAMBLING_OFFENSE){
						FebGO++;
					}
					else if (list1.get(i).getDate().getMonth() == 3 && list1.get(i).getOffense() == OffenseType.GAMBLING_OFFENSE){
						MarGO++;
					}
					else if (list1.get(i).getDate().getMonth() == 4 && list1.get(i).getOffense()== OffenseType.GAMBLING_OFFENSE){
						AprilGO++;
					}
					else if (list1.get(i).getDate().getMonth() == 5 && list1.get(i).getOffense()== OffenseType.GAMBLING_OFFENSE){
						MayGO++;
					}
					else if (list1.get(i).getDate().getMonth() == 6 && list1.get(i).getOffense()== OffenseType.GAMBLING_OFFENSE){
						JuneGO++;
					}
					else if (list1.get(i).getDate().getMonth() == 7 && list1.get(i).getOffense()== OffenseType.GAMBLING_OFFENSE){
						JulyGO++;
					}
					else if (list1.get(i).getDate().getMonth() == 8 && list1.get(i).getOffense()== OffenseType.GAMBLING_OFFENSE){
						AugGO++;
					}
					else if (list1.get(i).getDate().getMonth() == 9 && list1.get(i).getOffense()== OffenseType.GAMBLING_OFFENSE){
						SeptGO++;
					}
					else if (list1.get(i).getDate().getMonth() == 10 && list1.get(i).getOffense()== OffenseType.GAMBLING_OFFENSE){
						OctGO++;
					}
					else if (list1.get(i).getDate().getMonth() == 11 && list1.get(i).getOffense()== OffenseType.GAMBLING_OFFENSE){
						NovGO++;
					}
					else if (list1.get(i).getDate().getMonth() == 12 && list1.get(i).getOffense()== OffenseType.GAMBLING_OFFENSE){
						DecGO++;
					}
					else if (list1.get(i).getDate().getMonth() == 1 && list1.get(i).getOffense() == OffenseType.LIQUOR_LAWS){
						JanLL++;
					}
					else if (list1.get(i).getDate().getMonth() == 2 && list1.get(i).getOffense() == OffenseType.LIQUOR_LAWS){
						FebLL++;
					}
					else if (list1.get(i).getDate().getMonth() == 3 && list1.get(i).getOffense() == OffenseType.LIQUOR_LAWS){
						MarLL++;
					}
					else if (list1.get(i).getDate().getMonth() == 4 && list1.get(i).getOffense()== OffenseType.LIQUOR_LAWS){
						AprilLL++;
					}
					else if (list1.get(i).getDate().getMonth() == 5 && list1.get(i).getOffense()== OffenseType.LIQUOR_LAWS){
						MayLL++;
					}
					else if (list1.get(i).getDate().getMonth() == 6 && list1.get(i).getOffense()== OffenseType.LIQUOR_LAWS){
						JuneLL++;
					}
					else if (list1.get(i).getDate().getMonth() == 7 && list1.get(i).getOffense()== OffenseType.LIQUOR_LAWS){
						JulyLL++;
					}
					else if (list1.get(i).getDate().getMonth() == 8 && list1.get(i).getOffense()== OffenseType.LIQUOR_LAWS){
						AugLL++;
					}
					else if (list1.get(i).getDate().getMonth() == 9 && list1.get(i).getOffense()== OffenseType.LIQUOR_LAWS){
						SeptLL++;
					}
					else if (list1.get(i).getDate().getMonth() == 10 && list1.get(i).getOffense()== OffenseType.LIQUOR_LAWS){
						OctLL++;
					}
					else if (list1.get(i).getDate().getMonth() == 11 && list1.get(i).getOffense()== OffenseType.LIQUOR_LAWS){
						NovLL++;
					}
					else if (list1.get(i).getDate().getMonth() == 12 && list1.get(i).getOffense()== OffenseType.LIQUOR_LAWS){
						DecLL++;
					}
					else if (list1.get(i).getDate().getMonth() == 1 && list1.get(i).getOffense() == OffenseType.NO_CRIME){
						JanNC++;
					}
					else if (list1.get(i).getDate().getMonth() == 2 && list1.get(i).getOffense() == OffenseType.NO_CRIME){
						FebNC++;
					}
					else if (list1.get(i).getDate().getMonth() == 3 && list1.get(i).getOffense() == OffenseType.NO_CRIME){
						MarNC++;
					}
					else if (list1.get(i).getDate().getMonth() == 4 && list1.get(i).getOffense()== OffenseType.NO_CRIME){
						AprilNC++;
					}
					else if (list1.get(i).getDate().getMonth() == 5 && list1.get(i).getOffense()== OffenseType.NO_CRIME){
						MayNC++;
					}
					else if (list1.get(i).getDate().getMonth() == 6 && list1.get(i).getOffense()== OffenseType.NO_CRIME){
						JuneNC++;
					}
					else if (list1.get(i).getDate().getMonth() == 7 && list1.get(i).getOffense()== OffenseType.NO_CRIME){
						JulyNC++;
					}
					else if (list1.get(i).getDate().getMonth() == 8 && list1.get(i).getOffense()== OffenseType.NO_CRIME){
						AugNC++;
					}
					else if (list1.get(i).getDate().getMonth() == 9 && list1.get(i).getOffense()== OffenseType.NO_CRIME){
						SeptNC++;
					}
					else if (list1.get(i).getDate().getMonth() == 10 && list1.get(i).getOffense()== OffenseType.NO_CRIME){
						OctNC++;
					}
					else if (list1.get(i).getDate().getMonth() == 11 && list1.get(i).getOffense()== OffenseType.NO_CRIME){
						NovNC++;
					}
					else if (list1.get(i).getDate().getMonth() == 12 && list1.get(i).getOffense()== OffenseType.NO_CRIME){
						DecNC++;
					}
					else if (list1.get(i).getDate().getMonth() == 1 && list1.get(i).getOffense() == OffenseType.WEAPONS_OFFENSE){
						JanWO++;
					}
					else if (list1.get(i).getDate().getMonth() == 2 && list1.get(i).getOffense() == OffenseType.WEAPONS_OFFENSE){
						FebWO++;
					}
					else if (list1.get(i).getDate().getMonth() == 3 && list1.get(i).getOffense() == OffenseType.WEAPONS_OFFENSE){
						MarWO++;
					}
					else if (list1.get(i).getDate().getMonth() == 4 && list1.get(i).getOffense()== OffenseType.WEAPONS_OFFENSE){
						AprilWO++;
					}
					else if (list1.get(i).getDate().getMonth() == 5 && list1.get(i).getOffense()== OffenseType.WEAPONS_OFFENSE){
						MayWO++;
					}
					else if (list1.get(i).getDate().getMonth() == 6 && list1.get(i).getOffense()== OffenseType.WEAPONS_OFFENSE){
						JuneWO++;
					}
					else if (list1.get(i).getDate().getMonth() == 7 && list1.get(i).getOffense()== OffenseType.WEAPONS_OFFENSE){
						JulyWO++;
					}
					else if (list1.get(i).getDate().getMonth() == 8 && list1.get(i).getOffense()== OffenseType.WEAPONS_OFFENSE){
						AugWO++;
					}
					else if (list1.get(i).getDate().getMonth() == 9 && list1.get(i).getOffense()== OffenseType.WEAPONS_OFFENSE){
						SeptWO++;
					}
					else if (list1.get(i).getDate().getMonth() == 10 && list1.get(i).getOffense()== OffenseType.WEAPONS_OFFENSE){
						OctWO++;
					}
					else if (list1.get(i).getDate().getMonth() == 11 && list1.get(i).getOffense()== OffenseType.WEAPONS_OFFENSE){
						NovWO++;
					}
					else if (list1.get(i).getDate().getMonth() == 12 && list1.get(i).getOffense()== OffenseType.WEAPONS_OFFENSE){
						DecWO++;
					}
					else if (list1.get(i).getDate().getMonth() == 1 && list1.get(i).getOffense() == OffenseType.NON_CRIME){
						JanNCR++;
					}
					else if (list1.get(i).getDate().getMonth() == 2 && list1.get(i).getOffense() == OffenseType.NON_CRIME){
						FebNCR++;
					}
					else if (list1.get(i).getDate().getMonth() == 3 && list1.get(i).getOffense() == OffenseType.NON_CRIME){
						MarNCR++;
					}
					else if (list1.get(i).getDate().getMonth() == 4 && list1.get(i).getOffense()== OffenseType.NON_CRIME){
						AprilNCR++;
					}
					else if (list1.get(i).getDate().getMonth() == 5 && list1.get(i).getOffense()== OffenseType.NON_CRIME){
						MayNCR++;
					}
					else if (list1.get(i).getDate().getMonth() == 6 && list1.get(i).getOffense()== OffenseType.NON_CRIME){
						JuneNCR++;
					}
					else if (list1.get(i).getDate().getMonth() == 7 && list1.get(i).getOffense()== OffenseType.NON_CRIME){
						JulyNCR++;
					}
					else if (list1.get(i).getDate().getMonth() == 8 && list1.get(i).getOffense()== OffenseType.NON_CRIME){
						AugNCR++;
					}
					else if (list1.get(i).getDate().getMonth() == 9 && list1.get(i).getOffense()== OffenseType.NON_CRIME){
						SeptNCR++;
					}
					else if (list1.get(i).getDate().getMonth() == 10 && list1.get(i).getOffense()== OffenseType.NON_CRIME){
						OctNCR++;
					}
					else if (list1.get(i).getDate().getMonth() == 11 && list1.get(i).getOffense()== OffenseType.NON_CRIME){
						NovNCR++;
					}
					else if (list1.get(i).getDate().getMonth() == 12 && list1.get(i).getOffense()== OffenseType.NON_CRIME){
						DecNCR++;
					}
					else if (list1.get(i).getDate().getMonth() == 1 && list1.get(i).getOffense() == OffenseType.VAGRANCY_OFFENSE){
						JanVO++;
					}
					else if (list1.get(i).getDate().getMonth() == 2 && list1.get(i).getOffense() == OffenseType.VAGRANCY_OFFENSE){
						FebVO++;
					}
					else if (list1.get(i).getDate().getMonth() == 3 && list1.get(i).getOffense() == OffenseType.VAGRANCY_OFFENSE){
						MarVO++;
					}
					else if (list1.get(i).getDate().getMonth() == 4 && list1.get(i).getOffense()== OffenseType.VAGRANCY_OFFENSE){
						AprilVO++;
					}
					else if (list1.get(i).getDate().getMonth() == 5 && list1.get(i).getOffense()== OffenseType.VAGRANCY_OFFENSE){
						MayVO++;
					}
					else if (list1.get(i).getDate().getMonth() == 6 && list1.get(i).getOffense()== OffenseType.VAGRANCY_OFFENSE){
						JuneVO++;
					}
					else if (list1.get(i).getDate().getMonth() == 7 && list1.get(i).getOffense()== OffenseType.VAGRANCY_OFFENSE){
						JulyVO++;
					}
					else if (list1.get(i).getDate().getMonth() == 8 && list1.get(i).getOffense()== OffenseType.VAGRANCY_OFFENSE){
						AugVO++;
					}
					else if (list1.get(i).getDate().getMonth() == 9 && list1.get(i).getOffense()== OffenseType.VAGRANCY_OFFENSE){
						SeptVO++;
					}
					else if (list1.get(i).getDate().getMonth() == 10 && list1.get(i).getOffense()== OffenseType.VAGRANCY_OFFENSE){
						OctVO++;
					}
					else if (list1.get(i).getDate().getMonth() == 11 && list1.get(i).getOffense()== OffenseType.VAGRANCY_OFFENSE){
						NovVO++;
					}
					else if (list1.get(i).getDate().getMonth() == 12 && list1.get(i).getOffense()== OffenseType.VAGRANCY_OFFENSE){
						DecVO++;
					}
					else if (list1.get(i).getDate().getMonth() == 1 && list1.get(i).getOffense() == OffenseType.STOLEN_PROPERTY){
						JanSP++;
					}
					else if (list1.get(i).getDate().getMonth() == 2 && list1.get(i).getOffense() == OffenseType.STOLEN_PROPERTY){
						FebSP++;
					}
					else if (list1.get(i).getDate().getMonth() == 3 && list1.get(i).getOffense() == OffenseType.STOLEN_PROPERTY){
						MarSP++;
					}
					else if (list1.get(i).getDate().getMonth() == 4 && list1.get(i).getOffense()== OffenseType.STOLEN_PROPERTY){
						AprilSP++;
					}
					else if (list1.get(i).getDate().getMonth() == 5 && list1.get(i).getOffense()== OffenseType.STOLEN_PROPERTY){
						MaySP++;
					}
					else if (list1.get(i).getDate().getMonth() == 6 && list1.get(i).getOffense()== OffenseType.STOLEN_PROPERTY){
						JuneSP++;
					}
					else if (list1.get(i).getDate().getMonth() == 7 && list1.get(i).getOffense()== OffenseType.STOLEN_PROPERTY){
						JulySP++;
					}
					else if (list1.get(i).getDate().getMonth() == 8 && list1.get(i).getOffense()== OffenseType.STOLEN_PROPERTY){
						AugSP++;
					}
					else if (list1.get(i).getDate().getMonth() == 9 && list1.get(i).getOffense()== OffenseType.STOLEN_PROPERTY){
						SeptSP++;
					}
					else if (list1.get(i).getDate().getMonth() == 10 && list1.get(i).getOffense()== OffenseType.STOLEN_PROPERTY){
						OctSP++;
					}
					else if (list1.get(i).getDate().getMonth() == 11 && list1.get(i).getOffense()== OffenseType.STOLEN_PROPERTY){
						NovSP++;
					}
					else if (list1.get(i).getDate().getMonth() == 12 && list1.get(i).getOffense()== OffenseType.STOLEN_PROPERTY){
						DecSP++;
					}
					else if (list1.get(i).getDate().getMonth() == 1 && list1.get(i).getOffense() == OffenseType.SEX_OFFENSE){
						JanSO++;
					}
					else if (list1.get(i).getDate().getMonth() == 2 && list1.get(i).getOffense() == OffenseType.SEX_OFFENSE){
						FebSO++;
					}
					else if (list1.get(i).getDate().getMonth() == 3 && list1.get(i).getOffense() == OffenseType.SEX_OFFENSE){
						MarSO++;
					}
					else if (list1.get(i).getDate().getMonth() == 4 && list1.get(i).getOffense()== OffenseType.SEX_OFFENSE){
						AprilSO++;
					}
					else if (list1.get(i).getDate().getMonth() == 5 && list1.get(i).getOffense()== OffenseType.SEX_OFFENSE){
						MaySO++;
					}
					else if (list1.get(i).getDate().getMonth() == 6 && list1.get(i).getOffense()== OffenseType.SEX_OFFENSE){
						JuneSO++;
					}
					else if (list1.get(i).getDate().getMonth() == 7 && list1.get(i).getOffense()== OffenseType.SEX_OFFENSE){
						JulySO++;
					}
					else if (list1.get(i).getDate().getMonth() == 8 && list1.get(i).getOffense()== OffenseType.SEX_OFFENSE){
						AugSO++;
					}
					else if (list1.get(i).getDate().getMonth() == 9 && list1.get(i).getOffense()== OffenseType.SEX_OFFENSE){
						SeptSO++;
					}
					else if (list1.get(i).getDate().getMonth() == 10 && list1.get(i).getOffense()== OffenseType.SEX_OFFENSE){
						OctSO++;
					}
					else if (list1.get(i).getDate().getMonth() == 11 && list1.get(i).getOffense()== OffenseType.SEX_OFFENSE){
						NovSO++;
					}
					else if (list1.get(i).getDate().getMonth() == 12 && list1.get(i).getOffense()== OffenseType.SEX_OFFENSE){
						DecSO++;
					}
					
					else if (list1.get(i).getDate().getMonth() == 1 && list1.get(i).getOffense() == OffenseType.PUBLIC_PEACE_OFFENSE){
						JanPP++;
					}
					else if (list1.get(i).getDate().getMonth() == 2 && list1.get(i).getOffense() == OffenseType.PUBLIC_PEACE_OFFENSE){
						FebPP++;
					}
					else if (list1.get(i).getDate().getMonth() == 3 && list1.get(i).getOffense() == OffenseType.PUBLIC_PEACE_OFFENSE){
						MarPP++;
					}
					else if (list1.get(i).getDate().getMonth() == 4 && list1.get(i).getOffense()== OffenseType.PUBLIC_PEACE_OFFENSE){
						AprilPP++;
					}
					else if (list1.get(i).getDate().getMonth() == 5 && list1.get(i).getOffense()== OffenseType.PUBLIC_PEACE_OFFENSE){
						MayPP++;
					}
					else if (list1.get(i).getDate().getMonth() == 6 && list1.get(i).getOffense()== OffenseType.PUBLIC_PEACE_OFFENSE){
						JunePP++;
					}
					else if (list1.get(i).getDate().getMonth() == 7 && list1.get(i).getOffense()== OffenseType.PUBLIC_PEACE_OFFENSE){
						JulyPP++;
					}
					else if (list1.get(i).getDate().getMonth() == 8 && list1.get(i).getOffense()== OffenseType.PUBLIC_PEACE_OFFENSE){
						AugPP++;
					}
					else if (list1.get(i).getDate().getMonth() == 9 && list1.get(i).getOffense()== OffenseType.PUBLIC_PEACE_OFFENSE){
						SeptPP++;
					}
					else if (list1.get(i).getDate().getMonth() == 10 && list1.get(i).getOffense()== OffenseType.PUBLIC_PEACE_OFFENSE){
						OctPP++;
					}
					else if (list1.get(i).getDate().getMonth() == 11 && list1.get(i).getOffense()== OffenseType.PUBLIC_PEACE_OFFENSE){
						NovPP++;
					}
					else if (list1.get(i).getDate().getMonth() == 12 && list1.get(i).getOffense()== OffenseType.PUBLIC_PEACE_OFFENSE){
						DecPP++;
					}
					else if (list1.get(i).getDate().getMonth() == 1 && list1.get(i).getOffense() == OffenseType.PROSTITUTION){
						JanP++;
					}
					else if (list1.get(i).getDate().getMonth() == 2 && list1.get(i).getOffense() == OffenseType.PROSTITUTION){
						FebP++;
					}
					else if (list1.get(i).getDate().getMonth() == 3 && list1.get(i).getOffense() == OffenseType.PROSTITUTION){
						MarP++;
					}
					else if (list1.get(i).getDate().getMonth() == 4 && list1.get(i).getOffense()== OffenseType.PROSTITUTION){
						AprilP++;
					}
					else if (list1.get(i).getDate().getMonth() == 5 && list1.get(i).getOffense()== OffenseType.PROSTITUTION){
						MayP++;
					}
					else if (list1.get(i).getDate().getMonth() == 6 && list1.get(i).getOffense()== OffenseType.PROSTITUTION){
						JuneP++;
					}
					else if (list1.get(i).getDate().getMonth() == 7 && list1.get(i).getOffense()== OffenseType.PROSTITUTION){
						JulyP++;
					}
					else if (list1.get(i).getDate().getMonth() == 8 && list1.get(i).getOffense()== OffenseType.PROSTITUTION){
						AugP++;
					}
					else if (list1.get(i).getDate().getMonth() == 9 && list1.get(i).getOffense()== OffenseType.PROSTITUTION){
						SeptP++;
					}
					else if (list1.get(i).getDate().getMonth() == 10 && list1.get(i).getOffense()== OffenseType.PROSTITUTION){
						OctP++;
					}
					else if (list1.get(i).getDate().getMonth() == 11 && list1.get(i).getOffense()== OffenseType.PROSTITUTION){
						NovP++;
					}
					else if (list1.get(i).getDate().getMonth() == 12 && list1.get(i).getOffense()== OffenseType.PROSTITUTION){
						DecP++;
					}
					else if (list1.get(i).getDate().getMonth() == 1 && list1.get(i).getOffense() == OffenseType.OTHER_ASSAULTS){
						JanOA++;
					}
					else if (list1.get(i).getDate().getMonth() == 2 && list1.get(i).getOffense() == OffenseType.OTHER_ASSAULTS){
						FebOA++;
					}
					else if (list1.get(i).getDate().getMonth() == 3 && list1.get(i).getOffense() == OffenseType.OTHER_ASSAULTS){
						MarOA++;
					}
					else if (list1.get(i).getDate().getMonth() == 4 && list1.get(i).getOffense()== OffenseType.OTHER_ASSAULTS){
						AprilOA++;
					}
					else if (list1.get(i).getDate().getMonth() == 5 && list1.get(i).getOffense()== OffenseType.OTHER_ASSAULTS){
						MayOA++;
					}
					else if (list1.get(i).getDate().getMonth() == 6 && list1.get(i).getOffense()== OffenseType.OTHER_ASSAULTS){
						JuneOA++;
					}
					else if (list1.get(i).getDate().getMonth() == 7 && list1.get(i).getOffense()== OffenseType.OTHER_ASSAULTS){
						JulyOA++;
					}
					else if (list1.get(i).getDate().getMonth() == 8 && list1.get(i).getOffense()== OffenseType.OTHER_ASSAULTS){
						AugOA++;
					}
					else if (list1.get(i).getDate().getMonth() == 9 && list1.get(i).getOffense()== OffenseType.OTHER_ASSAULTS){
						SeptOA++;
					}
					else if (list1.get(i).getDate().getMonth() == 10 && list1.get(i).getOffense()== OffenseType.OTHER_ASSAULTS){
						OctOA++;
					}
					else if (list1.get(i).getDate().getMonth() == 11 && list1.get(i).getOffense()== OffenseType.OTHER_ASSAULTS){
						NovOA++;
					}
					else if (list1.get(i).getDate().getMonth() == 12 && list1.get(i).getOffense()== OffenseType.OTHER_ASSAULTS){
						DecOA++;
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
						series1Numbers = new Number[]{ JanA,FebA ,MarA,AprilA,MayA,JuneA,JulyA,AugA,SeptA,OctA,NovA,DecA};
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.setRangeTopMax(150);

						plot.redraw();
					}
					else if (selectedItem == OffenseType.AGG_ASSAULT){
						plot.clear();
						series1Numbers = new Number[]{ JanAA,FebAA ,MarAA,AprilAA,MayAA,JuneAA,JulyAA,AugAA,SeptAA,OctAA,NovAA,DecAA};
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.ALL_OTHER_OFFENSES){
						plot.clear();
						series1Numbers = new Number[]{ JanAO,FebAO ,MarAO,AprilAO,MayAO,JuneAO,JulyAO,AugAO,SeptAO,OctAO,NovAO,DecAO};
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.AUTO_THEFT){
						plot.clear();
						series1Numbers = new Number[]{ JanAT,FebAT ,MarAT,AprilAT,MayAT,JuneAT,JulyAT,AugAT,SeptAT,OctAT,NovAT,DecAT};
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.DAMAGE_TO_PROPERTY){
						plot.clear();
						series1Numbers = new Number[]{ JanDP,FebDP ,MarDP,AprilDP,MayDP,JuneDP,JulyDP,AugDP,SeptDP,OctDP,NovDP,DecDP};
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.BURGLARY){
						plot.clear();
						series1Numbers = new Number[]{ JanB,FebB ,MarB,AprilB,MayB,JuneB,JulyB,AugB,SeptB,OctB,NovB,DecB};
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.DANGEROUS_DRUGS){
						plot.clear();
						series1Numbers = new Number[]{ JanDD,FebDD ,MarDD,AprilDD,MayDD,JuneDD,JulyDD,AugDD,SeptDD,OctDD,NovDD,DecDD};
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.DUI){
						plot.clear();
						series1Numbers = new Number[]{ JanD,FebD ,MarD,AprilD,MayD,JuneD,JulyD,AugD,SeptD,OctD,NovD,DecD};
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.EMBEZZLEMENT){
						plot.clear();
						series1Numbers = new Number[]{ JanE,FebE ,MarE,AprilE,MayE,JuneE,JulyE,AugE,SeptE,OctE,NovE,DecE};
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.FAMILY_OFFENSE){
						plot.clear();
						series1Numbers = new Number[]{ JanFO,FebFO ,MarFO,AprilFO,MayFO,JuneFO,JulyFO,AugFO,SeptFO,OctFO,NovFO,DecFO};
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.FORGERY){
						plot.clear();
						series1Numbers = new Number[]{ JanF,FebF ,MarF,AprilF,MayF,JuneF,JulyF,AugF,SeptF,OctF,NovF,DecF};
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.FRAUD){
						plot.clear();
						series1Numbers = new Number[]{ JanFR,FebFR ,MarFR,AprilFR,MayFR,JuneFR,JulyFR,AugFR,SeptFR,OctFR,NovFR,DecFR};
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.GAMBLING_OFFENSE){
						plot.clear();
						series1Numbers = new Number[]{ JanGO,FebGO ,MarGO,AprilGO,MayGO,JuneGO,JulyGO,AugGO,SeptGO,OctGO,NovGO,DecGO};
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.HOMICIDE){
						plot.clear();
						series1Numbers = new Number[]{ JanH,FebH ,MarH,AprilH,MayH,JuneH,JulyH,AugH,SeptH,OctH,NovH,DecH};
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.LIQUOR_LAWS){
						plot.clear();
						series1Numbers = new Number[]{ JanLL,FebLL ,MarLL,AprilLL,MayLL,JuneLL,JulyLL,AugLL,SeptLL,OctLL,NovLL,DecLL};
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.NO_CRIME){
						plot.clear();
						series1Numbers = new Number[]{ JanNC,FebNC ,MarNC,AprilNC,MayNC,JuneNC,JulyNC,AugNC,SeptNC,OctNC,NovNC,DecNC};
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.NON_CRIME){
						plot.clear();
						series1Numbers = new Number[]{ JanNCR,FebNCR ,MarNCR,AprilNCR,MayNCR,JuneNCR,JulyNCR,AugNCR,SeptNCR,OctNCR,NovNCR,DecNCR};
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.OTHER_ASSAULTS){
						plot.clear();
						series1Numbers = new Number[]{ JanOA,FebOA ,MarOA,AprilOA,MayOA,JuneOA,JulyOA,AugOA,SeptOA,OctOA,NovOA,DecOA};
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.PROSTITUTION){
						plot.clear();
						series1Numbers = new Number[]{ JanP,FebP ,MarP,AprilP,MayP,JuneP,JulyP,AugP,SeptP,OctP,NovP,DecP};
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.RAPE){
						plot.clear();
						series1Numbers = new Number[]{ JanRA,FebRA ,MarRA,AprilRA,MayRA,JuneRA,JulyRA,AugRA,SeptRA,OctRA,NovRA,DecRA};
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.PUBLIC_PEACE_OFFENSE){
						plot.clear();
						series1Numbers = new Number[]{ JanPP,FebPP ,MarPP,AprilPP,MayPP,JunePP,JulyPP,AugPP,SeptPP,OctPP,NovPP,DecPP};
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.ROBBERY){
						plot.clear();
						series1Numbers = new Number[]{ JanR, FebR, MarR, AprilR, MayR, JuneR,
								JulyR, AugR, SeptR, OctR, NovR, DecR };						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.SEX_OFFENSE){
						plot.clear();
						series1Numbers = new Number[]{ JanSO,FebSO ,MarSO,AprilSO,MaySO,JuneSO,JulySO,AugSO,SeptSO,OctSO,NovSO,DecSO};
						
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.STOLEN_PROPERTY){
						plot.clear();
						series1Numbers = new Number[]{ JanSP,FebSP ,MarSP,AprilSP,MaySP,JuneSP,JulySP,AugSP,SeptSP,OctSP,NovSP,DecSP};

						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.VAGRANCY_OFFENSE){
						plot.clear();
						series1Numbers = new Number[]{ JanVO,FebVO ,MarVO,AprilVO,MayVO,JuneVO,JulyVO,AugVO,SeptVO,OctVO,NovVO,DecVO};

						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.WEAPONS_OFFENSE){
						plot.clear();
						series1Numbers = new Number[]{ JanWO,FebWO ,MarWO,AprilWO,MayWO,JuneWO,JulyWO,AugWO,SeptWO,OctWO,NovWO,DecWO};

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
						plot.clear();// why does this make my app crash
						series2Numbers = new Number[]{ JanL, FebL, MarL, AprilL, MayL, JuneL,JulyL, AugL, SeptL, OctL, NovL, DecL };
						/*series2Numbers = new Number[]{ JanR, FebR, MarR, AprilR, MayR, JuneR,
								JulyR, AugR, SeptR, OctR, NovR, DecR };*/
						//JanL = 5;
						//FebL = 7;
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.ARSON){
						plot.clear();
						series2Numbers = new Number[]{ JanA,FebA ,MarA,AprilA,MayA,JuneA,JulyA,AugA,SeptA,OctA,NovA,DecA};
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.AGG_ASSAULT){
						plot.clear();
						series2Numbers = new Number[]{ JanAA,FebAA ,MarAA,AprilAA,MayAA,JuneAA,JulyAA,AugAA,SeptAA,OctAA,NovAA,DecAA};
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.ALL_OTHER_OFFENSES){
						plot.clear();
						series2Numbers = new Number[]{ JanAO,FebAO ,MarAO,AprilAO,MayAO,JuneAO,JulyAO,AugAO,SeptAO,OctAO,NovAO,DecAO};
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.AUTO_THEFT){
						plot.clear();
						series2Numbers = new Number[]{ JanAT,FebAT ,MarAT,AprilAT,MayAT,JuneAT,JulyAT,AugAT,SeptAT,OctAT,NovAT,DecAT};
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.DAMAGE_TO_PROPERTY){
						plot.clear();
						series2Numbers = new Number[]{ JanDP,FebDP ,MarDP,AprilDP,MayDP,JuneDP,JulyDP,AugDP,SeptDP,OctDP,NovDP,DecDP};
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.BURGLARY){
						plot.clear();
						series2Numbers = new Number[]{ JanB,FebB ,MarB,AprilB,MayB,JuneB,JulyB,AugB,SeptB,OctB,NovB,DecB};
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.DANGEROUS_DRUGS){
						plot.clear();
						series2Numbers = new Number[]{ JanDD,FebDD ,MarDD,AprilDD,MayDD,JuneDD,JulyDD,AugDD,SeptDD,OctDD,NovDD,DecDD};
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.DUI){
						plot.clear();
						series2Numbers = new Number[]{ JanD,FebD ,MarD,AprilD,MayD,JuneD,JulyD,AugD,SeptD,OctD,NovD,DecD};
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.EMBEZZLEMENT){
						plot.clear();
						series2Numbers = new Number[]{ JanE,FebE ,MarE,AprilE,MayE,JuneE,JulyE,AugE,SeptE,OctE,NovE,DecE};
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.FAMILY_OFFENSE){
						plot.clear();
						series2Numbers = new Number[]{ JanFO,FebFO ,MarFO,AprilFO,MayFO,JuneFO,JulyFO,AugFO,SeptFO,OctFO,NovFO,DecFO};
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.FORGERY){
						plot.clear();
						series2Numbers = new Number[]{ JanF,FebF ,MarF,AprilF,MayF,JuneF,JulyF,AugF,SeptF,OctF,NovF,DecF};
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.FRAUD){
						plot.clear();
						series2Numbers = new Number[]{ JanFR,FebFR ,MarFR,AprilFR,MayFR,JuneFR,JulyFR,AugFR,SeptFR,OctFR,NovFR,DecFR};
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.GAMBLING_OFFENSE){
						plot.clear();
						series2Numbers = new Number[]{ JanGO,FebGO ,MarGO,AprilGO,MayGO,JuneGO,JulyGO,AugGO,SeptGO,OctGO,NovGO,DecGO};
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.HOMICIDE){
						plot.clear();
						series2Numbers = new Number[]{ JanH,FebH ,MarH,AprilH,MayH,JuneH,JulyH,AugH,SeptH,OctH,NovH,DecH};
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.LIQUOR_LAWS){
						plot.clear();
						series2Numbers = new Number[]{ JanLL,FebLL ,MarLL,AprilLL,MayLL,JuneLL,JulyLL,AugLL,SeptLL,OctLL,NovLL,DecLL};
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.NO_CRIME){
						plot.clear();
						series2Numbers = new Number[]{ JanNC,FebNC ,MarNC,AprilNC,MayNC,JuneNC,JulyNC,AugNC,SeptNC,OctNC,NovNC,DecNC};
						//MarL = 7;
						//AprilL = 2;
					
						toc = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.NON_CRIME){
						plot.clear();
						series2Numbers = new Number[]{ JanNCR,FebNCR ,MarNCR,AprilNCR,MayNCR,JuneNCR,JulyNCR,AugNCR,SeptNCR,OctNCR,NovNCR,DecNCR};
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.OTHER_ASSAULTS){
						plot.clear();
						series2Numbers = new Number[]{ JanOA,FebOA ,MarOA,AprilOA,MayOA,JuneOA,JulyOA,AugOA,SeptOA,OctOA,NovOA,DecOA};
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.PROSTITUTION){
						plot.clear();
						series2Numbers = new Number[]{ JanP,FebP ,MarP,AprilP,MayP,JuneP,JulyP,AugP,SeptP,OctP,NovP,DecP};
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.RAPE){
						plot.clear();
						series2Numbers = new Number[]{ JanRA,FebRA ,MarRA,AprilRA,MayRA,JuneRA,JulyRA,AugRA,SeptRA,OctRA,NovRA,DecRA};
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.PUBLIC_PEACE_OFFENSE){
						plot.clear();
						series2Numbers = new Number[]{ JanPP,FebPP ,MarPP,AprilPP,MayPP,JunePP,JulyPP,AugPP,SeptPP,OctPP,NovPP,DecPP};
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.ROBBERY){
						plot.clear();
						series2Numbers = new Number[]{ JanR, FebR, MarR, AprilR, MayR, JuneR,
								JulyR, AugR, SeptR, OctR, NovR, DecR };						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.SEX_OFFENSE){
						plot.clear();
						series2Numbers = new Number[]{ JanSO,FebSO ,MarSO,AprilSO,MaySO,JuneSO,JulySO,AugSO,SeptSO,OctSO,NovSO,DecSO};
						
						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.STOLEN_PROPERTY){
						plot.clear();
						series2Numbers = new Number[]{ JanSP,FebSP ,MarSP,AprilSP,MaySP,JuneSP,JulySP,AugSP,SeptSP,OctSP,NovSP,DecSP};

						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.VAGRANCY_OFFENSE){
						plot.clear();
						series2Numbers = new Number[]{ JanVO,FebVO ,MarVO,AprilVO,MayVO,JuneVO,JulyVO,AugVO,SeptVO,OctVO,NovVO,DecVO};

						//MarL = 7;
						//AprilL = 2;
					
						toc2 = selectedItem.toString();
						drawGraph();
						plot.redraw();
					}
					else if (selectedItem == OffenseType.WEAPONS_OFFENSE){
						plot.clear();
						series2Numbers = new Number[]{ JanWO,FebWO ,MarWO,AprilWO,MayWO,JuneWO,JulyWO,AugWO,SeptWO,OctWO,NovWO,DecWO};

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
		plot.setRangeTopMax(150);


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
