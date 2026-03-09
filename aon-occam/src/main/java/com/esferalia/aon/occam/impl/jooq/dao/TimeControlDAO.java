package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Calendar.CALENDAR;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractLeave.CONTRACT_LEAVE;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Holiday.HOLIDAY;
import static com.esferalia.aon.jooq.tables.HolidayDetail.HOLIDAY_DETAIL;
import static com.esferalia.aon.jooq.tables.Location.LOCATION;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;
import static com.esferalia.aon.jooq.tables.TaskHolder.TASK_HOLDER;
import static com.esferalia.aon.jooq.tables.Timecontrol.TIMECONTROL;
import static com.esferalia.aon.occam.impl.jooq.dao.TaskHolderDAO.TASK_HOLDER_ALIAS;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Field;
import org.jooq.InsertSetMoreStep;
import org.jooq.Param;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.UpdateSetMoreStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.TimecontrolRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.TimeControlFilter;
import com.esferalia.aon.occam.api.model.aonsolutions.Coordinates;
import com.esferalia.aon.occam.api.model.aonsolutions.Location;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControl;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlContractEvent;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlContractEventSource;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlContractEvents;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlDetail;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlGroup;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlReason;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlStatus;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.TimeControlPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TaskHolderDAO.TaskHolderFiller;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class TimeControlDAO {	

	private static final TimeControlPropertiesDAO TIMECONTROL_PROPERTIES = new TimeControlPropertiesDAO();
	
	public static SelectConditionStep<Record> select(AONContext ctx, TimeControlFilter filter){	
		return ctx.getDslContext()
				.select()
				.from(TIMECONTROL)
				.join(TASK_HOLDER).on(TASK_HOLDER.REGISTRY.eq(TIMECONTROL.TASK_HOLDER))
				.join(TASK_HOLDER_ALIAS).on(TASK_HOLDER_ALIAS.ID.eq(TASK_HOLDER.REGISTRY))
				.join(DOMAIN).on(TIMECONTROL.DOMAIN.eq(DOMAIN.ID))
				.leftOuterJoin(LOCATION).on(LOCATION.ID.eq(TIMECONTROL.LOCATION))
				.where(TIMECONTROL_PROPERTIES.getConditions(filter));
	}
	
	public static Stream<TimeControlDetail> getTimeControlDetailStream(AONContext ctx, TimeControlFilter filter) {
		ctx.checkRead();
		return select(ctx, filter)
			.and(TIMECONTROL.ID.gt(0))
			.orderBy(TIMECONTROL.DATE.asc())
			.fetch().stream().map(new TimeControlDetailFiller());
	}
	
	public static Stream<TimeControlDetail> getTimeControlHistoric(AONContext ctx, TimeControlFilter filter) {
		ctx.checkRead();
		return select(ctx, filter)
			.orderBy(TIMECONTROL.ID.desc())
			.fetch().stream().map(new TimeControlDetailFiller());
	}
	
	
	public static TimeControlDetail getLastTimeControlDetail(AONContext ctx, TimeControlFilter filter) {
		ctx.checkRead();
		return select(ctx, filter)
			.orderBy(TIMECONTROL.DATE.desc()).limit(1)
			.fetch().stream().map(new TimeControlDetailFiller()).findFirst().orElse(new TimeControlDetail());
	}
	
	public static LinkedList<TimeControlDetail> getTimeControlDetailList(AONContext ctx, TimeControlFilter filter) {
		return getTimeControlDetailStream(ctx, filter).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static void updateTimeControDetailLocation(AONContext ctx, Location lc) {
		ctx.checkRead();
		Param<Double> lt = DSL.val(lc.getCoordinates().getLatitude());
		Param<Double> lg = DSL.val(lc.getCoordinates().getLongitude());
		Field<BigDecimal> pi = DSL.pi();
		
		Field<BigDecimal> f1 = DSL.sin(TIMECONTROL.LATITUDE.mul(pi).div(180))
				.mul(DSL.sin(lt.mul(pi).div(180)));
		
		Field<BigDecimal> f2 = DSL.cos(TIMECONTROL.LATITUDE.mul(pi).div(180))
				.mul(DSL.cos(lt.mul(pi).div(180)))
				.mul(DSL.cos(TIMECONTROL.LONGITUDE.sub(lg).mul(pi.div(180))));
		
		Field<BigDecimal> f3 = DSL.acos( f1.add(f2)).mul(DSL.val(180).div(pi));

		Field<BigDecimal> distance = f3.mul(DSL.val(60).mul(1.1515).mul(1609.344)).as("distance");

		LinkedList<Integer> list = ctx.getDslContext()
		.select(TIMECONTROL.ID,distance)
		.from(TIMECONTROL)
		.where(TIMECONTROL.DOMAIN.eq(ctx.getDomainId()))
		.and(TIMECONTROL.LOCATION.isNull())
		.having(
				distance.le(DSL.val(lc.getRadio())
				.cast(BigDecimal.class))
		 ).fetch().stream()
		.map( r-> r.getValue(TIMECONTROL.ID) )
		.collect(Collectors.toCollection(LinkedList::new));

		ctx.getDslContext()
		.update(TIMECONTROL)
		.set(TIMECONTROL.LOCATION, lc.getId())
		.where(TIMECONTROL.ID.in(list)).execute();
	}

	public static Stream<TimeControl> getTimeControlStream(AONContext ctx, Date startDate, Date endDate) {
		startDate = AonDateUtils.getDateWithoutTime(startDate);
		endDate = AonDateUtils.getDateWithoutTime(endDate);
		endDate = AonDateUtils.addDays(endDate, 1);
		endDate = AonDateUtils.addSeconds(endDate, -1);
		Timestamp startTimestamp = new Timestamp(startDate.getTime());
		Timestamp endTimestamp = new Timestamp(endDate.getTime());
		
		LinkedList<TimeControl> tcList = new LinkedList<>();
		LinkedList<TimeControlDetail> list = getTimeControlDetailList(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())
			.and(f.getDateProperty().ge(startTimestamp))
			.and(f.getDateProperty().le(endTimestamp)));

		TaskHolderDAO.getStream(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()))
		.forEach(th -> {
				TimeControl tc = buildTimeControl(ctx, th.getId(), list.stream().filter(f -> f.getTaskHolder().getId().equals(th.getId())), null, null, null);
				tcList.add(tc);
			});
		return tcList.stream();
	}
	
	public static Stream<TimeControl> getTaskHolderTimeControlStream(AONContext ctx, Integer taskHolderId, Date startDate, Date endDate, TimeControlGroup group) {
		startDate = AonDateUtils.getDateWithoutTime(startDate);
		endDate = AonDateUtils.getDateWithoutTime(endDate);
		endDate = AonDateUtils.addDays(endDate, 1);
		endDate = AonDateUtils.addSeconds(endDate, -1);
		
		Timestamp startTimestamp = new Timestamp(startDate.getTime());
		Timestamp endTimestamp = new Timestamp(endDate.getTime());
		
		LinkedList<TimeControl> tcList = new LinkedList<>();
		LinkedList<TimeControlDetail> list = getTimeControlDetailList(ctx, f -> 
			f.getDateProperty().ge(startTimestamp)
			.and(f.getDateProperty().le(endTimestamp))
			.and(f.getTaskHolderProperty().eq(taskHolderId)));
		
		if(TimeControlGroup.DAY.equals(group)) {
			Date date = startDate;
			while(date.compareTo(endDate) <= 0 ) {
				Date aDate = AonDateUtils.getDateWithoutTime(date);
				Date bDate = AonDateUtils.addDays(aDate, 1);
				Date cDate = AonDateUtils.addSeconds(bDate, -1);
				TimeControl tc = buildTimeControl(ctx, taskHolderId, list.stream().filter(f -> (f.getDate().compareTo(aDate) >= 0 && f.getDate().compareTo(cDate) <= 0)), aDate, cDate, TimeControlGroup.DAY);
				tcList.add(tc);
				date = AonDateUtils.addDays(date, 1);
			}
		} else if(TimeControlGroup.WEEK.equals(group)) {
			Date date = AonDateUtils.getFirstDayOfWeek(startDate);
			while(date.compareTo(endDate) <= 0 ) {
				Date aDate = date;
				Date bDate = AonDateUtils.addDays(date, 7);
				Date cDate = AonDateUtils.addSeconds(bDate, -1);
				TimeControl tc = buildTimeControl(ctx, taskHolderId, list.stream().filter(f -> (f.getDate().compareTo(aDate) >= 0 && f.getDate().compareTo(cDate) <= 0)), date, cDate, TimeControlGroup.WEEK);
				tcList.add(tc);
				date = AonDateUtils.addWeeks(date, 1);
			}
		} else if(TimeControlGroup.MONTH.equals(group)) {
			Date date = AonDateUtils.getMonthFirstDay(startDate);
			while(date.compareTo(endDate) <= 0 ) {
				int month = AonDateUtils.getMonth(date);
				int year = AonDateUtils.getYear(date);
				Date zDate = AonDateUtils.getMonthLastDay(date);
				TimeControl tc = buildTimeControl(ctx, taskHolderId, list.stream().filter(f -> (
						AonDateUtils.getMonth(f.getDate()) == month
						&& AonDateUtils.getYear(f.getDate()) == year)), date, zDate, TimeControlGroup.MONTH );
				tcList.add(tc);
				date = AonDateUtils.addMonths(date, 1);
			}
		} else if(TimeControlGroup.YEAR.equals(group)) {
			Date date = AonDateUtils.getYearFirstDay(startDate);
			while(date.compareTo(endDate) <= 0 ) {
				int year = AonDateUtils.getYear(date);
				Date zDate = AonDateUtils.getYearLastDay(date);
				TimeControl tc = buildTimeControl(ctx, taskHolderId, list.stream().filter(f -> AonDateUtils.getYear(f.getDate()) == year), date, zDate, TimeControlGroup.YEAR);
				tcList.add(tc);
				date = AonDateUtils.addYears(date, 1);
			}
		}
		return tcList.stream();
	}
	
	public static TimeControl getTaskHolderTimeControl(AONContext ctx, Integer taskHolderId, Date startDate, Date endDate) {
		startDate = AonDateUtils.getDateWithoutTime(startDate);
		endDate = AonDateUtils.getDateWithoutTime(endDate);
		endDate = AonDateUtils.addDays(endDate, 1);
		endDate = AonDateUtils.addSeconds(endDate, -1);
		
		Timestamp startTimestamp = new Timestamp(startDate.getTime());
		Timestamp endTimestamp = new Timestamp(endDate.getTime());
		
		return buildTimeControl(ctx, taskHolderId, getTimeControlDetailStream(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())
				.and(f.getDateProperty().ge(startTimestamp))
				.and(f.getDateProperty().le(endTimestamp))
				.and(f.getTaskHolderProperty().eq(taskHolderId))), null, null, null);
	}
	
	public static TimeControlDetail save(AONContext ctx, TimeControlDetail tcd) {
		return tcd.getId() != null 
			? update(ctx, tcd)
			: insert(ctx, tcd);
	}
	
	private static TimeControlDetail insert(AONContext ctx, TimeControlDetail tcd) {
		ctx.checkWrite();

	    InsertSetMoreStep<TimecontrolRecord> sets = ctx.getDslContext().insertInto(TIMECONTROL)
		.set(TIMECONTROL.DOMAIN, tcd.getDomain().getId())
		.set(TIMECONTROL.TASK_HOLDER, tcd.getTaskHolder().getId())
		.set(TIMECONTROL.STATUS, tcd.getStatus().value())
		.set(TIMECONTROL.DATE, new Timestamp(tcd.getDate().getTime()))
		.set(TIMECONTROL.COMMENTS, tcd.getComments())
		.set(TIMECONTROL.LOCATION, tcd.getLocation().getId())
		.set(TIMECONTROL.LOCATION_DESCRIPTION ,tcd.getLocationDescription())
		.set(TIMECONTROL.LATITUDE, tcd.getCoordinates().getLatitude())
		.set(TIMECONTROL.LONGITUDE, tcd.getCoordinates().getLongitude())
		.set(TIMECONTROL.CREATION_USER, ctx.getUser())
		.set(TIMECONTROL.CREATION_DATE, new Timestamp(new Date().getTime()))
		.set(TIMECONTROL.MODIFICATED_TIMECONTROL, tcd.getModificatedTimeControl());
		
		if(null != tcd.getId()) 
			sets.set(TIMECONTROL.ID, tcd.getId());
		
		if(null == tcd.getCause()) 
			sets.set(TIMECONTROL.CAUSE, DSL.castNull(TIMECONTROL.CAUSE));
		
		if(null != tcd.getCause() && null == tcd.getReason()) 
			sets.set(TIMECONTROL.CAUSE, tcd.getCause().value());
		
		if(null != tcd.getReason()) 
			sets.set(TIMECONTROL.CAUSE, tcd.getReason().value());
		
		if(null != tcd.getReason() && AonStringUtils.isBlank(tcd.getComments())) 
			sets.set(TIMECONTROL.COMMENTS, tcd.getReason().getDescription());
		
		Integer id = sets.returning(TIMECONTROL.ID).fetchOne().getValue(TIMECONTROL.ID);
		
		ctx.log().debug("INSERT TIMECONTROL id: " + id);	
		return tcd.setId(id);
	}
	
	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		ctx.getDslContext().delete(TIMECONTROL).where(TIMECONTROL.ID.eq(id)).or(TIMECONTROL.MODIFICATED_TIMECONTROL.eq(id)).execute();	
		ctx.log().debug("DELETE TIMECONTROL id: " +id);	
	}
	
	private static TimeControlDetail update(AONContext ctx, TimeControlDetail tcd) {
		ctx.checkWrite();
		
		boolean updateCoordinates = tcd.getCoordinates()!=null && tcd.getCoordinates().getLatitude()!=null &&  tcd.getCoordinates().getLongitude()!=null;
		
//		if(!updateCoordinates) {			
			saveLog(ctx, tcd);
//		}

		UpdateSetMoreStep<TimecontrolRecord> sets = ctx.getDslContext()
		.update(TIMECONTROL)
		.set(TIMECONTROL.DATE, new Timestamp(tcd.getDate().getTime()))
		.set(TIMECONTROL.COMMENTS, tcd.getComments())
		.set(TIMECONTROL.STATUS, tcd.getStatus().value())
		.set(TIMECONTROL.LOCATION ,tcd.getLocation()!=null ? tcd.getLocation().getId() : null)
		.set(TIMECONTROL.LOCATION_DESCRIPTION ,tcd.getLocationDescription())
		.set(TIMECONTROL.MODIFICATION_USER, ctx.getUser())
		.set(TIMECONTROL.MODIFICATION_DATE, new Timestamp(new Date().getTime()))
		;
		
		if(updateCoordinates) {
			sets.set(TIMECONTROL.LATITUDE, tcd.getCoordinates().getLatitude())
			.set(TIMECONTROL.LONGITUDE, tcd.getCoordinates().getLongitude());
		}
		
		sets.where(TIMECONTROL.ID.eq(tcd.getId()))
		.execute();		
		
		ctx.log().debug("UPDATE TIMECONTROL id: " + tcd.getId());	
	
		return tcd;
	}
	
	private static void saveLog(AONContext ctx, TimeControlDetail tcd) {
		try {
			TimeControlDetail timeControl = getLastTimeControlDetail(ctx, 
					f->f.getDomainProperty().eq(tcd.getDomain().getId()).and(f.getIdProperty().eq(tcd.getId()))
			);
		
			if( timeControl.isDirty(tcd) ) {
				Optional<TimecontrolRecord> last = select(ctx, 
					f-> f.getDomainProperty().eq(tcd.getDomain().getId())
					.and(f.getIdProperty().lt(0))
				)
				.orderBy(TIMECONTROL.ID.asc())
				.limit(1)
				.fetchOptionalInto(TIMECONTROL);
				
				Integer lastMinId = last.isPresent() ? last.get().getId() : 0;

				timeControl.setId(lastMinId-1).setModificatedTimeControl(tcd.getId());
				
				ctx.log().debug("--------INSERT LOG----------");	
				insert(ctx, timeControl);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	private static TimeControl buildTimeControl(AONContext ctx, Integer taskHolderId, Stream<TimeControlDetail> details, Date startDate, Date endDate, TimeControlGroup group) {
		TimeControl tc = new TimeControl().setTime(0L);
		details.forEach(r -> {
			if(tc.getStatus() == null) {
				tc.setInDate(AonDateUtils.getDateWithoutTime(r.getDate()));
			}
			if(TimeControlStatus.IN.equals(r.getStatus())) {
				tc.setInDate(r.getDate());
				tc.setStatus(r.getStatus());
			} else if(tc.getInDate() != null){
				tc.setTime(tc.getTime() + r.getDate().getTime() - tc.getInDate().getTime());
				tc.setInDate(null);
				tc.setStatus(r.getStatus());
			}

			if(tc.getTaskHolder() == null || tc.getTaskHolder().getId() == null) {
				tc.setTaskHolder(r.getTaskHolder());
			}
			tc.getDetail().add(r);
		});
		
		TimeControlDetail tcd = getLastTimeControlDetail(ctx, 
			f -> 
			f.getTaskHolderProperty().eq(taskHolderId)
			.and(f.getIdProperty().ge(0)));

		if(tc.getDetail().isEmpty() && TimeControlStatus.IN.equals(tcd.getStatus())  
			&& AonDateUtils.isSameDay(AonDateUtils.addDays(new Date(), -1), tcd.getDate())) {
			tc.setInDate(AonDateUtils.getDateWithoutTime(new Date()));
			tc.setStatus(TimeControlStatus.IN);
			tc.getDetail().add(new TimeControlDetail()
					.setDate(AonDateUtils.getDateWithoutTime(new Date()))
					.setStatus(TimeControlStatus.IN)
					.setDomain(tcd.getDomain()));
		} else if(TimeControlStatus.IN.equals(tc.getStatus()) 
			&& tc.getInDate().compareTo(AonDateUtils.getDateWithoutTime(new Date())) < 0) {
			Date d = AonDateUtils.addDays(tc.getInDate(), 1);
			Date date = AonDateUtils.addSeconds(AonDateUtils.getDateWithoutTime(d), -1);
			tc.setTime(tc.getTime() + date.getTime() - tc.getInDate().getTime());
			tc.setInDate(null);
			tc.setStatus(TimeControlStatus.OUT);
			tc.getDetail().add(new TimeControlDetail()
					.setDate(date)
					.setStatus(TimeControlStatus.OUT)
					.setDomain(tcd.getDomain()));
		} 
		else if (TimeControlStatus.IN.equals(tc.getStatus()) && AonDateUtils.isSameDay(tc.getInDate(), new Date())) {				
			// CASO NUEVO: IN abierto hoy - expandir hasta NOW
			
			Date now = new Date();
			tc.setTime(tc.getTime() + now.getTime() - tc.getInDate().getTime());
		}

		
		tc.setStatus(tcd.getStatus());
		
		tc.setLastCoordinates(tcd.getCoordinates());
		tc.setLastDate(tcd.getDate());
		tc.setLastLocation(tcd.getLocation());
		tc.setTaskHolder(TaskHolderDAO.get(ctx, f -> f.getIdProperty().eq(taskHolderId)));

		tc.setStartDate(startDate);
		tc.setEndDate(endDate);
		tc.setGroup(group);
		
		return tc;
	}
	*/
	
	private static TimeControl buildTimeControl(AONContext ctx, Integer taskHolderId, Stream<TimeControlDetail> details, Date startDate, Date endDate, TimeControlGroup group) {
		TimeControl tc = new TimeControl().setTime(0L);

		details.forEach(r -> {

		    if (tc.getStatus() == null) {
		        tc.setInDate(r.getDate());
		        tc.setStatus(r.getStatus());
		        tc.getDetail().add(r);
		        return;
		    }

		    boolean isOpen = tc.getInDate() != null;
		    boolean isInOrPause = TimeControlStatus.IN.equals(r.getStatus()) || TimeControlStatus.PAUSE.equals(r.getStatus());
		    boolean isOut = TimeControlStatus.OUT.equals(r.getStatus());

		    // CERRAR TRAMO si hay uno abierto y el estado cambia
		    if (isOpen && !tc.getStatus().equals(r.getStatus())) {
		        tc.setTime(tc.getTime() + (r.getDate().getTime() - tc.getInDate().getTime()));
		        tc.setInDate(null);
		    }

		    // ABRIR TRAMO si es IN o PAUSE
		    if (isInOrPause) {
		        tc.setInDate(r.getDate());
		    }

		    // OUT no abre tramo, solo cierra
		    tc.setStatus(r.getStatus());

		    if (tc.getTaskHolder() == null || tc.getTaskHolder().getId() == null) {
		        tc.setTaskHolder(r.getTaskHolder());
		    }

		    tc.getDetail().add(r);
		});
		
		TimeControlDetail tcd = getLastTimeControlDetail(ctx, 
			f -> 
			f.getTaskHolderProperty().eq(taskHolderId)
			.and(f.getIdProperty().ge(0)));

		if(tc.getDetail().isEmpty() && TimeControlStatus.IN.equals(tcd.getStatus())  
			&& AonDateUtils.isSameDay(AonDateUtils.addDays(new Date(), -1), tcd.getDate())) {
			tc.setInDate(AonDateUtils.getDateWithoutTime(new Date()));
			tc.setStatus(TimeControlStatus.IN);
			tc.getDetail().add(new TimeControlDetail()
					.setDate(AonDateUtils.getDateWithoutTime(new Date()))
					.setStatus(TimeControlStatus.IN)
					.setDomain(tcd.getDomain()));
		} else if(TimeControlStatus.IN.equals(tc.getStatus()) 
			&& tc.getInDate().compareTo(AonDateUtils.getDateWithoutTime(new Date())) < 0) {
			Date d = AonDateUtils.addDays(tc.getInDate(), 1);
			Date date = AonDateUtils.addSeconds(AonDateUtils.getDateWithoutTime(d), -1);
			tc.setTime(tc.getTime() + date.getTime() - tc.getInDate().getTime());
			tc.setInDate(null);
			tc.setStatus(TimeControlStatus.OUT);
			tc.getDetail().add(new TimeControlDetail()
					.setDate(date)
					.setStatus(TimeControlStatus.OUT)
					.setDomain(tcd.getDomain()));
		} 
		else if (TimeControlStatus.IN.equals(tc.getStatus()) && AonDateUtils.isSameDay(tc.getInDate(), new Date())) {				
			// CASO NUEVO: IN abierto hoy - expandir hasta NOW
			
			Date now = new Date();
			tc.setTime(tc.getTime() + now.getTime() - tc.getInDate().getTime());
		}

		
		tc.setStatus(tcd.getStatus());
		
		tc.setLastCoordinates(tcd.getCoordinates());
		tc.setLastDate(tcd.getDate());
		tc.setLastLocation(tcd.getLocation());
		tc.setTaskHolder(TaskHolderDAO.get(ctx, f -> f.getIdProperty().eq(taskHolderId)));

		tc.setStartDate(startDate);
		tc.setEndDate(endDate);
		tc.setGroup(group);
		
		return tc;
	}
	
	public static TimeControlContractEvents getTaskHolderTimeContractEvents(AONContext ctx, Integer taskHolderId, Date startDate, Date endDate) {
		TimeControlContractEvents contractEvents = new TimeControlContractEvents();
		
		List<TimeControlContractEvent> contractFestives = new ArrayList<TimeControlContractEvent>();
		List<TimeControlContractEvent> contractDaysType = new ArrayList<TimeControlContractEvent>();
		
		//List<TimeControlContractEvent> contractITs = new ArrayList<TimeControlContractEvent>();
		
		Result<ContractRecord> taskHolderContracts = ctx.getDslContext().selectFrom(CONTRACT)
				.where(CONTRACT.PERSON.eq(taskHolderId))
				.and(
					(
						CONTRACT.START_DATE.le(AonDateUtils.toSql(startDate))
						.and(CONTRACT.END_DATE.isNull().or(CONTRACT.END_DATE.ge(AonDateUtils.toSql(startDate))))
					).or(
						CONTRACT.START_DATE.le(AonDateUtils.toSql(endDate))
						.and(CONTRACT.END_DATE.isNull().or(CONTRACT.END_DATE.ge(AonDateUtils.toSql(endDate))))
					)
					
				)
				.fetch();
		
		if(!taskHolderContracts.isEmpty()) {
			
			Integer lastContractId = taskHolderContracts.get(taskHolderContracts.size() - 1).getId();
			
			contractEvents.setFullTime(getFullTimeJourney(ctx, lastContractId));
			contractEvents.setWorkingDays(getWorkingDays(ctx, lastContractId));
			contractEvents.setWorkingDaysHours(getWorkingDaysHours(ctx, lastContractId));
			contractEvents.setAnnualHolidays(getAnnualHolidays(ctx, lastContractId));
			
			getContractFestives(ctx, startDate, endDate, contractFestives, taskHolderContracts);
			getContractDaysTypes(ctx, startDate, endDate, contractDaysType, taskHolderContracts);
			getContractITs(ctx, startDate, endDate, contractDaysType, taskHolderContracts);
			
		}
		
		contractEvents.setFestives(contractFestives);
		contractEvents.setContractDaysType(contractDaysType);
		//contractEvents.setContractITs(contractITs);
		
		return contractEvents;
	}
	private static Boolean getFullTimeJourney(AONContext ctx, Integer contractId) {
		String journeyTypeEmployee = ctx.getDslContext().select()
				  .from(CONTRACT_DATA)
				  .where(CONTRACT_DATA.CONTRACT.eq(contractId))
				  .and(CONTRACT_DATA.NAME.like("TC2"))
				  .orderBy(CONTRACT_DATA.START_DATE.desc())
				  .fetchStreamInto(CONTRACT_DATA)
				  .map( data -> data.getExpression())
				  .filter(tc2 -> AonStringUtils.isNotBlank(tc2))
				  .findFirst()
				  .orElse("true");
		
		return isFullTimeJourney(journeyTypeEmployee);
	}
	
	private static Boolean isFullTimeJourney(String tc2) {
		if(AonStringUtils.isBlank(tc2))
			return false;
		
		parseContractData(tc2);
		
		try {
			Integer contractType = Integer.parseInt(tc2);
			return !AonNumberUtils.between(contractType, 200, 300) && !AonNumberUtils.between(contractType, 500, 599) && !AonNumberUtils.equals(contractType, 0);
		} catch (NumberFormatException e) {
			return '1' == tc2.charAt(1) || '4' == tc2.charAt(1)|| "true".equals(tc2);
		}
	}
	
	private static String parseContractData(String contractType) {
		if(AonStringUtils.isNotBlank(contractType) && contractType.contains("\""))
			try {
				contractType = contractType.split("\"")[1];
				return contractType;
			} catch (IndexOutOfBoundsException e) {
				return contractType;
			}	
		else
			return contractType;
	}

	private static void getContractFestives(AONContext ctx, Date startDate, Date endDate, List<TimeControlContractEvent> contractFestives, Result<ContractRecord> taskHolderContracts) {
		taskHolderContracts.forEach(taskHolderContract -> {
			
			Record contractCalendarRecord = ctx.getDslContext().select(DSL.ifnull(CONTRACT.CALENDAR, PAYROLL_WORKPLACE.CALENDAR).as(CONTRACT.CALENDAR))
					  .from(CONTRACT)
					  .innerJoin(PAYROLL_WORKPLACE)
					  .on(CONTRACT.WORKPLACE.eq(PAYROLL_WORKPLACE.WORKPLACE))
					  .where(CONTRACT.ID.eq(taskHolderContract.getId()))
					  .fetchOne();
			
			Integer calendarId = null == contractCalendarRecord ? null : contractCalendarRecord.get(CONTRACT.CALENDAR);
			
			if(null != calendarId) {
				
				Integer holidayId = ctx.getDslContext().select(CALENDAR.HOLIDAY).from(CALENDAR)
						.where(CALENDAR.ID.eq(calendarId))
						.fetchOne(CALENDAR.HOLIDAY);
				
				ArrayList<Integer> holidays = new ArrayList<>();
				
				// Get all holidays ids
				while ( holidayId != null ) {
					holidays.add(holidayId);
					
					holidayId = ctx.getDslContext().select(HOLIDAY.HOLIDAY_)
							.from(HOLIDAY)
							.where(HOLIDAY.ID.eq(holidayId))
							.fetchOne()
							.get(HOLIDAY.HOLIDAY_);
				}
				
				Result<Record> holidayRecords = ctx.getDslContext().select()
						.from(HOLIDAY_DETAIL)
						.join(HOLIDAY).on(HOLIDAY.ID.eq(HOLIDAY_DETAIL.HOLIDAY))
						.where(HOLIDAY_DETAIL.HOLIDAY.in(holidays))
						.and(HOLIDAY_DETAIL.DATE.between(AonDateUtils.toSql(startDate), AonDateUtils.toSql(endDate)))
						.fetch();
				
				for(Record holidayRecord : holidayRecords) {
					TimeControlContractEvent timeControlContractEvent = new TimeControlContractEvent()
							.setSource(TimeControlContractEventSource.FESTIVE)
							.setStartDate(parseDateSqlToUtil(holidayRecord.get(HOLIDAY_DETAIL.DATE)))
							.setEndDate(parseDateSqlToUtil(holidayRecord.get(HOLIDAY_DETAIL.DATE)))
							.setDescription(holidayRecord.get(HOLIDAY_DETAIL.DESCRIPTION))
							//.setAdditionalInfo(holidayRecord.get(HOLIDAY.DESCRIPTION))
							;
					
					contractFestives.add(timeControlContractEvent);
				}
			}
			
		});
	}
	
	private static void getContractDaysTypes(AONContext ctx, Date startDate, Date endDate, List<TimeControlContractEvent> contractDaysType, Result<ContractRecord> taskHolderContracts) {
		taskHolderContracts.forEach(taskHolderContract -> {
			
			Result<Record> daysTypeRecords = ctx.getDslContext()
					.select()
					.from(CONTRACT_DATA)
					.where(CONTRACT_DATA.CONTRACT.eq(taskHolderContract.getId()))
					.and(CONTRACT_DATA.NAME.in(
							"DIAS_VACACIONES"
							,"NO_LABORABLE"
							,"DIAS_EFECTIVOS"
							,"JORNADAS_REALES"
							,"JORNADAS_TEORICAS"
							,"LABORABLE"
							//Coeficientes
							,"COEFICIENTE_ERE"
							,"COEFICIENTE_HUELGA"
							,"COEFICIENTE_ERE_FZA"
							,"COEFICIENTE_ERE_FZA_EXONERADO"
							//,"FIN_ERE_FZA_EXONERADO"
							,"COEFICIENTE_AUSENCIA"
							,"CAUSA_INACTIVIDAD"
							,"PERMISO_RETRIBUIDO"
							,"COEFICIENTE_PARCIALIDAD"))
					.and(CONTRACT_DATA.START_DATE.ge(AonDateUtils.toSql(startDate)).and(CONTRACT_DATA.START_DATE.le(AonDateUtils.toSql(endDate))))
					.and(CONTRACT_DATA.END_DATE.isNull().or(CONTRACT_DATA.END_DATE.ge(AonDateUtils.toSql(startDate))))
					.fetch();
			
			for(Record daysTypeRecord : daysTypeRecords){
				TimeControlContractEvent timeControlContractEvent = new TimeControlContractEvent()
						.setSource(TimeControlContractEventSource.parseContractDataName(daysTypeRecord.get(CONTRACT_DATA.NAME)))
						.setStartDate(parseDateSqlToUtil(daysTypeRecord.get(CONTRACT_DATA.START_DATE)))
						.setEndDate(parseDateSqlToUtil(daysTypeRecord.get(CONTRACT_DATA.END_DATE)))
						.setDescription(daysTypeRecord.get(CONTRACT_DATA.EXPRESSION))
						;
				
				contractDaysType.add(timeControlContractEvent);
			}
		});
	}
	
	private static void getContractITs(AONContext ctx, Date startDate, Date endDate, List<TimeControlContractEvent> contractITs, Result<ContractRecord> taskHolderContracts) {
		taskHolderContracts.forEach(taskHolderContract -> {
			
			Result<Record> itDaysRecords = ctx.getDslContext().select().from(CONTRACT_LEAVE)
					.where(CONTRACT_LEAVE.CONTRACT.eq(taskHolderContract.getId()))
					.fetch();
			
			for(Record itDaysRecord : itDaysRecords){
				TimeControlContractEvent timeControlContractEvent = new TimeControlContractEvent()
						.setSource(TimeControlContractEventSource.IT)
						.setStartDate(parseDateSqlToUtil(itDaysRecord.get(CONTRACT_LEAVE.START_DATE)))
						.setEndDate(parseDateSqlToUtil(itDaysRecord.get(CONTRACT_LEAVE.END_DATE)))
						.setDescription("Baja IT")
						;
				
				contractITs.add(timeControlContractEvent);
			}
		});
	}
	
	private static Byte[] getWorkingDays(AONContext ctx, Integer contractId) {
		Byte[] workingDays = new Byte[7];
		
		Record contractCalendarRecord = ctx.getDslContext().select(DSL.ifnull(CONTRACT.CALENDAR, PAYROLL_WORKPLACE.CALENDAR).as(CONTRACT.CALENDAR))
				  .from(CONTRACT)
				  .innerJoin(PAYROLL_WORKPLACE)
				  .on(CONTRACT.WORKPLACE.eq(PAYROLL_WORKPLACE.WORKPLACE))
				  .where(CONTRACT.ID.eq(contractId))
				  .fetchOne();
		
		if (null != contractCalendarRecord){
				
			// Si vale 0 es laborable y si vale 1 es no laborables
			
			Result<Record> workingDaysRecords = ctx.getDslContext().select().from(CONTRACT_DATA)
					.where(CONTRACT_DATA.CONTRACT.eq(contractId))
					.and(CONTRACT_DATA.NAME.in(
							"LABORABLE_LUNES",
							"LABORABLE_MARTES",
							"LABORABLE_MIERCOLES",
							"LABORABLE_JUEVES",
							"LABORABLE_VIERNES",
							"LABORABLE_SABADO",
							"LABORABLE_DOMINGO"
					)).fetch();
			
			if(workingDaysRecords.isEmpty()) {
				
				// Default calendar working days
				
				Integer calendarId = contractCalendarRecord.get(CONTRACT.CALENDAR);
				
				Result<Record> calendarRecords = ctx.getDslContext().select().from(CALENDAR)
						.where(CALENDAR.ID.eq(calendarId))
						.fetch();
				
				for(Record calendarRecord : calendarRecords){
					workingDays[0] = calendarRecord.get(CALENDAR.SUNDAY);
					workingDays[1] = calendarRecord.get(CALENDAR.MONDAY);
					workingDays[2] = calendarRecord.get(CALENDAR.TUESDAY);
					workingDays[3] = calendarRecord.get(CALENDAR.WEDNESDAY);
					workingDays[4] = calendarRecord.get(CALENDAR.THURSDAY);
					workingDays[5] = calendarRecord.get(CALENDAR.FRIDAY);
					workingDays[6] = calendarRecord.get(CALENDAR.SATURDAY);
				}
				
			} else {
				
				for(Record workingDaysRecord : workingDaysRecords) {
					String name = workingDaysRecord.get(CONTRACT_DATA.NAME);
					switch (name) {
						case "LABORABLE_DOMINGO":
							workingDays[0] = Byte.parseByte(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "LABORABLE_LUNES":
							workingDays[1] = Byte.parseByte(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "LABORABLE_MARTES":
							workingDays[2] = Byte.parseByte(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "LABORABLE_MIERCOLES":
							workingDays[3] = Byte.parseByte(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "LABORABLE_JUEVES":
							workingDays[4] = Byte.parseByte(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "LABORABLE_VIERNES":
							workingDays[5] = Byte.parseByte(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "LABORABLE_SABADO":
							workingDays[6] = Byte.parseByte(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						default:
							break;
					}
				}
			}	
		
		} else {
			
			// Si vale 0 es laborable y si vale 1 es no laborables
			Result<Record> workingDaysRecords = ctx.getDslContext().select().from(CONTRACT_DATA)
					.where(CONTRACT_DATA.CONTRACT.eq(contractId))
					.and(CONTRACT_DATA.NAME.in(
							"LABORABLE_LUNES",
							"LABORABLE_MARTES",
							"LABORABLE_MIERCOLES",
							"LABORABLE_JUEVES",
							"LABORABLE_VIERNES",
							"LABORABLE_SABADO",
							"LABORABLE_DOMINGO"
					)).fetch();
			
			if(workingDaysRecords.isEmpty()) {
				
				workingDays[0] = 1;
				workingDays[1] = 0;
				workingDays[2] = 0;
				workingDays[3] = 0;
				workingDays[4] = 0;
				workingDays[5] = 0;
				workingDays[6] = 1;
			
			} else {
				
				for(Record workingDaysRecord : workingDaysRecords) {
					String name = workingDaysRecord.get(CONTRACT_DATA.NAME);
					switch (name) {
						case "LABORABLE_DOMINGO":
							workingDays[0] = Byte.parseByte(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "LABORABLE_LUNES":
							workingDays[1] = Byte.parseByte(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "LABORABLE_MARTES":
							workingDays[2] = Byte.parseByte(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "LABORABLE_MIERCOLES":
							workingDays[3] = Byte.parseByte(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "LABORABLE_JUEVES":
							workingDays[4] = Byte.parseByte(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "LABORABLE_VIERNES":
							workingDays[5] = Byte.parseByte(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "LABORABLE_SABADO":
							workingDays[6] = Byte.parseByte(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						default:
							break;
					}
				}
			}
		}
		
		return workingDays;
	}
	
	private static Double[] getWorkingDaysHours(AONContext ctx, Integer contractId) {
		Double[] workingDays = new Double[7];
		
		Record contractCalendarRecord = ctx.getDslContext().select(DSL.ifnull(CONTRACT.CALENDAR, PAYROLL_WORKPLACE.CALENDAR).as(CONTRACT.CALENDAR))
				  .from(CONTRACT)
				  .innerJoin(PAYROLL_WORKPLACE)
				  .on(CONTRACT.WORKPLACE.eq(PAYROLL_WORKPLACE.WORKPLACE))
				  .where(CONTRACT.ID.eq(contractId))
				  .fetchOne();
		
		if (null != contractCalendarRecord){
				
			Result<Record> workingDaysRecords = ctx.getDslContext().select().from(CONTRACT_DATA)
					.where(CONTRACT_DATA.CONTRACT.eq(contractId))
					.and(CONTRACT_DATA.NAME.in(
							"HORAS_LUNES",
							"HORAS_MARTES",
							"HORAS_MIERCOLES",
							"HORAS_JUEVES",
							"HORAS_VIERNES",
							"HORAS_SABADO",
							"HORAS_DOMINGO"
					)).fetch();
			
			if(workingDaysRecords.isEmpty()) {
				
				// Default calendar working days
				
				Integer calendarId = contractCalendarRecord.get(CONTRACT.CALENDAR);
				
				Result<Record> calendarRecords = ctx.getDslContext().select().from(CALENDAR)
						.where(CALENDAR.ID.eq(calendarId))
						.fetch();
				
				for(Record calendarRecord : calendarRecords){
					workingDays[0] = calendarRecord.get(CALENDAR.SUNDAY_HOURS);
					workingDays[1] = calendarRecord.get(CALENDAR.MONDAY_HOURS);
					workingDays[2] = calendarRecord.get(CALENDAR.TUESDAY_HOURS);
					workingDays[3] = calendarRecord.get(CALENDAR.WEDNESDAY_HOURS);
					workingDays[4] = calendarRecord.get(CALENDAR.THURSDAY_HOURS);
					workingDays[5] = calendarRecord.get(CALENDAR.FRIDAY_HOURS);
					workingDays[6] = calendarRecord.get(CALENDAR.SATURDAY_HOURS);
				}
				
			} else {
				
				for(Record workingDaysRecord : workingDaysRecords) {
					String name = workingDaysRecord.get(CONTRACT_DATA.NAME);
					switch (name) {
						case "HORAS_DOMINGO":
							workingDays[0] = Double.parseDouble(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "HORAS_LUNES":
							workingDays[1] = Double.parseDouble(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "HORASE_MARTES":
							workingDays[2] = Double.parseDouble(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "HORAS_MIERCOLES":
							workingDays[3] = Double.parseDouble(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "LHORAS_JUEVES":
							workingDays[4] = Double.parseDouble(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "HORAS_VIERNES":
							workingDays[5] = Double.parseDouble(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "HORAS_SABADO":
							workingDays[6] = Double.parseDouble(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						default:
							break;
					}
				}
			}	
		
		} else {
			
			// Si vale 0 es laborable y si vale 1 es no laborables
			Result<Record> workingDaysRecords = ctx.getDslContext().select().from(CONTRACT_DATA)
					.where(CONTRACT_DATA.CONTRACT.eq(contractId))
					.and(CONTRACT_DATA.NAME.in(
							"HORAS_LUNES",
							"HORAS_MARTES",
							"HORAS_MIERCOLES",
							"HORAS_JUEVES",
							"HORAS_VIERNES",
							"HORAS_SABADO",
							"HORAS_DOMINGO"
					)).fetch();
			
			if(workingDaysRecords.isEmpty()) {
				
				workingDays[0] = 0.00;
				workingDays[1] = 8.00;
				workingDays[2] = 8.00;
				workingDays[3] = 8.00;
				workingDays[4] = 8.00;
				workingDays[5] = 8.00;
				workingDays[6] = 0.00;
			
			} else {
				
				for(Record workingDaysRecord : workingDaysRecords) {
					String name = workingDaysRecord.get(CONTRACT_DATA.NAME);
					switch (name) {
						case "HORAS_DOMINGO":
							workingDays[0] = Double.parseDouble(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "HORAS_LUNES":
							workingDays[1] = Double.parseDouble(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "HORAS_MARTES":
							workingDays[2] = Double.parseDouble(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "HORAS_MIERCOLES":
							workingDays[3] = Double.parseDouble(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "HORAS_JUEVES":
							workingDays[4] = Double.parseDouble(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "HORAS_VIERNES":
							workingDays[5] = Double.parseDouble(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						case "HORAS_SABADO":
							workingDays[6] = Double.parseDouble(workingDaysRecord.get(CONTRACT_DATA.EXPRESSION));
							break;
						default:
							break;
					}
				}
			}
		}
		
		return workingDays;
	}
	
	private static Double getAnnualHolidays(AONContext ctx, Integer contractId) {
		Record contractCalendarRecord = ctx.getDslContext().select(DSL.ifnull(CONTRACT.CALENDAR, PAYROLL_WORKPLACE.CALENDAR).as(CONTRACT.CALENDAR))
				  .from(CONTRACT)
				  .innerJoin(PAYROLL_WORKPLACE)
				  .on(CONTRACT.WORKPLACE.eq(PAYROLL_WORKPLACE.WORKPLACE))
				  .where(CONTRACT.ID.eq(contractId))
				  .fetchOne();
		
		if (null != contractCalendarRecord){
			
			Integer calendarId = contractCalendarRecord.get(CONTRACT.CALENDAR);
			
			Result<Record> calendarRecords = ctx.getDslContext().select().from(CALENDAR)
					.where(CALENDAR.ID.eq(calendarId))
					.fetch();
			
			if(!calendarRecords.isEmpty()) {
				Record calendarRecord = calendarRecords.get(0);
				return calendarRecord.get(CALENDAR.ANNUAL_HOLIDAYS);
			}
			
		}
		
		return 0.00;
	}
	
	private static java.util.Date parseDateSqlToUtil(Date date) {
		if(null == date)
			return null;
		
		java.util.Date javaDate = new java.util.Date(date.getTime());
		
		return javaDate;
	}

	public static class TimeControlDetailFiller implements Function<Record, TimeControlDetail> {

		@Override
		public TimeControlDetail apply(Record record) {
			Location location = new Location()
					.setId(record.getValue(LOCATION.ID))
					.setDomain(new Domain().setId(record.getValue(LOCATION.DOMAIN)))
					.setCoordinates(new Coordinates(record.getValue(LOCATION.LATITUDE), record.getValue(LOCATION.LONGITUDE)))
					.setDescription(record.getValue(LOCATION.DESCRIPTION))
					.setRadio(record.getValue(LOCATION.RADIO));
			
			return new TimeControlDetail()
					.setId(record.getValue(TIMECONTROL.ID))
					.setDomain(new Domain().setId(record.getValue(TIMECONTROL.DOMAIN)))
					.setDate(record.getValue(TIMECONTROL.DATE))
					.setStatus(TimeControlStatus.safeValueOf(record.getValue(TIMECONTROL.STATUS)))
					.setTaskHolder(TaskHolderFiller.build(record))
					.setLocation(location)
					.setLocationDescription(record.get(TIMECONTROL.LOCATION_DESCRIPTION))
					.setComments(record.getValue(TIMECONTROL.COMMENTS))
					.setCoordinates(new Coordinates(record.getValue(TIMECONTROL.LATITUDE),record.getValue(TIMECONTROL.LONGITUDE)))
					.setReason(null == record.get(TIMECONTROL.CAUSE) ? null : TimeControlReason.values()[record.get(TIMECONTROL.CAUSE)])
					
					.setCreationDate(record.getValue(TIMECONTROL.CREATION_DATE))
					.setCreationUser(record.getValue(TIMECONTROL.CREATION_USER))
					.setModificationDate(record.getValue(TIMECONTROL.MODIFICATION_DATE))
					.setModificationUser(record.getValue(TIMECONTROL.MODIFICATION_USER))
					.setModificatedTimeControl(record.getValue(TIMECONTROL.MODIFICATED_TIMECONTROL))
					;	
		}
		
	}
}
