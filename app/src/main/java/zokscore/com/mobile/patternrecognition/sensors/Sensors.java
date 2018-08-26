package zokscore.com.mobile.patternrecognition.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pafgoncalves on 22-03-2018.
 */

public class Sensors implements SensorEventListener {

    public static int LOCATION_GPS = Sensor.TYPE_DEVICE_PRIVATE_BASE+1;
    public static int LOCATION_NETWORK = Sensor.TYPE_DEVICE_PRIVATE_BASE+2;

    private static Sensors instance = null;

    private SensorManager manager = null;
    private List<Sensor> sensorsList = null;
    private List<Integer> availableSensors = null;
    private List<Integer> selectedSensors = null;
    private List<Integer> classifySensors = null;
    private List<SensorDataListener> listeners = null;
    private boolean running = false;
    private SensorData data = null;
    private double lastStatusUpdate;

    private Sensors(Context context) {

        inicialize(context);
    }

    private void inicializeAvaliableSensor() {

        availableSensors = new ArrayList<>();

        availableSensors.add(Sensor.TYPE_ACCELEROMETER);               //1
        availableSensors.add(Sensor.TYPE_MAGNETIC_FIELD);              //2
        availableSensors.add(Sensor.TYPE_ORIENTATION);                 //3
        availableSensors.add(Sensor.TYPE_GYROSCOPE);                   //4
        availableSensors.add(Sensor.TYPE_LIGHT);                       //5
        availableSensors.add(Sensor.TYPE_PROXIMITY);                   //8
        availableSensors.add(Sensor.TYPE_GRAVITY);                     //9
        availableSensors.add(Sensor.TYPE_LINEAR_ACCELERATION);         //10
        availableSensors.add(Sensor.TYPE_ROTATION_VECTOR);             //11
        availableSensors.add(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED); //14
        availableSensors.add(Sensor.TYPE_GAME_ROTATION_VECTOR);        //15
        availableSensors.add(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);      //16
        /*
        availableSensors.add(Sensor.TYPE_SIGNIFICANT_MOTION);          //17
        */
        availableSensors.add(Sensor.TYPE_STEP_DETECTOR);               //18
        availableSensors.add(Sensor.TYPE_STEP_COUNTER);                //19
        availableSensors.add(22);                                      //TILT_DETECTOR  22
        availableSensors.add(Sensors.LOCATION_GPS);
        availableSensors.add(Sensors.LOCATION_NETWORK);
    }

    private void inicializeSelectedSensors() {

        selectedSensors = new ArrayList<>();

        selectedSensors.add(Sensor.TYPE_ACCELEROMETER);               //1
        selectedSensors.add(Sensor.TYPE_GAME_ROTATION_VECTOR);        //15
        selectedSensors.add(Sensor.TYPE_GRAVITY);                     //9
        selectedSensors.add(Sensor.TYPE_MAGNETIC_FIELD);              //2
        selectedSensors.add(Sensor.TYPE_ORIENTATION);                 //3
        selectedSensors.add(Sensor.TYPE_PROXIMITY);                   //8
        selectedSensors.add(Sensor.TYPE_ROTATION_VECTOR);             //11

        selectedSensors.add(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);      //16
        selectedSensors.add(Sensor.TYPE_STEP_DETECTOR);               //18
        selectedSensors.add(Sensor.TYPE_STEP_COUNTER);                //19
    }


    private void inicializeClassifySensors() {

        classifySensors = new ArrayList<>();

        classifySensors.add(Sensor.TYPE_ACCELEROMETER);               //1
        classifySensors.add(Sensor.TYPE_GYROSCOPE);                   //4
        classifySensors.add(Sensor.TYPE_STEP_DETECTOR);               //18
    }

    private void inicialize(Context context) {

        sensorsList = new ArrayList<>();
        listeners = new ArrayList<>();
        manager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        data = new SensorData(context);
        lastStatusUpdate = SensorData.getTimeInMillis();
    }

    public List<Integer> getAvailableSensors() {

        if (availableSensors == null) {

            inicializeAvaliableSensor();
        }
        return availableSensors;
    }

    public List<Integer> getSelectedSensors() {

        if (selectedSensors == null) {

            inicializeSelectedSensors();
        }
        return selectedSensors;
    }

    public List<Integer> getClassifySensors() {

        if (classifySensors == null) {

            inicializeClassifySensors();
        }
        return classifySensors;
    }

    public synchronized static Sensors getInstance(Context context) {

        if( instance == null ) {

            instance = new Sensors(context);
        }
        return instance;
    }

    /**
     * Adds a sensor to monitor.
     *
     * @param type something like Sensor.TYPE_STEP_COUNTER, Sensor.TYPE_ACCELEROMETER, etc
     */
    public synchronized void addSensor(int type) {

        if (running) {
            throw new IllegalStateException("Is running");
        }
        Sensor s = manager.getDefaultSensor(type);

        if (!sensorsList.contains(s)) {
            sensorsList.add(s);
        }
    }

