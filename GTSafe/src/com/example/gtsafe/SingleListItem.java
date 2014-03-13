package com.example.gtsafe;

import com.example.gtsafe.library.DBManager;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.webkit.WebView;

public class SingleListItem extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_single_list_item);
		
		  
        /*TextView txtProduct = (TextView) findViewById(R.id.product_label);
         
        Intent i = getIntent();
        // getting attached intent data
        String product = i.getStringExtra("product");
        // displaying selected product name
        txtProduct.setText(product);*/
        for( int j = 0; j<=10;j++){
			String summary = DBManager.getInstance().getCleryAct(j).getText();
			WebView view = (WebView)findViewById(R.id.webView1);
			view.loadData(summary, "text/html", null);
			view.loadData(summary, "text/html", null);
		} 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.single_list_item, menu);
		
		return true;
	}

}
