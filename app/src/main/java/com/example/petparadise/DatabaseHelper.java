package com.example.petparadise;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PetParadise.db";
    private static final int DATABASE_VERSION = 3; // Nâng cấp version

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FULL_NAME = "full_name";
    public static final String COLUMN_EMAIL_PHONE = "email_phone";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ROLE = "role";
    public static final String COLUMN_AVATAR = "avatar"; // Cột lưu ảnh

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_FULL_NAME + " TEXT, " +
                    COLUMN_EMAIL_PHONE + " TEXT UNIQUE, " +
                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_ROLE + " TEXT, " +
                    COLUMN_AVATAR + " TEXT" +
                    ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        addAdmin(db, "Admin PetParadise", "admin@pet.com", "admin123");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_AVATAR + " TEXT");
        }
    }

    private void addAdmin(SQLiteDatabase db, String fullName, String email, String password) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_FULL_NAME, fullName);
        values.put(COLUMN_EMAIL_PHONE, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_ROLE, "admin");
        db.insert(TABLE_USERS, null, values);
    }

    public boolean addUser(String fullName, String emailPhone, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FULL_NAME, fullName);
        values.put(COLUMN_EMAIL_PHONE, emailPhone);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_ROLE, "user");
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public String checkUserRole(String emailPhone, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String role = null;
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ROLE}, COLUMN_EMAIL_PHONE + "=? AND " + COLUMN_PASSWORD + "=?", new String[]{emailPhone, password}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            role = cursor.getString(0);
            cursor.close();
        }
        return role;
    }

    public String getUserName(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String name = "";
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_FULL_NAME}, COLUMN_EMAIL_PHONE + "=?", new String[]{email}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            name = cursor.getString(0);
            cursor.close();
        }
        return name;
    }

    public boolean updateUserName(String email, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FULL_NAME, newName);
        return db.update(TABLE_USERS, values, COLUMN_EMAIL_PHONE + "=?", new String[]{email}) > 0;
    }

    // Cập nhật ảnh đại diện
    public boolean updateUserAvatar(String email, String avatarPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AVATAR, avatarPath);
        return db.update(TABLE_USERS, values, COLUMN_EMAIL_PHONE + "=?", new String[]{email}) > 0;
    }

    // Lấy ảnh đại diện
    public String getUserAvatar(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String avatar = "";
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_AVATAR}, COLUMN_EMAIL_PHONE + "=?", new String[]{email}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            avatar = cursor.getString(0);
            cursor.close();
        }
        return avatar;
    }
}
