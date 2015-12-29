package com.esferalia.aon.occam.impl.jooq;

import java.util.List;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IOffice;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.impl.jooq.dao.AonHubDAO;

public class OfficeImpl implements IOffice {

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
	public Tag addNewTag(AONContext ctx, Tag tag)
			throws IllegalArgumentException {
		return AonHubDAO.addNewTag(ctx, tag);
	}

	@Override
	public List<Tag> getTags(AONContext ctx) throws IllegalArgumentException {
		return AonHubDAO.getTags(ctx);
	}

	@Override
	public Notice addNewNotice(AONContext ctx, Notice notice)
			throws IllegalArgumentException {		
		return AonHubDAO.addNewNotice(ctx, notice);
	}

	@Override
	public void deleteNotice(AONContext ctx, Notice notice) {
		AonHubDAO.deleteNotice(ctx, notice);
	}
	
	@Override
	public void deleteTag(AONContext ctx, Tag tag) {
		AonHubDAO.deleteTag(ctx, tag);
	}

}
