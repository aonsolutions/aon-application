package com.esferalia.aon.payroll.calculator.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.code.aon.common.enumeration.WeekDay;
import com.esferalia.aon.calendar.enumeration.DayType;
import com.esferalia.aon.payroll.calculator.LRUCacheFactory;
import com.esferalia.aon.payroll.sql.SQLConstants.CalendarColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.CalendarHolidayColumns;

public class SQLCalendarFactory implements LRUCacheFactory<Integer, ICalendar> {
	
	
	private static class DefaultCalendar implements ICalendar
	{
		
		private Map<Date, DayType> days = 
			new HashMap<Date, DayType>();
		
		private DayType week [] = new DayType [7];
		
		@Override
		public DayType getDayType(Calendar day) {
			DayType type = days.get(day.getTime()) ;
			return type != null ? type : week[day.get(Calendar.DAY_OF_WEEK)-1];
		}
		
		protected void add ( Date day, DayType type ) {
			days.put(day, type);
		}

		protected void set ( int dayOfWeeek, DayType type ) {
			week[dayOfWeeek-1] = type;
		}
		
		private static ICalendar DEFAULT_CALENDAR = 
			new DefaultCalendar();
	}
	
	private static final String CALENDAR_SQL = "SELECT * "
		+" FROM calendar"
		+" WHERE id = ? ";
	
	private static final String HOLIDAY_SQL = "SELECT * "
		+" FROM calendar_holiday"
		+" WHERE calendar = ?"
		+" AND date <= ? "
		+" AND date >= ? ";
	
	
	private PreparedStatement calendarStmt;
	private PreparedStatement holidayStmt;
	
	
	public SQLCalendarFactory(Connection connection, Date startDate, Date endDate) 
	throws SQLException {
		initCalendarStmt(connection, startDate, endDate);
		initHolidaysStmt(connection, startDate, endDate);
	}
	
	
	@Override
	public ICalendar create(Integer calendarId) {
		if ( calendarId == null ){
			return DefaultCalendar.DEFAULT_CALENDAR;
		}
		DefaultCalendar calendar = 
			new DefaultCalendar();
		
		try {
			loadCalendar(calendarId, calendar);
			loadHolidays( calendarId, calendar);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
		return calendar;
	}
	
	// ------------------------------------------
	
	
	private void loadCalendar(Integer calendarId, DefaultCalendar calendar ) 
		throws SQLException {
		ResultSet rs = null;
		try  {
			calendarStmt.setInt(1, calendarId );
			rs = calendarStmt.executeQuery();
			while ( rs.next() ) {
				calendar.set(Calendar.MONDAY, int2DayType(rs.getInt(CalendarColumns.MONDAY)) );
				calendar.set(Calendar.TUESDAY, int2DayType(rs.getInt(CalendarColumns.TUESDAY)) );
				calendar.set(Calendar.WEDNESDAY, int2DayType(rs.getInt(CalendarColumns.WEDNESDAY)) );
				calendar.set(Calendar.THURSDAY, int2DayType(rs.getInt(CalendarColumns.THURSDAY)) );
				calendar.set(Calendar.FRIDAY, int2DayType(rs.getInt(CalendarColumns.FRIDAY)) );
				calendar.set(Calendar.SATURDAY, int2DayType(rs.getInt(CalendarColumns.SATURDAY)) );
				calendar.set(Calendar.SUNDAY, int2DayType(rs.getInt(CalendarColumns.SUNDAY)) );
			}
		}
		finally {
			if ( rs != null )
				rs.close();
		}
	}

	private void loadHolidays( Integer calendarId, DefaultCalendar calendar ) 
	throws SQLException {
		ResultSet rs = null;
		try  {
			holidayStmt.setInt(1, calendarId );
			rs = holidayStmt.executeQuery();
			while ( rs.next() ) {
				Date day = rs.getDate(CalendarHolidayColumns.DATE);
				int type = rs.getInt(CalendarHolidayColumns.DAY_TYPE);
				calendar.add(day, DayType.values()[type]);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
		}
	}

	private void initCalendarStmt(Connection connection, Date startDate, Date endDate)
	throws SQLException {
		this.calendarStmt  = 
			connection.prepareStatement(CALENDAR_SQL);
	}
	
	private void initHolidaysStmt(Connection connection, Date startDate, Date endDate)
	throws SQLException {
		this.holidayStmt  = 
			connection.prepareStatement(HOLIDAY_SQL);
		this.holidayStmt.setDate(2, new java.sql.Date(endDate.getTime()) );
		this.holidayStmt.setDate(3, new java.sql.Date(startDate.getTime()) );
	}
	
	
	private static DayType int2DayType(int ordinal ) {
		return DayType.values()[ordinal];
	}

}
