package com.esferalia.aon.payroll.calculator.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.calendar.enumeration.DayType;
import com.esferalia.aon.payroll.calculator.LRUCache;
import com.esferalia.aon.payroll.calculator.LRUCacheFactory;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.CalendarColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.CalendarHolidayColumns;

public class SQLCalendarFactory implements LRUCacheFactory<Integer, ICalendar> {
	
	
	private static class DefaultCalendar implements ICalendar
	{
		
		private Map<Date, DayType> days = 
			new HashMap<Date, DayType>();
		
		private DayType week [] = {
				DayType.NOT_WORKING_DAY, 	// SUNDAY
				DayType.WORKING_DAY,		// MONDAY
				DayType.WORKING_DAY,		// TUESDAY
				DayType.WORKING_DAY,		// WEDNESDAY
				DayType.WORKING_DAY,		// THURSDAY
				DayType.WORKING_DAY,		// FRIDAY
				DayType.NOT_WORKING_DAY		// SATURDAY
		};
		
		private DefaultCalendar parent = null;
		
		@Override
		public DayType getDayType(Calendar day) {
			DayType type = days.get(day.getTime()) ;
			if (  type != null )
				return type;
			
			if ( parent != null ){
				type = parent.days.get(day.getTime()); 
				if ( type != null )
					return type ;
			}
			return week[day.get(Calendar.DAY_OF_WEEK)-1];
		}
		
		@Override
		public DayType getWeekDayType(int weekDay) {
			return week[weekDay-1];
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
	
	private static final String CALENDAR_HOLIDAY_SQL = 
			"SELECT *"
			+" FROM  calendar "
			+" INNER JOIN holiday ON ( calendar.holiday = holiday.id )"
			+" INNER JOIN holiday_detail ON ( holiday.id = holiday_detail.holiday )"
			+" WHERE calendar.id =  ? "
			+" AND holiday_detail.date <= ?"
			+" AND holiday_detail.date >= ?"
			;	

	private static final String HOLIDAY_HOLIDAY_SQL = 
			"SELECT *"
			+" FROM  holiday_detail "
			+" INNER JOIN holiday ON ( holiday_detail.holiday = holiday.id )"
			+" WHERE holiday.id =  ? "
			+" AND holiday_detail.date <= ?"
			+" AND holiday_detail.date >= ?"
			;	

	private PreparedStatement calendarStmt;
	private PreparedStatement calendarHolidayStmt;
	private PreparedStatement holidayHolidayStmt;
	private LRUCache<Integer, ICalendar> cache;
	
	
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
			loadHolidays( calendarId, calendar);
			loadCalendar(calendarId, calendar);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
		return calendar;
	}

	public void setCache(LRUCache<Integer, ICalendar> cache) {
		this.cache = cache;
	}
	
	public void close() 
	throws SQLException {
		if ( calendarStmt != null ) {
			calendarStmt.close();
			calendarStmt = null;
		}
		if ( calendarHolidayStmt != null ) {
			calendarHolidayStmt.close();
			calendarHolidayStmt = null;
		}
		if ( holidayHolidayStmt != null ) {
			holidayHolidayStmt.close();
			holidayHolidayStmt = null;
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
	
	// ------------------------------------------
	
	
	private void loadCalendar(Integer calendarId, DefaultCalendar calendar ) 
		throws SQLException {
		Integer parent = null;
		ResultSet rs = null;
		try  {
			calendarStmt.setInt(1, calendarId );
			rs = calendarStmt.executeQuery();
			while ( rs.next() ) {
				parent = ( Integer ) rs.getObject(CalendarColumns.CALENDAR);
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
		calendar.parent = ( DefaultCalendar ) cache.get(parent);
	}

	private void loadHolidays( Integer calendarId, DefaultCalendar calendar ) 
	throws SQLException {
		ResultSet rs = null;
		try  {
			Integer holidayId = null;
			calendarHolidayStmt.setInt(1, calendarId );
			rs = calendarHolidayStmt.executeQuery();
			while ( rs.next() ) {
				Date day = rs.getDate(SQLConstants.HolidayDetailColumns.DATE);
				calendar.add(day, DayType.HOLIDAY);
				holidayId = (Integer) rs.getObject(SQLConstants.HOLIDAY +"." + SQLConstants.HolidayColumns.HOLIDAY);
			}
			rs.close();
			rs = null;

			while ( holidayId != null ) {
				holidayHolidayStmt.setInt(1, holidayId);
				rs = holidayHolidayStmt.executeQuery();
				while ( rs.next() ) {
					Date day = rs.getDate(SQLConstants.HolidayDetailColumns.DATE);
					calendar.add(day, DayType.HOLIDAY);
					holidayId = (Integer) rs.getObject(SQLConstants.HOLIDAY +"." + SQLConstants.HolidayColumns.HOLIDAY);
				}
				rs.close();
				rs = null;
			}
			
		}
		catch ( Exception e ) {
			e.printStackTrace();
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
		this.calendarHolidayStmt  = 
			connection.prepareStatement(CALENDAR_HOLIDAY_SQL);
		this.calendarHolidayStmt.setDate(2, new java.sql.Date(endDate.getTime()) );
		this.calendarHolidayStmt.setDate(3, new java.sql.Date(startDate.getTime()) );
		
		this.holidayHolidayStmt  = 
				connection.prepareStatement(HOLIDAY_HOLIDAY_SQL);
		this.holidayHolidayStmt.setDate(2, new java.sql.Date(endDate.getTime()) );
		this.holidayHolidayStmt.setDate(3, new java.sql.Date(startDate.getTime()) );
	}
	
	
	private static DayType int2DayType(int ordinal ) {
		return DayType.values()[ordinal];
	}



}
