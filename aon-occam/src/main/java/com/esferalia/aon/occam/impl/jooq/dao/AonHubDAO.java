package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Notice.NOTICE;
import static com.esferalia.aon.jooq.tables.NoticeTag.NOTICE_TAG;
import static com.esferalia.aon.jooq.tables.Tag.TAG;
import static com.esferalia.aon.jooq.tables.User.USER;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jooq.Cursor;
import org.jooq.Record;

import com.esferalia.aon.jooq.tables.records.NoticeRecord;
import com.esferalia.aon.jooq.tables.records.TagRecord;
import com.esferalia.aon.jooq.tables.records.UserRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.office.User;
import com.esferalia.aon.occam.api.model.type.NoticeStatus;
import com.esferalia.aon.occam.api.model.type.NoticeType;
import com.esferalia.aon.occam.api.model.type.Priority;
import com.esferalia.aon.occam.api.model.type.TagType;

public class AonHubDAO {

	public static Notice addNewNotice(AONContext ctx, Notice notice) {

		Date today = new Date();

		User user = new User();
		UserRecord userRecord = ctx.getDslContext().selectFrom(USER)
				.where(USER.DOMAIN.eq(ctx.getDomainId())
						.and(USER.LOGIN.eq(ctx.getUser())))
				.fetchOne();
		user.setId(userRecord.getValue(USER.ID));
		user.setName(ctx.getUser());

		NoticeRecord noticeRecord = ctx.getDslContext().insertInto(NOTICE)
				.set(NOTICE.DOMAIN, ctx.getDomainId())
				.set(NOTICE.SENDER, user.getId())
				.set(NOTICE.DATE,
						new java.sql.Timestamp((new Date()).getTime()))
				.set(NOTICE.SUBJECT, notice.getTitle())
				.set(NOTICE.STATUS, NoticeStatus.OPEN.value())
				.set(NOTICE.TYPE,
							NoticeType.MESSAGE.value())
				.set(NOTICE.PRIORITY,
						(byte) Priority.valueOf(notice.getPriority()).ordinal())
				.set(NOTICE.RECIPIENT, (notice.getRecipient() != null)
						? notice.getRecipient() : null)
				.returning().fetchOne();

		ctx.getDslContext().insertInto(NOTICE)
				.set(NOTICE.DOMAIN, noticeRecord.getValue(NOTICE.DOMAIN))
				.set(NOTICE.SENDER, noticeRecord.getValue(NOTICE.SENDER))
				.set(NOTICE.DATE, noticeRecord.getValue(NOTICE.DATE))
				.set(NOTICE.SUBJECT, notice.getBody())
				.set(NOTICE.STATUS, noticeRecord.getValue(NOTICE.STATUS))
				.set(NOTICE.TYPE, NoticeType.DESCRIPTION.value())
				.set(NOTICE.PRIORITY, noticeRecord.getValue(NOTICE.PRIORITY))
				.set(NOTICE.RECIPIENT, noticeRecord.getValue(NOTICE.RECIPIENT))
				.set(NOTICE.NOTICE_, noticeRecord.getValue(NOTICE.ID))
				.execute();

		Notice object = new Notice();
		object.setId(noticeRecord.getValue(NOTICE.ID));
		object.setDomain(ctx.getDomainId());
		object.setStartDate(today);
		object.setSender(user);
		object.setTitle(notice.getTitle());
		object.setBody(notice.getBody());
		object.setRecipient(noticeRecord.getValue(NOTICE.RECIPIENT));
		object.setStatus(noticeRecord.getValue(NOTICE.STATUS));
		object.setType(notice.getType());
		object.setPriority(notice.getPriority());	

		for (Tag tag : notice.getTags()) {
			ctx.getDslContext().insertInto(NOTICE_TAG)
					.set(NOTICE_TAG.NOTICE, noticeRecord.getValue(NOTICE.ID))
					.set(NOTICE_TAG.TAG, tag.getId()).set(NOTICE_TAG.START_DATE,
							noticeRecord.getValue(NOTICE.DATE))
					.execute();

			Tag tagAux = new Tag();
			tagAux.setId(tag.getId());
			tagAux.setName(tag.getName());
			tagAux.setColor((tag.getColor() != null) ? tag.getColor() : "");
			object.addTag(tagAux);
		}

		return object;
	}

