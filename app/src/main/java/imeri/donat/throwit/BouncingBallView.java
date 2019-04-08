package imeri.donat.throwit;

import android.app.Activity;
import android.content.Context;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import java.util.Calendar;

public class BouncingBallView extends View {
    public static final String slastHeight="last_height";

    private DisplayMetrics displayMetrics;
    int screenHeight, screenWidth;
    int ballWidth, ballHeight;
    String scoreText;
    private Paint paint;
    private float timeFlying; //the time that the ball is up
    private long startTime;
    private float s, initialVelocity; //s is the distance the ball travels
    private boolean show; //show=true when ball is in "our hand" i.e. in our phone
    //and show=false when the ball is thrown from "our hand" i.e. our phone
    private SharedPreferences preferences;

    public BouncingBallView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);

        //Get and set display parameters
        displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;

        //Initialize the other variables
        preferences=context.getSharedPreferences(MainActivity.spreferences,Context.MODE_PRIVATE);
        ballWidth=150;
        ballHeight=150;

        paint=new Paint();
        paint.setARGB(255,255,255,255); //Set the text color to white
        paint.setTextSize(50);
        paint.setTextAlign(Paint.Align.CENTER);

        scoreText="Height: 0.0 m";
        s=0.0f;
        show=true;
    }

    //Drawing method, for changing the text and showing or hiding the ball on the canvas
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //If ball is in our hand draw it, if it is thrown the ball is not in our hand
        //so we don't see it in our app
        if (show) {
            Bitmap ball = BitmapFactory.decodeResource(getResources(), R.drawable.basketball);
            ball = Bitmap.createScaledBitmap(ball, ballWidth, ballHeight, false);
            canvas.drawBitmap(ball, (screenWidth / 2) - (ballWidth / 2), (screenHeight / 2) - (ballHeight / 2), null);
        }
        canvas.drawText(scoreText, (screenWidth/2), screenHeight-240, paint);
    }

    public boolean moveUp(){
        //The time since the ball has started moving
        float t=(float)((Calendar.getInstance().getTimeInMillis()-startTime)/1000.0);

        //If the ball has not achieved the highest point yet continue increasing the distance
        if (t<timeFlying) {
            calculateDistance(t,true);
            postInvalidate();
            return true;
        }
        else{
            t=timeFlying;
            calculateDistance(t, true);
            postInvalidate();

            SharedPreferences.Editor editor = preferences.edit();
            editor.putFloat(slastHeight,s);
            if (preferences.getFloat(MainActivity.shighscore,0.0f)<s) {
                editor.putFloat(MainActivity.shighscore, s);
            }
            editor.apply();
            return false;
        }


    }

    public boolean moveDown(){
        float t=(float)((Calendar.getInstance().getTimeInMillis()-startTime)/1000.0);
        if (t<timeFlying) {
            calculateDistance(t, false);
            postInvalidate();
            return true;
        }
        else{
            t=timeFlying;
            calculateDistance(t, false);
            postInvalidate();
            return false;
        }
    }

    public void disappearBall(){
        show=false;
        postInvalidate();
    }

    public void reappearBall(){
        show=true;
        postInvalidate();
    }

    //This method rewrites the distance of the body at a particular time
    public void calculateDistance(float t, boolean up){
        if (up){
            s= (float) ((initialVelocity*t)-(MainActivity.EARTH_GRAVITY*Math.pow(t,2.0))*0.5f);
            s=Math.round(s*100.0)/100.0f;
            scoreText="Height: "+s+" m";
        }
        //else if the body is going down
        else{
            float d= (float) ((MainActivity.EARTH_GRAVITY*Math.pow(t,2.0))*0.5f);
            d=Math.round((s-d)*100.0)/100.0f;
            scoreText="Height: "+d+" m";
        }

    }

    public void setTimeFlying(float v){
        timeFlying=v;
    }

    public void setInitialVelocity(float v){
        initialVelocity=v;
    }

    public void setStartingTime(long startingTime){
        startTime=startingTime;
    }

}
