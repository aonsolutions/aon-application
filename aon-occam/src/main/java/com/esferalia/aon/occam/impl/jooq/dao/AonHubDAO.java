package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Notice.NOTICE;
import static com.esferalia.aon.jooq.tables.NoticeTag.NOTICE_TAG;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.Tag.TAG;
import static com.esferalia.aon.jooq.tables.User.USER;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Cursor;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.jooq.tables.records.AppParamRecord;
import com.esferalia.aon.jooq.tables.records.NoticeRecord;
import com.esferalia.aon.jooq.tables.records.RmediaRecord;
import com.esferalia.aon.jooq.tables.records.TagRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.RegistryMediaFilter;
import com.esferalia.aon.occam.api.model.Properties.RegistryMediaProperties;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.office.NotificationInfo;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.NoticeType;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.watson.server.AonEnumUtils;

public class AonHubDAO {

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
	
	public static List<Tag> getTags(AONContext ctx) {
		return ctx.getDslContext()
				.select(TAG.fields())
				.from(TAG)
				.where(TAG.TYPE.eq(TagType.OFFICE_NOTICE.value())
						.or(TAG.TYPE.eq(TagType.OFFICE_PRIORITY.value()))
						.or(TAG.TYPE.eq(TagType.OFFICE_STATUS.value()))
						.or(TAG.TYPE.eq(TagType.OFFICE_TYPE.value()))
						.and(TAG.DOMAIN.eq(0).or(TAG.DOMAIN.eq(ctx.getDomainId()))))
				.orderBy(TAG.NAME.asc())
				.fetch()
				.stream()
				.map(new FullTagFiller())
				.collect(Collectors.toCollection(LinkedList::new));
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

	public static List<User> fillUsersFromNotices(AONContext ctx, Integer parentDomain) {
		SelectConditionStep<Record> users =
				ctx.getDslContext()
				.select(USER.fields())
				.from(USER)
				.where(USER.DOMAIN.eq(ctx.getDomainId()));
		
		if (parentDomain != null)
			users = users.or(USER.DOMAIN.eq(parentDomain));

		return users.orderBy(USER.NAME.asc())
				.fetch()
				.stream()
				.map(new MinimalUserFiller())
				.collect(Collectors.toCollection(LinkedList::new));
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

		return buildTag(tagRecord);
	}

	public static boolean deleteTag(AONContext ctx, String labelName) {

		try {

			TagRecord tagRecord = ctx
					.getDslContext().selectFrom(TAG).where(TAG.DOMAIN
							.eq(ctx.getDomainId()).and(TAG.NAME.eq(labelName)))
					.fetchOne();

			ctx.getDslContext().delete(NOTICE_TAG)
					.where(NOTICE_TAG.TAG.eq(tagRecord.getValue(TAG.ID)))
					.execute();

			ctx.getDslContext().delete(TAG)
					.where(TAG.ID.eq(tagRecord.getValue(TAG.ID))).execute();

			return true;

		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
			return false;
		}
	}

	public static void getComents(AONContext ctx, Integer headId,
			List<Notice> comments) {

		Result<NoticeRecord> result = ctx.getDslContext().selectFrom(NOTICE)
				.where(NOTICE.NOTICE_.eq(headId)
						.and(NOTICE.TYPE.eq(NoticeType.COMMENT.value())))
				.orderBy(NOTICE.DATE.asc()).fetch();

		if (result != null) {
			result.stream().forEach(record -> {
				Notice notice = new Notice();
				notice.setId(record.getValue(NOTICE.ID));
				notice.setDomain(record.getValue(NOTICE.DOMAIN));
				notice.setStartDate(record.getValue(NOTICE.DATE));
				User user = SecurityDAO.getUser(ctx, f -> 
					f.getIdProperty().eq(record.getValue(NOTICE.SENDER)));
				notice.setSender(user);
				notice.setBody(record.getValue(NOTICE.SUBJECT));
				comments.add(notice);
			});
		}
	}

	public static Tag getTag(AONContext ctx, String name) {

		TagRecord tagRecord = ctx.getDslContext()
				.selectFrom(TAG).where(
						TAG.DOMAIN.eq(ctx.getDomainId()).and(TAG.NAME.eq(name))
								.and(TAG.TYPE.eq(TagType.OFFICE_NOTICE.value())
										.or(TAG.TYPE.eq(TagType.OFFICE_PRIORITY
												.value()))
								.or(TAG.TYPE.eq(TagType.OFFICE_TYPE.value()))))
				.fetchOne();

		return buildTag(tagRecord);
	}

	private static Tag buildTag(Record record) {

		Tag tag = new Tag();
		tag.setId(record.getValue(TAG.ID));
		tag.setDomain(record.getValue(TAG.DOMAIN));
		tag.setName(record.getValue(TAG.NAME));
		tag.setType(record.getValue(TAG.TYPE));
		tag.setColor(record.getValue(TAG.COLOR));

		return tag;
	}

	// -----------------------------------------------------------------------
	
	private static final RegistryMediaPropertiesDAO RMEDIA_PROPERTIES = new RegistryMediaPropertiesDAO();
	
	protected static class RegistryMediaPropertiesDAO implements RegistryMediaProperties {
		protected Condition[] getConditions(RegistryMediaFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(RMEDIA.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(RMEDIA.DOMAIN);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<Integer>(RMEDIA.REGISTRY);}
		@Override public Property<Byte> getMediaProperty() {return new FilterDAO.PropertyDAO<Byte>(RMEDIA.MEDIA);}
		@Override public Property<String> getValueProperty() {return new FilterDAO.PropertyDAO<String>(RMEDIA.VALUE);}
		@Override public Property<String> getCommentProperty() {return new FilterDAO.PropertyDAO<String>(RMEDIA.COMMENT);}
		@Override public Property<Byte> getAdministrativeProperty() {return new FilterDAO.PropertyDAO<Byte>(RMEDIA.ADMINISTRATIVE);}
		@Override public Property<Byte> getCommercialProperty() {return new FilterDAO.PropertyDAO<Byte>(RMEDIA.COMMERCIAL);}
		@Override public Property<Byte> getTechnicalProperty() {return new FilterDAO.PropertyDAO<Byte>(RMEDIA.TECHNICAL);}
		@Override public Property<Integer> getRaddressProperty() {return new FilterDAO.PropertyDAO<Integer>(RMEDIA.RADDRESS);}
	}
	
	private static class FullRegistryMediaFiller implements Function<RmediaRecord, RegistryMedia> {

		@Override
		public RegistryMedia apply(RmediaRecord r) {
			return new RegistryMedia().setComment(r.getComment())
					.setDomain(r.getDomain())
					.setId(r.getId())
					.setMedia(r.getMedia())
					.setRegistry(new Registry().setId(r.getRegistry()))
					.setValue(r.getValue());
		}
	}
	
	public static LinkedList<RegistryMedia> getRMediaList(AONContext ctx, RegistryMediaFilter filter) {
		return ctx.getDslContext().select().from(RMEDIA).where(RMEDIA_PROPERTIES.getConditions(filter)).fetchInto(RMEDIA)
				.stream().map(new FullRegistryMediaFiller()).collect(Collectors.toCollection(LinkedList::new));
	}

	public static List<Registry> getRegistries(AONContext ctx,
			Integer parentDomain) {

		Cursor<Record> cursor = null;
		List<Registry> registries = new LinkedList<Registry>();

		try {

			if (parentDomain == null)
				return registries;

			SelectConditionStep<Record1<Integer>> domainSelect = ctx
					.getDslContext().select(DOMAIN.ID).from(DOMAIN)
					.where(DOMAIN.ID.eq(ctx.getDomainId())
							.and(DOMAIN.PARENT.eq(parentDomain)));

			cursor = ctx.getDslContext().select()
					.from(REGISTRY.rightOuterJoin(CUSTOMER)
							.on(REGISTRY.ID.eq(CUSTOMER.REGISTRY)))
					.where(REGISTRY.DOMAIN.in(domainSelect))
					.and(CUSTOMER.STATUS.eq((byte) 0)).fetchLazy();

			for (Record registry : cursor) {
				Registry reg = new Registry();
				reg.setId(registry.getValue(REGISTRY.ID));
				reg.setName(registry.getValue(REGISTRY.NAME));
				reg.setAlias(registry.getValue(REGISTRY.ALIAS));
				reg.setDocument(registry.getValue(REGISTRY.DOCUMENT));
				registries.add(reg);
			}

			return registries;

		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	public static NotificationInfo getNotificationInfo(AONContext ctx){
		Result<AppParamRecord> mail = ctx.getDslContext().select(APP_PARAM.VALUE).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
			.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_MAIL.getValue())).limit(1).fetchInto(APP_PARAM);
		Result<AppParamRecord> auto = ctx.getDslContext().select(APP_PARAM.VALUE).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
			.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_AUTO.getValue())).limit(1).fetchInto(APP_PARAM);
		Result<AppParamRecord> bcc = ctx.getDslContext().select(APP_PARAM.VALUE).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
			.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_BCC.getValue())).limit(1).fetchInto(APP_PARAM);
		Result<AppParamRecord> history = ctx.getDslContext().select(APP_PARAM.VALUE).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
			.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_HISTORY.getValue())).limit(1).fetchInto(APP_PARAM);
		Result<AppParamRecord> signature = ctx.getDslContext().select(APP_PARAM.VALUE).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
			.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_SIGNATURE.getValue())).limit(1).fetchInto(APP_PARAM);
		Result<AppParamRecord> mode = ctx.getDslContext().select(APP_PARAM.VALUE).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
				.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_MODE.getValue())).limit(1).fetchInto(APP_PARAM);

		Result<AppParamRecord> logo = ctx.getDslContext().select(APP_PARAM.VALUE).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
				.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_LOGO.getValue())).limit(1).fetchInto(APP_PARAM);
					
		return new NotificationInfo()
				.setCommentsHistory(history.isNotEmpty() && history.get(0).getValue().length() == 2 
						? history.get(0).getValue().substring(0, 1).equals("1") : false)
				.setStatusHistory(history.isNotEmpty() && history.get(0).getValue().length() == 2
						? history.get(0).getValue().substring(1).equals("1") : false)
				.setMailAccount(mail.isNotEmpty() && mail.get(0).getValue() != null ? SecurityDAO.getMailAccount(ctx, f-> f.getIdProperty().eq(Integer.parseInt(mail.get(0).getValue()))) : null)
				
				.setNotifyOpen(auto.isNotEmpty() && auto.get(0).getValue().length() >= 4 
						? auto.get(0).getValue().substring(0, 1).equals("1") : false)
				.setNotifyClose(auto.isNotEmpty() && auto.get(0).getValue().length() >= 4
						? auto.get(0).getValue().substring(1, 2).equals("1") : false)
				.setNotifyReopen(auto.isNotEmpty() && auto.get(0).getValue().length() >= 4
						? auto.get(0).getValue().substring(2, 3).equals("1") : false)
				.setNotifyComment(auto.isNotEmpty() && auto.get(0).getValue().length() >= 4
						? auto.get(0).getValue().substring(3, 4).equals("1") : false)
				.setNotifyAssignee(auto.isNotEmpty() && auto.get(0).getValue().length() > 4
					? auto.get(0).getValue().substring(4).equals("1") : false)
				
				.setSignature(signature.isNotEmpty() && signature.get(0).getValue()!= null ? SecurityDAO.getSignature(ctx, Integer.parseInt(signature.get(0).getValue())) : null)
				.setBcc(bcc.isNotEmpty() ? bcc.get(0).getValue() : "")
				.setMode(mode.isNotEmpty() ?  Integer.parseInt(mode.get(0).getValue()): 1)
				.setIsLogo(logo.isNotEmpty() ? logo.get(0).getValue().substring(0,1).equals("1"): true)
				.setLogoPercentage(logo.isNotEmpty() && logo.get(0).getId() != null ? Integer.parseInt(logo.get(0).getValue().substring(1)): 20);
	}
	
	public static void insertNotificationInfo(AONContext ctx, String data, AppParam appParam){
		if(appParam.equals(AppParam.NOTICE_NOTIFICATION_AUTO)){
			Result<Record1<Integer>> auto = ctx.getDslContext().select(APP_PARAM.ID).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
					.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_AUTO.getValue())).limit(1).fetch();
			if(auto.isEmpty()) ctx.getDslContext().insertInto(APP_PARAM, APP_PARAM.DOMAIN, APP_PARAM.NAME, APP_PARAM.VALUE)
				.values(ctx.getDomainId(), AppParam.NOTICE_NOTIFICATION_AUTO.getValue(), data).execute();
			else ctx.getDslContext().update(APP_PARAM).set(APP_PARAM.VALUE, data)
				.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId())).and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_AUTO.getValue())).execute();
		} else if(appParam.equals(AppParam.NOTICE_NOTIFICATION_BCC)){
			Result<Record1<Integer>> bcc = ctx.getDslContext().select(APP_PARAM.ID).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
					.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_BCC.getValue())).limit(1).fetch();
			if(bcc.isEmpty()) ctx.getDslContext().insertInto(APP_PARAM, APP_PARAM.DOMAIN, APP_PARAM.NAME, APP_PARAM.VALUE)
				.values(ctx.getDomainId(), AppParam.NOTICE_NOTIFICATION_BCC.getValue(), data).execute();
			else ctx.getDslContext().update(APP_PARAM).set(APP_PARAM.VALUE, data)
				.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId())).and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_BCC.getValue())).execute();
		} else if(appParam.equals(AppParam.NOTICE_NOTIFICATION_HISTORY)){
			Result<Record1<Integer>> history = ctx.getDslContext().select(APP_PARAM.ID).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
					.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_HISTORY.getValue())).limit(1).fetch();
			if(history.isEmpty()) ctx.getDslContext().insertInto(APP_PARAM, APP_PARAM.DOMAIN, APP_PARAM.NAME, APP_PARAM.VALUE)
				.values(ctx.getDomainId(), AppParam.NOTICE_NOTIFICATION_HISTORY.getValue(), data).execute();
			else ctx.getDslContext().update(APP_PARAM).set(APP_PARAM.VALUE, data)
				.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId())).and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_HISTORY.getValue())).execute();
		} else if(appParam.equals(AppParam.NOTICE_NOTIFICATION_LOGO)){
			Result<Record1<Integer>> logo = ctx.getDslContext().select(APP_PARAM.ID).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
					.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_LOGO.getValue())).limit(1).fetch();
			if(logo.isEmpty()) ctx.getDslContext().insertInto(APP_PARAM, APP_PARAM.DOMAIN, APP_PARAM.NAME, APP_PARAM.VALUE)
				.values(ctx.getDomainId(), AppParam.NOTICE_NOTIFICATION_LOGO.getValue(),data).execute();
			else ctx.getDslContext().update(APP_PARAM).set(APP_PARAM.VALUE, data)
				.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId())).and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_LOGO.getValue())).execute();
		} else if(appParam.equals(AppParam.NOTICE_NOTIFICATION_MAIL)){
			Result<Record1<Integer>> mail = ctx.getDslContext().select(APP_PARAM.ID).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
					.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_MAIL.getValue())).limit(1).fetch();
			if(mail.isEmpty()) ctx.getDslContext().insertInto(APP_PARAM, APP_PARAM.DOMAIN, APP_PARAM.NAME, APP_PARAM.VALUE)
				.values(ctx.getDomainId(), AppParam.NOTICE_NOTIFICATION_MAIL.getValue(), data).execute();
			else ctx.getDslContext().update(APP_PARAM).set(APP_PARAM.VALUE, data)
				.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId())).and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_MAIL.getValue())).execute();	
		} else if(appParam.equals(AppParam.NOTICE_NOTIFICATION_MODE)){
			Result<Record1<Integer>> mode = ctx.getDslContext().select(APP_PARAM.ID).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
					.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_MODE.getValue())).limit(1).fetch();
			if(mode.isEmpty()) ctx.getDslContext().insertInto(APP_PARAM, APP_PARAM.DOMAIN, APP_PARAM.NAME, APP_PARAM.VALUE)
				.values(ctx.getDomainId(), AppParam.NOTICE_NOTIFICATION_MODE.getValue(), data).execute();
			else ctx.getDslContext().update(APP_PARAM).set(APP_PARAM.VALUE, data)
				.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId())).and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_MODE.getValue())).execute();
		} else if(appParam.equals(AppParam.NOTICE_NOTIFICATION_SIGNATURE)){
			Result<Record1<Integer>> sign = ctx.getDslContext().select(APP_PARAM.ID).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
					.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_SIGNATURE.getValue())).limit(1).fetch();
			if(sign.isEmpty()) ctx.getDslContext().insertInto(APP_PARAM, APP_PARAM.DOMAIN, APP_PARAM.NAME, APP_PARAM.VALUE)
				.values(ctx.getDomainId(), AppParam.NOTICE_NOTIFICATION_SIGNATURE.getValue(), data).execute();
			else ctx.getDslContext().update(APP_PARAM).set(APP_PARAM.VALUE, data)
				.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId())).and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_SIGNATURE.getValue())).execute();
		}
	}

	
	public static void insertNotificationInfo(AONContext ctx, NotificationInfo notificationInfo){
		Result<Record1<Integer>> auto = ctx.getDslContext().select(APP_PARAM.ID).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
		.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_AUTO.getValue())).limit(1).fetch();
		String open = notificationInfo.getNotifyOpen() ? "1" : "0";
		String close = notificationInfo.getNotifyClose() ? "1" : "0";
		String reopen = notificationInfo.getNotifyReopen() ? "1" : "0";
		String comment = notificationInfo.getNotifyComment() ? "1" : "0";
		if(auto.isEmpty()) ctx.getDslContext().insertInto(APP_PARAM, APP_PARAM.DOMAIN, APP_PARAM.NAME, APP_PARAM.VALUE)
			.values(ctx.getDomainId(), AppParam.NOTICE_NOTIFICATION_AUTO.getValue(), open+close+reopen+comment).execute();
		else ctx.getDslContext().update(APP_PARAM).set(APP_PARAM.VALUE, open+close+reopen+comment)
			.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId())).and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_AUTO.getValue())).execute();

		
		Result<Record1<Integer>> bcc = ctx.getDslContext().select(APP_PARAM.ID).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
				.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_BCC.getValue())).limit(1).fetch();
		if(bcc.isEmpty()) ctx.getDslContext().insertInto(APP_PARAM, APP_PARAM.DOMAIN, APP_PARAM.NAME, APP_PARAM.VALUE)
			.values(ctx.getDomainId(), AppParam.NOTICE_NOTIFICATION_BCC.getValue(), notificationInfo.getBcc()).execute();
		else ctx.getDslContext().update(APP_PARAM).set(APP_PARAM.VALUE, notificationInfo.getBcc())
			.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId())).and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_BCC.getValue())).execute();

			
		Result<Record1<Integer>> history = ctx.getDslContext().select(APP_PARAM.ID).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
				.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_HISTORY.getValue())).limit(1).fetch();
		String commentsHistory = notificationInfo.getCommentsHistory() ? "1" : "0";
		String statusHistory = notificationInfo.getStatusHistory() ? "1" : "0";
		if(history.isEmpty()) ctx.getDslContext().insertInto(APP_PARAM, APP_PARAM.DOMAIN, APP_PARAM.NAME, APP_PARAM.VALUE)
			.values(ctx.getDomainId(), AppParam.NOTICE_NOTIFICATION_HISTORY.getValue(), commentsHistory + statusHistory).execute();
		else ctx.getDslContext().update(APP_PARAM).set(APP_PARAM.VALUE, commentsHistory + statusHistory)
			.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId())).and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_HISTORY.getValue())).execute();
		
		Result<Record1<Integer>> mail = ctx.getDslContext().select(APP_PARAM.ID).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
				.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_MAIL.getValue())).limit(1).fetch();
		if(mail.isEmpty()) ctx.getDslContext().insertInto(APP_PARAM, APP_PARAM.DOMAIN, APP_PARAM.NAME, APP_PARAM.VALUE)
			.values(ctx.getDomainId(), AppParam.NOTICE_NOTIFICATION_MAIL.getValue(), notificationInfo.getMailAccount().getId() != null ?
					notificationInfo.getMailAccount().getId().toString(): null).execute();
		else ctx.getDslContext().update(APP_PARAM).set(APP_PARAM.VALUE,  notificationInfo.getMailAccount().getId() != null ?
				notificationInfo.getMailAccount().getId().toString(): null)
			.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId())).and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_MAIL.getValue())).execute();	
		
		Result<Record1<Integer>> sign = ctx.getDslContext().select(APP_PARAM.ID).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
				.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_SIGNATURE.getValue())).limit(1).fetch();
		if(sign.isEmpty()) ctx.getDslContext().insertInto(APP_PARAM, APP_PARAM.DOMAIN, APP_PARAM.NAME, APP_PARAM.VALUE)
			.values(ctx.getDomainId(), AppParam.NOTICE_NOTIFICATION_SIGNATURE.getValue(), notificationInfo.getSignature().getId() != null ? 
					notificationInfo.getSignature().getId().toString(): null).execute();
		else ctx.getDslContext().update(APP_PARAM).set(APP_PARAM.VALUE, notificationInfo.getSignature().getId() != null ?
				notificationInfo.getSignature().getId().toString(): null)
			.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId())).and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_SIGNATURE.getValue())).execute();

		Result<Record1<Integer>> mode = ctx.getDslContext().select(APP_PARAM.ID).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
				.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_MODE.getValue())).limit(1).fetch();
		if(mode.isEmpty()) ctx.getDslContext().insertInto(APP_PARAM, APP_PARAM.DOMAIN, APP_PARAM.NAME, APP_PARAM.VALUE)
			.values(ctx.getDomainId(), AppParam.NOTICE_NOTIFICATION_MODE.getValue(), notificationInfo.getMode() != null ? 
					notificationInfo.getMode().toString(): "1").execute();
		else ctx.getDslContext().update(APP_PARAM).set(APP_PARAM.VALUE, notificationInfo.getMode() != null ?
				notificationInfo.getMode().toString(): "1")
			.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId())).and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_MODE.getValue())).execute();

		Result<Record1<Integer>> logo = ctx.getDslContext().select(APP_PARAM.ID).from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
				.and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_LOGO.getValue())).limit(1).fetch();
		String val ="0";
		if(notificationInfo.getIsLogo()) val = "1"; 
		
		if(logo.isEmpty()) ctx.getDslContext().insertInto(APP_PARAM, APP_PARAM.DOMAIN, APP_PARAM.NAME, APP_PARAM.VALUE)
			.values(ctx.getDomainId(), AppParam.NOTICE_NOTIFICATION_LOGO.getValue(), val + notificationInfo.getLogoPercentage().toString()).execute();
		else ctx.getDslContext().update(APP_PARAM).set(APP_PARAM.VALUE, val + notificationInfo.getLogoPercentage().toString())
			.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId())).and(APP_PARAM.NAME.eq(AppParam.NOTICE_NOTIFICATION_LOGO.getValue())).execute();

	}
}
