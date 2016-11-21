package com.esferalia.aon.occam.impl.jooq;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IOffice;
import com.esferalia.aon.occam.api.model.Filter.RegistryMediaFilter;
import com.esferalia.aon.occam.api.model.office.NotificationInfo;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.impl.jooq.dao.AonHubDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.impl.jooq.dao.UserDAO;

public class OfficeImpl implements IOffice {
	
	@Override
	public void getSolutionIssuesDescription(AONContext ctx, Integer domainId) {
		ctx.getDslContext().transaction(configuration -> 
			AonHubDAO.solutionIssuesDescription(ctx, domainId));	
	}
	
	// ----------------------------------------------------- SELECTS
	@Override
	public User getUser(AONContext ctx, Integer id) {
		return ctx.getDslContext().transactionResult(
				conf -> UserDAO.getUser(ctx, id));		
	}
	
	@Override
	public List<User> getUsers(AONContext ctx) {		
		Integer parentID = DomainDAO.getParentDomain(ctx);
		return ctx.getDslContext().transactionResult(
				conf -> AonHubDAO.fillUsersFromNotices(ctx, parentID));
	}
	
	// ----------------------------------------------------- INSERTS

	@Override
	public Tag addNewTag(AONContext ctx, Tag tag)
			throws IllegalArgumentException {
		return ctx.getDslContext().transactionResult(
				conf -> AonHubDAO.insertTag(ctx, tag));
	}

	// ----------------------------------------------------- DELETES


	@Override
	public Tag editTag(AONContext ctx, String labelName, Tag tag) {
		return AonHubDAO.editTag(ctx, labelName, tag);
	}

	@Override
	public List<Tag> getTags(AONContext ctx) throws IllegalArgumentException {
		return ctx.getDslContext().transactionResult(
				conf -> AonHubDAO.getTags(ctx));
	}

	@Override
	public boolean deleteTag(AONContext ctx, String labelName) {
		return AonHubDAO.deleteTag(ctx, labelName);
	}

	@Override
	public Tag getTag(AONContext ctx, String name)
			throws IllegalArgumentException {
		return AonHubDAO.getTag(ctx, name);
	}

	@Override
	public List<Registry> getRegistries(AONContext ctx)
			throws IllegalArgumentException {
		Integer parentID = DomainDAO.getParentDomain(ctx);
		return AonHubDAO.getRegistries(ctx, parentID);
	}

	@Override
	public LinkedList<RegistryMedia> getRMediaList(AONContext ctx, RegistryMediaFilter filter)
			throws IllegalArgumentException {
		return AonHubDAO.getRMediaList(ctx, filter);
	}

	
	//-------------------- NOTIFICATIONS
	
	@Override
	public NotificationInfo getNotificationInfo(AONContext ctx) {
		return ctx.getDslContext().transactionResult(
				configuration -> AonHubDAO.getNotificationInfo(ctx));
	}
	
	@Override
	public void insertNotificationInfo(AONContext ctx, NotificationInfo notificationInfo) {
		ctx.getDslContext().transaction(configuration -> 
				AonHubDAO.insertNotificationInfo(ctx, notificationInfo));	
	}
	
	@Override
	public void insertNotificationInfo(AONContext ctx, String data, AppParam appParam) {
		ctx.getDslContext().transaction(configuration -> 
				AonHubDAO.insertNotificationInfo(ctx, data, appParam));	
	}

}
