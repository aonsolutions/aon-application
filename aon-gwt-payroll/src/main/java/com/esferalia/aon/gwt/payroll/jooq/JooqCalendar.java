package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Calendar.CALENDAR;
import static com.esferalia.aon.jooq.tables.Holiday.HOLIDAY;
import static com.esferalia.aon.jooq.tables.HolidayDetail.HOLIDAY_DETAIL;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.UpdateSetMoreStep;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.shared.HolidayDraft;
import com.esferalia.aon.jooq.tables.records.HolidayDetailRecord;
import com.esferalia.aon.jooq.tables.records.HolidayRecord;
import com.esferalia.aon.jooq.tables.records.PayrollWorkplaceRecord;

public class JooqCalendar {

	private static Settings SETTINGS = null;

	public static List<String> getHolidayDescription(Connection conn)
			throws IllegalArgumentException {

		DSLContext dslContext = DSL.using(conn, getDefaultSettings());

		List<HolidayRecord> result = dslContext.selectFrom(HOLIDAY)
				.where(HOLIDAY.DOMAIN.eq(0)).orderBy(HOLIDAY.DESCRIPTION.asc())
				.fetchInto(HOLIDAY);

		List<String> list = new ArrayList<String>();

		if (result != null) {

			for (HolidayRecord item : result) {
				list.add(item.getValue(HOLIDAY.DESCRIPTION));
			}
		}

		return list;

	}

	public static List<HolidayDraft> getCalendar(Connection conn,
			Integer workplaceId, String pattern)
			throws IllegalArgumentException {

		if (pattern == null) {
			// It's my first time here
			return getCalendar(DSL.using(conn, getDefaultSettings()),
					workplaceId);
		} else
			return getCalendarSelected(DSL.using(conn, getDefaultSettings()),
					pattern);
	}

	private static List<HolidayDraft> getCalendar(DSLContext dslContext,
			Integer workplaceId) throws IllegalArgumentException {

		Record record = dslContext.select().from(PAYROLL_WORKPLACE)
				.join(CALENDAR).on(PAYROLL_WORKPLACE.CALENDAR.eq(CALENDAR.ID))
				.where(PAYROLL_WORKPLACE.WORKPLACE.eq(workplaceId)).fetchOne();

		List<HolidayDraft> holidays = new LinkedList<HolidayDraft>();

		if (record != null) {

			Integer calendar = record.getValue(PAYROLL_WORKPLACE.CALENDAR);

			if (calendar != null) {

				Integer holiday = record.getValue(CALENDAR.HOLIDAY);

				while (holiday != null)
					holiday = loadHoliday(dslContext, holidays, holiday);
			}
		}

		return holidays;
	}

	private static List<HolidayDraft> getCalendarSelected(
			DSLContext dslContext, String pattern)
			throws IllegalArgumentException {

		Record record = dslContext.selectFrom(HOLIDAY)
				.where(HOLIDAY.DESCRIPTION.eq(pattern))
				.and(HOLIDAY.DOMAIN.eq(0)).fetchOne();

		List<HolidayDraft> holidays = new LinkedList<HolidayDraft>();

		if (record != null) {

			Integer holiday = record.getValue(HOLIDAY.ID);

			while (holiday != null)
				holiday = loadHoliday(dslContext, holidays, holiday);
		}

		return holidays;

	}

	private static Integer loadHoliday(DSLContext dslContext,
			List<HolidayDraft> holidays, Integer holiday)
			throws IllegalArgumentException {

		HolidayRecord record = dslContext.selectFrom(HOLIDAY)
				.where(HOLIDAY.ID.eq(holiday)).fetchOne();

		Integer id = record.getValue(HOLIDAY.ID);
		Integer domain = record.getValue(HOLIDAY.DOMAIN);

		HolidayDraft draft = new HolidayDraft();
		draft.setDomain(domain);
		draft.setId(id);
		draft.setDescription(record.getValue(HOLIDAY.DESCRIPTION));

		loadHolidayDetail(dslContext, domain, id, draft);

		holidays.add(draft);

		return record.getValue(HOLIDAY.HOLIDAY_);
	}

	private static void loadHolidayDetail(DSLContext dslContext,
			Integer domain, Integer holiday, HolidayDraft draft)
			throws IllegalArgumentException {

		List<HolidayDetailRecord> result = dslContext
				.selectFrom(HOLIDAY_DETAIL)
				.where(HOLIDAY_DETAIL.HOLIDAY.eq(holiday))
				.and(HOLIDAY_DETAIL.DATE.like("%2015%"))
				.orderBy(HOLIDAY_DETAIL.DATE.asc()).fetchInto(HOLIDAY_DETAIL);

		if (result != null) {

			for (HolidayDetailRecord record : result) {

				Date date = record.getValue(HOLIDAY_DETAIL.DATE);
				String descrip = record.getValue(HOLIDAY_DETAIL.DESCRIPTION);

				draft.addHoliday(date, descrip);
			}
		}
	}
	
