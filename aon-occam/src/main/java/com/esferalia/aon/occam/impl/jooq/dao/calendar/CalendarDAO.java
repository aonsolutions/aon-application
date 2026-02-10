package com.esferalia.aon.occam.impl.jooq.dao.calendar;

import static com.esferalia.aon.jooq.tables.Calendar.CALENDAR;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Filter.CalendarFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.CalendarProperties;
import com.esferalia.aon.occam.api.model.calendar.Calendar;
import com.esferalia.aon.occam.api.model.calendar.Holiday;
import com.esferalia.aon.occam.impl.jooq.dao.Filler;
import com.esferalia.aon.occam.impl.jooq.dao.FilterDAO;

public class CalendarDAO {
	private static final CalendarPropertiesDAO CALENDAR_PROPERTIES = new CalendarPropertiesDAO();
	
	protected static class CalendarPropertiesDAO implements CalendarProperties {
		protected Condition[] getConditions(CalendarFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(CALENDAR.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(CALENDAR.DOMAIN);}
		@Override public Property<Integer> getHolidayroperty() { return new FilterDAO.PropertyDAO<>(CALENDAR.HOLIDAY);}
		@Override public Property<Double> getAnualHoursProperty() { return new FilterDAO.PropertyDAO<>(CALENDAR.ANUAL_HOURS);}
		@Override public Property<Double> getAnualPersonalDaysProperty() { return new FilterDAO.PropertyDAO<>(CALENDAR.ANNUAL_PERSONAL_DAYS);}
		@Override public Property<Double> getAnualHolidaysProperty() { return new FilterDAO.PropertyDAO<>(CALENDAR.ANNUAL_HOLIDAYS);}
		@Override public Property<Byte> getHolidaysTypeProperty() { return new FilterDAO.PropertyDAO<>(CALENDAR.HOLIDAYS_TYPE);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(CALENDAR.DESCRIPTION);}
		@Override public Property<String> getCommentsProperty() { return new FilterDAO.PropertyDAO<>(CALENDAR.COMMENTS);}
		@Override public Property<Byte> getMondayProperty() { return new FilterDAO.PropertyDAO<>(CALENDAR.MONDAY);}
		@Override public Property<Double> getMondayHoursProperty() { return new FilterDAO.PropertyDAO<>(CALENDAR.MONDAY_HOURS);}
		@Override public Property<Byte> getTuesdayProperty() { return new FilterDAO.PropertyDAO<>(CALENDAR.TUESDAY);}
		@Override public Property<Double> getTuesdayHoursProperty() { return new FilterDAO.PropertyDAO<>(CALENDAR.TUESDAY_HOURS);}
		@Override public Property<Byte> getWednesdayProperty() { return new FilterDAO.PropertyDAO<>(CALENDAR.WEDNESDAY);}
		@Override public Property<Double> getWednesHoursProperty() { return new FilterDAO.PropertyDAO<>(CALENDAR.WEDNESDAY_HOURS);}
		@Override public Property<Byte> getThursdayProperty() { return new FilterDAO.PropertyDAO<>(CALENDAR.THURSDAY);}
		@Override public Property<Double> getThursdayHoursProperty() { return new FilterDAO.PropertyDAO<>(CALENDAR.THURSDAY_HOURS);}
		@Override public Property<Byte> getFridayProperty() { return new FilterDAO.PropertyDAO<>(CALENDAR.FRIDAY);}
		@Override public Property<Double> getFridayHoursProperty() { return new FilterDAO.PropertyDAO<>(CALENDAR.FRIDAY_HOURS);}
		@Override public Property<Byte> getSaturdayProperty() { return new FilterDAO.PropertyDAO<>(CALENDAR.SATURDAY);}
		@Override public Property<Double> getSaturdayHoursProperty() { return new FilterDAO.PropertyDAO<>(CALENDAR.SATURDAY_HOURS);}
		@Override public Property<Byte> getSundayProperty() { return new FilterDAO.PropertyDAO<>(CALENDAR.SUNDAY);}
		@Override public Property<Double> getSundayHoursProperty() { return new FilterDAO.PropertyDAO<>(CALENDAR.SUNDAY_HOURS);}
		@Override public Property<Byte> getGenericProperty() { return new FilterDAO.PropertyDAO<>(CALENDAR.GENERIC);}
		@Override public Property<Integer> getCalendarParentProperty() { return new FilterDAO.PropertyDAO<>(CALENDAR.CALENDAR_);}
		
	}
	
