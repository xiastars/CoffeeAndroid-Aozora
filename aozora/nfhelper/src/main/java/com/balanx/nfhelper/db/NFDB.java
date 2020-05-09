package com.balanx.nfhelper.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 应用商店数据库
 * @author xiaqiliang
 * @time 2016年6月4日
 */
public class NFDB extends SQLiteOpenHelper {

	private SQLiteDatabase db = null;
	/** 数据库名称 */
	private static String DB = "huoxingquandb";
	/** 版本号 */
	private final static int VERSIONCODE = 5;

	public NFDB(Context context) {
		super(context, DB, null, VERSIONCODE);
	}

	/**
	 * 需要在引用的Application里设置名称
	 * @param name
	 */
	public static void initDBName(String name){
		DB = name;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		/** 公用数据库 */
		String commonDatabase = "create table commonDatabase(id integer primary key autoincrement,"
				+ "cacheData mediumblob,"
				+ "type integer,"
				+ "count integer,"
				+ "content text,"
				+ "name text,"
				+ "next integer,"
				+ "createTime integer)";
		db.execSQL(commonDatabase);

		/** 资源数据库 */
		String shortcutDatabase = "create table "+DBNames.PUSH_MSG+"(id integer primary key autoincrement,"
				+ DBNames.DOWNLOAD_URL+" text,"
				+ DBNames.ITEM_NAME+" text,"
				+ DBNames.ITEM_EXTRA+" text,"
				+ DBNames.DOWNLOAD_STATUS+" integer,"
				+ DBNames.TYPE_ID+" integer,"
				+ DBNames.ITEM_CONTENT+" text,"
				+ "type integer,"
				+ "createTime integer)";
		db.execSQL(shortcutDatabase);

		/** 搜索数据库 */
		String searchDatabase = "create table "+DBNames.NF_SEARCH_DATA+"(id integer primary key autoincrement,"
				+ DBNames.ITEM_NAME+" text,"
				+ DBNames.ITEM_EXTRA+" text,"
				+ DBNames.DOWNLOAD_STATUS+" integer,"
				+ DBNames.TYPE_ID+" integer,"
				+ DBNames.USER_ID+" text,"
				+ DBNames.ITEM_CONTENT+" text,"
				+ "type integer,"
				+ "createTime integer)";
		db.execSQL(searchDatabase);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "drop table if exists commonDatabase";
		db.execSQL(sql);
		sql = "drop table if exists "+DBNames.PUSH_MSG;
		db.execSQL(sql);
		sql = "drop table if exists "+DBNames.NF_SEARCH_DATA;
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
