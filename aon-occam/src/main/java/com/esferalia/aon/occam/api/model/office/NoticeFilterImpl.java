package com.esferalia.aon.occam.api.model.office;

import static com.esferalia.aon.jooq.tables.Notice.NOTICE;
import static com.esferalia.aon.jooq.tables.NoticeTag.NOTICE_TAG;
import static com.esferalia.aon.jooq.tables.Tag.TAG;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.jooq.Condition;

import com.esferalia.aon.jooq.tables.Notice;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.type.NoticeStatus;
import com.esferalia.aon.occam.api.model.type.NoticeType;
import com.esferalia.aon.watson.util.AonStringUtils;

public class NoticeFilterImpl  {
	
	public static List<Condition> getNoticeConditions(AONContext ctx, NoticeFilter filter) {
		
		List<Condition> conditions = new LinkedList<Condition>();
		
		conditions.add(NOTICE.DOMAIN.eq(ctx.getDomainId()));
		conditions.add(NOTICE.NOTICE_.isNull());
		
		evalStatusFilter(conditions, filter);
		
		if (AonStringUtils.isNotEmpty(filter.getComany())){
			conditions.add(Notice.NOTICE.COMPANY.eq(filter.getComany()));
		}
		
		if (AonStringUtils.isNotEmpty(filter.getSubject()))
			conditions.add(NOTICE.SUBJECT.like("%" + filter.getSubject() + "%"));		
		
		if (filter.getSince() != null) {
			Timestamp today = new java.sql.Timestamp(new Date().getTime());
			Timestamp since = new java.sql.Timestamp(filter.getSince().getTime());
			conditions.add(NOTICE.DATE.between(since, today));			
		}
		
		if (filter.getUser() != -1)
			conditions.add(NOTICE.SENDER.eq(filter.getUser()));
		
		Condition condition = null;
	
		for ( int x = 0; x < filter.getTags().length; x++) {			
			String labelName = filter.getTags()[x];			
			if ( condition == null ) {
				condition = builLabelCondition(ctx, labelName);			
				continue;
			}
			
			condition = condition.and(builLabelCondition(ctx, labelName));
		}
		
		if (condition != null)
			conditions.add(condition);
		
		return conditions;
	}
	
	private static void evalStatusFilter(List<Condition> conditions, NoticeFilter filter) {
		
		if ( filter.isOpened() ) {
			conditions.add(NOTICE.TYPE.eq(NoticeType.TICKET.value())
					.or(NOTICE.TYPE.eq(NoticeType.DUPLICATED.value())));
			conditions.add(NOTICE.STATUS.eq(NoticeStatus.OPEN.value())
					.or(NOTICE.STATUS.eq(NoticeStatus.REOPEN.value())));
		}
		
		else if ( filter.isClosed() ) {
			conditions.add(NOTICE.TYPE.eq(NoticeType.TICKET.value()));
			conditions.add(NOTICE.STATUS.eq(NoticeStatus.CLOSED.value()));
		}
		
		else if ( filter.isAll() ) {
			conditions.add(NOTICE.TYPE.eq(NoticeType.TICKET.value())
					.or(NOTICE.TYPE.eq(NoticeType.DUPLICATED.value())));
			conditions.add(NOTICE.STATUS.eq(NoticeStatus.OPEN.value())
					.or(NOTICE.STATUS.eq(NoticeStatus.REOPEN.value()))
					.or(NOTICE.STATUS.eq(NoticeStatus.CLOSED.value())));
		}
		
		else if ( filter.isFaq() ) {
			conditions.add(NOTICE.TYPE.eq(NoticeType.FAQ.value()));
			conditions.add(NOTICE.STATUS.eq(NoticeStatus.OPEN.value()));
		}
	}
	
	private static Condition builLabelCondition(AONContext ctx, String labelName) {
		
		return NOTICE.ID.in(
				ctx.getDslContext()
				.select(NOTICE_TAG.NOTICE)
				.from(NOTICE_TAG)
				.join(TAG)
				.on(NOTICE_TAG.TAG.eq(TAG.ID))
				.where(TAG.NAME.eq(labelName)
						.and(TAG.DOMAIN.eq(ctx.getDomainId()))
					  ));
	}
}
