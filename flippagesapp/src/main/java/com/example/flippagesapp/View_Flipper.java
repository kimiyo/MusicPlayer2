package com.example.flippagesapp;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

/**
 * Created by jonghoonkim on 2017. 12. 30..
 */

public class View_Flipper extends LinearLayout implements View.OnTouchListener{
    public static int cntIndex = 3;
    LinearLayout indexLayout;
    ImageView[] indexImgs;
    View[] views;
    ViewFlipper viewFlipper;
    float startX;
    float endX;
    int currentIndex = 0;
    public View_Flipper(Context context){
        super(context);
        init(context);
    }
    public View_Flipper(Context context, AttributeSet attrs){
        super(context,attrs);
        init(context);
    }
    public void init(Context context){
        setBackgroundColor(0xffbbbbff);
        LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflator.inflate(R.layout.flipper, this, true);

        indexLayout = (LinearLayout)findViewById(R.id.screenIdx);
        viewFlipper = (ViewFlipper) findViewById(R.id.flipper);
        viewFlipper.setOnTouchListener(this);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = 50;

        indexImgs = new ImageView[cntIndex];
        views = new TextView[cntIndex];

        for(int i=0;i < cntIndex;i++){
            indexImgs[i] = new ImageView(context);
            if(i == currentIndex){
                indexImgs[i].setImageResource(android.R.drawable.btn_star_big_on);
            } else {
                indexImgs[i].setImageResource(android.R.drawable.btn_star_big_off);
            }

            indexLayout.addView(indexImgs[i], params);

            TextView currentView = new TextView(context);
            currentView.setText(i+1+"화면");
            currentView.setTextSize(30);
            views[i] = currentView;

            viewFlipper.addView(views[i]);

        }

    }

    private void modifyIndex() {
        for(int i = 0;i< cntIndex; i++){
            if(i == currentIndex){
                indexImgs[i].setImageResource(android.R.drawable.btn_star_big_on);
                Log.d("modifyIndex","Image On Selected:"+ i);
            } else {
                indexImgs[i].setImageResource(android.R.drawable.btn_star_big_off);
                Log.d("modifyIndex","Image Off:"+ i);
            }
        }
    }

    public boolean onTouch(View v, MotionEvent event){
        if(v != viewFlipper) return false;
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            startX = event.getX();
        } else if(event.getAction() == MotionEvent.ACTION_UP)  {
            endX = event.getX();
            if(startX < endX ) { // Left to Right
                Animation leftin = AnimationUtils.loadAnimation(getContext(),R.anim.left_in);
                viewFlipper.setInAnimation(leftin);
                Animation rightOut = AnimationUtils.loadAnimation(getContext(),R.anim.right_out);
                viewFlipper.setOutAnimation(rightOut);

                if(currentIndex > 0) {
                    viewFlipper.showPrevious();
                    currentIndex--;
                    modifyIndex();
                    Log.d("onTouch","currentIndex:"+ currentIndex);
                }
            } else if(startX > endX ) { // Right to Left
                Animation rightin = AnimationUtils.loadAnimation(getContext(),R.anim.right_in);
                viewFlipper.setInAnimation(rightin);
                Animation leftOut = AnimationUtils.loadAnimation(getContext(),R.anim.left_out);
                viewFlipper.setOutAnimation(leftOut);

                if(currentIndex < (cntIndex -1)) {
                    viewFlipper.showNext();
                    currentIndex++;
                    modifyIndex();
                    Log.d("onTouch","currentIndex:"+ currentIndex);
                }
            }
        }
        return true;
    }


}
