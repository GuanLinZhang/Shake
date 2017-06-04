package com.zlx.shake;

import android.database.CursorIndexOutOfBoundsException;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    Vibrator mVibrator;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    boolean isOK=true;

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.id_tv);
        //获得振动器服务
        mVibrator = (Vibrator) getApplication().getSystemService(VIBRATOR_SERVICE);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ALL);

    }

    private void setScreenBritness(int brightness) {
        // 获取系统亮度
        try {
            Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            // 设置系统亮度
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);

    }

    private long currentTime;
    private long lastTime;
    private long distanceTime;
    private long changeTime;

    private boolean is = false;

    private double SPEED_SHAREHOLD = 200;
    private float last_x;//记录x轴最后一次的值
    private float last_y;//记录y轴最后一次的值
    private float last_z;//记录z轴最后一次的值
    private float x, y, z;

    @Override
    public void onSensorChanged(SensorEvent event) {
         //wang
        currentTime = System.currentTimeMillis();
        if(currentTime - changeTime >2000)
            isOK=true;

        if (currentTime - lastTime > 100) {
            //两次摇动时间间隔
            distanceTime = currentTime - lastTime;
            lastTime = currentTime;
            //当前值
            x = event.values[0];
            y = event.values[1];
            z = event.values[2];
            tv.setText(x + " " + y + " " + z);
            double speed;
            //double absValue = Math.abs(x + y + z - last_x - last_y - la   st_z);
            double absValue = Math.abs( z - last_z) * 5;

            speed = absValue / distanceTime * 10000;
            if (speed > SPEED_SHAREHOLD && isOK) {
                //当x/y/z达到一定值，进行后续操作
                Log.e("TAG", "ok--------------------");
                if (is)
                {
                    setScreenBritness(255);
                    changeTime = System.currentTimeMillis();
                    isOK=false;
                }
                else
                {
                    setScreenBritness(0);
                    changeTime = System.currentTimeMillis();
                    isOK=false;
                }

                is = !is;
            }
            last_x = x;
            last_y = y;
            last_z = z;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
