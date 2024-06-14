package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Calendar.CALENDAR;
import static com.esferalia.aon.jooq.tables.Holiday.HOLIDAY;
import static com.esferalia.aon.jooq.tables.HolidayDetail.HOLIDAY_DETAIL;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;

import java.sql.Connection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.shared.CalendarDraft;
import com.esferalia.aon.gwt.payroll.shared.CalendarDraft.DayType;
import com.esferalia.aon.gwt.payroll.shared.HolidayDraft;
import com.esferalia.aon.jooq.tables.records.HolidayDetailRecord;
import com.esferalia.aon.jooq.tables.records.HolidayRecord;
import com.esferalia.aon.jooq.tables.records.PayrollWorkplaceRecord;

public class JooqCalendar {

	private static Settings SETTINGS = null;
	
	// Festives listBox

	public static Map<Integer, String> getHolidayDescription(Connection conn, Integer parentDomain, Integer domain) throws IllegalArgumentException {
		return getHolidayDescription(DSL.using(conn, getDefaultSettings()), parentDomain, domain);
	}

	private static Map<Integer, String> getHolidayDescription(DSLContext dslContext, Integer parentDomain, Integer domain) throws IllegalArgumentException {
		List<HolidayRecord> aviableHolidays = dslContext.selectFrom(HOLIDAY)
				.where(HOLIDAY.DOMAIN.eq(0))
				.or(HOLIDAY.DOMAIN.eq(parentDomain))
				.or(HOLIDAY.DOMAIN.eq(domain))
				.orderBy(HOLIDAY.DESCRIPTION.asc()).fetchInto(HOLIDAY);
		
		Map<Integer, String> map = new TreeMap<Integer, String>();
		
		aviableHolidays.forEach(aviableHoliday -> {
			Integer id = aviableHoliday.getValue(HOLIDAY.ID);
			String description = aviableHoliday.getValue(HOLIDAY.DESCRIPTION);
			
			map.put(id, description);
		});

		return map;
	}
	
	// Get Calendar

	public static CalendarDraft getCalendar(Connection conn, Integer workplaceId, Integer selectedHoliday, Integer year) throws IllegalArgumentException {
		return selectedHoliday == null ? 
				getCalendar(DSL.using(conn, getDefaultSettings()), year, workplaceId) : // It's my first time here
				getCalendarSelectedHoliday(DSL.using(conn, getDefaultSettings()), selectedHoliday, year, workplaceId);
	}

	private static CalendarDraft getCalendar(DSLContext dslContext, Integer year, Integer workplaceId) throws IllegalArgumentException {

		CalendarDraft calendarDraft = new CalendarDraft();

		Result<Record> records = dslContext.select()
				.from(PAYROLL_WORKPLACE)
				.join(CALENDAR)
				.on(PAYROLL_WORKPLACE.CALENDAR.eq(CALENDAR.ID))
				.where(PAYROLL_WORKPLACE.WORKPLACE.eq(workplaceId))
				.orderBy(PAYROLL_WORKPLACE.ID.desc())
				.fetch();

		List<HolidayDraft> holidays = new LinkedList<HolidayDraft>();

		if (records.isNotEmpty()) {

			Record record = records.get(0);
			Integer calendar = record.getValue(PAYROLL_WORKPLACE.CALENDAR);

			if (calendar != null) {

				Integer holiday = record.getValue(CALENDAR.HOLIDAY);
				
				calendarDraft.setCalendarHoliday(holiday);

				while (holiday != null)
					holiday = loadHoliday(dslContext, year, holidays, holiday);
			}
			
			calendarDraft.setDayType(0, DayType.valueOf(record.getValue(CALENDAR.SUNDAY)));
			calendarDraft.setDayType(1, DayType.valueOf(record.getValue(CALENDAR.MONDAY)));
			calendarDraft.setDayType(2, DayType.valueOf(record.getValue(CALENDAR.TUESDAY)));
			calendarDraft.setDayType(3, DayType.valueOf(record.getValue(CALENDAR.WEDNESDAY)));
			calendarDraft.setDayType(4, DayType.valueOf(record.getValue(CALENDAR.THURSDAY)));
			calendarDraft.setDayType(5, DayType.valueOf(record.getValue(CALENDAR.FRIDAY)));
			calendarDraft.setDayType(6, DayType.valueOf(record.getValue(CALENDAR.SATURDAY)));
		}
		
		calendarDraft.setHolidayDrafts(holidays);
		
		return calendarDraft;
	}

