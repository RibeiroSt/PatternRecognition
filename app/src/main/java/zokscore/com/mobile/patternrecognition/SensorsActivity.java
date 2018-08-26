package zokscore.com.mobile.patternrecognition;

import android.hardware.Sensor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.List;

import zokscore.com.mobile.patternrecognition.sensors.Sensors;

public class SensorsActivity extends AppCompatActivity {

    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sensors);
        text = findViewById(R.id.textView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        List<Sensor> sensors = Sensors.getInstance(this).getSensorList();

        int i=1;
        StringBuilder sb = new StringBuilder();

        for(Sensor s : sensors) {

            sb.append(i).append(": ").append(s.toString()).append("\n\n");
            i++;
        }

        text.setText(sb.toString());
    }
}
