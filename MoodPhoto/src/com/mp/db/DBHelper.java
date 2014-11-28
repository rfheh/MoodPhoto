
package com.mp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	private static final int DB_VERSION = 2;
	private static final String DB_NAME = "com_mood_photo.db";
	
	private SQLiteDatabase mDatabase;
	
	public DBHelper(Context context) {
		this(context, DB_NAME, null, DB_VERSION);
	}
	
	public DBHelper(Context context, String name,
			CursorFactory factory, int version) {
		
		super(context, name, factory, version);
		    
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		this.mDatabase = db;
		
		createTables(db);
	}

	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		Log.i("NetworkFailDBHelper onUpgrade", "oldVersion:" + oldVersion + " --- newVersion:" + newVersion);
		createTables(db);
	}

	private void createTables(SQLiteDatabase db) {
		try {
			db.beginTransaction();
			Log.i("DataBase Transaction", "beginTransaction");
			Log.i("DataBase createTables", "createTables");
			db.execSQL(String.format(DBTables.DROP_TABLE, DBTables.TABLE_MOOD_ARTICLE));
			db.execSQL(DBTables.CREATE_TABLE_MOOD_ARTICLE);
			db.setTransactionSuccessful();
			Log.i("DataBase createTables", "transactionSuccessful");
		} catch (Exception e) {
			Log.e("DataBase Transaction", "Transaction Exception:" + e.getMessage() + "");
		} finally {
			db.endTransaction();
		}
	}
	
	/* ***************************************************************
	 * table 增删改查
	 * ***************************************************************/
	
	public long insert(String Table_Name, ContentValues values) {
		if (mDatabase == null)
			mDatabase = getWritableDatabase();
		return mDatabase.insert(Table_Name, null, values);
	}

	public int delete(String Table_Name, String whereClause, String[] whereArgs) {
		if (mDatabase == null)
			mDatabase = getWritableDatabase();
		return mDatabase.delete(Table_Name, whereClause, whereArgs);
	}

	public int update(String Table_Name, ContentValues values,
			String WhereClause, String[] whereArgs) {
		if (mDatabase == null) {
			mDatabase = getWritableDatabase();
		}
		return mDatabase.update(Table_Name, values, WhereClause, whereArgs);
	}

	public Cursor query(String Table_Name, String[] columns, String whereStr,
			String[] whereArgs) {
		if (mDatabase == null) {
			mDatabase = getReadableDatabase();
		}
		return mDatabase.query(Table_Name, columns, whereStr, whereArgs, null, null,
				null);
	}

	public Cursor query(String querySql, String[] selectionArgs) {
		if (mDatabase == null) {
			mDatabase = getReadableDatabase();
		}
		return mDatabase.rawQuery(querySql, selectionArgs);
	}
	
	/* ***************************************************************
	 * 事务操作
	 * ***************************************************************/
	
	/**开始事务*/
	public void beginTransaction() {
		if (mDatabase == null) {
			mDatabase = getWritableDatabase();
		}
		mDatabase.beginTransaction();
	}
	
	/**事务成功*/
	public void setTransactionSuccessful() {
		if (mDatabase == null) {
			mDatabase = getWritableDatabase();
		}
		mDatabase.setTransactionSuccessful();
	}
	
	/**事务结束*/
	public void endTransaction() {
		if (mDatabase == null) {
			mDatabase = getWritableDatabase();
		}
		mDatabase.endTransaction();
	}
	
	public void closeDatabase() {
		if (mDatabase != null) {
			mDatabase.close();
			mDatabase = null;
		}
	}
	
}

    