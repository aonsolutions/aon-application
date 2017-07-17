package com.esferalia.aon.dsi.util;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Identity;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.code.aon.config.enumeration.DomainType;
import net.aonsolutions.core.dbutils.AonSQLException;
import com.code.aon.master.VersionManager;
import com.esferalia.aon.jooq.tables.Domain;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.hxtt.sql.paradox.ParadoxDriver;

public class DBUtils {

	private static final Map<Identity<?, Integer>, Integer> NEXTS = new Hashtable<Identity<?, Integer>, Integer>();

	public static Integer next(DSLContext ctx, Identity<?, Integer> identity) {
		Integer t = NEXTS.get(identity);
		if (t != null) {
			t += 1;
			NEXTS.put(identity, t);
			return t;
		}

		//@formatter:off
		Field<Integer> next = DSL.coalesce(
				DSL.max(identity.getField()), 
				DSL.field(String.valueOf(0))).plus(1);
		t =  ctx.select(next)
				.from(identity.getTable())
				.fetchOne(next);
		//@formatter:on
		NEXTS.put(identity, t);
		return t;
	}

	public static Connection getDsiConnection(String url) throws SQLException {
		Properties info = new Properties();

		Driver driver = new ParadoxDriver();
		return driver.connect(url, info);
	}

	public static Connection getAonConnection(String url, String user,
			String password) throws SQLException {

		Properties info = new Properties();
		if (user != null)
			info.put("user", user);
		if (password != null)
			info.put("password", password);

		Driver driver = new com.mysql.jdbc.Driver();

		return driver.connect(url, info);

	}

	public static void dropDatabase(Connection conn, String dbName)
			throws SQLException {
		conn.createStatement().execute(
				String.format("DROP DATABASE `%s`", dbName));
	}

	public static void uptodateDatabase(Connection conn) throws AonSQLException {
		VersionManager manager = new VersionManager();
		manager.uptodateDatabase(conn);
	}

	public static void createDatabase(Connection conn, String dbName)
			throws AonSQLException {
		VersionManager manager = new VersionManager();
		manager.createDatabase(conn, dbName);
	}

	public static int createParentDomain(Connection conn, String name,
			String description, String owner) throws AonSQLException {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		DSLContext create = DSL.using(conn, SQLDialect.MARIADB, settings);

		//@formatter:off
		DomainRecord record = 
			create.insertInto(DOMAIN)
			.set(DOMAIN.NAME, name)
			.set(DOMAIN.OWNER, owner)
			.set(DOMAIN.ACTIVE, (byte)1)
			.set(DOMAIN.SUBDOMAINSUFFIX, name)
			.set(DOMAIN.ENABLEHEREDITY, (byte)1)
			.set(DOMAIN.DISABLEDOMAINMANAGEMENT, (byte)0)
			.set(DOMAIN.DESCRIPTION, description)
			.set(DOMAIN.TYPE, EnumUtils.enum2Byte(DomainType.CONSULTANCY))
			.set(DOMAIN.CREATION_DATE, new java.sql.Timestamp(System.currentTimeMillis()))
			.returning(DOMAIN.ID)
			.fetchOne();
		//@formatter:on

		int domainId = record.getId();

		insertDefaults4ParentDomain(conn, domainId);

		return domainId;
	}

	public static void insertDefaults4ParentDomain(Connection conn, int domain)
			throws AonSQLException {

		VersionManager manager = new VersionManager();
		try {
			conn.createStatement().execute(
					String.format("SET @Domain=%d;", domain));
			manager.insertApplicationDefaults(conn, null, "aon.domain");
		} catch (SQLException e) {
			throw new AonSQLException(e);
		}

	}

	public static void insertDefaults4ChildDomain(Connection conn, int domain)
			throws AonSQLException {

		VersionManager manager = new VersionManager();
		try {
			conn.createStatement().execute(
					String.format("SET @Domain=%d;", domain));
			manager.insertApplicationDefaults(conn, null,
					"aon.domain.from.parent");
		} catch (SQLException e) {
			throw new AonSQLException(e);
		}

	}

}
