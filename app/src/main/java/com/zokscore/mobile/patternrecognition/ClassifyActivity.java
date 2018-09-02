package com.zokscore.mobile.patternrecognition;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import weka.classifiers.Classifier;
import weka.core.Instances;
import com.zokscore.mobile.patternrecognition.classifiers.ActivityClassifier;
import com.zokscore.mobile.patternrecognition.classifiers.AndroidTrainClassifier;
import com.zokscore.mobile.patternrecognition.sensors.Sensors;
import com.zokscore.mobile.patternrecognition.sensors.SensorsService;

public class ClassifyActivity extends AppCompatActivity implements ServiceListener {


    private Intent sensorsIntent;
    private SensorsService sensorsService;
    private boolean sensorsServiceBound = false;

    private TextView hcTxtInfo1;
    private Button hcBtnStart;
    private Button hcBtnStop;

    private Instances instancesSet = null;
    private Classifier classifier = null;
    private int counter = 0;

    /**
     * Callbacks for service binding, passed to bindService()
     */
    private ServiceConnection sensorsServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // cast the IBinder and get MyService instance
            SensorsService.LocalBinder binder = (SensorsService.LocalBinder) service;
            sensorsService = binder.getService();

            sensorsService.setCallbacks(ClassifyActivity.this);

            blockButtons();
            sensorsServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

            sensorsServiceBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_classify);

        hcTxtInfo1 = findViewById(R.id.hc_txt_info1);
        hcBtnStart = findViewById(R.id.hc_btn_start);
        hcBtnStop = findViewById(R.id.hc_btn_stop);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        switch (item.getItemId()) {
            case R.id.menu_sensors:
                Intent intentSensors = new Intent(this, SensorsActivity.class);
                startActivity(intentSensors);
                return true;
            case R.id.menu_activity_mngmt:
                Intent intentAddAct = new Intent(this, ManagementActivity.class);
                startActivity(intentAddAct);
                return true;
            case R.id.menu_collect:
                Intent intentClassify = new Intent(this, CollectActivity.class);
                startActivity(intentClassify);
                return true;
            case R.id.menu_train:
                Intent intentTrain = new Intent(this, TrainActivity.class);
                startActivity(intentTrain);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (sensorsService.isRunning()) {
            menu.findItem(R.id.menu_collect).setEnabled(false);
            menu.findItem(R.id.menu_train).setEnabled(false);
        } else {
            menu.findItem(R.id.menu_collect).setEnabled(true);
            menu.findItem(R.id.menu_train).setEnabled(true);
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        //listAssetFiles();

        if (sensorsService != null) {
            sensorsService.setCallbacks(ClassifyActivity.this);
        }

        if (sensorsServiceBound) {
            blockButtons();
        }
    }

    @Override
    protected void onStart() {

        super.onStart();

        sensorsIntent = new Intent(this, SensorsService.class);
        bindService(sensorsIntent, sensorsServiceConnection, Context.BIND_AUTO_CREATE);
        startService(sensorsIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //onClickStop(null);
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    public void onShowStatus(final String text) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                hcTxtInfo1.setText(text);
                System.out.println(text);
            }
        });

    }

    @Override
    public void onSensorsError(Exception e) {

        Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSensorsStop() {
    }

    public void onClickStart(View view) {

        if (sensorsService.isRunning()) {

            return;
        }

        blockButtons();

        List<Integer> lista = Sensors.getInstance(this).getClassifySensors();

        for (Integer sensor : lista) {

            sensorsService.getSensors().addSensor(sensor);
        }
        startCollecting();
        invalidateOptionsMenu();
    }

    public void onClickStop(View view) {

        if (!sensorsService.isRunning()) {

            return;
        }
        sensorsService.stopCollecting();

        for (Integer sensor : Sensors.getInstance(this).getClassifySensors()) {

            sensorsService.getSensors().removeSensor(sensor);
        }
        blockButtons();

        if (sensorsServiceBound) {

            sensorsService.setCallbacks(null); // unregister
            unbindService(sensorsServiceConnection);
            sensorsServiceBound = false;
        }
        invalidateOptionsMenu();
    }

    public void startCollecting() {

        try {
            ActivityClassifier activityClassifier = new AndroidTrainClassifier(this, true);
            sensorsService.startCollecting(activityClassifier);
            blockButtons();

        } catch (Exception e) {

            e.printStackTrace(System.err);
            String message = e.getMessage();

            if (message == null && e.getCause() != null) {

                message = e.getCause().getMessage();
            }
            Toast.makeText(this, "Exception: " + message, Toast.LENGTH_LONG).show();
        }
    }

    public void blockButtons() {

        hcBtnStart.setEnabled(true);
        hcBtnStop.setEnabled(true);

        if (sensorsService.isRunning()) {

            hcBtnStop.setEnabled(true);
            hcBtnStart.setEnabled(false);
        } else {

            hcBtnStop.setEnabled(false);
            hcBtnStart.setEnabled(true);
        }
    }

    private void listAssetFiles() {

        AssetManager assetManager = getAssets();

        String filelist = "";
        String[] files;
        try {
            files = assetManager.list("");

            for (String s : files) {

                filelist += s + "\n";
            }

        } catch (Exception e) {

            hcTxtInfo1.setText(filelist);
        }
        hcTxtInfo1.setText(filelist);
    }

}
