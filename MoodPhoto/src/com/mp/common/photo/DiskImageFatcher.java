
/**  
 * @Description: TODO
 * @author hx Lu
 * @date 2014-9-1 下午4:23:56
 */ 
package com.mp.common.photo;

import com.mp.util.BitmapUtil;
import com.mp.util.WindowScreenUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

/**
 * @Description: A simple subclass of {@link ImageResizer} that fetches and resizes images fetched from a Disk Uri.
 * @Author:hx Lu
 * @Since:2014-9-1
 */

public class DiskImageFatcher extends ImageResizer {

	//private static final String TAG = "DiskImageFatcher";
	
	private Context mContext;
	private OnResizeBitmapListener mOnResizeBitmapListener;
	
	/**
     * Initialize providing a target image width and height for the processing images.
     *
     * @param context
     * @param imageWidth
     * @param imageHeight
     */ 
	public DiskImageFatcher(Context context, int imageWidth, int imageHeight) {
		
		super(context, imageWidth, imageHeight);
		init(context);    
	}

	private void init(Context context) {
		this.mContext = context;
	}
	
	@Override
	protected Bitmap processBitmap(Object data) {
		
		return processBitmap(String.valueOf(data));
		    
	}
	
	private Bitmap processBitmap(String filePath) {
		return decodeSampledBitmapFromFile(filePath, mImageWidth, mImageHeight);
	}
	
	/**
	 * 先查询内存中是否存在
	 */
	@Override
	public void loadImage(Object data, ImageView imageView) {
		
		super.loadImage(data, imageView);
		    
	}
	
	@Override
	protected void setImageBitmap(ImageView imageView, Bitmap bitmap) {
		
		Bitmap resizeBitmap = bitmap;
		if(mOnResizeBitmapListener != null) {
			resizeBitmap = mOnResizeBitmapListener.onResizeBitmap(bitmap);
		}
		super.setImageBitmap(imageView, resizeBitmap);
	}
	
	public void setOnResizeBitmapListener(OnResizeBitmapListener listener) {
		this.mOnResizeBitmapListener = listener;
	}
	
	public interface OnResizeBitmapListener {
		public Bitmap onResizeBitmap(Bitmap src);
	}
}

    