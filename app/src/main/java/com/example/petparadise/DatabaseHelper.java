package com.example.petparadise;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PetParadise.db";
    private static final int DATABASE_VERSION = 2; // Tăng version để cập nhật bảng

    // Tên bảng và các cột
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FULL_NAME = "full_name";
    public static final String COLUMN_EMAIL_PHONE = "email_phone";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ROLE = "role"; // Cột mới cho phân quyền

    // Câu lệnh tạo bảng
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_FULL_NAME + " TEXT, " +
                    COLUMN_EMAIL_PHONE + " TEXT UNIQUE, " +
                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_ROLE + " TEXT" +
                    ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        // Tạo mặc định một tài khoản Admin khi khởi tạo DB
        addAdmin(db, "Admin PetParadise", "admin@pet.com", "admin123");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Phương thức nội bộ để thêm admin lúc khởi tạo
    private void addAdmin(SQLiteDatabase db, String fullName, String email, String password) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_FULL_NAME, fullName);
        values.put(COLUMN_EMAIL_PHONE, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_ROLE, "admin");
        db.insert(TABLE_USERS, null, values);
    }

    // Phương thức thêm người dùng mới (Mặc định là 'user')
    public boolean addUser(String fullName, String emailPhone, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FULL_NAME, fullName);
        values.put(COLUMN_EMAIL_PHONE, emailPhone);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_ROLE, "user"); // Mặc định là user

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    // Phương thức kiểm tra đăng nhập và lấy Role
    public String checkUserRole(String emailPhone, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String role = null;
        String[] columns = {COLUMN_ROLE};
        String selection = COLUMN_EMAIL_PHONE + " = ?" + " AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {emailPhone, password};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            role = cursor.getString(0);
            cursor.close();
        }
        return role; // Trả về "admin", "user" hoặc null nếu sai thông tin
    }
}