	private static CalendarDraft getCalendarSelectedHoliday(DSLContext dslContext, Integer selectedHoliday, Integer year, Integer workplaceId) throws IllegalArgumentException {

		CalendarDraft calendarDraft = new CalendarDraft();
		
		Result<Record> payrollWokplaces = dslContext.select()
				.from(PAYROLL_WORKPLACE)
				.join(CALENDAR)
				.on(PAYROLL_WORKPLACE.CALENDAR.eq(CALENDAR.ID))
				.where(PAYROLL_WORKPLACE.WORKPLACE.eq(workplaceId))
				.orderBy(PAYROLL_WORKPLACE.ID.desc())
				.fetch();
		
		if (payrollWokplaces.isNotEmpty()) {

			Record payrollWokplace = payrollWokplaces.get(0);
			
			calendarDraft.setCalendarHoliday(selectedHoliday);
			
			Record record = dslContext.selectFrom(HOLIDAY)
					.where(HOLIDAY.ID.eq(selectedHoliday))
					.fetchOne();

			List<HolidayDraft> holidays = new LinkedList<HolidayDraft>();

			if (record != null) {

				Integer holiday = record.getValue(HOLIDAY.ID);
				calendarDraft.setCalendarHoliday(holiday);

				while (holiday != null)
					holiday = loadHoliday(dslContext, year, holidays, holiday);
			}
			
			calendarDraft.setDayType(0, DayType.valueOf(payrollWokplace.getValue(CALENDAR.SUNDAY)));
			calendarDraft.setDayType(1, DayType.valueOf(payrollWokplace.getValue(CALENDAR.MONDAY)));
			calendarDraft.setDayType(2, DayType.valueOf(payrollWokplace.getValue(CALENDAR.TUESDAY)));
			calendarDraft.setDayType(3, DayType.valueOf(payrollWokplace.getValue(CALENDAR.WEDNESDAY)));
			calendarDraft.setDayType(4, DayType.valueOf(payrollWokplace.getValue(CALENDAR.THURSDAY)));
			calendarDraft.setDayType(5, DayType.valueOf(payrollWokplace.getValue(CALENDAR.FRIDAY)));
			calendarDraft.setDayType(6, DayType.valueOf(payrollWokplace.getValue(CALENDAR.SATURDAY)));
			
			calendarDraft.setHolidayDrafts(holidays);
		}
		
		return calendarDraft;

	}

	private static Integer loadHoliday(DSLContext dslContext, Integer year, List<HolidayDraft> holidays, Integer holiday) throws IllegalArgumentException {

		HolidayRecord record = dslContext.selectFrom(HOLIDAY)
				.where(HOLIDAY.ID.eq(holiday)).fetchOne();

		Integer id = record.getValue(HOLIDAY.ID);
		Integer domain = record.getValue(HOLIDAY.DOMAIN);
		Integer parentHoliday = (record.getValue(HOLIDAY.HOLIDAY_) != null) ? record.getValue(HOLIDAY.HOLIDAY_) : -1;
		
		HolidayDraft draft = new HolidayDraft();
		draft.setDomain(domain);
		draft.setId(id);
		draft.setHoliday(parentHoliday);
		draft.setDescription(record.getValue(HOLIDAY.DESCRIPTION));

		loadHolidayDetail(dslContext, domain, year, id, draft);

		holidays.add(draft);

		// Return parent holiday
		return record.getValue(HOLIDAY.HOLIDAY_);
	}

