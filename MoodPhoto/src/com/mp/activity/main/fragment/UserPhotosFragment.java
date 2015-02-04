package com.mp.activity.main.fragment;

import java.util.ArrayList;
import java.util.List;

import com.etsy.android.grid.StaggeredGridView;
import com.mp.R;
import com.mp.constant.PreferenceConstants;
import com.mp.entity.MediaStoreBucket;
import com.mp.task.MediaStoreBucketAsyncTask;
import com.mp.task.MediaStoreBucketAsyncTask.MediaStoreBucketsResultListener;
import com.mp.util.MediaStoreCursorHelper;
import com.mp.util.ViewUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore.Images;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

public class UserPhotosFragment extends Fragment implements OnItemClickListener,
			OnItemSelectedListener, MediaStoreBucketsResultListener, LoaderCallbacks<Cursor>,
			OnScanCompletedListener {

	static final int LOADER_USER_PHOTOS_EXTERNAL = 0x01;
	
	private SharedPreferences mPrefs;
	private UserPhotosCursorAdapter mPhotoAdapter;
	private ArrayAdapter<MediaStoreBucket> mBucketAdapter;
	private StaggeredGridView mPhotosSGV;
	private Spinner mBucketSpinner;
	
	private List<MediaStoreBucket> mBuckets = new ArrayList<MediaStoreBucket>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		
		mPhotoAdapter = new UserPhotosCursorAdapter(getActivity(), null);
		mBucketAdapter = new ArrayAdapter<MediaStoreBucket>(getActivity(), ViewUtil.getSpinnerItemResId(), mBuckets);
		mBucketAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_grid_content, container, false);
		
		mPhotosSGV = (StaggeredGridView) view.findViewById(R.id.sgv);
		mPhotosSGV.setAdapter(mPhotoAdapter);
		mPhotosSGV.setOnItemClickListener(this);
		
		mBucketSpinner = (Spinner) view.findViewById(R.id.sp_buckets);
		mBucketSpinner.setOnItemSelectedListener(this);
		mBucketSpinner.setAdapter(mBucketAdapter);
		mBucketSpinner.setVisibility(View.VISIBLE);
		
		return view;
		
	}
	
	@Override
	public void onStart() {
		
		super.onStart();
		MediaStoreBucketAsyncTask.excute(getActivity(), this);
	}
	
	@Override
	public void onResume() {
		
		super.onResume();
		ImageLoader.getInstance().resume();
	}
	
	@Override
	public void onPause() {
		
		super.onPause();
		ImageLoader.getInstance().stop();
	}
	
	@Override
	public void onDestroy() {
		
		super.onDestroy();
		ImageLoader.getInstance().clearMemoryCache();
		ImageLoader.getInstance().clearDiskCache();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		MediaStoreBucket item = (MediaStoreBucket) parent.getItemAtPosition(position);
		if (null != item) {
			loadBucketId(item.getId());
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}
	
	@Override
	public void onBucketsLoaded(List<MediaStoreBucket> buckets) {
		if (null != buckets && !buckets.isEmpty()) {
			mBuckets.clear();
			mBuckets.addAll(buckets);
			mBucketAdapter.notifyDataSetChanged();
			setSelectedBucketFromPrefs();
		}
	}
	
	@Override
	public void onScanCompleted(String path, Uri uri) {
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		CursorLoader cursorLoader = null;
		
		switch (arg0) {
		case LOADER_USER_PHOTOS_EXTERNAL:
			String selection = null;
			String[] selectionArgs = null;
			if (null != arg1 && arg1.containsKey(Images.Media.BUCKET_ID)) {
				selection = Images.Media.BUCKET_ID + " = ?";
				selectionArgs = new String[]{arg1.getString(Images.Media.BUCKET_ID)};
			}
			
			cursorLoader = new CursorLoader(getActivity(), MediaStoreCursorHelper.MEDIA_STORE_CONTENT_URI,
					MediaStoreCursorHelper.PHOTOS_PROJECTION, selection, selectionArgs, 
					MediaStoreCursorHelper.PHOTOS_ORDER_BY);
			break;

		default:
			break;
		}
		return cursorLoader;
		
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		switch (arg0.getId()) {
		case LOADER_USER_PHOTOS_EXTERNAL:
			mPhotoAdapter.swapCursor(arg1);
			mPhotosSGV.setSelection(0);
			break;

		default:
			break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		switch (arg0.getId()) {
		case LOADER_USER_PHOTOS_EXTERNAL:
			mPhotoAdapter.swapCursor(null);
			break;

		default:
			break;
		}
	}
	
	private void setSelectedBucketFromPrefs() {
		if (null != mBucketSpinner) {
			int newSelection = 0;
			
			if (null != mPrefs) {
				final String lastBucketId = mPrefs.getString(PreferenceConstants.PREF_SELECTED_MEDIA_BUCKET_ID, null);
				if (null != lastBucketId) {
					for (int i = 0; i < mBuckets.size(); i++) {
						if (lastBucketId.equals(mBuckets.get(i).getId())) {
							newSelection = i;
							break;
						}
					}
				}
			}
			
			// If we have a new position, then just set it. If it's our current
            // position, then call onItemSelected manually
			if (newSelection != mBucketSpinner.getSelectedItemPosition()) {
				mBucketSpinner.setSelection(newSelection);
			} else {
				onItemSelected(mBucketSpinner, null, newSelection, 0);
			}
		}
	}
	
	private void loadBucketId(String id) {
		if (isAdded()) {
			Bundle bundle = new Bundle();
			if (null != id) {
				bundle.putString(Images.Media.BUCKET_ID, id);
			}
			getLoaderManager().restartLoader(LOADER_USER_PHOTOS_EXTERNAL, bundle, this);
		}
	}

}
