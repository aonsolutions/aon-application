package es.aonsolutions.aio.test;

public class AonTestsConfig {
	
	private static AonTestsConfig INSTANCE;
	
	private static final String DOMAIN_NAME = "inelco-mac.ecastellano.pro";
	private static final int    DOMAIN = 400;
	private static final String USER = "mac";
	
	private AonTestsConfig() {
		
	};
	
	public static synchronized AonTestsConfig getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new AonTestsConfig();
		}
		return INSTANCE;
	}
	
	public String getDomainName() {
		return DOMAIN_NAME;
	}
	public int getDomain() {
		return DOMAIN;
	}
	public String getUser() {
		return USER;
	}
	
	
}
