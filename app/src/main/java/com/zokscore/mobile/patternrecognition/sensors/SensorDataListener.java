package com.zokscore.mobile.patternrecognition.sensors;

/**
 * Created by pafgoncalves on 22-03-2018.
 */

public interface SensorDataListener {

    public void onSensorData(SensorData data);

    public void showStatus(String text);

}
