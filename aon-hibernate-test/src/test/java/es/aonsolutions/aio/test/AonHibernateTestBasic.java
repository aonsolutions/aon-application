package es.aonsolutions.aio.test;

import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.domain.IDomainProvider;
import com.code.aon.ui.common.listener.BeanRegisterContextListener;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;

public class AonHibernateTestBasic {
	private static final Object MONITOR = new Object();
	private static boolean FRAMEWORK_LISTENERS_LOADED = false;
	
	
	private static final String DOMAIN_NAME = "occamtest.aonsolutions.test";
	private static final int    DOMAIN = 1; // 400;
	private static final String USER = "admin";
	protected static CloseableAONContext ctx;
	
	public AonHibernateTestBasic() {
		loadListeners();
	}

	
	@BeforeEach
	public void beforeEach( TestInfo testInfo) {
		synchronized (MONITOR) {
			if ( ctx == null) {
				ctx = AONContext.getAONContext(getOccam());
			}
		}
		boolean isRepeatedTest = AonStringUtils.contains(testInfo.getDisplayName(),"repetition");
		String className = testInfo.getTestClass().map(clazz -> clazz.getName()).orElse("?");
		String methodName = testInfo.getTestMethod().map(tm -> tm.getName()).map(mn -> mn + "()").orElse("?");
		String repetitionInfo = isRepeatedTest?testInfo.getDisplayName():""; 
		System.out.println( 
			String.format("Running [ %s.%s %s ]"
				,className
				,methodName
				,repetitionInfo 
		));				
	}
	
	@AfterEach
	public void afterEach(TestInfo info) {
		synchronized (MONITOR) {
			if (ctx != null) ctx.close();
		}
	}
	
	
	public static String getDomainName() {
		return DOMAIN_NAME;
	}
	public static int getDomain() {
		return DOMAIN;
	}
	protected static String getUser() {
		return USER;
	}
	protected static Occam getOccam() {
		return new Occam()
				.setDomainName(DOMAIN_NAME)
				.setDomain(DOMAIN)
				.setUser(USER);
	}

	private void loadListeners() {
		synchronized (MONITOR) {
			if (!FRAMEWORK_LISTENERS_LOADED) {
				final ServletContext servletContext = mock(ServletContext.class);
				ServletContextEvent event = new ServletContextEvent( servletContext );
				
				new BeanRegisterContextListener().contextInitialized( event );

				// ServletContextDomainListener() 
				DomainManager.setDomainProvider( new IDomainProvider() {
					@Override public Integer getCurrentDomain() 			{ return DOMAIN; }
					@Override public Integer getUserDomain() 				{ return null; }
					@Override public boolean isDomainManagementAvailable() 	{ return false; }
					@Override public Integer getParentDomain() 				{ return null; }
					@Override public boolean isEnableHeredity() 			{ return false; }
				});

				FRAMEWORK_LISTENERS_LOADED = true;
			}
		}
	}	
	
}
