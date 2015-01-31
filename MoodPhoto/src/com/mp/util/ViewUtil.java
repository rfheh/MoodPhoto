package com.mp.util;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;

public class ViewUtil {

	public static int getSpinnerItemResId() {
		if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
			return android.R.layout.simple_spinner_item;
		} else {
			return android.R.layout.simple_list_item_1;
		}
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
}
