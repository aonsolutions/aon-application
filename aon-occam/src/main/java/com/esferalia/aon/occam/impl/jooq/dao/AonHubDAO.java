package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Notice.NOTICE;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.jooq.tables.Workgroup.WORKGROUP;

import java.util.List;

import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;

import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.NoticeRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.WorkgroupRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.office.Notice;

public class AonHubDAO {

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
				.set(NOTICE.STATUS, notice.getStatus().byteValue())
				.set(NOTICE.SENDER, notice.getSender())
				.set(NOTICE.TYPE, notice.getType().byteValue())
				.set(NOTICE.PRIORITY, notice.getPriority().byteValue());
				
				if (notice.getRecipient() != null)
					noticeRecord = noticeRecord.set(NOTICE.RECIPIENT, notice.getRecipient());
				if (notice.getWorkgroup() != null)
					noticeRecord = noticeRecord.set(NOTICE.WORK_GROUP, notice.getWorkgroup());
				
				NoticeRecord newNoticeRecord = noticeRecord.returning().fetchOne();

		// BODY NOTICE
		ctx.getDslContext()
				.insertInto(NOTICE)
				.set(NOTICE.DOMAIN, newNoticeRecord.getValue(NOTICE.DOMAIN))
				.set(NOTICE.SENDER, newNoticeRecord.getValue(NOTICE.SENDER))
				.set(NOTICE.SUBJECT, newNoticeRecord.getValue(NOTICE.SUBJECT))
				.set(NOTICE.RECIPIENT, newNoticeRecord.getValue(NOTICE.RECIPIENT))
				.set(NOTICE.PHONE, newNoticeRecord.getValue(NOTICE.PHONE))
				.set(NOTICE.COMPANY, newNoticeRecord.getValue(NOTICE.COMPANY))
				.set(NOTICE.STATUS, newNoticeRecord.getValue(NOTICE.STATUS))
				.set(NOTICE.TYPE, newNoticeRecord.getValue(NOTICE.TYPE))
				.set(NOTICE.PRIORITY, newNoticeRecord.getValue(NOTICE.PRIORITY))
				.set(NOTICE.WORK_GROUP, newNoticeRecord.getValue(NOTICE.WORK_GROUP))
				.set(NOTICE.DATE, newNoticeRecord.getValue(NOTICE.DATE))
				.set(NOTICE.NOTICE_, newNoticeRecord.getValue(NOTICE.ID)).execute();
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

	public static DomainRecord getParentDomain(AONContext ctx, Integer domain) {
		return ctx.getDslContext().selectFrom(DOMAIN)
				.where(DOMAIN.ID.eq(domain)).fetchOne();
	}

}
