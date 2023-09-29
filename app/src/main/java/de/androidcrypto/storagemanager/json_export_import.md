# JSON export and import database to/from JSON

Source: https://github.com/marwa-eltayeb/Android-Features/blob/master/BackupData/app/src/main/java/com/marwaeltayeb/backupdata/MainActivity.kt

The code was converted from Kotlin to Java using ChatGPT without any checks !

Required: implementation 'com.google.code.gson:gson:2.10.1'

```plaintext
package com.marwaeltayeb.backupdata;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.marwaeltayeb.backupdata.databinding.ActivityMainBinding;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ActivityMainBinding binding;
    private Gson gson;
    private String allJsonResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer("Hala", "developer", 34.5f));
        customers.add(new Customer("Nora", "teacher", 43.5f));
        customers.add(new Customer("Rana", "doctor", 56.5f));

        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee("ALi", "carpenter"));
        employees.add(new Employee("Yaser", "mechanic"));

        gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        allJsonResponse = convertClassToJson(customers, employees);

        Data allData = convertJsonToClass(allJsonResponse);
        Log.d(TAG, allData != null ? allData.getCustomers().get(0).getName() : "");

        binding.btnExport.setOnClickListener(view -> {
            if (isStoragePermissionGranted()) {
                writeTextToFile(allJsonResponse);
            }
        });

        binding.btnImport.setOnClickListener(view -> openFileManager());
    }

    private String convertClassToJson(List<Customer> customers, List<Employee> employees) {
        Data allData = new Data(customers, employees);
        return gson.toJson(allData);
    }

    private Data convertJsonToClass(String jsonResponse) {
        TypeToken<Data> dataAllType = new TypeToken<Data>() {
        };
        return gson.fromJson(jsonResponse, dataAllType.getType());
    }

    private void writeTextToFile(String jsonResponse) {
        if (!jsonResponse.isEmpty()) {
            File dir = new File("//sdcard//Download//");
            File myExternalFile = new File(dir, getRandomFileName());
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(myExternalFile);
                fos.write(jsonResponse.getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(this, "Information saved to SD card. " + myExternalFile, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            return true;
        }
    }

    private String readTextFromUri(Uri uri) {
        InputStream inputStream = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            inputStream = getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private String getRandomFileName() {
        return String.valueOf(Calendar.getInstance().getTimeInMillis()) + ".json";
    }

    private void openFileManager() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        getDataFromFile.launch(intent);
    }

    private final ActivityResultContracts.StartActivityForResult getDataFromFile = result -> {
        if (result.getResultCode() == RESULT_OK) {
            Uri uri = result.getData().getData();
            String fileContents = readTextFromUri(uri);
            Toast.makeText(this, fileContents, Toast.LENGTH_SHORT).show();
        }
    };
}
```