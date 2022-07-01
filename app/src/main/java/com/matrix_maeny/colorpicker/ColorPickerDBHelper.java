package com.matrix_maeny.colorpicker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ColorPickerDBHelper extends SQLiteOpenHelper {
    public ColorPickerDBHelper(@Nullable Context context) {
        super(context, "ColorPicker.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create Table ColorPicker(name TEXT primary key,color INT, hexCode TEXT, argbCode TEXT, imageUri TEXT, pointX INT, pointY INT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop Table if exists ColorPicker");
    }

    //insertion
    public boolean insertColor(String name, int color, String hexCode, String argbCode, String imageUri, int pointX, int pointY) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("name", name);
        cv.put("color", color);
        cv.put("hexCode", hexCode);
        cv.put("argbCode", argbCode);
        cv.put("imageUri", imageUri);
        cv.put("pointX", pointX);
        cv.put("pointY", pointY);

        long result = db.insert("ColorPicker", null, cv);


        return result != -1;
    }

    // update
    public boolean updateColor(String name, int color, String hexCode, String argbCode, String imageUri, int pointX, int pointY) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("color", color);
        cv.put("hexCode", hexCode);
        cv.put("argbCode", argbCode);
        cv.put("imageUri", imageUri);
        cv.put("pointX", pointX);
        cv.put("pointY", pointY);

        long result = db.update("ColorPicker", cv, "name=?", new String[]{name});

        return result != -1;
    }

    // delete
    public void deleteColor(String name) {

        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.delete("ColorPicker", "name=?", new String[]{name});


    }

    // get data
    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("Select * from ColorPicker", null);
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("ColorPicker", "", null);
    }
}
