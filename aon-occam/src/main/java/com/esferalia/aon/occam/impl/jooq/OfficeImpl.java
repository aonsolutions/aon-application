package com.esferalia.aon.occam.impl.jooq;

import java.util.List;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IOffice;
import com.esferalia.aon.occam.api.model.office.NotificationInfo;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.impl.jooq.dao.AonHubDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;

@Deprecated
public class OfficeImpl implements IOffice {
		
	@Override
	@Deprecated
	public List<User> getUsers(AONContext ctx) {		
		Integer parentID = DomainDAO.getParentDomain(ctx);
		return ctx.getDslContext().transactionResult(
				conf -> AonHubDAO.fillUsersFromNotices(ctx, parentID));
	}
	
	//-------------------- NOTIFICATION INFO
	
	@Override
	@Deprecated
	public NotificationInfo getNotificationInfo(AONContext ctx) {
		return ctx.getDslContext().transactionResult(
				configuration -> AonHubDAO.getNotificationInfo(ctx));
	}
	
	@Override
	@Deprecated
	public void insertNotificationInfo(AONContext ctx, NotificationInfo notificationInfo) {
		ctx.getDslContext().transaction(configuration -> 
				AonHubDAO.insertNotificationInfo(ctx, notificationInfo));	
	}
	
	@Override
	@Deprecated
	public void insertNotificationInfo(AONContext ctx, String data, AppParam appParam) {
		ctx.getDslContext().transaction(configuration -> 
				AonHubDAO.insertNotificationInfo(ctx, data, appParam));	
	}

}
