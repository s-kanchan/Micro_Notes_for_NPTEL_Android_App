package com.micro.nptel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateNote extends Activity implements OnTouchListener{
	
	static final int MIN_DISTANCE = 100;
	private float downX, downY, upX, upY;
	Button cretate_button;
	EditText author_view;
	EditText content_view;
	EditText link_view;
	String file_url;
	int time;
	String author;
	String content;
	String link;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("__CreateNote__", "inside");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_note);
		Log.i("__CreateNote__", "contentSet");
		
		Bundle bundle = getIntent().getExtras();
		file_url = bundle.getString("video");
		time = bundle.getInt("time", 0);
		
		cretate_button = (Button) findViewById(R.id.create_button);
		author_view = (EditText) findViewById(R.id.create_author);
		content_view = (EditText) findViewById(R.id.create_content);
		link_view = (EditText) findViewById(R.id.create_links);
		final Toast toast = Toast.makeText(this, "Please give description.", Toast.LENGTH_SHORT);
		
		View view = findViewById(R.id.create_note_view); // Make sure its the same layout 
		view.setOnTouchListener(this);
		
		cretate_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				author = String.valueOf(author_view.getText());
				content = String.valueOf(content_view.getText());
				link = String.valueOf(link_view.getText());
				Log.i("__CREATE_NOTE", "author: "+author + "content : "+content + "link : "+link);
				if(!content.isEmpty())
				{
					write_to_file();
					Intent intent = new Intent();
					intent.setClass(getApplicationContext(), Home.class);
					intent.putExtra("video", file_url);
					//intent.putExtra("time", videoView.getCurrentPosition()); 
					startActivity(intent);
				}
				else
					toast.show();
			}
		});
	}
	
	
	protected void write_to_file() {
		// TODO Auto-generated method stub
		String url = "";
		if (file_url.indexOf(".") > 0)
		{
			url = file_url.substring(0, file_url.lastIndexOf("."));
			url = url + ".json";
		}
		int time_sec = time/1000;
		int min = time_sec/60;
		int sec = time_sec%60;
		String time_str = String.valueOf(min)+":"+String.valueOf(sec);
		String note_data = "{\"datetime\":\""+new Date().toString() +"\", \"avg_rating\": 0, \"ext_links\":\""+link+"\" , \"content\":\""+content+"\" , \"note_type\": \"description\", \"language\": \"ENGLISH\", \"note_time\":\""+time_str+"\", \"lec_no\": 24, \"subject\": \"106104028\", \"usn\": \"1PI10IS112\", \"_id\": \"5348d5e9d0913d4b71e83ecf\", \"__v\": 0,\"ratings\": []}";
		File file_obj = new File(url);
		if(file_obj.isFile())
		{
			//JsonParser parser = new JsonParser(getApplicationContext(), url);
			String old_json = JsonParser.loadJSON(url);
			old_json = old_json.substring(0, (old_json.length()-1) );
			String new_json = old_json + "," + note_data + "]";
			Log.i("__CREATION__", "DATA : "+new_json);
			
			FileWriter fw;
			try {
				fw = new FileWriter(file_obj.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(new_json);
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else
		{
			String new_json = "[" + note_data + "]";
			Log.i("__CREATION__", "DATA : "+new_json);
			
			FileWriter fw;
			try {
				fw = new FileWriter(file_obj.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(new_json);
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
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
	    startActivity(new Intent(this, Home.class));
	    overridePendingTransition(R.anim.enter_right_to_left, R.anim.leave_right_to_left);
	    
	}

	public void onLeftToRightSwipe(){
	    //Toast.makeText(this,"left to right", Toast.LENGTH_SHORT).show();
	    
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
