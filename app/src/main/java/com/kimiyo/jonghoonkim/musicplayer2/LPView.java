package com.kimiyo.jonghoonkim.musicplayer2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by jonghoonkim on 2017. 12. 14..
 */

public class LPView extends View {

    public int width;
    public int height;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    //private Path mPath;
    private Paint mPaint;
    //private float mX, mY;
    private static final float TOLERANCE = 5;
    private Context context;
    private Bitmap mLPImage;
    private Point centerPoint = new Point(0,0);
    private int mRadius;
    private int ballWidth=40;
    private double mDegree=0;
    private Point ballPoint = new Point(0,0); //(int) mX, (int) mY);

    private Bitmap mDial;

    public LPView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        //mPath = new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(4f);
        BitmapDrawable lp = (BitmapDrawable)context.getResources().getDrawable(R.drawable.lp);
        mLPImage = lp.getBitmap();
        BitmapDrawable dial = (BitmapDrawable)context.getResources().getDrawable(R.drawable.dial);
        mDial = dial.getBitmap();

    }
    public double getDegree(){
        return mDegree;
    }

    public int getQuadrantOfBall(){
        int quadrant = 1;
        if (mDegree <= 90)  quadrant = 1;
        else if (mDegree <= 180)  quadrant = 2;
        else if (mDegree <= 270)  quadrant = 3;
        else if (mDegree <= 360)  quadrant = 4;
        return quadrant;
    }

    private Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    private double getDegreeOfCircle(Point pos,Point center){

        int posX = center.x - pos.x;
        int posY = center.y - pos.y;
        double atanval = Math.atan((double)posX/(double)(-posY));
        double atanval2 = -atanval*180/Math.PI;
        double degree = 0;
        if(posY > 0 ) {
            if(posY == 0) {degree = 90;}
            else if(posX > 0) {degree = atanval2;}
            else {degree = 360 +atanval2;}
        }
        else {
            if(posY == 0) {degree = 270;}
            else if(posX > 0) {degree = 180+ atanval2;}
            else {degree = 180 + atanval2;}
        }
        degree = 360 - degree;
        //Log.i("LPView","getDegreeOfCircle(), degree:"+degree);
        return degree;
    }
    private Point getDegreeToPosOfCircle(double degree,Point center,int radius){
        degree = 360 - degree;
        Point pos = new Point(0, 0);
        pos.x = (int)( (float)radius * Math.sin(degree / 180 * Math.PI) );
        pos.y = (int) ( (float)radius* Math.cos(degree / 180 * Math.PI) );
        pos.x =  center.x - pos.x ;
        pos.y =  center.y - pos.y;
        return pos;
    }

    public void setDegreeToPosOfCircle(double degree){
        this.mDegree =  degree;
        degree = 360 - degree;
        Point pos = new Point(0, 0);
        pos.x = (int)( (float)mRadius * Math.sin(degree / 180 * Math.PI) );
        pos.y = (int) ( (float)mRadius* Math.cos(degree / 180 * Math.PI) );
        pos.x =  centerPoint.x - pos.x ;
        pos.y =  centerPoint.y - pos.y;
        ballPoint = pos;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w,int h,int oldw, int oldh) {
        super.onSizeChanged(w,h,oldw,oldh);
        mBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        mLPImage = getResizedBitmap(mLPImage,w,h);
        centerPoint.x = w/2; centerPoint.y = h/2; mRadius = (int) ((float)centerPoint.x * 0.7);
        mDial = getResizedBitmap(mDial,ballWidth,ballWidth);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        //canvas.drawBitmap(lp.getBitmap(),0,0,null);
        canvas.drawBitmap(mLPImage,0,0,null);
        int ballPosX = ballPoint.x - ballWidth/2;
        int ballPosY = ballPoint.y - ballWidth/2;

        canvas.drawBitmap(mDial,ballPosX,ballPosY,null);

        //canvas.drawPath(mPath, mPaint);
    }

    public void onStartTouch(float x, float y){
        //mPath.moveTo(x,y);
        ballPoint.x = (int)x;
        ballPoint.y = (int)y;
        mDegree = getDegreeOfCircle(ballPoint,centerPoint);
        ballPoint = getDegreeToPosOfCircle(mDegree, centerPoint, mRadius);

//        mX = x;
//        mY = y;
    }

    public void moveTouch(float x, float y){
        float dx = Math.abs(x - ballPoint.x);
        float dy = Math.abs(y - ballPoint.y);
        if(dx >= TOLERANCE || dy >= TOLERANCE) {
            //mPath.quadTo(ballPoint.x, ballPoint.y, (x + ballPoint.x)/2, (y + ballPoint.y)/2);
            ballPoint.x = (int)x;
            ballPoint.y = (int)y;
            mDegree = getDegreeOfCircle(ballPoint,centerPoint);
            ballPoint = getDegreeToPosOfCircle(mDegree, centerPoint, mRadius);
//            mX = x;
//            mY = y;
        }
    }

    public void clearCanvas(){
        //mPath.reset();
        invalidate();
    }

    public void upTouch(){
        //mPath.lineTo(mX,mY);
    }


//    @Override
//    public boolean onTouchEvent(MotionEvent event){
//        float x = event.getX();
//        float y = event.getY();
//        switch (event.getAction()){
//            case MotionEvent.ACTION_DOWN:
//                onStartTouch(x,y);
//                invalidate();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                moveTouch(x,y);
//                invalidate();
//                break;
//            case MotionEvent.ACTION_UP:
//                upTouch();
//                invalidate();
//                break;
//
//        }
//        return true;
//    }
}
