package com.esferalia.aon.gwt.payroll.shared;

import java.util.Date;

import com.esferalia.aon.watson.util.AonStringUtils;

final class Shared {


	protected static Date parse(String str) {
		if ( str == null )
			return null;
		int year = Integer.parseInt(str.substring(0, 4));
		int month = Integer.parseInt(str.substring(4, 6));
		int day = Integer.parseInt(str.substring(6,8));
		return new Date(year, month, day);
	}

	protected static String format(Date date) {
		if ( date == null )
			return null;
		
		int year = date.getYear();
		int month = date.getMonth();
		int day =  date.getDate();
		return AonStringUtils.leftPad(Integer.toString(year), 4 , '0') + 
				AonStringUtils.leftPad(Integer.toString(month), 2, '0')+ 
				AonStringUtils.leftPad(Integer.toString(day), 2, '0');
	}


}