package zokscore.com.mobile.patternrecognition.classifiers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instances;
import zokscore.com.mobile.patternrecognition.recognition.TrainIntentService;

/**
 * Created by pafgoncalves on 14-07-2018.
 */

public class AndroidTrainClassifier extends FFTAccGyroscope64 {

    private static final String PREFERENCES = "com.example.renato.datacollector.train.PREFERENCES";

    private SharedPreferences sharedPreferences;
    private List<String> classesList;

    public AndroidTrainClassifier(Context context) {
        super(context);
    }

    protected Classifier createClassifier() {
        classesList = new ArrayList<>();
        sharedPreferences = contextInstance.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        String listOfClasses = sharedPreferences.getString("listOfClasses", null);
        System.out.println("listOfClasses=<"+listOfClasses+">");
        for(String c : listOfClasses.split(",")) {
            classesList.add(c.trim());
        }

        Classifier c = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(TrainIntentService.FILE_DIR), TrainIntentService.CLASSIFIER_NAME);
            c = (Classifier) weka.core.SerializationHelper
                    .read(new FileInputStream(file));
            System.out.println(c);

        } catch(Exception e) {

            throw new IllegalArgumentException(e);
        }
        return c;
    }


    protected Instances createInstances() {

//        DatabaseHandler db = new DatabaseHandler(contextInstance);
//        List<String> classesList = db.getAllActivities();
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
}
