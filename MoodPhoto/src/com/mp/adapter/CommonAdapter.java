
/**  
 * @Description: TODO
 * @author 
 * @date 2014-8-21 上午11:33:57
 * @version V1.0  
 */ 
package com.mp.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @Description: 
 * @Author:Administrator
 * @Since:2014-8-21
 * @Version:1.1.0
 */

public class CommonAdapter<T> extends BaseAdapter {

	protected Context mContext;
	protected List<T> mItems;
	
	public CommonAdapter(Context context, List<T> items) {
		this.mContext = context;
		buildItems(items);
	}
	
	private void buildItems(List<T> items) {
		if (items == null || items.isEmpty()) {
			this.mItems = new ArrayList<T>();
		} else {			
			this.mItems = items;
		}
	}
	
	@Override
	public int getCount() {
		
		return mItems.size();
		    
	}

	@Override
	public T getItem(int position) {
		
		return mItems.get(position);
		    
	}

	@Override
	public long getItemId(int position) {
		
		return position;
		    
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		return null;
		    
	}

	public void setItems(List<T> items) {
		buildItems(items);
		notifyDataSetChanged();
	}
}

    