	public static Notice changeNoticeState(AONContext ctx, Notice notice) {

		try {

			ctx.getDslContext().update(NOTICE)
					.set(NOTICE.STATUS, notice.getStatus())
					.where(NOTICE.ID.eq(notice.getId())).execute();

			ctx.getDslContext().update(NOTICE)
					.set(NOTICE.STATUS, notice.getStatus())
					.where(NOTICE.NOTICE_.eq(notice.getId())).execute();

			return buildNoticeById(ctx, notice.getId());
		} catch (Exception ex) {
			System.out
					.println(ex.getMessage() + " " + ex.getLocalizedMessage());
			throw ex;
		}
	}

	public static Notice editNotice(AONContext ctx, Notice notice) {

		try {
			ctx.getDslContext().update(NOTICE)
					.set(NOTICE.SUBJECT, notice.getTitle())
					.where(NOTICE.ID.eq(notice.getId())).execute();

			ctx.getDslContext().update(NOTICE)
					.set(NOTICE.SUBJECT, notice.getBody())
					.where(NOTICE.NOTICE_.eq(notice.getId())).execute();

			return buildNoticeById(ctx, notice.getId());

		} catch (Exception ex) {
			System.out
					.println(ex.getMessage() + " " + ex.getLocalizedMessage());
			throw ex;
		}
	}

	public static boolean deleteNotice(AONContext ctx, Integer id) {

		try {
			ctx.getDslContext().delete(NOTICE_TAG)
					.where(NOTICE_TAG.NOTICE.eq(id)).execute();

			ctx.getDslContext().delete(NOTICE).where(NOTICE.NOTICE_.eq(id))
					.execute();

			ctx.getDslContext().delete(NOTICE).where(NOTICE.ID.eq(id))
					.execute();
			// FALTA LA GESTION DE LOS COMENTARIOS

			return true;

		} catch (Exception ex) {
			System.err.println("Exception: " + ex.getMessage());
			return false;
		}

	}

	public static Tag addNewTag(AONContext ctx, Tag tag) {

		Tag newTag = new Tag();

		TagRecord record = ctx.getDslContext().insertInto(TAG)
				.set(TAG.DOMAIN, ctx.getDomainId()).set(TAG.NAME, tag.getName())
				.set(TAG.TYPE, tag.getType())
				.set(TAG.COLOR,
						(tag.getColor() != null) ? tag.getColor() : null)
				.returning().fetchOne();

		newTag.setId(record.getValue(TAG.ID));
		newTag.setName(record.getValue(TAG.NAME));
		newTag.setDomain(record.getValue(TAG.DOMAIN));
		newTag.setType(record.getValue(TAG.TYPE));
		newTag.setColor(record.getValue(TAG.COLOR));

		return newTag;
	}

	public static Tag editTag(AONContext ctx, String labelName, Tag tag) {

		ctx.getDslContext().update(TAG).set(TAG.NAME, tag.getName())
				.where(TAG.DOMAIN.eq(ctx.getDomainId())
						.and(TAG.TYPE.eq(TagType.OFFICE_NOTICE.value()))
						.and(TAG.NAME.eq(labelName)))
				.execute();

		TagRecord tagRecord = ctx.getDslContext().selectFrom(TAG)
				.where(TAG.DOMAIN.eq(ctx.getDomainId())
						.and(TAG.TYPE.eq(TagType.OFFICE_NOTICE.value()))
						.and(TAG.NAME.eq(tag.getName())))
				.fetchOne();

		Tag editTag = new Tag();
		editTag.setId(tagRecord.getValue(TAG.ID));
		editTag.setName(tagRecord.getValue(TAG.NAME));
		editTag.setColor(tagRecord.getValue(TAG.COLOR));

		return editTag;
	}

	public static boolean deleteTag(AONContext ctx, String labelName) {

		try {

			TagRecord tagRecord = ctx.getDslContext().selectFrom(TAG)
					.where(TAG.DOMAIN.eq(ctx.getDomainId())
							.and(TAG.TYPE.equal(TagType.OFFICE_NOTICE.value()))
							.and(TAG.NAME.eq(labelName)))
					.fetchOne();

			ctx.getDslContext().delete(NOTICE_TAG)
					.where(NOTICE_TAG.ID.eq(tagRecord.getValue(TAG.ID)))
					.execute();

			ctx.getDslContext().delete(TAG)
					.where(TAG.ID.eq(tagRecord.getValue(TAG.ID))).execute();

			return true;

		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
			return false;
		}
	}

