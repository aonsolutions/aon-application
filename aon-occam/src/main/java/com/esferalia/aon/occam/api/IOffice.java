package com.esferalia.aon.occam.api;

import java.util.List;

import com.esferalia.aon.occam.api.model.office.NotificationInfo;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AppParam;

public interface IOffice {

	@Deprecated public List<User> getUsers(AONContext ctx);
	
	// NOTIFICATION INFO

	@Deprecated public NotificationInfo getNotificationInfo(AONContext ctx);
	@Deprecated public void insertNotificationInfo(AONContext ctx, NotificationInfo notificationInfo);
	@Deprecated public void insertNotificationInfo(AONContext ctx, String data, AppParam appParam);

}
