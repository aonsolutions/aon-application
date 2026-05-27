package com.esferalia.aon.occam.impl.jooq;

import java.util.List;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.ICalendar;
import com.esferalia.aon.occam.api.model.calendar.Calendar;
import com.esferalia.aon.occam.api.model.calendar.Holiday;
import com.esferalia.aon.occam.impl.jooq.dao.calendar.CalendarDAO;
import com.esferalia.aon.occam.impl.jooq.dao.calendar.HolidayDAO;
import com.esferalia.aon.occam.impl.jooq.dao.calendar.HolidayDetailDAO;

public class CalendarImpl implements ICalendar {

	@Override
	public List<Calendar> getCalendar(CloseableAONContext ctx, Integer domainId, Integer workplace) {
		return ctx.getDslContext().transactionResult(
				configuration -> CalendarDAO.getStream(ctx, domainId, workplace).collect(Collectors.toList()));
	}

	@Override
	public List<Calendar> getCalendars(CloseableAONContext ctx, Integer domainId) {
		return ctx.getDslContext().transactionResult(
				configuration -> CalendarDAO.getStream(ctx, f -> f.getDomainProperty().eq(domainId), 1, Integer.MAX_VALUE).collect(Collectors.toList()));
	}

	@Override
	public List<Holiday> getHolidays(CloseableAONContext ctx, Integer domainId, Integer parentDomain) {
		if(null == parentDomain)
			return ctx.getDslContext().transactionResult(
					configuration -> HolidayDAO.getStream(ctx, f -> f.getDomainProperty().eq(0).or(f.getDomainProperty().eq(domainId))).collect(Collectors.toList()));
		else
			return ctx.getDslContext().transactionResult(
					configuration -> HolidayDAO.getStream(ctx, f -> f.getDomainProperty().eq(0).or(f.getDomainProperty().eq(parentDomain)).or(f.getDomainProperty().eq(domainId))).collect(Collectors.toList()));
	}

	@Override
	public void deleteHolidayDetail(CloseableAONContext ctx, Integer id) {
		ctx.getDslContext().transaction(
				configuration -> HolidayDetailDAO.delete(ctx, id));
	}

	@Override
	public Calendar saveCalendar(CloseableAONContext ctx, Integer domainId, Calendar calendar) {
		return ctx.getDslContext().transactionResult(
				configuration -> CalendarDAO.save(ctx, calendar));
	}

	@Override
	public void setPayrollWorkplaceCalendar(CloseableAONContext ctx, Integer domainId, Integer workplaceId,
			Integer calendarId) {
		ctx.getDslContext().transaction(
				configuration -> CalendarDAO.setPayrollWorkplaceCalendar(ctx, domainId, workplaceId, calendarId));
	}

}
