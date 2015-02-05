
/**  
 * @Description: TODO
 * @author hx Lu
 * @date 2014-8-27 下午2:15:31
 */ 
package com.mp.activity.main.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mp.R;
import com.mp.adapter.CommonAdapter;
import com.mp.common.AsyncTask;
import com.mp.common.ZoomImageFromThumbWorker;
import com.mp.entity.ArticleText;
import com.mp.entity.MoodArticle;
import com.mp.util.BitmapUtil;
import com.mp.util.DateUtil;
import com.mp.util.FileUtil;
import com.mp.util.MediaFileTypeUtil;
import com.mp.util.WindowScreenUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

/**
 * @Description: 
 * @Author:hx Lu
 * @Since:2014-8-27
 */

public class PhotoListFragment extends Fragment implements OnItemClickListener {

	View mProgressContainerLayout, mListContainerLayout, mEmptyTv, expandededParentView, expandededView;
	ListView mListView;
	ImageView expendedIv;
	TextView mReadCountTv,  mArticleTv;
	CommonAdapter<MoodArticle> mAdapter;
	
	DisplayImageOptions mOptions;
	
	ZoomImageFromThumbWorker mZoomImageFromThumbWorker;
	
	//DiskImageFatcher mImageFatcher;
	//ImageCacheParams mCacheParams;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		mAdapter = new PhotoListAdapter(getActivity(), null);
		
		mOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.empty_photo)
				.cacheInMemory(true)
				.cacheInMemory(true)
				.considerExifParams(true)
				.displayer(new BitmapDisplayer() {
					
					@Override
					public void display(Bitmap bitmap, ImageAware imageAware,
							LoadedFrom loadedFrom) {
						float scale = (float)WindowScreenUtil.getScreenWidth(getActivity()) / WindowScreenUtil.getScreenHeight(getActivity()) * 3;
						imageAware.setImageBitmap(BitmapUtil.cutMatrixBitmap(bitmap, scale));
					}
					
				})
				.build();
		
		
		/*mCacheParams = new ImageCacheParams(getActivity());
		mCacheParams.setMemCacheSizePercent(0.5f); // Set memory cache to 50% of app memory
		
		int imageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
		mImageFatcher = new DiskImageFatcher(getActivity(), imageThumbSize, imageThumbSize);
		mImageFatcher.setLoadingImage(R.drawable.empty_photo);
		mImageFatcher.addImageCache(getActivity().getSupportFragmentManager(), mCacheParams);
		*/
		
		
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
		mListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == SCROLL_STATE_FLING) 
					ImageLoader.getInstance().pause();
				else 
					ImageLoader.getInstance().resume();
				//mImageFatcher.setPauseWork(scrollState == SCROLL_STATE_FLING);
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
		
		expandededParentView = mListContainerLayout;
		expandededView = content.findViewById(R.id.rl_expended);
		expendedIv = (ImageView) content.findViewById(R.id.iv_expanded);
		
		mReadCountTv = (TextView) content.findViewById(R.id.tv_count);
		mArticleTv = (TextView) content.findViewById(R.id.tv_article);
		
		String DCIM = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
		new LoadSDCardPhotoTask().execute(DCIM);
		
		return content;
		    
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		/*Intent intent = BaseActivity.newIntent(MoodArticleActivity.class);
		intent.putExtra(MoodArticle.class.getSimpleName(), (MoodArticle)parent.getItemAtPosition(position));
		startActivity(intent);*/
		MoodArticle moodArticle = (MoodArticle) parent.getItemAtPosition(position);
		mReadCountTv.setText(moodArticle.getReadTimes() + "");
		mArticleTv.setText(ArticleText.getRadionArticle());
		
		
		View thumbView = view.findViewById(R.id.iv_photo);
		if (mZoomImageFromThumbWorker == null) {
			mZoomImageFromThumbWorker = new ZoomImageFromThumbWorker(thumbView, expendedIv, expandededView, expandededParentView, moodArticle.getUri());
		} else {
			mZoomImageFromThumbWorker.resetImageUri(thumbView, moodArticle.getUri());
		}
		mZoomImageFromThumbWorker.expandedImageFromThumb();
		//ZoomImageFromThumbWorker.zoomImageFromThumb(thumbView, expendedIv, expandededView, expandededParentView, moodArticle.getUri());
	}
	
	@Override
	public void onResume() {
		
		super.onResume();
		ImageLoader.getInstance().resume();
		//mImageFatcher.setExitTasksEarly(false);

	}
	
	@Override
	public void onPause() {
		
		super.onPause();
		ImageLoader.getInstance().stop();
		//mImageFatcher.setExitTasksEarly(true);
		//mImageFatcher.flushCache();
	}
	
	@Override
	public void onDestroy() {
		
		super.onDestroy();
		ImageLoader.getInstance().clearMemoryCache();
		ImageLoader.getInstance().clearDiskCache();
		if (mZoomImageFromThumbWorker != null) {
			mZoomImageFromThumbWorker.closeImageToThumb();
		}
		//mImageFatcher.closeCache(); 
	}
	
	//true加载中  false加载完成
	private void loadStatusOfViews(boolean status) {
		mProgressContainerLayout.setVisibility(status ? View.VISIBLE : View.GONE);
		mListContainerLayout.setVisibility(!status ? View.VISIBLE : View.GONE);
	}
	
	class LoadSDCardPhotoTask extends AsyncTask<String, Void, List<MoodArticle>> {

		@Override
		protected void onPreExecute() {
			loadStatusOfViews(true);  
		}
		
		@Override
		protected List<MoodArticle> doInBackground(String... params) {
			List<MoodArticle> photoItems = new ArrayList<MoodArticle>();
			if (params != null && params.length > 0) {
				for (String filePath : params) {
					File photoFile = new File(filePath);
					readFiles(photoFile, photoItems);
				}
			}
			return photoItems;
		}
		
		private void readFiles(File parentFile, List<MoodArticle> photoItems) {
			
			if (!parentFile.exists() || !FileUtil.isLegalDirec(parentFile)) return; 
			
			File[] files = parentFile.listFiles();
			if (files != null && files.length > 0) {
				for (File file : files) {
					if (file.isDirectory()) { //文件夹
						readFiles(file, photoItems);
					} else {
						if (MediaFileTypeUtil.isImageFileType(file.getPath())) {							
							MoodArticle item = new MoodArticle();
							item.setUri("file:///" + file.getPath());
							item.setDate(DateUtil.formateDateTime(file.lastModified()));
							photoItems.add(item);
						}
					}
				}
			}
		}
		
		@Override
		protected void onPostExecute(List<MoodArticle> result) {
			loadStatusOfViews(false);
			mAdapter.setItems(result);
			if (result == null || result.isEmpty()) {
				mEmptyTv.setVisibility(View.VISIBLE);
			}
		}
	}
	
	class PhotoListAdapter extends CommonAdapter<MoodArticle> {

		private Typeface mTypeface;
		
		public PhotoListAdapter(Context context, List<MoodArticle> items) {
			
			super(context, items);
			mTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/00TT.TTF");    
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_photo_list_item, parent, false);
				holder = new Holder(convertView);
				int h = WindowScreenUtil.getScreenHeight(mContext) / 3;
				holder.photoIv.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, h));
				FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) holder.markIv.getLayoutParams();
				int dime = (int)getResources().getDimension(R.dimen.image_header_size);
				if(lp == null) lp = new FrameLayout.LayoutParams(dime, dime);
				lp.setMargins(getResources().getDimensionPixelOffset(R.dimen.ui_element_spacing), -dime/2, 0, 0);
				holder.markIv.setLayoutParams(lp);
				holder.dateTv.setTypeface(mTypeface);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			
			holder.markIv.setImageResource(R.drawable.icon_heart_blue);
			holder.dateTv.setText(getItem(position).getDate());
			//ImageView photoIv = holder.photoIv;
			ImageLoader.getInstance().displayImage(getItem(position).getUri(), holder.photoIv, mOptions);
			/*mImageFatcher.setOnResizeBitmapListener(new OnResizeBitmapListener() {
				@Override
				public Bitmap onResizeBitmap(Bitmap src) {
					float scale = (float)WindowScreenUtil.getScreenWidth(mContext) / WindowScreenUtil.getScreenHeight(mContext) * 3;
					return BitmapUtil.cutMatrixBitmap(src, scale);
				}
			});
			mImageFatcher.loadImage(getItem(position).getUri(), photoIv);*/
			return convertView;
		}
		
		class Holder {
			private ImageView photoIv, markIv;
			private TextView dateTv;
			
			public Holder(View convertView) {
				this.photoIv = (ImageView) convertView.findViewById(R.id.iv_photo);
				this.markIv = (ImageView) convertView.findViewById(R.id.iv_mark);
				this.dateTv = (TextView) convertView.findViewById(R.id.tv_date);
			}
		}
	}
}

    