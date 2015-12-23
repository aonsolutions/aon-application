package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Notice.NOTICE;
import static com.esferalia.aon.jooq.tables.NoticeTag.NOTICE_TAG;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.Tag.TAG;
import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.jooq.tables.Workgroup.WORKGROUP;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.jooq.Record;
import org.jooq.Result;
import org.jooq.exception.DataAccessException;

import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.NoticeRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.TagRecord;
import com.esferalia.aon.jooq.tables.records.UserRecord;
import com.esferalia.aon.jooq.tables.records.WorkgroupRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.office.Identification;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.office.NoticeComment;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.office.User;
import com.esferalia.aon.occam.api.model.type.NoticeStatus;
import com.esferalia.aon.occam.api.model.type.NoticeType;
import com.esferalia.aon.occam.api.model.type.Priority;
import com.esferalia.aon.occam.api.model.type.TagType;

public class AonHubDAO {
	
	public static void deleteNotice (AONContext ctx, Notice notice) {
		
		
	}

	public static Notice addNewNotice(AONContext ctx, Notice notice) {	

		Date today = new Date();

		Integer userId = ctx.getDslContext().selectFrom(USER)
				.where(USER.LOGIN.eq(ctx.getUser())
						.and(USER.DOMAIN.eq(ctx.getDomainId())))
				.fetchOne().getValue(USER.ID);

		NoticeRecord noticeRecord = ctx.getDslContext().insertInto(NOTICE)
				.set(NOTICE.DOMAIN, ctx.getDomainId())
				.set(NOTICE.SENDER, userId)
				.set(NOTICE.DATE,
						new java.sql.Timestamp((new Date()).getTime()))
				.set(NOTICE.SUBJECT, notice.getTitle())
				.set(NOTICE.PHONE,
						(notice.getPhone() != null) ? notice.getPhone() : null)
				.set(NOTICE.STATUS, NoticeStatus.OPEN.value())
				.set(NOTICE.TYPE,
						(byte) NoticeType.valueOf(notice.getType()).ordinal())
				.set(NOTICE.PRIORITY,
						(byte) Priority.valueOf(notice.getPriority()).ordinal())
				.set(NOTICE.COMPANY, 
						(notice.getCompany() != null) ? notice.getCompany() : null)
				.set(NOTICE.RECIPIENT, 
						(notice.getRecipient() != null) ? notice.getRecipient() : null)
				.set(NOTICE.WORK_GROUP, 
						(notice.getWorkgroup() != null) ? notice.getWorkgroup() : null)
				.returning().fetchOne();
		
		ctx.getDslContext()
		.insertInto(NOTICE)
		.set(NOTICE.DOMAIN, noticeRecord.getValue(NOTICE.DOMAIN))
		.set(NOTICE.SENDER, noticeRecord.getValue(NOTICE.SENDER))
		.set(NOTICE.DATE, noticeRecord.getValue(NOTICE.DATE))
		.set(NOTICE.SUBJECT, notice.getTitle())
		.set(NOTICE.PHONE, noticeRecord.getValue(NOTICE.PHONE))
		.set(NOTICE.STATUS, noticeRecord.getValue(NOTICE.STATUS))
		.set(NOTICE.TYPE, noticeRecord.getValue(NOTICE.TYPE))
		.set(NOTICE.PRIORITY, noticeRecord.getValue(NOTICE.PRIORITY))
		.set(NOTICE.WORK_GROUP, noticeRecord.getValue(NOTICE.WORK_GROUP))
		.set(NOTICE.NOTICE_, noticeRecord.getValue(NOTICE.ID))
		.execute();
		
		for (String tagName : notice.getTags()) {
			
			TagRecord tagRecord = ctx.getDslContext()
					.selectFrom(TAG)
					.where(TAG.DOMAIN.eq(ctx.getDomainId())
							.and(TAG.NAME.eq(tagName)))
					.fetchOne();
			
			ctx.getDslContext().insertInto(NOTICE_TAG)
			.set(NOTICE_TAG.NOTICE, noticeRecord.getValue(NOTICE.ID))
			.set(NOTICE_TAG.TAG, tagRecord.getValue(TAG.ID))
			.set(NOTICE_TAG.START_DATE, noticeRecord.getValue(NOTICE.DATE))
			.execute();
		}
		
		Notice object = new Notice();
		object.setId(noticeRecord.getValue(NOTICE.ID));
		object.setDomain(ctx.getDomainId());
		object.setDate(today);
		object.setSender(noticeRecord.getValue(NOTICE.SENDER));
		object.setTitle(notice.getTitle());
		object.setBody(notice.getBody());
		object.setRecipient(noticeRecord.getValue(NOTICE.RECIPIENT));
		object.setContact(noticeRecord.getValue(NOTICE.PHONE));
		object.setSource(noticeRecord.getValue(NOTICE.SOURCE));
		object.setCompany(noticeRecord.getValue(NOTICE.COMPANY));
		object.setStatus(noticeRecord.getValue(NOTICE.STATUS).intValue());
		object.setWorkgroup(noticeRecord.getValue(NOTICE.WORK_GROUP));
		object.setType(NoticeType.values()[noticeRecord.getValue(NOTICE.TYPE)].getValue());
		object.setPriority(Priority.values()[noticeRecord.getValue(NOTICE.PRIORITY)].getValue());
		
		return object;

	}

