
/**  
 * @Description: TODO
 * @author hx Lu
 * @date 2014-8-26 下午2:26:17
 */ 
package com.mp.activity.article;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.lib.satellite_menu.SatelliteMenu;
import com.lib.satellite_menu.SatelliteMenu.SateliteClickedListener;
import com.lib.satellite_menu.SatelliteMenuItem;
import com.mp.R;
import com.mp.activity.BaseActivity;
import com.mp.activity.article.fragment.ArticleDetailFragment;
import com.mp.activity.article.fragment.ArticleDetailFragment.ArticleDetailMethods;
import com.mp.activity.article.fragment.ArticleDetailFragment.ArticleMode;
import com.mp.application.MPApplication;
import com.mp.common.AsyncTask;
import com.mp.common.photo.DiskImageFatcher;
import com.mp.common.photo.ImageCache.ImageCacheParams;
import com.mp.db.DBHelper;
import com.mp.db.DBTables;
import com.mp.db.DBUtil;
import com.mp.entity.MoodArticle;

/**
 * @Description: 
 * @Author:hx Lu
 * @Since:2014-8-26
 */

public class MoodArticleActivity extends BaseActivity implements OnBackStackChangedListener , ArticleDetailMethods {

	MoodArticle mMoodArticle;
	
	CallBackFragmentMethods mCallBackFragmentMethods;
	public interface CallBackFragmentMethods {
		public void viewArticle(MoodArticle moodArticle);
	}
	
	//Whether or not we're showing the back of the card (otherwise showing the front).
	boolean mShowingBack = false;
	
	//EditText mArticleEt;
	SatelliteMenu mSatelliteMenu;
	
	DiskImageFatcher mImageFatcher;
	ImageCacheParams mCacheParams;
	
	@Override
	protected void onCreate(Bundle saveInstanceBundle) {
		
		super.onCreate(saveInstanceBundle);
		setContentView(R.layout.activity_mood_article);    
		
		mMoodArticle = (MoodArticle) getSerializableObj(saveInstanceBundle, MoodArticle.class.getSimpleName());
		
		if (saveInstanceBundle == null) {			
			Fragment viewFragment = new ArticleDetailFragment();
			Bundle args = new Bundle();
			args.putSerializable(ArticleMode.class.getSimpleName(), ArticleMode.View);
			viewFragment.setArguments(args);
			
			 getFragmentManager().beginTransaction().add(R.id.flayout_content, viewFragment).commit();
		} else {
			mShowingBack = getFragmentManager().getBackStackEntryCount() > 0;
		}
		getFragmentManager().addOnBackStackChangedListener(this);
		
		mCacheParams = new ImageCacheParams(MoodArticleActivity.this);
		mCacheParams.setMemCacheSizePercent(0.5f); // Set memory cache to 50% of app memory
		
		int imageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
		mImageFatcher = new DiskImageFatcher(MoodArticleActivity.this, imageThumbSize, imageThumbSize);
		mImageFatcher.setLoadingImage(R.drawable.empty_photo);
		mImageFatcher.addImageCache(getSupportFragmentManager(), mCacheParams);
		
		ImageView bgIv = (ImageView) findViewById(R.id.iv_background);
		mImageFatcher.loadImage(mMoodArticle.getUri(), bgIv);
		
		mSatelliteMenu = (SatelliteMenu) findViewById(R.id.sm_menu);
		setSatelliteMenuItems();
	}
	
