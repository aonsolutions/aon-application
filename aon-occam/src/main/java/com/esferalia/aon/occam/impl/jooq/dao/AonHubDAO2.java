package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Notice.NOTICE;
import static com.esferalia.aon.jooq.tables.NoticeTag.NOTICE_TAG;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.Tag.TAG;
import static com.esferalia.aon.jooq.tables.User.USER;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.jooq.tables.records.NoticeRecord;
import com.esferalia.aon.jooq.tables.records.NoticeTagRecord;
import com.esferalia.aon.jooq.tables.records.TagRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.office.NoticeFilter;
import com.esferalia.aon.occam.api.model.office.NoticeFilterImpl;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.NoticeStatus;
import com.esferalia.aon.occam.api.model.type.NoticeType;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.watson.server.AonEnumUtils;

public class AonHubDAO2 {

	private static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

	public static Notice insertNotice(AONContext ctx, Notice notice) {
		// @formatter:off
		Timestamp date = getTime(notice.getStartDate());
		int id = ctx.getDslContext()
				.insertInto(NOTICE)
				.set(NOTICE.DOMAIN, ctx.getDomainId())
				.set(NOTICE.DATE, date)						
				.set(NOTICE.SENDER, notice.getSender().getId())
				.set(NOTICE.SUBJECT, notice.getTitle())
				.set(NOTICE.STATUS, NoticeStatus.OPEN.value())
				.set(NOTICE.TYPE, NoticeType.TICKET.value())
				.set(NOTICE.COMPANY, (notice.getCompany() != null) ? notice.getCompany(): null)
				.set(NOTICE.SOURCE, (notice.getSource() != null) ? String.valueOf(notice.getSource()) : null)
				.set(NOTICE.PRIORITY, (byte) 0)
				.returning( NOTICE.ID )
				.fetchOne()
				.getValue(NOTICE.ID);				

		ctx.getDslContext().insertInto(NOTICE_TAG)
				.set(NOTICE_TAG.NOTICE, id)
				.set(NOTICE_TAG.START_DATE, date)
				.set(NOTICE_TAG.TAG, getOpenNoticesId(ctx.getDslContext()))
				.set(NOTICE_TAG.USER, notice.getSender().getId())
				.returning().fetchOne();
		// @formatter:on
		
		return getTicketNotice(ctx, id);
	}

	public static Notice changeNoticeState(AONContext ctx, Notice notice) {

		NoticeTagRecord updateTag = ctx.getDslContext().update(NOTICE_TAG)
				.set(NOTICE_TAG.END_DATE, getTime(notice.getStartDate()))
				.set(NOTICE_TAG.USER, notice.getSender().getId())
				.where(NOTICE_TAG.ID.eq(ctx.getDslContext()
						.select(NOTICE_TAG.ID).from(NOTICE_TAG).join(TAG)
						.on(NOTICE_TAG.TAG.eq(TAG.ID))
						.where(NOTICE_TAG.END_DATE.isNull()
								.and(NOTICE_TAG.NOTICE.eq(notice.getId()))
								.and(TAG.TYPE
										.eq(TagType.OFFICE_STATUS.value())))))
				.returning().fetchOne();

		ctx.getDslContext().insertInto(NOTICE_TAG)
				.set(NOTICE_TAG.NOTICE, notice.getId())
				.set(NOTICE_TAG.TAG, updateTag.getValue(NOTICE_TAG.TAG))
				.set(NOTICE_TAG.START_DATE,
						updateTag.getValue(NOTICE_TAG.END_DATE))
				.set(NOTICE_TAG.USER, notice.getSender().getId()).execute();

		return getTicketNotice(ctx, notice.getId());

	}

