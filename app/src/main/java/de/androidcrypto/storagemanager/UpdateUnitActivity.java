package de.androidcrypto.storagemanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class UpdateUnitActivity extends AppCompatActivity implements ILockableActivity {

    private static final String TAG = UpdateUnitActivity.class.getSimpleName();

    // variables for our edit text, button, strings and db-handler class.

    private com.google.android.material.textfield.TextInputEditText unitNumber, unitShortContent,
            unitContent, unitType, unitWeight, unitPlace, unitRoom, unitLastEdit, unitExternalId,
            unitTagUid1, unitTagUid2, unitTagUid3,
            unitImageFilename1, unitImageFilename2, unitImageFilename3;
    private Button showImage1, showImage2, showImage3;
    private Button updateUnit, abort;

    private DBUnitHandler dbUnitHandler;

    //String entryLoginName, entryLoginPassword;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void lock() {
        //etLoginPassword.setVisibility(View.GONE);
        //etLoginName.setVisibility(View.GONE);
    }

    @Override
    public void unlock() {
        //etLoginPassword.setVisibility(View.VISIBLE);
        //etLoginName.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_unit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        // initializing all our variables.
        unitNumber = findViewById(R.id.etUnitNumber);
        unitShortContent = findViewById(R.id.etUnitShortContent);
        unitContent = findViewById(R.id.etUnitContent);
        unitType = findViewById(R.id.etUnitType);
        unitWeight = findViewById(R.id.etUnitWeight);
        unitPlace = findViewById(R.id.etUnitPlace);
        unitRoom = findViewById(R.id.etUnitRoom);
        unitLastEdit = findViewById(R.id.etUnitLastEdit);
        unitExternalId = findViewById(R.id.etUnitIdServer);
        unitTagUid1 = findViewById(R.id.etUnitTagUid1);
        unitTagUid2 = findViewById(R.id.etUnitTagUid2);
        unitTagUid3 = findViewById(R.id.etUnitTagUid3);
        unitImageFilename1 = findViewById(R.id.etUnitImageFilename1);
        unitImageFilename2 = findViewById(R.id.etUnitImageFilename2);
        unitImageFilename3 = findViewById(R.id.etUnitImageFilename3);
        showImage1 = findViewById(R.id.btnShowImage1);
        showImage2 = findViewById(R.id.btnShowImage2);
        showImage3 = findViewById(R.id.btnShowImage3);

        updateUnit = findViewById(R.id.btnUpdateUnit);
        abort = findViewById(R.id.btnAbort);

        // todo test data, remove on final
        /*
        unitNumber.setText("B12");
        unitShortContent.setText("short content");
        unitContent.setText("long content");
        unitType.setText("Karton");
        unitWeight.setText("leicht");
        unitPlace.setText("hinten unten links");
        unitRoom.setText("B");
*/

// unitName, unitShortContent,
//            unitContent, unitType, unitWeight, unitPlace, unitRoom, unitLastEdit, unitExternalId,
//            unitTagUid1, unitTagUid2, unitTagUid3;

        // hide soft keyboard from showing up on startup
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // on below line we are initialing our dbhandler class.
        dbUnitHandler = new DBUnitHandler(UpdateUnitActivity.this);

        // enable on create
        //etLoginPassword.setVisibility(View.VISIBLE);
        //etLoginName.setVisibility(View.VISIBLE);

        // on below lines we are getting data which
        // we passed in our adapter class
        String unitId = getIntent().getStringExtra("unitId");
        unitNumber.setText(getIntent().getStringExtra("unitNumber"));
        unitShortContent.setText(getIntent().getStringExtra("unitShortContent"));
        unitContent.setText(getIntent().getStringExtra("unitContent"));
        unitType.setText(getIntent().getStringExtra("unitType"));
        unitWeight.setText(getIntent().getStringExtra("unitWeight"));
        unitPlace.setText(getIntent().getStringExtra("unitPlace"));
        unitRoom.setText(getIntent().getStringExtra("unitRoomNr"));
        unitLastEdit.setText(getIntent().getStringExtra("unitLastEdit"));
        unitExternalId.setText(getIntent().getStringExtra("unitIdServer"));
        unitTagUid1.setText(getIntent().getStringExtra("unitTagUid1"));
        unitTagUid2.setText(getIntent().getStringExtra("unitTagUid2"));
        unitTagUid3.setText(getIntent().getStringExtra("unitTagUid3"));
        String unitDeleted = getIntent().getStringExtra("unitDeleted");
        unitImageFilename1.setText(getIntent().getStringExtra("unitImageFilename1"));
        unitImageFilename2.setText(getIntent().getStringExtra("unitImageFilename2"));
        unitImageFilename3.setText(getIntent().getStringExtra("unitImageFilename3"));

        // enable haptic feedback = vibrate
        getWindow().getDecorView().performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);

        showImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "showImage1");
                // check if image is available
                String filename = unitImageFilename1.getText().toString();
                if (TextUtils.isEmpty(filename)) {
                    Log.d(TAG, "filename is not available");
                    return;
                }
                // todo check for file availability as real file, both for thumbnail and original

                onShowImage(filename, filename);
            }
        });

        showImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "showImage2");
                // check if image is available
                String filename = unitImageFilename2.getText().toString();
                if (TextUtils.isEmpty(filename)) {
                    Log.d(TAG, "filename is not available");
                    return;
                }
                // todo check for file availability as real file, both for thumbnail and original

                onShowImage(filename, filename);
            }
        });

        showImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "showImage3");
                // check if image is available
                String filename = unitImageFilename3.getText().toString();
                if (TextUtils.isEmpty(filename)) {
                    Log.d(TAG, "filename is not available");
                    return;
                }
                // todo check for file availability as real file, both for thumbnail and original

                onShowImage(filename, filename);
            }
        });

        updateUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // sanity checks
                if (!validateInput()) {
                    Snackbar snackbar = Snackbar.make(view, "Ein oder mehrere Einträge sind leer oder zu lang - abgebrochen", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(ContextCompat.getColor(UpdateUnitActivity.this, R.color.orange));
                    snackbar.show();
                    return;
                }

                dbUnitHandler.updateUnit(
                        unitId,
                        unitExternalId.getText().toString(),
                        unitNumber.getText().toString(),
                        unitShortContent.getText().toString(),
                        unitContent.getText().toString(),
                        unitType.getText().toString(),
                        unitWeight.getText().toString(),
                        unitPlace.getText().toString(),
                        unitRoom.getText().toString(),
                        unitLastEdit.getText().toString(),
                        unitTagUid1.getText().toString(),
                        unitTagUid2.getText().toString(),
                        unitTagUid3.getText().toString(),
                        unitImageFilename1.getText().toString(),
                        unitImageFilename2.getText().toString(),
                        unitImageFilename3.getText().toString(),
                        unitDeleted);

                Toast.makeText(UpdateUnitActivity.this, "Eintrag geändert..", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(UpdateUnitActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        abort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // return to home
                Intent i = new Intent(UpdateUnitActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

/*
        btnAddEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean entryCheck = true; // everything is ok
                // check length of fields
                if (etEntryname.length() < 1) {
                    Snackbar snackbar = Snackbar.make(v, "Der Name des Eintrages ist zu kurz", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(ContextCompat.getColor(UpdateUnitActivity.this, R.color.orange));
                    snackbar.show();
                    entryCheck = false;
                }
                if (etLoginName.length() < 1) {
                    Snackbar snackbar = Snackbar.make(v, "Der Login Name ist zu kurz", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(ContextCompat.getColor(UpdateUnitActivity.this, R.color.orange));
                    snackbar.show();
                    entryCheck = false;
                }
                if (etLoginPassword.length() < 1) {
                    Snackbar snackbar = Snackbar.make(v, "Das Passwort ist zu kurz", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(ContextCompat.getColor(UpdateUnitActivity.this, R.color.orange));
                    snackbar.show();
                    entryCheck = false;
                }
                if (etCategory.length() < 1) {
                    Snackbar snackbar = Snackbar.make(v, "Die Kategory ist zu kurz", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(ContextCompat.getColor(UpdateUnitActivity.this, R.color.orange));
                    snackbar.show();
                    entryCheck = false;
                }

                if (entryCheck) {
                    dbHandler.addNewEntry(etEntryname.getText().toString(), etLoginName.getText().toString(), etLoginPassword.getText().toString(), etCategory.getText().toString(), favourite);
                    Toast.makeText(UpdateUnitActivity.this, "Eintrag hinzugefügt..", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(UpdateUnitActivity.this, MainActivity.class);
                    startActivity(i);
                }
            }
        });

 */
    }

    private boolean validateInput() {
        boolean success = true;
        String unitNumberString = unitNumber.getText().toString();
        if ((TextUtils.isEmpty(unitNumberString)) || (unitNumberString.length() > 5)) return false;
        String unitShortContentString = unitShortContent.getText().toString();
        if ((TextUtils.isEmpty(unitShortContentString)) || (unitShortContentString.length() > 40)) return false;
        if (TextUtils.isEmpty(unitContent.getText().toString())) return false;
        String unitTypeString = unitType.getText().toString();
        if ((TextUtils.isEmpty(unitTypeString)) || (unitTypeString.length() > 10)) return false;
        String unitWeightString = unitWeight.getText().toString();
        if ((TextUtils.isEmpty(unitWeightString)) || (unitWeightString.length() > 10)) return false;
        String unitPlaceString = unitPlace.getText().toString();
        if ((TextUtils.isEmpty(unitPlaceString)) || (unitPlaceString.length() > 40)) return false;
        String unitRoomString = unitRoom.getText().toString();
        if ((TextUtils.isEmpty(unitRoomString)) || (unitRoomString.length() > 10)) return false;
        return success;
    }


    private void onShowImage(String title, String filename) {

        File file = new File("original", filename);
        //filename = "original/b12_1.jpg";

        FileInputStream fis = null;
        Bitmap bitmap = null;
        try {
            File dir=new File(getFilesDir(), "original");
            fis = new FileInputStream(new File(dir, filename));

            //fis = openFileInput(file.getAbsolutePath());
            if(fis !=null && fis.available() > 0) {
                bitmap = BitmapFactory.decodeStream(fis);
            } else {
                //input stream has not much data to convert into  Bitmap
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
        Log.d(TAG, "bmp available");
        showImageAlertDialog(title, bitmap);

    }

    private void showImageAlertDialog(String title, Bitmap bitmap) {
        if (bitmap == null) {
            Log.d(TAG, "bitmap is NULL");
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //Yes Button
        builder.setPositiveButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getApplicationContext(), "Yes button Clicked", Toast.LENGTH_LONG).show();
                //Log.i("Code2care ", "Yes button Clicked!");
                dialog.dismiss();
            }
        });

        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.image_alert_dialog, null);
        builder.setView(dialoglayout);
        ImageView preview = dialoglayout.findViewById(R.id.ivPreviewImage);
        preview.setImageBitmap(bitmap);
        builder.setTitle(title);
        builder.show();
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
                Intent intent = new Intent(UpdateUnitActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}
