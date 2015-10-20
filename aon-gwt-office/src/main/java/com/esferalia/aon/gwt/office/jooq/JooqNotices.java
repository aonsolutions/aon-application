package com.esferalia.aon.gwt.office.jooq;

import java.sql.Connection;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.office.shared.Notice;

public class JooqNotices {

	private static Settings SETTINGS = null;

	public static List<Notice> getNotices(Connection conn,
			Integer parentDomain, Integer domain)
			throws IllegalArgumentException {

		return getNotices(DSL.using(conn, getDefaultSettings()), parentDomain,
				domain);
	}

	private static List<Notice> getNotices(DSLContext dslContext,
			Integer parentDomain, Integer domain)
			throws IllegalArgumentException {
		return null;
	}

	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}

}
