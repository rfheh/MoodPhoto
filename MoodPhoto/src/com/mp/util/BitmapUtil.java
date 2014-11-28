package com.mp.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;

public class BitmapUtil {

	//缩放图片 按需求宽高
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float x = (float)w/width;
		float y = (float)h/height;
		matrix.postScale(x, y);
		Bitmap zoomBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		return zoomBitmap;
	}
	
	//缩放图片 按比例
	public static Bitmap zoomBitmap(Bitmap bitmap, float times) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale(times, times);
		Bitmap zoomBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		return zoomBitmap;
	}
	
	//Drawable 复制到 Bitmap
	public static Bitmap drawableToBitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity()!= PixelFormat.OPAQUE?
				Bitmap.Config.ARGB_8888:Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}
	
	//获取四角圆角 Bitmap
	public static Bitmap createRoundCornerBitmap(Bitmap bitmap, float roundPx) {
		Bitmap roundCornerBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(roundCornerBitmap);
		Paint paint = new Paint();
		Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		RectF rectF = new RectF(rect);
		
		int color = 0xff424242;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return roundCornerBitmap;
	}
	
	/**
	 * 
	 * Administrator
	 * 2014-2-26 下午5:26:31
	 * @param bitmap
	 * @param roundPx 值越大角度越大
	 * @param lt true 圆角 false 直角
	 * @param rt true 圆角 false 直角
	 * @param lb true 圆角 false 直角
	 * @param rb true 圆角 false 直角
	 * @return
	 * Bitmap
	 */
	public static Bitmap createRoundCornerBitmap(Bitmap bitmap, float roundPx, boolean lt
			, boolean rt, boolean lb, boolean rb) {
		Bitmap roundCornerBitmap = createRoundCornerBitmap(bitmap, roundPx);
		Canvas canvas = new Canvas(roundCornerBitmap);
		canvas.drawARGB(0, 0, 0, 0);
		int bw = bitmap.getWidth();
		int bh = bitmap.getHeight();
		int centerW = bw / 2;
		int centerH = bh / 2;
		
		Paint paint = new Paint();
		Paint defaultPaint = new Paint();
		int color = 0xff424242;
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		paint.setAntiAlias(true);
		paint.setColor(color);
		
		Bitmap cutBitmap = null;
		Canvas cutCanvas = null;
		if (!lt) {
			cutBitmap = Bitmap.createBitmap(bw, bh, Bitmap.Config.ARGB_8888);
			cutCanvas = new Canvas(cutBitmap);
			cutCanvas.drawRect(0, 0, centerW, centerH, defaultPaint);
			cutCanvas.drawBitmap(bitmap, 0, 0, paint);
			canvas.drawBitmap(cutBitmap, 0, 0, defaultPaint);
			cutBitmap.recycle();
		} 
		if (!rt) {
			cutBitmap = Bitmap.createBitmap(bw, bh, Bitmap.Config.ARGB_8888);
			cutCanvas = new Canvas(cutBitmap);
			cutCanvas.drawRect(centerW, 0, bw, centerH, defaultPaint);
			cutCanvas.drawBitmap(bitmap, 0, 0, paint);
			canvas.drawBitmap(cutBitmap, 0, 0, defaultPaint);
			cutBitmap.recycle();
		}
		if (!lb) {
			cutBitmap = Bitmap.createBitmap(bw, bh, Bitmap.Config.ARGB_8888);
			cutCanvas = new Canvas(cutBitmap);
			cutCanvas.drawRect(0, centerH, centerW, bh, defaultPaint);
			cutCanvas.drawBitmap(bitmap, 0, 0, paint);
			canvas.drawBitmap(cutBitmap, 0, 0, defaultPaint);
			cutBitmap.recycle();
		}
		if (!rb) {
			cutBitmap = Bitmap.createBitmap(bw, bh, Bitmap.Config.ARGB_8888);
			cutCanvas = new Canvas(cutBitmap);
			cutCanvas.drawRect(centerW, centerH, bw, bh, defaultPaint);
			cutCanvas.drawBitmap(bitmap, 0, 0, paint);
			canvas.drawBitmap(cutBitmap, 0, 0, defaultPaint);
			cutBitmap.recycle();
		}
		return roundCornerBitmap;
	}
	
	@SuppressWarnings("deprecation")
	public static Bitmap drawImageBorder(Bitmap srcBitmap, Bitmap borderBitmap) {
		if(srcBitmap == null) throw new NullPointerException("srcBitmap should not null");
		if(borderBitmap == null) return srcBitmap;
		Canvas canvas = new Canvas(srcBitmap);
		Matrix m = canvas.getMatrix();//new Matrix();
		Bitmap newBitmap = Bitmap.createBitmap(borderBitmap, 0, 0, 
				srcBitmap.getWidth(), srcBitmap.getHeight(), m, true);
		canvas.drawBitmap(newBitmap, 0, 0, null);
		return srcBitmap;
	}
	
	//创建带倒影的 Bitmap
	public static Bitmap createInvertedBitmap(Bitmap bitmap) {
		int reflectionGap = 4;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		
		//创建翻转图片
		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);
		Bitmap reflectionBitmap = Bitmap.createBitmap(bitmap, 0, height/2, width, height/2, matrix, false);
		
		//绘图整图+翻转（半图）
		Bitmap bitmapWidthReflectionBitmap = Bitmap.createBitmap(width, (int)(height*1.5), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmapWidthReflectionBitmap);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint defaultPaint = new Paint();
		canvas.drawRect(0, height, width, height+reflectionGap, defaultPaint);
		canvas.drawBitmap(reflectionBitmap, 0, height+reflectionGap, null);
		
		//模糊化翻转（半图）
		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, height, 0, bitmapWidthReflectionBitmap.getHeight(), 
				0x70ffffff, 0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in 
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWidthReflectionBitmap.getHeight()+reflectionGap, paint);
		return bitmapWidthReflectionBitmap;
	}
	
	/**
	 * 
	 * @Description: TODO
	 * @param src
	 * @param scale width / height
	 * @return
	 * Bitmap 
	 * @throws
	 */
	public static Bitmap cutMatrixBitmap(Bitmap src, float scale) {
		
		int srcW = src.getWidth();
		int srcH = src.getHeight();
		int matrixW = srcW;
		int matrixH = srcH;
		
		matrixW = (int)(matrixH * scale);
		if (matrixW > srcW) {
			matrixW = srcW;
			matrixH = (int) (srcW / scale);
		}
		int left = (srcW - matrixW) / 2;
		int top = (srcH - matrixH) / 2;
		
		return src == null ? Bitmap.createBitmap(matrixW, matrixH, Config.ARGB_8888) :
				Bitmap.createBitmap(src, left, top, matrixW, matrixH);
	}
}
