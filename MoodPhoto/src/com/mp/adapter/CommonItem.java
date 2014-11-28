
/**  
 * @Description: TODO
 * @author 
 * @date 2014-8-21 上午11:34:36
 * @version V1.0  
 */ 
package com.mp.adapter;

import java.io.Serializable;

import android.support.v4.app.Fragment;

/**
 * @Description: 
 * @Author:Administrator
 * @Since:2014-8-21
 * @Version:1.1.0
 */

public class CommonItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private String title;
	private Class<?> clazz;		//Activity class
	private Fragment fragment;  //Fragment
	
	public String getTitle() {
	
		return title;
	}
	public void setTitle(String title) {
	
		this.title = title;
	}
	public Class<?> getClazz() {
	
		return clazz;
	}
	public void setClazz(Class<?> clazz) {
	
		this.clazz = clazz;
	}
	public Fragment getFragment() {
	
		return fragment;
	}
	public void setFragment(Fragment fragment) {
	
		this.fragment = fragment;
	}
}

    