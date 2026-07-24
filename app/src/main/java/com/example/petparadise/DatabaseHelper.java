package com.example.petparadise;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PetParadise.db";
    private static final int DATABASE_VERSION = 6; // Nâng lên version 6 để đảm bảo làm mới bảng

    // Bảng Users
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FULL_NAME = "full_name";
    public static final String COLUMN_EMAIL_PHONE = "email_phone";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ROLE = "role";
    public static final String COLUMN_AVATAR = "avatar";

    // Bảng Products
    public static final String TABLE_PRODUCTS = "products";
    public static final String COLUMN_PROD_ID = "id";
    public static final String COLUMN_PROD_NAME = "name";
    public static final String COLUMN_PROD_CATEGORY = "category";
    public static final String COLUMN_PROD_PRICE = "price";
    public static final String COLUMN_PROD_IMAGE = "image";
    public static final String COLUMN_PROD_DESC = "description";
    public static final String COLUMN_PROD_QUANTITY = "quantity";

    private static final String USERS_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_FULL_NAME + " TEXT, " +
                    COLUMN_EMAIL_PHONE + " TEXT UNIQUE, " +
                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_ROLE + " TEXT, " +
                    COLUMN_AVATAR + " TEXT" +
                    ");";

    private static final String PRODUCTS_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_PRODUCTS + " (" +
                    COLUMN_PROD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PROD_NAME + " TEXT, " +
                    COLUMN_PROD_CATEGORY + " TEXT, " +
                    COLUMN_PROD_PRICE + " TEXT, " +
                    COLUMN_PROD_IMAGE + " TEXT, " +
                    COLUMN_PROD_DESC + " TEXT, " +
                    COLUMN_PROD_QUANTITY + " INTEGER" +
                    ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USERS_CREATE);
        db.execSQL(PRODUCTS_CREATE);
        addAdmin(db, "Admin PetParadise", "admin@pet.com", "admin123");
        addDefaultProducts(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_AVATAR + " TEXT");
        }
        db.execSQL(PRODUCTS_CREATE); // Đảm bảo bảng luôn được tạo nếu chưa có
        if (oldVersion < 6) {
            addDefaultProducts(db);
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

    private void addDefaultProducts(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PRODUCTS, null);
        cursor.moveToFirst();
        if (cursor.getInt(0) == 0) {
            addProductToDB(db, "Chó Poodle", "Chó", "5.000.000", "img_poodle", "Bé Poodle thông minh, dễ thương.", 10);
            addProductToDB(db, "Chó Phốc Sóc", "Chó", "4.500.000", "img_phoc_soc", "Bé Phốc Sóc trắng như bông.", 8);
            addProductToDB(db, "Chó Golden", "Chó", "6.000.000", "img_golden", "Bé Golden trung thành, năng động.", 5);
            addProductToDB(db, "Mèo Anh lông ngắn", "Mèo", "3.500.000", "img_cat_british", "Mèo Anh lông ngắn xám cực xinh.", 12);
        }
        cursor.close();
    }

    private void addProductToDB(SQLiteDatabase db, String name, String category, String price, String image, String desc, int quantity) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROD_NAME, name);
        values.put(COLUMN_PROD_CATEGORY, category);
        values.put(COLUMN_PROD_PRICE, price);
        values.put(COLUMN_PROD_IMAGE, image);
        values.put(COLUMN_PROD_DESC, desc);
        values.put(COLUMN_PROD_QUANTITY, quantity);
        db.insert(TABLE_PRODUCTS, null, values);
    }

    // --- USER METHODS ---
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

    public boolean updateUserAvatar(String email, String avatarPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AVATAR, avatarPath);
        return db.update(TABLE_USERS, values, COLUMN_EMAIL_PHONE + "=?", new String[]{email}) > 0;
    }

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

    // --- PRODUCT METHODS ---
    public long addProduct(String name, String category, String price, String image, String desc, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROD_NAME, name);
        values.put(COLUMN_PROD_CATEGORY, category);
        values.put(COLUMN_PROD_PRICE, price);
        values.put(COLUMN_PROD_IMAGE, image);
        values.put(COLUMN_PROD_DESC, desc);
        values.put(COLUMN_PROD_QUANTITY, quantity);
        return db.insert(TABLE_PRODUCTS, null, values);
    }

    public Cursor getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS, null);
    }

    public boolean deleteProduct(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PRODUCTS, COLUMN_PROD_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean updateProduct(int id, String name, String category, String price, String image, String desc, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROD_NAME, name);
        values.put(COLUMN_PROD_CATEGORY, category);
        values.put(COLUMN_PROD_PRICE, price);
        values.put(COLUMN_PROD_IMAGE, image);
        values.put(COLUMN_PROD_DESC, desc);
        values.put(COLUMN_PROD_QUANTITY, quantity);
        return db.update(TABLE_PRODUCTS, values, COLUMN_PROD_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }
}
