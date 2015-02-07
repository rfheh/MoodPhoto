package com.mp.activity.photo;

import android.os.Bundle;
import android.view.Menu;

import com.mp.R;
import com.mp.activity.BaseActionBarActivity;

public class PhotoViewerActivity extends BaseActionBarActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		
		super.onCreate(arg0);
		
		setContentView(R.layout.activity_photo_viewer);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

}
