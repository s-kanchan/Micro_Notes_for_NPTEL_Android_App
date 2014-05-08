package com.micro.nptel;

import org.json.JSONException;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class Home extends Activity implements OnTouchListener{
	
	VideoView videoView;
	ImageView author_pic;
	VideoStatus video_status = null;
	JsonParser json_parser;
	int note_index;
	MediaController mediacontroller;
	//String VideoURL = "http://www.androidbegin.com/tutorial/AndroidCommercial.3gp";
	String VideoURL;
	static final String logTag = "ActivitySwipeDetector";
	static final int MIN_DISTANCE = 100;
	private float downX, downY, upX, upY;
	//private GestureDetector gestureDetector;
	//private GestureListener gd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
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
		note_index = 0;
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
           
                ///////////////// Displaying notes as pic /////////////
                if(note_index < json_parser.notes_object.size())
                {
	                int note_time = -1;
					try 
					{
						note_time = json_parser.convertToSeconds(json_parser.notes_object.get(note_index).getString("note_time") );
					} 
					catch (JSONException e) 
					{
						e.printStackTrace();
					}
	                note_time = note_time * 1000;
	                if(pos >= note_time && pos < (note_time + 5000))
	                {
	                	Home.this.showMicronotes();
	                	note_index++;
	                }
                }
               
            	video_status.note_timer--;
            	if(video_status.note_timer == 0)
            		Home.this.hideMicronotes();                	
                /////////// ENDS ///////////////////////////
            	
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
            	mp.setOnSeekCompleteListener(new OnSeekCompleteListener() {
            		@Override
					public void onSeekComplete(MediaPlayer mp) {
						// RESET note_index after a seek
            			int curr_time = videoView.getCurrentPosition();
            			int note_time = 0;
            			note_index = 0;
            			while(note_index < json_parser.notes_object.size())
            			{
	            			try {
								note_time = json_parser.convertToSeconds(json_parser.notes_object.get(note_index).getString("note_time") );
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	            			note_time = note_time * 1000;
	            			if( note_time > curr_time)
	            				break;
	            			note_index++;
            			}
						
					}
				});
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
         
         String note_txt = "";
         try 
         {
			note_txt = json_parser.notes_object.get(note_index-1).getString("content");
			Log.i("__HOME__", "NOTE : "+note_txt);
         } 
         catch (JSONException e) 
         {
        	 e.printStackTrace();
         }
         //disp_note.setText(note_txt);
         dialog.setCancelable(true);
         dialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				videoView.start();
				
				
			}
         });
         TextView disp_note = (TextView) dialog.findViewById(R.id.display_note);
         disp_note.setText(note_txt);
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








