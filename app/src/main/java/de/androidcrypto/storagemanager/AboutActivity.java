package de.androidcrypto.storagemanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AboutActivity extends AppCompatActivity {

    private DBUnitHandler dbUnitHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        TextView tv = (TextView) findViewById(R.id.appVersion);
        tv.setText(getResources().getString(R.string.appVersionData));
        TextView tv3 = (TextView) findViewById(R.id.projectpage);
        tv3.setMovementMethod(LinkMovementMethod.getInstance());
        TextView tv4 = (TextView) findViewById(R.id.sourcecodepage);
        tv4.setMovementMethod(LinkMovementMethod.getInstance());

        // nr database entries and size
        // on below line we are initialing our dbhandler class.
        dbUnitHandler = new DBUnitHandler(AboutActivity.this);
        TextView tv5 = (TextView) findViewById(R.id.nrDatabaseEntries);
        tv5.setText(dbUnitHandler.getNrDatabaseRecords());
        TextView tv6 = (TextView) findViewById(R.id.databaseSize);
        tv6.setText(dbUnitHandler.getDatabaseSize(getApplicationContext()) + " Bytes");
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
                Intent intent = new Intent(AboutActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}