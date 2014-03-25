package com.example.gtsafe;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.gtsafe.library.DBManager;
import com.example.gtsafe.library.listeners.interfaces.OnDBUpdateListener;
import com.example.gtsafe.model.CleryActModel;

public class CleryActActivity extends ListActivity {
	
	List<CleryActModel> models;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		models = DBManager.getInstance().getAllCleryActs();
		
		final ArrayAdapter<CleryActModel> adapter = new ArrayAdapter<CleryActModel>(this, R.layout.activity_clery_act, R.id.cleryact, models);
		this.setListAdapter(adapter);
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
		
		DBManager.getInstance().setOnCleryActUpdateListener(new OnDBUpdateListener<CleryActModel>()
		{
			@Override
			public void OnUpdate(CleryActModel item) 
			{
				int position = lv.getSelectedItemPosition();
				adapter.insert(item, 0);
				lv.setSelection(position);
				
				runOnUiThread(new Runnable() {
			        @Override
			        public void run() {
			                adapter.notifyDataSetChanged();
			        }
			    });
			}
		});
	}
}
