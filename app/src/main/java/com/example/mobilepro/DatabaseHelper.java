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
    private static final int DATABASE_VERSION = 3;

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
    public static final String COLUMN_QUANTITY = "quantity";

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
                    COLUMN_IMAGE_RES_ID + " INTEGER, " +
                    COLUMN_QUANTITY + " INTEGER DEFAULT 1" +
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
        // Nâng cấp v2 -> v3: thêm cột quantity cho bảng cart cũ
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_CART +
                    " ADD COLUMN " + COLUMN_QUANTITY + " INTEGER DEFAULT 1");
        }
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

    // Thêm 1 sản phẩm vào giỏ (mặc định số lượng 1)
    public void addToCart(Product product) {
        addToCart(product, 1);
    }

    // Thêm sản phẩm với số lượng tùy chọn (nếu đã có thì cộng dồn)
    public void addToCart(Product product, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + CART_ID + ", " + COLUMN_QUANTITY +
                        " FROM " + TABLE_CART + " WHERE " + COLUMN_NAME + " = ?",
                new String[]{product.getName()});

        if (cursor.moveToFirst()) {
            int cartId = cursor.getInt(0);
            int newQty = cursor.getInt(1) + quantity;
            ContentValues values = new ContentValues();
            values.put(COLUMN_QUANTITY, newQty);
            db.update(TABLE_CART, values, CART_ID + " = ?",
                    new String[]{String.valueOf(cartId)});
        } else {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, product.getName());
            values.put(COLUMN_PRICE, product.getPrice());
            values.put(COLUMN_DESCRIPTION, product.getDescription());
            values.put(COLUMN_IMAGE_RES_ID, product.getImageResId());
            values.put(COLUMN_QUANTITY, quantity);
            db.insert(TABLE_CART, null, values);
        }
        cursor.close();
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
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_RES_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // Tăng số lượng 1 sản phẩm trong giỏ
    public void increaseQuantity(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_CART +
                        " SET " + COLUMN_QUANTITY + " = " + COLUMN_QUANTITY + " + 1" +
                        " WHERE " + COLUMN_NAME + " = ?",
                new Object[]{name});
        db.close();
    }

    // Giảm số lượng (tối thiểu 1)
    public void decreaseQuantity(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_CART +
                        " SET " + COLUMN_QUANTITY + " = " + COLUMN_QUANTITY + " - 1" +
                        " WHERE " + COLUMN_NAME + " = ? AND " + COLUMN_QUANTITY + " > 1",
                new Object[]{name});
        db.close();
    }

    // Xóa 1 sản phẩm khỏi giỏ
    public void removeFromCart(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, COLUMN_NAME + " = ?", new String[]{name});
        db.close();
    }

    // Xóa sạch giỏ hàng
    public void clearCart() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, null, null);
        db.close();
    }

    // Tính tổng tiền giỏ hàng
    public int getCartTotal() {
        int total = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_PRICE + ", " + COLUMN_QUANTITY +
                        " FROM " + TABLE_CART, null);
        while (cursor.moveToNext()) {
            total += parsePrice(cursor.getString(0)) * cursor.getInt(1);
        }
        cursor.close();
        db.close();
        return total;
    }

    // Chuyển "30.000đ" thành 30000
    private int parsePrice(String price) {
        String clean = price.replace("đ", "").replace(".", "").trim();
        try {
            return Integer.parseInt(clean);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}