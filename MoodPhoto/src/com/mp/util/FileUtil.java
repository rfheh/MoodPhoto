
/**  
 * @Description: TODO
 * @author hx Lu
 * @date 2014-8-26 上午10:34:01
 */ 
package com.mp.util;

import java.io.File;

/**
 * @Description: 
 * @Author:hx Lu
 * @Since:2014-8-26
 */

public class FileUtil {

	/**
	 * 
	 * @Description: 判断是否为特殊文件夹，eg：文件夹名称带'.',通常文件夹名称是不能带特殊符号"./-+"等
	 * @param: boolean 
	 * @throws
	 */
	public static boolean isLegalDirec(File direc) {
		return direc.getName().indexOf('.') == -1;
	}
}

    