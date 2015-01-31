package com.mp.task;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;

import com.mp.common.AsyncTask;
import com.mp.entity.MediaStoreBucket;
import com.mp.util.MediaStoreCursorHelper;

public class MediaStoreBucketAsyncTask extends AsyncTask<Void, Void, List<MediaStoreBucket>> {

	private WeakReference<Context> mContext;
	private WeakReference<MediaStoreBucketsResultListener> mListener;
	
	public static interface MediaStoreBucketsResultListener {
		public void onBucketsLoaded(List<MediaStoreBucket> buckets);
	}
	
	private MediaStoreBucketAsyncTask(Context context, MediaStoreBucketsResultListener listener) {
		mContext = new WeakReference<Context>(context);
		mListener = new WeakReference<MediaStoreBucketAsyncTask.MediaStoreBucketsResultListener>(listener);
	}
	
	public static void excute(Context context, MediaStoreBucketsResultListener listener) {
		new MediaStoreBucketAsyncTask(context, listener).execute();
	}
	
	@Override
	protected List<MediaStoreBucket> doInBackground(Void... params) {
		List<MediaStoreBucket> result = null;
		Context context = mContext.get();
		
		if (null != context) {
			result = new ArrayList<MediaStoreBucket>();
			result.add(MediaStoreBucket.newAllPhotosBucket());
			
			Cursor cursor = MediaStoreCursorHelper.openPhotoCursor(context, MediaStoreCursorHelper.MEDIA_STORE_CONTENT_URI);
			if (null != cursor) {
				MediaStoreCursorHelper.photosCursorToBucketList(cursor, result);
				cursor.close();
			}
		}
		return result;
		
	}

	@Override
	protected void onPostExecute(List<MediaStoreBucket> result) {
		
		super.onPostExecute(result);
		
		MediaStoreBucketsResultListener listener = mListener.get();
		if (null != listener) {
			listener.onBucketsLoaded(result);
		}
	}
}
