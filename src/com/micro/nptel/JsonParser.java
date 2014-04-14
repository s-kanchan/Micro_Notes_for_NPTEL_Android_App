package com.micro.nptel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class JsonParser {
	
	String video_file;
	String json_files[]; 
	Context app_context;
	FileHandler file_handler;
	
	public JsonParser(Context context, String vid_file)
	{
		video_file = vid_file;
		app_context = context;
		file_handler = new FileHandler(context);
		nameJsonFiles();
		getNotes();
	}

	private ArrayList<JSONObject> getNotes() {
		String file;
		String json;
		ArrayList<JSONObject> ret_json_objs = new ArrayList<JSONObject>();
		file = json_files[0];
		Log.i("_JSON_PARSER_", "FILE_NAME :"+file);
		json = loadJSON(file);
		Log.i("_JSON_PARSER_", "JSON_STR :"+json);
		
		try {
			JSONArray json_root = new JSONArray(json);
			JSONObject[] json_objs = new JSONObject[json_root.length()];
			for (int i=0;i<json_root.length();i++)
			{
				json_objs[i] = json_root.getJSONObject(i);
				String cont = json_objs[i].getString("content");
				Log.i("_JSON_PARSER_", "Cont :" + cont);
				
				//for(int j=0;j<)
			}
			//Log.i("_JSON_PARSER_", "JsonArrayCreated");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.i("JSONPARSE_ERROR", "Object cannot be created");
			e.printStackTrace();
		}
		
		return ret_json_objs;
		
	}
	
	public String loadJSON(String filename) {
	    String json = null;
	    try {

	       
	    	//InputStream is = app_context.openFileInput(filename);  //Throws illegal argument EXC.. 
	    	// openFileInput takes only file name, not paths
	    	InputStream is = new FileInputStream(new File(filename));

	        int size = is.available();

	        byte[] buffer = new byte[size];

	        is.read(buffer);

	        is.close();

	        json = new String(buffer, "UTF-8");


	    } catch (IOException ex) {
	        ex.printStackTrace();
	        return null;
	    }
	    
	    return json;

	}
	
	public void nameJsonFiles()
	{
		String file;
		json_files = new String[1];
		
		if (video_file.indexOf(".") > 0)
		{
			file = video_file.substring(0, video_file.lastIndexOf("."));
			json_files[0] = file + ".json";
		}	
		
	}
	
	public String[] getJsonFiles()
	{
		Log.i("__JSON__", json_files[0]);
		return json_files;
	}
	

}
