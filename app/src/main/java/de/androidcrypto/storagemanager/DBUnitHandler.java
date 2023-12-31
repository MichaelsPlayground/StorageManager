package de.androidcrypto.storagemanager;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class DBUnitHandler extends SQLiteOpenHelper {
    private static final String TAG = DBUnitHandler.class.getSimpleName();

    // creating a constant variables for our database.
    // below variable is for our database name.
    private static final String DB_NAME = "unitdb";

    // below int is our database version,
    // when changing don't forget to change HEADER & FOOTER as well
    private static final int DB_VERSION = 1;

    // below variable is for our table name.
    private static final String TABLE_NAME = "units";

    // below variable is for our id column.
    private static final String ID_COL = "id";

    // below variable are for our column names
    private static final String UNIT_NUMBER = "unitnumber";
    private static final String UNIT_SHORT_CONTENT = "unitshortcontent";
    private static final String UNIT_CONTENT = "unitcontent";
    private static final String UNIT_TYPE = "unittype";
    private static final String UNIT_WEIGHT = "unitweight";
    private static final String UNIT_PLACE = "unitplace";
    private static final String UNIT_ROOM = "unitroom";
    private static final String UNIT_LAST_EDIT = "unitlastedit";
    private static final String UNIT_ID_SERVER = "unitidserver";
    private static final String UNIT_TAG_UID_1 = "unittaguid1";
    private static final String UNIT_TAG_UID_2 = "unittaguid2";
    private static final String UNIT_TAG_UID_3 = "unittaguid3";
    private static final String UNIT_IMAGE_FILENAME_1 = "unitimagefilename1";
    private static final String UNIT_IMAGE_FILENAME_2 = "unitimagefilename2";
    private static final String UNIT_IMAGE_FILENAME_3 = "unitimagefilename3";
    private static final String UNIT_DELETED = "unitdeleted";
    private static final String RESERVED_COL = "reserved";

    // get a context to the database
    private Context dbContext;

    // header and footer export string
    private static final String DATABASE_HEADER = "### DATABASE START V1 ###";
    private static final String DATABASE_FOOTER = "### DATABASE END V1 ###";
    private static final String DATA_HEADER = "#Z#";
    private static final String DATA_FOOTER = "#Y#";
    private int PBKDF2ITERATIONS = 15000; // for en-/decryption key derivation from password

    // creating a constructor for our database handler.
    public DBUnitHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        dbContext = context;
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {
        // on below line we are creating
        // an sqlite query and we are
        // setting our column names
        // along with their data types.
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + UNIT_NUMBER + " TEXT,"
                + UNIT_SHORT_CONTENT + " TEXT,"
                + UNIT_CONTENT + " TEXT,"
                + UNIT_TYPE + " TEXT,"
                + UNIT_WEIGHT + " TEXT,"
                + UNIT_PLACE + " TEXT,"
                + UNIT_ROOM + " TEXT,"
                + UNIT_LAST_EDIT + " TEXT,"
                + UNIT_ID_SERVER + " TEXT,"
                + UNIT_TAG_UID_1 + " TEXT,"
                + UNIT_TAG_UID_2 + " TEXT,"
                + UNIT_TAG_UID_3 + " TEXT,"
                + UNIT_IMAGE_FILENAME_1 + " TEXT,"
                + UNIT_IMAGE_FILENAME_2 + " TEXT,"
                + UNIT_IMAGE_FILENAME_3 + " TEXT,"
                + UNIT_DELETED + " TEXT,"
                + RESERVED_COL + " TEXT)";
        // at last we are calling a exec sql
        // method to execute above sql query
        db.execSQL(query);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addNewUnit(String unitNumber, String unitShortContent, String unitContent, String unitType,
                           String unitWeight,  String unitPlace, String unitRoom, String unitLastEdit,
                           String unitIdServer, String unitTagUid1, String unitTagUid2, String unitTagUid3,
                           String unitImageFilename1, String unitImageFilename2, String unitImageFilename3,
                           String unitDeleted) {

        // check that unitNumber is unique
        if (isUnitNumberDuplicate(unitNumber)) {
            Log.d(TAG, "unitNumber is duplicate");
            return false;
        }

        // on below line we are creating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are creating a
        // variable for content values.
        ContentValues values = new ContentValues();

        // on below line we are passing all values
        // along with its key and value pair.
        values.put(UNIT_NUMBER, unitNumber); // idx 01
        values.put(UNIT_SHORT_CONTENT, unitShortContent);
        values.put(UNIT_CONTENT, unitContent);
        values.put(UNIT_TYPE, unitType);
        values.put(UNIT_WEIGHT, unitWeight);
        values.put(UNIT_PLACE, unitPlace);
        values.put(UNIT_ROOM, unitRoom);
        values.put(UNIT_LAST_EDIT, unitLastEdit);
        values.put(UNIT_ID_SERVER, unitIdServer); // idx 08
        values.put(UNIT_TAG_UID_1, unitTagUid1);
        values.put(UNIT_TAG_UID_2, unitTagUid2);
        values.put(UNIT_TAG_UID_3, unitTagUid3);
        values.put(UNIT_IMAGE_FILENAME_1, unitImageFilename1);
        values.put(UNIT_IMAGE_FILENAME_2, unitImageFilename2);
        values.put(UNIT_IMAGE_FILENAME_3, unitImageFilename3);
        values.put(UNIT_DELETED, unitDeleted);
        values.put(RESERVED_COL, "");

        // after adding all values we are passing
        // content values to our table.
        db.insert(TABLE_NAME, null, values);

        // at last we are closing our
        // database after adding database.
        db.close();
        Log.d(TAG, "database entry added");
        return true;
    }

    public boolean updateUnit(String unitId, String unitIdServer, String unitNumber, String unitShortContent,
                           String unitContent, String unitType, String unitWeight, String unitPlace, String unitRoom,
                           String unitLastEdit, String unitTagUid1, String unitTagUid2, String unitTagUid3,
                           String unitImageFilename1, String unitImageFilename2, String unitImageFilename3,
                           String unitDeleted) {
        // check that unitNumber is unique
        if (isUnitNumberDuplicate(unitNumber)) {
            Log.d(TAG, "unitNumber is duplicate");
            return false;
        }

        // the LastEditValue is replaced by actual one.
        // The reason that it is in the parameter list is simple: same parameter data as for a "addUnit"
        unitLastEdit = Utils.getShortTimestamp();

        // on below line we are updating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are creating a
        // variable for content values.
        ContentValues values = new ContentValues();

        // on below line we are passing all values
        // along with its key and value pair.
        values.put(UNIT_NUMBER, unitNumber); // idx 01
        values.put(UNIT_SHORT_CONTENT, unitShortContent);
        values.put(UNIT_CONTENT, unitContent);
        values.put(UNIT_TYPE, unitType);
        values.put(UNIT_WEIGHT, unitWeight);
        values.put(UNIT_PLACE, unitPlace);
        values.put(UNIT_ROOM, unitRoom);
        values.put(UNIT_LAST_EDIT, unitLastEdit);
        values.put(UNIT_ID_SERVER, unitIdServer); //idx 08
        values.put(UNIT_TAG_UID_1, unitTagUid1);
        values.put(UNIT_TAG_UID_2, unitTagUid2);
        values.put(UNIT_TAG_UID_3, unitTagUid3);
        values.put(UNIT_IMAGE_FILENAME_1, unitImageFilename1);
        values.put(UNIT_IMAGE_FILENAME_2, unitImageFilename2);
        values.put(UNIT_IMAGE_FILENAME_3, unitImageFilename3);
        values.put(UNIT_DELETED, unitDeleted);
        values.put(RESERVED_COL, "");

        // after adding all values we are passing
        // content values to our table.
        db.update(TABLE_NAME, values, "id=?", new String[]{unitId});

        // at last we are closing our
        // database after adding database.
        db.close();
        Log.d(TAG, "database entry updated for id " + unitId);
        return true;
    }

    public boolean updateUnitTagUid1(String unitId, String tagUid) {
        // on below line we are updating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UNIT_TAG_UID_1, tagUid);
        int result = db.update(TABLE_NAME, contentValues, "id=?", new String[]{unitId});
        db.close();
        Log.d(TAG, "database entry updated for id " + unitId + " with tagUid1: " + tagUid);
        Log.d(TAG, "update result: " + result);
        return true;
    }

    public boolean updateUnitTagUid2(String unitId, String tagUid) {
        // on below line we are updating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UNIT_TAG_UID_2, tagUid);
        int result = db.update(TABLE_NAME, contentValues, "id=?", new String[]{unitId});
        db.close();
        Log.d(TAG, "database entry updated for id " + unitId + " with tagUid2: " + tagUid);
        Log.d(TAG, "update result: " + result);
        return true;
    }

    public boolean updateUnitTagUid3(String unitId, String tagUid) {
        // on below line we are updating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UNIT_TAG_UID_3, tagUid);
        int result = db.update(TABLE_NAME, contentValues, "id=?", new String[]{unitId});
        db.close();
        Log.d(TAG, "database entry updated for id " + unitId + " with tagUid3: " + tagUid);
        Log.d(TAG, "update result: " + result);
        return true;
    }

    public boolean updateUnitImageFilename1(String unitId, String filename) {
        // on below line we are updating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UNIT_IMAGE_FILENAME_1, filename);
        int result = db.update(TABLE_NAME, contentValues, "id=?", new String[]{unitId});
        db.close();
        Log.d(TAG, "database entry updated for id " + unitId + " with filename1: " + filename);
        Log.d(TAG, "update result: " + result);
        return true;
    }

    public boolean updateUnitImageFilename2(String unitId, String filename) {
        // on below line we are updating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UNIT_IMAGE_FILENAME_2, filename);
        int result = db.update(TABLE_NAME, contentValues, "id=?", new String[]{unitId});
        db.close();
        Log.d(TAG, "database entry updated for id " + unitId + " with filename2: " + filename);
        Log.d(TAG, "update result: " + result);
        return true;
    }

    public boolean updateUnitImageFilename3(String unitId, String filename) {
        // on below line we are updating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UNIT_IMAGE_FILENAME_3, filename);
        int result = db.update(TABLE_NAME, contentValues, "id=?", new String[]{unitId});
        db.close();
        Log.d(TAG, "database entry updated for id " + unitId + " with filename3: " + filename);
        Log.d(TAG, "update result: " + result);
        return true;
    }


    // below is the method for deleting our course.
    public void deleteAllUnits() {

        // on below line we are creating
        // a variable to write our database.
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are calling a method to delete our
        // course and we are comparing it with our course name.
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    public void deleteUnit(String unitId) {
        // on below line we are creating
        // a variable to write our database.
        SQLiteDatabase db = this.getWritableDatabase();
        // on below line we are calling a method to delete our
        // course and we are comparing it with our course name.
        db.delete(TABLE_NAME, "id=?", new String[]{unitId});
        db.close();
    }


    public ArrayList<StorageUnitModel> readUnits() {
        // on below line we are creating a
        // database for reading our database.
        SQLiteDatabase db = this.getReadableDatabase();

        // on below line we are creating a cursor with query to read data from database.
        Cursor cursorUnits = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        // on below line we are creating a new array list.
        ArrayList<StorageUnitModel> unitModelArrayList = new ArrayList<>();

        // moving our cursor to first position.
        if (cursorUnits.moveToFirst()) {
            do {
                // on below line we are adding the data from cursor to our array list.

                // read data to storageUnitModel
                unitModelArrayList.add(new StorageUnitModel(
                        cursorUnits.getString(0), // 0 = unitId
                        cursorUnits.getString(9), // 09 = unitIdServer
                        cursorUnits.getString(1),
                        cursorUnits.getString(2),
                        cursorUnits.getString(3),
                        cursorUnits.getString(4),
                        cursorUnits.getString(5),
                        cursorUnits.getString(6),
                        cursorUnits.getString(7),
                        cursorUnits.getString(8),
                        cursorUnits.getString(10), // tagUids 1-3
                        cursorUnits.getString(11),
                        cursorUnits.getString(12),
                        cursorUnits.getString(13), // imageFilenames 1-3
                        cursorUnits.getString(14),
                        cursorUnits.getString(15),
                        cursorUnits.getString(16))); // 16 = unitDeleted
            } while (cursorUnits.moveToNext());
            // moving our cursor to next.
        }
        // at last closing our cursor
        // and returning our array list.
        cursorUnits.close();
        db.close();
        return unitModelArrayList;
    }

    public String getNrDatabaseRecords() {
        SQLiteDatabase db = this.getReadableDatabase();
        long numberOfRows = DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return String.valueOf(numberOfRows);
    }

    // we have created a new method for reading all the courses.
    public String getDatabaseSize(Context context) {
        File f = context.getDatabasePath(DB_NAME);
        long dbSize = f.length();
        return String.valueOf(dbSize);
    }

    @SuppressLint("Range")
    public String[] getUnitNumbers() {
        // https://stackoverflow.com/a/44339427/8166854
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + UNIT_NUMBER + " FROM " + TABLE_NAME + ";";
        Cursor cursor = db.rawQuery(query, null);
        String returnString[] = new String[cursor.getCount()]; // # elements = rows
        int i = 0;
        while (cursor.moveToNext()) {
            returnString[i++] = cursor.getString(
                    cursor.getColumnIndex(UNIT_NUMBER)
            );
        }
        cursor.close();
        db.close();
        return returnString;
    }

    public boolean isUnitNumberDuplicate(String searchString) {
        return isDuplicate(UNIT_NUMBER, searchString);
    }

    public boolean isDuplicate(String columnName, String searchString) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + columnName + " = " + "'" +
                searchString + "'", null);
        if(cursor.moveToFirst())
        {
            cursor.close();
            db.close();
            return true;
        }
        else
        {
            cursor.close();
            db.close();
            return false;
        }
    }

    public int getIdFromUnitNumber(String unitNumber) {
        Log.d(TAG, "getIdFromUnitNumber: " + unitNumber);
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor cursor = db.rawQuery("SELECT id FROM user WHERE unitNumber=?", new String[]{unitNumber});
        //Cursor cursor = db.rawQuery("SELECT id FROM user WHERE unitNumber=?", new String[]{unitNumber});
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + UNIT_NUMBER + " = " + "'" +
                unitNumber + "'", null);
        int id = -1;
        if (cursor.moveToFirst()) id = cursor.getInt(0);
        cursor.close();
        db.close();
        Log.d(TAG, "tagId found: " + id);
        return id;
    }
}