	public static Notice editNotice(AONContext ctx, Notice notice) {

		ctx.getDslContext().update(NOTICE)
				.set(NOTICE.SUBJECT, notice.getTitle())
				.where(NOTICE.ID.eq(notice.getId())).execute();

		ctx.getDslContext().update(NOTICE).set(NOTICE.SUBJECT, notice.getBody())
				.where(NOTICE.NOTICE_.eq(notice.getId())
						.and(NOTICE.TYPE.eq(NoticeType.MESSAGE.value())))
				.execute();

		return getTicketNotice(ctx, notice.getId());
	}

	public static Notice createComment(AONContext ctx, int headId,
			Notice comment) {

		NoticeRecord noticeRecord = ctx.getDslContext().insertInto(NOTICE)
				.set(NOTICE.DOMAIN, ctx.getDomainId())
				.set(NOTICE.DATE, getTime(comment.getStartDate()))
				.set(NOTICE.SENDER, comment.getSender().getId())
				.set(NOTICE.SUBJECT, comment.getBody())
				.set(NOTICE.STATUS, NoticeStatus.OPEN.value())
				.set(NOTICE.TYPE, NoticeType.COMMENT.value())
				.set(NOTICE.NOTICE_, headId).set(NOTICE.PRIORITY, (byte) 0)
				.returning().fetchOne();

		comment.setId(noticeRecord.getValue(NOTICE.ID));
		comment.setStartDate(noticeRecord.getValue(NOTICE.DATE));
		return comment;
	}

	public static Notice editComment(AONContext ctx, int commentId,
			String body) {

		ctx.getDslContext().update(NOTICE).set(NOTICE.SUBJECT, body)
				.where(NOTICE.ID.eq(commentId)).execute();

		// @formatter:off
		Record record = ctx.getDslContext()
				.select(NOTICE.fields())
				.select(USER.fields())
				.from(NOTICE)
				.join(USER)
				.on(NOTICE.SENDER.eq(USER.ID))
				.where(NOTICE.ID.eq(commentId))
				.fetchOne();
		// @formatter:on

		return new FullNoticeCommentFiller().apply(record);
	}
	
	public static Tag insertTag(AONContext ctx, Tag tag) {
		
		TagRecord tagRecord = ctx.getDslContext()
				.insertInto(TAG)
				.set(TAG.DOMAIN, ctx.getDomainId())
				.set(TAG.NAME, tag.getName())
				.set(TAG.TYPE, tag.getType())
				.set(TAG.COLOR, (tag.getColor() != null) ? tag.getColor() : null)
				.returning()
				.fetchOne();
				
		return new FullTagFiller().apply(tagRecord);
	}

	private static Notice getTicketNotice(AONContext ctx, int noticeId) {

		// @formatter:off 
		Record record = ctx.getDslContext()
				.select(NOTICE.fields())
				.select(USER.fields())
				.from(NOTICE)
				.join(USER)
				.on(NOTICE.SENDER.eq(USER.ID))
				.where(NOTICE.ID.eq(noticeId))
				.fetchOne();
		// @formatter:off	
		
		Notice notice = new FullNoticeFiller().apply(record);
		notice.setTags(fillOfficeTags(ctx, notice)
				.collect(Collectors.toCollection(LinkedList::new)));
		return notice;
	}
	
	public static int getSelectedCount(AONContext ctx, NoticeFilter filter) {
		// @formatter:off
		return ctx.getDslContext()
				.select(NOTICE.fields())
				.select(USER.fields())
				.from(NOTICE)
				.join(USER)
				.on(NOTICE.SENDER.eq(USER.ID))
				.where(NOTICE.DOMAIN.eq(ctx.getDomainId()))
				.and(NOTICE.NOTICE_.isNull())
				.and(NOTICE.TYPE.eq(NoticeType.TICKET.value()))
				.orderBy(NOTICE.DATE.desc())
				.fetch()
				.stream()
				.map(new FullNoticeFiller())
				.peek(notice -> notice.setTags(fillOfficeTags(ctx, notice)
						.collect(Collectors.toCollection(LinkedList::new))))
				.filter(notice -> new NoticeFilterImpl().test(filter, notice))
				.collect(Collectors.toCollection(LinkedList::new))
				.size();
		// @formatter:on
	}

