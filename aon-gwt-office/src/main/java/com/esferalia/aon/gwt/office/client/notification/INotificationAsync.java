package com.esferalia.aon.gwt.office.client.notification;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.office.NotificationInfo;
import com.esferalia.aon.occam.api.model.office.NotificationType;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface INotificationAsync {
	
	void sendNotification(Domain domain, Notice notice,NotificationType type, String to, AsyncCallback<Void> callback);

	void getMailAccountList(Domain domain, AsyncCallback<LinkedList<MailAccount>> callback);

	void getNotificationInfo(Domain domain, AsyncCallback<NotificationInfo> callback);

	void insertNotificationInfo(Domain domain, NotificationInfo notificationInfo, AsyncCallback<Void> callback);

}
