package com.esferalia.aon.gwt.office.jooq;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Notice.NOTICE;
import static com.esferalia.aon.jooq.tables.NoticeTag.NOTICE_TAG;
import static com.esferalia.aon.jooq.tables.Tag.TAG;
import static com.esferalia.aon.jooq.tables.User.USER;

import java.sql.Connection;
import java.util.Iterator;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.NoticeRecord;
import com.esferalia.aon.jooq.tables.records.NoticeTagRecord;
import com.esferalia.aon.jooq.tables.records.TagRecord;

public class JooqNotices {

	private static Settings SETTINGS = null;

	public static String getIssues(Connection conn, Integer parentDomain,
			Integer domain) {
		return getIssues(DSL.using(conn, getDefaultSettings()), parentDomain,
				domain);
	}

	private static String getIssues(DSLContext dslContext,
			Integer parentDomain, Integer domain) {

		try {
			List<NoticeRecord> result = dslContext
					.select()
					.from(NOTICE)
					.where(NOTICE.DOMAIN.eq(domain).or(
							NOTICE.DOMAIN.eq(parentDomain))).fetchInto(NOTICE);

			StringBuffer buffer = new StringBuffer();

			buffer.append('[');

			Iterator<NoticeRecord> iterator = result.iterator();
			while (iterator.hasNext()) {
				NoticeRecord record = iterator.next();
				
				Integer id = record.getValue(NOTICE.ID);
				Integer userId = record.getValue(NOTICE.SENDER);
				
				buffer.append('{');
				buffer.append(String.format("\"id\":\"%s\",",
						String.valueOf(id)));
				buffer.append(String.format("\"title\":\"%s\",",
						record.getValue(NOTICE.SUBJECT)));
				buffer.append(String.format("\"body\":\"%s\",",
						record.getValue(NOTICE.SUBJECT)));				
				buffer.append(String.format("\"user\":%s",
						getUser(dslContext, userId)));
				buffer.append(String.format("\"labels\":%s",
						getNoticeTags(dslContext, domain, id)));
				
				
				buffer.append('}');
				if (iterator.hasNext())
					buffer.append(',');

				System.out.println(buffer.toString());
			}

			buffer.append(']');
			return buffer.toString();

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new IllegalArgumentException(ex);
		}
	}

	private static String getUser(DSLContext dslContext, Integer userId) {

		try {

			Record result = dslContext
					.select()
					.from(USER.rightOuterJoin(DOMAIN).on(
							USER.DOMAIN.eq(DOMAIN.ID)))
					.where(USER.ID.eq(userId)).fetchOne();

			StringBuffer buffer = new StringBuffer();
			buffer.append('{');
			if (result != null) {
				buffer.append(String.format("\"login\":\"%s\",",
						String.valueOf(result.getValue(USER.LOGIN))));
				buffer.append(String.format("\"id\":\"%s\",",
						result.getValue(USER.ID)));
				buffer.append(String.format("\"name\":\"%s\",",
						result.getValue(USER.NAME)));
				buffer.append(String.format("\"company\":\"%s\"",
						result.getValue(DOMAIN.DESCRIPTION)));
			}
			buffer.append("},");
			
			return buffer.toString();

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new IllegalArgumentException(ex);
		}
	}

	private static String getLabels(DSLContext dslContext, Integer domain,
			Integer parentDomain) {

		try {
			List<TagRecord> result = dslContext
					.selectFrom(TAG)
					.where(TAG.DOMAIN.eq(domain)
							.or(TAG.DOMAIN.eq(parentDomain))).fetchInto(TAG);

			StringBuffer buffer = new StringBuffer();

			Iterator<TagRecord> iterator = result.iterator();

			buffer.append('[');

			while (iterator.hasNext()) {
				TagRecord tag = iterator.next();
				buffer.append('{');
				buffer.append(String.format("\"id\":\"%s\",",
						String.valueOf(tag.getValue(TAG.ID))));
				buffer.append(String.format("\"domain\":\"%s\",",
						tag.getValue(TAG.DOMAIN)));
				buffer.append(String.format("\"name\":\"%s\"",
						tag.getValue(TAG.NAME)));
				buffer.append(String.format("\"type\":\"%s\"",
						tag.getValue(TAG.TYPE)));
				buffer.append(String.format("\"color\":\"%s\"",
						tag.getValue(TAG.COLOR)));

				buffer.append("}");
				if (iterator.hasNext())
					buffer.append(',');
			}

			buffer.append(']');

			return buffer.toString();

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new IllegalArgumentException(ex);
		}
	}

	private static String getNoticeTags(DSLContext dslContext, Integer domain,
			Integer noticeId) {

		try {

			List<NoticeTagRecord> result = dslContext.selectFrom(NOTICE_TAG)
					.where(NOTICE_TAG.NOTICE.eq(noticeId))
					.fetchInto(NOTICE_TAG);
			
			StringBuffer buffer = new StringBuffer();
			buffer.append('[');
			Iterator<NoticeTagRecord> iterator = result.iterator();
			while (iterator.hasNext()) {
				NoticeTagRecord tag = iterator.next();
				buffer.append('{');
				buffer.append(String.format("\"id\":\"%s\",",
						String.valueOf(tag.getValue(NOTICE_TAG.ID))));
				buffer.append(String.format("\"notice\":\"%s\",",
						tag.getValue(NOTICE_TAG.NOTICE)));
				buffer.append(String.format("\"tag\":\"%s\"",
						tag.getValue(NOTICE_TAG.TAG)));
				buffer.append(String.format("\"startDate\":\"%s\",",
						tag.getValue(NOTICE_TAG.START_DATE)));
				buffer.append(String.format("\"color\":\"%s\"",
						tag.getValue(NOTICE_TAG.END_DATE)));
				buffer.append('}');
				if (iterator.hasNext())
					buffer.append(',');
			}
			
			buffer.append(']');
			return buffer.toString();

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new IllegalArgumentException(ex);
		}
	}

	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}

}
