
/**  
 * @Description: TODO
 * @author hx Lu
 * @date 2014-8-26 下午4:02:25
 */ 
package com.mp.entity;

import android.text.TextUtils;

import com.mp.adapter.CommonItem;

/**
 * @Description: 文章类
 * @Author:hx Lu
 * @Since:2014-8-26
 */

public class MoodArticle extends CommonItem {

	private static final long serialVersionUID = 1L;
	private static final int SHORT_ARTICLE_LENGTH = 70;
	
	private Integer id;				//主键
	private String uri;			//相片SD卡路径
	private String article;		//文章内容
	private int readTimes;		//阅读次数
	private String date;		//日期 yyyy-MM-dd HH:mm:ss
	
	public MoodArticle() {
		super();
	}
	
	public MoodArticle(String uri) {
		super();
		this.uri = uri;
	}

	public MoodArticle(Integer id, String uri, String article, int readTimes,
			String date) {
		super();
		this.id = id;
		this.uri = uri;
		this.article = article;
		this.readTimes = readTimes;
		this.date = date;
	}

	public String getShortArticle() {
		if(TextUtils.isEmpty(article) || article.length() <= SHORT_ARTICLE_LENGTH) return this.article;
		return this.article.substring(0, SHORT_ARTICLE_LENGTH);
	}
	
	public String[] getSpiltDate() {
		return this.date.split("-");
	}
	
	public Integer getId() {
	
		return id;
	}

	public void setId(Integer id) {
	
		this.id = id;
	}

	public String getUri() {
	
		return uri;
	}

	public void setUri(String uri) {
	
		this.uri = uri;
	}

	public String getArticle() {
	
		return article;
	}

	public void setArticle(String article) {
	
		this.article = article;
	}

	public int getReadTimes() {
	
		return readTimes;
	}

	public void setReadTimes(int readTimes) {
	
		this.readTimes = readTimes;
	}

	public String getDate() {
	
		return date;
	}

	public void setDate(String date) {
	
		this.date = date;
	}
	
	public String getYear() {
		if(this.date == null) return null;
		return null;
	}
	
}

    