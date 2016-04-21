package com.esferalia.aon.occam.api.model.office;

import static com.esferalia.aon.jooq.tables.Notice.NOTICE;
import static com.esferalia.aon.jooq.tables.NoticeTag.NOTICE_TAG;
import static com.esferalia.aon.jooq.tables.Tag.TAG;

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
		conditions.add(NOTICE.TYPE.eq(NoticeType.TICKET.value()));
		
		if ( filter.isOpened() ) {
			conditions.add(
			// @formatted:off
			NOTICE.ID.in(ctx.getDslContext()
					.select(NOTICE_TAG.NOTICE)
					.from(NOTICE_TAG)
					.join(TAG)
					.on(NOTICE_TAG.TAG.eq(TAG.ID))
					.where((TAG.NAME.eq(NoticeStatus.OPEN.getValue()))
							.or(TAG.NAME.eq(NoticeStatus.REOPEN.getValue()))
							.and(NOTICE_TAG.END_DATE.isNull())
						  ))			
			// @formatted:on
			);
		}
		
		else if ( filter.isClosed() ) {
			conditions.add(
			// @formatted:off
			NOTICE.ID.in(ctx.getDslContext()
					.select(NOTICE_TAG.NOTICE)
					.from(NOTICE_TAG)
					.join(TAG)
					.on(NOTICE_TAG.TAG.eq(TAG.ID))
					.where(TAG.NAME.eq(NoticeStatus.CLOSED.getValue())							
							.and(NOTICE_TAG.END_DATE.isNull())
						  ))			
			// @formatted:on
			);
		}
		
		else if ( filter.isAll() ) {
			conditions.add(
			// @formatted:off
			NOTICE.ID.in(ctx.getDslContext()
					.select(NOTICE_TAG.NOTICE)
					.from(NOTICE_TAG)
					.join(TAG)
					.on(NOTICE_TAG.TAG.eq(TAG.ID))
					.where((TAG.NAME.eq(NoticeStatus.OPEN.getValue()))
								.or(TAG.NAME.eq(NoticeStatus.REOPEN.getValue()))
								.or(TAG.NAME.eq(NoticeStatus.CLOSED.getValue()))
							.and(NOTICE_TAG.END_DATE.isNull())
						  ))			
			// @formatted:on
			);
		}
		
		else if ( filter.isFaq() ) {
			conditions.add(
			// @formatted:off
			NOTICE.ID.in(ctx.getDslContext()
					.select(NOTICE_TAG.NOTICE)
					.from(NOTICE_TAG)
					.join(TAG)
					.on(NOTICE_TAG.TAG.eq(TAG.ID))
					.where(TAG.NAME.eq(NoticeStatus.FAQ.getValue())							
							.and(NOTICE_TAG.END_DATE.isNull())
						  ))			
			// @formatted:on
			);
		}
		
		else if ( filter.isDuplicated()) {
			conditions.add(
			// @formatted:off
			NOTICE.ID.in(ctx.getDslContext()
					.select(NOTICE_TAG.NOTICE)
					.from(NOTICE_TAG)
					.join(TAG)
					.on(NOTICE_TAG.TAG.eq(TAG.ID))
					.where(TAG.NAME.eq(NoticeStatus.DUPLICATED.getValue())							
							.and(NOTICE_TAG.END_DATE.isNull())
						  ))			
			// @formatted:on
			);
		}
		
		if (AonStringUtils.isNotEmpty(filter.getComany())){
			conditions.add(Notice.NOTICE.COMPANY.eq(filter.getComany()));
		}
		
		if (AonStringUtils.isNotEmpty(filter.getSubject()))
			conditions.add(NOTICE.SUBJECT.like("%" + filter.getSubject() + "%"));		
		
		if (filter.getSince() != null) 
			conditions.add(NOTICE.DATE.ge( new java.sql.Timestamp(filter.getSince().getTime())));
		
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
