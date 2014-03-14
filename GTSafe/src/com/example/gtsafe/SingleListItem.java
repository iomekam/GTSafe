package com.example.gtsafe;

import com.example.gtsafe.library.DBManager;
import com.example.gtsafe.model.CleryActModel;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.webkit.WebView;
import android.widget.TextView;

public class SingleListItem extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_single_list_item);
		Intent i = getIntent();

        // getting attached intent data
        int id = i.getIntExtra("clery_id", 1);
        
        CleryActModel model = DBManager.getInstance().getCleryAct(id);
        
		WebView view = (WebView)findViewById(R.id.webView1);
		view.loadData(model.getText(), "text/html", null);
	}
}