	public static SelectConditionStep<Record> select(AONContext ctx, CalendarFilter filter){	
		return ctx.getDslContext()
				.select()
				.from(CALENDAR)
				.where(CALENDAR_PROPERTIES.getConditions(filter));
	}
	
	public static Optional<Calendar> get(AONContext ctx, CalendarFilter filter) {
		ctx.checkRead();
		Optional<Calendar> calendar = select(ctx, filter)
				.limit(1)
				.fetch()
				.stream()
				.map(new CalendarFiller())
				.findFirst();
		
		if(calendar.isPresent() && null != calendar.get().getHoliday().getId()) {
			calendar.get().setHoliday(HolidayDAO.get(ctx, f -> f.getIdProperty().eq(calendar.get().getHoliday().getId())).get());
		}
		
		return calendar;
	}
	
	public static Stream<Calendar>  getStream(AONContext ctx, Integer domainId, Integer workplace) {
		Result<Record> payrollWorkplaces = ctx.getDslContext().select()
			.from(PAYROLL_WORKPLACE)
			.where(PAYROLL_WORKPLACE.DOMAIN.eq(domainId))
			.and(PAYROLL_WORKPLACE.WORKPLACE.eq(workplace))
			.fetch();
		
		if(payrollWorkplaces.isEmpty()) throw new IllegalArgumentException("No existe payroll_workplace asociado al CT");
		else if(payrollWorkplaces.size() > 1) throw new IllegalArgumentException("Existe mas de un payroll_workplace asociado al CT");
		else {
			Integer calendarId = payrollWorkplaces.get(0).get(PAYROLL_WORKPLACE.CALENDAR);
			if(null == calendarId) return new ArrayList<Calendar>().stream();
			else 
				return getStream(ctx, f -> f.getIdProperty().eq(calendarId));
		}
	}
	
	public static Stream<Calendar> getStream(AONContext ctx, CalendarFilter filter){	
		List<Calendar> calendarList = select(ctx, filter)
				.fetch()
				.stream()
				.map(new CalendarFiller())
				.collect(Collectors.toList());
		
		calendarList.forEach(calendar -> {
			if(null != calendar.getHoliday().getId()) {
				calendar.setHoliday(HolidayDAO.get(ctx, f -> f.getIdProperty().eq(calendar.getHoliday().getId())).get());
			}
		});
		
		return calendarList.stream();
	}
	
	public static Stream<Calendar> getStream(AONContext ctx, CalendarFilter filter, Integer page, Integer perPage){	
		List<Calendar> calendarList  = select(ctx, filter)	
				.offset(perPage * (page -1))
				.fetch()
				.stream()
				.map(new CalendarFiller())
				.collect(Collectors.toList());
		
		calendarList.forEach(calendar -> {
			if(null != calendar.getHoliday().getId()) {
				calendar.setHoliday(HolidayDAO.get(ctx, f -> f.getIdProperty().eq(calendar.getHoliday().getId())).get());
			}
		});
		
		return calendarList.stream();
	}
	
	
	public static Calendar save(AONContext ctx, Calendar calendar) {
		return (calendar.getId() != null && calendar.getId() > 0)
			? update(ctx, calendar)
			: insert(ctx, calendar); 
	}
	
