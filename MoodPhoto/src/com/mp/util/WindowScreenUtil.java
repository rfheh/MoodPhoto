package com.mp.util;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class WindowScreenUtil {
	
	private static final String TAG = WindowScreenUtil.class.getSimpleName();
	
	private static int SCREEN_SIZE_1 = -1;
	private static int SCREEN_SIZE_2 = -1;

	/**
	 * 获取屏幕的宽度 按横竖屏适配
	 * 
	 * @param context
	 *            Context
	 * @return int
	 * @author Haixing Lu
	 * @CreateDate 2013-8-2
	 */
	public static int getScreenWidth(Context context) {
		if (SCREEN_SIZE_1 <= 0 || SCREEN_SIZE_2 <= 0) {
			DisplayMetrics dm = new DisplayMetrics();
			WindowManager wm = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			if (wm != null) {
				wm.getDefaultDisplay().getMetrics(dm);
			}
			SCREEN_SIZE_1 = dm.widthPixels;
			SCREEN_SIZE_2 = dm.heightPixels;
		}

		Configuration conf = context.getResources().getConfiguration();
		switch (conf.orientation) {
		case Configuration.ORIENTATION_LANDSCAPE:
			return SCREEN_SIZE_1 > SCREEN_SIZE_2 ? SCREEN_SIZE_1
					: SCREEN_SIZE_2;
		case Configuration.ORIENTATION_PORTRAIT:
			return SCREEN_SIZE_1 < SCREEN_SIZE_2 ? SCREEN_SIZE_1
					: SCREEN_SIZE_2;
		default:
			Log.e(TAG, "can't get screen width!");
		}
		return SCREEN_SIZE_1;
	}

	/**
	 * 获取屏幕的高度 根据横竖屏适配
	 * 
	 * @param context
	 *            Context
	 * @return int
	 * @author Haixing Lu
	 * @CreateDate 2013-8-2
	 */
	public static int getScreenHeight(Context context) {
		if (SCREEN_SIZE_1 <= 0 || SCREEN_SIZE_2 <= 0) {
			DisplayMetrics dm = new DisplayMetrics();
			WindowManager wm = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			wm.getDefaultDisplay().getMetrics(dm);
			SCREEN_SIZE_1 = dm.widthPixels;
			SCREEN_SIZE_2 = dm.heightPixels;
		}

		Configuration conf = context.getResources().getConfiguration();
		switch (conf.orientation) {
		case Configuration.ORIENTATION_LANDSCAPE:
			return SCREEN_SIZE_1 < SCREEN_SIZE_2 ? SCREEN_SIZE_1
					: SCREEN_SIZE_2;
		case Configuration.ORIENTATION_PORTRAIT:
			return SCREEN_SIZE_1 > SCREEN_SIZE_2 ? SCREEN_SIZE_1
					: SCREEN_SIZE_2;
		default:
			Log.e(TAG, "can't get screen height!");
		}
		return SCREEN_SIZE_1;
	}
	
	public static float getWindowDensityDpi(Context context) {
		return context.getResources().getDisplayMetrics().densityDpi;
	}
	
	/** density = densityDpi / 160;
	 *  px = (densityDpi / 160) * dp
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */ 
	public static int dip2px(Context context, float dpValue) { 
	    final float scale = context.getResources().getDisplayMetrics().density; 
	    return (int) (dpValue * scale + 0.5f); 
	} 
	  
	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */ 
	public static int px2dip(Context context, float pxValue) { 
	    final float scale = context.getResources().getDisplayMetrics().density; 
	    return (int) (pxValue / scale + 0.5f); 
	} 
}
