package net.aonsolutions.aon.verifactu;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.aonsolutions.AonSecret;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.UserDAO;
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
	
	protected static Environment VERIFACTU_ENV = new VerifactuEnvironment();
	
	private Date testDate; 
	
	public static interface Environment {
		CloseableAONContext getCtx();
		void setCtx(CloseableAONContext aonContext);
		
		
		Integer getDomainId();
		void setDomainId(Integer id);
		Occam getOccam();
		InvoiceCommunicationConfiguration getCommunicationConfiguration();
		void setCommunicationConfiguration(InvoiceCommunicationConfiguration invoiceCommunicationConfiguration);
		InvoiceCommunicationConfiguration getCommunicationConfigurationWithCertificate();
		void setCommunicationConfigurationWithCertificate(InvoiceCommunicationConfiguration invoiceCommunicationConfiguration);
		String getDomainName();	
		String getUser();
		Domain getDomain();
		
		default void close() {
			CloseableAONContext ctx = getCtx();
			if (ctx != null) {
				ctx.close();
			}
		}
	}
	static abstract class VerifactuEnvironmentAbs implements Environment {
		protected CloseableAONContext ctx;
		protected Integer domainId;
		protected InvoiceCommunicationConfiguration communicationConfiguration;
		protected InvoiceCommunicationConfiguration communicationConfigurationWithCertificate;
		protected Domain domain;

		@Override
		public CloseableAONContext getCtx() {
			return ctx;
		}
		@Override
		public void setCtx(CloseableAONContext aonContext) {
			this.ctx = aonContext;
		}
		
		@Override
		public Integer getDomainId() {
			return domainId;
		}
		@Override
		public void setDomainId(Integer id) {
			this.domainId = id;
		}
		
		@Override
		public InvoiceCommunicationConfiguration getCommunicationConfiguration() {
			return communicationConfiguration;
		}
		@Override
		public void setCommunicationConfiguration(InvoiceCommunicationConfiguration invoiceCommunicationConfiguration) {
			this.communicationConfiguration = invoiceCommunicationConfiguration;
		}
		
		@Override
		public InvoiceCommunicationConfiguration getCommunicationConfigurationWithCertificate() {
			return communicationConfigurationWithCertificate;
		}
		@Override
		public void setCommunicationConfigurationWithCertificate(InvoiceCommunicationConfiguration invoiceCommunicationConfiguration) {
			this.communicationConfigurationWithCertificate = invoiceCommunicationConfiguration;
		}
		
		@Override
		public Domain getDomain() {
			if (domain == null) {
				domain = DomainDAO.getDomain(getCtx(), getDomainId());
			}
			return domain;
		}
		
		@Override
		public Occam getOccam() {
			return new Occam()
				.setDomainName(getDomainName())
				.setDomain(domainId)
				.setUser(getUser());
		}
	}
	
	static final class VerifactuEnvironment extends VerifactuEnvironmentAbs {
		protected String domainName = "verifactutest.aonsolutions.test";	
		protected String user 		= "admin";
		
		@Override
		public String getDomainName() {
			return domainName;
		}
		@Override
		public String getUser() {
			return user;
		}
	}
	
	static final class NoVerifactuEnvironment extends VerifactuEnvironmentAbs {
		protected String domainName = "noverifactutest.aonsolutions.test";	
		protected String user 		= "admin";
		
		@Override
		public String getDomainName() {
			return domainName;
		}
		
		@Override
		public String getUser() {
			return user;
		}
	}

	@BeforeAll
	public static void beforeClass() throws ClassNotFoundException, SQLException {
		shutUp();
		synchronized (VERIFACTU_ENV) {
			if ( VERIFACTU_ENV.getDomainId() == null) {
				AONContext context = new AONContext(connect());
				Domain domain = TestDomainProvider.getOrCreateDomain(context, VERIFACTU_ENV );
				VERIFACTU_ENV.setDomainId( domain.getId() );
			}
			VERIFACTU_ENV.setCtx( AONContext.getAONContext(VERIFACTU_ENV.getOccam()) );
		}
		System.setOut(System.out);
		System.setErr(System.err);
	}

	@AfterAll
	public static void afterClass() {
		synchronized (VERIFACTU_ENV) {
			VERIFACTU_ENV.close();
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
	
	protected Company company() {
		return CompanyDAO.getCompany(getEnvironment().getCtx(), getEnvironment().getDomainId());
	}
	
	protected Domain domain() {
		return DomainDAO.getDomain(getEnvironment().getCtx(), getEnvironment().getDomainId());
	}

	protected User user() {
		return UserDAO.get(getEnvironment().getCtx(), getEnvironment().getDomainId(), getEnvironment().getUser())
			.orElseThrow( () -> new IllegalStateException("User " + getEnvironment().getUser() + " not found in domain " + getEnvironment().getDomainId()) );
	}
	
	protected InvoiceCommunicatorContext getInvoiceCommunicatorContextWithCertificate() {
		return getInvoiceCommunicatorContextWithCertificate(null);		
	}
	
	protected InvoiceCommunicatorContext getInvoiceCommunicatorContext(List<Invoice> invoices) {
		return new InvoiceCommunicatorContext(domain(),user(), null, invoices)
			.setCompany( company() )
			.setConfig( config() )
		;
	}
	protected InvoiceCommunicatorContext getInvoiceCommunicatorContextWithCertificate(List<Invoice> invoices) {
		return new InvoiceCommunicatorContext(domain(),user(), null, invoices)
			.setCompany( company() )
			.setConfig( configWithCertificate() )
		;
	}

	protected InvoiceCommunicationConfiguration config() {
		if (getEnvironment().getCommunicationConfiguration() == null) {
			getEnvironment().setCommunicationConfiguration( InvoiceCommunicationDAO.get(getEnvironment().getCtx(),getEnvironment().getDomainId())); 
		}
		assertNotNull(getEnvironment().getCommunicationConfiguration(),"communicationConfiguration NULL" );
		assertTrue(getEnvironment().getCommunicationConfiguration().isVerifactu() ,"communicationConfiguration VERIFACTU NO ACTIVO");
		assertTrue(getEnvironment().getCommunicationConfiguration().isVerifactuTest(),"communicationConfiguration NO ENTORNO TEST" );
		return getEnvironment().getCommunicationConfiguration();
	}

	protected InvoiceCommunicationConfiguration configWithCertificate() {
		if (getEnvironment().getCommunicationConfigurationWithCertificate() == null) {
			getEnvironment().setCommunicationConfigurationWithCertificate( InvoiceCommunicationDAO.get(getEnvironment().getCtx(),getEnvironment().getDomainId())); 
		}
		assertNotNull(getEnvironment().getCommunicationConfigurationWithCertificate(),"communicationConfigurationWithCertificate NULL" );
		assertTrue(getEnvironment().getCommunicationConfigurationWithCertificate().isVerifactu() ,"communicationConfigurationWithCertificate VERIFACTU NO ACTIVO");
		assertTrue(getEnvironment().getCommunicationConfigurationWithCertificate().isVerifactuTest(),"communicationConfigurationWithCertificate NO ENTORNO TEST" );
		Certificate c = AonSecret.getSigCert();
		assertNotNull(c, "Verifactu Certificate NULL");
		getEnvironment().getCommunicationConfigurationWithCertificate().setCertificate(AonSecret.getSigCert()); 
		return getEnvironment().getCommunicationConfigurationWithCertificate();
	}

	protected abstract Environment getEnvironment();
	
}
