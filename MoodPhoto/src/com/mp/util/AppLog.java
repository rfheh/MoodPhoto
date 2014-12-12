package com.mp.util;

import android.util.Log;

import com.mp.BuildConfig;
import com.mp.application.MPApplication;

public class AppLog {

	static boolean debug = BuildConfig.DEBUG;
	static final String TAG = MPApplication.class.getSimpleName();
	
	public static void i(String msg) {
		i(TAG, msg);
	}
	
	public static void i(String tag, String msg) {
		if (debug) {
			Log.i(tag, msg);
		}
	}
	
	public static void e(String msg) {
		e(TAG, msg);
	}
	
	public static void e(String tag, String msg) {
		if (debug) 
			Log.e(tag, msg);
	}
}
