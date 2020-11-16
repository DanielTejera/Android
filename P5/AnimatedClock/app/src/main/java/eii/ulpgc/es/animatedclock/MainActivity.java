package eii.ulpgc.es.animatedclock;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.Display;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AnalogClock;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener {

    private AnalogClock clock;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private AnimatorSet animatorSet;
    private int maxX;
    private int maxY;

    private final int ANIMATION_TIME = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        clock = (AnalogClock) findViewById(R.id.clock);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, R.string.sensor_error, Toast.LENGTH_SHORT).show();
        }

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        maxX = size.x;
        maxY = size.y;

        animatorSet = new AnimatorSet();

        clock.animate().translationX(maxX/2).start();
        clock.animate().translationY(maxY/2).start();
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onSensorChanged(SensorEvent event) {

        float x = Math.round(event.values[0]);
        float y = Math.round(event.values[1]);

        float degrees = (float) Math.toDegrees(Math.atan2(x, y));

        clock.setRotation(degrees);

        prepareAnimation(x, -y);
    }

    /**
     * Change the animation with the accelerometer values
     **/
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void prepareAnimation(float x, float y){

        animatorSet.end();
        animatorSet.cancel();

        float startY = clock.getY();
        float startX = clock.getX();

        float endY = startY - y;
        float endX = startX - x;

        ValueAnimator translateY = ObjectAnimator.ofFloat(clock, "y", startY, endY);
        translateY.setDuration(ANIMATION_TIME);
        translateY.setInterpolator(new AccelerateInterpolator());

        ValueAnimator translateX = ObjectAnimator.ofFloat(clock, "x", startX, endX);
        translateX.setDuration(ANIMATION_TIME);
        translateX.setInterpolator(new AccelerateInterpolator());

        if (startX + clock.getHeight() > maxX){
            translateX = ObjectAnimator.ofFloat(clock, "x", startX, startX);
        }else if (startX < 0){
            translateX = ObjectAnimator.ofFloat(clock, "x", startX, startX);
        }

        if (startY + clock.getHeight() > maxY){
            translateY = ObjectAnimator.ofFloat(clock, "y", startY, startY);
        }else if (startY < 0){
            translateY = ObjectAnimator.ofFloat(clock, "y", startY, startY);
        }

        animatorSet.playTogether(translateY, translateX);
        animatorSet.start();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
