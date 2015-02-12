package com.mp.activity.photo;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;

import com.mp.R;
import com.mp.activity.BaseActionBarActivity;

public class PhotoViewerActivity extends BaseActionBarActivity implements OnPageChangeListener {

	ViewPager mViewPager;
	PagerAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle arg0) {
		
		super.onCreate(arg0);
		
		setContentView(R.layout.activity_photo_viewer);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		mViewPager = (ViewPager) findViewById(R.id.vp_photos);
		mViewPager.setOffscreenPageLimit(2);
		mViewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.ui_element_spacing_small));
		mViewPager.setOnPageChangeListener(this);
		
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//使用support library，showAsAction需要使用新的命名空间才可以，详见R.menu.menu_photo_viewer
		getMenuInflater().inflate(R.menu.menu_photo_viewer, menu);
		return super.onCreateOptionsMenu(menu);
		
	}
	
	@Override
	protected void saveInstanceState(Bundle outState) {
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
	}

}
