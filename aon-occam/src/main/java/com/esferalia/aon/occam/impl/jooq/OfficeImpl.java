package com.esferalia.aon.occam.impl.jooq;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IOffice;
import com.esferalia.aon.occam.api.model.Filter.RegistryMediaFilter;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.office.NoticeFilter;
import com.esferalia.aon.occam.api.model.office.NotificationInfo;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.impl.jooq.dao.AonHubDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AonHubDAO2;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.impl.jooq.dao.UserDAO;

public class OfficeImpl implements IOffice {
	
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
				conf -> AonHubDAO2.fillUsersFromNotices(ctx, parentID));
	}
	
	@Override
	public int getSelectedCount(AONContext ctx, NoticeFilter filter) {
		return ctx.getDslContext().transactionResult(
				conf -> AonHubDAO2.getSelectedCount(ctx, filter));
	}
	
	@Override
	public List<Notice> getNotices(AONContext ctx, NoticeFilter filter) {
		return ctx.getDslContext().transactionResult(
				conf -> AonHubDAO2.getTicketNotices(ctx, filter));		
	}
	
	@Override
	public Notice getIssueById(AONContext ctx, int number) {
		return ctx.getDslContext().transactionResult(
				conf -> AonHubDAO2.getTicketNotice(ctx, number));
	}
	
	// ----------------------------------------------------- INSERTS

	@Override
	public Notice addNewNotice(AONContext ctx, Notice notice) throws Exception {
		return ctx.getDslContext().transactionResult( 
				conf -> AonHubDAO2.insertNotice(ctx, notice));
	}

	@Override
	public Notice createComment(AONContext ctx, Integer headId, Notice comment)
			throws IllegalArgumentException {
		return ctx.getDslContext().transactionResult( 
				conf -> AonHubDAO2.createComment(ctx, headId, comment));
	}

	@Override
	public Tag addNewTag(AONContext ctx, Tag tag)
			throws IllegalArgumentException {
		return ctx.getDslContext().transactionResult(
				conf -> AonHubDAO2.insertTag(ctx, tag));
	}
	
	@Override
	public Notice createNewDuplicated(AONContext ctx, int id) {
		return ctx.getDslContext().transactionResult(
				conf -> AonHubDAO2.createNewDuplicated(ctx, id));
	}
	
	@Override
	public Notice addDuplicateNotice(AONContext ctx, Notice childNotice,
			int noticeParentId) {
		return ctx.getDslContext().transactionResult(
				conf -> AonHubDAO2.addDuplicateNotice(ctx, childNotice, noticeParentId));
	}
	
	@Override
	public Notice assigneeTo(AONContext ctx, int noticeId, int userId) {		
		return ctx.getDslContext().transactionResult(
				conf -> AonHubDAO2.assigneeTo(ctx, noticeId, userId));
	}

	// ----------------------------------------------------- DELETES


	@Override
	public Notice editComment(AONContext ctx, Integer commentId, String body) {
		return ctx.getDslContext().transactionResult(
				conf -> AonHubDAO2.editComment(ctx, commentId, body));
	}

	@Override
	public Tag editTag(AONContext ctx, String labelName, Tag tag) {
		return AonHubDAO.editTag(ctx, labelName, tag);
	}

	@Override
	public Notice addLabelsToAnIssue(AONContext ctx, Integer noticeId,
			List<Tag> tagList) {
		return AonHubDAO.addLabelsToAnIssue(ctx, noticeId, tagList);
	}

	@Override
	public List<Tag> getTags(AONContext ctx) throws IllegalArgumentException {
		return ctx.getDslContext().transactionResult(
				conf -> AonHubDAO2.getTags(ctx));
	}

	@Override
	public Notice editNotice(AONContext ctx, Notice notice) {
		return ctx.getDslContext().transactionResult(
				conf -> AonHubDAO2.editNotice(ctx, notice));		
	}

	@Override
	public Notice changeNoticeStatus(AONContext ctx, Notice notice) {
		return ctx.getDslContext().transactionResult(
				conf -> AonHubDAO2.changeNoticeState(ctx, notice));
	}

	@Override
	public boolean deleteTag(AONContext ctx, String labelName) {
		return AonHubDAO.deleteTag(ctx, labelName);
	}

	@Override
	public boolean removeLabelFromIssue(AONContext ctx, Integer issueId,
			Tag tag) {
		return ctx.getDslContext().transactionResult(
				conf -> AonHubDAO2.removeLabelFromIssue(ctx, issueId, tag));
	}

	@Override
	public Notice replaceLabelsForIssue(AONContext ctx, Integer noticeId,
			List<Tag> add, List<Tag> deleted) {
		return ctx.getDslContext().transactionResult(
				conf -> AonHubDAO2.replaceLabelsForIssue(ctx, noticeId, add, deleted));
//		return AonHubDAO.replaceLabelsForIssue(ctx, noticeId, add, deleted);
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
	public List<RegistryMedia> getRMedias(AONContext ctx)
			throws IllegalArgumentException {
		Integer parentID = DomainDAO.getParentDomain(ctx);
		return AonHubDAO.getRMedias(ctx, parentID);
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