    public synchronized void removeSensor(int type) {

        if (running) {
            throw new IllegalStateException("Is running");
        }
        //TODO: verificar se o objecto devolvido tem sempre a mesma referencia
        Sensor s = manager.getDefaultSensor(type);
        sensorsList.remove(s);
    }

    /**
     * Starts monitoring
     */
    public synchronized void start() {

        if(running) {

            return;
        }
        for(Sensor s : sensorsList) {

            manager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
        }
        running = true;
    }

    /**
     * Stops monitoring
     */
    public synchronized void stop() {

        if(!running) {

            return;
        }
        for(Sensor s : sensorsList) {
            manager.unregisterListener(this);
        }
        running = false;
    }

    public void addListener(SensorDataListener l) {

        synchronized (data) {

            if (!listeners.contains(l)) {

                listeners.add(l);
            }
        }
    }

    public void removeListener(SensorDataListener l) {

        synchronized (data) {

            listeners.remove(l);
        }
    }

    public List<Sensor> getSensorList() {

        return manager.getSensorList(Sensor.TYPE_ALL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

       notifyListeners(sensorEvent);
    }

    private void notifyListeners(SensorEvent sensorEvent) {

        data.updateTimestamp();

        for (SensorDataListener l : listeners) {

            if (sensorEvent != null) {

                collectEventData(sensorEvent, l.verifyClassifying());
            }
            proceedNotifyingListener(l);
        }
    }

    private void proceedNotifyingListener(SensorDataListener sdl) {

        if (sdl.verifyClassifying()) {

            if (data.isCompleteForClassifySensors()) {

                sdl.onSensorData(data);
                data.setStepDetectorData(0);
            }
        } else {

            if (data.isComplete()) {

                sdl.onSensorData(data);
                data.setStepDetectorData(0);

            } else if (lastStatusUpdate > 1000) {

                String text = "Waiting for the sensors to become online...\n" + data.getSensorsStatus();
                sdl.showStatus(text);

                lastStatusUpdate = SensorData.getTimeInMillis();
            }
        }
    }

    private void collectEventData(SensorEvent sensorEvent, boolean classifying) {

        synchronized (data) {
            switch (sensorEvent.sensor.getType()) {

                case Sensor.TYPE_ACCELEROMETER: //1
                    data.setAccelerometerData(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
                    break;
                case Sensor.TYPE_GAME_ROTATION_VECTOR: //15
                    data.setGameRotationVectorData(sensorEvent.values[0],sensorEvent.values[1],sensorEvent.values[2],sensorEvent.values[3]);
                    break;
                case Sensor.TYPE_GRAVITY: //9
                    data.setGravityData(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD: //2
                    data.setMagneticFieldData(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
                    break;
                case Sensor.TYPE_ORIENTATION: //3
                    data.setOrientationData(sensorEvent.values[0],sensorEvent.values[1],sensorEvent.values[2]);
                    break;
                case Sensor.TYPE_PROXIMITY: //5
                    //TODO:: Android Oreo does not collect three values for this sensor, so, its necessary to refactor this point
                    data.setProximityData(sensorEvent.values[0], 0, 0);
                    break;
                case Sensor.TYPE_GYROSCOPE_UNCALIBRATED: //16
                    data.setGyroscopeUncalibratedData(sensorEvent.values[0],sensorEvent.values[1],sensorEvent.values[2],sensorEvent.values[3],sensorEvent.values[4],sensorEvent.values[5]);
                    break;
                case Sensor.TYPE_ROTATION_VECTOR: //11
                    data.setRotationVectorData(sensorEvent.values[0],sensorEvent.values[1],sensorEvent.values[2],sensorEvent.values[3],sensorEvent.values[4]);
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION: //10
                    data.setLinearAccelerationData(sensorEvent.values[0],sensorEvent.values[1],sensorEvent.values[2]);
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED: //14
                    data.setMagneticFieldUncalibratedData(sensorEvent.values[0],sensorEvent.values[1],sensorEvent.values[2],sensorEvent.values[3],sensorEvent.values[4],sensorEvent.values[5]);
                    break;
                case Sensor.TYPE_SIGNIFICANT_MOTION: //17
                    data.setSignificantMotionData(sensorEvent.values[0]);
                    break;
                case Sensor.TYPE_STEP_DETECTOR: //18
                    data.setStepDetectorData(sensorEvent.values[0]);
                    break;
                case 22: //TILT_DETECTOR
                    data.setTiltDetectorData(sensorEvent.values[0]);
                    break;
                case Sensor.TYPE_GYROSCOPE: //4
                    data.setGyroscopeData(sensorEvent.values[0],sensorEvent.values[1],sensorEvent.values[2]);
                    break;
                case Sensor.TYPE_LIGHT: //5
                    //TODO:: Android Oreo does not collect three values for this sensor, so, its necessary to refactor this point
                    data.setLightData(sensorEvent.values[0], 0, 0);
                    break;
                case Sensor.TYPE_STEP_COUNTER: //19
                    data.setStepCounterData(sensorEvent.values[0]);
                    break;
                default:
                    return;
            }
        }
    }

    public boolean isRunning() {

        return running;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