	private static void loadHolidayDetail(DSLContext dslContext, Integer domain, Integer year, Integer holiday, HolidayDraft draft) throws IllegalArgumentException {
		List<HolidayDetailRecord> holidayDetails = dslContext
				.selectFrom(HOLIDAY_DETAIL)
				.where(HOLIDAY_DETAIL.HOLIDAY.eq(holiday))
				.and(HOLIDAY_DETAIL.DATE.like("%" + year + "%"))
				.orderBy(HOLIDAY_DETAIL.DATE.asc()).fetchInto(HOLIDAY_DETAIL);
		
		holidayDetails.forEach(holidayDetail -> {
			Date date = holidayDetail.getValue(HOLIDAY_DETAIL.DATE);
			String descrip = holidayDetail.getValue(HOLIDAY_DETAIL.DESCRIPTION);

			draft.addHoliday(date, descrip);
		});
	}
	
	// Update Holiday Calendar
	
	public static void updateHolidayCalendar(Connection conn, Integer domainID, int workplaceId, Integer holidayId, DayType daysTypes []) {
		DSLContext dslContext = DSL.using(conn, getDefaultSettings());

		Result<PayrollWorkplaceRecord> resultRecords = dslContext
				.selectFrom(PAYROLL_WORKPLACE)
				.where(PAYROLL_WORKPLACE.WORKPLACE.eq(workplaceId))
				.orderBy(PAYROLL_WORKPLACE.ID.desc())
				.fetch();
		
		if (!resultRecords.isEmpty()) {
			PayrollWorkplaceRecord payrollWorkplace = resultRecords.get(0);
			
			Integer calendar = payrollWorkplace.getCalendar();
			
			if(null == calendar) {
				dslContext.insertInto(CALENDAR)
					.set(CALENDAR.DOMAIN, domainID)
					.set(CALENDAR.HOLIDAY, holidayId)
					.set(CALENDAR.SUNDAY, (byte)daysTypes[0].ordinal())
					.set(CALENDAR.MONDAY, (byte)daysTypes[1].ordinal())
					.set(CALENDAR.TUESDAY, (byte)daysTypes[2].ordinal())
					.set(CALENDAR.WEDNESDAY, (byte)daysTypes[3].ordinal())
					.set(CALENDAR.THURSDAY, (byte)daysTypes[4].ordinal())
					.set(CALENDAR.FRIDAY, (byte)daysTypes[5].ordinal())
					.set(CALENDAR.SATURDAY, (byte)daysTypes[6].ordinal())
					.execute();
			} else {
				dslContext.update(CALENDAR)
					.set(CALENDAR.HOLIDAY, holidayId)
					.where(CALENDAR.ID.eq(calendar))
					.execute();
			}
		}
	}
	
	// Set Calendar