	public static Tag addNewTag(AONContext ctx, Tag tag) {

		Tag newTag = new Tag();

		TagRecord record = ctx.getDslContext().insertInto(TAG)
				.set(TAG.DOMAIN, ctx.getDomainId()).set(TAG.NAME, tag.getName())
				.set(TAG.COLOR,
						(tag.getColor() != null) ? tag.getColor() : null)
				.returning().fetchOne();

		newTag.setId(record.getValue(TAG.ID));
		newTag.setName(record.getValue(TAG.NAME));
		newTag.setDomain(record.getValue(TAG.DOMAIN));
		newTag.setColor(record.getValue(TAG.COLOR));

		return newTag;
	}

	public static List<Notice> getOpenNotices(AONContext ctx) {

		int domainId = ctx.getDomainId();

		List<Notice> notices = new LinkedList<Notice>();
		ctx.getDslContext().selectFrom(NOTICE)
				.where(NOTICE.DOMAIN.eq(domainId)
						.and(NOTICE.STATUS.eq(NoticeStatus.OPEN.value())
								.or(NOTICE.STATUS
										.eq(NoticeStatus.REOPEN.value())))
				.and(NOTICE.NOTICE_.isNull())).fetch().stream()
				.forEach(record -> {
					Notice notice = new Notice();
					int id = record.getValue(NOTICE.ID);
					notice.setId(id);
					notice.setDomain(record.getValue(NOTICE.DOMAIN));
					notice.setDate(record.getValue(NOTICE.DATE));
					notice.setSender(record.getValue(NOTICE.SENDER));
					notice.setTitle(record.getValue(NOTICE.SUBJECT));
					notice.setRecipient(record.getValue(NOTICE.RECIPIENT));
					notice.setContact(record.getValue(NOTICE.PHONE));
					notice.setSource(record.getValue(NOTICE.SOURCE));
					notice.setCompany(record.getValue(NOTICE.COMPANY));
					notice.setStatus(record.getValue(NOTICE.STATUS).intValue());
					notice.setWorkgroup(record.getValue(NOTICE.WORK_GROUP));
					
					byte ordinalType = record.getValue(NOTICE.TYPE);
					notice.setType(NoticeType.values()[ordinalType].getValue());

					try {

						String priorityName = ctx.getDslContext()
								.selectFrom(TAG.rightOuterJoin(NOTICE_TAG)
										.on(TAG.ID.eq(NOTICE_TAG.TAG)))
								.where(TAG.TYPE.eq(TagType.PRIORITY.value())
										.and(TAG.DOMAIN.eq(domainId))
										.and(NOTICE_TAG.NOTICE.eq(id))
										.and(NOTICE_TAG.START_DATE.eq(
												record.getValue(NOTICE.DATE))))
								.fetchOne().getValue(TAG.NAME);

						notice.setPriority(priorityName);

					} catch (Exception ex) {
					}

					ctx.getDslContext().selectFrom(NOTICE)
							.where(NOTICE.NOTICE_.eq(id));

					notices.add(notice);
				});
		return notices;

	}

