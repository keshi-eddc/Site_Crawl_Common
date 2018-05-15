package fun.jerry.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

public class DateFormatSupport {
	
	public static final String YYYY_MM_DD = "yyyy-MM-dd";

	public static final String YYYYMMDD = "yyyyMMdd";

	public static final String YYYY_MM = "yyyy-MM";

	public static final String YYYY = "yyyy";
	
	public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	
	public static final String YYYY_MM_DD_HH_MM_SS_2 = "yyyyMMddHHmmss";
	
	public static String dateFormat(String pattern, Date date) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}
	
	public static boolean before (String source, String pattern, Date date) {
		return dateFormat(pattern, source).before(date);
	}
	
	public static Date dateFormat(String pattern, String date) {
		Date rtnDate = null;
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		try {
			rtnDate = format.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return rtnDate;
	}
	
	/**
	 * 获取距离当天指定的某一天
	 * @param pattern
	 * @return
	 */
	public static String getSpecifyDay(int year, int month, int day, String pattern) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + year);
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + month);
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + day);
		return DateFormatUtils.format(calendar, pattern);
	}
	
	public static Date getSpecifyDay(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + year);
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + month);
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + day);
		return calendar.getTime();
	}
	
	/**
	 * 返回两个日期范围内的所有日期 日期格式为 YYYY-MM-DD格式
	 * @param startDate
	 * @param startIncluded 开始日期是否包含
	 * @param endDate
	 * @param endIncluded 结束日期是否包含
	 * @return
	 */
	public static List<String> getIntervalDate(String startDate, boolean startIncluded, String endDate, boolean endIncluded) {
		List<String> dateList = new ArrayList<String>();
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		try {
			c1.setTime(DateUtils.parseDate(startDate, YYYY_MM_DD));
			c2.setTime(DateUtils.parseDate(endDate, YYYY_MM_DD));
			while (true) {
				if (endIncluded && c1.after(c2)) {
					break;
				}
				if (!endIncluded && DateFormatUtils.format(c1, YYYY_MM_DD).equals(DateFormatUtils.format(c2, YYYY_MM_DD))) {
					break;
				}
				//如果包含开始日期，先放list中在加一天
				if (startIncluded) {
					dateList.add(DateFormatUtils.format(c1.getTime(), YYYY_MM_DD));
					c1.add(Calendar.DATE, 1);
				} else {
					//如果不包含开始日期，先加一天，在放到list中
					c1.add(Calendar.DATE, 1);
					dateList.add(DateFormatUtils.format(c1.getTime(), YYYY_MM_DD));
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateList;
	}
	
	/**
	 * 返回两个日期范围内的所有日期 日期格式为 YYYY-MM-DD格式
	 * @param startMonth
	 * @param startIncluded 开始是否包含
	 * @param endMonth
	 * @param endIncluded 结束是否包含
	 * @return
	 */
	public static List<String> getIntervalMonth(String startMonth, boolean startIncluded, String endMonth, boolean endIncluded) {
		List<String> dateList = new ArrayList<String>();
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		try {
			c1.setTime(DateUtils.parseDate(startMonth, YYYY_MM));
			c2.setTime(DateUtils.parseDate(endMonth, YYYY_MM));
			while (true) {
				if (endIncluded && c1.after(c2)) {
					break;
				}
				if (!endIncluded && DateFormatUtils.format(c1, YYYY_MM).equals(DateFormatUtils.format(c2, YYYY_MM))) {
					break;
				}
				//如果包含开始日期，先放list中在加一个月
				if (startIncluded) {
					dateList.add(DateFormatUtils.format(c1.getTime(), YYYY_MM));
					c1.add(Calendar.MONTH, 1);
				} else {
					//如果不包含开始日期，先加一个月，在放到list中
					c1.add(Calendar.MONTH, 1);
					dateList.add(DateFormatUtils.format(c1.getTime(), YYYY_MM));
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateList;
	}
	
	/**
	 * 返回两个年份范围内的所有年份 日期格式为 YYYY格式
	 * @param startYear
	 * @param startIncluded 开始是否包含
	 * @param endYear
	 * @param endIncluded 结束是否包含
	 * @return
	 */
	public static List<String> getIntervalYear(String startYear, boolean startIncluded, String endYear, boolean endIncluded) {
		List<String> yearList = new ArrayList<String>();
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		try {
			c1.setTime(DateUtils.parseDate(startYear, YYYY));
			while (true) {
				if (endIncluded && c1.after(c2)) {
					break;
				}
				if (!endIncluded && DateFormatUtils.format(c1, YYYY).equals(DateFormatUtils.format(c2, YYYY))) {
					break;
				}
				//如果包含开始日期，先放list中在加一天
				if (startIncluded) {
					yearList.add(DateFormatUtils.format(c1.getTime(), YYYY));
					c1.add(Calendar.YEAR, 1);
				} else {
					//如果不包含开始日期，先加一天，在放到list中
					c1.add(Calendar.YEAR, 1);
					yearList.add(DateFormatUtils.format(c1.getTime(), YYYY));
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return yearList;
	}
	
	public static void main(String[] args) {
//		Calendar c = Calendar.getInstance();
//		System.out.println(dateFormat("yyyy-MM-dd", c.getTime()) + " 00:00:00");
//		c.add(Calendar.DATE, -1);
//		System.out.println(dateFormat("yyyy-MM-dd", c.getTime()) + " 00:00:00");
////		c.add(Calendar.DATE, -1);
//		System.out.println(dateFormat("yyyy-MM-dd HH:mm:ss", c.getTime()));
//		List<String> abc = getIntervalDate("2015-01-01", true, "2016-03-03", true);
//		
//		System.out.println(new Date(1457019905000L));
//		
//		try {
//			Date date = DateUtils.parseDate("2016-03-3 23:45:05", "yyyy-MM-dd HH:mm:ss");
//			System.out.println(date.getTime());
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		DateFormatSupport.getIntervalYear("2014", true, "2016", true);
		
		System.out.println(getIntervalMonth("2011-04", true, "2016-05", false));
		
	}
}
