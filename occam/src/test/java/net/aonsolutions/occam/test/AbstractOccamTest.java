package net.aonsolutions.occam.test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import net.aonsolutions.core.pool.AonConnectionException;
import net.aonsolutions.occam.api.AONContext;
import net.aonsolutions.occam.api.Occam;
import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.watson.server.AonObjectUtils;

public abstract class AbstractOccamTest {

	protected static AONContext ctx;
	protected static Integer DOMAIN_ID;

	protected static String DOMAIN_NAME = System.getProperty("domainName", "occam.aonsolutions.test");
	protected static String USER 		= System.getProperty("domainUser", "admin");
	private static String getDbPort() {		return System.getProperty("dbPort", "3306");	}
	private static String getDbHost() {		return System.getProperty("dbHost", "127.0.0.1");	}
	private static String getDbName() {		return System.getProperty("dbName", "aon_jooq_");	}
	private static String getDbUser() {		return System.getProperty("dbUser", "dbuser");	}
	private static String getDbPasswd() {	return System.getProperty("dbPasswd", "serubd2000");	}
	private static String getDbUseSSL() {	return System.getProperty("dbUseSSL", "false");	}
	private static String getDbTimeZone() {	return System.getProperty("dbTimeZone", TimeZone.getDefault().getID());	}
	
	private Date testDate; // AonDateUtils.getDate(2022, 11, 15);
	
	protected static Occam getOccam() {
		return new Occam()
			.setDomainName(DOMAIN_NAME)
			.setUser(USER);
	}
	
	@BeforeAll	
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		shutUp();
		if ( DOMAIN_ID == null) {
			AONTestContext context = new AONTestContext(connect(), DOMAIN_NAME, USER);
			Domain domain = DomainProvider.getOrCreateDomain(context, DOMAIN_NAME, USER);
			DOMAIN_ID = domain.getId();
		}
		ctx = AONContext.getAONContext(getOccam());
		System.setOut(System.out);
		System.setErr(System.err);
	}

	@AfterAll
	public static void afterClass() {
		if (ctx != null) ctx.close();
	}
	
	private static void shutUp() {
		if (mustShutUp()) {
			PrintStream devnull = new PrintStream(new OutputStream() {
				@Override
				public void write(int b) throws IOException { /* Nothing */ }
			});
			System.setOut(devnull);
			System.setErr(devnull);
		}
	}
	
	protected static boolean mustShutUp() {
		String mustShutUp = System.getProperty("mustShutUp", "true");
		return "false".equalsIgnoreCase(mustShutUp);
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
				connection.createStatement().execute("use `" + schemaName + "`");
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
	
	protected void setLoggerLevel(Level targetLevel) {
      Logger root = Logger.getLogger("");
      root.setLevel(targetLevel);
      for (Handler handler : root.getHandlers()) {
          handler.setLevel(targetLevel);
      }
      System.out.println("Level set: " + targetLevel.getName());
  }
}
