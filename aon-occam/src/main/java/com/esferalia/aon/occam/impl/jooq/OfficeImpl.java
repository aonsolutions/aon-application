package com.esferalia.aon.occam.impl.jooq;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IOffice;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.office.User;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.impl.jooq.dao.AonHubDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;

public class OfficeImpl implements IOffice {

	@Override
	public User getUser(AONContext ctx, Integer id) {		
		return AonHubDAO.getUser(ctx, id);
	}
	@Override
	public List<Notice> getOpenNotices(AONContext ctx)
			throws IllegalArgumentException {
		return AonHubDAO.getOpenNotices(ctx);
	}

	@Override
	public List<Notice> getClosedNotices(AONContext ctx)
			throws IllegalArgumentException {		
		return AonHubDAO.getClosedIsues(ctx);
	}

	@Override
	public List<Notice> getAllIssues(AONContext ctx)
			throws IllegalArgumentException {
		return AonHubDAO.getAllNotices(ctx);
	}
	
	@Override
	public Notice createComment(AONContext ctx, Integer headId, Notice comment)
			throws IllegalArgumentException {		
		return AonHubDAO.createComment(ctx, headId, comment);
	}
	
	@Override
	public Notice editComment(AONContext ctx, Integer commentId, String body) {	
		return AonHubDAO.editComment(ctx, commentId, body);
	}

	@Override
	public Tag addNewTag(AONContext ctx, Tag tag)
			throws IllegalArgumentException {
		return AonHubDAO.addNewTag(ctx, tag);
	}
	
	@Override
	public Tag editTag(AONContext ctx, String labelName, Tag tag) {		
		return AonHubDAO.editTag(ctx, labelName, tag);
	}

	@Override
	public List<Tag> getTags(AONContext ctx) throws IllegalArgumentException {
		return AonHubDAO.getTags(ctx);
	}

	@Override
	public Notice addNewNotice(AONContext ctx, Notice notice) 
			throws Exception {		
		return AonHubDAO.addNewNotice(ctx, notice);
	}
	
	@Override
	public Notice editNotice(AONContext ctx, Notice notice) {		
		return AonHubDAO.editNotice(ctx, notice);
	}
	
	@Override
	public Notice changeNoticeStatus(AONContext ctx, Notice notice) {	
		return AonHubDAO.changeNoticeState(ctx, notice);
	}

	@Override
	public boolean deleteNotice(AONContext ctx, Integer id) {
		return AonHubDAO.deleteNotice(ctx, id);
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

}
