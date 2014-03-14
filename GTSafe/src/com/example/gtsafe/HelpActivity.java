package com.example.gtsafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.view.View;
import android.view.View.OnClickListener;
 

public class HelpActivity extends Activity {

	private Button button;
	private Button back_button;
	private ImageView image;
	private int count = 1;
 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
 
		addListenerOnButton();
		
		back_button = (Button)findViewById(R.id.btnBack);
		back_button.setOnClickListener(new View.OnClickListener() {
			 @Override
			 public void onClick(View v) {
			        finish();
			 }
			 });
	}
 
	public void addListenerOnButton() {
 
		image = (ImageView) findViewById(R.id.imageView1);
 
		button = (Button) findViewById(R.id.btnChangeImage);
		button.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View arg0) {
				if (count == 1)
				{
					image.setImageResource(R.drawable.help2);
					count++;
				}
				else if (count == 2)
				{
					image.setImageResource(R.drawable.help3);
					count++;
				}
				else if (count == 3)
				{
					image.setImageResource(R.drawable.help4);
					count++;
				}
				else
				{
					image.setImageResource(R.drawable.help1);
					count = 1;
				}
			}
 
		});
 
	}



}
