package net.aonsolutions.aon.tbai;

public abstract class AbstractTbaiTest {
	
//	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTbaiTest.class);
//
//
//	private static final String CERT_NAME = "bizkaia.p12";
//	private static final String CERT_PASSWORD = "IZDesa2021";
//	private static final String CERT_TYPE = "AEAT";
//	private final static String TEST_NIF_140 = "99980200M";
//	private final static String TEST_NAME_140 = "8FVCxNbMNm"; 
//	private final static String TEST_SURNAME1_140 = "Vux9anjAES"; 
//	private final static String TEST_SURNAME2_140 = "EMPTmw3fmi";
//	
//	protected static CloseableAONContext ctxAraba;
//	protected static Integer DOMAIN_ID;
//	private InvoiceCommunicationConfiguration communicationConfiguration;
//	private InvoiceCommunicationConfiguration communicationConfigurationWithCertificate;
//
//	protected static String DOMAIN_NAME = System.getProperty("domainName", "tbaitest.aonsolutions.test");	
//	protected static String USER 		= System.getProperty("domainUser", "admin");
//
//	private static String getDbPort() {		return System.getProperty("dbPort", "3306");	}
//	private static String getDbHost() {		return System.getProperty("dbHost", "127.0.0.1");	}
//	private static String getDbName() {		return System.getProperty("dbName", "aon_jooq_");	}
//	private static String getDbUser() {		return System.getProperty("dbUser", "dbuser");	}
//	private static String getDbPasswd() {	return System.getProperty("dbPasswd", "serubd2000");	}
//	private static String getDbUseSSL() {	return System.getProperty("dbUseSSL", "false");	}
//	private static String getDbTimeZone() {	return System.getProperty("dbTimeZone", TimeZone.getDefault().getID());	}
//	
//	private Date testDate; 
//	
//	protected static Occam getOccam() {
//		return new Occam()
//			.setDomainName(DOMAIN_NAME)
//			.setDomain(DOMAIN_ID)
//			.setUser(USER);
//	}
//	
//	@BeforeAll
//	public static void beforeClass() throws ClassNotFoundException, SQLException {
//		shutUp();
//		if ( DOMAIN_ID == null) {
//			AONContext context = new AONContext(connect());
//			Domain domain = DomainProviderForTests.getOrCreateDomain(context, DOMAIN_NAME, USER);
//			DOMAIN_ID = domain.getId();
//		}
////		ctx = AONContext.getAONContext(getOccam());
//		System.setOut(System.out);
//		System.setErr(System.err);
//	}
//
//	@AfterAll
//	public static void afterClass() {
////		if (ctx != null) ctx.close();
//	}
//	
//	@BeforeEach
//	public void beforeTest(TestInfo testInfo) {
//		boolean isRepeatedTest = AonStringUtils.contains(testInfo.getDisplayName(),"repetition");
//		String className = testInfo.getTestClass().map(clazz -> clazz.getName()).orElse("?");
//		String methodName = testInfo.getTestMethod().map(Method::getName).map(mn -> mn + "()").orElse("?");
//		String repetitionInfo = isRepeatedTest?testInfo.getDisplayName():"";
//		LOGGER.debug(() -> String.format("Running [ %s.%s %s ]",className,methodName,repetitionInfo));				
//	}
//
//	private static void shutUp() {
//		if (mustShutUp()) {
//			PrintStream devnull = new PrintStream(new OutputStream() {
//				@Override
//				public void write(int b) throws IOException {
//					// Nothing
//				}
//			});
//			System.setOut(devnull);
//			System.setErr(devnull);
//		}
//	}
//	
//	protected static boolean mustShutUp() {
//		String mustShutUp = System.getProperty("mustShutUp", "false");
//		return "true".equalsIgnoreCase(mustShutUp);
//	}
//
//	
//	private static Connection connect() throws ClassNotFoundException, SQLException  {
//		Class.forName(Driver.class.getName());
//
//		String dbHost = getDbHost();
//		String dbPort = getDbPort();
//		String dbName = getDbName();
//		String dbUser = getDbUser();
//		String dbPasswd = getDbPasswd();
//		String dbUseSSL = getDbUseSSL();
//		String dbTimeZone = getDbTimeZone();
//
//		Properties properties = new Properties();
//		properties.setProperty("user", dbUser);
//		properties.setProperty("password", dbPasswd);
//		properties.setProperty("useSSL", dbUseSSL);
//		properties.setProperty("serverTimezone", dbTimeZone);
//		String url = String.format("jdbc:mysql://%s:%s?allowMultiQueries=true", dbHost, dbPort);
//		Connection connection = DriverManager.getConnection(url, properties);
//
//		ResultSet rs = connection.createStatement().executeQuery("SHOW DATABASES");
//		while (rs.next()) {
//			if (rs.getString(1).startsWith(dbName)) {
//				String schemaName = rs.getString(1); 
//				connection.createStatement().execute("use `" + schemaName +"`");
//				String infoText = "USING  [" + schemaName+ "] schema";
//				System.out.println("\033[1;34m");
//				System.out.println(AonStringUtils.spaces(10) + "\u250C" + AonStringUtils.repeat('\u2500', 50) + "\u2510");
//				System.out.println(AonStringUtils.spaces(10) + "\u2502" + AonStringUtils.center(infoText, 50) + "\u2502");
//				System.out.println(AonStringUtils.spaces(10) + "\u2514" + AonStringUtils.repeat('\u2500', 50) + "\u2518");
//				System.out.println("\033[0m");
//				return connection;
//			}
//		}
//		return connection;
//	}
//	
//	protected Date getTestDate() {
//		return AonObjectUtils.defaultIfNull(testDate, new Date());
//	}
//	
//	protected Company company() {
//		return CompanyDAO.getCompany(ctx, DOMAIN_ID);
//	}
//	
//	protected Domain domain() {
//		return DomainDAO.getDomain(ctx, DOMAIN_ID);
//	}
//
//	protected User user() {
//		return UserDAO.get(ctx, DOMAIN_ID, USER)
//			.orElseThrow( () -> new IllegalStateException("User " + USER + " not found in domain " + DOMAIN_ID) );
//	}
//	
//	protected InvoiceCommunicatorContext getInvoiceCommunicatorContext(List<Invoice> invoices) {
//		return new InvoiceCommunicatorContext(domain(),user(), null, invoices)
//			.setCompany( company() )
//			.setConfig( config() )
//		;
//	}
//	protected InvoiceCommunicatorContext getInvoiceCommunicatorContextWithCertificate(List<Invoice> invoices) {
//		return new InvoiceCommunicatorContext(domain(),user(), null, invoices)
//			.setCompany( company() )
//			.setConfig( configWithCertificate() )
//		;
//	}
//
//	protected InvoiceCommunicationConfiguration config() {
//		if (communicationConfiguration == null) {
//			communicationConfiguration = InvoiceCommunicationDAO.get(ctx,ctx.getDomainId()); 
//		}
//		assertNotNull(communicationConfiguration,"communicationConfiguration NULL" );
//		assertTrue(communicationConfiguration.isVerifactu() ,"communicationConfiguration VERIFACTU NO ACTIVO");
//		assertTrue(communicationConfiguration.isVerifactuTest(),"communicationConfiguration NO ENTORNO TEST" );
//		return communicationConfiguration;
//	}
//
//	protected InvoiceCommunicationConfiguration configWithCertificate() {
//		if (communicationConfigurationWithCertificate == null) {
//			communicationConfigurationWithCertificate = InvoiceCommunicationDAO.get(ctx,ctx.getDomainId()); 
//		}
//		assertNotNull(communicationConfigurationWithCertificate,"communicationConfigurationWithCertificate NULL" );
//		assertTrue(communicationConfigurationWithCertificate.isVerifactu() ,"communicationConfigurationWithCertificate VERIFACTU NO ACTIVO");
//		assertTrue(communicationConfigurationWithCertificate.isVerifactuTest(),"communicationConfigurationWithCertificate NO ENTORNO TEST" );
//
//		InputStream is = AbstractTbaiTest.class.getResourceAsStream(CERT_NAME);
//		Certificate c = null;
//		try {
//			c = new Certificate()
//					.setData(AonIOUtils.toByteArray(is))
//					.setConfidential(new Random().nextBoolean())
//					.setDescription(CERT_NAME)
//					.setPassword(CERT_PASSWORD)
//					.setType(CERT_TYPE);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
// 
//		assertNotNull(c, "Certificate NULL");
//		communicationConfigurationWithCertificate.setCertificate(c); 
//		return communicationConfigurationWithCertificate;
//	}
}
