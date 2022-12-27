package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.User.USER;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.jooq.tables.records.AppParamRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.office.NotificationInfo;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.watson.server.AonEnumUtils;

@Deprecated
public class AonHubDAO {

	@Deprecated
	private static class MinimalUserFiller implements Function<Record, User> {
		@Override
		public User apply(Record record) {
			User user = new User();
			user.setId(record.getValue(USER.ID));
			user.setDomain(new Domain().setId(record.getValue(USER.DOMAIN)));
			user.setName(record.getValue(USER.NAME));
			user.setLogin(record.getValue(USER.LOGIN));
			user.setActive(
					AonEnumUtils.getBoolean(record.getValue(USER.ACTIVE)));
			return user;
		}
	}

	@Deprecated
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

	@Deprecated
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
	
	@Deprecated
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

	@Deprecated
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