	private static Calendar update(AONContext ctx, Calendar calendar){
		Holiday holiday = calendar.getHoliday();
		if(holiday != null && null != holiday.getDomain())
			holiday = HolidayDAO.save(ctx, holiday);
		
		ctx.getDslContext().update(CALENDAR)
			.set(CALENDAR.HOLIDAY, null == holiday ? null : holiday.getId())
			.set(CALENDAR.ANUAL_HOURS, calendar.getAnualHours())
			.set(CALENDAR.ANNUAL_PERSONAL_DAYS, calendar.getAnualPersonalDays())
			.set(CALENDAR.ANNUAL_HOLIDAYS, calendar.getAnnualHolidays())
			.set(CALENDAR.HOLIDAYS_TYPE, null == calendar.getHolidaysType() ? (byte)0 : calendar.getHolidaysType())
			.set(CALENDAR.DESCRIPTION, calendar.getDescription())
			.set(CALENDAR.COMMENTS, calendar.getComment())
			.set(CALENDAR.MONDAY, calendar.isMonday() ? (byte) 1 : (byte) 0)
			.set(CALENDAR.MONDAY_HOURS, calendar.getMondayHours())
			.set(CALENDAR.TUESDAY, calendar.isTuesday() ? (byte) 1 : (byte) 0)
			.set(CALENDAR.TUESDAY_HOURS, calendar.getTuesdayHours())
			.set(CALENDAR.WEDNESDAY, calendar.isWednesday() ? (byte) 1 : (byte) 0)
			.set(CALENDAR.WEDNESDAY_HOURS, calendar.getWednesdayHours())
			.set(CALENDAR.THURSDAY, calendar.isThursday() ? (byte) 1 : (byte) 0)
			.set(CALENDAR.THURSDAY_HOURS, calendar.getThursdayHours())
			.set(CALENDAR.FRIDAY, calendar.isFriday() ? (byte) 1 : (byte) 0)
			.set(CALENDAR.FRIDAY_HOURS, calendar.getFridayHours())
			.set(CALENDAR.SATURDAY, calendar.isSaturday() ? (byte) 1 : (byte) 0)
			.set(CALENDAR.SATURDAY_HOURS, calendar.getSaturdayHours())
			.set(CALENDAR.SUNDAY, calendar.isSunday() ? (byte) 1 : (byte) 0)
			.set(CALENDAR.SUNDAY_HOURS, calendar.getSundayHours())
			.set(CALENDAR.GENERIC, calendar.isGeneric() ? (byte) 1 : (byte) 0)
			.set(CALENDAR.CALENDAR_, calendar.getCalendarParent())
			.where(CALENDAR.ID.eq(calendar.getId()))
			.execute();
		
		
		
		ctx.log().debug("UPDATE CALENDAR id: " + calendar.getId());		
		return calendar;
	}
	
	private static Calendar insert(AONContext ctx, Calendar calendar) {
		Holiday holiday = calendar.getHoliday();
		if(holiday != null && null != holiday.getDomain())
			holiday = HolidayDAO.save(ctx, holiday);
		
		Integer id = ctx.getDslContext()
				.insertInto(CALENDAR)
				.set(CALENDAR.DOMAIN, calendar.getDomain())
				.set(CALENDAR.HOLIDAY, null == holiday ? null : holiday.getId())
				.set(CALENDAR.ANUAL_HOURS, calendar.getAnualHours())
				.set(CALENDAR.ANNUAL_PERSONAL_DAYS, calendar.getAnualPersonalDays())
				.set(CALENDAR.ANNUAL_HOLIDAYS, calendar.getAnnualHolidays())
				.set(CALENDAR.HOLIDAYS_TYPE, null == calendar.getHolidaysType() ? (byte)0 : calendar.getHolidaysType())
				.set(CALENDAR.DESCRIPTION, calendar.getDescription())
				.set(CALENDAR.COMMENTS, calendar.getComment())
				.set(CALENDAR.MONDAY, calendar.isMonday() ? (byte) 1 : (byte) 0)
				.set(CALENDAR.MONDAY_HOURS, calendar.getMondayHours())
				.set(CALENDAR.TUESDAY, calendar.isTuesday() ? (byte) 1 : (byte) 0)
				.set(CALENDAR.TUESDAY_HOURS, calendar.getTuesdayHours())
				.set(CALENDAR.WEDNESDAY, calendar.isWednesday() ? (byte) 1 : (byte) 0)
				.set(CALENDAR.WEDNESDAY_HOURS, calendar.getWednesdayHours())
				.set(CALENDAR.THURSDAY, calendar.isThursday() ? (byte) 1 : (byte) 0)
				.set(CALENDAR.THURSDAY_HOURS, calendar.getThursdayHours())
				.set(CALENDAR.FRIDAY, calendar.isFriday() ? (byte) 1 : (byte) 0)
				.set(CALENDAR.FRIDAY_HOURS, calendar.getFridayHours())
				.set(CALENDAR.SATURDAY, calendar.isSaturday() ? (byte) 1 : (byte) 0)
				.set(CALENDAR.SATURDAY_HOURS, calendar.getSaturdayHours())
				.set(CALENDAR.SUNDAY, calendar.isSunday() ? (byte) 1 : (byte) 0)
				.set(CALENDAR.SUNDAY_HOURS, calendar.getSundayHours())
				.set(CALENDAR.GENERIC, calendar.isGeneric() ? (byte) 1 : (byte) 0)
				.set(CALENDAR.CALENDAR_, calendar.getCalendarParent())
				.returning(CALENDAR.ID)
				.fetchOne()
				.getId();
		
		calendar.setId(id);
		ctx.log().debug("INSERT CALENDAR id: " + id);		
		return calendar;
	}

	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		
		ctx.getDslContext().delete(CALENDAR)
			.where(CALENDAR.ID.eq(id))
			.execute();	
		
