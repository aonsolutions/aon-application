package es.aonsolutions.aio.test;

import org.junit.ClassRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import com.esferalia.aon.watson.util.AonStringUtils;

public class AonHibernateTestBasic {
	
	private static final String DOMAIN_NAME = "inelco-mac.ecastellano.pro";  //"occamtest.aonsolutions.test";
	private static final int    DOMAIN = 400;
	private static final String USER = "mac";
	
	@ClassRule
	public static CustomTestWatcher classWatcher = new CustomTestWatcher();

	public static class CustomTestWatcher extends TestWatcher {
		private int testCount = 0;

		@Override
		protected void starting(Description description) {
			++testCount;
			System.out.print( "\n" );
			System.out.println( testCount + " .- [START]" + AonStringUtils.repeat(AonStringUtils.HYPHEN, 30 ) + description.getClassName() );
		}
		
		@Override
		protected void finished(Description description) {
			System.out.println( testCount + " .- [ END ]" +  AonStringUtils.repeat(AonStringUtils.HYPHEN, 30 ) + description.getClassName() + " [END]" );
		}
		
	}	
	
	
	public static String getDomainName() {
		return DOMAIN_NAME;
	}
	public static int getDomain() {
		return DOMAIN;
	}
	public static String getUser() {
		return USER;
	}
	
	
	
}
