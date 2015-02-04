
/**  
 * @Description: TODO
 * @author 
 * @date 2014-8-21 上午11:22:38
 * @version V1.0  
 */ 
package com.mp.activity.main;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.view.KeyEvent;

import com.mp.R;
import com.mp.activity.BaseActionBarActivity;
import com.mp.activity.main.fragment.ArticleListFragment;
import com.mp.activity.main.fragment.PhotoListFragment;
import com.mp.activity.main.fragment.UserPhotosFragment;
import com.mp.entity.MoodArticle;

/**
 * @Description: 
 * @Author:Administrator
 * @Since:2014-8-21
 * @Version:1.1.0
 */

public class MainActivity extends BaseActionBarActivity implements TabListener {

	public static final String KEY_PHOTO_ITEMS = "KEY_PHOTO_ITEMS";
	static final int TAB_PHOTOS = 0;
	static final int TAB_PHOTO_LIST = 1;
	static final int TAB_ARTICLES = 2;
	
	
	ArrayList<MoodArticle> mPhotoItems;
	Tab mPreviouslySelectedTab;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle arg0) {
		
		super.onCreate(arg0);
		
		if (arg0 != null) {
			mPhotoItems = (ArrayList<MoodArticle>) arg0.getSerializable(KEY_PHOTO_ITEMS);
		}
		
		setContentView(R.layout.activity_main_images);  
		
		ActionBar ab = getSupportActionBar();
		ab.setDisplayShowTitleEnabled(false);
		ab.setNavigationMode(ab.getNavigationMode() == ActionBar.NAVIGATION_MODE_STANDARD
            ? ActionBar.NAVIGATION_MODE_TABS : ActionBar.NAVIGATION_MODE_STANDARD);
		ab.addTab(ab.newTab().setText(R.string.tab_photos).setTag(TAB_PHOTOS).setTabListener(this));
		ab.addTab(ab.newTab().setText(R.string.tab_photo_list).setTag(TAB_PHOTO_LIST).setTabListener(this));
		ab.addTab(ab.newTab().setText(R.string.tab_articles).setTag(TAB_ARTICLES).setTabListener(this));
		
	}
	
	@Override
	protected void onResume() {
		
		super.onResume();
	}
	
	@Override
	protected void saveInstanceState(Bundle outState) {
		outState.putSerializable(KEY_PHOTO_ITEMS, mPhotoItems);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
		}
		return super.onKeyDown(keyCode, event);
		    
	}
	
	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		//onTabUnselected(...) would could before  onTabSelected(...)
		mPreviouslySelectedTab = arg0;
	}
	
	@Override
	public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
		System.out.println("onTabSelected");
		final int id = (Integer) arg0.getTag();
		replacePrimaryFragment(id, arg1);
		supportInvalidateOptionsMenu();
	}

	private void replacePrimaryFragment(int id, FragmentTransaction ft) {
		Fragment fragment = null;
		switch (id) {
		case TAB_PHOTOS:
			fragment = new UserPhotosFragment();
			break;
		case TAB_PHOTO_LIST:
			fragment = PhotoListFragment.getInstance();
			break;
		case TAB_ARTICLES:
			fragment = ArticleListFragment.getInstance();
			break;
		default:
			break;
		}
		
		if (mPreviouslySelectedTab != null) {
			final int oldId = (Integer) mPreviouslySelectedTab.getTag();
			final int enterAnim = id > oldId ? R.animator.slide_in_right : R.animator.slide_in_left;
			final int exitAnim = id > oldId ? R.animator.slide_out_left : R.animator.slide_out_right;
			ft.setCustomAnimations(enterAnim, exitAnim);
		}
		
		ft.replace(R.id.frag_primary, fragment);
	}
}

    