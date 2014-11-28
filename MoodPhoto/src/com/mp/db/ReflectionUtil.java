
package com.mp.db;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.text.TextUtils;
import android.util.Log;

/**
 * 
 * @Description: 一个反射类，用于反射实体类的属性/属性类型/属性值
 * @Author:Administrator
 * @Since:2014-8-8
 * @Version:1.1.0
 */
public class ReflectionUtil {
	
	private static final String CLASS = "ReflectionUtil";

	private static final String METHOD_HEAD_SET = "set";
	private static final String METHOD_HEAD_GET = "get";
	private static final String METHOD_HEAD_IS = "is";
	
	/**
	 * 
	 * @Title: setObjectFieldVal
	 * @Description: 只使用setMethod赋值
	 * @param object
	 * @param fieldName
	 * @param value 
	 * void 
	 * @throws
	 */
	public static void setObjectFieldVal(Object object, String fieldName, Object value) {
		if(object == null || TextUtils.isEmpty(fieldName)) return;
		
		//根据属性来赋值
		/*Field field = reflectClassField(object.getClass(), fieldName, true);
		if (field == null) return;
		try {
			field.set(object, value);
			return;
		}  catch (Exception e) {
			Log.e(CLASS, e.getMessage() + "");
		} */
		
		//根据方法来赋值
		Method method = reflectClassMethod(object.getClass(), METHOD_HEAD_SET + fieldName, true);
		if(method == null) return;
		
		try {
			method.invoke(object, value);
		} catch (Exception e) {
			Log.e(CLASS, e.getMessage() + "");
		} 
	}

	/**
	 * 
	 * @Title: getObjectFieldVal
	 * @Description: 只使用getMethod/isMethod取值
	 * @param object
	 * @param fieldName
	 * @return 
	 * Object 
	 * @throws
	 */
	public static Object getObjectFieldVal(Object object, String fieldName) {
		if(object == null || TextUtils.isEmpty(fieldName)) return null;
		
		Method method = reflectClassMethod(object.getClass(), METHOD_HEAD_GET + fieldName, true);
		if(method != null) {
			try {
				return method.invoke(object);
			} catch (Exception e) {
				Log.e(CLASS, e.getMessage() + "");
			} 
		}
		
		method = reflectClassMethod(object.getClass(), METHOD_HEAD_IS + fieldName, true);
		if(method != null) {
			try {
				return method.invoke(object);
			} catch (Exception e) {
				Log.e(CLASS, e.getMessage() + "");
			} 
		}
		return null;
	}

	public static Field reflectClassField(Class<?> clazz, String fieldName, boolean isEqualsIgnoreCase) {
		if(clazz == null || TextUtils.isEmpty(fieldName))
			return null;
		
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if ((isEqualsIgnoreCase && fieldName.equalsIgnoreCase(field.getName())) || 
					fieldName.equals(field.getName())) {
				return field;
			} 
		}
		return null;
	}
	
	private static Method reflectClassMethod(Class<?> clazz, String methodName, boolean isEqualsIgnoreCase) {
		if(clazz == null || TextUtils.isEmpty(methodName))
			return null;
		
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			if ((isEqualsIgnoreCase && method.getName().equalsIgnoreCase(methodName)) ||
					method.getName().equals(methodName)) {
				return method;
			}
		}
		return null;
	}
}

    