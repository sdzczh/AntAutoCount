package com.ant.count.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @描述 日期时间工具<br>
 * @author 陈之晶
 * @版本 v1.0.0
 * @日期 2017-6-8
 */
public class DateUtils {

	private static final String CURRENT_DATE_STR = "yyyy-MM-dd";
	private static final String CURRENT_TIME_STR = "yyyy-MM-dd HH:mm:ss";

	public static Date getCurrentDate() {
		Date date = new Date();
		return date;
	}

	/**
	 * @描述 获取字符串格式的当前<br>
	 * @return XXXX-XX-XX XX:XX:XX
	 * @author 陈之晶
	 * @版本 v1.0.0
	 * @日期 2017-6-13
	 */
	public static String getCurrentTimeStr() {
		SimpleDateFormat format = new SimpleDateFormat(CURRENT_TIME_STR);
		return format.format(new Date());
	}

	/**
	 * @描述 获取字符串格式的当前<br>
	 * @return XXXX-XX-XX
	 * @author 李娜
	 * @版本 v1.0.0
	 * @日期 2017-6-17
	 */
	public static String getCurrentDateStr() {
		SimpleDateFormat format = new SimpleDateFormat(CURRENT_DATE_STR);
		return format.format(new Date());
	}

	/**
	 * @描述 字符串转时间 例：XXXX-XX-XX XX:XX:XX<br>
	 * @param str
	 * @return
	 * @author 陈之晶
	 * @版本 v1.0.0
	 * @日期 2017-6-17
	 */
	public static Date strToDate(String str) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * @描述 计算机两个时间相差天数<br>
	 * @param date
	 *            字符串格式日期时间,例：XXXX-XX-XX XX:XX:XX
	 * @return
	 * @author 陈之晶
	 * @版本 v1.0.0
	 * @日期 2017-6-17
	 */
	public static int daysBetween(String dateStr) {
		Date after = strToDate(dateStr);
		Calendar cal = Calendar.getInstance();
		Date before = cal.getTime();
		long afterMillis = after.getTime();
		long beforeMillis = before.getTime();
		long between_days = (beforeMillis - afterMillis) / (1000 * 3600 * 24);
		return Integer.parseInt(String.valueOf(between_days));
	}

	/**
	 * @描述 计算机两个时间相差小时数<br>
	 * @param date
	 *            字符串格式日期时间,例：XXXX-XX-XX XX:XX:XX
	 * @return
	 * @author 陈之晶
	 * @版本 v1.0.0
	 * @日期 2017-6-17
	 */
	public static int hoursBetween(String dateStr) {
		Date after = strToDate(dateStr);
		Calendar cal = Calendar.getInstance();
		Date before = cal.getTime();
		long afterMillis = after.getTime();
		long beforeMillis = before.getTime();
		long between_days = (beforeMillis - afterMillis) / (1000 * 3600);
		return Integer.parseInt(String.valueOf(between_days));
	}

	/**
	 * @描述 获得指定日期的前n天 <br>
	 * @param currentDateTime 字符串格式日期时间,例：XXXX-XX-XX XX:XX:XX
	 * @param n 天数 
	 * @author 陈之晶
	 * @版本 v1.0.0
	 * @日期 2017-6-29
	 */
	public static String getBeforDateTime(String currentDateTime,int n) {
		Calendar c = Calendar.getInstance();
		Date date = null;
		try {
			date = new SimpleDateFormat(CURRENT_DATE_STR).parse(currentDateTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day - n);

		String dayBefore = new SimpleDateFormat(CURRENT_DATE_STR).format(c
				.getTime());
		return dayBefore;
	}

	/**
	 * @描述 获得指定日期的后N天 <br>
	 * @param currentDateTime 字符串格式日期时间,例：XXXX-XX-XX XX:XX:XX
	 * @param n 天数 
	 * @return
	 * @author 陈之晶
	 * @版本 v1.0.0
	 * @日期 2017-6-29
	 */
	public static String getAfterDateTime(String currentDateTime,int n) {
		Calendar c = Calendar.getInstance();
		Date date = null;
		try {
			date = new SimpleDateFormat(CURRENT_DATE_STR).parse(currentDateTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day + n);

		String dayAfter = new SimpleDateFormat(CURRENT_DATE_STR)
				.format(c.getTime());
		return dayAfter;
	}

	public static void main(String[] args) {
	/**
		String str = "2017-06-16 10:45:02";
		System.out.println(daysBetween(str));
	*/
		String currentDateTime = getCurrentDateStr();
		
		String beforeDateTime = getBeforDateTime(currentDateTime, 1);
		
		String afterDateTime = getAfterDateTime(currentDateTime, 1);
		
		System.out.println("昨天:"+beforeDateTime);
		System.out.println("今天:"+currentDateTime);
		System.out.println("明天:"+afterDateTime);
	}

}
