package com.micro.nptel;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class Home extends Activity implements OnTouchListener{
	
	VideoView videoView;
	ImageView author_pic;
	VideoStatus video_status = null;
	JsonParser json_parser;
	MediaController mediacontroller;
	//String VideoURL = "http://www.androidbegin.com/tutorial/AndroidCommercial.3gp";
	String VideoURL;
	static final String logTag = "ActivitySwipeDetector";
	static final int MIN_DISTANCE = 100;
	private float downX, downY, upX, upY;
	//private GestureDetector gestureDetector;
	//private GestureListener gd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//this.overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right)
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_home);
		View view = findViewById(R.id.playerLayout);
		view.setOnTouchListener(this);
		
		Bundle bundle = getIntent().getExtras();
		VideoURL = bundle.getString("video");
		json_parser = new JsonParser(getBaseContext(), VideoURL);
		//final VideoView videoView = (VideoView) findViewById(R.id.nptel_video);
		videoView = (VideoView) findViewById(R.id.nptel_video);
		author_pic = (ImageView) findViewById(R.id.author_pic);
		author_pic.setVisibility(View.GONE);
		final Handler handler=new Handler();
        final Toast toast = Toast.makeText(this, "hello", Toast.LENGTH_SHORT);
        
        author_pic.setOnClickListener(new OnClickListener() {
            @Override
                public void onClick(View v) {
            		videoView.pause();
                    Home.this.showNotesDialog();
                }
            });
        
        final Runnable r=new Runnable()
        {
            public void run() 
            {
                int pos = videoView.getCurrentPosition();
                if(pos >=3000 && pos<3500)
                {
                    Home.this.showMicronotes();
                	toast.setText("Reached 3 seconds");
                   toast.show();
                 }
                else if(pos >=7000 && pos<7500)
                {
                	Home.this.showMicronotes();
                	toast.setText("Reached 7 seconds");
                   toast.show();
                 }
                else
                {
                	video_status.note_timer--;
                	if(video_status.note_timer == 0)
                		Home.this.hideMicronotes();
                }
                	
                //toast.show();
                handler.postDelayed(this, 250);
            }
        };
        
        try {
            // Start the MediaController
        	if(video_status == null)
        	{
        		mediacontroller = new MediaController(Home.this);
        		mediacontroller.setAnchorView(videoView);
        		// Get the URL from String VideoURL
        		Uri video = Uri.parse(VideoURL);
        		//Uri video = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.android_commercial);
        		videoView.setMediaController(mediacontroller);
        		videoView.setVideoURI(video);
        		video_status = new VideoStatus();
        	}
        	else
        	{
        		videoView.seekTo(video_status.seek_time);
        	}
 
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        

		//Uri video = Uri.parse("http://download.blender.org/durian/trailer/sintel_trailer-480p.mp4");
		//videoView.start();
        
        videoView.setOnPreparedListener(new OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
                handler.postDelayed(r, 250);
            }
        });
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		video_status.seek_time = videoView.getCurrentPosition();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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
                    Log.i(logTag, "Swipe was only " + Math.abs(deltaX) + " long, need at least " + MIN_DISTANCE);
                    return false; // We don't consume the event
            }

            return true;
        }
    }
    return false;
		
	}  
	
	//////////////////////// CLASS GestureListener ///////////////
	class GestureListener extends SimpleOnGestureListener{
	       
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;
       
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, 
                                        float velocityX, float velocityY) {
           
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && 
                         Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            	Toast.makeText(Home.this,"Right to left", Toast.LENGTH_SHORT).show();
                //From Right to Left
                return true;
            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE &&
                         Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                //From Left to Right
            	Toast.makeText(Home.this,"left to right", Toast.LENGTH_SHORT).show();
                return true;
            }
           
            if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && 
                        Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                //From Bottom to Top
            	Toast.makeText(Home.this,"Bottom to top", Toast.LENGTH_SHORT).show();
                return true;
            }  else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && 
                        Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                //From Top to Bottom
            	Toast.makeText(Home.this,"Top to bottom", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        }
        @Override
        public boolean onDown(MotionEvent e) {
            //always return true since all gestures always begin with onDown and<br>
            //if this returns false, the framework won't try to pick up onFling for example.
            return true;
        }
    }
////////////////////////CLASS GestureListener ENDS ///////////////
	
	
	

	public void onRightToLeftSwipe(){
	    Log.i(logTag, "RightToLeftSwipe!");
	    //Toast.makeText(Home.this,"Right to left", Toast.LENGTH_SHORT).show();
	    startActivity(new Intent(Home.this, Chat.class));
	    overridePendingTransition(R.anim.enter_right_to_left, R.anim.leave_right_to_left);
	    
	}

	public void onLeftToRightSwipe(){
	    Log.i(logTag, "LeftToRightSwipe!");
	    //Toast.makeText(Home.this,"left to right", Toast.LENGTH_SHORT).show();
	    startActivity(new Intent(Home.this, CreateNote.class));
	    overridePendingTransition(R.anim.enter_left_to_right, R.anim.leave_left_to_right);
	    //activity.doSomething();
	}

	public void onTopToBottomSwipe(){
	    Log.i(logTag, "onTopToBottomSwipe!");
	    //Toast.makeText(Home.this,"Top to bottom", Toast.LENGTH_SHORT).show();
	    //activity.doSomething();
	}

	public void onBottomToTopSwipe(){
	    Log.i(logTag, "onBottomToTopSwipe!");
	    //Toast.makeText(Home.this,"Bottom to top", Toast.LENGTH_SHORT).show();
	    startActivity(new Intent(Home.this, AboutUs.class));
	    overridePendingTransition(R.anim.enter_bottom_to_top, R.anim.leave_bottom_to_top);
	    //activity.doSomething();
	}
	
	public void showMicronotes()
	{
		author_pic.setVisibility(View.VISIBLE);
		video_status.note_timer = video_status.__NOTES_DISPLAY_COUNT;
	}
	
	public void hideMicronotes()
	{
		author_pic.setVisibility(View.INVISIBLE);
	}

	public void showNotesDialog()
	{
		 final Dialog dialog = new Dialog(Home.this);
         dialog.setContentView(R.layout.dialog_notes);
         dialog.setTitle("Note");
         dialog.setCancelable(true);
         dialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				videoView.start();
				
				
			}
		});
         
         Button button = (Button) dialog.findViewById(R.id.dialog_cancel);
         button.setOnClickListener(new OnClickListener() {
         @Override
             public void onClick(View v) {
                 //finish();
        	 dialog.dismiss();
             }
         });
         dialog.show();
	}
	
	
}








