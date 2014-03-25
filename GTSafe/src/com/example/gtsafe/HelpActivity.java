package com.example.gtsafe;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ViewFlipper;
 
public class HelpActivity extends SuperActivity {
 
	private ViewFlipper viewFlipper;
	
	private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;
 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		viewFlipper = (ViewFlipper) findViewById(R.id.flipper);
		
		int images[]={R.drawable.help1, R.drawable.help2, R.drawable.help3, R.drawable.help4};
		
		for(int i=0;i<images.length;i++)
        {
        //  This will create dynamic image view and add them to ViewFlipper
            setFlipperImage(images[i]);
        }
		
		 // Gesture detection
        gestureDetector = new GestureDetector(this, new MyGestureDetector());
        viewFlipper.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
	}
	
	private void setFlipperImage(int res) 
	{
	    ImageView image = new ImageView(getApplicationContext());
	    image.setBackgroundResource(res);
	    viewFlipper.addView(image);
	}
	
	private class MyGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) 
                {
                    viewFlipper.showNext();
                }  
                else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) //left to right
                {
                    viewFlipper.showPrevious();
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }

            @Override
        public boolean onDown(MotionEvent e) {
              return true;
        }
    }
}