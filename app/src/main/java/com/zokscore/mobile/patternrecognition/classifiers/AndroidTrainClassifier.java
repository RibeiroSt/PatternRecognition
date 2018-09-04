package com.zokscore.mobile.patternrecognition.classifiers;

import android.content.Context;
import android.content.SharedPreferences;

import com.zokscore.mobile.patternrecognition.recognition.TrainIntentService;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instances;

/**
 * Created by pafgoncalves on 14-07-2018.
 */

public class AndroidTrainClassifier extends FFTAccGyroscope64 {

    private static final String PREFERENCES = "com.zokscore.mobile.patternrecognition.recognition.PREFERENCES";

    private SharedPreferences sharedPreferences;
    private List<String> classesList;

    public AndroidTrainClassifier(Context context, boolean broadcastActivity) throws Exception {
        super(context,broadcastActivity);
    }

    protected Classifier createClassifier() throws Exception {
        classesList = new ArrayList<>();
        sharedPreferences = contextInstance.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        String listOfClasses = sharedPreferences.getString("listOfClasses", null);
        System.out.println("listOfClasses=<"+listOfClasses+">");
        if( listOfClasses == null ) {
            throw new Exception("You must collect data and train before classify.");
        }
        for(String c : listOfClasses.split(",")) {
            classesList.add(c.trim());
        }

        Classifier c = null;
        try {
            File file = new File(contextInstance.getFilesDir(), TrainIntentService.CLASSIFIER_NAME);
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
