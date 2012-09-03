package com.code.aon.jaas.vendor.tomcat;

import javax.management.ObjectName;

import org.apache.catalina.mbeans.MBeanUtils;
import org.apache.catalina.mbeans.ServerLifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.jaas.auth.IConstants;

public class SecurityLifecycleListener extends ServerLifecycleListener {

	/** SecurityLifecycleListener Logger instance. */
	private final static Logger LOGGER = LoggerFactory.getLogger(SecurityLifecycleListener.class);

	protected void createMBeans() {
		super.createMBeans();
		try {
			ObjectName oname = new ObjectName( IConstants.MAIN_DEPLOYER_OBJECT_NAME );
			if ( !MBeanUtils.createServer().isRegistered(oname) ) {
				TomcatMainDeployer deployer = new TomcatMainDeployer();
				MBeanUtils.createRegistry().registerComponent( deployer, oname, null );
			}
			oname = new ObjectName( IConstants.SESSION_MANAGER_OBJECT_NAME );
			if ( !MBeanUtils.createServer().isRegistered(oname) ) {
				TomcatSessionManager tss = new TomcatSessionManager();
				MBeanUtils.createRegistry().registerComponent( tss, oname, null );
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

}
