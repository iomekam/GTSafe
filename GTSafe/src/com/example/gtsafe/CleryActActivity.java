package com.example.gtsafe;

import com.example.gtsafe.library.DBManager;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.webkit.WebView;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class CleryActActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clery_act);
		
		// storing string resources into Array
		String[] clery_acts = getResources().getStringArray(R.array.Clery_Acts);
		// Binding resources Array to ListAdapter
		this.setListAdapter(new ArrayAdapter<String>(this, R.layout.activity_clery_act, R.id.cleryact, clery_acts));
		
		ListView lv = getListView();
		
		// listening to single list item on click
		lv.setOnItemClickListener(new OnItemClickListener() {
	          public void onItemClick(AdapterView<?> parent, View view,
	              int position, long id) {
	               
	              // selected item 
	              String product = ((TextView) view).getText().toString();
	               
	              // Launching new Activity on selecting single List Item
	              Intent i = new Intent(getApplicationContext(), SingleListItem.class);
	              // sending data to new activity
	              i.putExtra("product", product);
	              startActivity(i);
	             
	          }
	        });

		
		
		
	}
}
