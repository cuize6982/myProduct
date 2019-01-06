package com.lzz.aspp.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	/**
	 * <p>获取两个时间点的年份差</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年8月7日
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static Integer getDifferYear(Date startDate, Date endDate){
		if(null == startDate || null == endDate){
			return 0;
		}
		Calendar beginCalendar = Calendar.getInstance();  
        Calendar endCalendar = Calendar.getInstance();
        beginCalendar.setTime(startDate);  
        endCalendar.setTime(endDate);
        return endCalendar.get(Calendar.YEAR) - beginCalendar.get(Calendar.YEAR);
	}
}
