package zokscore.com.mobile.patternrecognition.classifiers;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import zokscore.com.mobile.patternrecognition.ServiceListener;
import zokscore.com.mobile.patternrecognition.recognition.FFT;
import zokscore.com.mobile.patternrecognition.sensors.SensorData;

/**
 * Created by pafgoncalves on 11-07-2018.
 */

public abstract class FFTAccGyroscope64 extends ActivityClassifier {

    protected final static int N = 64;

    private int counter = 0;

    private int index = 0;

    private double[] stepdetector = new double[N];
    private double[] acc_fft = new double[N];
    private double[] gyroscope_fft = new double[N];
    private double[] im = new double[N];

    private FFT fft = new FFT(N);

    public FFTAccGyroscope64(Context context) {
        super(context);
    }

    private boolean isClassifing = false;

    protected Instances createInstances() {

        List<String> classesList   = Arrays.asList(
                "Andar",
                "Andar de carro",
                "Correr",
                "Descer escadas",
                "Nenhuma",
                "Saltar",
                "Saltar continuo",
                "Subir escadas"
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

        //para não guardar o lixo todo
//        instancesSet.add(singleInstance);
        singleInstance.setDataset(instancesSet);

        index = 0;

        return singleInstance;
    }


    @Override
    public void classify(SensorData data, ServiceListener listener) {

        if( isClassifing ) {
            System.out.println("||||||||| ");
            return;
        }
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
        return Math.sqrt(x*x+y*y+z*z);
    }

    private void doClassify(final SensorData data, final ServiceListener listener) {
        isClassifing = true;
        new Thread() {
            public void run() {
                try {
                    Instance singleInstance = sensorDataToInstance(data);

                    if( classifier!=null ) {

                        double result = classifier.classifyInstance(singleInstance);

                        singleInstance.setClassValue(result);

                        int i = 0;
                        String distribution = "\n";

                        for (double d : classifier.distributionForInstance(singleInstance)) {

                            distribution += singleInstance.classAttribute().value(i) + ": " + String.valueOf(d) + "\n";
                            i++;
                        }
                        String res = "\n\n";

                        res += "Activity: " + singleInstance.classAttribute().value((int) result) + "\n";
                        res += "Counter: " + (counter++) + "\n";
                        res += "Stepdetector: " + data.getStepDetectorData() + "\n";
                        res += "Distribution: " + distribution + "\n\n";

                        if (listener != null) {
                            listener.onShowStatus(res);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                    listener.onSensorsError(e);
                } finally {
                    isClassifing = false;
                }
            }
        }.start();
    }
}