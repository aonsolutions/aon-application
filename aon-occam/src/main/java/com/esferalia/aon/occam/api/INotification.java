package com.esferalia.aon.occam.api;

import java.util.stream.Stream;
import com.esferalia.aon.occam.api.model.Filter.NotificationFilter;
import com.esferalia.aon.occam.api.model.aonsolutions.Notification;

public interface INotification {
	
	public Stream<Notification> getNotificationStream(AONContext ctx, NotificationFilter filter);
	public Notification getNotification(AONContext ctx, NotificationFilter filter);
	public Notification saveNotification(AONContext ctx, Notification nt);
	public void deleteNotification(AONContext ctx, Notification nt);
}
