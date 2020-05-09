package com.balanx.nfhelper.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;

/**
 * 公用的数据库，传入序列化对象与类型
 * 警告：Type类型严格按照@DBType 来写
 *
 * @author xiaqiliang
 * @time 2016年6月4日
 */
public class CommonDB extends NFDB {

    public CommonDB(Context context) {
        super(context);
    }

    /**
     * 插入数据与总数
     *
     * @param type
     * @param cacheData
     * @param createTime
     * @return
     */
    public synchronized long commonInsertData(int type, byte[] cacheData, int count, long createTime) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("type", type);
        cv.put("cacheData", cacheData);
        cv.put("count", count);
        cv.put("createTime", createTime);
        return db.insert("commonDatabase", null, cv);
    }

    /**
     * 根据时间删除数据
     *
     * @param time
     * @return
     */
    public synchronized int deletePushMsg(long time) {
        SQLiteDatabase db = getWritableDatabase();
        String where = "createTime=" + time;
        return db.delete(DBNames.PUSH_MSG, where, null);
    }

    /**
     * 删除所有数据
     *
     * @return
     */
    public synchronized int deleteAllPushMsg() {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(DBNames.PUSH_MSG, null, null);
    }

    /**
     * 删除单条ThemeData数据
     *
     * @param name
     * @param type
     * @return
     */
    public synchronized long deleteThemeData(String name, int type) {
        SQLiteDatabase db = getWritableDatabase();
        String where = null;
        where = DBNames.ITEM_NAME + "=?" + " and type =?";
        String[] whereValue = {name, String.valueOf(type)};
        return db.delete(DBNames.PUSH_MSG, where, whereValue);
    }

    public synchronized Cursor getPushMsg() {
        SQLiteDatabase db = getWritableDatabase();
        return db.query(DBNames.PUSH_MSG, null, "type=" + 0 + " order by createTime desc", null, null, null, null);
    }

    /**
     * 通过ID与类型获取数据
     *
     * @param type
     * @return
     */
    public synchronized Cursor checkDataExist(String packageName, int type) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = null;
        cursor = db.query(DBNames.PUSH_MSG, null, DBNames.ITEM_NAME + "=" + "'" + packageName + "'" + " and type =" + type, null, null, null, null);
        return cursor;
    }


    /**
     * 插入数据
     *
     * @param type
     * @param cacheData
     * @param createTime
     * @return
     */
    public synchronized long commonInsertData(int type, byte[] cacheData, long createTime) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("type", type);
        cv.put("cacheData", cacheData);
        cv.put("createTime", createTime);
        return db.insert("commonDatabase", null, cv);
    }

    /**
     * 插入数据（不同组里的数据）
     *
     * @param type
     * @param cacheData
     * @param createTime
     * @return
     */
    public synchronized long commonInsertData(int type, byte[] cacheData, String key, long createTime) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
        } catch (SQLiteCantOpenDatabaseException e) {
            e.printStackTrace();
        }
        if (db == null) {
            return -1;
        }
        ContentValues cv = new ContentValues();
        cv.put("type", type);
        cv.put("name", key);
        cv.put("cacheData", cacheData);
        cv.put("createTime", createTime);
        return db.insert("commonDatabase", null, cv);
    }

    /**
     * 插入数据（不同组里的数据）
     *
     * @param type
     * @param cacheData
     * @param createTime
     * @return
     */
    public synchronized long commonInsertContentData(int type, String cacheData, String key, long createTime) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
        } catch (SQLiteCantOpenDatabaseException e) {
            e.printStackTrace();
        }
        if (db == null) {
            return -1;
        }
        ContentValues cv = new ContentValues();
        cv.put("type", type);
        cv.put("name", key);
        cv.put("content", cacheData);
        cv.put("createTime", createTime);
        return db.insert("commonDatabase", null, cv);
    }

    /**
     * 插入next
     *
     * @param type
     * @param createTime
     * @return
     */
    public synchronized long commonInsertNext(int type, int next, long createTime) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("type", type);
        cv.put("next", next);
        cv.put("createTime", createTime);
        return db.insert("commonDatabase", null, cv);
    }

    /**
     * 插入数据（不同组里的数据）,带总数
     *
     * @param type
     * @param cacheData
     * @param createTime
     * @return
     */
    public synchronized long commonInsertData(int type, byte[] cacheData, int count, String key, long createTime) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("type", type);
        cv.put("name", key);
        cv.put("count", count);
        cv.put("cacheData", cacheData);
        cv.put("createTime", createTime);
        return db.insert("commonDatabase", null, cv);
    }

    /**
     * 插入数据（不同组里的数据）,带总数,带下一页查找
     *
     * @param type
     * @param cacheData
     * @param createTime
     * @return
     */
    public synchronized long commonInsertData(int type, byte[] cacheData, int count, String key, int pageIndex, long createTime) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("type", type);
        cv.put("name", key);
        cv.put("count", count);
        cv.put("cacheData", cacheData);
        cv.put("next", pageIndex);
        cv.put("createTime", createTime);
        return db.insert("commonDatabase", null, cv);
    }

    /**
     * 根据Type 和ID 修改数据，用于有下一页的
     *
     * @param type
     * @param cacheData
     * @return
     */
    public synchronized long updateData(int type, byte[] cacheData, int count, String key, long createTime) {
        SQLiteDatabase db = getWritableDatabase();
        String where = "type=? and name =?";
        String[] whereValue = {String.valueOf(key), key};
        ContentValues cv = new ContentValues();
        cv.put("cacheData", cacheData);
        return db.update("commonDatabase", cv, where, whereValue);
    }

    /**
     * 根据Type修改数据
     *
     * @param type
     * @param cacheData
     * @return
     */
    public synchronized long updateData(int type, byte[] cacheData) {
        SQLiteDatabase db = getWritableDatabase();
        String where = "type=" + type;
        String[] whereValue = {String.valueOf(type)};
        ContentValues cv = new ContentValues();
        cv.put("cacheData", cacheData);
        return db.update("commonDatabase", cv, where, whereValue);
    }

    /**
     * 获取数据
     *
     * @param type
     * @return
     */
    public synchronized Cursor commonGetData(int type) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("commonDatabase", null, "type=" + type, null, null, null, "createTime desc");
        return cursor;
    }

    /**
     * 通过ID与类型获取数据
     *
     * @param type
     * @return
     */
    public synchronized Cursor commonGetData(int type, String name) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query("commonDatabase", null, "type=" + type + " and name='" + name + "'", null, null, null, null);
        return cursor;
    }

    /**
     * 删除数据
     *
     * @return
     */
    public synchronized int commonDeleteData(String where, String[] whereArgs) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete("commonDatabase", where, whereArgs);
    }

    /**
     * 根据类型删除数据
     *
     * @param type
     * @return
     */
    public synchronized int commonDeleteData(int type) {
        SQLiteDatabase db = getWritableDatabase();
        String where = "type=" + type;
        return db.delete("commonDatabase", where, null);
    }

    /**
     * 删除所有数据
     *
     * @return
     */
    public synchronized int commonDeleteData() {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete("commonDatabase", null, null);
    }

    /**
     * 根据ID与类型删除数据
     */
    public synchronized int commonDeleteData(int type, String name) {
        SQLiteDatabase db = getWritableDatabase();
        String where = "type=" + type + " and name='" + name + "'";
        return db.delete("commonDatabase", where, null);
    }

    /**
     * 根据type获取next
     *
     * @param type
     * @return
     */
    public synchronized Cursor getNext(int type) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("commonDatabase", null, "type=" + type, null, null, null, null);
        return cursor;
    }

    /**
     * 根据type获取next
     *
     * @param type
     * @return
     */
    public synchronized Cursor getNext(int type, String id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("commonDatabase", null, "type=" + type + " and name='" + id + "'", null, null, null, null);
        return cursor;
    }

    /**
     * 当运用到一个参数进行判断时，就Next设为参数
     *
     * @param type
     * @param next
     * @return
     */
    public int updateNext(int type, int next) {
        SQLiteDatabase db = getWritableDatabase();
        String where = "type=?";
        String[] whereValue = {String.valueOf(type)};
        ContentValues cv = new ContentValues();
        cv.put("next", next);
        return db.update("commonDatabase", cv, where, whereValue);
    }

    public int updateNext(int type, String id, int next) {
        SQLiteDatabase db = getWritableDatabase();
        String where = "type=? and name =?";
        String[] whereValue = {String.valueOf(type), id};
        ContentValues cv = new ContentValues();
        cv.put("next", next);
        return db.update("commonDatabase", cv, where, whereValue);
    }

    /**
     * 修改count
     *
     * @param type
     * @return
     */
    public int updateCount(int type, int count, String name) {
        SQLiteDatabase db = getWritableDatabase();
        String where = "type=?" + " and name=?";
        String[] whereValue = {String.valueOf(type), "'%" + name + "%'"};
        ContentValues cv = new ContentValues();
        cv.put("count", count);
        return db.update("commonDatabase", cv, where, whereValue);
    }

    /**
     * 根据Type修改数据
     *
     * @param type
     * @param cacheData
     * @return
     */
    public synchronized long commonUpdateData(int type, byte[] cacheData) {
        SQLiteDatabase db = getWritableDatabase();
        String where = "type=?";
        String[] whereValue = {String.valueOf(type)};
        ContentValues cv = new ContentValues();
        cv.put("cacheData", cacheData);
        return db.update("commonDatabase", cv, where, whereValue);
    }

    /**
     * 根据Type修改数据和ID
     *
     * @param type
     * @param cacheData
     * @return
     */
    public synchronized long commonUpdateData(int type, String name, byte[] cacheData) {
        SQLiteDatabase db = getWritableDatabase();
        String where = "type=?" + " and name=?";
        String[] whereValue = {String.valueOf(type), name};
        ContentValues cv = new ContentValues();
        cv.put("cacheData", cacheData);
        return db.update("commonDatabase", cv, where, whereValue);
    }

}
