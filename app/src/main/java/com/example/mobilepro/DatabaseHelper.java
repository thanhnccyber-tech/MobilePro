package com.example.mobilepro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MobilePro.db";
    private static final int DATABASE_VERSION = 2;

    // Bảng sản phẩm
    public static final String TABLE_PRODUCTS = "products";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_IMAGE_RES_ID = "image_res_id";

    // Bảng giỏ hàng
    public static final String TABLE_CART = "cart";
    public static final String CART_ID = "cart_id";

    // Câu lệnh tạo bảng sản phẩm
    private static final String CREATE_TABLE_PRODUCTS =
            "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_PRICE + " TEXT, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_IMAGE_RES_ID + " INTEGER" +
                    ")";

    // Câu lệnh tạo bảng giỏ hàng
    private static final String CREATE_TABLE_CART =
            "CREATE TABLE " + TABLE_CART + " (" +
                    CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_PRICE + " TEXT, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_IMAGE_RES_ID + " INTEGER" +
                    ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PRODUCTS);
        db.execSQL(CREATE_TABLE_CART);
        seedData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        onCreate(db);
    }

    // ============ DỮ LIỆU MẪU ============

    private void seedData(SQLiteDatabase db) {
        insertProduct(db, "Apple",  "30.000đ",  "Táo đỏ nhập khẩu, giòn ngọt", R.drawable.apple);
        insertProduct(db, "Banana", "15.000đ",  "Chuối chín tự nhiên",          R.drawable.apple);
        insertProduct(db, "Cherry", "120.000đ", "Cherry Mỹ, quả to",            R.drawable.apple);
        insertProduct(db, "Date",   "90.000đ",  "Quả chà là khô",               R.drawable.apple);
        insertProduct(db, "Grapes", "60.000đ",  "Nho xanh không hạt",           R.drawable.apple);
    }

    private void insertProduct(SQLiteDatabase db, String name, String price,
                               String description, int imageResId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_IMAGE_RES_ID, imageResId);
        db.insert(TABLE_PRODUCTS, null, values);
    }

    // ============ SẢN PHẨM ============

    // Thêm một sản phẩm vào bảng products
    public void addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        insertProduct(db, product.getName(), product.getPrice(),
                product.getDescription(), product.getImageResId());
        db.close();
    }

    // Lấy toàn bộ sản phẩm
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS, null);

        if (cursor.moveToFirst()) {
            do {
                list.add(new Product(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_RES_ID))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // ============ GIỎ HÀNG ============

    // Thêm sản phẩm vào giỏ
    public void addToCart(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, product.getName());
        values.put(COLUMN_PRICE, product.getPrice());
        values.put(COLUMN_DESCRIPTION, product.getDescription());
        values.put(COLUMN_IMAGE_RES_ID, product.getImageResId());
        db.insert(TABLE_CART, null, values);
        db.close();
    }

    // Lấy toàn bộ sản phẩm trong giỏ
    public List<Product> getCartProducts() {
        List<Product> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CART, null);

        if (cursor.moveToFirst()) {
            do {
                list.add(new Product(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_RES_ID))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // Xóa sạch giỏ hàng
    public void clearCart() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, null, null);
        db.close();
    }
}