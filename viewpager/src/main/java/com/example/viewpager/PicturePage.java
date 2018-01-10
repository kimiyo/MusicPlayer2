package com.example.viewpager;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by jonghoonkim on 2018. 1. 2..
 */

public class PicturePage extends LinearLayout {

    Context mContext;
    TextView title;
    ImageView imgView;
    Button msgButton;

    public PicturePage(Context context) {
        super(context);
        init(context);
    }
    public PicturePage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        LayoutInflater inflator = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        inflator.inflate(R.layout.viewpager, this, true);

        title = (TextView) findViewById(R.id.screenTitle);
        imgView = (ImageView) findViewById(R.id.img);
        msgButton = (Button) findViewById(R.id.btn01);


        msgButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String showMsg = (String)msgButton.getTag();
                Toast.makeText(getContext(), "Clicked Button:"+ showMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setMsg(String msg){
        msgButton.setTag(msg);
    }
    public void setImage(int imgId){
        Log.d("PictureActivity","imgId:"+imgId);
//        imgView.setImageResource(R.drawable.img1);//  .setImageResource(imgId);
        imgView.setImageResource(imgId);
    }
    public void setTitle(String titleStr){
        title.setText(titleStr);
    }

}
