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
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;

import com.esferalia.aon.jooq.tables.records.NoticeRecord;
import com.esferalia.aon.jooq.tables.records.NoticeTagRecord;
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
						(notice.getCompany() != null) ? notice.getCompany()
								: null)
				.set(NOTICE.RECIPIENT,
						(notice.getRecipient() != null) ? notice.getRecipient()
								: null)
				.set(NOTICE.WORK_GROUP, (notice.getWorkgroup() != null)
						? notice.getWorkgroup() : null)
				.returning().fetchOne();

		ctx.getDslContext().insertInto(NOTICE)
				.set(NOTICE.DOMAIN, noticeRecord.getValue(NOTICE.DOMAIN))
				.set(NOTICE.SENDER, noticeRecord.getValue(NOTICE.SENDER))
				.set(NOTICE.DATE, noticeRecord.getValue(NOTICE.DATE))
				.set(NOTICE.SUBJECT, notice.getTitle())
				.set(NOTICE.PHONE, noticeRecord.getValue(NOTICE.PHONE))
				.set(NOTICE.STATUS, noticeRecord.getValue(NOTICE.STATUS))
				.set(NOTICE.TYPE, noticeRecord.getValue(NOTICE.TYPE))
				.set(NOTICE.PRIORITY, noticeRecord.getValue(NOTICE.PRIORITY))
				.set(NOTICE.WORK_GROUP,
						noticeRecord.getValue(NOTICE.WORK_GROUP))
				.set(NOTICE.NOTICE_, noticeRecord.getValue(NOTICE.ID))
				.execute();			
		
		if ( notice.getTags().isEmpty() ) {
			ctx.getDslContext().insertInto(NOTICE_TAG)
			.set(NOTICE_TAG.NOTICE, noticeRecord.getValue(NOTICE.ID))
			.set(NOTICE_TAG.START_DATE,
					noticeRecord.getValue(NOTICE.DATE))
			.execute();
		}
		else {

			for (Tag tag : notice.getTags()) {
				// PUEDE QUE SOLO VENGA EL NOMBRE
				Integer tagId = ctx.getDslContext().selectFrom(TAG)
						.where(TAG.DOMAIN.eq(ctx.getDomainId())
								.and(TAG.NAME.eq(tag.getName())))
						.fetchOne().getValue(TAG.ID);

				ctx.getDslContext().insertInto(NOTICE_TAG)
						.set(NOTICE_TAG.NOTICE, noticeRecord.getValue(NOTICE.ID))
						.set(NOTICE_TAG.TAG, tagId).set(NOTICE_TAG.START_DATE,
								noticeRecord.getValue(NOTICE.DATE))
						.execute();
			}
		}

		Notice object = new Notice();
		object.setId(noticeRecord.getValue(NOTICE.ID));
		object.setDomain(ctx.getDomainId());
		object.setStartDate(today);		

		User user = new User();
		UserRecord userRecord = ctx.getDslContext().selectFrom(USER)
				.where(USER.DOMAIN.eq(ctx.getDomainId())
						.and(USER.LOGIN.eq(ctx.getUser())))
				.fetchOne();
		user.setId(userRecord.getValue(USER.ID));
		user.setName(ctx.getUser());

		object.setSender(user);
		object.setTitle(notice.getTitle());
		object.setBody(notice.getBody());
		object.setRecipient(noticeRecord.getValue(NOTICE.RECIPIENT));
		object.setContact(noticeRecord.getValue(NOTICE.PHONE));
		object.setSource(noticeRecord.getValue(NOTICE.SOURCE));
		object.setCompany(noticeRecord.getValue(NOTICE.COMPANY));
		object.setStatus(noticeRecord.getValue(NOTICE.STATUS));
		object.setWorkgroup(noticeRecord.getValue(NOTICE.WORK_GROUP));
		object.setType(NoticeType.values()[noticeRecord.getValue(NOTICE.TYPE)]
				.getValue());
		object.setPriority(
				Priority.values()[noticeRecord.getValue(NOTICE.PRIORITY)]
						.getValue());

		return object;
	}
	
	public static Notice editNotice(AONContext ctx, Notice notice) {		
		
		NoticeRecord noticeRecord = ctx.getDslContext().update(NOTICE)
		.set(NOTICE.SUBJECT, notice.getTitle())
		.set(NOTICE.STATUS, notice.getStatus())
		.set(NOTICE.TYPE,
				(byte) NoticeType.valueOf(notice.getType()).ordinal())
		.set(NOTICE.PRIORITY,
				(byte) Priority.valueOf(notice.getPriority()).ordinal())
		.where(NOTICE.ID.eq(notice.getId()))
		.returning().fetchOne();
		
		ctx.getDslContext().update(NOTICE)
		.set(NOTICE.SUBJECT, notice.getBody())
		.set(NOTICE.STATUS, notice.getStatus())
		.set(NOTICE.TYPE,
				(byte) NoticeType.valueOf(notice.getType()).ordinal())
		.set(NOTICE.PRIORITY,
				(byte) Priority.valueOf(notice.getPriority()).ordinal())
		.where(NOTICE.NOTICE_.eq(notice.getId()))
		.execute();
		
		ctx.getDslContext().delete(NOTICE_TAG)
		.where(NOTICE_TAG.NOTICE.eq(notice.getId()))
		.execute();
		
		for (Tag tag : notice.getTags()) {
			// PUEDE QUE SOLO VENGA EL NOMBRE
			Integer tagId = ctx.getDslContext().selectFrom(TAG)
					.where(TAG.DOMAIN.eq(ctx.getDomainId())
							.and(TAG.NAME.eq(tag.getName())))
					.fetchOne().getValue(TAG.ID);

			InsertSetMoreStep<NoticeTagRecord> select = ctx.getDslContext().insertInto(NOTICE_TAG)
					.set(NOTICE_TAG.NOTICE, noticeRecord.getValue(NOTICE.ID))
					.set(NOTICE_TAG.TAG, tagId).set(NOTICE_TAG.START_DATE,
							noticeRecord.getValue(NOTICE.DATE));
		}
		
		return notice;
	}

	public static void deleteNotice(AONContext ctx, Notice notice) {
		
		Integer domainId = ctx.getDomainId();
		
		ctx.getDslContext().delete(NOTICE_TAG)
		.where(NOTICE_TAG.NOTICE.eq(notice.getId()))				
		.execute();
		
		ctx.getDslContext().delete(NOTICE)
		.where(NOTICE.NOTICE_.eq(notice.getId())
				.and(NOTICE.DOMAIN.eq(domainId)));
		
		ctx.getDslContext().delete(NOTICE)
		.where(NOTICE.ID.eq(notice.getId()));
		
		//FALTA LA GESTION DE LOS COMENTARIOS
	}

	public static Tag addNewTag(AONContext ctx, Tag tag) {

		Tag newTag = new Tag();

		TagRecord record = ctx.getDslContext().insertInto(TAG)
				.set(TAG.DOMAIN, ctx.getDomainId())
				.set(TAG.NAME, tag.getName())
				.set(TAG.TYPE, tag.getType())
				.set(TAG.COLOR,
						(tag.getColor() != null) ? tag.getColor() : null)
				.returning().fetchOne();

		newTag.setId(record.getValue(TAG.ID));
		newTag.setName(record.getValue(TAG.NAME));
		newTag.setDomain(record.getValue(TAG.DOMAIN));
		newTag.setColor(record.getValue(TAG.COLOR));

		return newTag;
	}
	
	public static void deleteTag (AONContext ctx, Tag tag) {
		
		ctx.getDslContext().delete(NOTICE_TAG)
		.where(NOTICE_TAG.TAG.eq(tag.getId()))
		.execute();
		
		ctx.getDslContext().delete(TAG)
		.where(TAG.ID.eq(tag.getId()))
		.execute();
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
							.where(USER.DOMAIN.eq(ctx.getDomainId())
									.and(USER.LOGIN.eq(ctx.getUser())))
							.fetchOne();
					user.setId(userRecord.getValue(USER.ID));
					user.setName(ctx.getUser());

					notice.setSender(user);
					notice.setTitle(record.getValue(NOTICE.SUBJECT));
					notice.setRecipient(record.getValue(NOTICE.RECIPIENT));
					notice.setContact(record.getValue(NOTICE.PHONE));
					notice.setSource(record.getValue(NOTICE.SOURCE));
					notice.setCompany(record.getValue(NOTICE.COMPANY));
					notice.setStatus(record.getValue(NOTICE.STATUS));
					notice.setWorkgroup(record.getValue(NOTICE.WORK_GROUP));

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
					notice.setStartDate(record.getValue(NOTICE.DATE));
					
					Date endDate = ctx.getDslContext()
							.selectFrom(NOTICE_TAG)
							.where(NOTICE_TAG.NOTICE.eq(id))
							.fetchOne().getValue(NOTICE_TAG.END_DATE);
					notice.setEndDate(endDate);
					
					User user = new User();
					UserRecord userRecord = ctx.getDslContext().selectFrom(USER)
							.where(USER.DOMAIN.eq(ctx.getDomainId())
									.and(USER.LOGIN.eq(ctx.getUser())))
							.fetchOne();
					user.setId(userRecord.getValue(USER.ID));
					user.setName(ctx.getUser());

					notice.setSender(user);

					notice.setTitle(record.getValue(NOTICE.SUBJECT));
					notice.setRecipient(record.getValue(NOTICE.RECIPIENT));
					notice.setContact(record.getValue(NOTICE.PHONE));
					notice.setSource(record.getValue(NOTICE.SOURCE));
					notice.setCompany(record.getValue(NOTICE.COMPANY));
					notice.setStatus(record.getValue(NOTICE.STATUS));
					notice.setWorkgroup(record.getValue(NOTICE.WORK_GROUP));

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
					notice.setStartDate(record.getValue(NOTICE.DATE));
					
					Date endDate = ctx.getDslContext().selectFrom(NOTICE_TAG)
							.where(NOTICE_TAG.NOTICE.eq(id))
							.fetchOne().getValue(NOTICE_TAG.END_DATE);
					notice.setEndDate(endDate);

					User user = new User();
					UserRecord userRecord = ctx.getDslContext().selectFrom(USER)
							.where(USER.DOMAIN.eq(ctx.getDomainId())
									.and(USER.LOGIN.eq(ctx.getUser())))
							.fetchOne();
					user.setId(userRecord.getValue(USER.ID));
					user.setName(ctx.getUser());

					notice.setSender(user);

					notice.setTitle(record.getValue(NOTICE.SUBJECT));
					notice.setRecipient(record.getValue(NOTICE.RECIPIENT));
					notice.setContact(record.getValue(NOTICE.PHONE));
					notice.setSource(record.getValue(NOTICE.SOURCE));
					notice.setCompany(record.getValue(NOTICE.COMPANY));
					notice.setStatus(record.getValue(NOTICE.STATUS));
					notice.setWorkgroup(record.getValue(NOTICE.WORK_GROUP));

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
	
	public static List<Tag> getTags(AONContext ctx) {
		
		int domainId = ctx.getDomainId();
		List<Tag> tags = new LinkedList<Tag>();
		
		ctx.getDslContext().selectFrom(TAG)
		.where(TAG.DOMAIN.eq(domainId)
				.and(TAG.TYPE.eq(TagType.NOTICE.value())))
		.fetch()
		.stream()
		.forEach(record -> {
			Tag tag = new Tag();
			
			tag.setId(record.getValue(TAG.ID));
			tag.setName(record.getValue(TAG.NAME));
			tag.setColor(record.getValue(TAG.COLOR));
			
			tags.add(tag);
		});
		
		return tags;
	}
}
