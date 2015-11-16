package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Notice.NOTICE;
import static com.esferalia.aon.jooq.tables.NoticeTag.NOTICE_TAG;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.Tag.TAG;
import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.jooq.tables.Workgroup.WORKGROUP;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.exception.DataAccessException;

import com.esferalia.aon.jooq.tables.NoticeTag;
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

public class AonHubDAO {

	public static List<Notice> getOpenIssues(AONContext ctx, int parentDomain,
			Integer domain, byte openIndex, byte reopenIndex, byte tagOrdinal,
			byte priorityOrdinal) throws DataAccessException, Exception {

		List<Notice> notices = new LinkedList<Notice>();

		try {
			List<NoticeRecord> recordList = ctx
					.getDslContext()
					.selectFrom(NOTICE)
					.where(NOTICE.DOMAIN.eq(parentDomain).or(
							NOTICE.DOMAIN.eq(domain)))
					.and(NOTICE.STATUS.eq(openIndex).or(
							NOTICE.STATUS.equal(reopenIndex)))
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
				notice.setType(record.getValue(NOTICE.TYPE).intValue());
				
				try {
					String priorityName = ctx.getDslContext()
							.selectFrom(TAG.rightOuterJoin(NOTICE_TAG).on(TAG.ID.eq(NOTICE_TAG.TAG)))
							.where(TAG.TYPE.eq(priorityOrdinal)
									.and(TAG.DOMAIN.eq(record.getValue(NOTICE.DOMAIN)))
									.and(NOTICE_TAG.NOTICE.eq(record.getValue(NOTICE.ID)))
									.and(NOTICE_TAG.START_DATE.eq(record.getValue(NOTICE.DATE))))
									.fetchOne().getValue(TAG.NAME);
					
					notice.setPriority(priorityName);

				} catch ( Exception ex) {
					System.out.println( ex.getMessage() + " " + ex.getLocalizedMessage());
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
			Integer parentDomain, Integer domain) throws DataAccessException,
			Exception {

		return ctx
				.getDslContext()
				.selectFrom(
						USER.rightOuterJoin(REGISTRY).on(
								USER.DOMAIN.eq(REGISTRY.DOMAIN)))
				.where(USER.DOMAIN.eq(parentDomain).or(USER.DOMAIN.eq(domain)))
				.fetch();
	}

	public static List<WorkgroupRecord> getWorkGroups(AONContext ctx,
			Integer parentDomain, Integer domain) throws DataAccessException,
			Exception {

		return ctx
				.getDslContext()
				.selectFrom(WORKGROUP)
				.where(WORKGROUP.DOMAIN.eq(parentDomain).or(
						WORKGROUP.DOMAIN.eq(domain))).fetchInto(WORKGROUP);
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

	public static void insertNewNotice(AONContext ctx, Notice notice)
			throws DataAccessException, Exception {

		// HEAD NOTICE
		InsertSetMoreStep<NoticeRecord> noticeRecord = ctx
				.getDslContext()
				.insertInto(NOTICE)
				.set(NOTICE.DOMAIN, notice.getDomain())
				.set(NOTICE.DATE,
						new java.sql.Timestamp(notice.getDate().getTime()))
				.set(NOTICE.SUBJECT, notice.getTitle())
				.set(NOTICE.PHONE, notice.getPhone())
				.set(NOTICE.COMPANY, notice.getCompany())
				.set(NOTICE.SOURCE, notice.getSource())
				.set(NOTICE.STATUS, notice.getStatus().byteValue())
				.set(NOTICE.SENDER, notice.getSender())
				.set(NOTICE.TYPE, notice.getType().byteValue())
				.set(NOTICE.PRIORITY, (byte) 0);

		if (notice.getRecipient() != null)
			noticeRecord = noticeRecord.set(NOTICE.RECIPIENT,
					notice.getRecipient());
		if (notice.getWorkgroup() != null)
			noticeRecord = noticeRecord.set(NOTICE.WORK_GROUP,
					notice.getWorkgroup());

		NoticeRecord newNoticeRecord = noticeRecord.returning().fetchOne();

		// BODY NOTICE
		ctx.getDslContext()
				.insertInto(NOTICE)
				.set(NOTICE.DOMAIN, newNoticeRecord.getValue(NOTICE.DOMAIN))
				.set(NOTICE.SENDER, newNoticeRecord.getValue(NOTICE.SENDER))
				.set(NOTICE.SUBJECT, newNoticeRecord.getValue(NOTICE.SUBJECT))
				.set(NOTICE.RECIPIENT,
						newNoticeRecord.getValue(NOTICE.RECIPIENT))
				.set(NOTICE.PHONE, newNoticeRecord.getValue(NOTICE.PHONE))
				.set(NOTICE.COMPANY, newNoticeRecord.getValue(NOTICE.COMPANY))
				.set(NOTICE.SOURCE, newNoticeRecord.getValue(NOTICE.SOURCE))
				.set(NOTICE.STATUS, newNoticeRecord.getValue(NOTICE.STATUS))
				.set(NOTICE.TYPE, newNoticeRecord.getValue(NOTICE.TYPE))
				.set(NOTICE.PRIORITY, newNoticeRecord.getValue(NOTICE.PRIORITY))
				.set(NOTICE.WORK_GROUP,
						newNoticeRecord.getValue(NOTICE.WORK_GROUP))
				.set(NOTICE.DATE, newNoticeRecord.getValue(NOTICE.DATE))
				.set(NOTICE.NOTICE_, newNoticeRecord.getValue(NOTICE.ID))
				.execute();

		if (notice.getPriority() != null) {
			Integer priority = ctx
					.getDslContext()
					.selectFrom(TAG)
					.where(TAG.DOMAIN.eq(notice.getDomain())
							.and(TAG.NAME.eq(notice.getPriority()))
							.and(TAG.TYPE.eq(notice.getPriorityOrdinal())))
					.fetchOne().getValue(TAG.ID);

			ctx.getDslContext()
					.insertInto(NOTICE_TAG)
					.set(NOTICE_TAG.NOTICE, newNoticeRecord.getValue(NOTICE.ID))
					.set(NOTICE_TAG.TAG, priority)
					.set(NOTICE_TAG.START_DATE,
							newNoticeRecord.getValue(NOTICE.DATE)).execute();
		}

		for (String label : notice.getTags()) {

			Integer labelId = ctx
					.getDslContext()
					.selectFrom(TAG)
					.where(TAG.DOMAIN.eq(notice.getDomain())
							.and(TAG.NAME.eq(label))
							.and(TAG.TYPE.eq(notice.getTagOrdinal())))
					.fetchOne().getValue(TAG.ID);

			ctx.getDslContext()
					.insertInto(NOTICE_TAG)
					.set(NOTICE_TAG.NOTICE, newNoticeRecord.getValue(NOTICE.ID))
					.set(NOTICE_TAG.TAG, labelId)
					.set(NOTICE_TAG.START_DATE,
							newNoticeRecord.getValue(NOTICE.DATE)).execute();
		}

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
			Integer parentDomain, Integer domain) throws DataAccessException,
			Exception {
		return ctx
				.getDslContext()
				.selectFrom(REGISTRY)
				.where(REGISTRY.DOMAIN.eq(domain).or(
						REGISTRY.DOMAIN.eq(parentDomain))).fetchInto(REGISTRY);
	}

	public static List<Identification> getLoginsToIdentification(
			AONContext ctx, Integer domain) throws DataAccessException,
			Exception {

		Result<Record> result = ctx
				.getDslContext()
				.select()
				.from(REGISTRY.rightOuterJoin(RMEDIA).on(
						REGISTRY.DOMAIN.eq(RMEDIA.DOMAIN)))
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
