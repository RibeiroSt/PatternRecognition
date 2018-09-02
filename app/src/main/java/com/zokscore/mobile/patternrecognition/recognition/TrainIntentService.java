package com.zokscore.mobile.patternrecognition.recognition;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.rules.PART;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomForest;
import weka.core.Debug;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import com.zokscore.mobile.patternrecognition.CollectActivity;
import com.zokscore.mobile.patternrecognition.R;
import com.zokscore.mobile.patternrecognition.TrainActivity;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class TrainIntentService extends IntentService {

    private static final String PREFERENCES       = "com.zokscore.mobile.patternrecognition.recognition.PREFERENCES";
    public  static final String ACTION_TRAIN      = "com.zokscore.mobile.patternrecognition.recognition.action.TRAIN";
    public  static final String APP_CHANNEL_ID    = "DATACOLLECTOR_CHANNEL";
    public  static final String CLASSIFIER_NAME   = "classifier.model";

    private File model;
    private String completeText;

    private SharedPreferences sharedPreferences;


    public TrainIntentService() {
        super("TrainIntentService");
    }

    /**
     * Starts this service to perform action Train. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionTrain(Context context) {

        Intent intent = new Intent(context, TrainIntentService.class);
        intent.setAction(ACTION_TRAIN);

        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        if (intent != null) {
            final String action = intent.getAction();

            if (ACTION_TRAIN.equals(action)) {

                handleActionTrain();
            }
        }
    }

    /**
     * Handle action Train in the provided background thread with the provided
     * parameters.
     */
    private void handleActionTrain() {

        doTrain();

    }


    private void doTrain() {

        try {
            completeText = "";
            File f = new File(getFilesDir(), CollectActivity.FILE_NAME);
            if (!f.exists() || f.length()==0 ) {
                sendBroadcastText("ARFF file does not exist or is empty.");
                sendBroadcastCommand(TrainActivity.UNLOCK_ACTIVITY);
                return;
            }

            sendBroadcastText("Starting train...");

            //Instances data = DatabaseConnection.getInstances();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
            ArffLoader.ArffReader arffReader = new ArffLoader.ArffReader(bufferedReader);

            Instances data = arffReader.getData();

            if( data.numInstances() < 30 ) {
                sendBroadcastText("You need at least 30 instances to train.");
                sendBroadcastCommand(TrainActivity.UNLOCK_ACTIVITY);
                return;
            }

            for (int i = 0; i < data.numAttributes(); i++) {

                if (data.attribute(i).name().equals("activity")) {

                    data.setClassIndex(i);
                    break;
                }
            }

            if( data.numDistinctValues(data.classIndex()) < 2 ) {
                sendBroadcastText("You need data for at least 2 activities to train.");
                sendBroadcastCommand(TrainActivity.UNLOCK_ACTIVITY);
                return;
            }

            Enumeration en = data.classAttribute().enumerateValues();
            String classes = null;
            while(en.hasMoreElements()) {
                if( classes == null ) {
                    classes = en.nextElement().toString();
                } else {
                    classes += ", " + en.nextElement().toString();
                }
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("listOfClasses", classes);
            editor.apply();

            List<Map<String,Instances>> sets = new ArrayList<>();

            int trainSize = (int) Math.round(data.numInstances() * 0.7);
            int testSize = data.numInstances() - trainSize;

            data.randomize(new Debug.Random(1));// if you comment this line the accuracy of the model will be droped from 96.6% to 80%

            Instances traindataset = new Instances(data, 0, trainSize);
            Instances testdataset = new Instances(data, trainSize, testSize);

            Map<String,Instances> tmp = new HashMap<>();
            tmp.put("train", traindataset);
            tmp.put("test", testdataset);
            sets.add(tmp);

            Classifier[] classifiers = {
                new J48(),
                new RandomForest(),
                new PART(),
                new REPTree(),
            };

            float bestAccuracy = 0;
            Classifier bestClassifier = null;

            for(Classifier classifier : classifiers) {

                sendBroadcastText("\nTraining with "+classifier.getClass().getSimpleName()+"...");

                List<Prediction> predictions = new ArrayList<>();

                for(Map<String,Instances> set : sets) {
                    Instances train = set.get("train");
                    Instances test = set.get("test");

                    classifier.buildClassifier(train);
                    System.out.println(classifier.toString());

                    int block = 5000;
                    int total = test.numInstances();

                    for(int idx = 0; idx < test.numInstances(); idx += block) {

                        try {
                            int size = idx+block>total?total-idx:block;

                            Instances nova = new Instances(test,idx,size);

                            Evaluation evaluation = new Evaluation(train);
                            evaluation.evaluateModel(classifier, nova);

                            predictions.addAll(evaluation.predictions());

                        } catch(Exception e) {

                            e.printStackTrace(System.err);

                            sendBroadcastText("Exception: " + e.getMessage());
                        }
                    }
                }
                long correct = 0;

                for(Prediction p : predictions) {

                    NominalPrediction np = (NominalPrediction)p;

                    if( np.actual()==np.predicted() ) {

                        correct++;
                    }
                }
                float accuracy = (correct * 100.0f / predictions.size());

                String text = "";
                text += "Accuracy for " + classifier.getClass().getSimpleName() + ": " + String.format("%.2f", accuracy)+"%\n";
                text += "correct: " + correct + " in " + predictions.size();

                System.out.println(text);
                sendBroadcastText(text);

                if( bestClassifier==null || accuracy>bestAccuracy ) {

                    bestClassifier = classifier;
                    bestAccuracy = accuracy;

                    sendBroadcastText("Best Classifier until now!");
                }
            }

            //save the best classifier
            model = new File(getFilesDir(), CLASSIFIER_NAME);
            weka.core.SerializationHelper.write(model.getAbsolutePath(), bestClassifier);

            sendBroadcastText("\nChoosen classifier: " + bestClassifier.getClass().getSimpleName());

            createNotification("Choosen classifier: " + bestClassifier.getClass().getSimpleName());
        } catch(Exception e) {

            e.printStackTrace(System.err);
            sendBroadcastText("Exception: " + e.getMessage());
        } finally {
            sendBroadcastCommand(TrainActivity.UNLOCK_ACTIVITY);
        }
    }

    private void createNotification(String result) {

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        createNotificationChannel(notificationManager);

        Intent resultIntent = new Intent(this, TrainActivity.class);
        resultIntent.putExtra("TEXT", completeText);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, APP_CHANNEL_ID)
                        .setCategory(Notification.CATEGORY_MESSAGE)
                        .setAutoCancel(true)
                        .setContentTitle("Train complete. "+result)
                        .setSmallIcon(R.drawable.ic_stat_notification)
                        .setContentIntent(resultPendingIntent);

        notificationManager.notify(1,notificationBuilder.build());
    }

    private void createNotificationChannel(NotificationManager notificationManager) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            CharSequence name = "DataCollector Channel";
            String description = "Channel for notifications created by DataCollector app.";

            NotificationChannel channel = new NotificationChannel(APP_CHANNEL_ID, name, importance);
            channel.setDescription(description);

            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendBroadcastText(String text) {

        if( text!=null ) {
            completeText += text+"\n";
        }

        Intent intent = new Intent(ACTION_TRAIN);

        intent.putExtra(TrainActivity.RESULT_CODE_STRING, TrainActivity.STATUS_UPDATE_SENT);
        intent.putExtra(TrainActivity.RESULT_VALUE_STRING, text);

        sendBroadcast(intent);
    }

    private void sendBroadcastCommand(int command) {

        Intent intent = new Intent(ACTION_TRAIN);

        intent.putExtra(TrainActivity.RESULT_CODE_STRING, TrainActivity.COMMAND_SENT);
        intent.putExtra(TrainActivity.RESULT_VALUE_STRING, command);

        sendBroadcast(intent);
    }

}
