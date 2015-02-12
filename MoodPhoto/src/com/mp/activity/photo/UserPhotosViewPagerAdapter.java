package com.mp.activity.photo;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;

import com.mp.adapter.CursorPagerAdapter;

public class UserPhotosViewPagerAdapter extends CursorPagerAdapter {

	public UserPhotosViewPagerAdapter(Context context) {
		super(context, null, 0);
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
		
		return null;
		
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		
	}

}
