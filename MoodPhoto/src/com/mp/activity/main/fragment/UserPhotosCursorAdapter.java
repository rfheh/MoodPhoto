package com.mp.activity.main.fragment;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.support.v4.widget.ResourceCursorAdapter;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;

import com.etsy.android.grid.util.DynamicHeightImageView;
import com.mp.R;
import com.mp.util.MediaStoreCursorHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

public class UserPhotosCursorAdapter extends ResourceCursorAdapter {

	DisplayImageOptions mOptions;
	SparseBooleanArray mPositionSparse;
	int mPreviousPosition, mCurrentPosition;
	long mAnimDuration;
	Integer[] mColors;
	int mNextColor = 0;
	boolean mEnableAnim;
	SpeedScrollListener mScrollListener;
	
	public UserPhotosCursorAdapter(final Context context, Cursor c, boolean enableAnim,
			SpeedScrollListener speedScrollListener) {
		super(context, R.layout.layout_photo_grid_item, c, 0);
		
		if (c != null) {
			mPositionSparse = new SparseBooleanArray(c.getCount());
		}
		mPreviousPosition = -1;
		mCurrentPosition = -1;
		mEnableAnim = enableAnim;
		mScrollListener = speedScrollListener;
		mOptions = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.empty_photo)
		.cacheInMemory(true)
		.cacheInMemory(true)
		.considerExifParams(true)
		.displayer(new BitmapDisplayer() {
			
			@Override
			public void display(Bitmap bitmap, ImageAware imageAware,
					LoadedFrom loadedFrom) {
				if (!imageAware.isCollected() && imageAware.getWrappedView() instanceof DynamicHeightImageView) {
					//Log.e("BitmapDisplayer", "imageAware.getWrappedView() -- > DynamicHeightImageView ");
				}
				imageAware.setImageBitmap(bitmap);
			}
			
		})
		.build();
		
		mColors = new Integer[]{R.color.deep_blue_14, R.color.deep_green_14, R.color.deep_purple_9
				, R.color.deep_red_14, R.color.deep_yellow_14};
	}
	
	@Override
	public Cursor swapCursor(Cursor newCursor) {
		if (newCursor != null) {			
			mPositionSparse = new SparseBooleanArray(newCursor.getCount());
		}
		mPreviousPosition = -1;
		mCurrentPosition = -1;
		return super.swapCursor(newCursor);
		
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		
		mCurrentPosition = arg0;
		return super.getView(arg0, arg1, arg2);
		
	}
	
	@Override
	public void bindView(View arg0, Context arg1, Cursor arg2) {
		
		DynamicHeightImageView photoIv = (DynamicHeightImageView) arg0.findViewById(R.id.iv_photo);
		arg0.setBackgroundResource(mColors[mNextColor]);
		mNextColor++;
		mNextColor = mNextColor >= mColors.length ? 0 : mNextColor;
		
		Uri uri = MediaStoreCursorHelper.photosCursorToSelection(MediaStoreCursorHelper.MEDIA_STORE_CONTENT_URI, arg2);
		if (uri != null) {			
			ImageLoader.getInstance().displayImage(Uri.decode(uri.toString()), photoIv, mOptions);
		}

		if (mEnableAnim) {			
			startViewAnimation(arg0);
		}
		
	}

	private void startViewAnimation(View view) {
		
		double speed = mScrollListener.getSpeed();
		
		if ((view != null) && (!mPositionSparse.get(mCurrentPosition)) && 
				(mCurrentPosition > mPreviousPosition) && (int)speed != 0) {
			mAnimDuration = (long) (15000D * (1D / speed));
			mAnimDuration = mAnimDuration > 1000L ? 1000L : mAnimDuration;
			mPreviousPosition = mCurrentPosition;
			view.setTranslationX(0.0F);
			WindowManager localWindowManager = (WindowManager)mContext.getSystemService("window");
			Point point = new Point();
			localWindowManager.getDefaultDisplay().getSize(point);
		    int height = point.y;
			view.setTranslationY(height);
			view.setRotationX(45.0F);
			view.setScaleX(0.7F);
			view.setScaleY(0.55F);
			view.animate().rotationX(0.0F).rotationY(0.0F).translationX(0.0F).translationY(0.0F).
				setDuration(mAnimDuration).scaleX(1.0F).scaleY(1.0F).setInterpolator(new DecelerateInterpolator()).
				setStartDelay(0L).start();
		    mPositionSparse.put(mCurrentPosition, true);
		}
	}
}