	public static List<Notice> getClosedIsues(AONContext ctx) {

		int domainId = ctx.getDomainId();

		List<Notice> notices = new LinkedList<Notice>();
		ctx.getDslContext().selectFrom(NOTICE)
				.where(NOTICE.DOMAIN.eq(domainId)
						.and(NOTICE.STATUS.eq(NoticeStatus.CLOSED.value()))
						.and(NOTICE.NOTICE_.isNull()))
				.fetch().stream().forEach(record -> {
					Notice notice = new Notice();
					int id = record.getValue(NOTICE.ID);
					notice.setId(id);
					notice.setDomain(record.getValue(NOTICE.DOMAIN));
					notice.setDate(record.getValue(NOTICE.DATE));
					notice.setSender(record.getValue(NOTICE.SENDER));
					notice.setTitle(record.getValue(NOTICE.SUBJECT));
					notice.setRecipient(record.getValue(NOTICE.RECIPIENT));
					notice.setContact(record.getValue(NOTICE.PHONE));
					notice.setSource(record.getValue(NOTICE.SOURCE));
					notice.setCompany(record.getValue(NOTICE.COMPANY));
					notice.setStatus(record.getValue(NOTICE.STATUS).intValue());
					notice.setWorkgroup(record.getValue(NOTICE.WORK_GROUP));
					
					byte ordinalType = record.getValue(NOTICE.TYPE);					
					notice.setType(NoticeType.values()[ordinalType].getValue());

					try {

						String priorityName = ctx.getDslContext()
								.selectFrom(TAG.rightOuterJoin(NOTICE_TAG)
										.on(TAG.ID.eq(NOTICE_TAG.TAG)))
								.where(TAG.TYPE.eq(TagType.PRIORITY.value())
										.and(TAG.DOMAIN.eq(domainId))
										.and(NOTICE_TAG.NOTICE.eq(id))
										.and(NOTICE_TAG.START_DATE.eq(
												record.getValue(NOTICE.DATE))))
								.fetchOne().getValue(TAG.NAME);

						notice.setPriority(priorityName);

					} catch (Exception ex) {
					}

					notices.add(notice);
				});

		return notices;
	}

	public static List<Notice> getAllNotices(AONContext ctx) {

		int domainId = ctx.getDomainId();

		List<Notice> notices = new LinkedList<Notice>();
		ctx.getDslContext().selectFrom(NOTICE)
				.where(NOTICE.DOMAIN.eq(domainId)
						.and(NOTICE.STATUS.eq(NoticeStatus.OPEN.value())
								.or(NOTICE.STATUS
										.eq(NoticeStatus.REOPEN.value()))
						.or(NOTICE.STATUS.eq(NoticeStatus.CLOSED.value()))
						.or(NOTICE.STATUS.eq(NoticeStatus.DUPLICATED.value())))
				.and(NOTICE.NOTICE_.isNull())).fetch().stream()
				.forEach(record -> {
					Notice notice = new Notice();
					int id = record.getValue(NOTICE.ID);
					notice.setId(id);
					notice.setDomain(record.getValue(NOTICE.DOMAIN));
					notice.setDate(record.getValue(NOTICE.DATE));
					notice.setSender(record.getValue(NOTICE.SENDER));
					notice.setTitle(record.getValue(NOTICE.SUBJECT));
					notice.setRecipient(record.getValue(NOTICE.RECIPIENT));
					notice.setContact(record.getValue(NOTICE.PHONE));
					notice.setSource(record.getValue(NOTICE.SOURCE));
					notice.setCompany(record.getValue(NOTICE.COMPANY));
					notice.setStatus(record.getValue(NOTICE.STATUS).intValue());
					notice.setWorkgroup(record.getValue(NOTICE.WORK_GROUP));
					
					byte ordinalType = record.getValue(NOTICE.TYPE);					
					notice.setType(NoticeType.values()[ordinalType].getValue());


					try {

						String priorityName = ctx.getDslContext()
								.selectFrom(TAG.rightOuterJoin(NOTICE_TAG)
										.on(TAG.ID.eq(NOTICE_TAG.TAG)))
								.where(TAG.TYPE.eq(TagType.PRIORITY.value())
										.and(TAG.DOMAIN.eq(domainId))
										.and(NOTICE_TAG.NOTICE.eq(id))
										.and(NOTICE_TAG.START_DATE.eq(
												record.getValue(NOTICE.DATE))))
								.fetchOne().getValue(TAG.NAME);

						notice.setPriority(priorityName);

					} catch (Exception ex) {
					}

					notices.add(notice);
				});

		return notices;
	}
	
	
	
	

