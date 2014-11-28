
/**  
 * @Description: TODO
 * @author 
 * @date 2014-8-21 上午11:06:00
 * @version V1.0  
 */ 
package com.mp.application;

import com.mp.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.app.Application;
import android.content.Context;

/**
 * @Description: 
 * @Author:Administrator
 * @Since:2014-8-21
 * @Version:1.1.0
 */

public class MPApplication extends Application {

	private static Context mContext;
	
	@Override
	public void onCreate() {
		
		super.onCreate();
		    
		mContext = this;
		
		initImageLoader(getApplicationContext());
	}
	
	public static Context getContext() {
		return mContext;
	}
	
	/**
	 * 
	 * @Description: 缓存路径SD卡 /Android/data/[app_package_name]/cache
	 * @param context
	 * void 
	 * @throws
	 */
	public static void initImageLoader(Context context) {
		
		int imageThumbSize = getContext().getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
		.threadPriority(Thread.NORM_PRIORITY - 2)
		.memoryCacheExtraOptions(imageThumbSize, imageThumbSize)
		.denyCacheImageMultipleSizesInMemory()
		.memoryCacheSizePercentage(50)
		.diskCacheFileNameGenerator(new Md5FileNameGenerator())
		.diskCacheSize(50 * 1024 * 1024) // 50 Mb
		.tasksProcessingOrder(QueueProcessingType.LIFO)
		.writeDebugLogs() // Remove for release app
		.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
}

    