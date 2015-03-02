package com.mp.activity.photo;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mp.R;
import com.mp.adapter.CursorPagerAdapter;
import com.mp.util.MediaStoreCursorHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class UserPhotosViewPagerAdapter extends CursorPagerAdapter {

	DisplayImageOptions mOptions;
	
	public UserPhotosViewPagerAdapter(Context context) {
		super(context, null, 0);
		mOptions = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.empty_photo)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.displayer(new SimpleBitmapDisplayer())
		.build();
	}

	@Override
	public int getItemPosition(Object object) {
		
		return POSITION_NONE;
		
	}
	
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == (View)object;
	}
	  
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		ImageView imageView = new ImageView(context);
		
		return imageView;
		
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ImageView imageView = (ImageView) view;
		Uri uri = MediaStoreCursorHelper.photosCursorToSelection(MediaStoreCursorHelper.MEDIA_STORE_CONTENT_URI, cursor);
		if (uri != null) {			
			ImageLoader.getInstance().displayImage(Uri.decode(uri.toString()), imageView, mOptions);
		}
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		
		super.destroyItem(container, position, object);
		
	}
}
