package com.summer.db;

import java.util.ArrayList;
import java.util.List;

import com.summer.app.wuteai.entity.UrlInfo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;

/**
 * 公用的数据库，传入序列化对象与类型 警告：Type类型严格按照@CommonType 来写
 * 
 * @编者 夏起亮
 * 
 */
public class CommonService {

	private Cursor mCursor ;
	private CommonDB commonDB ;

	public CommonService(Context context) {
		super();
		commonDB = new CommonDB(context);
	}
	
	/**
	 * 插入最近玩的app
	 * @param info
	 * @param bitmap
	 */
	public synchronized void insertShortcut(UrlInfo info){
		if(info == null)return;
		try {
			commonDB.insertShortcut(info);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDB();
		}
	}
	
	/**
	 * 插入最近玩的app
	 * @param info
	 * @param bitmap
	 */
	public synchronized void insertShortcut(List<UrlInfo> infos,int type){
		for(UrlInfo info : infos){
			insertShortcut(info);
		}
	}
	
	/**
	 * 根据ID删除数据
	 */
	public synchronized void deleteShortcutByType(final int type){
		try {
			commonDB.deleteShortcutByType(type);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDB();
		}
	}
	
	/**
	 * 获取最近玩过的APP十条数据
	 * @return
	 */
	public List<UrlInfo> getRecentShortcuts(int count,int type){
		List<UrlInfo> activitys = new ArrayList<UrlInfo>();
		try {
			mCursor = commonDB.getShortcut(type);
			int index = 0;
			while(mCursor.moveToNext()){
				index ++;
				UrlInfo info = new UrlInfo();
				String packageName = mCursor.getString(mCursor.getColumnIndex("packagename"));
				String url = mCursor.getString(mCursor.getColumnIndex("classname"));
				String logo = mCursor.getString(mCursor.getColumnIndex("icon"));
			    info.setName(packageName);
			    info.setStringLogo(logo);
			    info.setUrl(url);
				activitys.add(info);
				if(index  == count){
					break;
				}
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		}catch(OutOfMemoryError e){
			
		}finally{
			closeDB();
		}
		return activitys != null ? activitys : null;		
	}
	
	/**
	 * 获取最近玩过的APP十条数据
	 * @return
	 */
	public boolean isShortcutExist(UrlInfo info){
		try {
			mCursor = commonDB.checkDataExist(info.getUrl());
			Log.i("mCursor...get", mCursor.getCount()+"----");
			if(mCursor.getCount() == 0){
				insertShortcut(info);
			}else{
				updateShortcut(info);
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
		return false;		
	}
	
	/**
	 * 更新某条shortcut数据
	 * @param info
	 */
	public void updateShortcut(UrlInfo info) {
		try {
			commonDB.updateShortcut(info);
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
	}
	
	/**
	 * 删除某条shortcut数据
	 * @param info
	 */
	public void deleteShortcut(UrlInfo info) {
		try {
			commonDB.deleteShortcut(info.getName());
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
	}
	
	/**
	 * 插入数据
	 * @param type 
	 * @param cacheData
	 * @param createTime
	 */
	public synchronized void commonInsertData(final int type,final byte[] cacheData,final long createTime){
		try {
			commonDB.commonInsertData(type, cacheData, createTime);
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
	}
	
	/**
	 * 插入对象
	 * @param type
	 * @param cacheData
	 */
	public synchronized void insert(final int type,final Object cacheData){
		try {
			commonDB.commonDeleteData(type);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDB();
			commonInsertData(type,SerializeUtil.serializeObject(cacheData),System.currentTimeMillis());
		}
	}
	
	/**
	 * 插入数据,带总数
	 * @param type 
	 * @param cacheData
	 * @param createTime
	 */
	public synchronized void commonInsertData(final int type,final byte[] cacheData,final int count,final long createTime){
		try {
			commonDB.commonInsertData(type,cacheData,count,createTime);
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
	}
	
	/**
	 * 根据ID插入数据,带总数，先删除之前的数据
	 */
	public synchronized void commonInsertSafeData(final int type,final byte[] cacheData,final int count,final long createTime){
		try {
			commonDB.commonDeleteData(type);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDB();
			commonInsertData(type,cacheData,count,createTime);
		}
	}
	
	/**
	 * 插入next
	 * @param type 
	 * @param cacheData
	 * @param createTime
	 */
	public synchronized void commonInsertNext(final int type,final int next,final long createTime){
		try {
			commonDB.commonInsertNext(type,next,createTime);
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
	}
	
	/**
	 * 根据ID插入数据
	 * @param type 
	 * @param cacheData
	 * @param createTime
	 */
	private synchronized void commonInsertData(final int type,final byte[] cacheData,final String id ,final long createTime){
		try {
			commonDB.commonInsertData(type,cacheData,id,createTime);
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
	}
	
	/**
	 * 根据ID插入数据,带总数，先删除之前的数据
	 */
	public synchronized void commonInsertSafeData(final int type,final byte[] cacheData,final String id,final long createTime){
		try {
			commonDB.commonDeleteData(type,id);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDB();
			commonInsertData(type,cacheData,id,createTime);
		}
	}
	
	public synchronized void insert(final int type,final String id,final Object cacheData){
		try {
			commonDB.commonDeleteData(type,id);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDB();
			commonInsertData(type,SerializeUtil.serializeObject(cacheData),id,0);
		}
	}
	
	public synchronized void insert(final int type,final long id,final Object cacheData){
		try {
			commonDB.commonDeleteData(type,id+"");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDB();
			commonInsertData(type,SerializeUtil.serializeObject(cacheData),id+"",0);
		}
	}
	
	/**
	 * 根据ID插入数据,带总数
	 * @param type 
	 * @param cacheData
	 * @param createTime
	 */
	private synchronized void commonInsertData(final int type,final byte[] cacheData,final int count,final String id ,final long createTime){
		try {
			commonDB.commonInsertData(type, cacheData, count, id, createTime);
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
	}
	
	/**
	 * 根据ID插入数据,带总数，先删除之前的数据
	 */
	public synchronized void commonInsertSafeData(final int type,final byte[] cacheData,final int count,final String id,final long createTime){
		try {
			commonDB.commonDeleteData(type,id);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDB();
			commonInsertData(type,cacheData,count,id,createTime);
		}
	}
	
	/**
	 * 根据ID插入数据,带总数,带下一页参数
	 * @param type 
	 * @param cacheData
	 * @param createTime
	 */
	public synchronized void commonInsertData(final int type,final byte[] cacheData,final int count,final String id ,final int pageIndex,final long createTime){
		try {
			commonDB.commonInsertData(type,cacheData,count,id,pageIndex,createTime);
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
	}
	
	public synchronized void insert(final int type,int count,final Object cacheData){
		try {
			commonDB.commonDeleteData(type);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDB();
			commonInsertData(type,SerializeUtil.serializeObject(cacheData),count,0);
		}
	}
	
	public synchronized void insert(final int type,final String id,int count,int pageIndex,final Object cacheData){
		try {
			commonDB.commonDeleteData(type,id);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDB();
			commonInsertData(type,SerializeUtil.serializeObject(cacheData),count,id,pageIndex,0);
		}
	}
	
	/**
	 * 根据ID插入数据,带总数,带下一页参数，先删除之前的数据
	 */
	public synchronized void commonInsertSafeData(final int type,final byte[] cacheData,final int count,final String id,final int pageIndex,final long createTime){
		try {
			commonDB.commonDeleteData(type,id);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDB();
			commonInsertData(type,cacheData,count,id,pageIndex,createTime);
		}
	}
	
	/**
	 * 根据Type 和ID 修改数据，用于有下一页的
	 * @param type
	 * @param cacheData
	 * @param count
	 * @param id
	 * @param createTime
	 */
	public synchronized void commonUpdateData(int type,byte[] cacheData,int count,String id,long createTime){
		try {
			commonDB.updateData(type,cacheData,count,id,createTime);
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
	}
	
	/**
	 * 获取数据
	 * @param 
	 * @return
	 */
	public List<?> getListData(int type){
		List<?> activitys = null;
		try {
			mCursor = commonDB.commonGetData(type);
			if(mCursor != null && mCursor.moveToNext()){
				activitys = (List<?> ) SerializeUtil.deserializeObject(mCursor.getBlob(
						mCursor.getColumnIndex("cacheData")));
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
		return activitys != null ? activitys : null;		
	}
		
	/**
	 * 获取对象数据
	 * @param 
	 * @return
	 */
	public Object getObjectData(int type){
		Object activitys = null;
		try {
			mCursor = commonDB.commonGetData(type);
			if(mCursor != null && mCursor.moveToNext()){
				activitys = SerializeUtil.deserializeObject(mCursor.getBlob(
						mCursor.getColumnIndex("cacheData")));
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
		return activitys != null ? activitys : null;		
	}
	
	/**
	 * 根据ID获取数据
	 * @param 
	 * @return
	 */
	public List<?> getListData(int type,String id){
		List<?> activitys = null;
		try {
			mCursor = commonDB.commonGetData(type,id);
			if(mCursor.moveToNext()){
				activitys = (List<?> ) SerializeUtil.deserializeObject(mCursor.getBlob(
						mCursor.getColumnIndex("cacheData")));
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
		return activitys != null ? activitys : null;		
	}
	
	/**
	 * 根据ID获取数据
	 * @param 
	 * @return
	 */
	public List<?> getListData(int type,long id){
		List<?> activitys = null;
		try {
			mCursor = commonDB.commonGetData(type,id+"");
			if(mCursor.moveToNext()){
				activitys = (List<?> ) SerializeUtil.deserializeObject(mCursor.getBlob(
						mCursor.getColumnIndex("cacheData")));
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
		return activitys != null ? activitys : null;		
	}
	
	/**
	 * 根据ID获取上对象的缓冲 
	 * @param 
	 * @return
	 */
	public Object getObjectData(int type, String id){
		Object activitys = null;
		try {
			mCursor = commonDB.commonGetData(type,id);
			if(mCursor.moveToNext()){
				activitys =  SerializeUtil.deserializeObject(mCursor.getBlob(
						mCursor.getColumnIndex("cacheData")));
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
		return activitys != null ? activitys : null;		
	}
	
	/**
	 * 根据ID获取上对象的缓冲 
	 * @param 
	 * @return
	 */
	public Object getObjectData(int type, long id){
		Object activitys = null;
		try {
			mCursor = commonDB.commonGetData(type,id + "");
			if(mCursor.moveToNext()){
				activitys =  SerializeUtil.deserializeObject(mCursor.getBlob(
						mCursor.getColumnIndex("cacheData")));
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
		return activitys != null ? activitys : null;		
	}
	
	/**
	 * 根据Url获取上对象的缓冲 
	 * @param 
	 * @return
	 */
	public boolean isHistoryItemExist(int type, String keywords){
		try {
			mCursor = commonDB.commonGetData(type, keywords);
			if(mCursor != null && mCursor.getCount() > 0)
				return true;
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
		return false;		
	}
	
	/**
	 * 根据Url获取上对象的缓冲 
	 * @param 
	 * @return
	 */
	public boolean isDownloadItemExist(int type, String url){
		try {
			mCursor = commonDB.commonGetData(type, url);
			if(mCursor != null && mCursor.getCount() > 0)
				return true;
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
		return false;		
	}
	
	/**
	 * 根据ID获取集合数据
	 * @param 
	 * @return
	 */
	public List<?> commonGetListData(int type){
		List<Object> activitys = new ArrayList<Object>();
		try {
			mCursor = commonDB.commonGetData(type);
			while(mCursor.moveToNext()){
				Object obj = (Object) SerializeUtil.deserializeObject(mCursor.getBlob(
						mCursor.getColumnIndex("cacheData")));
				activitys.add(obj);
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
		return activitys != null ? activitys : null;		
	}
	
	/**
	 * 根据类型删除数据
	 */
	public synchronized void commonDeleteData(final int type){
		try {
			commonDB.commonDeleteData(type);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDB();
		}
	}
	
	/**
	 * 删除所有数据
	 */
	public synchronized void commonDeleteData(){
		Commmons.getIntances().submit(new Runnable(){

			@Override
			public void run() {
				try {
					commonDB.commonDeleteData();
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					closeDB();
				}
			}
		});
	}
	
	/**
	 * 根据ID删除数据
	 */
	public synchronized void commonDeleteData(final int type, final String id){
		Commmons.getIntances().submit(new Runnable(){

			@Override
			public void run() {
				try {
					commonDB.commonDeleteData(type,id);
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					closeDB();
				}
			}
		});
	}
	
	/**
	 * 获取Count
	 */
	public int getCount(int type){
		int count = 0;
		try {
			mCursor = commonDB.commonGetData(type);
			if( mCursor.moveToNext()){
				count = mCursor.getInt(mCursor.getColumnIndex("count"));
				return count ;
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
		return 0;	
	}
	
	/**
	 * 根据ID获取Count
	 */
	public int getCount(int type,String id){
		int count = 0;
		try {
			mCursor = commonDB.commonGetData(type,id);
			if(mCursor.moveToNext()){
				count = mCursor.getInt(mCursor.getColumnIndex("count"));
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
		return count  ;	
	}
	
	/**
	 * 根据type获取next
	 */
	public int getNext(int type){
		int next = 0;
		try {
			mCursor = commonDB.commonGetData(type);
			if(mCursor.moveToNext()){
				next = mCursor.getInt(mCursor.getColumnIndex("next"));
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
		return next  ;	
	}
	
	/**
	 * 根据ID和type获取next
	 */
	public int getNext(int type,String id){
		int next = 0;
		try {
			mCursor = commonDB.commonGetData(type,id);
			if(mCursor.moveToNext()){
				next = mCursor.getInt(mCursor.getColumnIndex("next"));
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
		return next  ;	
	}
	
	/**
	 * 根据ID和type获取next
	 */
	public int getNext(int type,long id){
		int next = 0;
		try {
			mCursor = commonDB.commonGetData(type,id+"");
			if(mCursor.moveToNext()){
				next = mCursor.getInt(mCursor.getColumnIndex("next"));
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
		return next  ;	
	}
	
	/**
	 * 修改Next
	 * @param type
	 * @param next
	 * @return
	 */
	public int updateNext(int type, int next) {
		int i = -1;
		try {
			i = commonDB.updateNext(type, next);
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
		return i;
	}
	
	/**
	 * 修改用户数据,根据TYPE
	 * @param dingzaiId
	 * @param cacheData
	 */
	public void commonUpdateData(int type,byte[] cacheData) {
		try {
			commonDB.commonUpdateData(type, cacheData);
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
	}
	
	/**
	 * 修改用户数据,根据TYPE和ID
	 * @param dingzaiId
	 * @param cacheData
	 */
	public void commonUpdateData(int type,String id,byte[] cacheData) {
		try {
			commonDB.commonUpdateData(type, id, cacheData);
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
	}
	
	/**
	 * 关闭数据库
	 */
	public void closeDB() {
		if (mCursor != null) {
			mCursor.close();
		}
		if (commonDB != null) {
			commonDB.endTransaction();
			commonDB.close();
		}
	}

}
