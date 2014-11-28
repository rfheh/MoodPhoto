
/**  
 * @Description: TODO
 * @author 
 * @date 2014-8-21 上午11:22:38
 * @version V1.0  
 */ 
package com.mp.activity.main;

import java.util.ArrayList;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mp.R;
import com.mp.activity.BaseActionBarActivity;
import com.mp.activity.BaseActivity;
import com.mp.activity.main.fragment.ArticleListFragment;
import com.mp.activity.main.fragment.PhotoListFragment;
import com.mp.common.ZoomOutPageTransformer;
import com.mp.entity.MoodArticle;

/**
 * @Description: 
 * @Author:Administrator
 * @Since:2014-8-21
 * @Version:1.1.0
 */

public class MainActivity extends BaseActionBarActivity {

	public static final String KEY_PHOTO_ITEMS = "KEY_PHOTO_ITEMS";
	
	View mContentView;
	ViewPager mViewPager;
	PopupWindow mPopupWindow;
	ExpandableListView mDateListView;
	
	ArrayList<MoodArticle> mPhotoItems;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle arg0) {
		
		super.onCreate(arg0);
		
		if (arg0 != null) {
			mPhotoItems = (ArrayList<MoodArticle>) arg0.getSerializable(KEY_PHOTO_ITEMS);
		}
		
		setContentView(R.layout.activity_main_images);   
		
		mContentView = findViewById(R.id.vp_content);
		mViewPager = (ViewPager) findViewById(R.id.vp_content);
		
		mDateListView = new ExpandableListView(MainActivity.this);
		mDateListView.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_MENU && mPopupWindow.isShowing()) {
					mPopupWindow.dismiss();
				}
				return false;
			}
		});
		mDateListView.setAdapter(new MyExpandableListAdapter());
		mPopupWindow = new PopupWindow(mDateListView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mPopupWindow.setFocusable(true); //获取焦点
		mPopupWindow.setOutsideTouchable(false);	//设置允许在外点击消失 ，但必须设置 BackgroundDrawable
		mPopupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.baichun)));//new ColorDrawable(0xb0000000);
	}
	
	@Override
	protected void onResume() {
		
		super.onResume();
		
		PagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(adapter);
		mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
	}
	
	@Override
	protected void saveInstanceState(Bundle outState) {
		outState.putSerializable(KEY_PHOTO_ITEMS, mPhotoItems);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (!mPopupWindow.isShowing()) {
				mPopupWindow.showAtLocation(mViewPager, Gravity.LEFT, 0, 0);
				mPopupWindow.update();	
			}
			
			
			return true;
		}
		return super.onKeyDown(keyCode, event);
		    
	}
	
	
	static class ViewPagerAdapter extends FragmentStatePagerAdapter {

		private static final Fragment[] FRAGMENTS = new Fragment[]
				{PhotoListFragment.getInstance(), ArticleListFragment.getInstance()};
		
		public ViewPagerAdapter(FragmentManager fm) {
			
			super(fm);
			    
		}

		@Override
		public Fragment getItem(int arg0) {
			
			return FRAGMENTS[arg0];
			    
		}

		@Override
		public int getCount() {
			
			return FRAGMENTS.length;
			    
		}
		
	}
	
	/**
     * A simple adapter which maintains an ArrayList of photo resource Ids. 
     * Each photo is displayed as an image. This adapter supports clearing the
     * list of photos and adding a new photo.
     *
     */
    public class MyExpandableListAdapter extends BaseExpandableListAdapter {
        // Sample data set.  children[i] contains the children (String[]) for groups[i].
        private String[] groups = { "People Names", "Dog Names", "Cat Names", "Fish Names" };
        private String[][] children = {
                { "Arnold", "Barry", "Chuck", "David" },
                { "Ace", "Bandit", "Cha-Cha", "Deuce" },
                { "Fluffy", "Snuggles" },
                { "Goldy", "Bubbles" }
        };
        
        public Object getChild(int groupPosition, int childPosition) {
            return children[groupPosition][childPosition];
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        public int getChildrenCount(int groupPosition) {
            return children[groupPosition].length;
        }

        public TextView getGenericView() {
            // Layout parameters for the ExpandableListView
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 120);

            TextView textView = new TextView(MainActivity.this);
            textView.setLayoutParams(lp);
            // Center the text vertically
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            // Set the text starting position
            textView.setPadding(36, 0, 0, 0);
            return textView;
        }
        
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                View convertView, ViewGroup parent) {
            View childView = getLayoutInflater().inflate(android.R.layout.simple_expandable_list_item_2, parent, false);
            ((TextView)childView.findViewById(android.R.id.text1)).setText(getChild(groupPosition, childPosition).toString());
            return childView;
        }

        public Object getGroup(int groupPosition) {
            return groups[groupPosition];
        }

        public int getGroupCount() {
            return groups.length;
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                ViewGroup parent) {
        	View groupView = getLayoutInflater().inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
            ((TextView)groupView).setText(getGroup(groupPosition).toString());
            return groupView;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public boolean hasStableIds() {
            return true;
        }

    }
}

    