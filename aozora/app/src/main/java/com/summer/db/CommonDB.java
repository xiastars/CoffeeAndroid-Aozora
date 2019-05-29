package com.summer.db;

import com.summer.app.wuteai.entity.UrlInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 公用的数据库，传入序列化对象与类型 警告：Type类型严格按照@CommonType 来写
 * 
 * @编者 夏起亮
 * 
 */
public class CommonDB extends LianAiDB {

	public CommonDB(Context context) {
		super(context);
	}

	/**
	 * 插入数据与总数
	 * @param type
	 * @param cacheData
	 * @param createTime
	 * @return
	 */
	public synchronized long commonInsertData(int type,byte[] cacheData,int count,long createTime){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("type",type);
		cv.put("cacheData",cacheData);
		cv.put("count",count);
		cv.put("createTime",createTime);
		return db.insert("commonDatabase",null,cv);		
	}
	
	/**
	 * 插入最近玩的一个APP
	 * @param packageName
	 * @param className
	 * @param title
	 * @param url
	 * @param icon
	 * @param createTime
	 * @return
	 */
	public synchronized long insertShortcut(UrlInfo info){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("type",1);
		cv.put("packagename",info.getName());
		cv.put("classname",info.getUrl());
		cv.put("icon", info.getStringLogo());
		cv.put("createTime",System.currentTimeMillis());
		return db.insert(DBNames.DOWNLOAD_BOOK,null,cv);		
	}
	
	public synchronized long updateShortcut(UrlInfo info) {
		SQLiteDatabase db = getWritableDatabase();
		String where = null;
		ContentValues cv = new ContentValues();
		cv.put("createTime",System.currentTimeMillis());
		where = "classname=?"+" and type =?";
	    String[] whereValue = {info.getName(),String.valueOf(1)};
	 	return db.update(DBNames.DOWNLOAD_BOOK, cv, where, whereValue);
	}
	
	/**
	 * 根据类型删除数据
	 * @param type
	 * @return
	 */
	public synchronized int deleteShortcutByType(int type ){
		SQLiteDatabase db = getWritableDatabase();
		String where = "type="+type;
		return db.delete(DBNames.DOWNLOAD_BOOK,where,null);
	}
	
	
	public synchronized long deleteShortcut(String packagename) {
		SQLiteDatabase db = getWritableDatabase();
		String where = null;
		 where = "classname=?"+" and type =?";
	     String[] whereValue = {packagename,String.valueOf(1)};
	     return  db.delete(DBNames.DOWNLOAD_BOOK, where, whereValue);
	}
	
	public synchronized Cursor getShortcut(int type){		
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.query(DBNames.DOWNLOAD_BOOK,null,"type="+type + " order by createTime desc",null,null,null,null);
		return cursor;		
	}
	
	/**
	 * 通过ID与类型获取数据
	 * @param type
	 * @return
	 */
	public synchronized Cursor checkDataExist(String packageName){		
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = null;
		cursor = db.query(DBNames.DOWNLOAD_BOOK,null,"classname="+"'" +packageName+ "'"+" and type ="+1,null,null,null,null);
		return cursor;		
	}
	
	
	/**
	 * 插入数据
	 * @param type
	 * @param cacheData
	 * @param createTime
	 * @return
	 */
	public synchronized long commonInsertData(int type,byte[] cacheData,long createTime){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("type",type);
		cv.put("cacheData",cacheData);
		cv.put("createTime",createTime);
		return db.insert("commonDatabase",null,cv);		
	}
	
	/**
	 * 插入数据（不同组里的数据）
	 * @param type
	 * @param cacheData
	 * @param createTime
	 * @return
	 */
	public synchronized long commonInsertData(int type,byte[] cacheData,String key,long createTime){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("type",type);
		cv.put("key",key);
		cv.put("cacheData",cacheData);
		cv.put("createTime",createTime);
		return db.insert("commonDatabase",null,cv);		
	}	
	
	/**
	 * 插入next
	 * @param type
	 * @param cacheData
	 * @param createTime
	 * @return
	 */
	public synchronized long commonInsertNext(int type,int next,long createTime){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("type",type);
		cv.put("next",next);
		cv.put("createTime",createTime);
		return db.insert("commonDatabase",null,cv);		
	}	

	/**
	 * 插入数据（不同组里的数据）,带总数
	 * @param type
	 * @param cacheData
	 * @param createTime
	 * @return
	 */
	public synchronized long commonInsertData(int type,byte[] cacheData,int count,String key,long createTime){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("type",type);
		cv.put("key",key);
		cv.put("count",count);
		cv.put("cacheData",cacheData);
		cv.put("createTime",createTime);
		return db.insert("commonDatabase",null,cv);		
	}
	