	public static List<Notice> getTicketNotices(AONContext ctx,
			NoticeFilter filter) {
		// @formatter:off
		return ctx.getDslContext()
				.select(NOTICE.fields())
				.select(USER.fields())
				.from(NOTICE)
				.join(USER)
				.on(NOTICE.SENDER.eq(USER.ID))
				.where(NOTICE.DOMAIN.eq(ctx.getDomainId()))
				.and(NOTICE.NOTICE_.isNull())
				.and(NOTICE.TYPE.eq(NoticeType.TICKET.value()))
				.orderBy(NOTICE.DATE.desc())
				.fetch()
				.stream()
				.map(new FullNoticeFiller())
				.peek(notice -> notice.setTags(fillOfficeTags(ctx, notice)
						.collect(Collectors.toCollection(LinkedList::new))))
				.peek(notice -> notice.addComments(fillNoticeComments(ctx, notice)
						.collect(Collectors.toCollection(LinkedList::new))))				
				.peek(notice -> notice.setType(setTag(notice.getTags().stream()
						.filter(tag -> tag.getType() == TagType.OFFICE_TYPE.value())
						.collect(Collectors.toCollection(LinkedList::new)))))
				.peek(notice -> notice.setPriority(setTag(notice.getTags().stream()
						.filter(tag -> tag.getType() == TagType.OFFICE_PRIORITY.value())
						.collect(Collectors.toCollection(LinkedList::new)))))
				.peek(notice -> notice.setStatus(setTag(notice.getTags().stream()
						.filter(tag -> tag.getType() == TagType.OFFICE_STATUS.value())
						.collect(Collectors.toCollection(LinkedList::new)))))				
				.filter(notice -> new NoticeFilterImpl().test(filter, notice))
				.skip(filter.getOffset())
				.limit(50)
				.collect(Collectors.toCollection(LinkedList::new));
		// @formatter:on
	}
	
	private static String setTag(List<Tag> tags) {
		
		for (Tag tag : tags) {
			if (tag.getEndDate() == null)
				return tag.getName();
		}
		return null;
	}

	private static class FullNoticeFiller implements Function<Record, Notice> {

		@Override
		public Notice apply(Record record) {
			Notice notice = new Notice();
			notice.setId(record.getValue(NOTICE.ID));
			notice.setDomain(record.getValue(NOTICE.DOMAIN));
			notice.setStartDate(record.getValue(NOTICE.DATE));
			notice.setCompany(record.getValue(NOTICE.COMPANY));
			notice.setSource(record.getValue(NOTICE.SOURCE));
			notice.setSender(new MinimalUserFiller().apply(record));
			notice.setTitle(record.getValue(NOTICE.SUBJECT));
			notice.setRecipient(record.getValue(NOTICE.RECIPIENT));
			return notice;
		}
	}

	private static class FullNoticeCommentFiller
			implements Function<Record, Notice> {

		@Override
		public Notice apply(Record record) {
			Notice comment = new Notice();
			comment.setId(record.getValue(NOTICE.ID));
			comment.setDomain(record.getValue(NOTICE.DOMAIN));
			comment.setStartDate(record.getValue(NOTICE.DATE));
			comment.setSender(new MinimalUserFiller().apply(record));
			comment.setBody(record.getValue(NOTICE.SUBJECT));
			return comment;
		}
	}
	
	private static class FullTagFiller implements Function<Record, Tag> {
		
		@Override
		public Tag apply(Record record) {			
			Tag tag = new Tag();
			tag.setId(record.getValue(TAG.ID));
			tag.setDomain(record.getValue(TAG.DOMAIN));
			tag.setName(record.getValue(TAG.NAME));
			tag.setType(record.getValue(TAG.TYPE));
			tag.setColor(record.getValue(TAG.COLOR));
			return tag;

		}
	}

