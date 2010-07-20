package com.esferalia.aon.calendar.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.esferalia.aon.calendar.Calendar;
import com.esferalia.aon.calendar.CalendarHoliday;
import com.esferalia.aon.calendar.CalendarPeriod;


public class CalendarAliasWriter {
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File file = new File("/AON-TRUNK/aon-calendar/src/main/java/com/esferalia/aon/calendar/dao/ICalendarAlias.java");
		String[] classes = new String[3]; 
		classes[0] = Calendar.class.getName();
		classes[1] = CalendarHoliday.class.getName();
		classes[2] = CalendarPeriod.class.getName();
		HibernateUtil.getSessionFactory(null);
		AliasWriter writer = new AliasWriter("com.esferalia.aon.calendar.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}