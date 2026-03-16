package net.aonsolutions.aon.verifactu;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
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
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.CommunicationData;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationPhaseListener;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.api.model.type.Administration;
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
	protected static final Environment NO_SIF_ENV = new NoSifEnvironment();
	protected static final Environment TBAI_ALAVA_ENV = new TBAIAlavaEnvironment();
	protected static final Environment TBAI_SII_ALAVA_ENV = new TBAISIIAlavaEnvironment();
	protected static final Environment TBAI_GIPUZKOA_ENV = new TBAIGipuzkoaEnvironment();
	protected static final Environment TBAI_SII_GIPUZKOA_ENV = new TBAISIIGipuzkoaEnvironment();
	protected static final Environment LROE_ENV = new LROEEnvironment();
	protected static final Environment ICC_CONFIG_ENV = new ICCConfigurationEnvironment();
	
	private static final Environment[] ENVIRONMENTS = new Environment[] {
		VERIFACTU_ENV,
		VERIFACTU_CANARIAS_ENV,
		NO_VERIFACTU_ENV,
		SIF_ENV,
		TBAI_ALAVA_ENV,
		TBAI_GIPUZKOA_ENV,
		LROE_ENV,
		NO_SIF_ENV,
		TBAI_SII_ALAVA_ENV,
		TBAI_SII_GIPUZKOA_ENV,
		ICC_CONFIG_ENV
	}; 
	
	protected static InvoiceCommunicationPhaseListener PHASE_LISTENER = new InvoiceCommunicationPhaseListener() {
		@Override
		public void beforeAll(AONContext ctx, InvoiceCommunicatorContext icc) throws InvoiceCommunicationException {
			// Nothing
		}
		@Override
		public void beforeInvoice(AONContext ctx, InvoiceCommunicatorContext icc, Invoice invoice) {
			// Nothing
		}
		@Override
		public void afterRightInvoice(AONContext ctx, InvoiceCommunicatorContext icc, Invoice invoice) {
			// Nothing
		}
		
		@Override
		public void afterWrongInvoice(AONContext ctx, InvoiceCommunicatorContext icc, Invoice invoice) throws InvoiceCommunicationException{
			// Nothing
		}
		
		@Override
		public void afterAll(AONContext ctx, InvoiceCommunicatorContext icc) throws InvoiceCommunicationException {
			if (icc.isFailOnWrongValidation() 
			 && icc.invoiceStream().filter( Invoice::hasMessages ).anyMatch( Invoice::hasERRMessages )) {
				// TRACE _-- borrar
				icc.invoiceStream()
					.filter( Invoice::hasMessages )
					.flatMap( Invoice::messageStream )
					.forEach( m -> System.out.println( m.getLevel() + " " + m.getCode() + " - " + m.getMessage() ));
				// ----------------
				throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0024);
			}
		}
	};
	
	protected static final InvoiceCommunicationPhaseListener EMPTY_VERIFACTU_PHASE_LISTENER = new InvoiceCommunicationPhaseListener() {
		
		@Override public void beforeInvoice(AONContext ctx, InvoiceCommunicatorContext icc, Invoice invoice) {/* nothing */}
		@Override public void beforeAll(AONContext ctx, InvoiceCommunicatorContext icc) throws InvoiceCommunicationException {/* nothing */}
		@Override public void afterRightInvoice(AONContext ctx, InvoiceCommunicatorContext icc, Invoice invoice) {/* nothing */}
		@Override public void afterAll(AONContext ctx, InvoiceCommunicatorContext icc) throws InvoiceCommunicationException {/* nothing */}
		
		@Override
		public void afterWrongInvoice(AONContext ctx, InvoiceCommunicatorContext icc, Invoice invoice) throws InvoiceCommunicationException {
			// TRACE _-- borrar
			String s = "MSG Inv: [{0}]: {1} - {2} {3} - {4}";
			invoice.messageStream()
				.forEach( m -> 
					System.out.println( MessageFormat.format( s,
						invoice.getId(),
						invoice.getDocumentNumber(),
						m.getLevel(),
						m.getCode(),
						m.getMessage()
					 ))
				);
			// ----------------
		}
		
	};
	
	private Date testDate; 
	
	@BeforeAll
	public static void beforeClass() throws ClassNotFoundException, SQLException {
		shutUp();
		for (Environment env : ENVIRONMENTS) {
			synchronized (env) {
				if ( env.getDomainId() == null) {
					AONContext context = new AONContext(connect( env));
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

	
	private static Connection connect(Environment env) throws ClassNotFoundException, SQLException  {
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
				System.out.println("\t .... checking [" + schemaName+ "] schema.");
				connection.createStatement().execute("use " + schemaName);
				boolean exists = false;
				String sql = "SELECT 1 FROM domain WHERE name = ? LIMIT 1";
				try (PreparedStatement ps = connection.prepareStatement(sql)) {
				    ps.setString(1, env.getDomainName());
				    try (ResultSet rs0 = ps.executeQuery()) {
				        exists = rs0.next();
				    }
				}
				if (exists) {
					System.out.println("\t .... domain [" + env.getDomainName() + "] found!!");
					System.out.println("USING [" + schemaName+ "] SCHEMA FOR TESTS.");
					return connection;
				}
			}
		}
		return connection;
	}
	
	protected Date getTestDate() {
		return AonObjectUtils.defaultIfNull(testDate, new Date());
	}
	
	protected abstract Environment getEnvironment();
	
	protected AONContext getCtx() {
		return getEnvironment().getCtx();
	}
	protected Integer getDomainId() {
		return getEnvironment().getDomainId();
	}
	protected String getUser() {
		return getEnvironment().getUser();
	}
	
	protected CommunicationData getCD(Administration admon) {
		return new CommunicationData()
			.setDomain( getDomainId() )
			.setAdministration( admon )
		;
	}	
	
}