	private static class MinimalUserFiller implements Function<Record, User> {
		@Override
		public User apply(Record record) {
			User user = new User();
			user.setId(record.getValue(USER.ID));
			user.setDomain(record.getValue(USER.DOMAIN));
			user.setName(record.getValue(USER.NAME));
			user.setLogin(record.getValue(USER.LOGIN));
			user.setActive(
					AonEnumUtils.getBoolean(record.getValue(USER.ACTIVE)));
			return user;
		}
	}
	
	private static Stream<Notice> fillNoticeComments(AONContext ctx, Notice notice) {
		// @formatter:off
		return ctx.getDslContext()
				.select(NOTICE.fields())
				.select(USER.fields())
				.from(NOTICE)
				.join(USER)
				.on(NOTICE.SENDER.eq(USER.ID))
				.where(NOTICE.DOMAIN.eq(ctx.getDomainId())
						.and(NOTICE.TYPE.eq(NoticeType.COMMENT.value()))
						.and(NOTICE.NOTICE_.eq(notice.getId())))
				.orderBy(NOTICE.DATE.desc())
				.fetch()
				.stream()
				.map(new FullNoticeCommentFiller());
		// @formatter:on
	}

	private static Stream<Tag> fillOfficeTags(AONContext ctx, Notice notice) {
		// @formatter:off
		return ctx.getDslContext()
				.select()
				.from(NOTICE_TAG)
				.join(TAG)
				.on(NOTICE_TAG.TAG.eq(TAG.ID))
				.rightOuterJoin(USER)
				.on(NOTICE_TAG.USER.eq(USER.ID))
				.where(NOTICE_TAG.NOTICE.eq(notice.getId()))
				.and(TAG.TYPE.eq(TagType.OFFICE_STATUS.value())
						.or(TAG.TYPE.eq(TagType.OFFICE_TYPE.value()))
						.or(TAG.TYPE.eq(TagType.OFFICE_PRIORITY.value()))
						.or(TAG.TYPE.eq(TagType.OFFICE_NOTICE.value())))				
				.orderBy(NOTICE_TAG.START_DATE.desc())
				.fetch()
				.stream()
				.map(new FullNoticeTagFiller());
		// @formatter:off
	}

	private static class FullNoticeTagFiller implements Function<Record, Tag> {

		@Override
		public Tag apply(Record record) {
			Tag tag = new Tag();
			tag.setId(record.getValue(TAG.ID));
			tag.setDomain(record.getValue(NOTICE.DOMAIN));
			tag.setName(record.getValue(TAG.NAME));
			tag.setStartDate(record.getValue(NOTICE_TAG.START_DATE));
			tag.setEndDate(record.getValue(NOTICE_TAG.END_DATE));
			tag.setType(record.getValue(TAG.TYPE));
			tag.setColor(record.getValue(TAG.COLOR));
			tag.setUser(record.getValue(NOTICE_TAG.USER) != null
					? new MinimalUserFiller().apply(record) : null);
			return tag;
		}
	}

	public static List<Registry> getRegistries(AONContext ctx,
			int parentDomain) {

		SelectConditionStep<Record1<Integer>> domainSelect = ctx.getDslContext()
				.select(DOMAIN.ID).from(DOMAIN)
				.where(DOMAIN.ID.eq(ctx.getDomainId())
						.and(DOMAIN.PARENT.eq(parentDomain)));
		// @formatter:off
		return ctx.getDslContext()
				.select()
				.from(REGISTRY
				.join(CUSTOMER)
				.on(REGISTRY.ID.eq(CUSTOMER.REGISTRY)))
				.where(REGISTRY.DOMAIN.in(domainSelect))
				.orderBy(REGISTRY.NAME)
				.fetch()
				.stream()
				.map(new FullCustomerFiller())
				.collect(Collectors.toCollection(LinkedList::new));
		// @formatter:on
	}

