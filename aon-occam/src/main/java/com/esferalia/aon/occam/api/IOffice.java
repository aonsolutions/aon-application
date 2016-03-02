package com.esferalia.aon.occam.api;

import java.util.List;

import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.security.User;

public interface IOffice {

	public User getUser(AONContext ctx, Integer id);

	public Notice addNewNotice(AONContext ctx, Notice notice) throws Exception;

	public Notice editNotice(AONContext ctx, Notice notice);

	public Notice changeNoticeStatus(AONContext ctx, Notice notice);

	public List<Notice> getOpenNotices(AONContext ctx, String since)
			throws IllegalArgumentException;

	public List<Notice> getClosedNotices(AONContext ctx, String since)
			throws IllegalArgumentException;

	public List<Notice> getAllIssues(AONContext ctx, String since)
			throws IllegalArgumentException;

	public Notice createComment(AONContext ctx, Integer headId, Notice comment)
			throws IllegalArgumentException;

	public Notice editComment(AONContext ctx, Integer commentId, String body);

	public boolean deleteNotice(AONContext ctx, Integer id);

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

}
