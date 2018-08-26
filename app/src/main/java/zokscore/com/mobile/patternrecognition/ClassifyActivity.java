package zokscore.com.mobile.patternrecognition;

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
import zokscore.com.mobile.patternrecognition.classifiers.ActivityClassifier;
import zokscore.com.mobile.patternrecognition.classifiers.AndroidTrainClassifier;
import zokscore.com.mobile.patternrecognition.sensors.Sensors;
import zokscore.com.mobile.patternrecognition.sensors.SensorsService;

public class ClassifyActivity extends AppCompatActivity implements ServiceListener {


    private Intent sensorsIntent;
    private SensorsService sensorsService;
    private boolean sensorsServiceBound = false;

    private TextView hcTxtInfo1;
    private Button hcBtnStart;
    private Button hcBtnStop;
    private Spinner chooseClassifier;
    private ImageView activityImage;

    private Bitmap andar;
    private Bitmap andarDeCarro;
    private Bitmap correr;
    private Bitmap descer;
    private Bitmap nenhuma;
    private Bitmap saltar;
    private Bitmap subir;
    private Bitmap vazio;

    private Instances instancesSet = null;
    private Classifier classifier = null;
    private int counter = 0;

    /** Callbacks for service binding, passed to bindService() */
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
        activityImage = findViewById(R.id.currentActivityImg);

        chooseClassifier = findViewById(R.id.algoritmo);
        ArrayList<String> algoritmos = new ArrayList<String>();

        //lista de classificadores
        /*
        algoritmos.add(LogisticPaulo.class.getSimpleName());
        algoritmos.add(LogisticPauloCompleto.class.getSimpleName());
        algoritmos.add(ComMedia1.class.getSimpleName());
        algoritmos.add(ComMedia2.class.getSimpleName());
        algoritmos.add(ComMedia3.class.getSimpleName());
        algoritmos.add(ComMedia4.class.getSimpleName());
        algoritmos.add(ComMedia5.class.getSimpleName());
        */

        algoritmos.add(AndroidTrainClassifier.class.getSimpleName());

        algoritmos.add("-- 7 atividades");