	/**
	 * 插入数据（不同组里的数据）,带总数,带下一页查找
	 * @param type
	 * @param cacheData
	 * @param createTime
	 * @return
	 */
	public synchronized long commonInsertData(int type,byte[] cacheData,int count,String key,int pageIndex,long createTime){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("type",type);
		cv.put("key",key);
		cv.put("count",count);
		cv.put("cacheData",cacheData);
		cv.put("next",pageIndex);
		cv.put("createTime",createTime);
		return db.insert("commonDatabase",null,cv);		
	}
	
	/**
	 * 根据Type 和ID 修改数据，用于有下一页的
	 * @param groupId
	 * @param type
	 * @param cacheData
	 * @return
	 */
	public synchronized long updateData(int type,byte[] cacheData,int count,String key,long createTime) {
		SQLiteDatabase db = getWritableDatabase();
		String where = "type=? and key like ?";
		String[] whereValue = { String.valueOf(key), "'%"+ key + "%'"};
		ContentValues cv = new ContentValues();
		cv.put("cacheData", cacheData);
		return db.update("commonDatabase", cv, where, whereValue);
	}
	
	/**
	 * 根据Type修改数据
	 * @param groupId
	 * @param type
	 * @param cacheData
	 * @return
	 */
	public synchronized long updateData(int type,byte[] cacheData) {
		SQLiteDatabase db = getWritableDatabase();
		String where = "type="+type;
		String[] whereValue = { String.valueOf(type)};
		ContentValues cv = new ContentValues();
		cv.put("cacheData", cacheData);
		return db.update("commonDatabase", cv, where, whereValue);
	}
	
	/**
	 * 获取数据
	 * @param type
	 * @return
	 */
	public synchronized Cursor commonGetData(int type){		
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query("commonDatabase",null,"type="+type,null,null,null,"createTime desc");
		return cursor;		
	}
	
	/**
	 * 通过ID与类型获取数据
	 * @param type
	 * @return
	 */
	public synchronized Cursor commonGetData(int type, String key){		
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.query("commonDatabase",null,"type="+type+" and key='"+ key + "'",null,null,null,null);
		return cursor;		
	}
	
	/**
	 * 根据类型删除数据
	 * @param type
	 * @return
	 */
	public synchronized int commonDeleteData(int type ){
		SQLiteDatabase db = getWritableDatabase();
		String where = "type="+type;
		return db.delete("commonDatabase",where,null);
	}
	
	/**
	 * 删除所有数据
	 * @param type
	 * @return
	 */
	public synchronized int commonDeleteData(){
		SQLiteDatabase db = getWritableDatabase();
		return db.delete("commonDatabase",null,null);
	}
	
	/**
	 * 根据ID与类型删除数据
	 */
	public synchronized int commonDeleteData(int type,String key ){
		SQLiteDatabase db = getWritableDatabase();
		String where = "type="+type+" and key='"+key + "'";
		return db.delete("commonDatabase",where,null);
	}
	
	/**
	 * 根据type获取next
	 * @param type
	 * @return
	 */
	public synchronized Cursor getNext(int type){
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query("commonDatabase",null,"type="+type,null,null,null,null);
		return cursor;		
	}
	
    /**
     * 当运用到一个参数进行判断时，就Next设为参数
     * @param type
     * @param next
     * @return
     */
	public int updateNext(int type, int next) {
		SQLiteDatabase db = getWritableDatabase();
		String where = "type=?";
		String[] whereValue = { String.valueOf(type) };
		ContentValues cv = new ContentValues();
		cv.put("next", next);
		return db.update("commonDatabase", cv, where, whereValue);
	}
	
	/**
     * 修改count
     * @param type
     * @param next
     * @return
     */
	public int updateCount(int type, int count,String key) {
		SQLiteDatabase db = getWritableDatabase();
		String where = "type=?"+" and key like ?";
		String[] whereValue = { String.valueOf(type), "'%" + key + "%'"};
		ContentValues cv = new ContentValues();
		cv.put("count", count);
		return db.update("commonDatabase", cv, where, whereValue);
	}
	
	/**
	 * 根据Type修改数据
	 * @param groupId
	 * @param type
	 * @param cacheData
	 * @return
	 */
	public synchronized long commonUpdateData(int type,byte[] cacheData) {
		SQLiteDatabase db = getWritableDatabase();
		String where = "type=?";
		String[] whereValue = { String.valueOf(type)};
		ContentValues cv = new ContentValues();
		cv.put("cacheData", cacheData);
		return db.update("commonDatabase", cv, where, whereValue);
	}
	
	/**
	 * 根据Type修改数据和ID
	 * @param groupId
	 * @param type
	 * @param cacheData
	 * @return
	 */
	public synchronized long commonUpdateData(int type,String key,byte[] cacheData) {
		SQLiteDatabase db = getWritableDatabase();
		String where = "type=?"+" and key like ?";
		String[] whereValue = { String.valueOf(type), "'%" + key + "%'"};
		ContentValues cv = new ContentValues();
		cv.put("cacheData", cacheData);
		return db.update("commonDatabase", cv, where, whereValue);
	}
}
