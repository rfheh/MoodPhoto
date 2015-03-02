package com.mp.util;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

public class PhotoCursorLoader extends CursorLoader {

	private final boolean mRequeryOnChange;
	
	public PhotoCursorLoader(Context context, Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder, boolean requeryOnChange) {
		super(context, uri, projection, selection, selectionArgs, sortOrder);
		mRequeryOnChange = requeryOnChange;
	}

	@Override
	public void onContentChanged() {
		if(mRequeryOnChange)
			super.onContentChanged();
	}
}
