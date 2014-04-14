package com.micro.nptel;

import java.io.File;
import java.io.FilenameFilter;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListVideos extends ListActivity {
	ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		super.onCreate(savedInstanceState);
		adapter = new ArrayAdapter<String>(this,R.layout.list_videos,getVideos());
		setListAdapter(adapter);
		
		ListView listView = getListView();
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				String absolutepath = parent.getItemAtPosition(position).toString();
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), Home.class);
				intent.putExtra("video", absolutepath);
				startActivity(intent);
				
			}
			
		});
	}
	static String[] mFiles=null;
	public static String[] getVideos()
	{
		//File videos = Environment.getExternalStorageDirectory();
		File videos = new File("/storage/extSdCard/nptel/");
		File[] videoList = videos.listFiles(new FilenameFilter(){

			@Override
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				return((name.endsWith(".mp3")||(name.endsWith(".mp4"))));
			}
			
		});
		mFiles = new String[videoList.length];
		for(int i=0;i<videoList.length;i++)
		{
			mFiles[i] = videoList[i].getAbsolutePath();
			Log.i("__FILE__", mFiles[i]);
		}
		return mFiles;
	}

}

