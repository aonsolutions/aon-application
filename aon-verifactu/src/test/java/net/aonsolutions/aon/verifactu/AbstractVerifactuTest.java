package net.aonsolutions.aon.verifactu;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.watson.server.AonObjectUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.mysql.cj.jdbc.Driver;


public abstract class AbstractVerifactuTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractVerifactuTest.class);

	private static String getDbPort() {		return System.getProperty("dbPort", "3306");	}
	private static String getDbHost() {		return System.getProperty("dbHost", "127.0.0.1");	}
	private static String getDbName() {		return System.getProperty("dbName", "aon_jooq_");	}
	private static String getDbUser() {		return System.getProperty("dbUser", "dbuser");	}
	private static String getDbPasswd() {	return System.getProperty("dbPasswd", "serubd2000");	}
	private static String getDbUseSSL() {	return System.getProperty("dbUseSSL", "false");	}
	private static String getDbTimeZone() {	return System.getProperty("dbTimeZone", TimeZone.getDefault().getID());	}
	
	protected static final Environment VERIFACTU_ENV = new VerifactuEnvironment();
	protected static final Environment VERIFACTU_CANARIAS_ENV = new VerifactuCanariasEnvironment();
	protected static final Environment NO_VERIFACTU_ENV = new NoVerifactuEnvironment();
	protected static final Environment SIF_ENV = new SifEnvironment();
	
	private static final Environment[] ENVIRONMENTS = new Environment[] {
		VERIFACTU_ENV,
		VERIFACTU_CANARIAS_ENV,
		NO_VERIFACTU_ENV,
		SIF_ENV
	}; 
	
	private Date testDate; 
	
	@BeforeAll
	public static void beforeClass() throws ClassNotFoundException, SQLException {
		shutUp();
		for (Environment env : ENVIRONMENTS) {
			synchronized (env) {
				if ( env.getDomainId() == null) {
					AONContext context = new AONContext(connect());
					Domain domain = TestDomainProvider.getOrCreateDomain(context, env );
					env.setDomainId( domain.getId() );
				}
				env.setCtx( AONContext.getAONContext(env.getOccam()) );
			}
		}
		System.setOut(System.out);
		System.setErr(System.err);
	}

	@AfterAll
	public static void afterClass() {
		for (Environment env : ENVIRONMENTS) {
			synchronized (env) {
				env.close();
			}
		}
	}
	
	@BeforeEach
	public void beforeTest(TestInfo testInfo) {
		boolean isRepeatedTest = AonStringUtils.contains(testInfo.getDisplayName(),"repetition");
		String className = testInfo.getTestClass().map(clazz -> clazz.getName()).orElse("?");
		String methodName = testInfo.getTestMethod().map(Method::getName).map(mn -> mn + "()").orElse("?");
		String repetitionInfo = isRepeatedTest?testInfo.getDisplayName():"";
		LOGGER.debug(() -> String.format("Running [ %s.%s %s ]",className,methodName,repetitionInfo));				
	}

	private static void shutUp() {
		if (mustShutUp()) {
			PrintStream devnull = new PrintStream(new OutputStream() {
				@Override
				public void write(int b) throws IOException {
					// Nothing
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
		properties.setProperty("allowPublicKeyRetrieval", "true");
		String url = String.format("jdbc:mysql://%s:%s?allowMultiQueries=true", dbHost, dbPort);
		Connection connection = DriverManager.getConnection(url, properties);

		ResultSet rs = connection.createStatement().executeQuery("SHOW DATABASES");
		while (rs.next()) {
			if (rs.getString(1).startsWith(dbName)) {
				String schemaName = rs.getString(1); 
				connection.createStatement().execute("use `" + schemaName +"`");
				String infoText = "USING  [" + schemaName+ "] schema";
				System.out.println("\033[1;34m");
				System.out.println(AonStringUtils.spaces(10) + "\u250C" + AonStringUtils.repeat('\u2500', 50) + "\u2510");
				System.out.println(AonStringUtils.spaces(10) + "\u2502" + AonStringUtils.center(infoText, 50) + "\u2502");
				System.out.println(AonStringUtils.spaces(10) + "\u2514" + AonStringUtils.repeat('\u2500', 50) + "\u2518");
				System.out.println("\033[0m");
				return connection;
			}
		}
		return connection;
	}
	
	protected Date getTestDate() {
		return AonObjectUtils.defaultIfNull(testDate, new Date());
	}
	
	protected abstract Environment getEnvironment();
	
}
