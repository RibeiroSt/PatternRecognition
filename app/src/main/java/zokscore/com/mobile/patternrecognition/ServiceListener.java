package zokscore.com.mobile.patternrecognition;

public interface ServiceListener {

    void onShowStatus(String text);

    void onSensorsError(Exception e);

    void onSensorsStop();

    boolean isClassifying();
}