	public static List<Notice> getOpenNotices1(AONContext ctx) {

		int domainId = ctx.getDomainId();
		List<Notice> notices = new LinkedList<Notice>();
		ctx.getDslContext()
				.selectFrom(NOTICE.rightOuterJoin(NOTICE_TAG)
						.on(NOTICE.ID.eq(NOTICE_TAG.NOTICE)).rightOuterJoin(TAG)
						.on(NOTICE_TAG.TAG.eq(TAG.ID)))
				.where(NOTICE.DOMAIN.eq(domainId)
						.and(NOTICE.NOTICE_.isNull())
						.and(TAG.TYPE.eq(TagType.OFFICE_STATUS.value()))
						.and(NOTICE_TAG.TAG.eq(NoticeStatus.OPEN.ordinal())
								.or(NOTICE_TAG.TAG.eq(NoticeStatus.REOPEN.ordinal()))))
				.fetch()
				.stream()
				.forEach( record -> {
					
					Integer noticeId = record.getValue(NOTICE.ID);
					
					Notice notice = new Notice();
					notice.setId(record.getValue(NOTICE.ID));
					notice.setDomain(domainId);
					notice.setTitle(record.getValue(NOTICE.SUBJECT));
					
					String body = ctx.getDslContext()
							.selectFrom(NOTICE.rightOuterJoin(NOTICE_TAG)
									.on(NOTICE.ID.eq(NOTICE_TAG.NOTICE))
									.rightOuterJoin(TAG).on(NOTICE_TAG.TAG.eq(TAG.ID)))
							.where(NOTICE.DOMAIN.eq(domainId)
									.and(NOTICE.NOTICE_.eq(noticeId)
									.and(TAG.TYPE.eq(NoticeType.DESCRIPTION.value()))))
							.fetchOne().getValue(NOTICE.SUBJECT);
					
					Integer priority = ctx.getDslContext()
							.selectFrom(NOTICE_TAG.rightOuterJoin(TAG).on(NOTICE_TAG.TAG.eq(TAG.ID)))
							.where(NOTICE.DOMAIN.eq(domainId)
									.and(NOTICE_TAG.NOTICE.eq(noticeId)
									.and(TAG.TYPE.eq(TagType.OFFICE_PRIORITY.value()))))
							.fetchOne().getValue(NOTICE_TAG.TAG);
					
							
					
				});
				
		
		

		return notices;
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
					notice.setStartDate(record.getValue(NOTICE.DATE));

					User user = new User();
					UserRecord userRecord = ctx.getDslContext().selectFrom(USER)
							.where(USER.ID.eq(record.getValue(NOTICE.SENDER)))
							.fetchOne();
					user.setId(userRecord.getValue(USER.ID));
					user.setName(userRecord.getValue(USER.LOGIN));

					notice.setSender(user);
					notice.setTitle(record.getValue(NOTICE.SUBJECT));
					notice.setRecipient(record.getValue(NOTICE.RECIPIENT));
					notice.setStatus(record.getValue(NOTICE.STATUS));

					NoticeRecord bodyRecord = ctx.getDslContext()
							.selectFrom(NOTICE).where(NOTICE.DOMAIN.eq(domainId)
									.and(NOTICE.NOTICE_.eq(id)))
							.fetchOne();
					String body = bodyRecord.getValue(NOTICE.SUBJECT);
					notice.setBody((body != null) ? body : "");

					// Acuerdate que tengo que usar el id del body para ir
					// enlazando comentarios

					byte ordinalType = record.getValue(NOTICE.TYPE);
					notice.setType(NoticeType.values()[ordinalType].getValue());
					String priority = Priority.values()[record
							.getValue(NOTICE.PRIORITY)].getValue();
					notice.setPriority(priority);

					ctx.getDslContext().select().from(NOTICE_TAG)
							.rightOuterJoin(TAG).on(NOTICE_TAG.TAG.eq(TAG.ID))
							.where(NOTICE_TAG.NOTICE.eq(id)).fetch().stream()
							.forEach(noticeTag -> {
						Tag tag = new Tag();
						tag.setId(noticeTag.getValue(TAG.ID));
						tag.setName(noticeTag.getValue(TAG.NAME));
						tag.setColor(noticeTag.getValue(TAG.COLOR));

						notice.addTag(tag);

					});

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
				.fetch().stream().forEach(noticeRecord -> {
					Notice notice = new Notice();
					int id = noticeRecord.getValue(NOTICE.ID);
					notice.setId(id);
					notice.setDomain(noticeRecord.getValue(NOTICE.DOMAIN));
					notice.setStartDate(noticeRecord.getValue(NOTICE.DATE));

					User user = new User();
					UserRecord userRecord = ctx.getDslContext().selectFrom(USER)
							.where(USER.ID
									.eq(noticeRecord.getValue(NOTICE.SENDER)))
							.fetchOne();
					user.setId(userRecord.getValue(USER.ID));
					user.setName(userRecord.getValue(USER.LOGIN));

					notice.setSender(user);

					notice.setTitle(noticeRecord.getValue(NOTICE.SUBJECT));
					notice.setRecipient(
							noticeRecord.getValue(NOTICE.RECIPIENT));
					notice.setStatus(noticeRecord.getValue(NOTICE.STATUS));

					String body = ctx.getDslContext().selectFrom(NOTICE)
							.where(NOTICE.DOMAIN.eq(domainId)
									.and(NOTICE.NOTICE_.eq(id)))
							.fetchOne().getValue(NOTICE.SUBJECT);
					notice.setBody((body != null) ? body : "");

					// Acuerdate que tengo que usar el id del body para ir
					// enlazando comentarios

					byte ordinalType = noticeRecord.getValue(NOTICE.TYPE);
					notice.setType(NoticeType.values()[ordinalType].getValue());

					ctx.getDslContext().select().from(NOTICE_TAG)
							.rightOuterJoin(TAG).on(NOTICE_TAG.TAG.eq(TAG.ID))
							.where(NOTICE_TAG.NOTICE.eq(id)).fetch().stream()
							.forEach(noticeTag -> {

						Tag tag = new Tag();
						tag.setId(noticeTag.getValue(TAG.ID));
						tag.setName(noticeTag.getValue(TAG.NAME));
						tag.setColor(noticeTag.getValue(TAG.COLOR));

						notice.addTag(tag);

					});

					String priority = Priority.values()[noticeRecord
							.getValue(NOTICE.PRIORITY)].getValue();
					notice.setPriority(priority);

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
					notice.setStartDate(record.getValue(NOTICE.DATE));

					Date endDate = ctx.getDslContext().selectFrom(NOTICE_TAG)
							.where(NOTICE_TAG.NOTICE.eq(id)).fetchOne()
							.getValue(NOTICE_TAG.END_DATE);
					notice.setEndDate(endDate);

					User user = new User();
					UserRecord userRecord = ctx.getDslContext().selectFrom(USER)
							.where(USER.ID.eq(record.getValue(NOTICE.SENDER)))
							.fetchOne();
					user.setId(userRecord.getValue(USER.ID));
					user.setName(userRecord.getValue(USER.LOGIN));

					notice.setSender(user);

					notice.setTitle(record.getValue(NOTICE.SUBJECT));
					notice.setRecipient(record.getValue(NOTICE.RECIPIENT));
					notice.setStatus(record.getValue(NOTICE.STATUS));

					String body = ctx.getDslContext().selectFrom(NOTICE)
							.where(NOTICE.DOMAIN.eq(domainId)
									.and(NOTICE.NOTICE_.eq(id)))
							.fetchOne().getValue(NOTICE.SUBJECT);
					notice.setBody((body != null) ? body : "");

					// Acuerdate que tengo que usar el id del body para ir
					// enlazando comentarios

					byte ordinalType = record.getValue(NOTICE.TYPE);
					notice.setType(NoticeType.values()[ordinalType].getValue());

					Cursor<Record> labels = ctx.getDslContext().select()
							.from(NOTICE_TAG).rightOuterJoin(TAG)
							.on(NOTICE_TAG.TAG.eq(TAG.ID))
							.where(NOTICE_TAG.NOTICE.eq(id)).fetchLazy();

					Iterator<Record> iterator = labels.iterator();
					while (iterator.hasNext()) {

						Record tagRecord = iterator.next();
						Tag tag = new Tag();
						tag.setId(tagRecord.getValue(TAG.ID));
						tag.setName(tagRecord.getValue(TAG.NAME));
						tag.setColor(tagRecord.getValue(TAG.COLOR));

						notice.addTag(tag);
					}

					try {

						String priorityName = ctx.getDslContext()
								.selectFrom(TAG.rightOuterJoin(NOTICE_TAG)
										.on(TAG.ID.eq(NOTICE_TAG.TAG)))
								.where(TAG.TYPE
										.eq(TagType.OFFICE_PRIORITY.value())
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

	public static Tag getTag(AONContext ctx, String name) {

		TagRecord tagRecord = ctx.getDslContext().selectFrom(TAG)
				.where(TAG.DOMAIN.eq(ctx.getDomainId()).and(TAG.NAME.eq(name))
						.and(TAG.TYPE.eq(TagType.OFFICE_NOTICE.value())))
				.fetchOne();
		Tag tag = new Tag();
		tag.setId(tagRecord.getValue(TAG.ID));
		tag.setName(tagRecord.getValue(TAG.NAME));
		tag.setColor(tagRecord.getValue(TAG.COLOR));

		return tag;
	}

	public static List<Tag> getTags(AONContext ctx) {

		int domainId = ctx.getDomainId();
		List<Tag> tags = new LinkedList<Tag>();

		ctx.getDslContext().selectFrom(TAG)
				.where(TAG.DOMAIN.eq(domainId)
						.and(TAG.TYPE.eq(TagType.OFFICE_NOTICE.value())
								.or(TAG.TYPE.eq(TagType.OFFICE_PRIORITY.value()))
								.or(TAG.TYPE.eq(TagType.OFFICE_STATUS.value()))
								.or(TAG.TYPE.eq(TagType.OFFICE_TYPE.value()))))
				.fetch().stream().forEach(record -> {
					Tag tag = new Tag();

					tag.setId(record.getValue(TAG.ID));
					tag.setDomain(record.getValue(TAG.DOMAIN));
					tag.setName(record.getValue(TAG.NAME));
					tag.setType(record.getValue(TAG.TYPE));
					tag.setColor(record.getValue(TAG.COLOR));

					tags.add(tag);
				});

		return tags;
	}

	// -----------------------------------------------------------------------

	private static Notice buildNoticeById(AONContext ctx, Integer id) {

		NoticeRecord noticeRecord = ctx.getDslContext().selectFrom(NOTICE)
				.where(NOTICE.ID.eq(id)).fetchOne();

		Notice editNotice = new Notice();
		editNotice.setId(id);
		editNotice.setDomain(noticeRecord.getValue(NOTICE.DOMAIN));
		editNotice.setStartDate(noticeRecord.getValue(NOTICE.DATE));

		User user = new User();
		UserRecord userRecord = ctx.getDslContext().selectFrom(USER)
				.where(USER.ID.eq(noticeRecord.getValue(NOTICE.SENDER)))
				.fetchOne();
		user.setId(userRecord.getValue(USER.ID));
		user.setName(userRecord.getValue(USER.LOGIN));
		editNotice.setSender(user);
		editNotice.setTitle(noticeRecord.getValue(NOTICE.SUBJECT));

		String body = ctx.getDslContext().selectFrom(NOTICE)
				.where(NOTICE.NOTICE_.eq(id)).fetchOne()
				.getValue(NOTICE.SUBJECT);

		editNotice.setBody(body);
		editNotice.setRecipient(noticeRecord.getValue(NOTICE.RECIPIENT));
		editNotice.setStatus(noticeRecord.getValue(NOTICE.STATUS));
		editNotice
				.setType(NoticeType.values()[noticeRecord.getValue(NOTICE.TYPE)]
						.getValue());
		editNotice.setPriority(
				Priority.values()[noticeRecord.getValue(NOTICE.PRIORITY)]
						.getValue());
		ctx.getDslContext().select()
				.from(NOTICE_TAG.rightOuterJoin(TAG)
						.on(NOTICE_TAG.TAG.eq(TAG.ID)))
				.where(NOTICE_TAG.NOTICE.eq(id)).fetch().stream()
				.forEach(record -> {
					Tag tag = new Tag();
					tag.setId(record.getValue(NOTICE_TAG.ID));
					tag.setName(record.getValue(TAG.NAME));
					tag.setColor(record.getValue(TAG.COLOR));
					editNotice.addTag(tag);
				});

		return editNotice;
	}
}
