package com.kimiyo.jonghoonkim.musicplayer2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class PlayActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {

    //activity and playback pause flags
    private boolean paused=false, playbackPaused=false, isPlaying = false;
    //service
    private MusicService musicSrv =null;
    private Intent playIntent=null;
    //binding
    private boolean musicBound=false;
    private ArrayList<Song> songList = null;

    //controller
    private MusicController controller = null;
    private LPView lpImageView =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        Log.i("PlayActivity","called onCreate()");
        Intent intent = getIntent();
        Song song = (Song)intent.getSerializableExtra("song");
        songList = new ArrayList<Song>();
        songList.add(song);

        //musicSrv.setSong(0);//Select 1st song

        //
        setListenerOfLPImage();

        setController();
    }

    private void setListenerOfLPImage() {
        lpImageView = findViewById(R.id.lp_view);
        lpImageView.setOnTouchListener(new View.OnTouchListener() {
            private void changePlayPosition() {
                double degree = lpImageView.getDegree();
                int quadrant = lpImageView.getQuadrantOfBall();
                int currentPosition = musicSrv.getPosn();
                int mins = (currentPosition / 60000);
                int secs = currentPosition - mins * 60000;
                int prevQuadrant= -1;
                if (secs <= 15000) prevQuadrant = 1;
                else if (secs <= 30000) prevQuadrant = 2;
                else if (secs <= 45000) prevQuadrant = 3;
                else if (secs <= 60000) prevQuadrant = 4;
                int newPosition = mins * 60000 + (int)((degree / 6.0) * 1000);
                if( prevQuadrant ==4 && quadrant ==1) newPosition += 60000;
                if( prevQuadrant ==1 && quadrant ==4) newPosition -= 60000;
                if( newPosition <0 ) {
                    lpImageView.setDegreeToPosOfCircle(0);
                    newPosition =0;
                    quadrant=1;
                }
                if (newPosition > musicSrv.getDur()){
                    newPosition = musicSrv.getDur();
                    mins = (newPosition / 60000);
                    secs = currentPosition - mins * 60000;
                    lpImageView.setDegreeToPosOfCircle(secs*6/1000);
                }
                Log.i("PlayActivity","changePlayPosition: getDegree:"+degree+",prevQuadrant:"+prevQuadrant+",quadrant:"+quadrant+",currentPosition:"+currentPosition+",newPosition:"+newPosition);
                musicSrv.seek(newPosition);
            }
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        lpImageView.onStartTouch(x,y);
                        lpImageView.invalidate();
                        //if(isPlaying)
                        {
                            changePlayPosition();
                        }

                        //long pos = getDegree / 60
                        break;
                    case MotionEvent.ACTION_MOVE:
                        lpImageView.moveTouch(x,y);
                        lpImageView.invalidate();
                        //if(isPlaying)
                        {
                            changePlayPosition();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        lpImageView.upTouch();
                        lpImageView.invalidate();
                        break;

                }
                return true;
            }

        });
    }

    //set the controller up
    private void setController(){
        controller = new MusicController(this);
        //set previous and next button listeners
        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });
        //set and show
        controller.setMediaPlayer(this);
        //controller.setAnchorView(findViewById(R.id.song_list));
        VideoView video = (VideoView) findViewById(R.id.audio_play);
        controller.setAnchorView(video);
        controller.setEnabled(true);
        //controller.show();

        //controller.show();//JH
    }

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("PlayActivity","called onServiceConnected() of musicConnection");
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list
            controller.show();
            setTimer();
            musicSrv.setList(songList);
            musicSrv.setSong(0);//JH
            musicBound = true;
            Log.i("PlayActivity","end onServiceConnected() of musicConnection");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    //start and bind the service when the activity starts
    @Override
    protected void onStart() {
        super.onStart();
        Log.i("PlayActivity","call onStart()");
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }
    @Override
    protected void onPause(){
        super.onPause();
        Log.i("PlayActivity","call onPause()");
        paused=true;
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.i("PlayActivity","call onResume()");
        if(paused){
            setController();
            paused=false;
        }
    }

    @Override
    protected void onStop() {
        Log.i("PlayActivity","call onStop()");
        controller.hide();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i("PlayActivity","call onDestroy()");
        if(playIntent!=null){
            stopService(playIntent);
            unbindService(musicConnection);
        }
        musicSrv=null;
        if(mTimer != null){
            mTimer.cancel();
            mTimer.purge();
        }
        super.onDestroy();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("PlayActivity","call onOptionsItemSelected()");
        //menu item selected
        switch (item.getItemId()) {
            case R.id.action_shuffle:
                musicSrv.setShuffle();
                break;
            case R.id.action_end:
                stopService(playIntent);
                musicSrv=null;
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void backToMain(View view) {
        finish();
    }


    private void playNext(){
        musicSrv.playNext();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        controller.show(0);
    }

    private void playPrev(){
        musicSrv.playPrev();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        controller.show(0);
    }


    @Override
    public void start() {
        Log.i("PlayActivity","call start()");
        musicSrv.playSong();
        //musicSrv.go();
    }

    @Override
    public void pause() {
        Log.i("PlayActivity","call pause()");
        playbackPaused=true;
        musicSrv.pausePlayer();
    }

    @Override
    public int getDuration() {
        Log.i("PlayActivity","call getDuration()");
        if(musicSrv!=null && musicBound && musicSrv.isPng())
            return musicSrv.getDur();
        else return 0;
    }


    @Override
    public int getCurrentPosition() {
        Log.i("PlayActivity","call getCurrentPosition()");
        if(musicSrv!=null && musicBound && musicSrv.isPng())
            return musicSrv.getPosn();
        else return 0;
    }

    @Override
    public void seekTo(int pos) {
        Log.i("PlayActivity","call seekTo()");

        musicSrv.seek(pos);
    }


    @Override
    public boolean isPlaying() {
        Log.i("PlayActivity","call isPlaying()");
        if(musicSrv!=null && musicBound)
            return musicSrv.isPng();
        return false;
    }

    @Override
    public int getBufferPercentage() {

        Log.i("PlayActivity","call getBufferPercentage()");
        return 0;
    }

    @Override
    public boolean canPause() {

        Log.i("PlayActivity","call canPause()");
        return true;
    }

    @Override
    public boolean canSeekBackward() {

        Log.i("PlayActivity","call canSeekBackward()");
        return true;
    }

    @Override
    public boolean canSeekForward() {

        Log.i("PlayActivity","call canSeekForward()");
        return true;
    }

    @Override
    public int getAudioSessionId() {

        Log.i("PlayActivity","call getAudioSessionId()");
        return 0;
    }


    public void playSongButton(View view) {
//        Log.i("Timer","called playSongButton");
//        musicSrv.setSong(0);//Select 1st song
//        musicSrv.playSong();
//        if(playbackPaused){
//            setController();
//            playbackPaused=false;
//        }
//        controller.show(0);
//        isPlaying = true;
    }

    public void pauseButton(View view) {
//        musicSrv.pausePlayer();
//        playbackPaused=false;
    }
    //----------------- LP Events ------
    Timer mTimer=null;
    TimerTask mTimerTask=null;
    private void setTimer() {
        mTimer = new Timer();

        mTimerTask = new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if(musicSrv.isPng()){
                            int currentPosition = musicSrv.getPosn();
                            int degree = (currentPosition % 60000)* 6/1000;
                            lpImageView.setDegreeToPosOfCircle(degree);
//                        Log.i("Timer",":"+currentPosition+" milliseconds, "+degree +" degree");
                        }
                    }
                });
            }
        };

        mTimer.scheduleAtFixedRate(mTimerTask, 0, 100);
    }
}
