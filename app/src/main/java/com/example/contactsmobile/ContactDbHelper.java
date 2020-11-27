package com.example.contactsmobile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class ContactDbHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "Contact.db";
    public static final int DATABASE_VERSION = 1;
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + Contact.TABLE_NAME + " (" +
            Contact._ID + " INTEGER PRIMARY KEY, " +
            Contact.COLUMN_NAME + " TEXT, " +
            Contact.COLUMN_PHONE + " TEXT, " +
            Contact.COLUMN_ADDRESS + " TEXT, " +
            Contact.COLUMN_LATITUDE + " TEXT, " +
            Contact.COLUMN_LONGITUDE + " TEXT);";

    private static SQLiteDatabase contactDb;

    public ContactDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        contactDb =  this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Nothing
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Nothing
    }

    private ContentValues getContentValuesFrom(Contact contact) {
        ContentValues values = new ContentValues();
        values.put(Contact.COLUMN_NAME ,contact.getName());
        values.put(Contact.COLUMN_PHONE, contact.getPhone());
        values.put(Contact.COLUMN_ADDRESS, contact.getPhone());
        values.put(Contact.COLUMN_LATITUDE, contact.getLatitude());
        values.put(Contact.COLUMN_LONGITUDE, contact.getLongitude());

        return values;
    }

    private Contact getFromCursor(Cursor cur) {
        long id = cur.getInt( cur.getColumnIndex(Contact._ID) );
        String name = cur.getString( cur.getColumnIndex(Contact.COLUMN_NAME) );
        String phone = cur.getString( cur.getColumnIndex(Contact.COLUMN_PHONE) );
        String address = cur.getString( cur.getColumnIndex(Contact.COLUMN_ADDRESS) );
        String lat = cur.getString( cur.getColumnIndex(Contact.COLUMN_LATITUDE) );
        String lng = cur.getString( cur.getColumnIndex(Contact.COLUMN_LONGITUDE) );

        return new Contact(id, name, address, phone, lat, lng);
    }

    public ArrayList<Contact> getAll() {
        ArrayList<Contact> contacts = new ArrayList<>();
        String query = "SELECT * FROM " + Contact.TABLE_NAME;
        Cursor cur = contactDb.rawQuery(query, null);

        if (cur.getCount() > 0) {
            cur.moveToFirst();
        }

        for (int i = 0; i < cur.getCount(); i++) {
            Contact contact = getFromCursor(cur);

            contacts.add(contact);
            cur.moveToNext();
        }

        cur.close();
        return contacts;
    }

    public Contact getLastOne() {
        String query = "SELECT * FROM " + Contact.TABLE_NAME + " ORDER BY _id DESC LIMIT 1";
        Cursor cur = contactDb.rawQuery(query, null);

        if (cur.getCount() > 0) {
            cur.moveToFirst();
        }

        return getFromCursor(cur);
    }

    public long insertOne(Contact contact) {
        ContentValues values = getContentValuesFrom(contact);
        return contactDb.insert(Contact.TABLE_NAME, null, values);
    }

    public void updateOne(long rowId, Contact newContact) {
        ContentValues values = getContentValuesFrom(newContact);
        String whereClause = Contact._ID + " = ?";
        String[] whereArgs = { String.valueOf(rowId) };

        int updatedRows = contactDb.update(Contact.TABLE_NAME, values, whereClause, whereArgs);
    }

    public static void deleteOne(long rowId) {
        String whereClause = Contact._ID + " = ?";
        String[] whereArgs = { String.valueOf(rowId) };

        int deletedRows = contactDb.delete(Contact.TABLE_NAME, whereClause, whereArgs);
    }
}
