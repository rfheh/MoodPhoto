package com.mp.activity.photo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import com.mp.R;
import com.mp.activity.BaseActionBarActivity;
import com.mp.adapter.CursorPagerAdapter;
import com.mp.util.MediaStoreCursorHelper;
import com.mp.util.PhotoCursorLoader;

public class PhotoViewerActivity extends BaseActionBarActivity implements OnPageChangeListener
	, LoaderCallbacks<Cursor> {

	public static final String EXTRA_POSITION = "extra_position";
    public static final String EXTRA_BUCKET_ID = "extra_bucket_id";

    public static int MODE_ALL_VALUE = 100;
	
	ViewPager mViewPager;
	PagerAdapter mAdapter;
	
	String mBucketId;
	int mRequestedPosition = -1;
	
	@Override
	protected void onCreate(Bundle arg0) {
		
		super.onCreate(arg0);
		
		setContentView(R.layout.activity_photo_viewer);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		mViewPager = (ViewPager) findViewById(R.id.vp_photos);
		mViewPager.setOffscreenPageLimit(2);
		mViewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.ui_element_spacing_small));
		mViewPager.setOnPageChangeListener(this);
		
		final Intent intent = getIntent();
		
		mBucketId = intent.getStringExtra(EXTRA_BUCKET_ID); 
		mAdapter = new UserPhotosViewPagerAdapter(this);
		getSupportLoaderManager().initLoader(0, null, this);
		
		mViewPager.setAdapter(mAdapter);
		
		if (intent.hasExtra(EXTRA_POSITION)) {
			mRequestedPosition = intent.getIntExtra(EXTRA_POSITION, 0);
			mViewPager.setCurrentItem(mRequestedPosition);
		}
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(" ");
		
		mViewPager.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				mViewPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				onPageSelected(mViewPager.getCurrentItem());
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//使用support library，showAsAction需要使用新的命名空间才可以，详见R.menu.menu_photo_viewer
		getMenuInflater().inflate(R.menu.menu_photo_viewer, menu);
		return super.onCreateOptionsMenu(menu);
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
		
	}
	
	@Override
	protected void saveInstanceState(Bundle outState) {
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		if (arg0 != ViewPager.SCROLL_STATE_IDLE) {
			
		}
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle params) {
		String selection = null;
		String[] selectionArgs = null;
		if (null != mBucketId) {
			selection = Images.Media.BUCKET_ID + " = ?";
			selectionArgs = new String[]{mBucketId};
		}
		
		return new PhotoCursorLoader(this, MediaStoreCursorHelper.MEDIA_STORE_CONTENT_URI, 
				MediaStoreCursorHelper.PHOTOS_PROJECTION, selection, selectionArgs, 
				MediaStoreCursorHelper.PHOTOS_ORDER_BY, false);
		
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		if (mAdapter instanceof CursorPagerAdapter) {
			((CursorPagerAdapter) mAdapter).swapCursor(arg1);
		}
		
		if (mRequestedPosition != -1) {
			mViewPager.setCurrentItem(mRequestedPosition, false);
			mRequestedPosition = -1;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		onLoadFinished(arg0, null);
	}

}