	public static void insertHolidays(Connection conn, Integer domain, Integer workplaceId, String holidayDescription,
			Integer selectedHoliday, Map<Date, String> map, DayType daysTypes [], Integer year) throws IllegalArgumentException {

		DSLContext dslContext = DSL.using(conn, getDefaultSettings());

		Result<PayrollWorkplaceRecord> payrollWorkplaces = dslContext
				.selectFrom(PAYROLL_WORKPLACE)
				.where(PAYROLL_WORKPLACE.WORKPLACE.eq(workplaceId))
				.orderBy(PAYROLL_WORKPLACE.ID.desc())
				.fetch();

		if (payrollWorkplaces.isEmpty()) { 
			// No hay registro del workplace en payroll_workplace
			insertHolidayWithoutCalendar(dslContext, domain, workplaceId, selectedHoliday, holidayDescription, map, daysTypes, year);
		} else { 
			// Hay un registro en payroll_workplace
				
			PayrollWorkplaceRecord result = payrollWorkplaces.get(0);
				
			Integer calendar = result.getValue(PAYROLL_WORKPLACE.CALENDAR);

			if (calendar == null) {
				//No hay calendario para el payroll_workplace

				HolidayRecord holiday = getHolidayRecord(dslContext, selectedHoliday);
				
				if(holiday == null) {
					// Si no existe holiday
					holiday = dslContext
							.insertInto(HOLIDAY)
							.set(HOLIDAY.DOMAIN, domain)
							.set(HOLIDAY.DESCRIPTION, holidayDescription)
							.returning().fetchOne();
				}
				
				if(holiday.getDomain().equals(domain)) {
					// Si el holiday es del mismo dominio donde se quiere grabar se actrualiza el calendario
					
					insertHolidayDetail(dslContext, domain, holiday.getId(), map, year);
					
				} else {
					// Si no son del mismo dominio, se crea una copia y se le asigna como parent el original
					
					holiday = dslContext
							.insertInto(HOLIDAY)
							.set(HOLIDAY.DOMAIN, domain)
							.set(HOLIDAY.DESCRIPTION, holidayDescription)
							.set(HOLIDAY.HOLIDAY_, holiday.getId())
							.returning().fetchOne();
					
					insertHolidayDetail(dslContext, domain, holiday.getId(), map, year);
				}
				
				Integer calendarId = 
					dslContext.insertInto(CALENDAR)
						.set(CALENDAR.DOMAIN, domain)
						.set(CALENDAR.HOLIDAY, holiday.getId())
						.set(CALENDAR.SUNDAY, (byte)daysTypes[0].ordinal())
						.set(CALENDAR.MONDAY, (byte)daysTypes[1].ordinal())
						.set(CALENDAR.TUESDAY, (byte)daysTypes[2].ordinal())
						.set(CALENDAR.WEDNESDAY, (byte)daysTypes[3].ordinal())
						.set(CALENDAR.THURSDAY, (byte)daysTypes[4].ordinal())
						.set(CALENDAR.FRIDAY, (byte)daysTypes[5].ordinal())
						.set(CALENDAR.SATURDAY, (byte)daysTypes[6].ordinal())
					.returning(CALENDAR.ID).fetchOne().getId();

				
				dslContext.update(PAYROLL_WORKPLACE)
						.set(PAYROLL_WORKPLACE.CALENDAR, calendarId)
						.where(PAYROLL_WORKPLACE.WORKPLACE.eq(workplaceId))
						.execute();
					
			} else { 
				// Calendar en Payroll_workplace no es NULL
				
				HolidayRecord holiday = getHolidayRecord(dslContext, selectedHoliday);
				
				if(holiday == null) {
					// Si no existe holiday
					holiday = dslContext
							.insertInto(HOLIDAY)
							.set(HOLIDAY.DOMAIN, domain)
							.set(HOLIDAY.DESCRIPTION, holidayDescription)
							.returning().fetchOne();
				}
				
				if(holiday.getDomain().equals(domain)) {
					// Si el holiday es del mismo dominio donde se quiere grabar se actrualiza el calendario
					
					insertHolidayDetail(dslContext, domain, holiday.getId(), map, year);
					
				} else {
					// Si no son del mismo dominio, se crea una copia y se le asigna como parent el original
					
					holiday = dslContext
							.insertInto(HOLIDAY)
							.set(HOLIDAY.DOMAIN, domain)
							.set(HOLIDAY.DESCRIPTION, holidayDescription)
							.set(HOLIDAY.HOLIDAY_, holiday.getId())
							.returning().fetchOne();
					
					insertHolidayDetail(dslContext, domain, holiday.getId(), map, year);
				}
				
				dslContext.update(CALENDAR)
					.set(CALENDAR.DOMAIN, domain)
					.set(CALENDAR.HOLIDAY, holiday.getId())
					.set(CALENDAR.SUNDAY, (byte)daysTypes[0].ordinal())
					.set(CALENDAR.MONDAY, (byte)daysTypes[1].ordinal())
					.set(CALENDAR.TUESDAY, (byte)daysTypes[2].ordinal())
					.set(CALENDAR.WEDNESDAY, (byte)daysTypes[3].ordinal())
					.set(CALENDAR.THURSDAY, (byte)daysTypes[4].ordinal())
					.set(CALENDAR.FRIDAY, (byte)daysTypes[5].ordinal())
					.set(CALENDAR.SATURDAY, (byte)daysTypes[6].ordinal())
					.where(CALENDAR.ID.eq(calendar))
					.execute();

			}
		}
	}

	private static HolidayRecord getHolidayRecord(DSLContext dslContext, Integer selectedHoliday) throws IllegalArgumentException {
		return dslContext.selectFrom(HOLIDAY)
				.where(HOLIDAY.ID.eq(selectedHoliday)).fetchOne();
	}

