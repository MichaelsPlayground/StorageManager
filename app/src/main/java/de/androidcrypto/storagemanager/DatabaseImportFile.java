package de.androidcrypto.storagemanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class DatabaseImportFile extends AppCompatActivity {

    Context contextImportFile; // wird für read a file from uri benötigt

    Button importDatabaseFromFile;
    int minimumPassphraseLength = 4; // todo check password length
    DBUnitHandler dbUnitHandler;

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
                intent.setType("*/*");
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
                                EditText etData = (EditText) findViewById(R.id.etDatabaseContentImport);
                                etData.setText(fileContent);
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
        // achtung: context1 muss gefüllt sein !
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

}
/*
### PWMANAGER IMPORT START V1 ###
EE9bxAuMIX3aWMQIBJZfqq18tZ5wG9pRa7wPUcfzH98=:4NEFZB+xMuuAb7ce:eO75uuEv+AYCdn5M3VTcJnzb8cZdSEZ8A1c5gyImwECmcDhWHdt2d2maemf5rZj6JPUCrONAH5R7zGMzU0Gti3X1fKnrrajoGfn9vlDrx1QQjow/kwk1rLhJfX3a2L65WIjeVx6690X0gAZ5NFxImQunvgDl2F9ZzrSVEf+q7NwkqGVH8bVu07e4FtpjWTpIMlfKv53KvzoDTsrxAG2E2BwgrgBCpypoKx81/IHihbrBUnBjQtZ/kl71aFM8iF8zGQ==:GVAhBwFd+lvbOYYFZH3oIw==
### PWMANAGER IMPORT END V1 ###
*/