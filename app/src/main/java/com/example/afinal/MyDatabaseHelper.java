package com.example.afinal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "itemMem.db";

    static final String tb_name = "item_info";

    public MyDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, String tb_name) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.e("MyDatabaseHelper", "MyDatabaseHelper");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE IF NOT EXISTS " +
                tb_name +
                "(year INTEGER, " + " month INTEGER, " + " day INTEGER, " +
                "type INTEGER, " + "category INTEGER, " +
                "itemName VARCHAR(32), " + "cash INTEGER )";

        sqLiteDatabase.execSQL(createTable);

        Log.e("MyDatabaseHelper", "DB created or opened");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // drop table => delete table
        final String SQL = "DROP TABLE " + tb_name;
        sqLiteDatabase.execSQL(SQL);
    }

}
