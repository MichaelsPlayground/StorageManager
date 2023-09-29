package de.androidcrypto.storagemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

public class TakePhotoActivity extends AppCompatActivity {
    private static final String TAG = TakePhotoActivity.class.getSimpleName();
    private AutoCompleteTextView chooseUnitNumber;
    private String selectedUnitNumber;
    private DBUnitHandler dbUnitHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);

        dbUnitHandler = new DBUnitHandler(TakePhotoActivity.this);
        selectedUnitNumber = "";

        chooseUnitNumber = findViewById(R.id.chooseUnitNumber);
        String[] unitNumbers = dbUnitHandler.getUnitNumbers();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                R.layout.drop_down_item,
                unitNumbers);
        chooseUnitNumber.setAdapter(arrayAdapter);
        chooseUnitNumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedUnitNumber = chooseUnitNumber.getText().toString();
                Log.d(TAG, "selected unit number: " + selectedUnitNumber);
                // take a photo, crop it and store it

                // radio group for selecting image 1 / 2 / 3

            }
        });



    }
}