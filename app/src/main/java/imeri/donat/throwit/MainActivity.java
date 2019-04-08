package imeri.donat.throwit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    public static final String shighscore="highscore";
    public static final String spreferences="preferences";
    public static final float EARTH_GRAVITY=9.81f;
    private final static int RC_PREFERENCES=1;


    private SensorManager sensorManager;
    private Sensor sensor;
    private int counter, threshold; //counter is used for the sliding window size
    private float actualMax; //The maximum value founded in the sliding window
    private boolean muted; //Sound on or off
    private BouncingBallView bouncingBallView;
    private TextView score, txtHighscore;
    private MediaPlayer mp, mpHit, mpDecrease; //mp=Sound when ball is moving up, mpHit=when ball
    //in it's highest position and mpDecrease=when ball is moving downwards
    private ImageView btnPreferences, btnSound;
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize the variables
        counter=0;
        actualMax=-20;
        muted=true;
        bouncingBallView=(BouncingBallView)findViewById(R.id.ball_view);
        btnPreferences=(ImageView)findViewById(R.id.btn_preferences);
        btnSound=(ImageView)findViewById(R.id.btn_sound);
        txtHighscore=(TextView)findViewById(R.id.text_highscore);
        score=(TextView)findViewById(R.id.score_text);

        //Set the high score and threshold value
        preferences = getSharedPreferences(spreferences,Context.MODE_PRIVATE);
        txtHighscore.setText("HIGHSCORE\n"+preferences.getFloat(shighscore, 0.0f));
        threshold=preferences.getInt(PreferencesActivity.sthreshold,1);


        //Sensor Manager Initialization
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);

        //Initialize the sounds
        mp=MediaPlayer.create(this, R.raw.increase);
        mpHit=MediaPlayer.create(this, R.raw.hit);
        mpDecrease=MediaPlayer.create(this,R.raw.decrease);

        //Listener for preferences button onclick
        btnPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPreferences(v);
            }
        });

        //Sound on or off control
        btnSound.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (muted){
                    btnSound.setImageResource(R.drawable.sound);
                    mp.setVolume(0,0);
                    mpHit.setVolume(0,0);
                    mpDecrease.setVolume(0,0);
                    muted=false;
                }
                else{
                    btnSound.setImageResource(R.drawable.mute);
                    mp.setVolume(1,1);
                    mpHit.setVolume(1,1);
                    mpDecrease.setVolume(1,1);
                    muted=true;
                }
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        counter++;
        //Calculate ACC
        float ACC=Math.abs((float)(Math.sqrt((event.values[0]*event.values[0])+(event.values[1]*event.values[1])
            +(event.values[2]*event.values[2])))-EARTH_GRAVITY);
        //Set the maximum ACC
        if (actualMax<ACC)actualMax=ACC;

        //Sliding window size =20
        if (counter>=19){
            counter=0;
            if (actualMax>threshold){
                //If window size full and the maximum ACC is greater than the threshold then
                //Stop listening to sensor data
                sensorManager.unregisterListener(this, sensor);
                //Start moving the ball
                throwCheck();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public void throwCheck() {

        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    animateBall();
                    //After the ball is back restart listening for sensor data
                    sensorManager.registerListener(MainActivity.this, sensor, SensorManager.SENSOR_DELAY_GAME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();

    }

    //This methods runs on the new created thread and it's purpose is to call the
    //drawing methods in the BouncingBallView class
    private void animateBall() throws InterruptedException {
        long actual=Calendar.getInstance().getTimeInMillis();

        //Set the time when the ball start moving
        bouncingBallView.setStartingTime(actual);
        //Set the initial velocity (i.e. the max ACC)
        bouncingBallView.setInitialVelocity(actualMax);
        //Calculate and set the total time that the ball is going to move upwards
        float t=(actualMax/EARTH_GRAVITY);
        bouncingBallView.setTimeFlying(t);

        //Notify the user that the ball is thrown and it's not anymore in his/her hand
        bouncingBallView.disappearBall();
        //Start playing the upward sound
        mp.start();
        while (bouncingBallView.moveUp())Thread.sleep(50);
        mp.pause();

        mpHit.start();
        //Set the time when ball is starting to fall
        actual=Calendar.getInstance().getTimeInMillis();
        bouncingBallView.setStartingTime(actual);
        mpDecrease.start();
        while (bouncingBallView.moveDown())Thread.sleep(50);
        mpDecrease.pause();

        //Notify the user that the ball is returned back and it's in his/her hand
        bouncingBallView.reappearBall();

        //Change the highscore if a new one is set notify the user for it's last score
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtHighscore.setText("HIGHSCORE\n"+preferences.getFloat(shighscore, 0.0f));
                score.setText("Last height: "+preferences.getFloat(BouncingBallView.slastHeight, 0.00f));
            }
        });

        //Reset the sounds and maximum ACC
        mp.seekTo(0);
        mpDecrease.seekTo(0);
        actualMax=-20;
    }

    //Stop the sensor listener and start PreferencesActivity
    public void openPreferences(View view) {
        sensorManager.unregisterListener(this, sensor);
        Intent intent=new Intent(MainActivity.this, PreferencesActivity.class);
        startActivityForResult(intent, RC_PREFERENCES);
    }


    //After getting back from the PreferencesActivity change the threshold value
    //and start listening for new throws again
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (requestCode==RC_PREFERENCES && resultCode==RESULT_OK){
                threshold=preferences.getInt(PreferencesActivity.sthreshold, threshold);
            }
        sensorManager.registerListener(MainActivity.this, sensor, SensorManager.SENSOR_DELAY_GAME);
    }


    //When the user presses the back button the app should quit 
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }
}
