package com.example.trabajofinal.ui.theme.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import com.example.trabajofinal.ui.theme.models.Reading;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "lecturas.db";
    private static final int DB_VERSION = 1;

    public MyDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE lecturas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "sensor TEXT, " +
                "valor REAL, " +
                "timestamp INTEGER DEFAULT (strftime('%s','now')))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS lecturas");
        onCreate(db);
    }


    // Obtiene todas las lecturas ordenadas por fecha descendente
    public List<Reading> getAllReadings() {
        List<Reading> readings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT sensor, valor, timestamp FROM lecturas ORDER BY timestamp DESC", null);

        if (cursor.moveToFirst()) {
            do {
                String sensor = cursor.getString(cursor.getColumnIndexOrThrow("sensor"));
                float valor = cursor.getFloat(cursor.getColumnIndexOrThrow("valor"));
                long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"));
                readings.add(new Reading(sensor, valor, timestamp));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return readings;
    }

    public long addReading(Reading reading) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sensor", reading.getSensor());
        values.put("valor", reading.getValue());
        values.put("timestamp", reading.getTimestamp());
        long id = db.insert("lecturas", null, values);
        db.close();
        return id;
    }
}
