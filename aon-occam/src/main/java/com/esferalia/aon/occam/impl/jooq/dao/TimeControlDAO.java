package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Location.LOCATION;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.TaskHolder.TASK_HOLDER;
import static com.esferalia.aon.jooq.tables.Timecontrol.TIMECONTROL;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Field;
import org.jooq.InsertSetMoreStep;
import org.jooq.Param;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.UpdateSetMoreStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.TimecontrolRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.TimeControlFilter;
import com.esferalia.aon.occam.api.model.aonsolutions.Coordinates;
import com.esferalia.aon.occam.api.model.aonsolutions.Location;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControl;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlDetail;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlGroup;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlStatus;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.TimeControlPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TaskHolderDAO.TaskHolderFiller;
import com.esferalia.aon.watson.server.AonDateUtils;

public class TimeControlDAO {	

	private static final TimeControlPropertiesDAO TIMECONTROL_PROPERTIES = new TimeControlPropertiesDAO();
	
	public static SelectConditionStep<Record> select(AONContext ctx, TimeControlFilter filter){	
		return ctx.getDslContext()
				.select()
				.from(TIMECONTROL)
				.join(TASK_HOLDER).on(TASK_HOLDER.REGISTRY.eq(TIMECONTROL.TASK_HOLDER))
				.join(REGISTRY).on(REGISTRY.ID.eq(TASK_HOLDER.REGISTRY))
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
		
		TaskOldDAO.getTaskHolderStream(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId())
			.and(f.getUserIdProperty().isNotNull())).forEach(th -> {
				TimeControl tc = buildTimeControl(ctx, th.getId(), list.stream().filter(f -> f.getTaskHolder().getId().equals(th.getId())), null, null, null);
				tcList.add(tc);
			});
		return tcList.stream();
	}
	
	public static Stream<TimeControl> getTimeControlEmployeeStream(AONContext ctx, Date startDate, Date endDate, Integer page, Integer perPage) {
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
		
		TaskOldDAO.getTaskHolderEmployee(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId())
			.and(f.getUserIdProperty().isNotNull()), page, perPage).forEach(th -> {
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
			f.getDomainProperty().eq(ctx.getDomainId())
			.and(f.getDateProperty().ge(startTimestamp))
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
//			list.stream().map(r -> AonDateUtils.getDateWithoutTime(r.getDate())).distinct().forEach(date -> {
//				Date aDate = AonDateUtils.getDateWithoutTime(date);
//				Date bDate = AonDateUtils.addDays(aDate, 1);
//				Date cDate = AonDateUtils.addSeconds(bDate, -1);
//				TimeControl tc = buildTimeControl(ctx, taskHolderId, list.stream().filter(f -> (f.getDate().compareTo(aDate) >= 0 && f.getDate().compareTo(cDate) <= 0)));
//				tcList.add(tc);
//			});
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
//			list.stream().map(r -> AonDateUtils.getFirstDayOfWeek(AonDateUtils.getDateWithoutTime(r.getDate()))).distinct().forEach(date -> {
//				Date bDate = AonDateUtils.addDays(date, 7);
//				Date cDate = AonDateUtils.addSeconds(bDate, -1);
//				TimeControl tc = buildTimeControl(ctx, taskHolderId, list.stream().filter(f -> (f.getDate().compareTo(date) >= 0 && f.getDate().compareTo(cDate) <= 0)));
//				tcList.add(tc);
//			});
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
//			list.stream().map(r -> AonDateUtils.getMonth(r.getDate()) +"/"+ AonDateUtils.getYear(r.getDate())).distinct().forEach(date -> {
//				String[] a = date.split("/");
//				TimeControl tc = buildTimeControl(ctx, taskHolderId, list.stream().filter(f -> (
//						AonDateUtils.getMonth(f.getDate()) == Integer.parseInt(a[0])
//						&& AonDateUtils.getYear(f.getDate()) == Integer.parseInt(a[1]))));
//				tcList.add(tc);
//			});
		} else if(TimeControlGroup.YEAR.equals(group)) {
			Date date = AonDateUtils.getYearFirstDay(startDate);
			while(date.compareTo(endDate) <= 0 ) {
				int year = AonDateUtils.getYear(date);
				Date zDate = AonDateUtils.getYearLastDay(date);
				TimeControl tc = buildTimeControl(ctx, taskHolderId, list.stream().filter(f -> AonDateUtils.getYear(f.getDate()) == year), date, zDate, TimeControlGroup.YEAR);
				tcList.add(tc);
				date = AonDateUtils.addYears(date, 1);
			}
//			list.stream().map(r -> AonDateUtils.getYear(r.getDate())).distinct().forEach(year -> {
//				TimeControl tc = buildTimeControl(ctx, taskHolderId, list.stream().filter(f -> AonDateUtils.getYear(f.getDate()) == year));
//				tcList.add(tc);
//			});
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
		.set(TIMECONTROL.LATITUDE, tcd.getCoordinates().getLatitude())
		.set(TIMECONTROL.LONGITUDE, tcd.getCoordinates().getLongitude())
		.set(TIMECONTROL.CREATION_USER, ctx.getUser())
		.set(TIMECONTROL.CREATION_DATE, new Timestamp(new Date().getTime()))
		.set(TIMECONTROL.MODIFICATED_TIMECONTROL, tcd.getModificatedTimeControl());
		
		if(null != tcd.getId()) 
			sets.set(TIMECONTROL.ID, tcd.getId());
		
		if(null != tcd.getCause()) 
			sets.set(TIMECONTROL.CAUSE, tcd.getCause().value());
		
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
		
		TimeControlDetail tcd = getLastTimeControlDetail(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId())
			.and(f.getTaskHolderProperty().eq(taskHolderId)).and(f.getIdProperty().ge(0)));

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
		
		tc.setLastCoordinates(tcd.getCoordinates());
		tc.setLastDate(tcd.getDate());
		tc.setLastLocation(tcd.getLocation());
		tc.setTaskHolder(TaskOldDAO.getTaskHolderStream(ctx, f -> f.getIdProperty().eq(taskHolderId)).findFirst().orElse(new TaskHolder()));

		tc.setStartDate(startDate);
		tc.setEndDate(endDate);
		tc.setGroup(group);
		
		return tc;
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
					.setTaskHolder(TaskHolderFiller.build(record, null))
					.setLocation(location)
					.setComments(record.getValue(TIMECONTROL.COMMENTS))
					.setCoordinates(new Coordinates(record.getValue(TIMECONTROL.LATITUDE),record.getValue(TIMECONTROL.LONGITUDE)))
					
					.setCreationDate(record.getValue(TIMECONTROL.CREATION_DATE))
					.setCreationUser(record.getValue(TIMECONTROL.CREATION_USER))
					.setModificationDate(record.getValue(TIMECONTROL.MODIFICATION_DATE))
					.setModificationUser(record.getValue(TIMECONTROL.MODIFICATION_USER))
					.setModificatedTimeControl(record.getValue(TIMECONTROL.MODIFICATED_TIMECONTROL))
					;	
		}
		
	}
}
