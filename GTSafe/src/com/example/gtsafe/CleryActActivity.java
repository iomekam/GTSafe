package com.example.gtsafe;

import java.util.LinkedList;
import java.util.List;

import com.example.gtsafe.library.DBManager;
import com.example.gtsafe.model.CleryActModel;

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
		
		//setContentView(R.layout.activity_clery_act);
		
		// storing string resources into Array
		
		// Binding resources Array to ListAdapter
		this.setListAdapter(new ArrayAdapter<CleryActModel>(this, R.layout.activity_clery_act, R.id.cleryact, DBManager.getInstance().getAllCleryActs()));
		
		final ListView lv = getListView();
		
		// listening to single list item on click
		lv.setOnItemClickListener(new OnItemClickListener() {
	          public void onItemClick(AdapterView<?> parent, View view,
	              int position, long id) {
	               
	        	  CleryActModel model = (CleryActModel)lv.getItemAtPosition(position);
	        	  
	        	  Intent intent = new Intent(view.getContext(), SingleListItem.class);
	        	  intent.putExtra("clery_id", model.getID());
	        	  startActivity(intent);
	      		}    
	         
	        });
	}
}
