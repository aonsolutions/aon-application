package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Calendar.CALENDAR;
import static com.esferalia.aon.jooq.tables.Holiday.HOLIDAY;
import static com.esferalia.aon.jooq.tables.HolidayDetail.HOLIDAY_DETAIL;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;
import static java.util.Calendar.FRIDAY;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.THURSDAY;
import static java.util.Calendar.TUESDAY;
import static java.util.Calendar.WEDNESDAY;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
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

	public static Map<Integer, String> getHolidayDescription(Connection conn,
			Integer parentDomain, Integer domain) throws IllegalArgumentException {

		return getHolidayDescription(DSL.using(conn, getDefaultSettings()), parentDomain,
				domain);
	}

	private static Map<Integer, String> getHolidayDescription(
			DSLContext dslContext, Integer parentDomain, Integer domain)
			throws IllegalArgumentException {

		List<HolidayRecord> result = dslContext.selectFrom(HOLIDAY)
				.where(HOLIDAY.DOMAIN.eq(0))
				//.or(HOLIDAY.DOMAIN.eq(parentDomain))
				//.or(HOLIDAY.DOMAIN.eq(domain))
				.orderBy(HOLIDAY.DESCRIPTION.asc()).fetchInto(HOLIDAY);

		Map<Integer, String> map = new TreeMap<Integer, String>();

		if (result != null) {

			for (HolidayRecord item : result) {
				Integer id = item.getValue(HOLIDAY.ID);
				String description = item.getValue(HOLIDAY.DESCRIPTION);
				
				map.put(id, description);
			}
		}

		return map;
	}

	public static CalendarDraft getCalendar(Connection conn,
			Integer workplaceId, Integer pattern, Integer year)
			throws IllegalArgumentException {

		if (pattern == null) {
			// It's my first time here
			return getCalendar(DSL.using(conn, getDefaultSettings()), year,
					workplaceId);
		} else
			return getCalendarSelected(DSL.using(conn, getDefaultSettings()),
					pattern, year);
	}

	private static CalendarDraft getCalendar(DSLContext dslContext, Integer year,
			Integer workplaceId) throws IllegalArgumentException {

		CalendarDraft calendarDraft = new CalendarDraft();

		Record record = dslContext.select()
				.from(PAYROLL_WORKPLACE)
				.join(CALENDAR)
				.on(PAYROLL_WORKPLACE.CALENDAR.eq(CALENDAR.ID))
				.where(PAYROLL_WORKPLACE.WORKPLACE.eq(workplaceId)).fetchOne();

		List<HolidayDraft> holidays = new LinkedList<HolidayDraft>();

		if (record != null) {

			Integer calendar = record.getValue(PAYROLL_WORKPLACE.CALENDAR);

			if (calendar != null) {

				Integer holiday = record.getValue(CALENDAR.HOLIDAY);

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

	private static CalendarDraft getCalendarSelected(
			DSLContext dslContext, Integer pattern, Integer year)
			throws IllegalArgumentException {

		Record record = dslContext.selectFrom(HOLIDAY)
				.where(HOLIDAY.ID.eq(pattern))
				.fetchOne();

		List<HolidayDraft> holidays = new LinkedList<HolidayDraft>();

		if (record != null) {

			Integer holiday = record.getValue(HOLIDAY.ID);

			while (holiday != null)
				holiday = loadHoliday(dslContext, year, holidays, holiday);
		}

		CalendarDraft calendarDraft = new CalendarDraft();
		calendarDraft.setHolidayDrafts(holidays);
		return calendarDraft;

	}

	private static Integer loadHoliday(DSLContext dslContext, Integer year,
			List<HolidayDraft> holidays, Integer holiday)
			throws IllegalArgumentException {

		HolidayRecord record = dslContext.selectFrom(HOLIDAY)
				.where(HOLIDAY.ID.eq(holiday)).fetchOne();

		Integer id = record.getValue(HOLIDAY.ID);
		Integer domain = record.getValue(HOLIDAY.DOMAIN);
		Integer holidayAux = (record.getValue(HOLIDAY.HOLIDAY_) != null) ? record.getValue(HOLIDAY.HOLIDAY_) : -1;
		HolidayDraft draft = new HolidayDraft();
		draft.setDomain(domain);
		draft.setId(id);
		draft.setHoliday(holidayAux);
		draft.setDescription(record.getValue(HOLIDAY.DESCRIPTION));

		loadHolidayDetail(dslContext, domain, year, id, draft);

		holidays.add(draft);

		return record.getValue(HOLIDAY.HOLIDAY_);
	}

	private static void loadHolidayDetail(DSLContext dslContext,
			Integer domain, Integer year, Integer holiday, HolidayDraft draft)
			throws IllegalArgumentException {

		List<HolidayDetailRecord> result = dslContext
				.selectFrom(HOLIDAY_DETAIL)
				.where(HOLIDAY_DETAIL.HOLIDAY.eq(holiday))
				.and(HOLIDAY_DETAIL.DATE.like("%" + year + "%"))
				.orderBy(HOLIDAY_DETAIL.DATE.asc()).fetchInto(HOLIDAY_DETAIL);

		if (result != null) {

			for (HolidayDetailRecord record : result) {

				Date date = record.getValue(HOLIDAY_DETAIL.DATE);
				String descrip = record.getValue(HOLIDAY_DETAIL.DESCRIPTION);

				draft.addHoliday(date, descrip);
			}
		}
	}

	public static void insertHolidays(Connection conn, Integer domain,
			Integer workplaceId, String holidayDescription,
			Integer holidayListBox, Map<Date, String> map, DayType daysTypes [])
			throws IllegalArgumentException {

		DSLContext dslContext = DSL.using(conn, getDefaultSettings());

		PayrollWorkplaceRecord result = dslContext
				.selectFrom(PAYROLL_WORKPLACE)
				.where(PAYROLL_WORKPLACE.WORKPLACE.eq(workplaceId)).fetchOne();

		if (result == null) { // No hay registro del workplace en
			// payroll_workplace
			insertHolidayWithoutCalendar(dslContext, domain, workplaceId,
					holidayDescription, holidayListBox, map);
		}
			else { // Hay un registro en payroll_workplace
				Integer calendar = result.getValue(PAYROLL_WORKPLACE.CALENDAR);

				if (calendar == null) {

					HolidayRecord holiday = getHolidayRecord(dslContext,
							holidayListBox);

					InsertSetMoreStep<HolidayRecord> insert = dslContext
							.insertInto(HOLIDAY).set(HOLIDAY.DOMAIN, domain)
							.set(HOLIDAY.DESCRIPTION, holidayDescription);

					if (holidayListBox >= 0)
						insert = insert.set(HOLIDAY.HOLIDAY_,
								holiday.getValue(HOLIDAY.ID));

					Integer holidayId = insert.returning(HOLIDAY.ID).fetchOne()
							.getId();

					Integer calendarId = 
							dslContext.insertInto(CALENDAR)
							.set(CALENDAR.DOMAIN, domain)
							.set(CALENDAR.HOLIDAY, holidayId)
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

					insertHolidayDetail(dslContext, domain, holidayId, map);
				}

				else { // Calendar en Payroll_workplace no es NULL

					dslContext
					.update(CALENDAR)
					.set(CALENDAR.SUNDAY, (byte)daysTypes[0].ordinal())
					.set(CALENDAR.MONDAY, (byte)daysTypes[1].ordinal())
					.set(CALENDAR.TUESDAY, (byte)daysTypes[2].ordinal())
					.set(CALENDAR.WEDNESDAY, (byte)daysTypes[3].ordinal())
					.set(CALENDAR.THURSDAY, (byte)daysTypes[4].ordinal())
					.set(CALENDAR.FRIDAY, (byte)daysTypes[5].ordinal())
					.set(CALENDAR.SATURDAY, (byte)daysTypes[6].ordinal())
					.where(CALENDAR.ID.eq(result
							.getValue(PAYROLL_WORKPLACE.CALENDAR)))
					.execute();

					Record holidayResult = dslContext
							.select()
							.from(HOLIDAY)
							.rightOuterJoin(CALENDAR)
							.on(HOLIDAY.ID.eq(CALENDAR.HOLIDAY))
							.where(CALENDAR.ID.eq(result
									.getValue(PAYROLL_WORKPLACE.CALENDAR)))
							.fetchOne();

					if (holidayResult != null) {

						Integer hDomain = holidayResult
								.getValue(HOLIDAY.DOMAIN);

						if (hDomain == 0) {
							// Si Holiday tiene domain=0 tengo que insertar un
							// holiday propio
							Integer holidayIdAux = dslContext
									.insertInto(HOLIDAY)
									.set(HOLIDAY.DOMAIN, domain)
									.set(HOLIDAY.DESCRIPTION,
											holidayDescription)
									.set(HOLIDAY.HOLIDAY_,
											holidayResult
													.getValue(HOLIDAY.HOLIDAY_))
									.returning(HOLIDAY.ID).fetchOne().getId();

							dslContext
									.update(CALENDAR)
									.set(CALENDAR.HOLIDAY, holidayIdAux)
									.where(CALENDAR.ID.eq(result
											.getValue(PAYROLL_WORKPLACE.CALENDAR)))
									.execute();

							insertHolidayDetail(dslContext, domain,
									holidayIdAux, map);

						}

						else {
							// Holiday tiene domain != 0
							HolidayRecord id = getHolidayRecord(dslContext,
									holidayListBox);

							dslContext
									.update(HOLIDAY)
									.set(HOLIDAY.HOLIDAY_,
											id.getValue(HOLIDAY.ID))
									.where(HOLIDAY.ID.eq(holidayResult
											.getValue(CALENDAR.HOLIDAY)))
									.execute();

							insertHolidayDetail(dslContext, domain,
									holidayResult.getValue(HOLIDAY.ID), map);
						}
					}
				}
			}
		}

	private static HolidayRecord getHolidayRecord(DSLContext dslContext,
			Integer holidayListBox) throws IllegalArgumentException {

		return dslContext.selectFrom(HOLIDAY)
				.where(HOLIDAY.ID.eq(holidayListBox)).fetchOne();
	}

	private static void insertHolidayWithoutCalendar(DSLContext dslContext,
			Integer domain, Integer workplaceId, String holidayDescription,
			Integer holidayListBox, Map<Date, String> map)
			throws IllegalArgumentException {

		// NO HAY REGISTRO DEL WORKPLACE EN PAYROLL_WORKPLACE
		InsertSetMoreStep<HolidayRecord> insert = dslContext
				.insertInto(HOLIDAY).set(HOLIDAY.DOMAIN, domain)
				.set(HOLIDAY.DESCRIPTION, holidayDescription);

		if (holidayListBox >= 0)
			insert = insert.set(HOLIDAY.HOLIDAY_, holidayListBox);

		Integer holidayId = insert.returning(HOLIDAY.ID).fetchOne().getId();

		Integer calendarId = dslContext.insertInto(CALENDAR)
				.set(CALENDAR.DOMAIN, domain)
				.set(CALENDAR.HOLIDAY, holidayId)
				.returning(CALENDAR.ID).fetchOne().getId();

		dslContext.insertInto(PAYROLL_WORKPLACE)
				.set(PAYROLL_WORKPLACE.DOMAIN, domain)
				.set(PAYROLL_WORKPLACE.WORKPLACE, workplaceId)
				.set(PAYROLL_WORKPLACE.CALENDAR, calendarId).execute();

		insertHolidayDetail(dslContext, domain, holidayId, map);
	}

	private static void insertHolidayDetail(DSLContext dslContext,
			Integer domain, Integer holidayId, Map<Date, String> map)
			throws IllegalArgumentException {
		
		for (Date date : map.keySet()) {
			
			dslContext
			.delete(HOLIDAY_DETAIL)
			.where(HOLIDAY_DETAIL.HOLIDAY.eq(holidayId))
			.and(HOLIDAY_DETAIL.DATE.eq(new java.sql.Date(date.getTime())))
			.execute();
		}
				

		for (Date date : map.keySet()) {
			dslContext
					.insertInto(HOLIDAY_DETAIL)
					.set(HOLIDAY_DETAIL.DOMAIN, domain)
					.set(HOLIDAY_DETAIL.HOLIDAY, holidayId)
					.set(HOLIDAY_DETAIL.DATE, new java.sql.Date(date.getTime()))
					.set(HOLIDAY_DETAIL.DESCRIPTION, map.get(date)).execute();
		}
	}
	
	public static void deletePropertyHoliday(Connection conn, Integer id, Date date) {
		deletePropertyHoliday(DSL.using(conn, getDefaultSettings()), id, date);
	}
	
	private static void deletePropertyHoliday(DSLContext dslContext, Integer id, Date date) 
			throws IllegalArgumentException {
		
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
