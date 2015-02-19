package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Calendar.CALENDAR;
import static com.esferalia.aon.jooq.tables.Holiday.HOLIDAY;
import static com.esferalia.aon.jooq.tables.HolidayDetail.HOLIDAY_DETAIL;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;

import java.sql.Connection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.shared.HolidayDraft;
import com.esferalia.aon.jooq.tables.records.HolidayDetailRecord;
import com.esferalia.aon.jooq.tables.records.HolidayRecord;

public class JooqCalendar {

	private static Settings SETTINGS = null;

	public static HolidayDraft getCalendar(Connection conn, Integer workplaceId) 
			throws IllegalArgumentException {
		return getCalendar(DSL.using(conn, getDefaultSettings()), workplaceId);
	}

	private static HolidayDraft getCalendar(DSLContext dslContext,
			Integer workplaceId) throws IllegalArgumentException {

		Record record = dslContext.select().from(PAYROLL_WORKPLACE)
				.join(CALENDAR).on(PAYROLL_WORKPLACE.CALENDAR.eq(CALENDAR.ID))
				.where(PAYROLL_WORKPLACE.WORKPLACE.eq(workplaceId)).fetchOne();

		HolidayDraft draft = new HolidayDraft();

		if (record != null) {

			if (record.getValue(PAYROLL_WORKPLACE.CALENDAR) != null) {
				int holiday = record.getValue(CALENDAR.HOLIDAY);
				loadHolidayDraft(dslContext, draft, holiday);
			}
		}

		return draft;

	}

	private static void loadHolidayDraft(DSLContext dslContext,
			HolidayDraft holidayDraft, Integer holiday) throws IllegalArgumentException {

		HolidayRecord record = dslContext.selectFrom(HOLIDAY)
				.where(HOLIDAY.ID.eq(holiday)).fetchOne();

		Integer auxHoliday = record.getValue(HOLIDAY.HOLIDAY_);
		
		if (auxHoliday == null) {
			// *** FIESTAS ESTATALES ***
			int holidayId = record.getValue(HOLIDAY.ID);
			Map<Date, String> statalHolidays = loadMapHolidays(dslContext,
					holidayId);
			holidayDraft.addStatalHoliday(statalHolidays);
			holidayDraft.setStatalTitle(record
					.getValue(HOLIDAY_DETAIL.DESCRIPTION));
		}


		else if (auxHoliday != 21) {
			// *** FIESTAS LOCALES ***
			int holidayId = record.getValue(HOLIDAY.ID);
			Map<Date, String> localHolidays = loadMapHolidays(dslContext,
					holidayId);
			holidayDraft.addLocalHoliday(localHolidays);
			holidayDraft.setLocalTitle(record
					.getValue(HOLIDAY_DETAIL.DESCRIPTION));

			loadHolidayDraft(dslContext, holidayDraft, auxHoliday);
		}

		else if (auxHoliday == 21) {
			// *** FIESTAS AUTONOMICAS ***
			int holidayId = record.getValue(HOLIDAY.ID);
			Map<Date, String> autonomiHolidays = loadMapHolidays(dslContext,
					holidayId);
			holidayDraft.addAutonomiHolidays(autonomiHolidays);
			holidayDraft.setAutonomiTitle(record
					.getValue(HOLIDAY_DETAIL.DESCRIPTION));

			loadHolidayDraft(dslContext, holidayDraft, auxHoliday);
		}
	}

	private static Map<Date, String> loadMapHolidays(DSLContext dslContext,
			Integer holiday) throws IllegalArgumentException {

		List<HolidayDetailRecord> result = dslContext
				.selectFrom(HOLIDAY_DETAIL)
				.where(HOLIDAY_DETAIL.HOLIDAY.eq(holiday))
				.and(HOLIDAY_DETAIL.DATE.like("%2015%"))
				.fetchInto(HOLIDAY_DETAIL);

		Map<Date, String> map = new LinkedHashMap<Date, String>();

		if (result != null) {

			for (HolidayDetailRecord record : result) {

				Date date = record.getValue(HOLIDAY_DETAIL.DATE);
				String description = record
						.getValue(HOLIDAY_DETAIL.DESCRIPTION);

				map.put(date, description);
			}
		}

		return map;
	}

	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}

}
