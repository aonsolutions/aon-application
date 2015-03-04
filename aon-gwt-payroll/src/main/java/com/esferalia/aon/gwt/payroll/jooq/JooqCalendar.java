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

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.shared.HolidayDraft;
import com.esferalia.aon.jooq.tables.records.HolidayDetailRecord;
import com.esferalia.aon.jooq.tables.records.HolidayRecord;

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
				String description = item.getValue(HOLIDAY.DESCRIPTION);
				list.add(description);
			}
		}

		return list;

	}

	public static List<HolidayDraft> getCalendar(Connection conn,
			Integer workplaceId) throws IllegalArgumentException {
		return getCalendar(DSL.using(conn, getDefaultSettings()), workplaceId);
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

	private static Integer loadHoliday(DSLContext dslContext,
			List<HolidayDraft> holidays, Integer holiday) {

		HolidayRecord record = dslContext.selectFrom(HOLIDAY)
				.where(HOLIDAY.ID.eq(holiday)).fetchOne();

		Integer id = record.getValue(HOLIDAY.ID);

		HolidayDraft draft = new HolidayDraft();
		draft.setId(id);
		draft.setDescription(record.getValue(HOLIDAY.DESCRIPTION));

		loadHolidayDetail(dslContext, id, draft);

		holidays.add(draft);

		return record.getValue(HOLIDAY.HOLIDAY_);
	}

	private static void loadHolidayDetail(DSLContext dslContext,
			Integer holiday, HolidayDraft draft) {

		List<HolidayDetailRecord> result = dslContext
				.selectFrom(HOLIDAY_DETAIL)
				.where(HOLIDAY_DETAIL.HOLIDAY.eq(holiday))
				.and(HOLIDAY_DETAIL.DATE.like("%2015%"))
				.orderBy(HOLIDAY_DETAIL.DATE.asc()).fetchInto(HOLIDAY_DETAIL);

		if (result != null) {

			for (HolidayDetailRecord record : result) {

				Date date = record.getValue(HOLIDAY_DETAIL.DATE);
				String description = record
						.getValue(HOLIDAY_DETAIL.DESCRIPTION);

				draft.addHoliday(date, description);
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
