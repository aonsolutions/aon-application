package com.code.aon.jaas.vendor.tomcat;

import java.io.File;

import javax.management.ObjectName;

import org.apache.catalina.mbeans.MBeanUtils;
import org.apache.catalina.mbeans.ServerLifecycleListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SecurityLifecycleListener extends ServerLifecycleListener {

	/** SecurityLifecycleListener Logger instance. */
	private static final Log LOGGER = LogFactory.getLog( SecurityLifecycleListener.class.getName() );
	/** Applications Deployed file relative path */
	static final String DELPOYED_FILE_RELATIVE_PATH = "aon.workspace" + File.separator + "deployed.xml";

	/* (non-Javadoc)
	 * @see org.apache.catalina.mbeans.ServerLifecycleListener#createMBeans()
	 */
	protected void createMBeans() {
		super.createMBeans();
		try {
			ObjectName oname = new ObjectName( TomcatMainDeployer.OBJECT_NAME );
			if ( !MBeanUtils.createServer().isRegistered(oname) ) {
				TomcatMainDeployer deployer = new TomcatMainDeployer();
				MBeanUtils.createRegistry().registerComponent( deployer, oname, null );
				deployer.setConfigResource( DELPOYED_FILE_RELATIVE_PATH );
				deployer.start();
			}
			oname = new ObjectName( StandardSecurity.OBJECT_NAME );
			if ( !MBeanUtils.createServer().isRegistered(oname) ) {
				StandardSecurity ss = new StandardSecurity();
				MBeanUtils.createRegistry().registerComponent( ss, oname, null );
				ss.start();
			}
			oname = new ObjectName( TomcatSessionManager.OBJECT_NAME );
			if ( !MBeanUtils.createServer().isRegistered(oname) ) {
				TomcatSessionManager tss = new TomcatSessionManager();
				MBeanUtils.createRegistry().registerComponent( tss, oname, null );
				tss.start();
			}
		} catch (Exception e) {
			LOGGER.fatal(e.getMessage());
		}
	}

}
