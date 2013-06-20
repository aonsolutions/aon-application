package com.code.aon.aio.servlet;

import javax.management.ObjectName;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.catalina.mbeans.MBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.jaas.auth.IConstants;
import com.code.aon.jaas.vendor.tomcat.TomcatSessionManager;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.pool.AonDataSource;

public class SecurityLifecycleListener implements ServletContextListener {

	private final static Logger LOGGER = LoggerFactory.getLogger(SecurityLifecycleListener.class);

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		try {
			AonDataSource.getInstance().closePools();
		} catch (AonConnectionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		try {
//			ObjectName oname = new ObjectName( IConstants.MAIN_DEPLOYER_OBJECT_NAME );
//			if ( !MBeanUtils.createServer().isRegistered(oname) ) {
//				TomcatMainDeployer deployer = new TomcatMainDeployer();
//				MBeanUtils.createRegistry().registerComponent( deployer, oname, null );
//			}
			ObjectName oname = new ObjectName( IConstants.SESSION_MANAGER_OBJECT_NAME );
			if ( !MBeanUtils.createServer().isRegistered(oname) ) {
				TomcatSessionManager tss = new TomcatSessionManager();
				MBeanUtils.createRegistry().registerComponent( tss, oname, null );
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

}
