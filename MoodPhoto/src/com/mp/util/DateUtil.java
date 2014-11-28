
/**  
 * @Description: TODO
 * @author hx Lu
 * @date 2014-8-29 下午3:07:28
 */ 
package com.mp.util;

import android.annotation.SuppressLint;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @Description: 
 * @Author:hx Lu
 * @Since:2014-8-29
 */

@SuppressLint("SimpleDateFormat")
public class DateUtil {

	private static final String DATE_FORMATE_1 = "yyyy-MM-dd"; 
	private static final String DATE_FORMATE_2 = "yyyy-MM-dd HH:mm:ss"; 
	
	private static DateFormat dateFormat = new SimpleDateFormat(DATE_FORMATE_1);
	private static DateFormat dateFormat2 = new SimpleDateFormat(DATE_FORMATE_2);
	private static GregorianCalendar calendar = new GregorianCalendar();
	
	public static Date getToday() {
		return calendar.getTime();
	}
	
	public static String getTodyStr() {
		return formateDate(calendar.getTime());
	}
	
	public static String getOtherDayStr(int days) {
		calendar.add(GregorianCalendar.DATE, days);
		return formateDate(calendar.getTime());
	}
	
	public static String formateDate(Date date) {
		return dateFormat.format(date);
	}
	
	public static String formateDate(long milliseconds) {
		return dateFormat.format(new Date(milliseconds));
	}
	
	public static String formateDateTime(long milliseconds) {
		return dateFormat2.format(new Date(milliseconds));
	}
	
	/** 
	 *  
	 * @param 要转换的毫秒数 
	 * @return 该毫秒数转换为 * days * hours * minutes * seconds 后的格式 
	 * @author fy.zhang 
	 */  
	public static String formatDuring(long mss) {  
	    long days = mss / (1000 * 60 * 60 * 24);  
	    long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);  
	    long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);  
	    long seconds = (mss % (1000 * 60)) / 1000;  
	    return days + " days " + hours + " hours " + minutes + " minutes "  
	            + seconds + " seconds ";  
	}  
}

    