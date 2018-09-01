package zokscore.com.mobile.patternrecognition.recognition;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import zokscore.com.mobile.patternrecognition.ServiceListener;
import zokscore.com.mobile.patternrecognition.classifiers.ActivityClassifier;
import zokscore.com.mobile.patternrecognition.sensors.SensorData;

/**
 * Created by pafgoncalves on 13-07-2018.
 */

public class ClassifierTrainData extends ActivityClassifier {
    private final static int N = 64;

    private int counter = 0;

    private int index = 0;

    private double[] stepdetector = new double[N];
    private double[] acc_fft = new double[N];
    private double[] gyroscope_fft = new double[N];
    private double[] im = new double[N];

    private FFT fft = new FFT(N);

    public ClassifierTrainData(Context context) throws Exception {
        super(context);
    }

    @Override
    protected Classifier createClassifier() {
        return null;
    }

    @Override
    protected Instances createInstances() {


        List<String> classesList   = Arrays.asList(
                "None"
        );
        Attribute classes = new Attribute("@@class@@", classesList);


        List<String> attributeNamesList   = new ArrayList<>();
        for(int i=1; i<=N; i++) {
            attributeNamesList.add("acc_fft_"+i);
        }
        attributeNamesList.add("acc_va_max");
        for(int i=1; i<=N; i++) {
            attributeNamesList.add("gyroscope_fft_"+i);
        }
        attributeNamesList.add("gyroscope_va_max");
        attributeNamesList.add("stepdetector");

        ArrayList<Attribute> attributeList = new ArrayList<Attribute>();
        for(String attName : attributeNamesList) {
            attributeList.add(new Attribute(attName));
        }

        attributeList.add(classes);

        instancesSet = new Instances("TestInstances", attributeList,0);
        instancesSet.setClassIndex(instancesSet.numAttributes() -1);

        return instancesSet;
    }

    protected Instance sensorDataToInstance(SensorData data) {

        Instance singleInstance = new DenseInstance(instancesSet.numAttributes());

        double acc_va_max = Double.MIN_VALUE;
        double gyroscope_va_max = Double.MIN_VALUE;
        for(int j=0; j<N; j++) {
            if( acc_fft[j]>acc_va_max ) {
                acc_va_max = acc_fft[j];
            }
            if( gyroscope_fft[j]>gyroscope_va_max ) {
                gyroscope_va_max = gyroscope_fft[j];
            }
        }

        for(int j=0; j<N; j++) {
            im[j] = 0;
        }
        fft.fft(acc_fft,im);

        int idx = 0;
        for(int j=0; j<N; j++) {
            singleInstance.setValue(idx++, Math.sqrt(acc_fft[j]*acc_fft[j]+im[j]*im[j]));
        }
        singleInstance.setValue(idx++, acc_va_max);

        for(int j=0; j<N; j++) {
            im[j] = 0;
        }
        fft.fft(gyroscope_fft,im);
        for(int j=0; j<N; j++) {
            singleInstance.setValue(idx++, Math.sqrt(gyroscope_fft[j]*gyroscope_fft[j]+im[j]*im[j]));
        }
        singleInstance.setValue(idx++, gyroscope_va_max);

        double step = 0;
        for(int j=0; j<N; j++) {
            step += stepdetector[j];
        }
        step /= N;
        singleInstance.setValue(idx++, step);

        singleInstance.setDataset(instancesSet);

        index = 0;

        return singleInstance;
    }

    @Override
    public void classify(SensorData data, ServiceListener listener) {

        if (index < N) {

            acc_fft[index] = angularVelocity(data.getAccelerometerData().getX(),data.getAccelerometerData().getY(),data.getAccelerometerData().getZ());

            gyroscope_fft[index] = angularVelocity(data.getGyroscopeData().getX(),data.getGyroscopeData().getY(),data.getGyroscopeData().getZ());
            stepdetector[index] = data.getStepDetectorData();

            index++;

        } else {

            doClassify(data, listener);
        }
    }

    private static double angularVelocity(double x, double y, double z) {

        return Math.sqrt(x * x + y * y + z * z);
    }


    private void doClassify(final SensorData data, final ServiceListener listener) {

        Instance singleInstance = sensorDataToInstance(data);

        String str = singleInstance.toString();

        if (listener != null) {

            listener.onShowStatus(str);
        }
    }
}
