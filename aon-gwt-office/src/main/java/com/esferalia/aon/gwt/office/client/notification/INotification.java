package com.esferalia.aon.gwt.office.client.notification;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.office.NotificationInfo;
import com.esferalia.aon.occam.api.model.office.NotificationType;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwt_office_notification")
public interface INotification extends RemoteService{
	
	void sendNotification(Domain domain, Notice notice,NotificationType type, String to);

	LinkedList<MailAccount> getMailAccountList(Domain domain);
	
	NotificationInfo getNotificationInfo(Domain domain);
	
	void insertNotificationInfo(Domain domain, NotificationInfo notificationInfo);

}
