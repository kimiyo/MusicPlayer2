package com.kimiyo.jonghoonkim.musicplayer2;


import java.util.ArrayList;
import java.util.Random;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

/*
 * This is demo code to accompany the Mobiletuts+ series:
 * Android SDK: Creating a Music Player
 * 
 * Sue Smith - February 2014
 */

public class MusicService extends Service implements 
MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
MediaPlayer.OnCompletionListener {

	//media player
	private MediaPlayer player;
	//song list
	private ArrayList<Song> songs;
	//current position
	private int songPosn;
	//binder
	private final IBinder musicBind = new MusicBinder();
	//title of current song
	private String songTitle="";
	//notification id
	private static final int NOTIFY_ID=1;
	//shuffle flag and random
	private boolean shuffle=false;
	private Random rand;

	public void onCreate(){
		Log.d("MusicService","called onCreate()");
		//create the service
		super.onCreate();
		//initialize position
		songPosn=0;
		//random
		rand=new Random();
		//create player
		player = new MediaPlayer();
		//initialize
		initMusicPlayer();
	}

	public void initMusicPlayer(){
		Log.d("MusicService","called initMusicPlayer()");
		//set player properties
		player.setWakeMode(getApplicationContext(), 
				PowerManager.PARTIAL_WAKE_LOCK);
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		//set listeners
		player.setOnPreparedListener(this);
		player.setOnCompletionListener(this);
		player.setOnErrorListener(this);
	}

	//pass song list
	public void setList(ArrayList<Song> theSongs){
		songs=theSongs;
	}

	//binder
	public class MusicBinder extends Binder {
		MusicService getService() { 
			return MusicService.this;
		}
	}

	//activity will bind to service
	@Override
	public IBinder onBind(Intent intent) {
		return musicBind;
	}

	//release resources when unbind
	@Override
	public boolean onUnbind(Intent intent){
		player.stop();
		player.release();
		return false;
	}

	//play a song
	public void playSong(){
		Log.d("MusicService","called playSong()");
		//play
		player.reset();
		//get song
		Song playSong = songs.get(songPosn);
		//get title
		songTitle=playSong.getTitle();
		//get id
		long currSong = playSong.getID();
		//set uri
		Uri trackUri = ContentUris.withAppendedId(
				android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				currSong);
		//set the data source
		try{ 
			player.setDataSource(getApplicationContext(), trackUri);
		}
		catch(Exception e){
			Log.e("MUSIC SERVICE", "Error setting data source", e);
		}
		player.prepareAsync(); 
	}

	//set the song
	public void setSong(int songIndex){
	    songPosn=songIndex;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		Log.v("MusicService", "called onCompletion()");
		//check if playback has reached the end of a track
		if(player.getCurrentPosition()>0){
			mp.reset();
			playNext();
		}
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		Log.v("MusicService", "Playback Error");
		mp.reset();
		return false;
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		Log.v("MusicService", "called onPrepared()");
		//start playback
		mp.start();
		//notification
		Intent notIntent = new Intent(this, com.kimiyo.jonghoonkim.musicplayer2.MainActivity.class);
		notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendInt = PendingIntent.getActivity(this, 0,
				notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification.Builder builder = new Notification.Builder(this);

		builder.setContentIntent(pendInt)
		.setSmallIcon(R.drawable.play)
		.setTicker(songTitle)
		.setOngoing(true)
		.setContentTitle("Playing")
		.setContentText(songTitle);
		Notification not = builder.build();
		startForeground(NOTIFY_ID, not);
	}

	//playback methods
	public int getPosn(){
		Log.v("MusicService playback method", "called getPosn()");
		return player.getCurrentPosition();
	}

	public int getDur(){
		Log.v("MusicService playback method", "called getDur()");

		return player.getDuration();
	}

	public boolean isPng(){
//		Log.v("MusicService playback method", "called isPng()");
		return player.isPlaying();
	}

	public void pausePlayer(){
		Log.v("MusicService playback method", "called pausePlayer()");

		player.pause();
	}

	public void seek(int posn){
		Log.v("MusicService playback method", "called seek()");

		player.seekTo(posn);
	}

	public void go(){
        Log.d("MusicService","called go()");
        player.start();
	}

	//skip to previous track
	public void playPrev(){
		Log.d("MusicService","called playPrev()");
		songPosn--;
		if(songPosn<0) songPosn=songs.size()-1;
		playSong();
	}

	//skip to next
	public void playNext(){
		Log.d("MusicService","called playNext()");
		if(shuffle){
			int newSong = songPosn;
			while(newSong==songPosn){
				newSong=rand.nextInt(songs.size());
			}
			songPosn=newSong;
		}
		else{
			songPosn++;
			if(songPosn>=songs.size()) songPosn=0;
		}
		playSong();
	}

	@Override
	public void onDestroy() {
		Log.d("MusicService","called onDestroy()");

		stopForeground(true);
	}

	//toggle shuffle
	public void setShuffle(){
		if(shuffle) shuffle=false;
		else shuffle=true;
	}

}
