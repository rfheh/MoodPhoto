
/**  
 * @Description: TODO
 * @author hx Lu
 * @date 2014-11-27 上午10:36:32
 */ 
package com.mp.activity;

import com.mp.application.MPApplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.Toast;

/**
 * @Description: 
 * @Author:hx Lu
 * @Since:2014-11-27
 */

public abstract class BaseActionBarActivity extends ActionBarActivity {

protected FragmentManager mFm;
	
	@Override
	protected void onCreate(Bundle arg0) {
		
		super.onCreate(arg0);
		
		mFm = getSupportFragmentManager();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		saveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}
	
	/**
	 * 
	 * @Title: saveInstanceState
	 * @Description: 刻意去提醒执行保存动作
	 * @param outState 
	 * void 
	 * @throws
	 */
	protected abstract void saveInstanceState(Bundle outState);
	
	public static Intent newIntent(Class<?> clazz) {
		Intent intent = new Intent(MPApplication.getContext(), clazz);
		return intent;
	}
	
	/**
	 * 
	 * @Title: getSerializableObj
	 * @Description: 获取Intent跳转时存储在Bundle的对象，或者onSaveInstanceState时存储在Bundle的对象
	 * @param saveInstanceBundle
	 * @param key
	 * @return 
	 * Object 
	 * @throws
	 */
	protected Object getSerializableObj(Bundle saveInstanceBundle, String key) {
		if (saveInstanceBundle != null && saveInstanceBundle.containsKey(key)) {
			return saveInstanceBundle.getSerializable(key);
		} else if (getIntent() != null && getIntent().hasExtra(key)) {
			return getIntent().getSerializableExtra(key);
		}
		
		return null;
	}
	
	public int addFragment(int arg0, Fragment arg1) {
		return mFm.beginTransaction().add(arg0, arg1).commit();
	}
	
	public int addFragment(int arg0, Fragment arg1, String backStackStr) {
		return mFm.beginTransaction().add(arg0, arg1).addToBackStack(backStackStr).commit();
	}
	
	public int replaceFragment(int arg0, Fragment arg1) {
		return mFm.beginTransaction().replace(arg0, arg1).commit();
	}
	
	public int replaceFragment(int arg0, Fragment arg1, String backStackStr) {
		return mFm.beginTransaction().replace(arg0, arg1).addToBackStack(backStackStr).commit();
	}
	
	public FragmentTransaction removeFragment(Fragment arg0) {
		return mFm.beginTransaction().remove(arg0);
	}
	
	/**
	 * 重设ListView高度
	 * @param listView
	 */
	public void resetListViewHeight(ListView listView) {
		if (listView.getAdapter() == null)
			return;

		int totalHeight = 0;
		for (int i = 0; i < listView.getAdapter().getCount(); i++) {
			View item = listView.getAdapter().getView(i, null, listView);
			item.measure(0, 0);
			totalHeight += item.getMeasuredHeight();
		}

		LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * listView.getCount() - 1);
		listView.setLayoutParams(params);
	}
	
	/**
	 * 
	 * @Description: show Toast
	 * @param: void 
	 * @throws
	 */
	public void showToast(String msg) {
		Toast.makeText(BaseActionBarActivity.this, msg, Toast.LENGTH_LONG).show();
	}
}

    