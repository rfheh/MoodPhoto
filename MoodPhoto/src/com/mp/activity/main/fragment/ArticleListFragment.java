
/**  
 * @Description: TODO
 * @author hx Lu
 * @date 2014-8-27 下午2:16:36
 */ 
package com.mp.activity.main.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.mp.R;
import com.mp.activity.BaseActivity;
import com.mp.activity.article.MoodArticleActivity;
import com.mp.adapter.CommonAdapter;
import com.mp.application.MPApplication;
import com.mp.common.AsyncTask;
import com.mp.db.DBHelper;
import com.mp.db.DBTables;
import com.mp.db.DBUtil;
import com.mp.entity.MoodArticle;

/**
 * @Description: 
 * @Author:hx Lu
 * @Since:2014-8-27
 */

public class ArticleListFragment extends Fragment implements OnItemClickListener {

	private static ArticleListFragment mFragment;
	
	View mProgressContainerLayout, mListContainerLayout, mEmptyTv;
	ListView mListView;
	CommonAdapter<MoodArticle> mAdapter;
	
	public static ArticleListFragment getInstance() {
		return mFragment == null ? new ArticleListFragment() : mFragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		 
		mAdapter = new ArticleListAdapter(getActivity(), null);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View content = inflater.inflate(R.layout.layout_list_content, container, false);
		mProgressContainerLayout = content.findViewById(R.id.progressContainer);
		mListContainerLayout = content.findViewById(R.id.listContainer);
		mListView = (ListView) content.findViewById(android.R.id.list);
		mEmptyTv = content.findViewById(android.R.id.empty);
		
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		
		return content;
		    
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = BaseActivity.newIntent(MoodArticleActivity.class);
		intent.putExtra(MoodArticle.class.getSimpleName(), (MoodArticle)parent.getItemAtPosition(position));
		startActivity(intent);
	}
	
	public void onResume() {
		
		super.onResume();
		new LoadArticleTask().execute();
	};
	
	//true加载中  false加载完成
	private void loadStatusOfViews(boolean status) {
		mProgressContainerLayout.setVisibility(status ? View.VISIBLE : View.GONE);
		mListContainerLayout.setVisibility(!status ? View.VISIBLE : View.GONE);
	}
	
	class LoadArticleTask extends AsyncTask<Void, Void, List<MoodArticle>> {

		@Override
		protected void onPreExecute() {
			
			super.onPreExecute();
			loadStatusOfViews(true);
			    
		}
		
		@Override
		protected List<MoodArticle> doInBackground(Void... params) {
			
			List<MoodArticle> articles = new ArrayList<MoodArticle>();
			DBHelper dbHelper = new DBHelper(MPApplication.getContext());
			Cursor cursor = dbHelper.query(DBTables.TABLE_MOOD_ARTICLE, null, null, null);
			if (cursor.moveToFirst()) {
				do {
					MoodArticle moodArticle = new MoodArticle();
					DBUtil.cursorToObject(cursor, moodArticle);
					moodArticle.setArticle(moodArticle.getShortArticle());
					articles.add(moodArticle);
				} while (cursor.moveToNext());
			}
			return articles;
			    
		}
		
		@Override
		protected void onPostExecute(List<MoodArticle> result) {
			loadStatusOfViews(false);
			if (result == null ||result.isEmpty()) {
				mEmptyTv.setVisibility(View.VISIBLE);
			}
			mAdapter.setItems(result); 
		}
	}
	
	class ArticleListAdapter extends CommonAdapter<MoodArticle> {

		public ArticleListAdapter(Context context, List<MoodArticle> items) {
			
			super(context, items);
			    
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_article_list_item, parent, false);
				holder = new Holder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			MoodArticle item = getItem(position);
			String[] spiltDate = item.getSpiltDate();
			holder.yearTv.setText(spiltDate[0]);
			holder.shortArticleTv.setText(item.getShortArticle());
			holder.dateTv.setText(spiltDate[1] + "-" + spiltDate[2]);
			holder.readTimesTv.setText(String.valueOf(item.getReadTimes()));
			return convertView;
		}
		
		class Holder {
			private TextView yearTv, shortArticleTv, dateTv, readTimesTv;
			
			public Holder(View convertView) {
				yearTv = (TextView) convertView.findViewById(R.id.tv_year);
				shortArticleTv = (TextView) convertView.findViewById(R.id.tv_short_article);
				dateTv = (TextView) convertView.findViewById(R.id.tv_date);
				readTimesTv = (TextView) convertView.findViewById(R.id.tv_read_times);
			}
		}
	}
}

    