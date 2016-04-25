package com.esferalia.aon.occam.api;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.Filter.RegistryMediaFilter;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.office.NoticeFilter;
import com.esferalia.aon.occam.api.model.office.NotificationInfo;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.security.User;

public interface IOffice {

	public User getUser(AONContext ctx, Integer id);
	
	public List<User> getUsers(AONContext ctx);

	public Notice addNewNotice(AONContext ctx, Notice notice) throws Exception;
	
	public Notice getIssueById(AONContext ctx, int number);

	public Notice editNotice(AONContext ctx, Notice notice);
	
	public Notice createNewDuplicated(AONContext ctx, int id);
	
	public Notice addDuplicateNotice(AONContext ctx, Notice childNotice, int noticeParentId);

	public Notice changeNoticeStatus(AONContext ctx, Notice notice);
	
	public int getSelectedCount(AONContext ctx, NoticeFilter filter);
		
	public List<Notice> getNotices(AONContext ctx, NoticeFilter filter);

	public Notice createComment(AONContext ctx, Integer headId, Notice comment)
			throws IllegalArgumentException;

	public Notice editComment(AONContext ctx, Integer commentId, String body);

	public boolean deleteTag(AONContext ctx, String labelName);

	public boolean removeLabelFromIssue(AONContext ctx, Integer issueId,
			Tag tag);

	public Notice replaceLabelsForIssue(AONContext ctx, Integer noticeId,
			List<Tag> add, List<Tag> deleted);

	public Tag addNewTag(AONContext ctx, Tag tag)
			throws IllegalArgumentException;

	public Tag editTag(AONContext ctx, String labelName, Tag tag);

	public Notice addLabelsToAnIssue(AONContext ctx, Integer noticeId,
			List<Tag> tagList);

	public Tag getTag(AONContext ctx, String name)
			throws IllegalArgumentException;

	public List<Tag> getTags(AONContext ctx) throws IllegalArgumentException;

	public List<Registry> getRegistries(AONContext ctx)
			throws IllegalArgumentException;

	public List<RegistryMedia> getRMedias(AONContext ctx)
			throws IllegalArgumentException;
	
	public LinkedList<RegistryMedia> getRMediaList(AONContext ctx, RegistryMediaFilter filter);
	
	public NotificationInfo getNotificationInfo(AONContext ctx);
	public void insertNotificationInfo(AONContext ctx, NotificationInfo notificationInfo);

}
