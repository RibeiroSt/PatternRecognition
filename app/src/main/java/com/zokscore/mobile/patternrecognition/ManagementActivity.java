package com.zokscore.mobile.patternrecognition;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

import com.zokscore.mobile.patternrecognition.db.DatabaseHandler;


public class ManagementActivity extends AppCompatActivity {

    private EditText addText;
    private Spinner escolhaActividade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_management);
        addText = findViewById(R.id.editText);

        escolhaActividade = findViewById(R.id.choose_activity);

    }



    @Override
    protected void onStart() {
        super.onStart();
        // database handler
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());

        // Spinner Drop down elements
        List<String> actividades = db.getAllActivities();

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, actividades);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        escolhaActividade.setAdapter(dataAdapter);

    }


    public void onClickOk(View view) {
        // database handler
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        String str = addText.getText().toString();
        if( str!=null && !str.isEmpty() ) {
            db.insertActivity(str);
            Toast.makeText(this, "Activity inserted.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void onClickDelete(View view) {
        // database handler
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        String str = (String)escolhaActividade.getSelectedItem();
        if( str!=null && !str.isEmpty() ) {
            db.deleteActivity(str);
            Toast.makeText(this, "Activity removed.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

}
