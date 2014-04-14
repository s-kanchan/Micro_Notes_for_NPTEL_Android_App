package com.micro.nptel;

public class VideoStatus {
	
	public static enum STATUS {PLAYING, PAUSED, NOTES, SEEKED};
	
	static int __NOTES_DISPLAY_COUNT = 10;
	STATUS status;
	volatile int note_timer;
	volatile int seek_time;
	
	public VideoStatus()
	{
		note_timer = 0;
		seek_time = 0;
	}

}
