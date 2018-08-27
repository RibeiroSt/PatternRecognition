package zokscore.com.mobile.patternrecognition;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import zokscore.com.mobile.patternrecognition.db.DatabaseHandler;
import zokscore.com.mobile.patternrecognition.recognition.ClassifierTrainData;
import zokscore.com.mobile.patternrecognition.sensors.Sensors;
import zokscore.com.mobile.patternrecognition.sensors.SensorsService;

public class CollectActivity extends AppCompatActivity implements ServiceListener {

    public static final String FILE_NAME = "dados.arff";

    private Intent sensorsIntent;
    private SensorsService sensorsService;
    private boolean sensorsServiceBound = false;
    private String userActivity;
    private int line = 0;

    private TextView hcTxtInfo1;
    private Spinner chooseUserActivity;
    private Button startButton;
    private Button stopButton;
    private Button deleteButton;

    private FileWriter fileWriter;
    private File file;


    /** Callbacks for service binding, passed to bindService() */
    private ServiceConnection sensorsServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // cast the IBinder and get MyService instance
            SensorsService.LocalBinder binder = (SensorsService.LocalBinder) service;
            sensorsService = binder.getService();

            sensorsService.setCallbacks(CollectActivity.this);

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
        setContentView(R.layout.activity_collect);

        hcTxtInfo1 = findViewById(R.id.textView2);
        chooseUserActivity = findViewById(R.id.choose_activity);
        startButton = findViewById(R.id.start);
        stopButton = findViewById(R.id.stop);
        deleteButton = findViewById(R.id.delete);

        loadSpinnerData();

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
            case R.id.menu_classify:
                Intent intentClassify = new Intent(this, ClassifyActivity.class);
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

    private void loadSpinnerData() {

        // database handler
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());

