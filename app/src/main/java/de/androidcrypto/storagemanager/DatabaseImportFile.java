package de.androidcrypto.storagemanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

public class DatabaseImportFile extends AppCompatActivity {
    private static final String TAG = DatabaseImportFile.class.getSimpleName();
    private Context contextImportFile; // wird für read a file from uri benötigt

    private Button importDatabaseFromFile;
    //int minimumPassphraseLength = 4; // todo check password length
    private DBUnitHandler dbUnitHandler;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_import_file);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        dbUnitHandler = new DBUnitHandler(DatabaseImportFile.this);

        importDatabaseFromFile = (Button) findViewById(R.id.btnImportDatabaseFromFile);
        importDatabaseFromFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contextImportFile = v.getContext(); // Context context1;
                // wird für read a file from uri benötigt
                // https://developer.android.com/training/data-storage/shared/documents-files
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                //intent.setType("*/*");
                intent.setType("application/json");
                // Optionally, specify a URI for the file that should appear in the
                // system file picker when it loads.
                boolean pickerInitialUri = false;
                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
                fileChooserActivityResultLauncher.launch(intent);
            }
        });
    }

    ActivityResultLauncher<Intent> fileChooserActivityResultLauncher = registerForActivityResult(
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
                                String fileContent = readTextFromUri(uri);
                                Log.d(TAG, "import data:\n" + fileContent);
                                gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
                                List<StorageUnitModel> unitList = convertJsonToClass(fileContent);
                                if (unitList != null) {
                                    Log.d(TAG, "found datasets: " + unitList.size());
                                    if (unitList.size() > 0) {
                                        for (int i = 0; i < unitList.size(); i++) {
                                            StorageUnitModel unit = unitList.get(i);
                                            dbUnitHandler.addNewUnit(unit.getUnitNumber(),
                                                    unit.getUnitShortContent(),
                                                    unit.getUnitContent(),
                                                    unit.getUnitType(),
                                                    unit.getUnitWeight(),
                                                    unit.getUnitPlace(),
                                                    unit.getUnitRoomNr(),
                                                    unit.getUnitLastEdit(),
                                                    unit.getUnitIdServer(),
                                                    unit.getUnitTagUid1(),
                                                    unit.getUnitTagUid2(),
                                                    unit.getUnitTagUid3(),
                                                    unit.getUnitImageFilename1(),
                                                    unit.getUnitImageFilename2(),
                                                    unit.getUnitImageFilename3(),
                                                    unit.getUnitDeleted());
                                        }
                                    }
                                } else {
                                    Log.e(TAG, "cannot deserialize the  import file");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });

    private String readTextFromUri(Uri uri) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        //try (InputStream inputStream = getContentResolver().openInputStream(uri);
        try (InputStream inputStream = contextImportFile.getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
        }
        return stringBuilder.toString();
    }

    private List<StorageUnitModel> convertJsonToClass(String jsonResponse) {
        TypeToken<StorageUnitModel> dataAllType = new TypeToken<StorageUnitModel>() {
        };
        List<StorageUnitModel> unitList = gson.fromJson(jsonResponse, StorageUnitModelList.class);
        return unitList;
    }

    /**
     * section for options menu
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_return_home, menu);

        MenuItem mGoToHome = menu.findItem(R.id.action_return_main);
        mGoToHome.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(DatabaseImportFile.this, MainActivity.class);
                startActivity(intent);
                finish();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

}
