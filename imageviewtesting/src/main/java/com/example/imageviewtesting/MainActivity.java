package com.example.imageviewtesting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ImageView lpImageView = findViewById(R.id.lp_view);
//        lpImageView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int action = event.getActionMasked();
//                int action2 = event.getAction();
//                Log.d( "JH on Touch","Clicked Image:"+ "action-"+action+", action2"+action2);
//                if(action2 == MotionEvent.ACTION_MOVE){
//                    float x = event.getX();
//                    float y = event.getY();
//                    Log.d( "JH on Touch","x:"+x+", y:"+y);
//                    return true;
//                }
//                return false;
//            }
//        });

    }

    public void playSongButton(View view) {
        Toast.makeText(this,"Pushed the button",Toast.LENGTH_LONG).show();
    }
}
