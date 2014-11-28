
/**  
 * @Description: TODO
 * @author hx Lu
 * @date 2014-8-28 下午2:51:03
 */ 
package com.mp.activity.article.fragment;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.mp.R;
import com.mp.activity.article.MoodArticleActivity.CallBackFragmentMethods;
import com.mp.entity.ArticleText;
import com.mp.entity.MoodArticle;
import com.mp.util.DateUtil;

/**
 * @Description: 
 * @Author:hx Lu
 * @Since:2014-8-28
 */

public class ArticleDetailFragment extends Fragment implements CallBackFragmentMethods{

	ArticleMode mMode = ArticleMode.View;
	public enum ArticleMode {
		View, Edit;
	}
	
	ArticleDetailMethods mArticleDetailMethods;
	public interface ArticleDetailMethods {
		public void queryArticle(CallBackFragmentMethods callBackFragmentMethods);
		public void saveArticle(MoodArticle moodArticle);
	}
	
	MoodArticle mMoodArticle;
	SaveingTextWatcher mSaveingTextWatcher;
	
	View mContentView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if (bundle != null) {
			mMode = (ArticleMode) bundle.getSerializable(ArticleMode.class.getSimpleName());
			mMoodArticle = (MoodArticle) bundle.getSerializable(MoodArticle.class.getSimpleName());
		}
		mArticleDetailMethods = (ArticleDetailMethods) getActivity();
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		switch (mMode) {
		case Edit:
			mContentView = inflater.inflate(R.layout.layout_mood_article_edit, container, false);
			break;
		case View:
		default:
			mContentView = inflater.inflate(R.layout.layout_mood_article_view, container, false);
			break;
		}
		return mContentView;
	};
	
	@Override
	public void onResume() {
		
		super.onResume();
		if (mMoodArticle == null) {			
			mArticleDetailMethods.queryArticle(this);    
		} else {
			viewArticle(mMoodArticle);
		}
	}
	
	@Override
	public void viewArticle(MoodArticle moodArticle) {
		mMoodArticle = moodArticle;
		
		if(TextUtils.isEmpty(mMoodArticle.getArticle()))
			mMoodArticle.setArticle(ArticleText.getRadionArticle());
		
		switch (mMode) {
		case Edit:
			EditText editText = (EditText) mContentView;
			editText.setText(mMoodArticle.getArticle());
			mSaveingTextWatcher = new SaveingTextWatcher();
			editText.addTextChangedListener(mSaveingTextWatcher);
			break;
		case View:
		default:
			TextView textView = (TextView) mContentView.findViewById(R.id.tv_article);
			textView.setText(mMoodArticle.getArticle());
			
			break;
		}
	}
	
	/**
	 * @Description: 当内容修改时，等待10秒后自动保存到DB
	 * @Author:hx Lu
	 * @Since:2014-8-29
	 */
	class SaveingTextWatcher implements TextWatcher {

		private boolean isTextChanged = false;
		private Timer mWaitingTimer;
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			isTextChanged = false;
			cancelTimer();
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			isTextChanged = true;
			cancelTimer();
		}

		@Override
		public void afterTextChanged(Editable s) {
			if(!isTextChanged) return;
			
			final String text = TextUtils.isEmpty(s) ? "" : s.toString();
			mMoodArticle.setArticle(text);
			mMoodArticle.setDate(DateUtil.getTodyStr());
			
			cancelTimer();
			mWaitingTimer = new Timer();
			mWaitingTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					mArticleDetailMethods.saveArticle(mMoodArticle);
				}
			}, 10000);
			
			isTextChanged = false;
		}
		
		public void cancelTimer() {
			if (mWaitingTimer == null) return;
			mWaitingTimer.cancel();
			mWaitingTimer.purge();
		}
	}
}


    