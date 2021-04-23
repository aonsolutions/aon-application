package com.esferalia.aon.occam.impl.jooq;

import java.util.stream.Stream;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.INotification;
import com.esferalia.aon.occam.api.model.Filter.NotificationFilter;
import com.esferalia.aon.occam.api.model.aonsolutions.Notification;
import com.esferalia.aon.occam.impl.jooq.dao.NotificationDAO;

public class NotificationImpl implements INotification {
	
	@Override
	public Notification getNotification(AONContext ctx,  NotificationFilter filter) {
		return NotificationDAO.getNotification(ctx, filter);
	}

	@Override
	public Stream<Notification> getNotificationStream(AONContext ctx,  NotificationFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> NotificationDAO.getNotificationStream(ctx, filter));
	}
	
	@Override
	public Stream<Notification> getNotificationStream(AONContext ctx,  NotificationFilter filter, Integer page, Integer peerPage) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> NotificationDAO.getNotificationStream(ctx, filter, page, peerPage));
	}
	
	@Override
	public Notification saveNotification(AONContext ctx, Notification nt) {
		return ctx.getDslContext().transactionResult(
				configuration -> NotificationDAO.saveNotification(ctx, nt));
	}
	
	@Override
	public void deleteNotification(AONContext ctx, Notification nt) {
		ctx.getDslContext().transaction(
				configuration ->  NotificationDAO.deleteNotification(ctx, nt)
		);
		
	}
	
	@Override
	public void markReadNotification(AONContext ctx, Integer id) {
		ctx.getDslContext().transaction(configuration ->  NotificationDAO.markReadNotification(ctx, id));
	}
	
	@Override
	public Integer getTotalNotification(AONContext ctx, NotificationFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> NotificationDAO.getTotalNotification(ctx, filter));
	}
	
	
	
}