	public static List<Notice> getOpenIssues(AONContext ctx, int parentDomain,
			Integer domain, byte openIndex, byte reopenIndex, byte tagOrdinal,
			byte priorityOrdinal) throws DataAccessException, Exception {

		List<Notice> notices = new LinkedList<Notice>();

		try {
			List<NoticeRecord> recordList = ctx.getDslContext()
					.selectFrom(NOTICE)
					.where(NOTICE.DOMAIN.eq(parentDomain)
							.or(NOTICE.DOMAIN.eq(domain)))
					.and(NOTICE.STATUS.eq(openIndex)
							.or(NOTICE.STATUS.equal(reopenIndex)))
					.and(NOTICE.NOTICE_.isNull()).fetchInto(NOTICE);

			for (NoticeRecord record : recordList) {
				Notice notice = new Notice();
				Integer noticeId = record.getValue(NOTICE.ID);
				notice.setId(noticeId);
				notice.setDomain(record.getValue(NOTICE.DOMAIN));
				notice.setDate(record.getValue(NOTICE.DATE));
				notice.setSender(record.getValue(NOTICE.SENDER));
				notice.setTitle(record.getValue(NOTICE.SUBJECT));

				NoticeRecord object = getTitleSubject(ctx, notice, noticeId);

				if (object == null) {
					notice.setBody(record.getValue(NOTICE.SUBJECT));
					notice.setNotice(record.getValue(NOTICE.ID));
				} else {
					notice.setBody(object.getValue(NOTICE.SUBJECT));
					notice.setNotice(object.getValue(NOTICE.ID));
				}

				notice.setRecipient(record.getValue(NOTICE.RECIPIENT));
				notice.setContact(record.getValue(NOTICE.PHONE));
				notice.setSource(record.getValue(NOTICE.SOURCE));
				notice.setCompany(record.getValue(NOTICE.COMPANY));
				notice.setStatus(record.getValue(NOTICE.STATUS).intValue());
				notice.setWorkgroup(record.getValue(NOTICE.WORK_GROUP));
				
				byte ordinalType = record.getValue(NOTICE.TYPE);					
				notice.setType(NoticeType.values()[ordinalType].getValue());


				try {
					String priorityName = ctx.getDslContext()
							.selectFrom(TAG.rightOuterJoin(NOTICE_TAG)
									.on(TAG.ID.eq(NOTICE_TAG.TAG)))
							.where(TAG.TYPE.eq(priorityOrdinal)
									.and(TAG.DOMAIN
											.eq(record.getValue(NOTICE.DOMAIN)))
									.and(NOTICE_TAG.NOTICE
											.eq(record.getValue(NOTICE.ID)))
									.and(NOTICE_TAG.START_DATE
											.eq(record.getValue(NOTICE.DATE))))
							.fetchOne().getValue(TAG.NAME);

					notice.setPriority(priorityName);

				} catch (Exception ex) {
					System.out.println(
							ex.getMessage() + " " + ex.getLocalizedMessage());
				}

				loadComments(ctx, notice.getComments(), noticeId);

				notices.add(notice);
			}

		} catch (Exception ex) {
			System.out
					.println(ex.getMessage() + " " + ex.getLocalizedMessage());
		}
		return notices;
	}

	private static NoticeRecord getTitleSubject(AONContext ctx, Notice notice,
			Integer noticeId) {

		return ctx.getDslContext().selectFrom(NOTICE)
				.where(NOTICE.NOTICE_.eq(noticeId)).fetchOne();
	}

	private static void loadComments(AONContext ctx,
			List<NoticeComment> comments, Integer noticeId) {

		NoticeRecord noticeRecord = ctx.getDslContext().selectFrom(NOTICE)
				.where(NOTICE.NOTICE_.eq(noticeId)).fetchOne();

		if (noticeRecord != null) {

			NoticeComment comment = new NoticeComment();
			Integer id = noticeRecord.getValue(NOTICE.ID);
			comment.setId(id);
			comment.setDomain(noticeRecord.getValue(NOTICE.DOMAIN));
			comment.setDate(noticeRecord.getValue(NOTICE.DATE));
			comment.setBody(noticeRecord.getValue(NOTICE.SUBJECT));

			comments.add(comment);

			loadComments(ctx, comments, id);

		}
	}

	public static User getSender(AONContext ctx, Integer domain, Integer userId)
			throws DataAccessException {
		// Remitente del aviso. Entiendo que es un usuario único.
		UserRecord userRecord = ctx.getDslContext().selectFrom(USER)
				.where(USER.ID.eq(userId).and(USER.DOMAIN.eq(domain)))
				.fetchOne();

		User user = new User();

		if (userRecord != null) {
			user.setId(userRecord.getValue(USER.ID));
			user.setDomain(userRecord.getValue(USER.DOMAIN));
			user.setName(userRecord.getValue(USER.NAME));
		}
		return user;
	}

