package com.esferalia.aon.gwt.office.jooq;

import static com.esferalia.aon.jooq.tables.Notice.NOTICE;
import static com.esferalia.aon.jooq.tables.NoticeTag.NOTICE_TAG;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Tag.TAG;
import static com.esferalia.aon.jooq.tables.User.USER;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.exception.DataAccessException;

import com.esferalia.aon.jooq.tables.records.NoticeRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.UserRecord;

public class JooqAonHub {

	private static Settings SETTINGS = null;

	protected static List<NoticeRecord> getIssues(DSLContext dslContext,
			Integer domain) throws DataAccessException, Exception {

		return dslContext.selectFrom(NOTICE).where(NOTICE.DOMAIN.eq(domain))
				.fetchInto(NOTICE);
	}

	protected static List<UserRecord> getUsers(DSLContext dslContext,
			Integer domain) throws DataAccessException {

		return dslContext.selectFrom(USER).where(USER.DOMAIN.eq(domain))
				.fetchInto(USER);
	}

	protected static void saveNewNotice(DSLContext dslContext, Integer domain) {

	}

	protected static Result<Record> getIssueTags(DSLContext dslContext,
			Integer domain, Integer noticeId) throws DataAccessException,
			Exception {

		return dslContext
				.select()
				.from(NOTICE_TAG.rightOuterJoin(TAG).on(
						NOTICE_TAG.TAG.eq(TAG.ID)))
				.where(NOTICE_TAG.NOTICE.eq(noticeId)
						.and(TAG.DOMAIN.eq(domain))).fetch();
	}

	protected static UserRecord getSender(DSLContext dslContext,
			Integer domain, Integer userId) throws DataAccessException {
		// Remitente del aviso. Entiendo que es un usuario único.
		return dslContext.selectFrom(USER)
				.where(USER.ID.eq(userId).and(USER.DOMAIN.eq(domain)))
				.fetchOne();
	}

	protected static UserRecord getUserAssignee(DSLContext dslContext,
			Integer domain, Integer userId) throws DataAccessException {
		// Destinatario del aviso. Entiendo que es un usuario único.
		return getSender(dslContext, domain, userId);
	}

	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
}
