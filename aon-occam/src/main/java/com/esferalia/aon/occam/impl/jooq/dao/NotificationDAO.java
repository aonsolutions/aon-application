package com.esferalia.aon.occam.impl.jooq.dao;

import org.jooq.Record;
import org.jooq.SelectSeekStep2;
import static com.esferalia.aon.jooq.tables.Notification.NOTIFICATION;
import static com.esferalia.aon.jooq.tables.NotificationReceiver.NOTIFICATION_RECEIVER;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.function.Function;
import java.util.stream.Stream;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.NotificationFilter;
import com.esferalia.aon.occam.api.model.aonsolutions.Notification;
import com.esferalia.aon.occam.api.model.aonsolutions.NotificationSource;
import com.esferalia.aon.occam.api.model.aonsolutions.NotificationStatus;
import com.esferalia.aon.occam.api.model.type.Priority;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.NotificationPropertiesDAO;


public class NotificationDAO {

	private static final NotificationPropertiesDAO NOTIFICATION_PROPERTIES = new NotificationPropertiesDAO();
	
	public static SelectSeekStep2<Record, Byte, Integer> select(AONContext ctx, NotificationFilter filter) {
		 return ctx.getDslContext()
			.select()
			.from(NOTIFICATION)
			.join(NOTIFICATION_RECEIVER).on(NOTIFICATION_RECEIVER.NOTIFICATION.eq(NOTIFICATION.ID))
			.where(NOTIFICATION_PROPERTIES.getConditions(filter))
			.groupBy(NOTIFICATION_RECEIVER.NOTIFICATION)
			.orderBy( NOTIFICATION_RECEIVER.STATUS.asc(), NOTIFICATION.ID.desc());
	}
	
	public static Stream<Notification> getNotificationStream(AONContext ctx, NotificationFilter filter) {
		return select(ctx, filter)
 			.fetch().stream().map(new NotificationFiller());
	}
	
	public static Stream<Notification> getNotificationStream(AONContext ctx, NotificationFilter filter, Integer limit) {
		return select(ctx, filter)
			.limit(limit)
			.fetch().stream().map(new NotificationFiller());
	}
	
	public static Stream<Notification> getNotificationStream(AONContext ctx, NotificationFilter filter, Integer page, Integer perPage) {
		return select(ctx, filter)
			.limit(perPage).offset(perPage * (page -1))
			.fetch().stream().map(new NotificationFiller());
	}
	
	public static Notification saveNotification(AONContext ctx, Notification nt) {
		Notification notification  = nt.getId() !=0 ? update(ctx, nt) : insert(ctx, nt);
		return notification;
	}
	
	public static void markReadNotification(AONContext ctx, Integer id){
		ctx.getDslContext().update(NOTIFICATION_RECEIVER)
			.set(NOTIFICATION_RECEIVER.STATUS, NotificationStatus.READ.value())
			.where(NOTIFICATION_RECEIVER.ID.eq(id))
			.execute();
	}
	
	public static Integer getTotalNotification(AONContext ctx, NotificationFilter filter){
		  return ctx.getDslContext()
			.selectCount()
			.from(NOTIFICATION)
			.join(NOTIFICATION_RECEIVER).on(NOTIFICATION_RECEIVER.NOTIFICATION.eq(NOTIFICATION.ID))
			.where(NOTIFICATION_PROPERTIES.getConditions(filter))
			.fetchOne(0, int.class);
	}
	
	
	private static Notification insert(AONContext ctx, Notification nt) {
		ctx.checkWrite();
		Timestamp dt = Timestamp.from(Instant.now());
		Integer id = ctx.getDslContext()
			.insertInto(NOTIFICATION, NOTIFICATION.DOMAIN, NOTIFICATION.DATE, 
					NOTIFICATION.TITLE, NOTIFICATION.BODY, NOTIFICATION.SOURCE, 
					NOTIFICATION.SOURCE_ID, NOTIFICATION.SENDER, NOTIFICATION.PRIORITY)
			.values(nt.getDomain().getId(), 
					dt, 
					nt.getTitle(), 
					nt.getBody(), 
					NotificationSource.value(nt.getSource()), 
					nt.getSourceId(), 
					nt.getSender(), 
					Priority.value(nt.getPriority())
					)
			.returning(NOTIFICATION.ID).fetchOne().getValue(NOTIFICATION.ID);
		nt.setDate(dt);
		nt.setId(id);
		insertNotificationReceiver(ctx, nt);
		return nt;
	}
	
	
	private static Notification insertNotificationReceiver(AONContext ctx, Notification nt) {
		try {	 
			nt.getReceiver().stream().forEach(rc->{
				try {
					Integer id = ctx.getDslContext()
					.insertInto(NOTIFICATION_RECEIVER, NOTIFICATION_RECEIVER.DOMAIN, NOTIFICATION_RECEIVER.NOTIFICATION,
							NOTIFICATION_RECEIVER.AUTH)
					.values(
							nt.getDomain().getId(), 
							nt.getId(), 
							rc.getAuth()
					)
					.returning(NOTIFICATION_RECEIVER.ID).fetchOne().getValue(NOTIFICATION_RECEIVER.ID);
					rc.setId(id);
				} catch (Exception e) {
				}
			});
		} catch (Exception e) {}
		return nt;
	}
	
	private static Notification update(AONContext ctx, Notification nt) {
		ctx.checkWrite();
		ctx.getDslContext()
			.update(NOTIFICATION)
			.set(NOTIFICATION.DATE, new Timestamp(nt.getDate().getTime()))
			.set(NOTIFICATION.TITLE, nt.getTitle())
			.set(NOTIFICATION.BODY, nt.getBody())
			.set(NOTIFICATION.SOURCE, nt.getSource().value())
			.set(NOTIFICATION.SOURCE_ID, nt.getSourceId())
			.set(NOTIFICATION.PRIORITY, nt.getPriority().value())
			.where(NOTIFICATION.ID.eq(nt.getId()))
			.execute();		
		return nt;
	}
		
	public static void deleteNotification(AONContext ctx, Notification nt) {
		ctx.checkWrite();
		ctx.getDslContext().delete(NOTIFICATION).where(NOTIFICATION.ID.eq(nt.getId())).execute();	
	}
	
	public static Notification getNotification(AONContext ctx, NotificationFilter filter) {
		ctx.checkRead();
		return  ctx.getDslContext()
				.select()
				.from(NOTIFICATION)
				.where(NOTIFICATION_PROPERTIES.getConditions(filter))
				.stream()
				.map( new NotificationFiller() )
				.findFirst()
				.orElse(null);
	}

	public static class NotificationFiller implements Function<Record, Notification> {
		@Override
		public Notification apply(Record r) {
			return new Notification()
					.setId(r.getValue(NOTIFICATION_RECEIVER.ID))
					.setDomain(new Domain().setId(r.getValue(NOTIFICATION.DOMAIN)))
					.setDate(r.getValue(NOTIFICATION.DATE))
					.setTitle(r.getValue(NOTIFICATION.TITLE))
					.setBody(r.getValue(NOTIFICATION.BODY))
					.setSource(NotificationSource.safeValueOf(r.getValue(NOTIFICATION.SOURCE)))
					.setSourceId(r.getValue(NOTIFICATION.SOURCE_ID))
					.setSender(r.getValue(NOTIFICATION.SENDER))
					.setPriority(Priority.safeValueOf(r.getValue(NOTIFICATION.PRIORITY)))
					.setStatus(NotificationStatus.safeValueOf(r.getValue(NOTIFICATION_RECEIVER.STATUS)))
					;
		}
	}
	
}