	public static User getAssignee(AONContext ctx, Integer domain,
			Integer userId) throws DataAccessException {
		// Destinatario del aviso. Entiendo que usuario unico
		return getSender(ctx, domain, userId);
	}

	public static List<Record> getUsersWorkings(AONContext ctx,
			Integer parentDomain, Integer domain)
					throws DataAccessException, Exception {

		return ctx.getDslContext()
				.selectFrom(USER.rightOuterJoin(REGISTRY)
						.on(USER.DOMAIN.eq(REGISTRY.DOMAIN)))
				.where(USER.DOMAIN.eq(parentDomain).or(USER.DOMAIN.eq(domain)))
				.fetch();
	}

	public static List<WorkgroupRecord> getWorkGroups(AONContext ctx,
			Integer parentDomain, Integer domain)
					throws DataAccessException, Exception {

		return ctx
				.getDslContext().selectFrom(WORKGROUP).where(WORKGROUP.DOMAIN
						.eq(parentDomain).or(WORKGROUP.DOMAIN.eq(domain)))
				.fetchInto(WORKGROUP);
	}

	public static List<Tag> getTags(AONContext ctx, Integer parentDomain,
			Integer domain, byte type) {

		List<TagRecord> tagsRecord = ctx.getDslContext().selectFrom(TAG)
				.where(TAG.DOMAIN.eq(parentDomain).or(TAG.DOMAIN.eq(domain)))
				.and(TAG.TYPE.eq(type)).fetchInto(TAG);

		List<Tag> tags = new LinkedList<Tag>();

		if (tagsRecord != null) {

			for (TagRecord record : tagsRecord) {
				Tag tag = new Tag();
				tag.setId(record.getValue(TAG.ID));
				tag.setDomain(record.getValue(TAG.DOMAIN));
				tag.setName(record.getValue(TAG.NAME));
				tag.setColor(record.getValue(TAG.COLOR));

				tags.add(tag);
			}
		}

		return tags;
	}

	public static void insertNewTag(AONContext ctx, Tag tag)
			throws DataAccessException {

		ctx.getDslContext().insertInto(TAG).set(TAG.DOMAIN, tag.getDomain())
				.set(TAG.NAME, tag.getName()).set(TAG.TYPE, tag.getType())
				.set(TAG.COLOR, tag.getColor()).execute();

	}

	public static String getUserName(AONContext ctx, Integer userId) {

		return ctx.getDslContext().selectFrom(USER).where(USER.ID.eq(userId))
				.fetchOne().getValue(USER.NAME);
	}

	public static String getEnterprise(AONContext ctx, Integer domain) {

		return ctx.getDslContext().selectFrom(REGISTRY)
				.where(REGISTRY.DOMAIN.eq(domain)).fetchOne()
				.getValue(REGISTRY.NAME);
	}

	public static List<RegistryRecord> getRegistryNames(AONContext ctx,
			Integer parentDomain, Integer domain)
					throws DataAccessException, Exception {
		return ctx.getDslContext().selectFrom(REGISTRY)
				.where(REGISTRY.DOMAIN.eq(domain)
						.or(REGISTRY.DOMAIN.eq(parentDomain)))
				.fetchInto(REGISTRY);
	}

	public static List<Identification> getLoginsToIdentification(AONContext ctx,
			Integer domain) throws DataAccessException, Exception {

		Result<Record> result = ctx.getDslContext().select()
				.from(REGISTRY.rightOuterJoin(RMEDIA)
						.on(REGISTRY.DOMAIN.eq(RMEDIA.DOMAIN)))
				.where(REGISTRY.DOMAIN.eq(domain)).fetch();

		List<Identification> idents = new LinkedList<Identification>();

		if (result != null) {

			ListIterator<Record> iterator = result.listIterator();
			while (iterator.hasNext()) {
				Record record = iterator.next();
				Identification ident = new Identification();
				ident.setId(record.getValue(REGISTRY.ID));
				ident.setName(record.getValue(REGISTRY.NAME));
				ident.setDocument(record.getValue(REGISTRY.DOCUMENT));
				ident.setAlias(record.getValue(REGISTRY.ALIAS));
				ident.setValue(record.getValue(RMEDIA.VALUE));
				idents.add(ident);
			}
		}
		return idents;
	}

	public static DomainRecord getParentDomain(AONContext ctx, Integer domain) {
		return ctx.getDslContext().selectFrom(DOMAIN)
				.where(DOMAIN.ID.eq(domain)).fetchOne();
	}

}
