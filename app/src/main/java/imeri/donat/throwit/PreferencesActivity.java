package imeri.donat.throwit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class PreferencesActivity extends AppCompatActivity {
    public static final String sthreshold="threshold_value";

    private TextView txtProgress;
    private SeekBar seekBar;
    private Button btnSave;
    private int thresholdValue;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        //Initialize the variables
        txtProgress=(TextView) findViewById(R.id.txt_progress);
        seekBar=(SeekBar) findViewById(R.id.seekBar);
        btnSave=(Button)findViewById(R.id.btn_save);
        preferences=getSharedPreferences(MainActivity.spreferences,Context.MODE_PRIVATE);
        //Set the threshold value
        thresholdValue=preferences.getInt(sthreshold,1);
        seekBar.setProgress(thresholdValue);
        txtProgress.setText("Progress: "+thresholdValue);

        //Listen to seekbar changes and update the progress text
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtProgress.setText("Progress: "+progress);
                thresholdValue=progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Save the threshold value and return back to MainActivity
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor edit=preferences.edit();
                edit.putInt(sthreshold, thresholdValue);
                edit.apply();
                Intent intent=new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
