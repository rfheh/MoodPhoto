package com.mp.activity.main.fragment;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public class SpeedScrollListener implements OnScrollListener {

	private long mCurrTime;
	private int mScrollState;
	private double mSpeed = 0.0D;
	private long mPreviousEventTime, mTimeToScrollOneElement;
	private int mPreviousFirstVisibleItem;
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		mScrollState = scrollState;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (mPreviousFirstVisibleItem != firstVisibleItem) {
			mCurrTime = System.currentTimeMillis();
			mTimeToScrollOneElement = mCurrTime - mPreviousEventTime;
			mSpeed = 1000D * (1D / Math.max(1000D, mTimeToScrollOneElement));
			mPreviousFirstVisibleItem = firstVisibleItem;
			mPreviousEventTime = mCurrTime;
		}
	}

	public double getSpeed() {
		return mSpeed;
	}
	
	public boolean isFlingScrolling() {
		return mScrollState == 2;
	}
}
