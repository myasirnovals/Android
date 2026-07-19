package org.butterflygroup.aplikasianggaran;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class BudgetDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "budget.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_DESCRIPTION = "description";

    public BudgetDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_TRANSACTIONS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_DATE + " TEXT NOT NULL, "
                + COLUMN_TYPE + " TEXT NOT NULL, "
                + COLUMN_CATEGORY + " TEXT NOT NULL, "
                + COLUMN_AMOUNT + " INTEGER NOT NULL, "
                + COLUMN_DESCRIPTION + " TEXT NOT NULL DEFAULT ''"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        onCreate(db);
    }

    public long insertTransaction(Transaction transaction) {
        SQLiteDatabase database = getWritableDatabase();
        return database.insertWithOnConflict(TABLE_TRANSACTIONS, null, toContentValues(transaction), SQLiteDatabase.CONFLICT_REPLACE);
    }

    public int deleteTransaction(long id) {
        SQLiteDatabase database = getWritableDatabase();
        return database.delete(TABLE_TRANSACTIONS, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    public int deleteAllTransactions() {
        SQLiteDatabase database = getWritableDatabase();
        return database.delete(TABLE_TRANSACTIONS, null, null);
    }

    public void replaceAllTransactions(List<Transaction> transactions) {
        SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        try {
            database.delete(TABLE_TRANSACTIONS, null, null);
            if (transactions != null) {
                for (Transaction transaction : transactions) {
                    database.insertWithOnConflict(TABLE_TRANSACTIONS, null, toContentValues(transaction), SQLiteDatabase.CONFLICT_REPLACE);
                }
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public List<Transaction> getAllTransactionsOrdered() {
        SQLiteDatabase database = getReadableDatabase();
        List<Transaction> transactions = new ArrayList<>();
        Cursor cursor = database.query(TABLE_TRANSACTIONS, null, null, null, null, null, COLUMN_DATE + " ASC, " + COLUMN_ID + " ASC");
        try {
            while (cursor.moveToNext()) {
                transactions.add(readTransaction(cursor));
            }
        } finally {
            cursor.close();
        }
        return transactions;
    }

    public long getTransactionCount() {
        SQLiteDatabase database = getReadableDatabase();
        return DatabaseUtils.queryNumEntries(database, TABLE_TRANSACTIONS);
    }

    private ContentValues toContentValues(Transaction transaction) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, transaction.getDate());
        values.put(COLUMN_TYPE, transaction.getType());
        values.put(COLUMN_CATEGORY, transaction.getCategory());
        values.put(COLUMN_AMOUNT, transaction.getAmount());
        values.put(COLUMN_DESCRIPTION, transaction.getDescription());
        if (transaction.getId() > 0) {
            values.put(COLUMN_ID, transaction.getId());
        }
        return values;
    }

    private Transaction readTransaction(Cursor cursor) {
        return new Transaction(
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
        );
    }
}