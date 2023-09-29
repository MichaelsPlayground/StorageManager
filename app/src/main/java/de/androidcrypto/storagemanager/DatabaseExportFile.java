package de.androidcrypto.storagemanager;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * saves the complete unit database unencrypted in JSON encoding to a file in external storage
 */
public class DatabaseExportFile extends AppCompatActivity {
    private static final String TAG = DatabaseExportFile.class.getSimpleName();
    // ## version 1.00 ##
    private Button btnExportDatabaseToFile;
    private int minimumPassphraseLength = 4; // todo password length

    private Context contextSave; // wird für read a file from uri benötigt
    private final String DEFAULT_JSON_FILE_NAME = "export_";
    private final String DEFAULT_JSON_FILE_EXTENSION = ".json";
    private String exportFileName = "";
    private DBUnitHandler dbUnitHandler;
    private Gson gson;
    private String allJsonResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_export_file);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        // hide soft keyboard from showing up on startup
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // on below line we are initialing our dbhandler class.
        dbUnitHandler = new DBUnitHandler(DatabaseExportFile.this);

        btnExportDatabaseToFile = (Button) findViewById(R.id.btnExportDatabaseToFile);
        btnExportDatabaseToFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "export database to file");
                List<StorageUnitModel> units = dbUnitHandler.readUnits();
                if ((units == null) || (units.size() < 1)) {
                    Log.d(TAG, "no units available, aborted");
                    Toast.makeText(DatabaseExportFile.this, "No units in database", Toast.LENGTH_SHORT).show();
                    return;
                }

                gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
                allJsonResponse = convertClassToJson(units);
                exportFileName = DEFAULT_JSON_FILE_NAME + Utils.getExportTimestamp() + DEFAULT_JSON_FILE_EXTENSION;

                Log.d(TAG, "### allJsonResponse: " + allJsonResponse);

                contextSave = v.getContext();
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                // Optionally, specify a URI for the file that should appear in the
                // system file picker when it loads.
                //boolean pickerInitialUri = false;
                //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
                intent.putExtra(Intent.EXTRA_TITLE, exportFileName);
                fileSaverActivityResultLauncher.launch(intent);
            }
        });
    }

    ActivityResultLauncher<Intent> fileSaverActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent resultData = result.getData();
                        // The result data contains a URI for the document or directory that
                        // the user selected.
                        Uri uri = null;
                        if (resultData != null) {
                            uri = resultData.getData();
                            // Perform operations on the document using its URI.
                            try {
                                Log.d(TAG, "*** allJsonResponse: " + allJsonResponse);
                                writeTextToUri(uri, allJsonResponse);
                            } catch (IOException e) {
                                Log.e(TAG, "IOException: " + e.getMessage());
                            }
                        }
                    }
                }
            });

    private void writeTextToUri(Uri uri, String data) throws IOException {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(contextSave.getContentResolver().openOutputStream(uri));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
            return;
        }
    }

    private String convertClassToJson(List<StorageUnitModel> unitModels) {
        return gson.toJson(unitModels);
    }

}

/* 1234
### PWMANAGER IMPORT START V1 ###
6UxaFrzwa9HFQjKFkVER8rO7tyi8oL+J7G7TZf1Gga0=:usGMWKw53or2oCi/:cyanlpATwHrEJO1XgeBw/1VAxzfhbTnqFuhSkQmyyaO8PIIWqbWOxurIZyHdiEi5YsUHZh7EfKsIgQlZ6aTC4qOvDgIWaMX9eaR61wIyjtcACZkrJy/ncIgvnF6wzANMLY4WD49w0fQmbUSGFGxrOH7CnmVkxm8MqZfBRX2SloetwuDT+c+llruMwPmkv+LCkrDkEV3iHNiyKWiOmRQ3uOO51oxHhn8jqxysP9nAqMfIdtr2b1nLqvBEU36J0vbFlWMZxg3aeQv5LxJrKuvIYytiCyaGa0Fq2UQ7uerLagM2iPesQuaRrm38oXGfXjnfj+RJJvAwSSMX/58M8/z997X9iIeCUAeM:IjNXIHH3+xryB+TycdOvbQ==
### PWMANAGER IMPORT END V1 ###
*/
/*
 */