		ctx.log().debug("DELETE CALENDAR id: " + id);		
	}

	public static void setPayrollWorkplaceCalendar(CloseableAONContext ctx, Integer domainId, Integer workplaceId, Integer calendarId) {
		ctx.getDslContext()
			.update(PAYROLL_WORKPLACE)
			.set(PAYROLL_WORKPLACE.CALENDAR, calendarId)
			.where(PAYROLL_WORKPLACE.WORKPLACE.eq(workplaceId))
			.and(PAYROLL_WORKPLACE.DOMAIN.eq(domainId))
			.execute();
	}
	
	private static class CalendarFiller extends Filler implements Function<Record, Calendar> {
		@Override
		public Calendar apply(Record r) {
			return new Calendar()
					.setId(r.getValue(CALENDAR.ID))
					.setDomain(r.getValue(CALENDAR.DOMAIN))
					.setHoliday(new Holiday().setId( r.getValue(CALENDAR.HOLIDAY) ))
					.setAnualHours(r.getValue(CALENDAR.ANUAL_HOURS))
					.setAnualPersonalDays(r.getValue(CALENDAR.ANNUAL_PERSONAL_DAYS))
					.setAnnualHolidays(r.getValue(CALENDAR.ANNUAL_HOLIDAYS))
					.setHolidaysType(r.getValue(CALENDAR.HOLIDAYS_TYPE))
					.setDescription(r.getValue(CALENDAR.DESCRIPTION))
					.setComment(r.getValue(CALENDAR.COMMENTS))
					.setMonday(r.getValue(CALENDAR.MONDAY) == (byte) 0 ? false : true)
					.setMondayHours(r.getValue(CALENDAR.MONDAY_HOURS))
					.setTuesday(r.getValue(CALENDAR.TUESDAY) == (byte) 0 ? false : true)
					.setTuesdayHours(r.getValue(CALENDAR.TUESDAY_HOURS))
					.setWednesday(r.getValue(CALENDAR.WEDNESDAY) == (byte) 0 ? false : true)
					.setWednesdayHours(r.getValue(CALENDAR.WEDNESDAY_HOURS))
					.setThursday(r.getValue(CALENDAR.THURSDAY) == (byte) 0 ? false : true)
					.setThursdayHours(r.getValue(CALENDAR.THURSDAY_HOURS))
					.setFriday(r.getValue(CALENDAR.FRIDAY) == (byte) 0 ? false : true)
					.setFridayHours(r.getValue(CALENDAR.FRIDAY_HOURS))
					.setSaturday(r.getValue(CALENDAR.SATURDAY) == (byte) 0 ? false : true)
					.setSaturdayHours(r.getValue(CALENDAR.SATURDAY_HOURS))
					.setSunday(r.getValue(CALENDAR.SUNDAY) == (byte) 0 ? false : true)
					.setSundayHours(r.getValue(CALENDAR.SUNDAY_HOURS))
					.setGeneric(r.getValue(CALENDAR.GENERIC) == (byte) 0 ? false : true)
					.setCalendarParent(r.getValue(CALENDAR.CALENDAR_))
					;
		}
	}
}
