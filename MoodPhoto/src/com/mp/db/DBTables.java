
package com.mp.db;

/**
 * @Description: 建表语句类
 * @Author:Administrator
 * @Since:2014-8-8
 * @Version:1.1.0
 */
public class DBTables {

	//删除表
	public static final String DROP_TABLE = "DROP TABLE IF EXISTS %1$s";
	
	//查询表信息 字段序号|字段名称|字段类型
	public static final String PRAGMS_TABLE_INFO = "PRAGMA TABLE_INFO(%1$s)";
	
	//文章表
	public static final String TABLE_MOOD_ARTICLE = "MOOD_ARTICLE";
	public static final String CREATE_TABLE_MOOD_ARTICLE = "CREATE TABLE " + TABLE_MOOD_ARTICLE + " (" +
			"_id        INTEGER PRIMARY KEY AUTOINCREMENT," +
			"uri        TEXT," +
			"article    TEXT," +
			"readTimes  INTEGER," +
			"date       TEXT" +
			")";
	
}

    