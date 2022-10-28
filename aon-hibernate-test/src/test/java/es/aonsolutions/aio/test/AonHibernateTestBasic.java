package es.aonsolutions.aio.test;

import static org.mockito.Mockito.mock;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.domain.IDomainProvider;
import com.code.aon.ui.common.listener.BeanRegisterContextListener;

public class AonHibernateTestBasic {
	private static final Object MONITOR = new Object();
	private static boolean FRAMEWORK_LISTENERS_LOADED = false;
	
	private static final String DOMAIN_NAME = "occamtest.aonsolutions.test";
	// "inelco-mac.ecastellano.pro";  
	private static final int    DOMAIN = 1; // 400;
	private static final String USER = "mac";
	
	public AonHibernateTestBasic() {
		loadListeners();
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
