package com.micro.nptel;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class Chat extends Activity implements OnTouchListener{
	
	static final int MIN_DISTANCE = 100;
	private float downX, downY, upX, upY;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_chat);
		
		View view = findViewById(R.id.activity_chat); // Make sure its the same layout 
		view.setOnTouchListener(this);
		
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		//Toast.makeText(this,"Inst pressed", Toast.LENGTH_SHORT).show();
		//return true;
		switch(event.getAction()){
        case MotionEvent.ACTION_DOWN: {
            downX = event.getX();
            downY = event.getY();
            return true;
        }
        case MotionEvent.ACTION_UP: {
            upX = event.getX();
            upY = event.getY();

            float deltaX = downX - upX;
            float deltaY = downY - upY;

            // swipe horizontal?
            if(Math.abs(deltaX) > MIN_DISTANCE){
                // left or right
                if(deltaX < 0) { this.onLeftToRightSwipe(); return true; }
                if(deltaX > 0) { this.onRightToLeftSwipe(); return true; }
            }
            /*else {
                    Log.i(logTag, "Swipe was only " + Math.abs(deltaX) + " long, need at least " + MIN_DISTANCE);
                    return false; // We don't consume the event
            }*/

            // swipe vertical?
            else if(Math.abs(deltaY) > MIN_DISTANCE){
                // top or down
                if(deltaY < 0) { this.onTopToBottomSwipe(); return true; }
                if(deltaY > 0) { this.onBottomToTopSwipe(); return true; }
            }
            else {
                    Log.i("NOT_A_SWIPE", "Swipe was only " + Math.abs(deltaX) + " long, need at least " + MIN_DISTANCE);
                    return false; // We don't consume the event
            }

            return true;
        }
    }
    return false;
		
	}  
	
	public void onRightToLeftSwipe(){
	    //Toast.makeText(this,"Right to left", Toast.LENGTH_SHORT).show();
	    
	}

	public void onLeftToRightSwipe(){
	    //Toast.makeText(this,"left to right", Toast.LENGTH_SHORT).show();
	    //startActivity(new Intent(this, Home.class));
	    Intent intent = new Intent(this, Home.class);
	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
	    startActivity(intent);
	    overridePendingTransition(R.anim.enter_left_to_right, R.anim.leave_left_to_right);
	    //activity.doSomething();
	}

	public void onTopToBottomSwipe(){
	    //Toast.makeText(this,"Top to bottom", Toast.LENGTH_SHORT).show();
	    //activity.doSomething();
	}

	public void onBottomToTopSwipe(){
	    //Toast.makeText(this,"Bottom to top", Toast.LENGTH_SHORT).show();
	    //activity.doSomething();
	}


}
