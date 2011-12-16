package com.esferalia.aon.calendar.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.esferalia.aon.calendar.Calendar;
import com.esferalia.aon.calendar.CalendarHoliday;
import com.esferalia.aon.calendar.CalendarPeriod;
import com.esferalia.aon.calendar.Holiday;
import com.esferalia.aon.calendar.HolidayDetail;

/** 
* Interface for holding entity properties constants.
*/ 
public interface ICalendarAlias {



	/** 
	* DAOConstantsEntry for Calendar entity.
	*/ 
	DAOConstantsEntry CALENDAR_ENTRY = DAOConstants.getDAOConstant(Calendar.class);

	/** 
	* Alias value: Calendar_anualHours
	* Hibernate value: Calendar.anualHours
	*/
	String  CALENDAR_ANUAL_HOURS = CALENDAR_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Calendar_calendar_id
	* Hibernate value: Calendar.calendar.id
	*/
	String  CALENDAR_CALENDAR_ID = CALENDAR_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Calendar_comments
	* Hibernate value: Calendar.comments
	*/
	String  CALENDAR_COMMENTS = CALENDAR_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Calendar_description
	* Hibernate value: Calendar.description
	*/
	String  CALENDAR_DESCRIPTION = CALENDAR_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Calendar_friday
	* Hibernate value: Calendar.friday
	*/
	String  CALENDAR_FRIDAY = CALENDAR_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Calendar_fridayHours
	* Hibernate value: Calendar.fridayHours
	*/
	String  CALENDAR_FRIDAY_HOURS = CALENDAR_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Calendar_generic
	* Hibernate value: Calendar.generic
	*/
	String  CALENDAR_GENERIC = CALENDAR_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Calendar_calendar_holiday_id
	* Hibernate value: Calendar.holiday.id
	*/
	String  CALENDAR_CALENDAR_HOLIDAY_ID = CALENDAR_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Calendar_id
	* Hibernate value: Calendar.id
	*/
	String  CALENDAR_ID = CALENDAR_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Calendar_monday
	* Hibernate value: Calendar.monday
	*/
	String  CALENDAR_MONDAY = CALENDAR_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Calendar_mondayHours
	* Hibernate value: Calendar.mondayHours
	*/
	String  CALENDAR_MONDAY_HOURS = CALENDAR_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Calendar_saturday
	* Hibernate value: Calendar.saturday
	*/
	String  CALENDAR_SATURDAY = CALENDAR_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Calendar_saturdayHours
	* Hibernate value: Calendar.saturdayHours
	*/
	String  CALENDAR_SATURDAY_HOURS = CALENDAR_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Calendar_sunday
	* Hibernate value: Calendar.sunday
	*/
	String  CALENDAR_SUNDAY = CALENDAR_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Calendar_sundayHours
	* Hibernate value: Calendar.sundayHours
	*/
	String  CALENDAR_SUNDAY_HOURS = CALENDAR_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Calendar_thursday
	* Hibernate value: Calendar.thursday
	*/
	String  CALENDAR_THURSDAY = CALENDAR_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Calendar_thursdayHours
	* Hibernate value: Calendar.thursdayHours
	*/
	String  CALENDAR_THURSDAY_HOURS = CALENDAR_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Calendar_tuesday
	* Hibernate value: Calendar.tuesday
	*/
	String  CALENDAR_TUESDAY = CALENDAR_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Calendar_tuesdayHours
	* Hibernate value: Calendar.tuesdayHours
	*/
	String  CALENDAR_TUESDAY_HOURS = CALENDAR_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Calendar_wednesday
	* Hibernate value: Calendar.wednesday
	*/
	String  CALENDAR_WEDNESDAY = CALENDAR_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: Calendar_wednesdayHours
	* Hibernate value: Calendar.wednesdayHours
	*/
	String  CALENDAR_WEDNESDAY_HOURS = CALENDAR_ENTRY.getAliasNames()[20];



	/** 
	* DAOConstantsEntry for CalendarHoliday entity.
	*/ 
	DAOConstantsEntry CALENDAR_HOLIDAY_ENTRY = DAOConstants.getDAOConstant(CalendarHoliday.class);

	/** 
	* Alias value: CalendarHoliday_calendar_id
	* Hibernate value: CalendarHoliday.calendar.id
	*/
	String  CALENDAR_HOLIDAY_CALENDAR_ID = CALENDAR_HOLIDAY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CalendarHoliday_date
	* Hibernate value: CalendarHoliday.date
	*/
	String  CALENDAR_HOLIDAY_DATE = CALENDAR_HOLIDAY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CalendarHoliday_dayType
	* Hibernate value: CalendarHoliday.dayType
	*/
	String  CALENDAR_HOLIDAY_DAY_TYPE = CALENDAR_HOLIDAY_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: CalendarHoliday_description
	* Hibernate value: CalendarHoliday.description
	*/
	String  CALENDAR_HOLIDAY_DESCRIPTION = CALENDAR_HOLIDAY_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: CalendarHoliday_hours
	* Hibernate value: CalendarHoliday.hours
	*/
	String  CALENDAR_HOLIDAY_HOURS = CALENDAR_HOLIDAY_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: CalendarHoliday_id
	* Hibernate value: CalendarHoliday.id
	*/
	String  CALENDAR_HOLIDAY_ID = CALENDAR_HOLIDAY_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for CalendarPeriod entity.
	*/ 
	DAOConstantsEntry CALENDAR_PERIOD_ENTRY = DAOConstants.getDAOConstant(CalendarPeriod.class);

