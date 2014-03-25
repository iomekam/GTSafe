package com.example.gtsafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.example.gtsafe.model.CleryActModel;

public class SingleListItem extends SuperActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_single_list_item);
		Intent i = getIntent();

        // getting attached intent data
        int id = i.getIntExtra("clery_id", 1);
        
        CleryActModel model = manager.getCleryAct(id);
        
		WebView view = (WebView)findViewById(R.id.webView1);
		view.loadData(model.getText(), "text/html", null);
	}
}