	private static void insertHolidayWithoutCalendar(DSLContext dslContext, Integer domain, 
			Integer workplaceId, String holidayDescription, String holidayListBox, Map<Date, String> map) 
					throws IllegalArgumentException{
		
		// NO HAY REGISTRO DEL WORKPLACE EN PAYROLL_WORKPLACE
		InsertSetMoreStep<HolidayRecord> insert = dslContext
				.insertInto(HOLIDAY)
				.set(HOLIDAY.DOMAIN, domain)
				.set(HOLIDAY.DESCRIPTION, holidayDescription);
		
		if (!holidayListBox.equals("-"))
				insert = insert.set(HOLIDAY.HOLIDAY_,
									dslContext
								.select(HOLIDAY.ID)
								.from(HOLIDAY)
								.where(HOLIDAY.DESCRIPTION
										.eq(holidayListBox)));
				
		Integer holidayId = insert.returning(HOLIDAY.ID).fetchOne().getId();

		Integer calendarId = dslContext.insertInto(CALENDAR)
				.set(CALENDAR.DOMAIN, domain)
				.set(CALENDAR.HOLIDAY, holidayId).returning(CALENDAR.ID)
				.fetchOne().getId();

		dslContext.insertInto(PAYROLL_WORKPLACE)
				.set(PAYROLL_WORKPLACE.DOMAIN, domain)
				.set(PAYROLL_WORKPLACE.WORKPLACE, workplaceId)
				.set(PAYROLL_WORKPLACE.CALENDAR, calendarId).execute();
		
		insertHolidayDetail(dslContext, domain, holidayId, map);
	}
	
	private static void insertHolidayDetail(DSLContext dslContext, Integer domain, 
			Integer holidayId, Map<Date, String> map) throws IllegalArgumentException {
		
		for (Date date : map.keySet()) {
			dslContext
					.insertInto(HOLIDAY_DETAIL)
					.set(HOLIDAY_DETAIL.DOMAIN, domain)
					.set(HOLIDAY_DETAIL.HOLIDAY, holidayId)
					.set(HOLIDAY_DETAIL.DATE,
							new java.sql.Date(date.getTime()))
					.set(HOLIDAY_DETAIL.DESCRIPTION, map.get(date))
					.execute();
		}
	}

	public static void insertHolidays(Connection conn, Integer domain,
			Integer workplaceId, String holidayDescription,
			String holidayListBox, Map<Date, String> map)
			throws IllegalArgumentException {

		DSLContext dslContext = DSL.using(conn, getDefaultSettings());

		PayrollWorkplaceRecord result = dslContext
				.selectFrom(PAYROLL_WORKPLACE)
				.where(PAYROLL_WORKPLACE.WORKPLACE.eq(workplaceId)).fetchOne();

		if (result != null) {

			if (result.size() == 0) //No hay registro del workplace en payroll_workplace
				insertHolidayWithoutCalendar(dslContext, domain, workplaceId, holidayDescription, holidayListBox, map);

			
			else { // Hay un registro en payroll_workplace
				Integer calendar = result.getValue(PAYROLL_WORKPLACE.CALENDAR);

				if (calendar == null) {
					
					HolidayRecord holiday= dslContext
							.selectFrom(HOLIDAY)
							.where(HOLIDAY.DESCRIPTION.eq(holidayListBox))
							.fetchOne();
					
					InsertSetMoreStep<HolidayRecord> insert = dslContext.insertInto(HOLIDAY)
							.set(HOLIDAY.DOMAIN, domain)
							.set(HOLIDAY.DESCRIPTION, holidayDescription);
					
					if (!holidayListBox.equals("-"))
						insert = insert.set(HOLIDAY.HOLIDAY_, holiday.getValue(HOLIDAY.ID));
					
					Integer holidayId = insert.returning(HOLIDAY.ID).fetchOne().getId();
					
					Integer calendarId = dslContext.insertInto(CALENDAR)
					.set(CALENDAR.DOMAIN, domain)
					.set(CALENDAR.HOLIDAY, holidayId)
					.returning(CALENDAR.ID).fetchOne().getId();
					
					dslContext.update(PAYROLL_WORKPLACE)
					.set(PAYROLL_WORKPLACE.CALENDAR, calendarId)
					.where(PAYROLL_WORKPLACE.WORKPLACE.eq(workplaceId))					
					.execute();
					
					insertHolidayDetail(dslContext, domain, holidayId, map);
				}

				else { // Calendar en Payroll_workplace no es NULL
					
					Record holidayResult = dslContext.select()
					.from(HOLIDAY)
					.rightOuterJoin(CALENDAR).on(HOLIDAY.ID.eq(CALENDAR.HOLIDAY))
					.where(CALENDAR.ID.eq(result.getValue(PAYROLL_WORKPLACE.CALENDAR)))
					.fetchOne();

					if (holidayResult != null) {
						
						Integer hDomain = holidayResult
								.getValue(HOLIDAY.DOMAIN);

						if (hDomain == 0) {
							//Si Holiday tiene domain=0 tengo que insertar un holiday propio
							Integer holidayIdAux = dslContext
									.insertInto(HOLIDAY)
									.set(HOLIDAY.DOMAIN, domain)
									.set(HOLIDAY.DESCRIPTION,
											holidayDescription)
									.set(HOLIDAY.HOLIDAY_,
											holidayResult
													.getValue(HOLIDAY.HOLIDAY_))
									.returning(HOLIDAY.ID).fetchOne().getId();

							dslContext.update(CALENDAR)
									.set(CALENDAR.HOLIDAY, holidayIdAux)
									.where(CALENDAR.ID.eq(result.getValue(PAYROLL_WORKPLACE.CALENDAR)))
									.execute();
							
							insertHolidayDetail(dslContext, domain, holidayIdAux, map);
						
						}

						else {
							//Holiday tiene domain != 0
							HolidayRecord id = dslContext
									.selectFrom(HOLIDAY)
									.where(HOLIDAY.DESCRIPTION.eq(holidayListBox))
									.fetchOne();
							
							dslContext.update(HOLIDAY)
							.set(HOLIDAY.HOLIDAY_, id.getValue(HOLIDAY.ID))
							.where(HOLIDAY.ID.eq(holidayResult.getValue(CALENDAR.HOLIDAY)))
							.execute();
							
							insertHolidayDetail(dslContext, domain, holidayResult.getValue(HOLIDAY.ID), map);
						}

					}
				}
			}
		}
	}

	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
}
