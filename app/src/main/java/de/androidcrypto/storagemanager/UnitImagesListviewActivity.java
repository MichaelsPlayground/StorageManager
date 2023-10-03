package de.androidcrypto.storagemanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.biometric.BiometricManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class UnitImagesListviewActivity extends AppCompatActivity {

    private static final String TAG = UnitImagesListviewActivity.class.getSimpleName();

    // stay on implementation 'androidx.appcompat:appcompat:1.3.1'
    // do not update to 1.4.0 if you are on SDK30

    // service infos
    // https://github.com/IvBaranov/MaterialFavoriteButton
    // https://stackoverflow.com/questions/35866370/implementing-add-to-favourite-mechanism-in-recyclerview




    FloatingActionButton btnAddUnit;

    private DBUnitHandler dbUnitHandler;

    // recycler view
    private ArrayList<StorageUnitModel> unitModelArrayList;
    private UnitImagesRVAdapter unitImagesRVAdapter;
    private RecyclerView unitsRV;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_images_listview);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        // enable haptic feedback = vibrate
        getWindow().getDecorView().performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);

        // creating a new dbhandler class
        // and passing our context to it.
        dbUnitHandler = new DBUnitHandler(UnitImagesListviewActivity.this);

        // activate on creation
        RecyclerView recyclerView = findViewById(R.id.idRVUnits);
        recyclerView.setVisibility(View.VISIBLE);

        unitModelArrayList = new ArrayList<>();

        // list from db handler class.
        unitModelArrayList = dbUnitHandler.readUnits();

        // here we are filtering in entryName
        String filterString = "";
        ArrayList<StorageUnitModel> unitModelFilteredArrayList = new ArrayList<>();
        for (int l = 0; l < unitModelArrayList.size(); l++) {
            String serviceName = unitModelArrayList.get(l).getUnitNumber().toLowerCase();
            if (serviceName.contains(filterString.toLowerCase())) {
                unitModelFilteredArrayList.add(unitModelArrayList.get(l));
            }
        }
        unitImagesRVAdapter = new UnitImagesRVAdapter(unitModelFilteredArrayList, UnitImagesListviewActivity.this);
        unitsRV = findViewById(R.id.idRVUnits);

        // setting layout manager for our recycler view.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(UnitImagesListviewActivity.this, RecyclerView.VERTICAL, false);
        unitsRV.setLayoutManager(linearLayoutManager);

        // setting our adapter to recycler view.
        unitsRV.setAdapter(unitImagesRVAdapter);

        btnAddUnit = (FloatingActionButton) findViewById(R.id.fabAddUnit);
        btnAddUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UnitImagesListviewActivity.this, AddUnitActivity.class);
                startActivity(i);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        MenuItem mSearch = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("suche einen Eintrag");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //adapter.getFilter().filter(newText);
                // hier wird gefiltered
                String filterString = newText; // in courseDuration
                ArrayList<StorageUnitModel> unitModelFilteredArrayList = new ArrayList<>();
                for (int l = 0; l < unitModelArrayList.size(); l++) {
                    String serviceName = unitModelArrayList.get(l).getUnitNumber().toLowerCase();

                    if (serviceName.contains(filterString.toLowerCase())) {
                        unitModelFilteredArrayList.add(unitModelArrayList.get(l));
                    }

                    // original: courseRVAdapter = new CourseRVAdapter(courseModalArrayList, de.androidcrypto.sqllitetutorial1.ViewFilteredCourses.this);
                    unitImagesRVAdapter = new UnitImagesRVAdapter(unitModelFilteredArrayList, UnitImagesListviewActivity.this);

                    unitsRV = findViewById(R.id.idRVUnits);

                    // setting layout manager for our recycler view.
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(UnitImagesListviewActivity.this, RecyclerView.VERTICAL, false);
                    unitsRV.setLayoutManager(linearLayoutManager);

                    // setting our adapter to recycler view.
                    unitsRV.setAdapter(unitImagesRVAdapter);

                }
                onPrepareOptionsMenu(menu); // zeigt die app-bar wieder vollständig an
                return true;

            }
        });

        MenuItem mAdd = menu.findItem(R.id.action_add);
        mAdd.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i = new Intent(UnitImagesListviewActivity.this, AddEntryActivity.class);
                startActivity(i);
                return false;
            }
        });

        MenuItem mAddUnit = menu.findItem(R.id.action_add_unit);
        mAddUnit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i = new Intent(UnitImagesListviewActivity.this, AddUnitActivity.class);
                startActivity(i);
                return false;
            }
        });

        // todo remove menu entry as just for testing
        MenuItem mUpdateUnit = menu.findItem(R.id.action_update_unit);
        mUpdateUnit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i = new Intent(UnitImagesListviewActivity.this, UpdateUnitActivity.class);
                startActivity(i);
                return false;
            }
        });

        MenuItem mImageHandling = menu.findItem(R.id.action_image_handling);
        mImageHandling.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i = new Intent(UnitImagesListviewActivity.this, ImageHandlingActivity.class);
                startActivity(i);
                return false;
            }
        });

        MenuItem mQuit = menu.findItem(R.id.action_quit);
        mQuit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // todo clear masterkey ?
                System.out.println("*** quit clicked ***");
                finishAndRemoveTask(); // stops the app
                finishAffinity();
                return false;
            }
        });
        MenuItem mImportClipboard = menu.findItem(R.id.action_importClipboard);
        mImportClipboard.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i = new Intent(UnitImagesListviewActivity.this, DatabaseImportClipboard.class);
                startActivity(i);
                return false;
            }
        });

        MenuItem mExportMasterkey = menu.findItem(R.id.action_exportMasterkey);
        mExportMasterkey.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i = new Intent(UnitImagesListviewActivity.this, MasterkeyExportClipboard.class);
                startActivity(i);
                return false;
            }
        });

        MenuItem mExportDatabase = menu.findItem(R.id.action_exportDatabase);
        mExportDatabase.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i = new Intent(UnitImagesListviewActivity.this, DatabaseExportFile.class);
                startActivity(i);
                return false;
            }
        });

        MenuItem mImportDatabase = menu.findItem(R.id.action_importDatabase);
        mImportDatabase.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i = new Intent(UnitImagesListviewActivity.this, DatabaseImportFile.class);
                startActivity(i);
                return false;
            }
        });

        MenuItem mHelpInformation = menu.findItem(R.id.action_help);
        mHelpInformation.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                displayHelpAlertDialog();
                return false;
            }
        });
        MenuItem mLicenseInformation = menu.findItem(R.id.action_licenseInformation);
        mLicenseInformation.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                displayLicensesAlertDialog();
                return false;
            }
        });
        MenuItem mAbout = menu.findItem(R.id.action_about);
        mAbout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i = new Intent(UnitImagesListviewActivity.this, AboutActivity.class);
                startActivity(i);
                return false;
            }
        });

        MenuItem mDeleteAllEntries = menu.findItem(R.id.action_deleteAllEntries);
        mDeleteAllEntries.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(UnitImagesListviewActivity.this);
                alertDialog.setTitle("Datenbank löschen");
                String message = "\nEs werden alle Einträge gelöscht.\n\nDrücken Sie auf LÖSCHEN, um alle\nEinträge endgültig zu löschen.";
                alertDialog.setMessage(message);
                RelativeLayout container = new RelativeLayout(UnitImagesListviewActivity.this);
                RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                container.setLayoutParams(rlParams);
                alertDialog.setView(container);
                alertDialog.setPositiveButton("löschen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbUnitHandler.deleteAllUnits();
                        Intent i = new Intent(UnitImagesListviewActivity.this, UnitImagesListviewActivity.class);
                        startActivity(i);
                        finish();
                    }
                });
                alertDialog.setNegativeButton("abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
                alertDialog.show();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    // error dialog
    private void errorAndQuitAlert(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(UnitImagesListviewActivity.this).create();
        alertDialog.setTitle("Fehler");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finishAndRemoveTask(); // stops the app
                    }
                });
        // to avoid the back button usage
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                finishAndRemoveTask(); // stops the app
                finishAffinity();
            }
        });
        alertDialog.show();
    }

    // run: displayLicensesAlertDialog();
    // display licenses dialog see: https://bignerdranch.com/blog/open-source-licenses-and-android/
    private void displayLicensesAlertDialog() {
        WebView view = (WebView) LayoutInflater.from(this).inflate(R.layout.dialog_licenses, null);
        view.loadUrl("file:///android_asset/open_source_licenses.html");
        android.app.AlertDialog mAlertDialog = new android.app.AlertDialog.Builder(UnitImagesListviewActivity.this).create();
        mAlertDialog = new android.app.AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setTitle(getString(R.string.action_licenses))
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void displayLicensesDialogFragment() {
        LicensesDialogFragment dialog = LicensesDialogFragment.newInstance();
        dialog.show(getSupportFragmentManager(), "LicensesDialog");
    }

    // help
    private void displayHelpAlertDialog() {
        WebView view = (WebView) LayoutInflater.from(this).inflate(R.layout.dialog_help, null);
        view.loadUrl("file:///android_asset/help.html");
        android.app.AlertDialog mAlertDialog = new android.app.AlertDialog.Builder(UnitImagesListviewActivity.this).create();
        mAlertDialog = new android.app.AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setTitle(getString(R.string.action_help))
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void displayHelpDialogFragment() {
        HelpDialogFragment dialog = HelpDialogFragment.newInstance();
        dialog.show(getSupportFragmentManager(), "HelpDialog");
    }

    // error dialog
    private void alertView(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(UnitImagesListviewActivity.this).create();
        alertDialog.setTitle("Fehler");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

}