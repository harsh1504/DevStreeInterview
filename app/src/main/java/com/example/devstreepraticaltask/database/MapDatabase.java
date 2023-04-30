package com.example.devstreepraticaltask.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.devstreepraticaltask.model.MapModel;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MapDatabase extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "map_database.db";
    static final int DATABASE_VERSION = 1;
    final String TABLE_MAP = "map_data";
    final String KEY_ID = "id";
    final String KEY_NAME = "place_name";
    final String KEY_LATLNG = "latlng";
    final String KEY_LATITUDE = "latitude";
    final String KEY_LONGITUDE = "longitude";
    String tbl = "CREATE TABLE IF NOT EXISTS map_data(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            "place_name TEXT," +
            "latlng TEXT," +
            "latitude INTEGER," +
            "longitude TEXT)";

    public MapDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tbl);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(tbl);
    }

    public void insertLocationData(String pName, String latlog1, Double latitude, Double longitude) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, pName);
        values.put(KEY_LATLNG, latlog1);
        values.put(KEY_LATITUDE, latitude);
        values.put(KEY_LONGITUDE, longitude);
        getWritableDatabase().insert(TABLE_MAP, null, values);

    }

    public void updateData(Integer id, String placeName, String latlong1, Double latitude, Double longitude) {
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_ID, id);
        newValues.put(KEY_NAME, placeName);
        newValues.put(KEY_LATLNG, latlong1);
        newValues.put(KEY_LATITUDE, latitude);
        newValues.put(KEY_LONGITUDE, longitude);
        getWritableDatabase().update(TABLE_MAP, newValues, "id=" + id, null);

    }

    public ArrayList<String> getAllData() {
        ArrayList<String> placeList = new ArrayList<String>();
//        String selectQuery = "SELECT place_name FROM " + TABLE_MAP;
        String selectQuery = "SELECT place_name FROM " + TABLE_MAP;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                placeList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return placeList;
    }

    public ArrayList<MapModel> getModelData() {
        ArrayList<MapModel> allData = new ArrayList<MapModel>();
        String selectQuery = "SELECT * FROM " + TABLE_MAP;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                allData.add(new MapModel(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getDouble(3),
                        cursor.getDouble(4)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return allData;
    }


    public ArrayList<MapModel> getModelData1(String sortType) {
        ArrayList<MapModel> allData = new ArrayList<MapModel>();
        String selectQuery = "SELECT * FROM " + TABLE_MAP + " ORDER BY " + KEY_ID + " " + sortType ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                allData.add(new MapModel(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getDouble(3),
                        cursor.getDouble(4)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return allData;
    }



    public void deletePlace(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MAP, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public ArrayList<String> getAllLatLong() {
        ArrayList<String> latlongList = new ArrayList<String>();
        String selectQuery = "SELECT latlng FROM " + TABLE_MAP;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                latlongList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return latlongList;
    }

    /*public ArrayList<LatLng> getLatLng() {
        ArrayList<LatLng> list = new ArrayList<LatLng>();
        String selectQuery = "SELECT latlng FROM " + TABLE_MAP;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;

    }*/


}
