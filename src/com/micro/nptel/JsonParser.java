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
import android.media.MediaRouter.VolumeCallback;
import android.util.Log;

public class JsonParser {
	
	ArrayList<JSONObject> notes_object;
	String video_file;
	String json_files[]; 
	Context app_context;
	FileHandler file_handler;
	
	public int convertToSeconds(String time_str)
	{
		//Converter to be used if time is in format "min:sec" eg: 01:10 
		int time_int = 0;
		if(time_str.contains(":"))
		{
			String[] parts = time_str.split(":");
			int min = Integer.parseInt(parts[0].toString());
			int sec = Integer.parseInt(parts[1].toString());
			
			time_int = (min*60) + sec;
			
			Log.i("_CONVERTER_VAL : ", String.valueOf(time_int));
		}
		
		return time_int;
	}
	
	public JsonParser(Context context, String vid_file)
	{
		video_file = vid_file;
		app_context = context;
		file_handler = new FileHandler(context);
		nameJsonFiles();
		notes_object = getNotes();
		///////////////// Remove this ///////////
		Log.i("_JSON_PARSER_", "End of constructor");
		for(int i = 0 ; i < notes_object.size(); i++)
		{
			try {
				Log.i("_JSON_PARSE_ notes : ", String.valueOf(convertToSeconds(notes_object.get(i).getString("note_time"))));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		/////////////////////////////////////////
		
	}

	private ArrayList<JSONObject> getNotes() {
		String file;
		String json;
		int added_already;
		ArrayList<JSONObject> ret_json_objs = new ArrayList<JSONObject>();
		file = json_files[0];
		Log.i("_JSON_PARSER_", "FILE_NAME :"+file);
		json = loadJSON(file);
		Log.i("_JSON_PARSER_", "JSON_STR :"+json);
		
		try {
			JSONArray json_root = new JSONArray(json);
			//JSONObject[] json_objs = new JSONObject[json_root.length()];
			JSONObject obj;
			JSONObject ret_obj = null;  //Careful about this null 
			
			for (int i=0;i<json_root.length();i++)
			{
				//json_objs[i] = json_root.getJSONObject(i);
				//String cont = json_objs[i].getString("content");
				//Log.i("_JSON_PARSER_", "Cont :" + cont);
				obj = json_root.getJSONObject(i);
				added_already = 0;
				if(i == 0)
					ret_json_objs.add(obj);
				else
				{
					for(int j =0; j< ret_json_objs.size();j++)
					{
						ret_obj = ret_json_objs.get(j);
						if( convertToSeconds(obj.getString("note_time")) < convertToSeconds(ret_obj.getString("note_time")) )
						{
							ret_json_objs.add(j, obj);
							added_already = 1;
							break;
						}
					}
					
					if (added_already == 0)
					{
						Log.i("_ADDED_ALREADY_", "adding outside for");
						ret_json_objs.add(obj);
					}
				}
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
