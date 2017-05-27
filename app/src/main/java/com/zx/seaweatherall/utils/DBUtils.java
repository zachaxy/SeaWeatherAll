package com.zx.seaweatherall.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zhangxin on 2017/5/26 0026.
 * <p>
 * Description : 数据库相关的工具类；
 */

public class DBUtils {
    private static final String CREATE_TABLE_IF_NOT_EXISTS = "create table if not exists %s " +
            "(id integer primary key autoincrement," +
            "time text ," +
            "img interger," +
            "is_read integer)";

    //用来接受左边消息栏的内容
    private static final String CREATE_RECENT_MSGLIST = "create table recentMSG( "
            + "id integer primary key autoincrement,"
            + "img interger,"
            + "time text,"
            + "msg text )";

    private static final String CREATE_MSG = "create table msg( "
            + "id integer primary key autoincrement,"
            + "time text,"
            + "phoneNo text,"
            + "content text )";

    private static final String CREATE_BMSG = "create table bmsg( "
            + "id integer primary key autoincrement,"
            + "time text,"
            + "content text )";

    // TODO: 2017/5/26 0026 还不知道能不能插入浮点数；
    private static final String CREATE_TYPHOON = "create table typhoon( "
            + "id integer primary key autoincrement,"
            + "typhoonNo interger,"
            + "typhoonName text"
            + "typhoonContent text"
            + "locateX interger,"
            + "locateY interger )";

    private static final int MAX_TABLE_SIZE = 200;
    private static DBUtils sDBUtis;
    private SQLiteDatabase mSQLiteDatabase;

    private DBUtils(Context context) {
        mSQLiteDatabase = new DBHelper(context, "seaWeather.db").getWritableDatabase();
    }

    //提供统一的对外接口；
    public static DBUtils getDB(Context context) {
        if (sDBUtis == null) {
            synchronized (DBUtils.class) {
                if (sDBUtis == null) {
                    sDBUtis = new DBUtils(context);
                }
            }
        }
        return sDBUtis;
    }

    // TODO: 2017/5/26 0026  
    public void insertData(){
        
    }

    /***
     * @Deprecated
     * 插入数据，表明是否已经读取过，只适用于最近消息列表；
     * @param table 数据库表名；
     * @param key   id；
     * @param value 插入的值；
     */
    public void insertHasRead(String table, String key, int value) {
        //先看一下当前表中一共有多少项，如果超过200条，那么删除最前面的一条；
        Cursor cursor = mSQLiteDatabase.query(table, null, null, null, null, null, "id asc");
        if (cursor.getCount() > MAX_TABLE_SIZE && cursor.moveToNext()) {
            mSQLiteDatabase.delete(table, "id=?",
                    new String[]{String.valueOf(cursor.getInt(cursor.getColumnIndex("id")))});
        }
        cursor.close();
        ContentValues contentValues = new ContentValues();
        contentValues.put("key", key);
        contentValues.put("is_read", value);
        mSQLiteDatabase.insertWithOnConflict(table, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    // 不用数据库的形式来判断是否已经读取过了，效率低；
    @Deprecated
    public boolean isRead(String table, String key, int value) {
        boolean isRead = false;
        Cursor cursor = mSQLiteDatabase.query(table, null, "key=?", new String[]{key}, null, null, null);
        if (cursor.moveToNext() && (cursor.getInt(cursor.getColumnIndex("is_read")) == value)) {
            isRead = true;
        }
        cursor.close();
        return isRead;
    }

    public class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name) {
            super(context, name, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            /*db.execSQL(String.format(CREATE_TABLE_IF_NOT_EXISTS, "recent"));
            db.execSQL(String.format(CREATE_TABLE_IF_NOT_EXISTS, "msg"));
            db.execSQL(String.format(CREATE_TABLE_IF_NOT_EXISTS, "business_msg"));
            db.execSQL(String.format(CREATE_TABLE_IF_NOT_EXISTS, "weather"));*/
            // dbFragment用来展示数据的表；
            db.execSQL(CREATE_MSG);
            db.execSQL(CREATE_BMSG);
            db.execSQL(CREATE_TYPHOON);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //数据库更新方案，暂时不做；
        }
    }
}
