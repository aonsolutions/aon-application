package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Alarm.ALARM;
import static com.esferalia.aon.jooq.tables.Notice.NOTICE;
import static com.esferalia.aon.jooq.tables.User.USER;

import java.util.function.Function;

import org.jooq.Record;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Alarm;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.NoticeStatus;
import com.esferalia.aon.occam.api.model.type.NoticeType;
import com.esferalia.aon.occam.api.model.type.Priority;
import com.esferalia.aon.watson.server.AonEnumUtils;

public class GroupwareDAO {

	
	public static Notice getNotice(AONContext ctx, Integer id) {
		Record record = ctx.getDslContext()
				.select(NOTICE.fields())
				.from(NOTICE)
				.where(NOTICE.ID.eq(id))
				.fetchOne();
		Notice notice = new FullNoticeFiller().apply(record);
		return notice;
	}
	
	public static Alarm getAlarm(AONContext ctx, Integer id) {
		Record record = ctx.getDslContext()
				.select(ALARM.fields())
				.from(ALARM)
				.where(ALARM.ID.eq(id))
				.fetchOne();
		Alarm alarm = new FullAlarmFiller().apply(record);
		return alarm;
	}

	
	
	public static Integer insertNotice(AONContext ctx, Notice notice) {
		java.sql.Timestamp date = new java.sql.Timestamp(notice.getStartDate().getTime());
		
		return ctx.getDslContext()
				.insertInto(NOTICE)
				.set(NOTICE.DOMAIN, ctx.getDomainId())
				.set(NOTICE.DATE, date)						
				.set(NOTICE.SENDER, notice.getSender()!=null?notice.getSender().getId():null)
				.set(NOTICE.RECIPIENT, notice.getRecipient()!=null?notice.getRecipient().getId():null)
				.set(NOTICE.SUBJECT, notice.getTitle())
				.set(NOTICE.STATUS, NoticeStatus.valueOf(notice.getStatus()).value())				
				.set(NOTICE.TYPE, NoticeType.valueOf(notice.getType()).value())
				.set(NOTICE.COMPANY, (notice.getCompany() != null) ? notice.getCompany(): null)
				.set(NOTICE.SOURCE, (notice.getSource() != null) ? String.valueOf(notice.getSource()) : null)
				.set(NOTICE.PRIORITY, Priority.valueOf(notice.getPriority()).value())
				.returning( NOTICE.ID )
				.fetchOne()				
				.getId()
				;
	}
	
	public static Integer insertAlarm(AONContext ctx, Alarm alarm) {
		return ctx.getDslContext()
				.insertInto(ALARM)
				.set(ALARM.DOMAIN, ctx.getDomainId())
				.set(ALARM.DESCRIPTION, alarm.getDescription())
				.set(ALARM.ALARM_DATE, new java.sql.Timestamp(alarm.getAlarmDate().getTime()))
				.set(ALARM.STATUS, alarm.getStatus())
				.set(ALARM.SOURCE, alarm.getSource())
				.set(ALARM.SOURCE_ID, alarm.getSourceId())
				.set(ALARM.USER_ID, alarm.getUserId())
				.set(ALARM.PRIORITY, alarm.getPriority())
				.returning( ALARM.ID )
				.fetchOne()				
				.getId()
				;
	}
	

	

	private static class FullAlarmFiller implements Function<Record, Alarm> {

		@Override
		public Alarm apply(Record record) {
			Alarm alarm = new Alarm();
			alarm.setId(record.getValue(ALARM.ID));
			alarm.setDomain(record.getValue(ALARM.DOMAIN));
			alarm.setDescription(record.getValue(ALARM.DESCRIPTION));
			alarm.setAlarmDate(record.getValue(ALARM.ALARM_DATE));
			alarm.setStatus(record.getValue(ALARM.STATUS));
			alarm.setSource(record.getValue(ALARM.SOURCE));
			alarm.setSourceId(record.getValue(ALARM.SOURCE_ID));
			alarm.setUserId(record.getValue(ALARM.USER_ID));
			alarm.setPriority(record.getValue(ALARM.PRIORITY));
			return alarm;
		}
	}

	private static class FullNoticeFiller implements Function<Record, Notice> {

		@Override
		public Notice apply(Record record) {
			Notice notice = new Notice();
			notice.setId(record.getValue(NOTICE.ID));
			notice.setDomain(record.getValue(NOTICE.DOMAIN));
			notice.setStartDate(record.getValue(NOTICE.DATE));
			notice.setCompany(record.getValue(NOTICE.COMPANY));
			notice.setSource(record.getValue(NOTICE.SOURCE));
			notice.setSender(new UserFiller().apply(record));
			notice.setTitle(record.getValue(NOTICE.SUBJECT));
			notice.setRecipientId(record.getValue(NOTICE.RECIPIENT));

			return notice;
		}
	}
	
	private static class UserFiller implements Function<Record, User> {
		@Override
		public User apply(Record record) {
			User user = new User();
			user.setId(record.getValue(USER.ID));
			user.setDomain(record.getValue(USER.DOMAIN));
			user.setName(record.getValue(USER.NAME));
			user.setLogin(record.getValue(USER.LOGIN));
			user.setActive(
					AonEnumUtils.getBoolean(record.getValue(USER.ACTIVE)));
			return user;
		}
	}
	
}