	private static class FullCustomerFiller implements Function<Record, Registry> {

		@Override
		public Registry apply(Record record) {
			Registry reg = new Registry();
			reg.setId(record.getValue(REGISTRY.ID));
			reg.setName(record.getValue(REGISTRY.NAME));
			reg.setAlias(record.getValue(REGISTRY.ALIAS));
			reg.setDocument(record.getValue(REGISTRY.DOCUMENT));
			return reg;
		}
	}

	public static List<RegistryMedia> fillRMedias(AONContext ctx,
			int parentDomain) {

		SelectConditionStep<Record1<Integer>> domainSelect = ctx.getDslContext()
				.select(DOMAIN.ID).from(DOMAIN)
				.where(DOMAIN.ID.eq(ctx.getDomainId())
						.and(DOMAIN.PARENT.eq(parentDomain)));
		// @formatter:off
		return ctx.getDslContext()
				.select()
				.from(RMEDIA.join(REGISTRY).on(RMEDIA.REGISTRY.eq(REGISTRY.ID)))
				.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(REGISTRY.ID))
				.where(REGISTRY.DOMAIN.eq(domainSelect)
						.and(CUSTOMER.STATUS.eq((byte) 0)).and(
								RMEDIA.MEDIA.eq(MediaType.CELLULAR.value())
										.or(RMEDIA.MEDIA
												.eq(MediaType.EMAIL.value()))))
				.orderBy(REGISTRY.NAME).fetch().stream()
				.map(new FullRMediaFiller())
				.collect(Collectors.toCollection(LinkedList::new));
		// @formatter:on
	}
	
	public static List<User> fillUsersFromNotices(AONContext ctx, Integer parentDomain) {
		// @formatter:off
		SelectConditionStep<Record1<Integer>> select = 
				ctx.getDslContext()
				.select(NOTICE.SENDER)
				.from(NOTICE)
				.where(NOTICE.DOMAIN.eq(ctx.getDomainId()));
		// @formatter:off
		
		if (parentDomain != null)
			select = select.or(NOTICE.DOMAIN.eq(parentDomain));
		
		// @formatter:off
		return ctx.getDslContext()
				.select(USER.fields())
				.from(USER)
				.where(USER.ID.in(select))
				.orderBy(USER.NAME.asc())
				.fetch()
				.stream()
				.map(new MinimalUserFiller())
				.collect(Collectors.toCollection(LinkedList::new));
		// @formatter:on
	}

	private static class FullRMediaFiller
			implements Function<Record, RegistryMedia> {

		@Override
		public RegistryMedia apply(Record record) {

			RegistryMedia rmedia = new RegistryMedia();
			rmedia.setId(record.getValue(RMEDIA.ID));
			rmedia.setDomain(record.getValue(RMEDIA.DOMAIN));
			rmedia.setMedia(record.getValue(RMEDIA.MEDIA));
			rmedia.setValue(record.getValue(RMEDIA.VALUE));
			rmedia.setComment(record.getValue(RMEDIA.COMMENT));
			rmedia.setRegistry(new FullCustomerFiller().apply(record));
			return rmedia;
		}
	}

	// --------------------------------------------------------------------------------------
	private static SelectConditionStep<Record1<Integer>> getOpenNoticesId(
			DSLContext dsl) {

		return dsl.select(TAG.ID).from(TAG).where(TAG.DOMAIN.eq(0))
				.and(TAG.NAME.eq(NoticeStatus.OPEN.getValue())
						.and(TAG.TYPE.eq(TagType.OFFICE_STATUS.value())));
	}
	/**
	 * 				.set(NOTICE_TAG.END_DATE,
						new java.sql.Timestamp(notice.getStartDate().getTime()))
	 */
	
	private static java.sql.Timestamp getTime(Date date) {
		return new java.sql.Timestamp(date.getTime());
	}
}
