package zokscore.com.mobile.patternrecognition.classifiers;

import android.content.Context;
import android.widget.Toast;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import zokscore.com.mobile.patternrecognition.ServiceListener;
import zokscore.com.mobile.patternrecognition.sensors.SensorData;

/**
 * Created by pafgoncalves on 10-07-2018.
 */

public abstract class ActivityClassifier {

    public static final int DATA_COLLECT_INTERVAL     = 25;

    private int     counter = 0;
    private long    lastCollectTime;

    protected Context contextInstance;
    protected Instances instancesSet = null;
    protected Classifier classifier = null;

    public ActivityClassifier(Context context) throws Exception {

        contextInstance = context;
        classifier = createClassifier();
        instancesSet = createInstances();
    }

    protected String getClassifierFileName() {
        return null;
    }

    protected Classifier createClassifier() throws Exception {

        String fileName = getClassifierFileName();

        if(fileName == null) {

            throw new IllegalArgumentException("Must implement getClassifierFileName()");
        }
        Classifier c = null;
        try {
            c = (Classifier) weka.core.SerializationHelper
                    .read(contextInstance.getAssets().open(fileName));
            System.out.println(c);

        } catch(Exception e) {

            e.printStackTrace(System.err);
            Toast.makeText(contextInstance, "Exception: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return c;
    }

    protected abstract Instances createInstances();

    protected abstract Instance sensorDataToInstance(SensorData data);

    public void classify(SensorData data, ServiceListener listener) {

        long milis = SensorData.getTimeInMillis();

        if ((milis - lastCollectTime) > DATA_COLLECT_INTERVAL) {

            if (classifier != null) {

                try {
                    Instance singleInstance = sensorDataToInstance(data);

                    double result = classifier.classifyInstance(singleInstance);

                    singleInstance.setClassValue(result);

                    String res = "\n";
                    int i = 0;
                    String distribution = "\n";
                    for (double d : classifier.distributionForInstance(singleInstance)) {

                        distribution += singleInstance.classAttribute().value(i) + ": " + String.valueOf(d) + "\n";
                        i++;
                    }

                    res += "Activity: " + singleInstance.classAttribute().value((int) result) + "\n\n";
                    res += "Counter: " + (counter++) + "\n";
                    res += singleInstance.toString() + "\n";
                    res += "Distribution: " + distribution + "\n";

                    if (listener != null) {
                        listener.onShowStatus(res);
                    }
                } catch (Exception e) {

                    e.printStackTrace(System.err);
                    listener.onSensorsError(e);
                }
            }
            lastCollectTime = milis;
        }
    }

}
