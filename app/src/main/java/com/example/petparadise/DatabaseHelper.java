package com.example.petparadise;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PetParadise.db";
    private static final int DATABASE_VERSION = 13; // Tăng lên 13 để thêm cột payment_method

    // Các tên bảng
    public static final String TABLE_USERS = "users";
    public static final String TABLE_PRODUCTS = "products";
    public static final String TABLE_CART = "cart";
    public static final String TABLE_ORDERS = "orders";
    public static final String TABLE_BOOKINGS = "bookings";

    // Cột bảng Products
    public static final String COLUMN_PROD_ID = "id";
    public static final String COLUMN_PROD_NAME = "name";
    public static final String COLUMN_PROD_CATEGORY = "category";
    public static final String COLUMN_PROD_PRICE = "price";
    public static final String COLUMN_PROD_IMAGE = "image";
    public static final String COLUMN_PROD_DESC = "description";
    public static final String COLUMN_PROD_QUANTITY = "quantity";

    // Cột bảng Cart
    public static final String COLUMN_CART_ID = "cart_id";
    public static final String COLUMN_CART_USER_EMAIL = "user_email";
    public static final String COLUMN_CART_PROD_ID = "product_id";
    public static final String COLUMN_CART_QTY = "quantity";

    // Cột bảng Orders
    public static final String COLUMN_ORDER_ID = "id";
    public static final String COLUMN_ORDER_USER = "user_email";
    public static final String COLUMN_ORDER_TOTAL = "total_price";
    public static final String COLUMN_ORDER_DATE = "date";
    public static final String COLUMN_ORDER_ITEMS = "items_summary";
    public static final String COLUMN_ORDER_STATUS = "status";
    public static final String COLUMN_ORDER_PAYMENT = "payment_method";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, full_name TEXT, email_phone TEXT UNIQUE, password TEXT, role TEXT, avatar TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_PRODUCTS + " (" + COLUMN_PROD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, category TEXT, price TEXT, image TEXT, description TEXT, quantity INTEGER)");
        db.execSQL("CREATE TABLE " + TABLE_CART + " (" + COLUMN_CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, user_email TEXT, product_id INTEGER, quantity INTEGER)");
        db.execSQL("CREATE TABLE " + TABLE_ORDERS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, user_email TEXT, total_price TEXT, date TEXT, items_summary TEXT, status TEXT, payment_method TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_BOOKINGS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, user_email TEXT, pet_name TEXT, service_type TEXT, date TEXT, time TEXT, status TEXT)");
        
        addAdmin(db, "Admin PetParadise", "admin@pet.com", "admin123");
        addDefaultProducts(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 11) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CART + " (" + COLUMN_CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, user_email TEXT, product_id INTEGER, quantity INTEGER)");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_ORDERS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, user_email TEXT, total_price TEXT, date TEXT, items_summary TEXT)");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_BOOKINGS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, user_email TEXT, pet_name TEXT, service_type TEXT, date TEXT, time TEXT, status TEXT)");
        }
        if (oldVersion < 12) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_ORDERS + " ADD COLUMN status TEXT DEFAULT 'Chờ xử lý'");
            } catch (Exception ignored) {}
        }
        if (oldVersion < 13) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_ORDERS + " ADD COLUMN payment_method TEXT DEFAULT 'COD'");
            } catch (Exception ignored) {}
        }
    }

    public boolean addUser(String fullName, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("full_name", fullName);
        values.put("email_phone", email);
        values.put("password", password);
        values.put("role", "user");
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    private void addAdmin(SQLiteDatabase db, String fullName, String email, String password) {
        ContentValues values = new ContentValues();
        values.put("full_name", fullName);
        values.put("email_phone", email);
        values.put("password", password);
        values.put("role", "admin");
        db.insert(TABLE_USERS, null, values);
    }

    private void addDefaultProducts(SQLiteDatabase db) {
        addProductToDB(db, "Chó Poodle", "Chó", "5.000.000", "img_poodle", "Bé Poodle thông minh, dễ thương.", 10);
        addProductToDB(db, "Chó Phốc Sóc", "Chó", "4.500.000", "img_phoc_soc", "Bé Phốc Sóc trắng như bông.", 8);
        addProductToDB(db, "Chó Golden", "Chó", "6.000.000", "img_golden", "Bé Golden trung thành, năng động.", 5);
        addProductToDB(db, "Mèo Anh lông ngắn", "Mèo", "3.500.000", "img_cat_british", "Mèo Anh lông ngắn xám cực xinh.", 12);
    }

    private void addProductToDB(SQLiteDatabase db, String name, String category, String price, String image, String desc, int quantity) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("category", category);
        values.put("price", price);
        values.put("image", image);
        values.put("description", desc);
        values.put("quantity", quantity);
        db.insert(TABLE_PRODUCTS, null, values);
    }

    // --- CÁC HÀM XỬ LÝ ---
    public String checkUserRole(String email, String pass) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT role FROM users WHERE email_phone=? AND password=?", new String[]{email, pass});
        if (c.moveToFirst()) { String r = c.getString(0); c.close(); return r; }
        c.close(); return null;
    }

    public String getUserName(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT full_name FROM users WHERE email_phone=?", new String[]{email});
        if (c.moveToFirst()) { String n = c.getString(0); c.close(); return n; }
        c.close(); return "";
    }

    public boolean updateUserName(String email, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues(); v.put("full_name", name);
        return db.update("users", v, "email_phone=?", new String[]{email}) > 0;
    }

    public boolean updateUserAvatar(String email, String path) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues(); v.put("avatar", path);
        return db.update("users", v, "email_phone=?", new String[]{email}) > 0;
    }

    public String getUserAvatar(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT avatar FROM users WHERE email_phone=?", new String[]{email});
        if (c.moveToFirst()) { String a = c.getString(0); c.close(); return a; }
        c.close(); return null;
    }

    public long addProduct(String n, String c, String p, String i, String d, int q) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("name", n); v.put("category", c); v.put("price", p); v.put("image", i); v.put("description", d); v.put("quantity", q);
        return db.insert("products", null, v);
    }

    public Cursor getAllProducts() { return getReadableDatabase().rawQuery("SELECT * FROM products", null); }
    public boolean deleteProduct(int id) { return getWritableDatabase().delete("products", "id=?", new String[]{String.valueOf(id)}) > 0; }
    public boolean updateProduct(int id, String n, String c, String p, String i, String d, int q) {
        ContentValues v = new ContentValues();
        v.put("name", n); v.put("category", c); v.put("price", p); v.put("image", i); v.put("description", d); v.put("quantity", q);
        return getWritableDatabase().update("products", v, "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean addToCart(String email, int pid, int q) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT quantity FROM cart WHERE user_email=? AND product_id=?", new String[]{email, String.valueOf(pid)});
        if (c.moveToFirst()) {
            ContentValues v = new ContentValues(); v.put("quantity", c.getInt(0) + q);
            db.update("cart", v, "user_email=? AND product_id=?", new String[]{email, String.valueOf(pid)});
        } else {
            ContentValues v = new ContentValues(); v.put("user_email", email); v.put("product_id", pid); v.put("quantity", q);
            db.insert("cart", null, v);
        }
        c.close(); return true;
    }

    public Cursor getCartItems(String email) {
        return getReadableDatabase().rawQuery("SELECT p.*, c.quantity, c.cart_id FROM products p JOIN cart c ON p.id = c.product_id WHERE c.user_email=?", new String[]{email});
    }

    public boolean updateCartQuantity(String email, int pid, int q) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues(); v.put("quantity", q);
        return db.update("cart", v, "user_email=? AND product_id=?", new String[]{email, String.valueOf(pid)}) > 0;
    }

    public boolean removeFromCart(String email, int pid) {
        return getWritableDatabase().delete("cart", "user_email=? AND product_id=?", new String[]{email, String.valueOf(pid)}) > 0;
    }

    // --- MỚI: THANH TOÁN & ĐẶT LỊCH ---
    public boolean placeOrder(String email, String total, String summary, String payment) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("user_email", email);
        v.put("total_price", total);
        v.put("date", String.valueOf(System.currentTimeMillis()));
        v.put("items_summary", summary);
        v.put("status", "Chờ xử lý");
        v.put("payment_method", payment);
        long res = db.insert(TABLE_ORDERS, null, v);
        if (res != -1) { db.delete(TABLE_CART, "user_email=?", new String[]{email}); return true; }
        return false;
    }

    public Cursor getOrderHistory(String email) {
        return getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_ORDERS + " WHERE user_email=? ORDER BY id DESC", new String[]{email});
    }

    public Cursor getAllOrders() {
        return getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_ORDERS + " ORDER BY id DESC", null);
    }

    public boolean updateOrderStatus(int id, String status) {
        ContentValues v = new ContentValues();
        v.put("status", status);
        return getWritableDatabase().update(TABLE_ORDERS, v, "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean addBooking(String email, String pet, String service, String date, String time) {
        ContentValues v = new ContentValues();
        v.put("user_email", email); v.put("pet_name", pet); v.put("service_type", service); v.put("date", date); v.put("time", time); v.put("status", "Đang chờ");
        return getWritableDatabase().insert(TABLE_BOOKINGS, null, v) != -1;
    }

    public Cursor getAllBookings() {
        return getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_BOOKINGS + " ORDER BY id DESC", null);
    }

    public boolean updateBookingStatus(int id, String status) {
        ContentValues v = new ContentValues();
        v.put("status", status);
        return getWritableDatabase().update(TABLE_BOOKINGS, v, "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    // --- BÁO CÁO THỐNG KÊ ---
    public int getTotalOrdersCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ORDERS, null);
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public double getTotalRevenue() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT total_price FROM " + TABLE_ORDERS, null);
        double total = 0;
        if (cursor.moveToFirst()) {
            do {
                String priceStr = cursor.getString(0).replace(".", "").replace(",", "").replace("đ", "").replace("VND", "").trim();
                try {
                    total += Double.parseDouble(priceStr);
                } catch (Exception ignored) {}
            } while (cursor.moveToNext());
        }
        cursor.close();
        return total;
    }

    public int getNewCustomersCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        // Giả sử "mới" là những người có role 'user'
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USERS + " WHERE role='user'", null);
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public Cursor getRecentActivity() {
        // Lấy 5 đơn hàng mới nhất và 5 lịch hẹn mới nhất (giả định gộp hoặc lấy xen kẽ)
        return getReadableDatabase().rawQuery("SELECT 'order' as type, user_email, total_price as detail, date FROM " + TABLE_ORDERS + " UNION ALL " +
                "SELECT 'booking' as type, user_email, service_type as detail, date FROM " + TABLE_BOOKINGS + " ORDER BY date DESC LIMIT 10", null);
    }
}
