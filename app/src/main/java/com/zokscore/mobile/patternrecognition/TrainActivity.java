package com.zokscore.mobile.patternrecognition;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zokscore.mobile.patternrecognition.recognition.TrainIntentService;

public class TrainActivity extends AppCompatActivity {

    public static final String RESULT_CODE_STRING  = "resultCode";
    public static final String RESULT_VALUE_STRING = "resultValue";

    public static final int COMMAND_SENT       = 110;
    public static final int STATUS_UPDATE_SENT = 111;
    public static final int UNLOCK_ACTIVITY    = 112;

    private Button btnTrain;
    private TextView textView;

    private String textContent = "";

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

        int resultCode = intent.getIntExtra(RESULT_CODE_STRING, RESULT_CANCELED);

        if (resultCode == STATUS_UPDATE_SENT) {

            String text = intent.getStringExtra(RESULT_VALUE_STRING);
            updateTextStatusByBroadcast(text);
        }
        if (resultCode == COMMAND_SENT) {

            int command = intent.getIntExtra(RESULT_VALUE_STRING, 0);

            switch (command) {

                case UNLOCK_ACTIVITY:

                    btnTrain.setEnabled(true);
                    break;
            }
        }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        btnTrain  = findViewById(R.id.train_btn_train);
        textView  = findViewById(R.id.train_text_view);

        onNewIntent(getIntent());
    }

    @Override
    public void onNewIntent(Intent intent){
        Bundle extras = intent.getExtras();
        if(extras != null){
            if (extras.containsKey("TEXT")) {
                textView.setText(extras.getString("TEXT"));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter(TrainIntentService.ACTION_TRAIN);
         registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(broadcastReceiver);
    }


    public void onClickTrain(View view) {

        TrainIntentService.startActionTrain(this);
        btnTrain.setEnabled(false);
        textContent = "";
    }

    public void updateTextStatusByBroadcast(String text) {
        if( text==null ) {
            return;
        }
        textContent += text+"\n";
        textView.setText(textContent);
    }
}