        // Spinner Drop down elements
        List<String> actividades = db.getAllActivities();

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, actividades);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        chooseUserActivity.setAdapter(dataAdapter);

        file = new File(getFilesDir(), FILE_NAME);

    }


    @Override
    protected void onStart() {

        super.onStart();

        sensorsIntent = new Intent(this, SensorsService.class);
        bindService(sensorsIntent, sensorsServiceConnection, Context.BIND_AUTO_CREATE);
        startService(sensorsIntent);
    }

    @Override
    protected void onStop() {

        super.onStop();

        if (sensorsServiceBound) {
            sensorsService.unregisterComponentCallbacks(CollectActivity.this);
            unbindService(sensorsServiceConnection);
            sensorsServiceBound = false;
        }
    }


    public void onClickStart(View view) {


        if (sensorsService.isRunning()) {

            return;
        }
        userActivity = (String) chooseUserActivity.getSelectedItem();
        line = 0;

        try {
            if (!file.exists()) {

                file.createNewFile();
            }
            fileWriter = new FileWriter(file, true);

            if (file.length() == 0) {

                DatabaseHandler db = new DatabaseHandler(this);
                String classes = null;
                List<String> classesList = db.getAllActivities();
                for(String c : classesList) {
                    if( classes == null ) {
                        classes = "'" + c + "'";
                    } else {
                        classes += ", '" + c + "'";
                    }
                }

                fileWriter.append("@RELATION sample\n" +
                        "\n" +
                        "@ATTRIBUTE 'acc_fft_1' real\n" +
                        "@ATTRIBUTE 'acc_fft_2' real\n" +
                        "@ATTRIBUTE 'acc_fft_3' real\n" +
                        "@ATTRIBUTE 'acc_fft_4' real\n" +
                        "@ATTRIBUTE 'acc_fft_5' real\n" +
                        "@ATTRIBUTE 'acc_fft_6' real\n" +
                        "@ATTRIBUTE 'acc_fft_7' real\n" +
                        "@ATTRIBUTE 'acc_fft_8' real\n" +
                        "@ATTRIBUTE 'acc_fft_9' real\n" +
                        "@ATTRIBUTE 'acc_fft_10' real\n" +
                        "@ATTRIBUTE 'acc_fft_11' real\n" +
                        "@ATTRIBUTE 'acc_fft_12' real\n" +
                        "@ATTRIBUTE 'acc_fft_13' real\n" +
                        "@ATTRIBUTE 'acc_fft_14' real\n" +
                        "@ATTRIBUTE 'acc_fft_15' real\n" +
                        "@ATTRIBUTE 'acc_fft_16' real\n" +
                        "@ATTRIBUTE 'acc_fft_17' real\n" +
                        "@ATTRIBUTE 'acc_fft_18' real\n" +
                        "@ATTRIBUTE 'acc_fft_19' real\n" +
                        "@ATTRIBUTE 'acc_fft_20' real\n" +
                        "@ATTRIBUTE 'acc_fft_21' real\n" +
                        "@ATTRIBUTE 'acc_fft_22' real\n" +
                        "@ATTRIBUTE 'acc_fft_23' real\n" +
                        "@ATTRIBUTE 'acc_fft_24' real\n" +
                        "@ATTRIBUTE 'acc_fft_25' real\n" +
                        "@ATTRIBUTE 'acc_fft_26' real\n" +
                        "@ATTRIBUTE 'acc_fft_27' real\n" +
                        "@ATTRIBUTE 'acc_fft_28' real\n" +
                        "@ATTRIBUTE 'acc_fft_29' real\n" +
                        "@ATTRIBUTE 'acc_fft_30' real\n" +
                        "@ATTRIBUTE 'acc_fft_31' real\n" +
                        "@ATTRIBUTE 'acc_fft_32' real\n" +
                        "@ATTRIBUTE 'acc_fft_33' real\n" +
                        "@ATTRIBUTE 'acc_fft_34' real\n" +
                        "@ATTRIBUTE 'acc_fft_35' real\n" +
                        "@ATTRIBUTE 'acc_fft_36' real\n" +
                        "@ATTRIBUTE 'acc_fft_37' real\n" +
                        "@ATTRIBUTE 'acc_fft_38' real\n" +
                        "@ATTRIBUTE 'acc_fft_39' real\n" +
                        "@ATTRIBUTE 'acc_fft_40' real\n" +
                        "@ATTRIBUTE 'acc_fft_41' real\n" +
                        "@ATTRIBUTE 'acc_fft_42' real\n" +
                        "@ATTRIBUTE 'acc_fft_43' real\n" +
                        "@ATTRIBUTE 'acc_fft_44' real\n" +
                        "@ATTRIBUTE 'acc_fft_45' real\n" +
                        "@ATTRIBUTE 'acc_fft_46' real\n" +
                        "@ATTRIBUTE 'acc_fft_47' real\n" +
                        "@ATTRIBUTE 'acc_fft_48' real\n" +
                        "@ATTRIBUTE 'acc_fft_49' real\n" +
                        "@ATTRIBUTE 'acc_fft_50' real\n" +
                        "@ATTRIBUTE 'acc_fft_51' real\n" +
                        "@ATTRIBUTE 'acc_fft_52' real\n" +
                        "@ATTRIBUTE 'acc_fft_53' real\n" +
                        "@ATTRIBUTE 'acc_fft_54' real\n" +
                        "@ATTRIBUTE 'acc_fft_55' real\n" +
                        "@ATTRIBUTE 'acc_fft_56' real\n" +
                        "@ATTRIBUTE 'acc_fft_57' real\n" +
                        "@ATTRIBUTE 'acc_fft_58' real\n" +
                        "@ATTRIBUTE 'acc_fft_59' real\n" +
                        "@ATTRIBUTE 'acc_fft_60' real\n" +
                        "@ATTRIBUTE 'acc_fft_61' real\n" +
                        "@ATTRIBUTE 'acc_fft_62' real\n" +
                        "@ATTRIBUTE 'acc_fft_63' real\n" +
                        "@ATTRIBUTE 'acc_fft_64' real\n" +
                        "@ATTRIBUTE 'acc_va_max' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_1' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_2' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_3' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_4' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_5' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_6' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_7' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_8' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_9' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_10' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_11' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_12' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_13' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_14' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_15' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_16' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_17' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_18' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_19' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_20' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_21' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_22' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_23' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_24' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_25' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_26' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_27' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_28' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_29' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_30' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_31' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_32' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_33' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_34' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_35' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_36' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_37' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_38' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_39' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_40' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_41' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_42' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_43' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_44' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_45' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_46' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_47' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_48' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_49' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_50' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_51' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_52' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_53' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_54' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_55' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_56' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_57' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_58' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_59' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_60' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_61' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_62' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_63' real\n" +
                        "@ATTRIBUTE 'gyroscope_fft_64' real\n" +
                        "@ATTRIBUTE 'gyroscope_va_max' real\n" +
                        "@ATTRIBUTE 'stepdetector' real\n" +
                        "@ATTRIBUTE 'activity' {"+classes+"}\n" +
                        "\n" +
                        "@DATA\n");

            }

            for (Integer sensor: Sensors.getInstance(this).getClassifySensors()) {

                sensorsService.getSensors().addSensor(sensor);
            }
            sensorsService.startCollectingToClassify(new ClassifierTrainData(this));

        } catch (Exception e) {

            e.printStackTrace(System.err);
            Toast.makeText(this, "Exception: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        blockButtons();
    }

    public void onClickStop(View view) {

        if (!sensorsService.isRunning()) {

            return;
        }
        sensorsService.stopCollecting();

        for (Integer sensor: Sensors.getInstance(this).getClassifySensors()) {

            sensorsService.getSensors().removeSensor(sensor);
        }
        blockButtons();

        try {
            if( fileWriter!=null ) {
                fileWriter.close();
            }
            refreshFiles();

        } catch (Exception e) {

            e.printStackTrace(System.err);
            Toast.makeText(this, "Exception: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public void onClickDelete(View view) {

        if (sensorsService.isRunning()) {

            return;
        }

        try {
            file.delete();
            refreshFiles();

        } catch (Exception e) {

            e.printStackTrace(System.err);
            Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        blockButtons();
    }


    @Override
    public void onShowStatus(final String text) {

        line++;
        try {
            if( fileWriter==null ) {
                fileWriter = new FileWriter(file, true);
            }
            fileWriter.append(text.replace("?","'" + userActivity + "'\n"));

        } catch (Exception e) {

            Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                hcTxtInfo1.setText("Generated line number: " + line + "\n" + text.replace("?","'" + userActivity + "'"));
                System.out.println(text.replace("?","'" + userActivity + "'"));
            }
        });
    }

    @Override
    public void onSensorsError(Exception e) {

    }

    @Override
    public void onSensorsStop() {

    }

    @Override
    public boolean isClassifying() {
        return true;
    }



    public void blockButtons() {

        chooseUserActivity.setEnabled(true);
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        deleteButton.setEnabled(false);

        if (sensorsService.isRunning()) {

            stopButton.setEnabled(true);
            chooseUserActivity.setEnabled(false);
            startButton.setEnabled(false);

        } else {

            if (file.length() > 0) {

                deleteButton.setEnabled(true);
            }
        }
    }


    private void refreshFiles() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(file);
            mediaScanIntent.setData(contentUri);
            sendBroadcast(mediaScanIntent);

        } else {

            sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + getFilesDir())));
        }
    }
}
