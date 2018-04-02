package com.journaldev.externalstorage;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;



public class MainActivity extends Activity implements SensorEventListener{
    private Handler       mHandler;
    private HandlerThread mHandlerThread;
    private SensorManager mSensorManager;
    private Sensor        mSensor;
    List<String> inputText = new ArrayList<String>();
    TextView response;
    Button saveButton,readButton,startButton,endButton;

    private String filename = "SampleFile.txt";
    private String filepath = "MyFileStorage";
    File myExternalFile;
    String myData = "";

    //private SensorManager sensorManager;
    int click = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //setContentView(R.layout.activity_handler);


        //inputText = (EditText) findViewById(R.id.myInputText);
        response = (TextView) findViewById(R.id.response);
        //sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //SensorManager mSensorMgr = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        //lastUpdate = System.currentTimeMillis();

        startButton = (Button) findViewById(R.id.starSensor);
        endButton = (Button) findViewById(R.id.endSensor);

        startButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                click = 1;
                response.setText("Recording...");
            }

        });

        endButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                click = 0;
                response.setText("End Recording...");

            }

        });

        saveButton =
                (Button) findViewById(R.id.saveExternalStorage);
        saveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FileOutputStream fos = new FileOutputStream(myExternalFile);
                    fos.write(inputText.toString().getBytes());
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //inputText.setText("");
                response.setText("SampleFile.txt saved to External Storage...");
            }
        });


        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            saveButton.setEnabled(false);
        }
        else {
            myExternalFile = new File(getExternalFilesDir(filepath), filename);
        }

        mHandlerThread = new HandlerThread("SensorThread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


    }
    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {

            runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  if ((event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && click > 0) || (event.sensor.getType() == Sensor.TYPE_GYROSCOPE && click > 0)) {

                                      if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                                          long actualTime = event.timestamp;
                                          float gX = event.values[0];
                                          float gY = event.values[1];
                                          float gZ = event.values[2];
                                          inputText.add(event.timestamp + "," + "A" + "," + gX + "," + gY + "," + gZ + "\n");

                                      } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                                          long actualTime = event.timestamp;
                                          inputText.add(actualTime + "," + "G" + "," + event.values[0] + "," + event.values[1] + "," + event.values[2] + "\n");
                                      }
                                  }
                              }
                          });

    }




    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Global
        // ...Registration
        HandlerThread mHandlerThread = new HandlerThread("sensorThread");
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST, mHandler);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_FASTEST,mHandler);
        mHandlerThread.start();

    }

    @Override
    protected void onPause(){   // unregister listener
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    }