	private void setSatelliteMenuItems() {
		mSatelliteMenu.setExpandDuration(500); //clean items
		List<SatelliteMenuItem> items = new ArrayList<SatelliteMenuItem>();
		items.add(new SatelliteMenuItem(1, android.R.drawable.ic_menu_delete));
		items.add(new SatelliteMenuItem(2, android.R.drawable.ic_menu_edit));
		mSatelliteMenu.addItems(items);
		mSatelliteMenu.setOnItemClickedListener(new SateliteClickedListener() {
			@Override
			public void eventOccured(int id) {
				if (id == 2) {					
					flipCard();
				}
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			mSatelliteMenu.setVisibility(mSatelliteMenu.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
		    
	}
	
	
	@Override
	protected void onPause() {
		
		super.onPause();
		//if(mSaveingTextWatcher != null) mSaveingTextWatcher.cancelTimer();  
		//doSave(mCommonItem.getTitle(), mArticleEt.getText().toString());
	}
	
	@Override
	protected void onResume() {
		
		super.onResume();
		
	}
	
	@Override
	protected void saveInstanceState(Bundle outState) {
		outState.putSerializable(MoodArticle.class.getSimpleName(), mMoodArticle);
	}

	@Override
	public void onBackStackChanged() {
		mShowingBack = getFragmentManager().getBackStackEntryCount() > 0;
	}
	
	private void flipCard() {
		if(mShowingBack) {
			getFragmentManager().popBackStack();
			return;
		}
			
		mShowingBack = true;
		
		Fragment editFragment = new ArticleDetailFragment();
		Bundle args = new Bundle();
		args.putSerializable(ArticleMode.class.getSimpleName(), ArticleMode.Edit);
		if (mMoodArticle != null) {
			args.putSerializable(MoodArticle.class.getSimpleName(), mMoodArticle);
		}
		editFragment.setArguments(args);
		
		getFragmentManager().beginTransaction().setCustomAnimations(
				R.animator.card_flip_right_in, R.animator.card_flip_right_out,
                R.animator.card_flip_left_in, R.animator.card_flip_left_out)
           .replace(R.id.flayout_content, editFragment).addToBackStack(null).commit();
	}
	
	@Override
	public void queryArticle(CallBackFragmentMethods callBackFragmentMethods) {
		mCallBackFragmentMethods = callBackFragmentMethods;
		new QueryArticleTask().execute(mMoodArticle);    
	}
	
	@Override
	public void saveArticle(MoodArticle moodArticle) {
		new SaveArticleTask().execute(moodArticle);
	}
	
	/**
	 * 
	 * @Description: 查询Article
	 * @Author:hx Lu
	 * @Since:2014-8-29
	 */
	class QueryArticleTask extends AsyncTask<MoodArticle, Void, Void> {

		@Override
		protected Void doInBackground(MoodArticle... params) {
			String photoUri = params[0].getUri();
			Integer id = params[0].getId();
			DBHelper dbHelper = new DBHelper(MPApplication.getContext());
			Cursor cursor;
			if (id != null) {				
				cursor = dbHelper.query(DBTables.TABLE_MOOD_ARTICLE, null, "_id = ?", new String[] {id.toString()});
			} else {
				cursor = dbHelper.query(DBTables.TABLE_MOOD_ARTICLE, null, "uri = ?", new String[] {photoUri});
			}
			if (cursor.moveToFirst()) {				
				DBUtil.cursorToObject(cursor, mMoodArticle);
			}
			return null;
			    
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mCallBackFragmentMethods.viewArticle(mMoodArticle);
		}
	}
	
	
	/**
	 * 
	 * @Description: 保存Article
	 * @Author:hx Lu
	 * @Since:2014-8-29
	 */
	class SaveArticleTask extends AsyncTask<MoodArticle, Void, Void> {

		@Override
		protected Void doInBackground(MoodArticle... params) {
			
			MoodArticle moodArticle = params[0];
			DBHelper dbHelper = new DBHelper(MPApplication.getContext());
			ContentValues values = DBUtil.objectToContentValue(moodArticle, DBTables.TABLE_MOOD_ARTICLE, dbHelper);
			dbHelper.beginTransaction();
			try {
				if (moodArticle.getId() == null) {
					dbHelper.insert(DBTables.TABLE_MOOD_ARTICLE, values);
				} else {
					dbHelper.update(DBTables.TABLE_MOOD_ARTICLE, values, "_id = ?", new String[] {moodArticle.getId().toString()});
				}
				dbHelper.setTransactionSuccessful();
			} catch (Exception e) {
				Log.e("SaveArticleTask", e.getMessage()+"");   
			} finally {
				dbHelper.endTransaction();
			}
			
			return null;
			    
		}
		
		@Override
		protected void onPostExecute(Void result) {
			showToast("已自动保存文章内容。");  
		}
	}
	
	
}
