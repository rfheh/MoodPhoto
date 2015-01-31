package com.mp.util;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;

import com.mp.entity.MediaStoreBucket;

public class MediaStoreCursorHelper {

	public static final String[] PHOTOS_PROJECTION = {Images.Media._ID,
        Images.Media.MINI_THUMB_MAGIC, Images.Media.DATE_ADDED,
        Images.Media.DATA, Images.Media.BUCKET_DISPLAY_NAME, Images.Media.BUCKET_ID};
	
	public static final String PHOTOS_ORDER_BY = Images.Media.BUCKET_DISPLAY_NAME + " desc";

	public static final Uri MEDIA_STORE_CONTENT_URI = Images.Media.EXTERNAL_CONTENT_URI;

	public static Cursor openPhotoCursor(Context context, Uri contentUri) {
		return context.getContentResolver().query(contentUri, PHOTOS_PROJECTION, null, null, PHOTOS_ORDER_BY);
	}
	
	public static Uri photosCursorToSelection(Uri contentUri, Cursor cursor) {
		Uri uri = null;
		try {
			File file = new File(cursor.getString(cursor.getColumnIndexOrThrow(ImageColumns.DATA)));
			if (file.exists()) {
				int currCursorRow_ID = cursor.getInt(cursor.getColumnIndexOrThrow(ImageColumns._ID));
				uri = Uri.withAppendedPath(contentUri, String.valueOf(currCursorRow_ID));
				uri = Uri.fromFile(file);
			}
		} catch (Exception e) {
			uri = null;
		}
		return uri;
	}
	
	public static void photosCursorToBucketList(Cursor cursor, List<MediaStoreBucket> items) {
		final HashSet<String> bucketIds = new HashSet<String>();
		
		final int idColumn = cursor.getColumnIndex(ImageColumns.BUCKET_ID);
		final int nameColumn = cursor.getColumnIndex(ImageColumns.BUCKET_DISPLAY_NAME);
		
		if (cursor.moveToFirst()) {
			do {
				try {
					final String bucketId = cursor.getString(idColumn);
					if (bucketIds.add(bucketId)) {
						items.add(new MediaStoreBucket(bucketId, cursor.getString(nameColumn)));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} while (cursor.moveToNext());
		}
	}
}
