package zokscore.com.mobile.patternrecognition.sensors;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import zokscore.com.mobile.patternrecognition.ServiceListener;
import zokscore.com.mobile.patternrecognition.classifiers.ActivityClassifier;

/**
 * Created by pafgoncalves on 14-04-2018.
 */

public class SensorsService extends Service implements SensorDataListener {

    private Sensors sensors;
    private final IBinder mBinder = new LocalBinder();
    private ServiceListener activityCallback;
    private ActivityClassifier activityClassifier = null;

    public class LocalBinder extends Binder {

        public SensorsService getService() {

            return SensorsService.this;
        }
    }

    @Override
    public void onCreate() {

        super.onCreate();

        if(sensors == null) {

            sensors = Sensors.getInstance(this);
        }
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()

        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {

        return super.onUnbind(intent);
    }

    public void setCallbacks(ServiceListener activity) {

        activityCallback = activity;
    }

    public Sensors getSensors() {

        return sensors;
    }

    public void startCollecting(ActivityClassifier mapper){

        this.activityClassifier = mapper;

        sensors.addListener(this);
        sensors.start();
    }

    public void stopCollecting() {

        sensors.stop();
        sensors.removeListener(this);
        //stopForeground(true);

        if( activityCallback !=null ) {

            activityCallback.onSensorsStop();
        }

        activityClassifier = null;
    }

    public boolean isRunning() {
        return sensors.isRunning();
    }

    @Override
    public void onSensorData(SensorData data) {
        onSensorClassify(data);
    }

    private void onSensorClassify(SensorData data) {

        activityClassifier.classify(data, activityCallback);
    }

    @Override
    public void showStatus(String text) {

        if (activityCallback != null) {

            activityCallback.onShowStatus(text);
        }
    }

}