        /*
        algoritmos.add(J48FFTAccGyroscope64.class.getSimpleName());
//        algoritmos.add(RandomForestFFTAccGyroscope64.class.getSimpleName());
        algoritmos.add(HoeffdingTreeFFTAccGyroscope64.class.getSimpleName());
        algoritmos.add(PARTFFTAccGyroscope64.class.getSimpleName());
        algoritmos.add(LMTFFTAccGyroscope64.class.getSimpleName());
        algoritmos.add(REPTreeFFTAccGyroscope64.class.getSimpleName());
        algoritmos.add(JRIPFFTAccGyroscope64.class.getSimpleName());

        algoritmos.add("-- só 4 atividades");

        algoritmos.add(J48FFTAccGyroscope64_4.class.getSimpleName());
        algoritmos.add(LMTFFTAccGyroscope64_4.class.getSimpleName());

        algoritmos.add("-- fft 128 recolhas");

        algoritmos.add(J48FFTAccGyroscope128.class.getSimpleName());
//        algoritmos.add(RandomForestFFTAccGyroscope128.class.getSimpleName());
        algoritmos.add(HoeffdingTreeFFTAccGyroscope128.class.getSimpleName());
        algoritmos.add(PARTFFTAccGyroscope128.class.getSimpleName());
        algoritmos.add(LMTFFTAccGyroscope128.class.getSimpleName());
        algoritmos.add(REPTreeFFTAccGyroscope128.class.getSimpleName());
        algoritmos.add(JRIPFFTAccGyroscope128.class.getSimpleName());

        algoritmos.add("-- iniciais");

        algoritmos.add(ComMedia1.class.getSimpleName());
        algoritmos.add(ComMedia2.class.getSimpleName());
        algoritmos.add(ComMedia3.class.getSimpleName());
        algoritmos.add(ComMedia4.class.getSimpleName());
        algoritmos.add(ComMedia5.class.getSimpleName());
        algoritmos.add(LogisticPaulo.class.getSimpleName());
        algoritmos.add(LogisticPauloCompleto.class.getSimpleName());
        */


        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, algoritmos);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooseClassifier.setAdapter(adaptador);
        chooseClassifier.setSelection(0);

        andar = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.andar), 512, 512, true);
        andarDeCarro = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.andar_carro), 512, 512, true);
        nenhuma = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.nenhuma), 512, 512, true);
        correr = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.correr), 512, 512, true);
        descer = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.descer_escadas), 512, 512, true);
        subir = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.subir_escadas), 512, 512, true);
        saltar = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.saltar), 512, 512, true);
        vazio = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.vazio), 512, 512, true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //listAssetFiles();

        if( sensorsService!=null ) {
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

        onClickStop(null);
    }

    @Override
    protected void onStop() {

        super.onStop();

        if (sensorsServiceBound) {

            //sensorsService.setCallbacks(null); // unregister
            sensorsService.unregisterComponentCallbacks(ClassifyActivity.this);
            unbindService(sensorsServiceConnection);
            sensorsServiceBound = false;
        }
    }

    @Override
    public void onShowStatus(final String text) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Bitmap actual = vazio;
                try {
                    int idx = text.indexOf("Activity: ");
                    if( idx == -1) {
                        idx = text.indexOf("Atividade: ");
                    }
                    String actividadeActual = text.substring(idx);
                    actividadeActual = actividadeActual.split("\n")[0];
                    actividadeActual = actividadeActual.split(":")[1];
                    switch (actividadeActual.trim()) {
                        case "Andar":
                            actual = andar;
                            break;
                        case "Andar de carro":
                            actual = andarDeCarro;
                            break;
                        case "Correr":
                            actual = correr;
                            break;
                        case "Descer escadas":
                            actual = descer;
                            break;
                        case "Nenhuma":
                            actual = nenhuma;
                            break;
                        case "Saltar continuo":
                            actual = saltar;
                            break;
                        case "Subir escadas":
                            actual = subir;
                            break;
                    }
                } catch (Exception e) {}

                activityImage.setImageBitmap(actual);

                hcTxtInfo1.setText(text);
                System.out.println(text);
            }
        });

    }

    @Override
    public void onSensorsError(Exception e) {

        Toast.makeText(this, "Exception: "+e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSensorsStop() {}

    public void onClickStart(View view) {

        if (sensorsService.isRunning()) {

            return;
        }

        String algoritmo = (String)chooseClassifier.getSelectedItem();
        if( algoritmo.startsWith("-") ) {
            return;
        }

        blockButtons();

        List<Integer> lista = null;

        if( algoritmo.contains("64") || algoritmo.contains("128") || algoritmo.equals("AndroidTrainClassifier") ) {
            lista = Sensors.getInstance(this).getClassifySensors();
//        } else if( algoritmo precisa dos selected sensores ) {
//            lista = Sensors.getInstance(this).getAvailableSensors();
        } else {
            lista = Sensors.getInstance(this).getSelectedSensors();
        }

        for (Integer sensor: lista) {

            sensorsService.getSensors().addSensor(sensor);
        }
        startCollecting();
    }

    @Override
    public boolean isClassifying() {

        return true;
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
        activityImage.setImageBitmap(vazio);
    }

    public void startCollecting() {

//        createInstances();

        String algoritmo = (String)chooseClassifier.getSelectedItem();

        if(algoritmo.startsWith("-")) {

            return;
        }
        algoritmo = "com.example.renato.datacollector.classifiers." + algoritmo;
        try {
            Class alg = Class.forName(algoritmo);

            Constructor<ActivityClassifier> cons = alg.getConstructor(Context.class);
            ActivityClassifier activityClassifier = cons.newInstance(this);

            sensorsService.startCollectingToClassify(activityClassifier);
            blockButtons();

        } catch (Exception e) {

            e.printStackTrace(System.err);
            String message = e.getMessage();

            if( message==null && e.getCause()!=null ) {

                message = e.getCause().getMessage();
            }
            Toast.makeText(this, "Exception: "+message, Toast.LENGTH_LONG).show();
        }
    }

    public void blockButtons() {

        hcBtnStart.setEnabled(true);
        hcBtnStop.setEnabled(true);

        if (sensorsService.isRunning()) {

            hcBtnStop.setEnabled(true);
            hcBtnStart.setEnabled(false);
            chooseClassifier.setEnabled(false);
        } else {

            hcBtnStop.setEnabled(false);
            hcBtnStart.setEnabled(true);
            chooseClassifier.setEnabled(true);
        }
    }

    private void listAssetFiles() {

        AssetManager assetManager = getAssets();

        String filelist = "";
        String[] files;
        try {
            files = assetManager.list("");

            for (String s: files) {

                filelist += s + "\n";
            }

        } catch (Exception e) {

            hcTxtInfo1.setText(filelist);
        }
        hcTxtInfo1.setText(filelist);
    }

}