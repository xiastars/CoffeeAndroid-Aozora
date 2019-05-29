package com.summer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LianAiDB extends SQLiteOpenHelper {

	private SQLiteDatabase db = null;
	/** 数据库名称 */
	private final static String DB = "livedoor";
	/** 版本号 */
	private final static int VERSIONCODE = 1;

	public LianAiDB(Context context) {
		super(context, DB, null, VERSIONCODE);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		/** 公用数据库 */
		String commonDatabase = "create table commonDatabase(id integer primary key autoincrement,"
				+ "cacheData blob,"
				+ "type integer,"
				+ "count integer,"
				+ "stringtype TEXT,"
				+ "listId integer,"
				+ "next integer,"
				+ "key TEXT,"
		        + "createTime integer)";
		db.execSQL(commonDatabase);
		
		/** 桌面Shortcut数据库 */
		String shortcutDatabase = "create table "+DBNames.DOWNLOAD_BOOK+"(id integer primary key autoincrement,"
				+ "packagename text,"
				+ "classname text,"
				+ "type integer,"
				+ "url text,"
				+ "icon text,"
				+ "preicon blob,"
				+ "flags integer,"
				+ "createTime integer,"
				+ "title text)";
		db.execSQL(shortcutDatabase);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 当数据库被改变时，将原先的表删除，然后建立新表
		String sql = "drop table if exists commonDatabase";
		db.execSQL(sql);
		sql = "drop table if exists "+DBNames.DOWNLOAD_BOOK;
		db.execSQL(sql);
		onCreate(db);
	}
	
	// 开启读事务处理
	public void beginTransaction() {
		if(db == null)
			return;
		db = getWritableDatabase();
		db.beginTransaction();
	}

	// 停止事务处理
	public void endTransaction() {
		if(db == null)
			return;
		db.setTransactionSuccessful();
		db.endTransaction();
	}
}
