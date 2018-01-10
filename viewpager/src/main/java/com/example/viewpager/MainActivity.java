package com.example.viewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager vPager = (ViewPager) findViewById(R.id.viewPager);
        PictureAdapter adapter = new PictureAdapter(this);
        vPager.setAdapter(adapter);
    }

    public class PictureAdapter extends PagerAdapter{
        private Context mContext;

        private String[] titles = {"Page01","Page02","Page03"};
        private int[] imgId = {R.drawable.img1,R.drawable.img2,R.drawable.img3};
        private String[] msg = {"1st photo","2nd photo","3rd photo"};
        private PicturePage[] picturePages = new PicturePage[3];


        public PictureAdapter(Context context){
            mContext = context;
        }
        public Object instantiateItem(ViewGroup container, int position){
            Log.d("MainActivity","instantiateItem : position:"+position);
            PicturePage picturePage;
            if(picturePages[position] == null){
                picturePage = new PicturePage(mContext);

                picturePage.setImage(imgId[position]);
                picturePage.setMsg(msg[position]);

                container.addView(picturePage, position);

            } else {
                picturePage = picturePages[position];
            }

            return picturePage;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        public void destroyItem(ViewGroup container, int position, Object view){
            container.removeView((View)view);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }
    }
}