	/** 
	* Alias value: CalendarPeriod_calendar_id
	* Hibernate value: CalendarPeriod.calendar.id
	*/
	String  CALENDAR_PERIOD_CALENDAR_ID = CALENDAR_PERIOD_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CalendarPeriod_description
	* Hibernate value: CalendarPeriod.description
	*/
	String  CALENDAR_PERIOD_DESCRIPTION = CALENDAR_PERIOD_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CalendarPeriod_endDay
	* Hibernate value: CalendarPeriod.endDay
	*/
	String  CALENDAR_PERIOD_END_DAY = CALENDAR_PERIOD_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: CalendarPeriod_friday
	* Hibernate value: CalendarPeriod.friday
	*/
	String  CALENDAR_PERIOD_FRIDAY = CALENDAR_PERIOD_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: CalendarPeriod_fridayHours
	* Hibernate value: CalendarPeriod.fridayHours
	*/
	String  CALENDAR_PERIOD_FRIDAY_HOURS = CALENDAR_PERIOD_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: CalendarPeriod_id
	* Hibernate value: CalendarPeriod.id
	*/
	String  CALENDAR_PERIOD_ID = CALENDAR_PERIOD_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: CalendarPeriod_monday
	* Hibernate value: CalendarPeriod.monday
	*/
	String  CALENDAR_PERIOD_MONDAY = CALENDAR_PERIOD_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: CalendarPeriod_mondayHours
	* Hibernate value: CalendarPeriod.mondayHours
	*/
	String  CALENDAR_PERIOD_MONDAY_HOURS = CALENDAR_PERIOD_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: CalendarPeriod_month
	* Hibernate value: CalendarPeriod.month
	*/
	String  CALENDAR_PERIOD_MONTH = CALENDAR_PERIOD_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: CalendarPeriod_saturday
	* Hibernate value: CalendarPeriod.saturday
	*/
	String  CALENDAR_PERIOD_SATURDAY = CALENDAR_PERIOD_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: CalendarPeriod_saturdayHours
	* Hibernate value: CalendarPeriod.saturdayHours
	*/
	String  CALENDAR_PERIOD_SATURDAY_HOURS = CALENDAR_PERIOD_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: CalendarPeriod_startDay
	* Hibernate value: CalendarPeriod.startDay
	*/
	String  CALENDAR_PERIOD_START_DAY = CALENDAR_PERIOD_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: CalendarPeriod_sunday
	* Hibernate value: CalendarPeriod.sunday
	*/
	String  CALENDAR_PERIOD_SUNDAY = CALENDAR_PERIOD_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: CalendarPeriod_sundayHours
	* Hibernate value: CalendarPeriod.sundayHours
	*/
	String  CALENDAR_PERIOD_SUNDAY_HOURS = CALENDAR_PERIOD_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: CalendarPeriod_thursday
	* Hibernate value: CalendarPeriod.thursday
	*/
	String  CALENDAR_PERIOD_THURSDAY = CALENDAR_PERIOD_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: CalendarPeriod_thursdayHours
	* Hibernate value: CalendarPeriod.thursdayHours
	*/
	String  CALENDAR_PERIOD_THURSDAY_HOURS = CALENDAR_PERIOD_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: CalendarPeriod_tuesday
	* Hibernate value: CalendarPeriod.tuesday
	*/
	String  CALENDAR_PERIOD_TUESDAY = CALENDAR_PERIOD_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: CalendarPeriod_tuesdayHours
	* Hibernate value: CalendarPeriod.tuesdayHours
	*/
	String  CALENDAR_PERIOD_TUESDAY_HOURS = CALENDAR_PERIOD_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: CalendarPeriod_wednesday
	* Hibernate value: CalendarPeriod.wednesday
	*/
	String  CALENDAR_PERIOD_WEDNESDAY = CALENDAR_PERIOD_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: CalendarPeriod_wednesdayHours
	* Hibernate value: CalendarPeriod.wednesdayHours
	*/
	String  CALENDAR_PERIOD_WEDNESDAY_HOURS = CALENDAR_PERIOD_ENTRY.getAliasNames()[19];



	/** 
	* DAOConstantsEntry for Holiday entity.
	*/ 
	DAOConstantsEntry HOLIDAY_ENTRY = DAOConstants.getDAOConstant(Holiday.class);

	/** 
	* Alias value: Holiday_description
	* Hibernate value: Holiday.description
	*/
	String  HOLIDAY_DESCRIPTION = HOLIDAY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Holiday_editable
	* Hibernate value: Holiday.editable
	*/
	String  HOLIDAY_EDITABLE = HOLIDAY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Holiday_holiday_id
	* Hibernate value: Holiday.holiday.id
	*/
	String  HOLIDAY_HOLIDAY_ID = HOLIDAY_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Holiday_id
	* Hibernate value: Holiday.id
	*/
	String  HOLIDAY_ID = HOLIDAY_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for HolidayDetail entity.
	*/ 
	DAOConstantsEntry HOLIDAY_DETAIL_ENTRY = DAOConstants.getDAOConstant(HolidayDetail.class);

	/** 
	* Alias value: HolidayDetail_date
	* Hibernate value: HolidayDetail.date
	*/
	String  HOLIDAY_DETAIL_DATE = HOLIDAY_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: HolidayDetail_description
	* Hibernate value: HolidayDetail.description
	*/
	String  HOLIDAY_DETAIL_DESCRIPTION = HOLIDAY_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: HolidayDetail_holiday_id
	* Hibernate value: HolidayDetail.holiday.id
	*/
	String  HOLIDAY_DETAIL_HOLIDAY_ID = HOLIDAY_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: HolidayDetail_id
	* Hibernate value: HolidayDetail.id
	*/
	String  HOLIDAY_DETAIL_ID = HOLIDAY_DETAIL_ENTRY.getAliasNames()[3];


}