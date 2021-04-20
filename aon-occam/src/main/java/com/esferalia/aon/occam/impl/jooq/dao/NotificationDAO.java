package com.esferalia.aon.occam.impl.jooq.dao;


import org.jooq.Record;
import static com.esferalia.aon.jooq.tables.Notification.NOTIFICATION;
import static com.esferalia.aon.jooq.tables.NotificationReceiver.NOTIFICATION_RECEIVER;
import java.sql.Timestamp;
import java.util.function.Function;
import java.util.stream.Stream;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.NotificationFilter;
import com.esferalia.aon.occam.api.model.aonsolutions.Notification;
import com.esferalia.aon.occam.api.model.aonsolutions.NotificationReceiver;
import com.esferalia.aon.occam.api.model.aonsolutions.NotificationSource;
import com.esferalia.aon.occam.api.model.aonsolutions.NotificationStatus;
import com.esferalia.aon.occam.api.model.type.Priority;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.NotificationPropertiesDAO;


public class NotificationDAO {

	private static final NotificationPropertiesDAO NOTIFICATION_PROPERTIES = new NotificationPropertiesDAO();
	
	public static Stream<Notification> getNotificationStream(AONContext ctx, NotificationFilter filter) {
		ctx.checkRead();
		return ctx.getDslContext()
			.select()
			.distinctOn(NOTIFICATION_RECEIVER.NOTIFICATION)
			.from(NOTIFICATION)
			.join(NOTIFICATION_RECEIVER).on(NOTIFICATION_RECEIVER.NOTIFICATION.eq(NOTIFICATION.ID))
			.where(NOTIFICATION_PROPERTIES.getConditions(filter))
			.orderBy(NOTIFICATION.ID.desc())
			.fetch().stream().map(new NotificationFiller());
	}
	
	public static Notification saveNotification(AONContext ctx, Notification nt) {
		Notification notification  = nt.getId() !=0 ? update(ctx, nt) : insert(ctx, nt);
		return notification;
	}
	
	private static Notification insert(AONContext ctx, Notification nt) {
		ctx.checkWrite();
		Integer id = ctx.getDslContext()
			.insertInto(NOTIFICATION, NOTIFICATION.DOMAIN, NOTIFICATION.DATE, 
					NOTIFICATION.TITLE, NOTIFICATION.BODY, NOTIFICATION.SOURCE, 
					NOTIFICATION.SOURCE_ID, NOTIFICATION.SENDER, NOTIFICATION.PRIORITY)
			.values(nt.getDomain().getId(), new Timestamp(nt.getDate().getTime()), 
					nt.getTitle(), nt.getBody(), nt.getSource().value(), 
					nt.getSourceId(), nt.getSender(), nt.getPriority().value())
			.returning(NOTIFICATION.ID).fetchOne().getValue(NOTIFICATION.ID);
		return nt.setId(id);
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
//		.set(NOTIFICATION.SENDER, nt.getSender())
//		.set(NOTIFICATION.DOMAIN, nt.getDomain().getId())
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
			NotificationReceiver receiver = new NotificationReceiver()
					.setStatus(NotificationStatus.safeValueOf(r.getValue(NOTIFICATION_RECEIVER.STATUS)))
				    .setAuth(r.getValue(NOTIFICATION_RECEIVER.AUTH));
			
			return new Notification()
					.setId(r.getValue(NOTIFICATION.ID))
					.setDomain(new Domain().setId(r.getValue(NOTIFICATION.DOMAIN)))
					.setDate(r.getValue(NOTIFICATION.DATE))
					.setTitle(r.getValue(NOTIFICATION.TITLE))
					.setBody(r.getValue(NOTIFICATION.BODY))
					.setSource(NotificationSource.safeValueOf(r.getValue(NOTIFICATION.SOURCE)))
					.setSourceId(r.getValue(NOTIFICATION.SOURCE_ID))
					.setSender(r.getValue(NOTIFICATION.SENDER))
					.setPriority(Priority.safeValueOf(r.getValue(NOTIFICATION.PRIORITY)))
					.setReceiver(receiver)
					;
		}
	}
	
}
