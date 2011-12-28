package com.code.aon.jaas.auth.spi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

import org.jboss.mx.util.MBeanServerLocator;

import com.code.aon.jaas.client.ast.IDataSourceMetaData;
import com.code.aon.jaas.vendor.jboss.JBossMainDeployerMBean;

public class DatabaseLoginModuleEx extends BasicDatabaseLoginModule {

	private String driverClass;
	
	private String userName;
	
	private String password;
	
	private String url;

	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map sharedState, Map options) {
		super.initialize(subject, callbackHandler, sharedState, options);
		Properties properties = null;
		try {
			ObjectName oname = new ObjectName(JBossMainDeployerMBean.OBJECT_NAME);
			Object[] params = { null, null };
			String[] sig = { String.class.getName(), String.class.getName() }; 	
			properties = (Properties) getMBeanServer().invoke(oname, "getConnectionProperties", params, sig);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String tmp = properties.getProperty(IDataSourceMetaData.DRIVER_CLASS);
		if (tmp != null) {
			driverClass = tmp;
		}
		tmp = properties.getProperty(IDataSourceMetaData.USER);
		if (tmp != null) {
			userName = tmp;
		}
		tmp = properties.getProperty(IDataSourceMetaData.PASSWORD);
		if (tmp != null) {
			password = tmp;
		}
		tmp = properties.getProperty(IDataSourceMetaData.URL);
		if (tmp != null) {
			url = tmp;
		}
		if (log.isTraceEnabled()) {
			log.trace("DatabaseLoginModuleEx, driver_class=" + driverClass);
			log.trace("url=" + url);
		}
	}
	
	private MBeanServer getMBeanServer() {
		return MBeanServerLocator.locateJBoss();
	}	

	@Override
	protected Connection getConnection( String username ) throws SQLException, ClassNotFoundException, LoginException {
		
		Class.forName(driverClass);
		return DriverManager.getConnection(url, userName, password);
	}

}