	private static void insertHolidayWithoutCalendar(DSLContext dslContext,
			Integer domain, Integer workplaceId, Integer selectedHoliday, String holidayDescription, Map<Date, String> map, DayType[] daysTypes, Integer year)
			throws IllegalArgumentException {

		HolidayRecord holiday = getHolidayRecord(dslContext, selectedHoliday);
		
		if(holiday == null) {
			// Si no existe holiday
			holiday = dslContext
					.insertInto(HOLIDAY)
					.set(HOLIDAY.DOMAIN, domain)
					.set(HOLIDAY.DESCRIPTION, holidayDescription)
					.returning()
					.fetchOne();
		}
		
		if(holiday.getDomain().equals(domain)) {
			// Si el holiday es del mismo dominio donde se quiere grabar se actrualiza el calendario
			
			insertHolidayDetail(dslContext, domain, holiday.getId(), map, year);
			
		} else {
			// Si no son del mismo dominio, se crea una copia y se le asigna como parent el original
			
			holiday = dslContext
					.insertInto(HOLIDAY)
					.set(HOLIDAY.DOMAIN, domain)
					.set(HOLIDAY.DESCRIPTION, holidayDescription)
					.set(HOLIDAY.HOLIDAY_, holiday.getId())
					.returning().fetchOne();
			
			insertHolidayDetail(dslContext, domain, holiday.getId(), map, year);
		}
		
		Integer calendarId = 
			dslContext.insertInto(CALENDAR)
				.set(CALENDAR.DOMAIN, domain)
				.set(CALENDAR.HOLIDAY, holiday.getId())
				.set(CALENDAR.SUNDAY, (byte)daysTypes[0].ordinal())
				.set(CALENDAR.MONDAY, (byte)daysTypes[1].ordinal())
				.set(CALENDAR.TUESDAY, (byte)daysTypes[2].ordinal())
				.set(CALENDAR.WEDNESDAY, (byte)daysTypes[3].ordinal())
				.set(CALENDAR.THURSDAY, (byte)daysTypes[4].ordinal())
				.set(CALENDAR.FRIDAY, (byte)daysTypes[5].ordinal())
				.set(CALENDAR.SATURDAY, (byte)daysTypes[6].ordinal())
			.returning(CALENDAR.ID).fetchOne().getId();

		
		dslContext.insertInto(PAYROLL_WORKPLACE)
			.set(PAYROLL_WORKPLACE.DOMAIN, domain)
			.set(PAYROLL_WORKPLACE.WORKPLACE, workplaceId)
			.set(PAYROLL_WORKPLACE.CALENDAR, calendarId)
			.execute();
	}

	private static void insertHolidayDetail(DSLContext dslContext,
			Integer domain, Integer holidayId, Map<Date, String> map, Integer year)
			throws IllegalArgumentException {
		
		// Delete all holiday details
		dslContext
			.delete(HOLIDAY_DETAIL)
			.where(HOLIDAY_DETAIL.HOLIDAY.eq(holidayId))
			.and(HOLIDAY_DETAIL.DATE.like("%" + year + "%"))
			.execute();
	
		for (Date date : map.keySet()) {
			dslContext
					.insertInto(HOLIDAY_DETAIL)
				.set(HOLIDAY_DETAIL.DOMAIN, domain)
				.set(HOLIDAY_DETAIL.HOLIDAY, holidayId)
				.set(HOLIDAY_DETAIL.DATE, new java.sql.Date(date.getTime()))
				.set(HOLIDAY_DETAIL.DESCRIPTION, map.get(date))
				.execute();
		}
	}
	
	public static void deletePropertyHoliday(Connection conn, Integer id, Date date) {
		deletePropertyHoliday(DSL.using(conn, getDefaultSettings()), id, date);
	}
	
	private static void deletePropertyHoliday(DSLContext dslContext, Integer id, Date date) throws IllegalArgumentException {
		
		dslContext.delete(HOLIDAY_DETAIL)
			.where(HOLIDAY_DETAIL.HOLIDAY.eq(id))
			.and(HOLIDAY_DETAIL.DATE.eq(new java.sql.Date(date.getTime())))
			.execute();
		
	}

	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
}
