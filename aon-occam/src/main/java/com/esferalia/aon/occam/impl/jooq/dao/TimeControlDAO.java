package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Location.LOCATION;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.TaskHolder.TASK_HOLDER;
import static com.esferalia.aon.jooq.tables.Timecontrol.TIMECONTROL;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Record;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.TimeControlFilter;
import com.esferalia.aon.occam.api.model.aonsolutions.Coordinates;
import com.esferalia.aon.occam.api.model.aonsolutions.Location;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControl;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlDetail;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlGroup;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlStatus;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.TimeControlPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TaskDAO.TaskHolderFiller;
import com.esferalia.aon.watson.server.AonDateUtils;

public class TimeControlDAO {	

	private static final TimeControlPropertiesDAO TIMECONTROL_PROPERTIES = new TimeControlPropertiesDAO();

	
	public static Stream<TimeControlDetail> getTimeControlDetailStream(AONContext ctx, TimeControlFilter filter) {
		ctx.checkRead();
		return ctx.getDslContext()
			.select()
			.from(TIMECONTROL)
			.join(TASK_HOLDER).on(TASK_HOLDER.REGISTRY.eq(TIMECONTROL.TASK_HOLDER))
			.join(REGISTRY).on(REGISTRY.ID.eq(TASK_HOLDER.REGISTRY))
			.leftOuterJoin(LOCATION).on(LOCATION.ID.eq(TIMECONTROL.LOCATION))
			.where(TIMECONTROL_PROPERTIES.getConditions(filter))
			.fetch().stream().map(new TimeControlDetailFiller());
	}
	
	public static LinkedList<TimeControlDetail> getTimeControlDetailList(AONContext ctx, TimeControlFilter filter) {
		return getTimeControlDetailStream(ctx, filter).collect(Collectors.toCollection(LinkedList::new));
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
		
		list.stream().map(r -> r.getTaskHolder()).distinct().forEach(th -> {
			TimeControl tc = buildTimeControl(list.stream().filter(f -> f.getTaskHolder().getId().equals(th.getId())));
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
			list.stream().map(r -> AonDateUtils.getDateWithoutTime(r.getDate())).distinct().forEach(date -> {
				Date aDate = AonDateUtils.getDateWithoutTime(date);
				Date bDate = AonDateUtils.addDays(aDate, 1);
				Date cDate = AonDateUtils.addSeconds(bDate, -1);
				TimeControl tc = buildTimeControl(list.stream().filter(f -> (f.getDate().compareTo(aDate) >= 0 && f.getDate().compareTo(cDate) <= 0)));
				tcList.add(tc);
			});
		} else if(TimeControlGroup.WEEK.equals(group)) {
			list.stream().map(r -> AonDateUtils.getFirstDayOfWeek(AonDateUtils.getDateWithoutTime(r.getDate()))).distinct().forEach(date -> {
				Date bDate = AonDateUtils.addDays(date, 7);
				Date cDate = AonDateUtils.addSeconds(bDate, -1);
				TimeControl tc = buildTimeControl(list.stream().filter(f -> (f.getDate().compareTo(date) >= 0 && f.getDate().compareTo(cDate) <= 0)));
				tcList.add(tc);
			});
		} else if(TimeControlGroup.MONTH.equals(group)) {
			list.stream().map(r -> AonDateUtils.getMonth(r.getDate()) +"/"+ AonDateUtils.getYear(r.getDate())).distinct().forEach(date -> {
				String[] a = date.split("/");
				TimeControl tc = buildTimeControl(list.stream().filter(f -> (
						AonDateUtils.getMonth(f.getDate()) == Integer.parseInt(a[0])
						&& AonDateUtils.getYear(f.getDate()) == Integer.parseInt(a[1]))));
				tcList.add(tc);
			});
		} else if(TimeControlGroup.YEAR.equals(group)) {
			list.stream().map(r -> AonDateUtils.getYear(r.getDate())).distinct().forEach(year -> {
				TimeControl tc = buildTimeControl(list.stream().filter(f -> AonDateUtils.getYear(f.getDate()) == year));
				tcList.add(tc);
			});
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
		
		return buildTimeControl(getTimeControlDetailStream(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())
				.and(f.getDateProperty().ge(startTimestamp))
				.and(f.getDateProperty().le(endTimestamp))
				.and(f.getTaskHolderProperty().eq(taskHolderId))));
	}
	
	public static TimeControlDetail saveTimeControlDetail(AONContext ctx, TimeControlDetail tcd) {
		return tcd.getId() != null 
			? updateTimeControlDetail(ctx, tcd)
			: insertTimeControlDetail(ctx, tcd);
	}
	
	public static TimeControlDetail insertTimeControlDetail(AONContext ctx, TimeControlDetail tcd) {
		ctx.checkWrite();
		Integer id = ctx.getDslContext()
			.insertInto(TIMECONTROL, TIMECONTROL.DOMAIN, TIMECONTROL.TASK_HOLDER, TIMECONTROL.STATUS,
				TIMECONTROL.DATE, TIMECONTROL.COMMENTS, TIMECONTROL.LOCATION, TIMECONTROL.LATITUDE, TIMECONTROL.LONGITUDE)
			.values(tcd.getDomain().getId(), tcd.getTaskHolder().getId(), tcd.getStatus().value(),
					new Timestamp(tcd.getDate().getTime()), tcd.getComments(), tcd.getLocation().getId(),
					tcd.getCoordinates().getLatitude(), tcd.getCoordinates().getLongitude())
			.execute();
		
		return tcd.setId(id);
	}
	
	public static TimeControlDetail updateTimeControlDetail(AONContext ctx, TimeControlDetail tcd) {
		ctx.checkWrite();
		ctx.getDslContext()
			.update(TIMECONTROL)
			.set(TIMECONTROL.DATE, new Timestamp(tcd.getDate().getTime()))
			.set(TIMECONTROL.COMMENTS, tcd.getComments())
			.set(TIMECONTROL.LOCATION, tcd.getLocation().getId())
			.where(TIMECONTROL.ID.eq(tcd.getId()))
			.execute();		
		return tcd;
	}
	
	private static TimeControl buildTimeControl(Stream<TimeControlDetail> details) {
		TimeControl tc = new TimeControl().setTime(0L);
		details.forEach(r -> {
			tc.setLastDate(AonDateUtils.getDateWithoutTime(r.getDate()));
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
		return tc;
	}

	public static class TimeControlDetailFiller  implements Function<Record, TimeControlDetail> {

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
					.setTaskHolder(TaskHolderFiller.buildTaskHolder(record))
					.setLocation(location)
					.setComments(record.getValue(TIMECONTROL.COMMENTS))
					.setCoordinates(new Coordinates(record.getValue(TIMECONTROL.LATITUDE),record.getValue(TIMECONTROL.LONGITUDE)));
					
		}
		
	}
	
}
