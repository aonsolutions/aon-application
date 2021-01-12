package com.esferalia.aon.occam.jooq.test;

import static com.esferalia.aon.jooq.tables.Company.COMPANY;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.DomainApplication.DOMAIN_APPLICATION;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.TimeZone;

import org.jooq.Record;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.esferalia.aon.jooq.tables.User;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.watson.error.AonCoreException;

import net.aonsolutions.core.pool.AonConnectionException;

public class AbstractOccamTest {

	protected static AONContext ctx;
	protected static String DOMAIN_NAME = "occamTest.aonsolutions.test";
	protected static Integer DOMAIN_ID;
	protected static String USER = "admin";
	
	private static String getDbPort() {		return System.getProperty("dbPort", "3306");	}
	private static String getDbHost() {		return System.getProperty("dbHost", "127.0.0.1");	}
	private static String getDbName() {		return System.getProperty("dbName", "aon_jooq_");	}
	private static String getDbUser() {		return System.getProperty("dbUser", "dbuser");	}
	private static String getDbPasswd() {	return System.getProperty("dbPasswd", "serubd2000");	}
	private static String getDbUseSSL() {	return System.getProperty("dbUseSSL", "false");	}
	private static String getDbTimeZone() {	return System.getProperty("dbTimeZone", TimeZone.getDefault().getID());	}

	
	
	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		shutUp();
		if ( DOMAIN_ID == null) {
			createDomain();
		}
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID,USER);
		System.setOut(System.out);
		System.setErr(System.err);
	}

	@AfterClass
	public static void afterClass() {
		if (ctx != null) ctx.finalize();
	}

	private static void shutUp() {
		if (mustShutUp()) {
			PrintStream devnull = new PrintStream(new OutputStream() {
				@Override
				public void write(int b) throws IOException {
					// TODO Auto-generated method stub
				}
			});
			System.setOut(devnull);
			System.setErr(devnull);
		}
	}
	
	protected static boolean mustShutUp() {
		String mustShutUp = System.getProperty("mustShutUp", "true");
		return "true".equalsIgnoreCase(mustShutUp);
	}

	
	private static Connection connect() throws ClassNotFoundException, SQLException  {
		Class.forName(org.mariadb.jdbc.Driver.class.getName());

		String dbHost = getDbHost();
		String dbPort = getDbPort();
		String dbName = getDbName();
		String dbUser = getDbUser();
		String dbPasswd = getDbPasswd();
		String dbUseSSL = getDbUseSSL();
		String dbTimeZone = getDbTimeZone();

		Properties properties = new Properties();
		properties.setProperty("user", dbUser);
		properties.setProperty("password", dbPasswd);
		properties.setProperty("useSSL", dbUseSSL);
		properties.setProperty("serverTimezone", dbTimeZone);
		String url = String.format("jdbc:mysql://%s:%s", dbHost, dbPort, dbName);
		Connection connection = DriverManager.getConnection(url, properties);

		ResultSet rs = connection.createStatement().executeQuery("SHOW DATABASES");
		while (rs.next()) {
			if (rs.getString(1).startsWith(dbName)) {
				connection.createStatement().execute("use " + rs.getString(1));
				return connection;
			}
		}
		return connection;
	}
	
	private static void createDomain() {
		AONContext context = null;
		try {
			context = new AONContext(connect());
			AONContext contextBis = context;
			Domain domain = context.getDslContext().transactionResult(configuration -> insertDomain(contextBis, "11111111H", DOMAIN_NAME)); 
			DOMAIN_ID = domain.getId(); 
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			if (context != null) {
				context.finalize();
			}
		}
	}
	
	public static Domain insertDomain(AONContext ctx, String document, String name) throws AonCoreException {
		Domain domain = null;
		Record domainRecord = ctx.getDslContext()
				.select()
				.from(DOMAIN)
				.where(DOMAIN.NAME.eq(name))
				.fetchAny();

		if (domainRecord != null) {
			domain = new Domain();
			domain.setId(domainRecord.get(DOMAIN.ID));
			domain.setName(domainRecord.get(DOMAIN.NAME));
			return domain;
		}
		// DOMAIN
		int newDomainId = ctx
				.getDslContext()
				.insertInto(DOMAIN)
				.set(DOMAIN.CREATION_USER, USER)
				.set(DOMAIN.CREATION_DATE, new java.sql.Timestamp(System.currentTimeMillis()))
				.set(DOMAIN.DOMAINMANAGEMENT, (byte) 0)
				.set(DOMAIN.TYPE, (byte) 0)
				.set(DOMAIN.OWNER, USER )
				.set(DOMAIN.NAME, name )
				.set(DOMAIN.DESCRIPTION, name )
				.set(DOMAIN.ENABLEHEREDITY, (byte) 1)
				.set(DOMAIN.MAXDEFINEDUSERS, 0).set(DOMAIN.MAXDOCUMENTSIZE, 1)
				.set(DOMAIN.MAXTOTALDOCUMENTSIZE, 16).returning(DOMAIN.ID)
				.fetchOne().getId();
		ctx.log().info("Dominio " + name + " insertado correctamente");

		ctx.getDslContext().insertInto(DOMAIN_APPLICATION)
				.set(DOMAIN_APPLICATION.DOMAIN, newDomainId)
				.set(DOMAIN_APPLICATION.APPLICATION, 28)
				.set(DOMAIN_APPLICATION.ACTIVE, (byte) 1)
				.set(DOMAIN_APPLICATION.AUDIT_LEVEL, (byte) 0).execute();
		ctx.log().info("Aplicacion de dominio insertada correctamente");

		int newRegistryId = ctx.getDslContext().insertInto(REGISTRY)
				.set(REGISTRY.DOMAIN, newDomainId)
				.set(REGISTRY.DOCUMENT, document)
				.set(REGISTRY.DOCUMENT_TYPE, (byte) 0)
				.set(REGISTRY.NAME, name)
				.set(REGISTRY.TYPE, (byte) 1)
				.returning(REGISTRY.ID).fetchOne()
				.getId();
		ctx.log().info("Registry insertado correctamente");

		ctx.getDslContext().insertInto(COMPANY)
				.set(COMPANY.REGISTRY, newRegistryId)
				.set(COMPANY.DOMAIN, newDomainId).execute();
		ctx.log().info("Company insertada correctamente");
		
		int newScopeId = ctx.getDslContext().insertInto(SCOPE)
				.set(SCOPE.DESCRIPTION, "DEFAULT")
				.set(SCOPE.DOMAIN , newDomainId)
				.returning(SCOPE.ID).fetchOne()
				.getId();
		ctx.log().info("Scope insertado correctamente");

		User USERDB = com.esferalia.aon.jooq.tables.User.USER;
		int newUserId = ctx.getDslContext().insertInto(USERDB)
				.set(USERDB.DOMAIN , newDomainId)
				.set(USERDB.NAME, "DEFAULT USER")
				.set(USERDB.LOGIN, USER)
				.returning(USERDB.ID)
				.fetchOne()
				.getId();
		ctx.log().info("User insertado correctamente");

		ctx.getDslContext().insertInto(USER_SCOPE)
				.set(USER_SCOPE.USER_ID, newUserId)
				.set(USER_SCOPE.DOMAIN , newDomainId)
				.set(USER_SCOPE.SCOPE, newScopeId)
				.execute();
		ctx.log().info("User Scope insertado correctamente");

		ctx.getDslContext()
				.insertInto(ENTERPRISE)
				.set(ENTERPRISE.REGISTRY, newRegistryId)
				.set(ENTERPRISE.DOMAIN, newDomainId)
				.set(ENTERPRISE.SCOPE, newScopeId)
				.execute();
		ctx.log().info("Enterprise insertada correctamente");

		domain = new Domain();
		domain.setId(newDomainId);
		domain.setName(name);

		return domain;
	}
	
	
	
	
	
}
