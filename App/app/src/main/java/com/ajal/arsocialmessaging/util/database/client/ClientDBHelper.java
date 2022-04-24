package com.ajal.arsocialmessaging.util.database.client;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ajal.arsocialmessaging.util.database.Banner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

// REFERENCE: https://www.androidhive.info/2011/11/android-sqlite-database-tutorial/ 28/02/2002 14:06
public class ClientDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "notifications_db";
    private static final String TABLE_NAME = "newBanners";
    private static final String COLUMN_POSTCODE = "postcode";
    private static final String COLUMN_MESSAGE_ID = "message_id";
    private static final String COLUMN_CREATED_AT = "created_at";
    private static final String CREATE_TABLE_SQL =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_POSTCODE + " VARCHAR(255) NOT NULL,"
                    + COLUMN_MESSAGE_ID + " INTEGER NOT NULL,"
                    + COLUMN_CREATED_AT + " TIMESTAMP"
                    + ")";
    private Semaphore dbMutex = new Semaphore(1);

    public ClientDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        resetTable();
    }

    public long insertNewBanner(Banner banner) {
        try {
            dbMutex.acquire();
            List<Banner> banners = getAllNewBanners();
            for (Banner b : banners) {
                if (banner.getPostcode().equals(b.getPostcode()) &&
                    banner.getMessage().equals(b.getMessage()) &&
                    banner.getTimestamp().equals(b.getTimestamp())) {
                    return -1; // Temporary solution: Do not add duplicate banners to notifications_db
                }
            }

            // Add banner to notifications_db
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_POSTCODE, banner.getPostcode());
            values.put(COLUMN_MESSAGE_ID, banner.getMessage());
            values.put(COLUMN_CREATED_AT, banner.getTimestamp());

            long id = db.insert(TABLE_NAME, null, values);
            db.close();
            dbMutex.release();
            return id;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<Banner> getAllNewBanners() {
        List<Banner> result = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " ORDER BY " +
                COLUMN_CREATED_AT + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String postcode = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POSTCODE));
                int messageId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE_ID));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT));
                Banner banner = new Banner(postcode, messageId, timestamp);
                result.add(banner);
            } while (cursor.moveToNext());
        }
        db.close();

        return result;
    }

    public void resetTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME); // Drop older table if existed
        db.execSQL(CREATE_TABLE_SQL);
    }
}
