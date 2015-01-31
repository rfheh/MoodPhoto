package com.mp.activity.main.fragment;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mp.R;
import com.mp.util.BitmapUtil;
import com.mp.util.MediaStoreCursorHelper;
import com.mp.util.WindowScreenUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

public class UserPhotosCursorAdapter extends ResourceCursorAdapter {

	private Typeface mTypeface;
	DisplayImageOptions mOptions;
	
	public UserPhotosCursorAdapter(final Context context, Cursor c) {
		super(context, R.layout.layout_photo_list_item, c, 0);
		mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/00TT.TTF"); 
		mOptions = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.empty_photo)
		.cacheInMemory(true)
		.cacheInMemory(true)
		.considerExifParams(true)
		.displayer(new BitmapDisplayer() {
			
			@Override
			public void display(Bitmap bitmap, ImageAware imageAware,
					LoadedFrom loadedFrom) {
				float scale = (float)WindowScreenUtil.getScreenWidth(context) / WindowScreenUtil.getScreenHeight(context) * 3;
				imageAware.setImageBitmap(BitmapUtil.cutMatrixBitmap(bitmap, scale));
			}
			
		})
		.build();
	}

	@Override
	public void bindView(View arg0, Context arg1, Cursor arg2) {
		int h = WindowScreenUtil.getScreenHeight(mContext) / 3;
		ImageView photoIv = (ImageView) arg0.findViewById(R.id.iv_photo);
		ImageView markIv = (ImageView) arg0.findViewById(R.id.iv_mark);
		TextView dateTv = (TextView) arg0.findViewById(R.id.tv_date);
		photoIv.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, h));
		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) markIv.getLayoutParams();
		int dime = (int)arg1.getResources().getDimension(R.dimen.image_header_size);
		if(lp == null) lp = new FrameLayout.LayoutParams(dime, dime);
		lp.setMargins(arg1.getResources().getDimensionPixelOffset(R.dimen.ui_element_spacing), -dime/2, 0, 0);
		markIv.setLayoutParams(lp);
		dateTv.setTypeface(mTypeface);
		
		markIv.setImageResource(R.drawable.icon_heart_blue);
		Uri uri = MediaStoreCursorHelper.photosCursorToSelection(MediaStoreCursorHelper.MEDIA_STORE_CONTENT_URI, arg2);
		dateTv.setText(uri == null ? null : uri.toString());
		if (uri != null) {			
			ImageLoader.getInstance().displayImage(Uri.decode(uri.toString()), photoIv, mOptions);
		}
	}

}
