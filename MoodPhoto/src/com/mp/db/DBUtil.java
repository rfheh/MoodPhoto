
/**  
 * @Description: TODO
 * @author 
 * @date 2014-8-10 上午11:26:20
 * @version V1.0  
 */ 
package com.mp.db;

import java.lang.reflect.Field;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

/**
 * @Title: NetworkFailUtil.java
 * @Package com.icyt.common.db.networkfail
 * @Description: 只是一个工具类，针对NetworkFail的操作
 * @Author:Administrator
 * @Since:2014-8-10
 * @Version:1.1.0
 */

public class DBUtil {

	public static void putContentValue(ContentValues contentValues, Object object, String key, String fieldName) {
		
		if(contentValues == null || object == null || TextUtils.isEmpty(key)) return;
		
		Object valueObj = ReflectionUtil.getObjectFieldVal(object, fieldName);
		if(valueObj == null) return;
		
		Field field = ReflectionUtil.reflectClassField(object.getClass(), fieldName, true);
		if(field == null) return;
		
		String typeName = field.getType().getSimpleName();
		if (typeName.equals("int") || typeName.equals("Integer")) {
			contentValues.put(key, (Integer)valueObj);
		} else if (typeName.equals("double") || typeName.equals("Double")) {
			contentValues.put(key, (Double)valueObj);
		} else if (typeName.equals("String")) {
			contentValues.put(key, (String)valueObj);
		}
	}
	
	public static void setObjectValue(Object object, String fieldName, Cursor cursor, String columnName) {
		
		if(object == null || TextUtils.isEmpty(fieldName) || cursor == null || TextUtils.isEmpty(columnName)) return;
		
		Field field = ReflectionUtil.reflectClassField(object.getClass(), fieldName, true);
		if(field == null) return;
		
		String typeName = field.getType().getSimpleName();
		int columnIndex = cursor.getColumnIndex(columnName);
		if (typeName.equals("int") || typeName.equals("Integer")) {
			ReflectionUtil.setObjectFieldVal(object, fieldName, cursor.getInt(columnIndex));
		} else if (typeName.equals("double") || typeName.equals("Double")) {
			ReflectionUtil.setObjectFieldVal(object, fieldName, cursor.getDouble(columnIndex));
		} else if (typeName.equals("String")) {
			ReflectionUtil.setObjectFieldVal(object, fieldName, cursor.getString(columnIndex));
		} 
		
	}
	
	/* ***************************************************************
	 * 实体类与表字段转换
	 * ***************************************************************/
	
	public static ContentValues objectToContentValue(Object object, String tableName, DBHelper dbHelper) {
		if(object == null || TextUtils.isEmpty(tableName)) return null;
		
		ContentValues values = new ContentValues();
		//查询表的信息
		Cursor cursor = dbHelper.query(String.format(DBTables.PRAGMS_TABLE_INFO, tableName), null);
		if (cursor.moveToFirst()) {
			do {
				String columnName = cursor.getString(1); //大写，可能带下划线
				String fieldName = columnName.replaceAll("_", ""); //去掉下划线，和类属性名称大写状态一致
				DBUtil.putContentValue(values, object, columnName, fieldName);
			} while (cursor.moveToNext());
		}
		return values;
	}
	
	public static Object cursorToObject(Cursor cursor, Object object) {
		int columnCount = cursor.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			String columnName = cursor.getColumnName(i); //大写，可能带下划线
			String fieldName = columnName.replaceAll("_", ""); //去掉下划线，和类属性名称大写状态一致
			DBUtil.setObjectValue(object, fieldName, cursor, columnName);
		}
		return object;
	}
}

    