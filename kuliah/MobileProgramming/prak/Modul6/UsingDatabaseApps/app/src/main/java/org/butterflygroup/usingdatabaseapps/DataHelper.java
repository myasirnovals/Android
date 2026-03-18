package org.butterflygroup.usingdatabaseapps;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "akademik.db";
    private static final int DATABASE_VERSION = 1;

    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        String sql = "create table mahasiswa(nim integer primary key, nama text null, tgl text null, jk text null, alamat text null);";
        Log.d("Data", "onCreate: " + sql);
        db.execSQL(sql);

        //insert data dummy
        sql = "INSERT INTO mahasiswa (nim, nama, tgl, jk, alamat) VALUES (3411101111, 'Mahasiswa 1', '2020-07-11', 'Laki-laki', 'Cimahi');";
        db.execSQL(sql);
        sql = "INSERT INTO mahasiswa (nim, nama, tgl, jk, alamat) VALUES (3411101112,'Mahasiswa 2','2020-07-11','Laki-laki','Cimahi');";
        db.execSQL(sql);
        sql = "INSERT INTO mahasiswa (nim, nama, tgl, jk, alamat) VALUES (3411101113,'Mahasiswa 3','2020-07-11','Laki-laki','Cimahi');";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

    }
}
