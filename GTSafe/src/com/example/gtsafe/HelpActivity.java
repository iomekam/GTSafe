package com.example.gtsafe;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.view.View;
import android.view.View.OnClickListener;
 
public class HelpActivity extends Activity {
 
	private Button button;
	private ImageView image;
	private int next = 1;
 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
 
		addListenerOnButton();
 
	}
 
	public void addListenerOnButton() {
 
		image = (ImageView) findViewById(R.id.imageView1);
 
		button = (Button) findViewById(R.id.btnChangeImage);
		button.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View arg0) {
				if (next == 1)
				{
					image.setImageResource(R.drawable.help2);
					next++;
				}
				else if (next == 2)
				{
					image.setImageResource(R.drawable.help3);
					next++;
				}
				else if (next == 3)
				{
					image.setImageResource(R.drawable.help4);
					next++;
				}
				else
				{
					image.setImageResource(R.drawable.help1);
					next = 1;
				}
			}
 
		});
 
	}
 
}