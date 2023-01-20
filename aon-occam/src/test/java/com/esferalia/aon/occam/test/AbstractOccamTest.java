package com.esferalia.aon.occam.test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonObjectUtils;
import com.mysql.jdbc.Driver;

import net.aonsolutions.core.pool.AonConnectionException;

public abstract class AbstractOccamTest {

	protected static CloseableAONContext ctx;
	private static AonConfiguration config;
	protected static Integer DOMAIN_ID;

	protected static String DOMAIN_NAME = System.getProperty("domainName", "occamtest.aonsolutions.test");	
	protected static String USER 		= System.getProperty("domainUser", "admin");
	private static String getDbPort() {		return System.getProperty("dbPort", "3306");	}
	private static String getDbHost() {		return System.getProperty("dbHost", "127.0.0.1");	}
	private static String getDbName() {		return System.getProperty("dbName", "aon_jooq_");	}
	private static String getDbUser() {		return System.getProperty("dbUser", "dbuser");	}
	private static String getDbPasswd() {	return System.getProperty("dbPasswd", "serubd2000");	}
	private static String getDbUseSSL() {	return System.getProperty("dbUseSSL", "false");	}
	private static String getDbTimeZone() {	return System.getProperty("dbTimeZone", TimeZone.getDefault().getID());	}
	
	private Date testDate; // AonDateUtils.getDate(2022, 11, 15);
	
	@Rule
	public RepeatRule repeatRule = new RepeatRule();

	protected static Occam getOccam() {
		return new Occam()
				.setDomainName(DOMAIN_NAME)
				.setDomain(DOMAIN_ID)
				.setUser(USER);
	}
	
	protected static AonConfiguration getConfiguration() {
		if (config == null) {
			config = AON.getConfiguration(ctx);
		}
		return config;
	}
	
	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		shutUp();
		if ( DOMAIN_ID == null) {
			AONContext context = new AONContext(connect());
			Domain domain = DomainProviderForTests.getOrCreateDomain(context, DOMAIN_NAME, USER);
			DOMAIN_ID = domain.getId();
		}
		ctx = AONContext.getAONContext(getOccam());
		System.setOut(System.out);
		System.setErr(System.err);
	}

	@AfterClass
	public static void afterClass() {
		if (ctx != null) ctx.close();
	}
	
	@Before
	public void beforeTest() {
		System.out.println( "Running [" + this.getClass().getSimpleName() + "]");
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
		String mustShutUp = System.getProperty("mustShutUp", "false");
		return "true".equalsIgnoreCase(mustShutUp);
	}

	
	private static Connection connect() throws ClassNotFoundException, SQLException  {
		Class.forName(Driver.class.getName());

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
				String schemaName = rs.getString(1); 
				connection.createStatement().execute("use " + schemaName);
				System.out.println("*******************************");
				System.out.println("USING  [" + schemaName+ "] schema.");
				System.out.println("*******************************");
				return connection;
			}
		}
		return connection;
	}
	
	protected Date getTestDate() {
		return AonObjectUtils.defaultIfNull(testDate, new Date());
	}
	
}
